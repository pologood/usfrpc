/*    */ package com.huawei.csc.usf.framework.trace;
/*    */ 
/*    */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*    */ import com.huawei.csc.usf.framework.util.ImplicitArgsType;
/*    */ import com.huawei.csc.usf.framework.util.USFContext;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class TraceLinkUtil
/*    */ {
/* 10 */   private static final DebugLog LOGGER = com.huawei.csc.kernel.api.log.LogFactory.getDebugLog(TraceLinkUtil.class);
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private static final int MAX_SIZE = 3072;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static boolean setExtendInfo(String info)
/*    */   {
/* 24 */     if (null == info)
/*    */     {
/* 26 */       return false;
/*    */     }
/* 28 */     if (info.length() > 3072)
/*    */     {
/* 30 */       info = info.substring(0, 3072);
/* 31 */       if (LOGGER.isDebugEnable())
/*    */       {
/* 33 */         LOGGER.debug("user set extendInfo length:" + info.length() + " reach to max length");
/*    */       }
/*    */     }
/*    */     
/* 37 */     USFContext.getContext().setAttribute("ExtendInfo", info, ImplicitArgsType.LOCAL_MANUAL);
/*    */     
/* 39 */     return USFContext.getContext().getAttributes().containsKey("ExtendInfo");
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static boolean setHasComparedTrace(boolean hasComparedTrace)
/*    */   {
/* 52 */     USFContext.getContext().setAttribute("hasComparedTrace", hasComparedTrace ? "true" : "false", ImplicitArgsType.LOCAL_MANUAL);
/*    */     
/*    */ 
/* 55 */     return USFContext.getContext().getAttributes().containsKey("hasComparedTrace");
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\trace\TraceLinkUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */