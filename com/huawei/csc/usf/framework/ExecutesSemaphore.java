/*    */ package com.huawei.csc.usf.framework;
/*    */ 
/*    */ import com.huawei.csc.kernel.api.log.LogFactory;
/*    */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ExecutesSemaphore
/*    */ {
/* 20 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(ExecutesSemaphore.class);
/*    */   
/*    */ 
/* 23 */   private AtomicInteger concurrences = new AtomicInteger();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean tryAcquire(int permit)
/*    */   {
/* 37 */     int requestCount = this.concurrences.incrementAndGet();
/* 38 */     if (DEBUGGER.isDebugEnable())
/*    */     {
/* 40 */       StringBuilder msg = new StringBuilder();
/* 41 */       msg.append("Current request count: ");
/* 42 */       msg.append(requestCount);
/* 43 */       msg.append(", request threshold: ");
/* 44 */       msg.append(permit);
/* 45 */       DEBUGGER.debug(msg.toString());
/*    */     }
/*    */     
/* 48 */     if (requestCount <= permit)
/*    */     {
/* 50 */       return true;
/*    */     }
/*    */     
/* 53 */     return false;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void release()
/*    */   {
/* 61 */     this.concurrences.decrementAndGet();
/*    */   }
/*    */   
/*    */   public int getConcurrences()
/*    */   {
/* 66 */     return this.concurrences.get();
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\ExecutesSemaphore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */