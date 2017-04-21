/*    */ package com.huawei.csc.usf.framework.trace;
/*    */ 
/*    */ public class TraceLinkInitializerFactory
/*    */ {
/*  5 */   private static ITraceLinkInitializer traceLinkInitializer = null;
/*    */   
/*    */   public static ITraceLinkInitializer getTraceLinkInitializer()
/*    */   {
/*  9 */     return traceLinkInitializer;
/*    */   }
/*    */   
/*    */   public static void register(ITraceLinkInitializer initializer)
/*    */   {
/* 14 */     if (null == initializer)
/*    */     {
/* 16 */       return;
/*    */     }
/* 18 */     traceLinkInitializer = initializer;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\trace\TraceLinkInitializerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */