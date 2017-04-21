/*     */ package com.huawei.csc.usf.framework.monitor.delay;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AdaptiveAlgorithmUtil
/*     */ {
/*  24 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(AdaptiveAlgorithmUtil.class);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  30 */   public static final Integer INIT_WEIGHT = Integer.valueOf(100);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  35 */   private static Integer MAX_WEIGHT = Integer.valueOf(200);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  40 */   private static int period = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final double LAST_RATIO = 0.8333333333333334D;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final double CURRENT_RATIO = 0.16666666666666666D;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int RECOVERY_TIMES = 100;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int REFRESH_PERIOD = 10;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int gcd(Integer a, Integer b)
/*     */   {
/*  65 */     while (a.intValue() != 0)
/*     */     {
/*  67 */       int temp = a.intValue();
/*  68 */       a = Integer.valueOf(b.intValue() % a.intValue());
/*  69 */       b = Integer.valueOf(temp);
/*     */     }
/*  71 */     return b.intValue();
/*     */   }
/*     */   
/*     */ 
/*     */   public static int nMax(List<Integer> values)
/*     */   {
/*  77 */     return ((Integer)Collections.max(values)).intValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int ngcd(Integer[] nums, int n)
/*     */   {
/*  85 */     if (n == 1)
/*     */     {
/*  87 */       return nums[0].intValue();
/*     */     }
/*  89 */     return gcd(nums[(n - 1)], Integer.valueOf(ngcd(nums, n - 1)));
/*     */   }
/*     */   
/*     */   public static Integer[] getIntegerArray(List<Integer> values)
/*     */   {
/*  94 */     Integer[] arr = new Integer[values.size()];
/*  95 */     for (int i = 0; i < values.size(); i++)
/*     */     {
/*  97 */       arr[i] = ((Integer)values.get(i));
/*     */     }
/*  99 */     return arr;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void adaptiveAlgorithm(Map<String, Integer> serviceInstancePowerValues, List<ServiceInstanceInner> serviceInstances)
/*     */   {
/* 113 */     Double totalLoad = getTotalLoad(serviceInstances);
/*     */     
/* 115 */     Double lavg = Double.valueOf(totalLoad.doubleValue() / serviceInstances.size());
/*     */     
/* 117 */     period += 1;
/* 118 */     for (ServiceInstanceInner instance : serviceInstances)
/*     */     {
/* 120 */       StringBuilder sBuilder = new StringBuilder();
/* 121 */       String key = instance.getGroup() + "#" + instance.getInstanceName() + "#" + instance.getAddress();
/*     */       
/*     */ 
/* 124 */       if (!serviceInstancePowerValues.containsKey(key))
/*     */       {
/* 126 */         serviceInstancePowerValues.put(key, INIT_WEIGHT);
/*     */       }
/* 128 */       Object tmp = instance.getAttr("timeDelay");
/* 129 */       long lastLoad = 0L;
/* 130 */       if (null != tmp)
/*     */       {
/* 132 */         lastLoad = Long.parseLong(String.valueOf(tmp));
/*     */       }
/* 134 */       double ratio = 0.0D;
/*     */       
/* 136 */       if (lastLoad != 0L)
/*     */       {
/* 138 */         ratio = (lastLoad - lavg.doubleValue()) / lastLoad;
/*     */       }
/*     */       
/* 141 */       int lio = MAX_WEIGHT.intValue();
/* 142 */       if (totalLoad.doubleValue() != 0.0D)
/*     */       {
/* 144 */         lio = (int)(MAX_WEIGHT.intValue() * (1.0D - lastLoad / totalLoad.doubleValue()));
/*     */       }
/*     */       
/* 147 */       int weight = (int)(((Integer)serviceInstancePowerValues.get(key)).intValue() - INIT_WEIGHT.intValue() * ratio);
/*     */       
/*     */ 
/* 150 */       if (ratio < 0.0D)
/*     */       {
/* 152 */         weight = Math.min(weight, lio);
/*     */       }
/*     */       else
/*     */       {
/* 156 */         weight = Math.max(weight, lio);
/*     */       }
/*     */       
/*     */ 
/* 160 */       if (period % serviceInstances.size() == 0)
/*     */       {
/* 162 */         if (weight == 0)
/*     */         {
/* 164 */           Map<String, Integer> serviceInstanceNotInvokeMap = ServiceDelayTimeCountCenter.getInstance().getServiceInstanceNotInvokeMap();
/*     */           
/* 166 */           if (!serviceInstanceNotInvokeMap.containsKey(key))
/*     */           {
/* 168 */             serviceInstanceNotInvokeMap.put(key, Integer.valueOf(0));
/*     */           }
/* 170 */           if (((Integer)serviceInstanceNotInvokeMap.get(key)).intValue() >= 10)
/*     */           {
/* 172 */             weight++;
/* 173 */             serviceInstanceNotInvokeMap.put(key, Integer.valueOf(0));
/*     */           }
/*     */         }
/* 176 */         period = 0;
/*     */       }
/* 178 */       serviceInstancePowerValues.put(key, Integer.valueOf(weight));
/* 179 */       LOGGER.debug(MessageFormat.format("refresh power : serviceAddress key is {0} , the new power is {1} and the average delay time is {2}", new Object[] { key, Integer.valueOf(weight), Long.valueOf(lastLoad) }));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Double getTotalLoad(List<ServiceInstanceInner> serviceInstances)
/*     */   {
/* 192 */     double lav = 0.0D;
/*     */     
/* 194 */     for (ServiceInstanceInner info : serviceInstances)
/*     */     {
/* 196 */       Object tmp = info.getAttr("timeDelay");
/* 197 */       if (null != tmp)
/*     */       {
/*     */ 
/*     */ 
/* 201 */         lav += Long.parseLong(String.valueOf(tmp)); }
/*     */     }
/* 203 */     return Double.valueOf(lav);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static int nextAddrIndex(List<Integer> values, AtomicLong idx, AtomicInteger currentCw, int serviceNum)
/*     */   {
/* 210 */     int gcd = ngcd(getIntegerArray(values), values.size());
/*     */     
/*     */ 
/* 213 */     int max = nMax(values);
/*     */     
/* 215 */     int i = -1;
/*     */     do
/*     */     {
/* 218 */       i = (int)Math.abs(idx.incrementAndGet() % serviceNum);
/* 219 */       if (i == 0)
/*     */       {
/* 221 */         currentCw.addAndGet(-gcd);
/* 222 */         if (currentCw.get() <= 0)
/*     */         {
/* 224 */           currentCw.set(max);
/* 225 */           if (currentCw.get() == 0)
/*     */           {
/*     */ 
/* 228 */             return 0;
/*     */           }
/*     */         }
/*     */       }
/* 232 */     } while (((Integer)values.get(i)).intValue() < currentCw.get());
/*     */     
/* 234 */     if (((Integer)values.get(i)).intValue() == 0)
/*     */     {
/* 236 */       return values.indexOf(Integer.valueOf(max));
/*     */     }
/* 238 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setTime(Long lastTime, ServicePortDelayInfo lastAvgTime, List<ServicePortDelayInfo> times)
/*     */   {
/* 246 */     boolean hasValue = false;
/* 247 */     boolean lastIsZero = false;
/* 248 */     Long now = null;
/* 249 */     Long nowAvg = null;
/* 250 */     if (lastTime == null)
/*     */     {
/* 252 */       lastIsZero = true;
/* 253 */       if (LOGGER.isDebugEnable())
/*     */       {
/* 255 */         LOGGER.debug("has last avg serviceDelayTime time is " + lastIsZero);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 260 */     now = getAVG(times);
/* 261 */     if (now != null)
/*     */     {
/* 263 */       hasValue = true;
/* 264 */       if (LOGGER.isDebugEnable())
/*     */       {
/* 266 */         LOGGER.debug("count current avg serviceDelayTime time is " + now);
/*     */       }
/*     */       
/* 269 */       nowAvg = Long.valueOf(((lastTime == null ? 0L : lastTime.longValue()) * 0.8333333333333334D + now.longValue() * 0.16666666666666666D));
/*     */       
/* 271 */       if ((lastTime != null) && (lastTime.longValue() / now.longValue() > 100L))
/*     */       {
/* 273 */         nowAvg = now;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 278 */       nowAvg = Long.valueOf(lastTime == null ? 0L : lastTime.longValue());
/*     */     }
/*     */     
/* 281 */     if (nowAvg != null)
/*     */     {
/* 283 */       if (LOGGER.isDebugEnable())
/*     */       {
/* 285 */         LOGGER.debug("count avg serviceDelayTime by algorithm time is " + nowAvg);
/*     */       }
/*     */       
/* 288 */       if (hasValue)
/*     */       {
/*     */ 
/* 291 */         lastAvgTime.setTcpTime(nowAvg);
/*     */ 
/*     */ 
/*     */       }
/* 295 */       else if (!lastIsZero)
/*     */       {
/*     */ 
/* 298 */         lastAvgTime.setTcpTime(nowAvg);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static Long getAVG(List<ServicePortDelayInfo> times)
/*     */   {
/* 306 */     Long sum = null;
/* 307 */     int size = 0;
/* 308 */     boolean hasValue = false;
/* 309 */     Iterator<ServicePortDelayInfo> it = times.iterator();
/* 310 */     while (it.hasNext())
/*     */     {
/* 312 */       ServicePortDelayInfo time = (ServicePortDelayInfo)it.next();
/*     */       
/* 314 */       if (time.getTcpTime() != null)
/*     */       {
/* 316 */         if (!hasValue)
/*     */         {
/* 318 */           sum = time.getTcpTime();
/*     */         }
/*     */         else
/*     */         {
/* 322 */           sum = Long.valueOf(sum.longValue() + time.getTcpTime().longValue());
/*     */         }
/* 324 */         size++;
/* 325 */         hasValue = true;
/*     */       }
/*     */     }
/* 328 */     if (size == 0)
/*     */     {
/* 330 */       return sum;
/*     */     }
/* 332 */     return Long.valueOf(sum.longValue() / size);
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\monitor\delay\AdaptiveAlgorithmUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */