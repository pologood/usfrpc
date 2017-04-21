/*    */ package com.huawei.csc.usf.framework.circuitbreaker.util;
/*    */ 
/*    */ import io.netty.util.internal.chmv8.LongAdderV8;
/*    */ 
/*    */ 
/*    */ public class Bucket
/*    */ {
/*    */   final long windowStart;
/*    */   final LongAdderV8[] adderForCounterType;
/*    */   
/*    */   public Bucket(long startTime)
/*    */   {
/* 13 */     this.windowStart = startTime;
/*    */     
/* 15 */     this.adderForCounterType = new LongAdderV8[RollingNumberEvent.values().length];
/* 16 */     for (RollingNumberEvent type : RollingNumberEvent.values())
/*    */     {
/*    */ 
/* 19 */       this.adderForCounterType[type.ordinal()] = new LongAdderV8();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public long get(RollingNumberEvent type)
/*    */   {
/* 28 */     return this.adderForCounterType[type.ordinal()].sum();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public LongAdderV8 getAdder(RollingNumberEvent type)
/*    */   {
/* 35 */     return this.adderForCounterType[type.ordinal()];
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\circuitbreaker\util\Bucket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */