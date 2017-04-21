/*     */ package com.huawei.csc.usf.framework.circuitbreaker;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.circuitbreaker.util.DSFRollingNumber;
/*     */ import com.huawei.csc.usf.framework.circuitbreaker.util.RollingNumberEvent;
/*     */ import com.huawei.csc.usf.framework.config.DefaultSystemConfig;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ 
/*     */ public class CommandMetrics
/*     */   extends Metrics
/*     */ {
/*  15 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(CommandMetrics.class);
/*     */   
/*     */ 
/*     */   private static final long MS_TO_NS = 1000000L;
/*     */   
/*  20 */   private static DefaultSystemConfig metricsSystemConfig = new DefaultSystemConfig();
/*     */   
/*     */   protected CommandMetrics(DSFRollingNumber counter)
/*     */   {
/*  24 */     super(counter);
/*     */   }
/*     */   
/*  27 */   private static final ConcurrentHashMap<String, CommandMetrics> metrics = new ConcurrentHashMap();
/*     */   
/*  29 */   private volatile HealthCounts healthCountsSnapshot = new HealthCounts(0L, 0L, 0);
/*     */   
/*     */ 
/*  32 */   private volatile AtomicLong lastHealthCountsSnapshot = new AtomicLong(System.nanoTime() / 1000000L);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static CommandMetrics getInstance(String serviceName)
/*     */   {
/*  39 */     CommandMetrics commandMetrics = (CommandMetrics)metrics.get(serviceName);
/*  40 */     if (commandMetrics != null)
/*     */     {
/*  42 */       return commandMetrics;
/*     */     }
/*     */     
/*     */ 
/*  46 */     Integer bulkheadNum = Integer.valueOf(metricsSystemConfig.getBulkheadNumberOfBuckets());
/*  47 */     Integer bulkheadTimeInMilliseconds = Integer.valueOf(metricsSystemConfig.getBulkheadTimeInMilliseconds());
/*     */     
/*  49 */     if (bulkheadTimeInMilliseconds.intValue() % bulkheadNum.intValue() != 0)
/*     */     {
/*  51 */       int bucketSizeInMillseconds = Math.round(bulkheadTimeInMilliseconds.intValue() / bulkheadNum.intValue());
/*  52 */       bulkheadTimeInMilliseconds = Integer.valueOf(bucketSizeInMillseconds * bulkheadNum.intValue());
/*     */     }
/*     */     
/*  55 */     DSFRollingNumber rollingNum = new DSFRollingNumber(bulkheadTimeInMilliseconds.intValue(), bulkheadNum.intValue());
/*     */     
/*  57 */     commandMetrics = new CommandMetrics(rollingNum);
/*  58 */     metrics.putIfAbsent(serviceName, commandMetrics);
/*     */     
/*     */ 
/*     */ 
/*  62 */     return (CommandMetrics)metrics.get(serviceName);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static void reset()
/*     */   {
/*  69 */     metrics.clear();
/*     */   }
/*     */   
/*     */   public void resetCounter()
/*     */   {
/*  74 */     this.counter.reset();
/*  75 */     this.lastHealthCountsSnapshot.set(System.nanoTime() / 1000000L);
/*  76 */     this.healthCountsSnapshot = new HealthCounts(0L, 0L, 0);
/*     */   }
/*     */   
/*     */ 
/*     */   public void markSuccess()
/*     */   {
/*  82 */     this.counter.increment(RollingNumberEvent.SUCCESS);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void markTotalCount()
/*     */   {
/*  89 */     this.counter.increment(RollingNumberEvent.TOTALCOUNT);
/*     */   }
/*     */   
/*     */ 
/*     */   public void markFailure()
/*     */   {
/*  95 */     this.counter.increment(RollingNumberEvent.FAILURE);
/*     */   }
/*     */   
/*     */ 
/*     */   public void markTimeout()
/*     */   {
/* 101 */     this.counter.increment(RollingNumberEvent.TIMEOUT);
/*     */   }
/*     */   
/*     */ 
/*     */   public void markShortCircuited()
/*     */   {
/* 107 */     this.counter.increment(RollingNumberEvent.SHORT_CIRCUITED);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void markThreadPoolRejection()
/*     */   {
/* 114 */     this.counter.increment(RollingNumberEvent.THREAD_POOL_REJECTED);
/*     */   }
/*     */   
/*     */ 
/*     */   public void markSemaphoreRejection()
/*     */   {
/* 120 */     this.counter.increment(RollingNumberEvent.SEMAPHORE_REJECTED);
/*     */   }
/*     */   
/*     */ 
/*     */   public void markFallbackSuccess()
/*     */   {
/* 126 */     this.counter.increment(RollingNumberEvent.FALLBACK_SUCCESS);
/*     */   }
/*     */   
/*     */ 
/*     */   public void markFallbackFailure()
/*     */   {
/* 132 */     this.counter.increment(RollingNumberEvent.FALLBACK_FAILURE);
/*     */   }
/*     */   
/*     */ 
/*     */   public void markFallback()
/*     */   {
/* 138 */     this.counter.increment(RollingNumberEvent.FALLBACK);
/*     */   }
/*     */   
/*     */ 
/*     */   public void markFallbackMissing()
/*     */   {
/* 144 */     this.counter.increment(RollingNumberEvent.FALLBACK_MISSING);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public HealthCounts getHealthCounts(Context context)
/*     */   {
/* 153 */     Integer healthSnapshotInterval = context.getCircuitBreakProperties().getHealthSnapshotInterval();
/* 154 */     long lastTime = this.lastHealthCountsSnapshot.get();
/* 155 */     long currentTime = System.nanoTime() / 1000000L;
/* 156 */     long diffTime = currentTime - lastTime;
/*     */     
/* 158 */     if ((diffTime >= healthSnapshotInterval.intValue()) || (this.healthCountsSnapshot == null))
/*     */     {
/* 160 */       if (this.lastHealthCountsSnapshot.compareAndSet(lastTime, currentTime))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 165 */         long success = this.counter.getRollingSum(RollingNumberEvent.SUCCESS);
/*     */         
/*     */ 
/* 168 */         long failure = this.counter.getRollingSum(RollingNumberEvent.FAILURE);
/*     */         
/*     */ 
/*     */ 
/* 172 */         long timeout = this.counter.getRollingSum(RollingNumberEvent.TIMEOUT);
/*     */         
/*     */ 
/*     */ 
/* 176 */         long threadPoolRejected = this.counter.getRollingSum(RollingNumberEvent.THREAD_POOL_REJECTED);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 181 */         long semaphoreRejected = this.counter.getRollingSum(RollingNumberEvent.SEMAPHORE_REJECTED);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 186 */         long shortCircuited = this.counter.getRollingSum(RollingNumberEvent.SHORT_CIRCUITED);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 193 */         long errorCount = failure + timeout + threadPoolRejected + shortCircuited + semaphoreRejected;
/*     */         
/* 195 */         long totalCount = failure + success + timeout + threadPoolRejected + shortCircuited + semaphoreRejected;
/*     */         
/*     */ 
/*     */ 
/* 199 */         int errorPercentage = 0;
/*     */         
/* 201 */         if (totalCount > 0L)
/*     */         {
/* 203 */           errorPercentage = (int)(errorCount / totalCount * 100.0D);
/*     */         }
/*     */         
/*     */ 
/* 207 */         this.healthCountsSnapshot = new HealthCounts(totalCount, errorCount, errorPercentage);
/*     */       }
/*     */     }
/*     */     
/* 211 */     return this.healthCountsSnapshot;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\circuitbreaker\CommandMetrics.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */