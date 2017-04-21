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
/*    */ 
/*    */ public class IpPortServiceState
/*    */   extends ServiceState
/*    */ {
/*    */   public void handleServiceState(ServiceActiveInfo serviceActiveInfo)
/*    */   {
/* 27 */     ProviderConfigurationInstanceInner configurationInstanceInner = serviceActiveInfo.getConfigurationInstanceInner();
/*    */     
/* 29 */     Map<String, ConfigurationInstanceInner> portConfigurations = configurationInstanceInner.getPortConfigurations();
/*    */     
/* 31 */     ServiceInstanceInner instance = serviceActiveInfo.getInstance();
/* 32 */     String address = instance.getAddress();
/* 33 */     if ((!portConfigurations.isEmpty()) && (null != address))
/*    */     {
/* 35 */       ConfigurationInstanceInner config = (ConfigurationInstanceInner)portConfigurations.get(address);
/* 36 */       doHandle(config, serviceActiveInfo);
/*    */     }
/* 38 */     serviceActiveInfo.setCurrent(new IpServiceState());
/* 39 */     serviceActiveInfo.changeState();
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\commons\IpPortServiceState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */