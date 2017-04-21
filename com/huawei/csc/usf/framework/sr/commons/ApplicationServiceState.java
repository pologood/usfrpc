/*    */ package com.huawei.csc.usf.framework.sr.commons;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.sr.ConfigurationInstanceInner;
/*    */ import com.huawei.csc.usf.framework.sr.ProviderConfigurationInstanceInner;
/*    */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
/*    */ import java.util.Map;
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
/*    */ public class ApplicationServiceState
/*    */   extends ServiceState
/*    */ {
/*    */   public void handleServiceState(ServiceActiveInfo serviceActiveInfo)
/*    */   {
/* 26 */     ProviderConfigurationInstanceInner configurationInstanceInner = serviceActiveInfo.getConfigurationInstanceInner();
/*    */     
/* 28 */     Map<String, ConfigurationInstanceInner> applicationConfigurations = configurationInstanceInner.getApplicationConfigurations();
/*    */     
/* 30 */     ServiceInstanceInner instance = serviceActiveInfo.getInstance();
/* 31 */     String application = instance.getApplication();
/* 32 */     if ((!applicationConfigurations.isEmpty()) && (null != application))
/*    */     {
/* 34 */       ConfigurationInstanceInner config = (ConfigurationInstanceInner)applicationConfigurations.get(application);
/*    */       
/* 36 */       doHandle(config, serviceActiveInfo);
/*    */     }
/* 38 */     serviceActiveInfo.setCurrent(new AllNetServiceState());
/* 39 */     serviceActiveInfo.changeState();
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\commons\ApplicationServiceState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */