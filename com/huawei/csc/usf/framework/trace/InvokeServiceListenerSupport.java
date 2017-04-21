/*    */ package com.huawei.csc.usf.framework.trace;
/*    */ 
/*    */ import com.huawei.csc.kernel.api.log.LogFactory;
/*    */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*    */ 
/*    */ public class InvokeServiceListenerSupport
/*    */ {
/*  8 */   private static final DebugLog DEBUG_LOGGER = LogFactory.getDebugLog(InvokeServiceListenerSupport.class);
/*    */   
/*    */ 
/*    */   public static void register(InvokeServiceListener listener)
/*    */   {
/* 13 */     if (null != listener)
/*    */     {
/* 15 */       if (DEBUG_LOGGER.isDebugEnable())
/*    */       {
/* 17 */         DEBUG_LOGGER.debug("Add InvokeServiceListener: " + listener);
/*    */       }
/* 19 */       InvokeServiceListenerManager.addListener(listener);
/*    */     }
/*    */   }
/*    */   
/*    */   public static void unregister(InvokeServiceListener listener)
/*    */   {
/* 25 */     if (null != listener)
/*    */     {
/* 27 */       if (DEBUG_LOGGER.isDebugEnable())
/*    */       {
/* 29 */         DEBUG_LOGGER.debug("Remove InvokeServiceListener: " + listener);
/*    */       }
/* 31 */       InvokeServiceListenerManager.removeListener(listener);
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\trace\InvokeServiceListenerSupport.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */