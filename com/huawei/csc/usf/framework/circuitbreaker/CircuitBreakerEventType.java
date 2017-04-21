/*    */ package com.huawei.csc.usf.framework.circuitbreaker;
/*    */ 
/*    */ public enum CircuitBreakerEventType
/*    */ {
/*  5 */   EMIT(false), 
/*  6 */   SUCCESS(true), 
/*  7 */   TOTALCOUNT(true), 
/*  8 */   FAILURE(false), 
/*  9 */   TIMEOUT(false), 
/* 10 */   SHORT_CIRCUITED(false), 
/* 11 */   THREAD_POOL_REJECTED(false), 
/* 12 */   SEMAPHORE_REJECTED(false), 
/* 13 */   FALLBACK_EMIT(false), 
/* 14 */   FALLBACK_SUCCESS(true), 
/* 15 */   FALLBACK_FAILURE(true), 
/* 16 */   FALLBACK(true), 
/* 17 */   FALLBACK_MISSING(true), 
/* 18 */   EXCEPTION_THROWN(false), 
/* 19 */   RESPONSE_FROM_CACHE(true), 
/* 20 */   COLLAPSED(false), 
/* 21 */   BAD_REQUEST(true);
/*    */   
/*    */   private final boolean isTerminal;
/*    */   
/*    */   private CircuitBreakerEventType(boolean isTerminal)
/*    */   {
/* 27 */     this.isTerminal = isTerminal;
/*    */   }
/*    */   
/*    */   public boolean isTerminal() {
/* 31 */     return this.isTerminal;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\circuitbreaker\CircuitBreakerEventType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */