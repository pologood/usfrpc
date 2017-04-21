/*     */ package com.huawei.csc.usf.framework.sr;
/*     */ 
/*     */ import com.huawei.csc.usf.framework.sr.commons.IpPortServiceState;
/*     */ import com.huawei.csc.usf.framework.sr.commons.ServiceActiveInfo;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ProviderConfigurationInstanceInner
/*     */ {
/*  24 */   private Map<String, ConfigurationInstanceInner> portConfigurations = new TreeMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  29 */   private Map<String, ConfigurationInstanceInner> ipConfigurations = new TreeMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  34 */   private Map<String, ConfigurationInstanceInner> applicationConfigurations = new TreeMap();
/*     */   
/*     */ 
/*     */   private ConfigurationInstanceInner allMatchConfiguration;
/*     */   
/*     */ 
/*     */ 
/*     */   public Map<String, ConfigurationInstanceInner> getPortConfigurations()
/*     */   {
/*  43 */     return this.portConfigurations;
/*     */   }
/*     */   
/*     */ 
/*     */   public void putPortConfiguration(String ip, ConfigurationInstanceInner instance)
/*     */   {
/*  49 */     this.portConfigurations.put(ip, instance);
/*     */   }
/*     */   
/*     */   public void delPortConfiguration(String ip)
/*     */   {
/*  54 */     this.portConfigurations.remove(ip);
/*     */   }
/*     */   
/*     */   public Map<String, ConfigurationInstanceInner> getIpConfigurations()
/*     */   {
/*  59 */     return this.ipConfigurations;
/*     */   }
/*     */   
/*     */ 
/*     */   public void putIpConfiguration(String ip, ConfigurationInstanceInner instance)
/*     */   {
/*  65 */     this.ipConfigurations.put(ip, instance);
/*     */   }
/*     */   
/*     */   public void delIpConfiguration(String ip)
/*     */   {
/*  70 */     this.ipConfigurations.remove(ip);
/*     */   }
/*     */   
/*     */   public Map<String, ConfigurationInstanceInner> getApplicationConfigurations()
/*     */   {
/*  75 */     return this.applicationConfigurations;
/*     */   }
/*     */   
/*     */   public void delApplicationConfiguration(String application)
/*     */   {
/*  80 */     this.applicationConfigurations.remove(application);
/*     */   }
/*     */   
/*     */ 
/*     */   public void putApplicationConfiguration(String application, ConfigurationInstanceInner instance)
/*     */   {
/*  86 */     this.applicationConfigurations.put(application, instance);
/*     */   }
/*     */   
/*     */   public ConfigurationInstanceInner getAllMatchConfiguration()
/*     */   {
/*  91 */     return this.allMatchConfiguration;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setAllMatchConfiguration(ConfigurationInstanceInner allMatchConfiguration)
/*     */   {
/*  97 */     this.allMatchConfiguration = allMatchConfiguration;
/*     */   }
/*     */   
/*     */   public void delAllMatchConfiguration()
/*     */   {
/* 102 */     this.allMatchConfiguration = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isActive(String regId, ServiceInstanceInner instance, DsfZookeeperDataManager dataManger)
/*     */   {
/* 116 */     if (null == instance)
/*     */     {
/* 118 */       return false;
/*     */     }
/* 120 */     ServiceActiveInfo serviceActiveInfo = new ServiceActiveInfo(instance, this);
/*     */     
/* 122 */     serviceActiveInfo.setCurrent(new IpPortServiceState());
/* 123 */     serviceActiveInfo.changeState();
/* 124 */     ProviderConfigurationInstanceInner allOnOffLineConfig = dataManger.getConfigurationInstanceInnerForAll();
/*     */     
/*     */ 
/* 127 */     if (null != allOnOffLineConfig)
/*     */     {
/* 129 */       serviceActiveInfo.setConfigurationInstanceInner(allOnOffLineConfig);
/* 130 */       serviceActiveInfo.setCurrent(new IpPortServiceState());
/* 131 */       serviceActiveInfo.changeState();
/*     */     }
/*     */     
/* 134 */     return serviceActiveInfo.isActive();
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\ProviderConfigurationInstanceInner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */