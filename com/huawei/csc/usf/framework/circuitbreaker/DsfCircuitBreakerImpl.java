/*     */ package com.huawei.csc.usf.framework.circuitbreaker;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DsfCircuitBreakerImpl
/*     */   implements DsfCircuitBreaker
/*     */ {
/*  16 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(DsfCircuitBreakerImpl.class);
/*     */   
/*     */ 
/*     */ 
/*     */   private final CommandMetrics metrics;
/*     */   
/*     */ 
/*     */ 
/*  24 */   private AtomicBoolean circuitOpen = new AtomicBoolean(false);
/*     */   
/*  26 */   private AtomicBoolean allowSingleFlag = new AtomicBoolean(false);
/*     */   
/*     */ 
/*     */   private static final long MS_TO_NS = 1000000L;
/*     */   
/*  31 */   private AtomicLong circuitOpenedOrLastTestedTime = new AtomicLong();
/*     */   
/*     */   protected DsfCircuitBreakerImpl(CommandMetrics metrics)
/*     */   {
/*  35 */     this.metrics = metrics;
/*     */   }
/*     */   
/*     */ 
/*     */   public void markSuccess()
/*     */   {
/*  41 */     if ((this.circuitOpen.get()) && (this.allowSingleFlag.get()))
/*     */     {
/*  43 */       this.metrics.resetCounter();
/*     */       
/*  45 */       this.circuitOpen.set(false);
/*     */       
/*  47 */       this.allowSingleFlag.set(false);
/*     */       
/*  49 */       if (DEBUGGER.isInfoEnable())
/*     */       {
/*  51 */         DEBUGGER.info("Circuit breaker switch has closed");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean allowRequest(Context context)
/*     */     throws Exception
/*     */   {
/*  59 */     return (!isOpen(context)) || (allowSingleTest(context));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isOpen(Context context)
/*     */   {
/*  66 */     if (this.circuitOpen.get())
/*     */     {
/*  68 */       return true;
/*     */     }
/*     */     
/*     */ 
/*  72 */     HealthCounts health = this.metrics.getHealthCounts(context);
/*     */     
/*     */ 
/*  75 */     if (health.getTotalRequests() < context.getCircuitBreakProperties().getRequestVolumeThreadHold().intValue())
/*     */     {
/*     */ 
/*     */ 
/*  79 */       return false;
/*     */     }
/*     */     
/*  82 */     if (health.getErrorPercentage() < context.getCircuitBreakProperties().getErrThreadHoldPercentage().intValue())
/*     */     {
/*     */ 
/*  85 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  90 */     if (this.circuitOpen.compareAndSet(false, true))
/*     */     {
/*  92 */       this.circuitOpenedOrLastTestedTime.set(System.nanoTime() / 1000000L);
/*  93 */       if (DEBUGGER.isInfoEnable())
/*     */       {
/*  95 */         DEBUGGER.info("Circuit breaker switch has opened");
/*     */       }
/*  97 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 101 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean allowSingleTest(Context context)
/*     */   {
/* 111 */     long timeCircuitOpenedOrWasLastTested = this.circuitOpenedOrLastTestedTime.get();
/*     */     
/*     */ 
/* 114 */     if ((this.circuitOpen.get()) && (System.nanoTime() / 1000000L > timeCircuitOpenedOrWasLastTested + context.getCircuitBreakProperties().getSleepWindow().intValue()))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 119 */       if (this.circuitOpenedOrLastTestedTime.compareAndSet(timeCircuitOpenedOrWasLastTested, System.nanoTime() / 1000000L))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 124 */         if (DEBUGGER.isDebugEnable())
/*     */         {
/* 126 */           DEBUGGER.debug("The circuitBreaker switch is open and sleepWinow is end ,it allow singleTest");
/*     */         }
/*     */         
/* 129 */         this.allowSingleFlag.compareAndSet(false, true);
/* 130 */         return true;
/*     */       }
/*     */     }
/* 133 */     return false;
/*     */   }
/*     */   
/*     */   public AtomicBoolean getCircuitOpen()
/*     */   {
/* 138 */     return this.circuitOpen;
/*     */   }
/*     */   
/*     */   public void reset()
/*     */   {
/* 143 */     this.metrics.resetCounter();
/*     */     
/* 145 */     this.circuitOpen.set(false);
/*     */     
/* 147 */     this.allowSingleFlag.set(false);
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\circuitbreaker\DsfCircuitBreakerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */