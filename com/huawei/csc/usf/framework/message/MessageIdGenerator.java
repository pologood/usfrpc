/*    */ package com.huawei.csc.usf.framework.message;
/*    */ 
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
/*    */ public class MessageIdGenerator
/*    */ {
/* 29 */   private static final AtomicInteger IDCOUNT = new AtomicInteger(0);
/*    */   
/*    */   public static int nextId()
/*    */   {
/* 33 */     return IDCOUNT.incrementAndGet();
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\message\MessageIdGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */