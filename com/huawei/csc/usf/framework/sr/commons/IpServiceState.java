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
/*    */ 
/*    */ 
/*    */ public class IpServiceState
/*    */   extends ServiceState
/*    */ {
/*    */   private static final String ADDRESS_SPLITOR = ":";
/*    */   
/*    */   public void handleServiceState(ServiceActiveInfo serviceActiveInfo)
/*    */   {
/* 31 */     ProviderConfigurationInstanceInner configurationInstanceInner = serviceActiveInfo.getConfigurationInstanceInner();
/*    */     
/* 33 */     Map<String, ConfigurationInstanceInner> ipConfigurations = configurationInstanceInner.getIpConfigurations();
/*    */     
/* 35 */     ServiceInstanceInner instance = serviceActiveInfo.getInstance();
/* 36 */     String address = instance.getAddress();
/* 37 */     String ip = null;
/* 38 */     if ((!ipConfigurations.isEmpty()) && (null != address))
/*    */     {
/* 40 */       String[] addressConpoments = address.split(":");
/* 41 */       if ((null != addressConpoments) && (2 == addressConpoments.length))
/*    */       {
/* 43 */         ip = addressConpoments[0];
/* 44 */         ConfigurationInstanceInner config = null == ip ? null : (ConfigurationInstanceInner)ipConfigurations.get(ip);
/*    */         
/* 46 */         doHandle(config, serviceActiveInfo);
/*    */       }
/*    */     }
/* 49 */     serviceActiveInfo.setCurrent(new ApplicationServiceState());
/* 50 */     serviceActiveInfo.changeState();
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\commons\IpServiceState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */