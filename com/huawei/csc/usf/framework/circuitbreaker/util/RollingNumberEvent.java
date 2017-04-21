/*    */ package com.huawei.csc.usf.framework.circuitbreaker.util;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.circuitbreaker.CircuitBreakerEventType;
/*    */ 
/*    */ public enum RollingNumberEvent
/*    */ {
/*  7 */   SUCCESS(1),  TOTALCOUNT(1),  FAILURE(1),  TIMEOUT(1),  SHORT_CIRCUITED(1),  THREAD_POOL_REJECTED(1), 
/*  8 */   SEMAPHORE_REJECTED(1),  FALLBACK_SUCCESS(1),  FALLBACK_FAILURE(1),  FALLBACK(1), 
/*  9 */   FALLBACK_MISSING(1);
/*    */   
/*    */   private final int type;
/*    */   
/*    */   private RollingNumberEvent(int type)
/*    */   {
/* 15 */     this.type = type;
/*    */   }
/*    */   
/*    */   public static RollingNumberEvent from(CircuitBreakerEventType eventType)
/*    */   {
/* 20 */     switch (eventType)
/*    */     {
/*    */ 
/*    */     case FAILURE: 
/* 24 */       return FAILURE;
/*    */     
/*    */     case TOTALCOUNT: 
/* 27 */       return TOTALCOUNT;
/*    */     case FALLBACK_FAILURE: 
/* 29 */       return FALLBACK_FAILURE;
/*    */     case FALLBACK_MISSING: 
/* 31 */       return FALLBACK_MISSING;
/*    */     case FALLBACK: 
/* 33 */       return FALLBACK;
/*    */     case FALLBACK_SUCCESS: 
/* 35 */       return FALLBACK_SUCCESS;
/*    */     
/*    */     case SEMAPHORE_REJECTED: 
/* 38 */       return SEMAPHORE_REJECTED;
/*    */     case SHORT_CIRCUITED: 
/* 40 */       return SHORT_CIRCUITED;
/*    */     case SUCCESS: 
/* 42 */       return SUCCESS;
/*    */     case THREAD_POOL_REJECTED: 
/* 44 */       return THREAD_POOL_REJECTED;
/*    */     case TIMEOUT: 
/* 46 */       return TIMEOUT; }
/*    */     
/* 48 */     throw new RuntimeException("Unknown RollingNumberEvent : " + eventType);
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\circuitbreaker\util\RollingNumberEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */