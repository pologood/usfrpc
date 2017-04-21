/*     */ package com.huawei.csc.usf.framework.monitor.delay;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.sr.SRAgentFactory;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
/*     */ import com.huawei.csc.usf.framework.util.DSFRoutingUtil;
/*     */ import com.huawei.csc.usf.framework.util.Utils;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
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
/*     */ 
/*     */ public class ServiceDelayTimeCountCenter
/*     */ {
/*  37 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(ServiceDelayTimeCountCenter.class);
/*     */   
/*     */ 
/*  40 */   private Object calLock = new Object();
/*     */   
/*  42 */   private static final Object lock = new Object();
/*     */   
/*     */ 
/*  45 */   private int timeInterval = 10000;
/*     */   
/*  47 */   private static volatile ServiceDelayTimeCountCenter serviceDelayTimeCountCenter = null;
/*     */   
/*     */ 
/*  50 */   private Map<String, Integer> serviceInstancePowerMap = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  55 */   private Map<String, Integer> serviceInstanceNotInvokeMap = new ConcurrentHashMap();
/*     */   
/*     */   private SRAgentFactory srAgentFactory;
/*     */   
/*     */   private ServiceEngine serviceEngine;
/*     */   
/*  61 */   private volatile boolean start = false;
/*     */   
/*  63 */   private final Object LOCK = new Object();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final int DELAYTIME = 1000;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  74 */   private Map<String, ServicePortDelayInfo> lastAvgServiceDelayTimeMap = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*  78 */   private ConcurrentMap<String, List<ServicePortDelayInfo>> serviceDelayTimeMap = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ServiceDelayTimeCountCenter getInstance()
/*     */   {
/*  86 */     if (null == serviceDelayTimeCountCenter)
/*     */     {
/*  88 */       synchronized (lock)
/*     */       {
/*  90 */         if (null == serviceDelayTimeCountCenter)
/*     */         {
/*  92 */           serviceDelayTimeCountCenter = new ServiceDelayTimeCountCenter();
/*     */         }
/*     */       }
/*     */     }
/*  96 */     return serviceDelayTimeCountCenter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void putServiceDelayTime(Context context)
/*     */   {
/* 103 */     if (this.start)
/*     */     {
/* 105 */       MessageHeaders headers = context.getReceivedMessage().getHeaders();
/* 106 */       StringBuilder builder = new StringBuilder();
/* 107 */       builder.append(headers.getGroup()).append("#").append(headers.getServiceName()).append("#").append(headers.getDestAddr());
/*     */       
/*     */ 
/* 110 */       putServiceDelayTime(builder.toString(), Long.valueOf(System.nanoTime() - context.getStartTime()));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void putServiceDelayTime(String key, Long time)
/*     */   {
/* 117 */     if (LOGGER.isDebugEnable())
/*     */     {
/* 119 */       LOGGER.debug(MessageFormat.format("begin put serviceDelayTime time to center!serviceAddress key is {0} and time is {1}", new Object[] { key, time }));
/*     */     }
/*     */     
/*     */ 
/* 123 */     ServicePortDelayInfo info = new ServicePortDelayInfo();
/* 124 */     info.setTcpTime(time);
/*     */     
/* 126 */     synchronized (this.calLock)
/*     */     {
/* 128 */       List<ServicePortDelayInfo> times = (List)this.serviceDelayTimeMap.get(key);
/* 129 */       if (times == null)
/*     */       {
/* 131 */         this.serviceDelayTimeMap.putIfAbsent(key, new ArrayList());
/*     */         
/* 133 */         times = (List)this.serviceDelayTimeMap.get(key);
/*     */       }
/* 135 */       times.add(info);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void init(ServiceEngine serviceEngine, SRAgentFactory srAgentFactory)
/*     */   {
/* 144 */     this.serviceEngine = serviceEngine;
/* 145 */     this.srAgentFactory = srAgentFactory;
/* 146 */     validateTime();
/* 147 */     if ((DSFRoutingUtil.isDelayRouter()) || (this.serviceEngine.getSystemConfig().getDefaultRouter().equals("serviceDelayTimeRouter")))
/*     */     {
/*     */ 
/*     */ 
/* 151 */       startServiceDelayTimer();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void startServiceDelayTimer()
/*     */   {
/* 160 */     if (!this.start)
/*     */     {
/* 162 */       synchronized (this.LOCK)
/*     */       {
/* 164 */         if (!this.start)
/*     */         {
/* 166 */           startDataTimer();
/* 167 */           this.start = true;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void close()
/*     */   {
/* 179 */     this.serviceDelayTimeMap.clear();
/* 180 */     this.lastAvgServiceDelayTimeMap.clear();
/* 181 */     this.start = false;
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
/*     */   private void validateTime()
/*     */   {
/* 194 */     int time = this.serviceEngine.getSystemConfig().getDelayTimeInterval();
/* 195 */     if (time > 0)
/*     */     {
/* 197 */       this.timeInterval = time;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void updatePower(String instanceName, ServicePortDelayInfo delayInfo)
/*     */   {
/* 207 */     String[] tmp = instanceName.split("#");
/* 208 */     String group = tmp[0];
/* 209 */     String serviceName = tmp[1];
/* 210 */     String serviceAddress = tmp[2];
/* 211 */     List<ServiceInstanceInner> instances = this.srAgentFactory.findInstancesOnSRs(serviceName);
/*     */     
/* 213 */     if (Utils.isNotEmpty(instances))
/*     */     {
/* 215 */       for (ServiceInstanceInner ServiceInstanceInner : instances)
/*     */       {
/* 217 */         if (ServiceInstanceInner.getAddress().equals(serviceAddress))
/*     */         {
/*     */ 
/* 220 */           ServiceInstanceInner.setAttr("timeDelay", delayInfo.getTcpTime());
/*     */           
/*     */ 
/*     */ 
/* 224 */           break;
/*     */         }
/*     */       }
/* 227 */       calculatePower(group, serviceName, instances);
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
/*     */   private void calculatePower(String group, String serviceName, List<ServiceInstanceInner> serviceInstances)
/*     */   {
/* 240 */     for (ServiceInstanceInner ServiceInstanceInner : serviceInstances)
/*     */     {
/*     */ 
/* 243 */       StringBuilder sBuilder = new StringBuilder();
/*     */       
/* 245 */       String key = group + "#" + serviceName + "#" + ServiceInstanceInner.getAddress();
/*     */       
/*     */ 
/* 248 */       if (!this.serviceInstancePowerMap.containsKey(key))
/*     */       {
/* 250 */         this.serviceInstancePowerMap.put(key, AdaptiveAlgorithmUtil.INIT_WEIGHT);
/*     */       }
/*     */     }
/*     */     
/* 254 */     AdaptiveAlgorithmUtil.adaptiveAlgorithm(this.serviceInstancePowerMap, serviceInstances);
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, Integer> getServiceInstancePowerMap()
/*     */   {
/* 260 */     return this.serviceInstancePowerMap;
/*     */   }
/*     */   
/*     */   public Map<String, Integer> getServiceInstanceNotInvokeMap()
/*     */   {
/* 265 */     return this.serviceInstanceNotInvokeMap;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void startDataTimer()
/*     */   {
/* 274 */     Timer timer = new Timer(true);
/* 275 */     timer.schedule(new TimerTask()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */         try
/*     */         {
/* 282 */           for (Map.Entry<String, List<ServicePortDelayInfo>> element : ServiceDelayTimeCountCenter.this.serviceDelayTimeMap.entrySet())
/*     */           {
/*     */ 
/* 285 */             String key = (String)element.getKey();
/* 286 */             if (ServiceDelayTimeCountCenter.LOGGER.isDebugEnable())
/*     */             {
/* 288 */               ServiceDelayTimeCountCenter.LOGGER.debug("begin count serviceDelayTime info,service key=" + key);
/*     */             }
/*     */             
/*     */ 
/* 292 */             ServicePortDelayInfo lastAvgTime = (ServicePortDelayInfo)ServiceDelayTimeCountCenter.this.lastAvgServiceDelayTimeMap.get(key);
/*     */             
/* 294 */             if (lastAvgTime == null)
/*     */             {
/* 296 */               lastAvgTime = new ServicePortDelayInfo();
/*     */             }
/* 298 */             Long lastTcpTime = lastAvgTime.getTcpTime();
/* 299 */             synchronized (ServiceDelayTimeCountCenter.this.calLock)
/*     */             {
/* 301 */               List<ServicePortDelayInfo> timeList = (List)element.getValue();
/*     */               
/* 303 */               List<ServicePortDelayInfo> newList = new ArrayList();
/* 304 */               element.setValue(newList);
/* 305 */               AdaptiveAlgorithmUtil.setTime(lastTcpTime, lastAvgTime, Collections.synchronizedList(timeList));
/*     */               
/*     */ 
/*     */ 
/* 309 */               ServiceDelayTimeCountCenter.this.updateNotInvokeInstance(key, timeList);
/*     */             }
/* 311 */             ServiceDelayTimeCountCenter.this.lastAvgServiceDelayTimeMap.put(key, lastAvgTime);
/* 312 */             if (ServiceDelayTimeCountCenter.LOGGER.isDebugEnable())
/*     */             {
/* 314 */               ServiceDelayTimeCountCenter.LOGGER.debug("finish count serviceDelayTime info,service info=" + lastAvgTime.toString());
/*     */             }
/*     */             
/* 317 */             ServiceDelayTimeCountCenter.this.updatePower(key, lastAvgTime);
/*     */           }
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 322 */           ServiceDelayTimeCountCenter.LOGGER.error("count service deleytime info error!"); } } }, 1000L, this.timeInterval);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void updateNotInvokeInstance(String key, List<ServicePortDelayInfo> timeList)
/*     */   {
/* 332 */     if (!this.serviceInstanceNotInvokeMap.containsKey(key))
/*     */     {
/* 334 */       this.serviceInstanceNotInvokeMap.put(key, Integer.valueOf(0));
/*     */     }
/* 336 */     if (timeList.size() == 0)
/*     */     {
/* 338 */       this.serviceInstanceNotInvokeMap.put(key, Integer.valueOf(((Integer)this.serviceInstanceNotInvokeMap.get(key)).intValue() + 1));
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 344 */       this.serviceInstanceNotInvokeMap.put(key, Integer.valueOf(0));
/*     */     }
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\monitor\delay\ServiceDelayTimeCountCenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */