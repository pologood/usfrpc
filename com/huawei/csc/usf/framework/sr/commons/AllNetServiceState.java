/*    */ package com.huawei.csc.usf.framework.sr.commons;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.sr.ConfigurationInstanceInner;
/*    */ import com.huawei.csc.usf.framework.sr.ProviderConfigurationInstanceInner;
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
/*    */ public class AllNetServiceState
/*    */   extends ServiceState
/*    */ {
/*    */   public void handleServiceState(ServiceActiveInfo serviceActiveInfo)
/*    */   {
/* 24 */     ProviderConfigurationInstanceInner configurationInstanceInner = serviceActiveInfo.getConfigurationInstanceInner();
/*    */     
/* 26 */     ConfigurationInstanceInner allMatchConfiguration = configurationInstanceInner.getAllMatchConfiguration();
/*    */     
/* 28 */     doHandle(allMatchConfiguration, serviceActiveInfo);
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\commons\AllNetServiceState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */