/*    */ package com.huawei.csc.usf.framework.circuitbreaker;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.circuitbreaker.util.DSFRollingNumber;
/*    */ import com.huawei.csc.usf.framework.circuitbreaker.util.RollingNumberEvent;
/*    */ 
/*    */ public abstract class Metrics
/*    */ {
/*    */   protected final DSFRollingNumber counter;
/*    */   
/*    */   protected Metrics(DSFRollingNumber counter)
/*    */   {
/* 12 */     this.counter = counter;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public long getRollingCount(RollingNumberEvent event)
/*    */   {
/* 21 */     return this.counter.getRollingSum(event);
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\circuitbreaker\Metrics.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */