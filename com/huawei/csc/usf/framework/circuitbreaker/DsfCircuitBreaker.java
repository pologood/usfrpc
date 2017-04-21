/*     */ package com.huawei.csc.usf.framework.circuitbreaker;
/*     */ 
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract interface DsfCircuitBreaker
/*     */ {
/*     */   public abstract boolean allowRequest(Context paramContext)
/*     */     throws Exception;
/*     */   
/*     */   public abstract boolean isOpen(Context paramContext);
/*     */   
/*     */   public abstract void markSuccess();
/*     */   
/*     */   public abstract AtomicBoolean getCircuitOpen();
/*     */   
/*     */   public abstract void reset();
/*     */   
/*     */   public static class Factory
/*     */   {
/*  44 */     private static ConcurrentHashMap<String, DsfCircuitBreaker> circuitBreakersByCommand = new ConcurrentHashMap();
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
/*     */ 
/*     */ 
/*     */     public static DsfCircuitBreaker getInstance(String serviceName, CommandMetrics metrics)
/*     */     {
/*  68 */       DsfCircuitBreaker previouslyCached = (DsfCircuitBreaker)circuitBreakersByCommand.get(serviceName);
/*     */       
/*  70 */       if (previouslyCached != null)
/*     */       {
/*  72 */         return previouslyCached;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  83 */       DsfCircuitBreaker cbForCommand = (DsfCircuitBreaker)circuitBreakersByCommand.putIfAbsent(serviceName, new DsfCircuitBreakerImpl(metrics));
/*     */       
/*     */ 
/*  86 */       if (cbForCommand == null)
/*     */       {
/*     */ 
/*     */ 
/*  90 */         return (DsfCircuitBreaker)circuitBreakersByCommand.get(serviceName);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  97 */       return cbForCommand;
/*     */     }
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
/*     */     public static DsfCircuitBreaker getInstance(String key)
/*     */     {
/* 112 */       return (DsfCircuitBreaker)circuitBreakersByCommand.get(key);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public static void reset()
/*     */     {
/* 121 */       circuitBreakersByCommand.clear();
/*     */     }
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\circuitbreaker\DsfCircuitBreaker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */