/*     */ package com.huawei.csc.usf.framework.statistic;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import org.apache.commons.lang.time.FastDateFormat;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ProcessDelayTracker
/*     */ {
/*  32 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog("slowService");
/*     */   
/*     */ 
/*  35 */   private static final FastDateFormat simpleDateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss_SSS");
/*     */   
/*     */ 
/*  38 */   private static final BigDecimal MS_NANO = new BigDecimal(1000000);
/*     */   
/*     */ 
/*     */ 
/*     */   public static final String TRACKER_RMOTING_CONTEXT_KEY = "usf.tracker";
/*     */   
/*     */ 
/*     */ 
/*     */   public static final long DEFAULT_CLIENT_TRACKER_THRESHOLD = 2000L;
/*     */   
/*     */ 
/*     */ 
/*     */   public static final long DEFAULT_SERVER_TRACKER_THRESHOLD = 1000L;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int PROCESSES_INIT_SIZE = 20;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int PROCESSES_EXTEND_SIZE = 8;
/*     */   
/*     */ 
/*     */ 
/*  62 */   private static AtomicBoolean trackerOpen = new AtomicBoolean(true);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  67 */   private static AtomicLong clientTrackThreshold = new AtomicLong(2000L);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  73 */   private static AtomicLong serverTrackThreshold = new AtomicLong(1000L);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  79 */   private String traceId = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  84 */   private String msgId = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  89 */   private String serviceName = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  94 */   private String operation = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  99 */   private String name = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 104 */   private long beginTime = 0L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 109 */   private long endTime = 0L;
/*     */   
/* 111 */   private boolean isClient = true;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 116 */   private Map<String, Object> appendInfo = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 121 */   private ProcessDelayTracker[] processes = new ProcessDelayTracker[20];
/*     */   
/* 123 */   private String errorMessage = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 128 */   private int index = 0;
/*     */   
/* 130 */   private ProcessDelayTracker currentTracker = null;
/*     */   
/*     */   public ProcessDelayTracker(String name, long beginTime)
/*     */   {
/* 134 */     this.name = name;
/* 135 */     this.beginTime = beginTime;
/* 136 */     this.processes[this.index] = this;
/* 137 */     this.currentTracker = this;
/*     */   }
/*     */   
/*     */ 
/*     */   private ProcessDelayTracker() {}
/*     */   
/*     */ 
/*     */   private ProcessDelayTracker next(String name, long beginTime)
/*     */   {
/* 146 */     ProcessDelayTracker process = new ProcessDelayTracker();
/* 147 */     process.setName(name);
/* 148 */     process.setBeginTime(beginTime);
/* 149 */     next(process);
/* 150 */     return process;
/*     */   }
/*     */   
/*     */   public void next(ProcessDelayTracker process)
/*     */   {
/* 155 */     int orgIndex = process.getIndex();
/*     */     
/* 157 */     int newIndex = this.currentTracker.getIndex() + 1;
/*     */     
/* 159 */     process.setIndex(newIndex);
/*     */     
/*     */ 
/* 162 */     if (newIndex > this.processes.length - 1)
/*     */     {
/* 164 */       ProcessDelayTracker[] oldProcess = this.processes;
/* 165 */       this.processes = new ProcessDelayTracker[this.processes.length + 8];
/*     */       
/* 167 */       System.arraycopy(oldProcess, 0, this.processes, 0, oldProcess.length);
/*     */     }
/* 169 */     this.processes[newIndex] = process;
/*     */     
/* 171 */     this.currentTracker = process;
/*     */     
/*     */ 
/* 174 */     if ((orgIndex == 0) && (this != process))
/*     */     {
/* 176 */       ProcessDelayTracker[] trackers = process.getProcesses();
/* 177 */       if ((null != trackers) && (trackers.length > 0))
/*     */       {
/*     */ 
/* 180 */         for (int i = 1; i < trackers.length; i++)
/*     */         {
/* 182 */           ProcessDelayTracker tracker = trackers[i];
/* 183 */           if ((null != tracker) && (tracker.getIndex() > 0))
/*     */           {
/* 185 */             next(tracker);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void done(long endTime)
/*     */   {
/* 195 */     this.endTime = endTime;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getTraceId()
/*     */   {
/* 206 */     return this.traceId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTraceId(String traceId)
/*     */   {
/* 217 */     this.traceId = traceId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getMsgId()
/*     */   {
/* 227 */     return this.msgId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setMsgId(String msgId)
/*     */   {
/* 238 */     this.msgId = msgId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getBeginTime()
/*     */   {
/* 248 */     return this.beginTime;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setBeginTime(long beginTime)
/*     */   {
/* 259 */     this.beginTime = beginTime;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getEndTime()
/*     */   {
/* 269 */     return this.endTime;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setEndTime(long endTime)
/*     */   {
/* 280 */     this.endTime = endTime;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/* 291 */     this.name = name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getServiceName()
/*     */   {
/* 301 */     return this.serviceName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setServiceName(String serviceName)
/*     */   {
/* 312 */     this.serviceName = serviceName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getOperation()
/*     */   {
/* 322 */     return this.operation;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ProcessDelayTracker[] getProcesses()
/*     */   {
/* 332 */     return this.processes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setOperation(String operation)
/*     */   {
/* 343 */     this.operation = operation;
/*     */   }
/*     */   
/*     */   public String getName()
/*     */   {
/* 348 */     return this.name;
/*     */   }
/*     */   
/*     */   public long getDelayTime()
/*     */   {
/* 353 */     return this.endTime - this.beginTime;
/*     */   }
/*     */   
/*     */   public float getDelayTimeMs()
/*     */   {
/* 358 */     BigDecimal delay = new BigDecimal(this.endTime - this.beginTime);
/* 359 */     return delay.divide(MS_NANO, 4, 6).floatValue();
/*     */   }
/*     */   
/*     */ 
/*     */   public void putAppendInfo(String key, Object value)
/*     */   {
/* 365 */     this.appendInfo.put(key, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isTrackerOpen()
/*     */   {
/* 375 */     return trackerOpen.get();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setTrackerOpen(boolean trackerOpen)
/*     */   {
/* 386 */     trackerOpen.set(trackerOpen);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setClientTrackThreshold(long trackThreshold)
/*     */   {
/* 397 */     clientTrackThreshold.set(trackThreshold);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setServerTrackThreshold(long trackThreshold)
/*     */   {
/* 408 */     serverTrackThreshold.set(trackThreshold);
/*     */   }
/*     */   
/*     */   public static void reset()
/*     */   {
/* 413 */     setTrackerOpen(false);
/* 414 */     setClientTrackThreshold(2000L);
/* 415 */     setServerTrackThreshold(1000L);
/*     */   }
/*     */   
/*     */   public static ProcessDelayTracker createTracker(String name)
/*     */   {
/* 420 */     if (!isTrackerOpen())
/*     */     {
/* 422 */       return null;
/*     */     }
/* 424 */     return new ProcessDelayTracker(name, System.currentTimeMillis());
/*     */   }
/*     */   
/*     */ 
/*     */   public static ProcessDelayTracker next(ProcessDelayTracker tracker, String name)
/*     */   {
/* 430 */     if (!isTrackerOpen())
/*     */     {
/* 432 */       return null;
/*     */     }
/*     */     
/* 435 */     return tracker.next(name, System.currentTimeMillis());
/*     */   }
/*     */   
/*     */   public static void done(ProcessDelayTracker tracker)
/*     */   {
/* 440 */     if (!isTrackerOpen())
/*     */     {
/* 442 */       return;
/*     */     }
/*     */     
/* 445 */     tracker.done(System.currentTimeMillis());
/*     */   }
/*     */   
/*     */   public static boolean log(ProcessDelayTracker tracker)
/*     */   {
/* 450 */     if (!isTrackerOpen())
/*     */     {
/* 452 */       return false;
/*     */     }
/*     */     
/* 455 */     return tracker.log();
/*     */   }
/*     */   
/*     */   public static void setIsClient(ProcessDelayTracker tracker, boolean isClient)
/*     */   {
/* 460 */     if (!isTrackerOpen())
/*     */     {
/* 462 */       return;
/*     */     }
/*     */     
/* 465 */     tracker.setClient(isClient);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getErrorMessage()
/*     */   {
/* 475 */     return this.errorMessage;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setErrorMessage(String errorMessage)
/*     */   {
/* 486 */     this.errorMessage = errorMessage;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getIndex()
/*     */   {
/* 496 */     return this.index;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setIndex(int index)
/*     */   {
/* 507 */     this.index = index;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ProcessDelayTracker getCurrentTracker()
/*     */   {
/* 517 */     return this.currentTracker;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isClient()
/*     */   {
/* 527 */     return this.isClient;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setClient(boolean isClient)
/*     */   {
/* 538 */     this.isClient = isClient;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 544 */     ProcessDelayTracker mainProcess = this.processes[0];
/* 545 */     if (null == mainProcess)
/*     */     {
/* 547 */       return "no process delay tracker information.";
/*     */     }
/*     */     
/* 550 */     ProcessDelayTracker pdt = this.processes[0];
/*     */     
/* 552 */     String messageId = pdt.getMsgId();
/* 553 */     String serviceName = pdt.getServiceName();
/* 554 */     String operationName = pdt.getOperation();
/* 555 */     String beginTime = simpleDateFormat.format(new Date(pdt.getBeginTime()));
/*     */     
/* 557 */     String endTime = simpleDateFormat.format(new Date(pdt.getEndTime()));
/*     */     
/* 559 */     StringBuilder sb = new StringBuilder();
/* 560 */     if (null != this.traceId)
/*     */     {
/* 562 */       sb.append("traceId->");
/* 563 */       sb.append(this.traceId).append(", ");
/*     */     }
/* 565 */     sb.append("messageId->");
/* 566 */     sb.append(messageId);
/* 567 */     sb.append(", serviceName->");
/* 568 */     sb.append(serviceName);
/* 569 */     sb.append(", operationName->");
/* 570 */     sb.append(operationName);
/* 571 */     sb.append(", beginTime->");
/* 572 */     sb.append(beginTime);
/* 573 */     sb.append(", endTime->");
/* 574 */     sb.append(endTime);
/* 575 */     sb.append(", is->");
/* 576 */     if (this.isClient)
/*     */     {
/* 578 */       sb.append("client");
/*     */     }
/*     */     else
/*     */     {
/* 582 */       sb.append("server");
/*     */     }
/*     */     
/* 585 */     if (null != getErrorMessage())
/*     */     {
/* 587 */       sb.append(", errorMessage->");
/* 588 */       sb.append(getErrorMessage());
/*     */     }
/*     */     
/* 591 */     sb.append(", detail ==>{");
/*     */     
/* 593 */     ProcessDelayTracker currentProcess = null;
/* 594 */     for (int i = 0; i < this.processes.length; i++)
/*     */     {
/* 596 */       currentProcess = this.processes[i];
/*     */       
/* 598 */       if (null == currentProcess) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 605 */       if ((currentProcess.getBeginTime() != 0L) && (currentProcess.getEndTime() == 0L))
/*     */       {
/*     */ 
/* 608 */         currentProcess.setEndTime(System.currentTimeMillis());
/*     */       }
/*     */       
/* 611 */       if (i > 0)
/*     */       {
/* 613 */         sb.append(',');
/*     */       }
/* 615 */       Map<String, Object> aInfo = currentProcess.getAppendInfo();
/*     */       
/* 617 */       sb.append(currentProcess.getName());
/* 618 */       sb.append("->");
/* 619 */       sb.append(currentProcess.getDelayTime());
/* 620 */       sb.append("ms ");
/*     */       
/* 622 */       if (!aInfo.isEmpty())
/*     */       {
/* 624 */         sb.append(", ");
/* 625 */         sb.append(currentProcess.getName());
/* 626 */         sb.append(".appendInfo->");
/* 627 */         sb.append(aInfo);
/*     */       }
/*     */     }
/* 630 */     sb.append("}");
/*     */     
/* 632 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   private boolean log()
/*     */   {
/* 638 */     long threshold = isClient() ? clientTrackThreshold.get() : serverTrackThreshold.get();
/*     */     
/*     */ 
/* 641 */     if (getDelayTime() >= threshold)
/*     */     {
/* 643 */       DEBUGGER.info(toString());
/* 644 */       return true;
/*     */     }
/*     */     
/* 647 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<String, Object> getAppendInfo()
/*     */   {
/* 657 */     return this.appendInfo;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\statistic\ProcessDelayTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */