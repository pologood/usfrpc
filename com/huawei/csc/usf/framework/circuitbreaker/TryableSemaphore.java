/*    */ package com.huawei.csc.usf.framework.circuitbreaker;
/*    */ 
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TryableSemaphore
/*    */ {
/*    */   private int numberOfPermits;
/*    */   
/*    */   public void setNumberOfPermits(int numberOfPermits)
/*    */   {
/* 14 */     this.numberOfPermits = numberOfPermits;
/*    */   }
/*    */   
/*    */   public int getNumberOfPermits()
/*    */   {
/* 19 */     return this.numberOfPermits;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/* 24 */   private final AtomicInteger count = new AtomicInteger(0);
/*    */   
/*    */   public TryableSemaphore(int numberOfPermits)
/*    */   {
/* 28 */     this.numberOfPermits = numberOfPermits;
/*    */   }
/*    */   
/*    */   public boolean tryAcquire()
/*    */   {
/* 33 */     int currentCount = this.count.incrementAndGet();
/*    */     
/* 35 */     if (currentCount > this.numberOfPermits)
/*    */     {
/* 37 */       this.count.decrementAndGet();
/* 38 */       return false;
/*    */     }
/* 40 */     return true;
/*    */   }
/*    */   
/*    */   public void release()
/*    */   {
/* 45 */     this.count.decrementAndGet();
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\circuitbreaker\TryableSemaphore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */