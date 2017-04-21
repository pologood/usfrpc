/*     */ package com.huawei.csc.usf.framework.monitor.threadpool;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.event.FrameworkEvent;
/*     */ import com.huawei.csc.usf.framework.event.ServiceFrameworkEvent;
/*     */ import com.huawei.csc.usf.framework.event.ServiceFrameworkEventPublisher;
/*     */ import com.huawei.csc.usf.framework.executor.ExecutePoolManager;
/*     */ import com.huawei.csc.usf.framework.executor.ExecuteThreadPool;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ import org.springframework.context.ApplicationContext;
/*     */ 
/*     */ public class ThreadPoolMonitorCenter
/*     */ {
/*  25 */   private static final DebugLog LOGGER = com.huawei.csc.kernel.api.log.LogFactory.getDebugLog(ThreadPoolMonitorCenter.class);
/*     */   
/*     */ 
/*  28 */   private static ThreadPoolMonitorCenter instance = new ThreadPoolMonitorCenter();
/*     */   
/*  30 */   private boolean isInited = false;
/*     */   
/*  32 */   private int initTimes = 0;
/*     */   
/*     */ 
/*     */   private static final long DEFAULT_INITIAL_DELAY = 1000L;
/*     */   
/*     */ 
/*  38 */   private long monitorInterval = 0L;
/*     */   
/*     */ 
/*     */   private Timer timer;
/*     */   
/*     */ 
/*     */   private TimerTask timerTask;
/*     */   
/*     */ 
/*  47 */   private IThreadPoolMonitorHandler monitorHandler = new DefaultThreadPoolMonitorHandler();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  52 */   private final ReadWriteLock rwLock = new java.util.concurrent.locks.ReentrantReadWriteLock();
/*     */   
/*  54 */   private final String responseThreadPoolName = "responseBean";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ThreadPoolMonitorCenter getInstance()
/*     */   {
/*  63 */     return instance;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void init(SystemConfig config, ApplicationContext app)
/*     */   {
/*  74 */     synchronized (this)
/*     */     {
/*  76 */       this.initTimes += 1;
/*  77 */       if (!this.isInited)
/*     */       {
/*     */ 
/*     */ 
/*  81 */         boolean threadPoolMonitorSwitch = config.getThreadPoolMonitorSwitch();
/*     */         
/*  83 */         if (!threadPoolMonitorSwitch)
/*     */         {
/*  85 */           LOGGER.info("threadpool monitor switch off, will not start threadpool monitor center.");
/*     */           
/*  87 */           return;
/*     */         }
/*     */         
/*  90 */         long threadPoolMonitorInterval = config.getThreadPoolMonitorInterval();
/*     */         
/*     */ 
/*  93 */         IThreadPoolMonitorHandler monitorHandler = null;
/*  94 */         Map<String, IThreadPoolMonitorHandler> handlerBeanMap = app.getBeansOfType(IThreadPoolMonitorHandler.class);
/*     */         
/*     */ 
/*  97 */         if (handlerBeanMap.size() == 0)
/*     */         {
/*  99 */           LOGGER.info("user don't config threadpool monitor handler, will use default.");
/*     */           
/* 101 */           monitorHandler = new DefaultThreadPoolMonitorHandler();
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 106 */           if (handlerBeanMap.size() > 1)
/*     */           {
/* 108 */             LOGGER.info("user config more than 1 threadpool monitor handler, will use one randomly, config num: " + handlerBeanMap.size());
/*     */           }
/*     */           
/*     */ 
/* 112 */           Iterator i$ = handlerBeanMap.values().iterator(); if (i$.hasNext()) { IThreadPoolMonitorHandler handler = (IThreadPoolMonitorHandler)i$.next();
/*     */             
/*     */ 
/* 115 */             monitorHandler = handler;
/*     */           }
/*     */         }
/*     */         
/* 119 */         setMonitorHandler(monitorHandler);
/* 120 */         start(threadPoolMonitorInterval);
/*     */         
/* 122 */         this.isInited = true;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void destroy()
/*     */   {
/* 129 */     synchronized (this)
/*     */     {
/* 131 */       if (this.initTimes > 0)
/*     */       {
/* 133 */         this.initTimes -= 1;
/*     */       }
/* 135 */       if ((this.isInited) && (this.initTimes == 0))
/*     */       {
/* 137 */         stop();
/* 138 */         this.isInited = false;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public List<ThreadPoolMonitorInfo> gethreadPoolMonitorInfos()
/*     */   {
/* 145 */     List<ThreadPoolMonitorInfo> threadPoolMonitorInfoList = null;
/*     */     
/*     */ 
/* 148 */     List<DataUnit> dataUnits = getExecutePoolMonitorData();
/*     */     
/* 150 */     if (!dataUnits.isEmpty())
/*     */     {
/* 152 */       threadPoolMonitorInfoList = new ArrayList();
/*     */       
/* 154 */       for (DataUnit dataUnit : dataUnits)
/*     */       {
/* 156 */         ThreadPoolMonitorInfo threadPoolMonitorInfo = dataUnit.toThreadPoolMonitorInfo();
/*     */         
/* 158 */         threadPoolMonitorInfoList.add(threadPoolMonitorInfo);
/*     */       }
/*     */     }
/*     */     
/* 162 */     return threadPoolMonitorInfoList;
/*     */   }
/*     */   
/*     */ 
/*     */   private class ThreadPoolMonitorTimerTask
/*     */     extends TimerTask
/*     */   {
/*     */     private ThreadPoolMonitorTimerTask() {}
/*     */     
/*     */ 
/*     */     public void run()
/*     */     {
/* 174 */       ThreadPoolMonitorCenter.this.rwLock.readLock().lock();
/*     */       
/*     */       try
/*     */       {
/* 178 */         List<ThreadPoolMonitorCenter.DataUnit> dataUnits = ThreadPoolMonitorCenter.this.getExecutePoolMonitorData();
/*     */         
/* 180 */         if ((ThreadPoolMonitorCenter.this.monitorHandler != null) && (!dataUnits.isEmpty()))
/*     */         {
/* 182 */           List<ThreadPoolMonitorInfo> threadPoolMonitorInfoList = new ArrayList();
/* 183 */           for (ThreadPoolMonitorCenter.DataUnit dataUnit : dataUnits)
/*     */           {
/* 185 */             ThreadPoolMonitorInfo threadPoolMonitorInfo = dataUnit.toThreadPoolMonitorInfo();
/*     */             
/* 187 */             threadPoolMonitorInfoList.add(threadPoolMonitorInfo);
/*     */           }
/* 189 */           ThreadPoolMonitorCenter.this.monitorHandler.invoke(threadPoolMonitorInfoList);
/*     */         }
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 194 */         ThreadPoolMonitorCenter.LOGGER.error("error running threadpool monitor timer task, exception: ", e);
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/*     */ 
/* 200 */         ThreadPoolMonitorCenter.this.rwLock.readLock().unlock();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private class DataUnit
/*     */   {
/*     */     String threadPoolBeanName;
/*     */     
/*     */     int activeNum;
/*     */     
/*     */     int idleNum;
/*     */     
/*     */     int maxNum;
/*     */     
/*     */     int currQueueSize;
/*     */     long msgNum;
/*     */     long waitingMillis;
/*     */     int remainingCapacity;
/*     */     
/*     */     private DataUnit() {}
/*     */     
/*     */     public ThreadPoolMonitorInfo toThreadPoolMonitorInfo()
/*     */     {
/* 225 */       ThreadPoolMonitorInfo threadPoolMonitorInfo = new ThreadPoolMonitorInfo();
/* 226 */       threadPoolMonitorInfo.setThreadPoolBeanName(this.threadPoolBeanName);
/* 227 */       threadPoolMonitorInfo.setActiveNum(this.activeNum);
/* 228 */       threadPoolMonitorInfo.setIdleNum(this.idleNum);
/* 229 */       threadPoolMonitorInfo.setMaxNum(this.maxNum);
/* 230 */       threadPoolMonitorInfo.setCurrQueueSize(this.currQueueSize);
/* 231 */       long avgWaitingMillis = 0L;
/* 232 */       if (this.msgNum != 0L)
/*     */       {
/* 234 */         avgWaitingMillis = this.waitingMillis / this.msgNum;
/*     */       }
/* 236 */       threadPoolMonitorInfo.setAvgWaitingMillis(avgWaitingMillis);
/* 237 */       return threadPoolMonitorInfo;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void start()
/*     */   {
/* 246 */     long monitorIntervalS = this.monitorInterval / 1000L;
/* 247 */     start(monitorIntervalS);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void start(long monitorIntervalS)
/*     */   {
/* 258 */     if (monitorIntervalS <= 0L)
/*     */     {
/* 260 */       LOGGER.error("illegal monitorInterval, threadpool monitor will not start. monitorInterval(ms): " + monitorIntervalS);
/*     */       
/*     */ 
/* 263 */       return;
/*     */     }
/*     */     
/* 266 */     this.rwLock.readLock().lock();
/*     */     try
/*     */     {
/* 269 */       if (this.timer != null)
/*     */       {
/* 271 */         LOGGER.error("threadpool monitor already started, will not start again."); return;
/*     */       }
/*     */       
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/* 278 */       this.rwLock.readLock().unlock();
/*     */     }
/*     */     
/* 281 */     LOGGER.info("start threadpool monitor center.");
/*     */     
/* 283 */     this.rwLock.writeLock().lock();
/*     */     try
/*     */     {
/* 286 */       this.monitorInterval = (monitorIntervalS * 1000L);
/*     */       
/* 288 */       if (this.timerTask == null)
/*     */       {
/* 290 */         this.timerTask = new ThreadPoolMonitorTimerTask(null);
/*     */       }
/*     */       
/* 293 */       if (this.timer == null)
/*     */       {
/* 295 */         this.timer = new Timer("threadpool-monitor-timer", true);
/* 296 */         this.timer.schedule(this.timerTask, 1000L, this.monitorInterval);
/*     */       }
/*     */       
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 302 */       LOGGER.error("error starting threadpool monitor center, exception: " + e);
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/* 307 */       this.rwLock.writeLock().unlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void stop()
/*     */   {
/* 316 */     LOGGER.info("stop threadpool monitor center.");
/*     */     
/* 318 */     this.rwLock.writeLock().lock();
/*     */     try
/*     */     {
/* 321 */       if (this.timer != null)
/*     */       {
/* 323 */         this.timer.cancel();
/* 324 */         this.timer = null;
/*     */       }
/* 326 */       this.timerTask = null;
/* 327 */       this.monitorInterval = 0L;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 331 */       LOGGER.error("error stopping threadpool monitor center, exception: " + e);
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/* 336 */       this.rwLock.writeLock().unlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getMonitorInterval()
/*     */   {
/* 347 */     return this.monitorInterval / 1000L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setMonitorInterval(long monitorIntervalS)
/*     */   {
/* 358 */     this.rwLock.writeLock().lock();
/*     */     try
/*     */     {
/* 361 */       this.monitorInterval = (monitorIntervalS * 1000L);
/*     */     }
/*     */     finally
/*     */     {
/* 365 */       this.rwLock.writeLock().unlock();
/*     */     }
/*     */   }
/*     */   
/*     */   public IThreadPoolMonitorHandler getMonitorHandler()
/*     */   {
/* 371 */     this.rwLock.readLock().lock();
/*     */     try
/*     */     {
/* 374 */       return this.monitorHandler;
/*     */     }
/*     */     finally
/*     */     {
/* 378 */       this.rwLock.readLock().unlock();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setMonitorHandler(IThreadPoolMonitorHandler monitorHandler)
/*     */   {
/* 384 */     this.rwLock.writeLock().lock();
/*     */     try
/*     */     {
/* 387 */       this.monitorHandler = monitorHandler;
/*     */     }
/*     */     finally
/*     */     {
/* 391 */       this.rwLock.writeLock().unlock();
/*     */     }
/*     */   }
/*     */   
/*     */   private List<DataUnit> getExecutePoolMonitorData()
/*     */   {
/* 397 */     List<DataUnit> dataUnits = new ArrayList();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 405 */     ExecuteThreadPool[] ep = ExecutePoolManager.getInstance().getAllExecutePool();
/*     */     
/*     */ 
/* 408 */     if ((null == ep) || (ep.length == 0))
/*     */     {
/* 410 */       return dataUnits;
/*     */     }
/*     */     
/* 413 */     for (ExecuteThreadPool executePool : ep)
/*     */     {
/* 415 */       DataUnit dataUnit = new DataUnit(null);
/* 416 */       dataUnit.threadPoolBeanName = executePool.getBeanName();
/*     */       
/*     */ 
/* 419 */       dataUnit.msgNum = executePool.getAndClearMsgNum();
/* 420 */       dataUnit.waitingMillis = executePool.getAndClearWaitingMillis();
/*     */       
/* 422 */       ThreadPoolExecutor[] threadPools = executePool.getThreadExecutor();
/* 423 */       for (ThreadPoolExecutor threadPool : threadPools)
/*     */       {
/* 425 */         int activeThreadCount = threadPool.getActiveCount();
/* 426 */         dataUnit.activeNum += activeThreadCount;
/* 427 */         dataUnit.maxNum += threadPool.getMaximumPoolSize();
/* 428 */         dataUnit.idleNum += threadPool.getPoolSize() - activeThreadCount;
/*     */         
/* 430 */         dataUnit.currQueueSize += threadPool.getQueue().size();
/* 431 */         dataUnit.remainingCapacity += threadPool.getQueue().remainingCapacity();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 436 */       if (!"responseBean".equals(dataUnit.threadPoolBeanName))
/*     */       {
/*     */ 
/* 439 */         int totalSize = dataUnit.currQueueSize + dataUnit.remainingCapacity;
/*     */         
/* 441 */         if (dataUnit.currQueueSize >= totalSize * 0.8D)
/*     */         {
/* 443 */           ServiceFrameworkEvent event = FrameworkEvent.creatEvent(null, null, null, null, null, null, null, null);
/*     */           
/*     */           try
/*     */           {
/* 447 */             ServiceFrameworkEventPublisher.publish("usf.framework.threadPool.crowed", event);
/*     */ 
/*     */           }
/*     */           catch (Exception e)
/*     */           {
/*     */ 
/* 453 */             if (LOGGER.isErrorEnable())
/*     */             {
/* 455 */               LOGGER.error("Excpetion occured while processing threadpool event ", e);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 463 */       dataUnits.add(dataUnit);
/*     */     }
/*     */     
/* 466 */     return dataUnits;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\monitor\threadpool\ThreadPoolMonitorCenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */