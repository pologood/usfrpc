/*    */ package com.huawei.csc.usf.framework.sr;
/*    */ 
/*    */ import java.util.Map;
/*    */ import java.util.TreeMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ConsumerConfigurationInstanceInner
/*    */ {
/* 12 */   private Map<String, ConfigurationInstanceInner> ipConfigurations = new TreeMap();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 17 */   private Map<String, ConfigurationInstanceInner> applicationConfigurations = new TreeMap();
/*    */   
/*    */ 
/*    */   private ConfigurationInstanceInner allMatchConfiguration;
/*    */   
/*    */ 
/*    */ 
/*    */   public Map<String, ConfigurationInstanceInner> getIpConfigurations()
/*    */   {
/* 26 */     return this.ipConfigurations;
/*    */   }
/*    */   
/*    */   public Map<String, ConfigurationInstanceInner> getApplicationConfigurations()
/*    */   {
/* 31 */     return this.applicationConfigurations;
/*    */   }
/*    */   
/*    */   public ConfigurationInstanceInner getAllMatchConfiguration()
/*    */   {
/* 36 */     return this.allMatchConfiguration;
/*    */   }
/*    */   
/*    */ 
/*    */   public void setAllMatchConfiguration(ConfigurationInstanceInner allMatchConfiguration)
/*    */   {
/* 42 */     this.allMatchConfiguration = allMatchConfiguration;
/*    */   }
/*    */   
/*    */ 
/*    */   public void putApplicationConfiguration(String application, ConfigurationInstanceInner instance)
/*    */   {
/* 48 */     this.applicationConfigurations.put(application, instance);
/*    */   }
/*    */   
/*    */ 
/*    */   public void putIpConfiguration(String ip, ConfigurationInstanceInner instance)
/*    */   {
/* 54 */     this.ipConfigurations.put(ip, instance);
/*    */   }
/*    */   
/*    */   public void delApplicationConfiguration(String application)
/*    */   {
/* 59 */     this.applicationConfigurations.remove(application);
/*    */   }
/*    */   
/*    */   public void delAllMatchConfiguration()
/*    */   {
/* 64 */     this.allMatchConfiguration = null;
/*    */   }
/*    */   
/*    */   public void delIpConfiguration(String ip)
/*    */   {
/* 69 */     this.ipConfigurations.remove(ip);
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\ConsumerConfigurationInstanceInner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */