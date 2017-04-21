/*     */ package com.huawei.csc.usf.framework.executor;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.remoting.common.Config;
/*     */ import com.huawei.csc.remoting.common.ExecutorPool;
/*     */ import com.huawei.csc.remoting.common.buf.ProtoBuf;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import org.apache.commons.lang.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExecuteThreadPool
/*     */   implements ExecutorPool
/*     */ {
/*  23 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(ExecuteThreadPool.class);
/*     */   
/*     */ 
/*     */   public static final int VALID_EXCEPTION = -1;
/*     */   
/*  28 */   protected ServiceEngine serviceEngine = null;
/*     */   
/*  30 */   protected AtomicLong executorIdx = new AtomicLong();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  35 */   protected volatile int corePoolSize = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  40 */   protected volatile int maxPoolSize = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  45 */   protected volatile int keepAliveTime = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  50 */   protected int maxQueueSize = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  55 */   protected int fork = 1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  60 */   public boolean share = true;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  65 */   protected ThreadPoolExecutor[] threadExecutor = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  70 */   protected String beanName = "shared";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  75 */   protected String[] threadPoolNames = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  80 */   protected boolean beanIsEnable = true;
/*     */   
/*     */ 
/*     */ 
/*     */   protected volatile boolean isInitilized;
/*     */   
/*     */ 
/*  87 */   private AtomicLong msgNum = new AtomicLong(0L);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  92 */   private AtomicLong waitingMillis = new AtomicLong(0L);
/*     */   
/*     */ 
/*     */   private class ThreadPoolTask
/*     */     implements Runnable
/*     */   {
/*     */     private Runnable runnable;
/*     */     private long recvTime;
/*     */     private ExecuteThreadPool executePool;
/*     */     
/*     */     public ThreadPoolTask(Runnable runnable, ExecuteThreadPool executePool)
/*     */     {
/* 104 */       this.runnable = runnable;
/* 105 */       this.executePool = executePool;
/* 106 */       this.recvTime = System.currentTimeMillis();
/*     */     }
/*     */     
/*     */ 
/*     */     public void run()
/*     */     {
/* 112 */       long startRunTime = System.currentTimeMillis();
/* 113 */       long queueWait = startRunTime - this.recvTime;
/*     */       
/*     */ 
/* 116 */       if (this.executePool != null)
/*     */       {
/* 118 */         this.executePool.updateQueueMonitorData(queueWait);
/*     */       }
/*     */       
/* 121 */       this.runnable.run();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ExecuteThreadPool(int corePoolSize, int maxPoolSize, int keepAliveTime, int cacheQueueCapacity, int fork, boolean share)
/*     */   {
/* 129 */     this.corePoolSize = corePoolSize;
/* 130 */     this.maxPoolSize = maxPoolSize;
/* 131 */     this.keepAliveTime = keepAliveTime;
/* 132 */     this.maxQueueSize = cacheQueueCapacity;
/* 133 */     this.fork = fork;
/* 134 */     this.share = share;
/*     */   }
/*     */   
/*     */ 
/*     */   public ExecuteThreadPool() {}
/*     */   
/*     */ 
/*     */   public void initialize()
/*     */   {
/* 143 */     initialize("");
/*     */   }
/*     */   
/*     */   public void initialize(String bingingService)
/*     */   {
/* 148 */     if (!this.isInitilized)
/*     */     {
/* 150 */       synchronized (this)
/*     */       {
/* 152 */         if (!this.isInitilized)
/*     */         {
/* 154 */           doInitialize(bingingService);
/* 155 */           this.isInitilized = true;
/*     */         }
/*     */       }
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
/*     */ 
/*     */   protected void doInitialize(String bingingService)
/*     */   {
/* 171 */     if (!StringUtils.equals(bingingService, ""))
/*     */     {
/* 173 */       bingingService = "-" + bingingService;
/*     */     }
/*     */     
/* 176 */     String shareSuffix = this.share ? "[share]" : "";
/*     */     
/* 178 */     if (null == this.threadExecutor)
/*     */     {
/* 180 */       validateCfgParam();
/*     */       
/* 182 */       int size = this.fork;
/* 183 */       this.threadExecutor = new ThreadPoolExecutor[size];
/* 184 */       this.threadPoolNames = new String[size];
/*     */       
/* 186 */       for (int i = 0; i < size; i++)
/*     */       {
/*     */         try
/*     */         {
/* 190 */           this.threadPoolNames[i] = ("usf-service-woker:" + this.beanName + "-" + i + shareSuffix + bingingService);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 195 */           this.threadExecutor[i] = new ThreadPoolExecutor(this.corePoolSize, this.maxPoolSize, this.keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue(this.maxQueueSize), new NamedThreadFactory(this.threadPoolNames[i]));
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 203 */           if (LOGGER.isDebugEnable())
/*     */           {
/* 205 */             StringBuilder sb = new StringBuilder();
/* 206 */             sb.append("Success to initialize the execute pool, beanName:");
/* 207 */             sb.append(this.beanName);
/* 208 */             sb.append(",index:");
/* 209 */             sb.append(size);
/* 210 */             sb.append('_');
/* 211 */             sb.append(i);
/* 212 */             sb.append(shareSuffix);
/* 213 */             sb.append(",Bing service:");
/* 214 */             sb.append(bingingService);
/* 215 */             sb.append(",detailInfo: corePoolSize=");
/* 216 */             sb.append(this.corePoolSize);
/* 217 */             sb.append(", maxPoolSize=");
/* 218 */             sb.append(this.maxPoolSize);
/* 219 */             sb.append(", keepAliveTime=");
/* 220 */             sb.append(this.keepAliveTime);
/* 221 */             sb.append(", cacheQueueCapacity=");
/* 222 */             sb.append(this.maxQueueSize);
/* 223 */             sb.append(", share=");
/* 224 */             sb.append(this.share);
/* 225 */             sb.append('.');
/*     */             
/* 227 */             LOGGER.debug(sb.toString());
/*     */           }
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 232 */           if (LOGGER.isErrorEnable())
/*     */           {
/* 234 */             LOGGER.error("Initialize threadpool catch an exception", e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void validateCfgParam()
/*     */   {
/* 249 */     this.maxPoolSize = doValidate(this.maxPoolSize, 1024, 1, 15, "maxPoolSize");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 255 */     this.corePoolSize = doValidate(this.corePoolSize, this.maxPoolSize, 1, 8, "corePoolSize");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 260 */     this.maxQueueSize = doValidate(this.maxQueueSize, 1000000, 1, 100000, "cacheQueueCapacity");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 265 */     this.keepAliveTime = doValidate(this.keepAliveTime, 120, 30, 30, "keepAliveTime");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 271 */     this.fork = doValidate(this.fork, 50, 1, 1, "fork");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int doValidate(int paramValue, int maxValue, int minValue, int defaultValue, String paramName)
/*     */   {
/* 283 */     int intParam = -1;
/*     */     
/*     */     try
/*     */     {
/* 287 */       intParam = paramValue;
/*     */ 
/*     */     }
/*     */     catch (NumberFormatException e)
/*     */     {
/* 292 */       if (LOGGER.isDebugEnable())
/*     */       {
/* 294 */         LOGGER.debug("The config value " + paramValue + " of " + paramName + " is invalid, so the defalut value " + defaultValue + " will be used.");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 299 */       return defaultValue;
/*     */     }
/*     */     
/*     */ 
/* 303 */     if (intParam > maxValue)
/*     */     {
/* 305 */       if (LOGGER.isWarnEnable())
/*     */       {
/* 307 */         LOGGER.warn("The config value " + paramValue + " of " + paramName + " is greater than max, so the max value " + maxValue + " will be used.");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 312 */       return maxValue;
/*     */     }
/*     */     
/*     */ 
/* 316 */     if (intParam < minValue)
/*     */     {
/* 318 */       if (LOGGER.isWarnEnable())
/*     */       {
/* 320 */         LOGGER.warn("The config value " + paramValue + " of " + paramName + " is less than minValue, so the minValue " + minValue + " will be used.");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 326 */       return minValue;
/*     */     }
/*     */     
/* 329 */     return paramValue;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void shutdownExecutors()
/*     */   {
/* 341 */     for (ThreadPoolExecutor threadPool : this.threadExecutor)
/*     */     {
/*     */       try
/*     */       {
/* 345 */         threadPool.shutdown();
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */ 
/*     */ 
/* 354 */         LOGGER.error("failed to close the ThreadPool.", e);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 361 */     this.isInitilized = false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void shutdownNowExecutors()
/*     */   {
/* 372 */     if (this.isInitilized)
/*     */     {
/* 374 */       synchronized (this)
/*     */       {
/* 376 */         if (this.isInitilized)
/*     */         {
/* 378 */           for (ThreadPoolExecutor threadPool : this.threadExecutor)
/*     */           {
/* 380 */             threadPool.shutdownNow();
/*     */           }
/* 382 */           this.isInitilized = false;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isReadyToDestroy()
/*     */   {
/* 390 */     if (this.isInitilized)
/*     */     {
/* 392 */       synchronized (this)
/*     */       {
/* 394 */         if (this.isInitilized)
/*     */         {
/* 396 */           for (ThreadPoolExecutor threadPool : this.threadExecutor)
/*     */           {
/* 398 */             if (!threadPool.isTerminated())
/*     */             {
/* 400 */               return false;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 406 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getThreadCorePoolSize()
/*     */   {
/* 418 */     return this.threadExecutor[0].getCorePoolSize();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getThreadMaxPoolSize()
/*     */   {
/* 428 */     return this.threadExecutor[0].getMaximumPoolSize();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getThreadKeepAliveTime()
/*     */   {
/* 438 */     return this.threadExecutor[0].getKeepAliveTime(TimeUnit.SECONDS);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int[] getBusyThreadCount()
/*     */   {
/* 448 */     int size = this.threadExecutor.length;
/* 449 */     int[] busyThreadCount = new int[size];
/*     */     
/* 451 */     for (int i = 0; i < size; i++)
/*     */     {
/* 453 */       busyThreadCount[i] = this.threadExecutor[i].getActiveCount();
/*     */     }
/*     */     
/* 456 */     return busyThreadCount;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int[] getWaitingTaskSize()
/*     */   {
/* 466 */     int size = this.threadExecutor.length;
/* 467 */     int[] waitingTaskSize = new int[size];
/*     */     
/* 469 */     for (int i = 0; i < size; i++)
/*     */     {
/* 471 */       waitingTaskSize[i] = this.threadExecutor[i].getQueue().size();
/*     */     }
/*     */     
/* 474 */     return waitingTaskSize;
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
/*     */   public int getCorePoolSize()
/*     */   {
/* 487 */     return this.corePoolSize;
/*     */   }
/*     */   
/*     */   public void setCorePoolSize(int corePoolSize)
/*     */   {
/* 492 */     this.corePoolSize = corePoolSize;
/*     */   }
/*     */   
/*     */   public int getMaxPoolSize()
/*     */   {
/* 497 */     return this.maxPoolSize;
/*     */   }
/*     */   
/*     */   public void setMaxPoolSize(int maxPoolSize)
/*     */   {
/* 502 */     this.maxPoolSize = maxPoolSize;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getKeepAliveTime()
/*     */   {
/* 512 */     return this.keepAliveTime;
/*     */   }
/*     */   
/*     */   public void setKeepAliveTime(int keepAliveTime)
/*     */   {
/* 517 */     this.keepAliveTime = keepAliveTime;
/*     */   }
/*     */   
/*     */   public ThreadPoolExecutor[] getThreadExecutor()
/*     */   {
/* 522 */     if (null == this.threadExecutor)
/*     */     {
/* 524 */       if (LOGGER.isWarnEnable())
/*     */       {
/* 526 */         LOGGER.warn(this.beanName + "'s threadExecutor is null");
/*     */       }
/*     */     }
/*     */     
/* 530 */     return this.threadExecutor;
/*     */   }
/*     */   
/*     */   public ThreadPoolExecutor getOneThreadExecutor()
/*     */   {
/* 535 */     return this.threadExecutor[0];
/*     */   }
/*     */   
/*     */   public int getMaxQueueSize()
/*     */   {
/* 540 */     return this.maxQueueSize;
/*     */   }
/*     */   
/*     */   public void setMaxQueueSize(int maxQueueSize)
/*     */   {
/* 545 */     this.maxQueueSize = maxQueueSize;
/*     */   }
/*     */   
/*     */   public boolean getShare()
/*     */   {
/* 550 */     return this.share;
/*     */   }
/*     */   
/*     */   public void setShare(boolean share)
/*     */   {
/* 555 */     this.share = share;
/*     */   }
/*     */   
/*     */   public int getFork()
/*     */   {
/* 560 */     return this.fork;
/*     */   }
/*     */   
/*     */   public void setFork(int fork)
/*     */   {
/* 565 */     this.fork = fork;
/*     */   }
/*     */   
/*     */   public String getBeanName()
/*     */   {
/* 570 */     return this.beanName;
/*     */   }
/*     */   
/*     */   public void setBeanName(String beanName)
/*     */   {
/* 575 */     this.beanName = beanName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String[] getThreadPoolNames()
/*     */   {
/* 585 */     if (null == this.threadPoolNames)
/*     */     {
/* 587 */       if (LOGGER.isWarnEnable())
/*     */       {
/* 589 */         LOGGER.warn(this.beanName + "'s thread pool name is null");
/*     */       }
/*     */     }
/*     */     
/* 593 */     return this.threadPoolNames;
/*     */   }
/*     */   
/*     */   public void setThreadPoolNames(String[] threadPoolNames)
/*     */   {
/* 598 */     this.threadPoolNames = threadPoolNames;
/*     */   }
/*     */   
/*     */   public boolean getBeanIsEnable()
/*     */   {
/* 603 */     return this.beanIsEnable;
/*     */   }
/*     */   
/*     */   public void setBeanIsEnable(boolean beanIsEnable)
/*     */   {
/* 608 */     this.beanIsEnable = beanIsEnable;
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
/*     */   public void initilize(Config config)
/*     */   {
/* 621 */     initialize();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void execute(Runnable task)
/*     */   {
/* 632 */     ThreadPoolTask threadPoolTask = new ThreadPoolTask(task, this);
/*     */     
/* 634 */     long beforetime = System.currentTimeMillis();
/* 635 */     int size = this.fork;
/* 636 */     int index = (int)(this.executorIdx.getAndIncrement() % size);
/*     */     
/*     */     try
/*     */     {
/* 640 */       this.threadExecutor[index].execute(threadPoolTask);
/*     */     }
/*     */     catch (RejectedExecutionException rejectedEx)
/*     */     {
/* 644 */       throw rejectedEx;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 648 */       LOGGER.error("excuteReq id or name fail", e);
/*     */     }
/*     */     
/* 651 */     long time = System.currentTimeMillis() - beforetime;
/* 652 */     if (time > 1000L)
/*     */     {
/*     */ 
/*     */ 
/* 656 */       if (LOGGER.isErrorEnable())
/*     */       {
/* 658 */         LOGGER.error("put queue time:" + time + " ms, " + this.threadPoolNames[index] + " waiting to excute task " + ",work queue:" + this.threadExecutor[index].getQueue().size() + "/" + this.maxQueueSize + ",active count:" + this.threadExecutor[index].getActiveCount() + ",current thread count:" + this.threadExecutor[index].getPoolSize() + ",max pool size:" + this.threadExecutor[index].getMaximumPoolSize() + ",core pool size:" + this.threadExecutor[index].getCorePoolSize());
/*     */       }
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
/*     */   public void shutdown()
/*     */   {
/* 683 */     shutdownExecutors();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void shutdownNow()
/*     */   {
/* 694 */     shutdownNowExecutors();
/*     */   }
/*     */   
/*     */ 
/*     */   public void execute(Runnable task, ProtoBuf byteBuffer)
/*     */   {
/* 700 */     if (LOGGER.isDebugEnable())
/*     */     {
/* 702 */       LOGGER.debug("Discarding buffer:" + byteBuffer + ",and executing task.");
/*     */     }
/*     */     
/* 705 */     execute(task);
/*     */   }
/*     */   
/*     */ 
/*     */   public void updateQueueMonitorData(long queueWaitMillis)
/*     */   {
/* 711 */     this.msgNum.incrementAndGet();
/* 712 */     this.waitingMillis.addAndGet(queueWaitMillis);
/*     */   }
/*     */   
/*     */   public long getAndClearMsgNum()
/*     */   {
/* 717 */     return this.msgNum.getAndSet(0L);
/*     */   }
/*     */   
/*     */   public long getAndClearWaitingMillis()
/*     */   {
/* 722 */     return this.waitingMillis.getAndSet(0L);
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\executor\ExecuteThreadPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */