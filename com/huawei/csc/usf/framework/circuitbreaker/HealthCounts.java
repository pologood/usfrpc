/*    */ package com.huawei.csc.usf.framework.circuitbreaker;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HealthCounts
/*    */ {
/*    */   private final long totalCount;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   private final long errorCount;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   private final int errorPercentage;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   HealthCounts(long total, long error, int errorPercentage)
/*    */   {
/* 27 */     this.totalCount = total;
/* 28 */     this.errorCount = error;
/* 29 */     this.errorPercentage = errorPercentage;
/*    */   }
/*    */   
/*    */   public long getTotalRequests()
/*    */   {
/* 34 */     return this.totalCount;
/*    */   }
/*    */   
/*    */   public long getErrorCount()
/*    */   {
/* 39 */     return this.errorCount;
/*    */   }
/*    */   
/*    */   public int getErrorPercentage()
/*    */   {
/* 44 */     return this.errorPercentage;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\circuitbreaker\HealthCounts.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */