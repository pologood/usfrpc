/*    */ package com.huawei.csc.usf.framework.sr.commons;
/*    */ 
/*    */ import com.huawei.csc.kernel.api.log.LogFactory;
/*    */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*    */ import com.huawei.csc.usf.framework.sr.ConfigurationInstanceInner;
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
/*    */ public abstract class ServiceState
/*    */ {
/* 21 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(ServiceState.class);
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final String ACTIVE = "active";
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   abstract void handleServiceState(ServiceActiveInfo paramServiceActiveInfo);
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected void doHandle(ConfigurationInstanceInner config, ServiceActiveInfo serviceActiveInfo)
/*    */   {
/* 38 */     if (null == config)
/*    */     {
/* 40 */       return;
/*    */     }
/* 42 */     String activeStr = config.getAttribute("active");
/* 43 */     if (null != activeStr)
/*    */     {
/*    */       try
/*    */       {
/* 47 */         boolean isActive = Boolean.parseBoolean(activeStr);
/* 48 */         serviceActiveInfo.setActive(isActive);
/* 49 */         serviceActiveInfo.setFinish(true);
/*    */       }
/*    */       catch (Exception e)
/*    */       {
/* 53 */         LOGGER.error("The format of active [" + activeStr + "] is wrong,this should be true or false");
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\commons\ServiceState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */