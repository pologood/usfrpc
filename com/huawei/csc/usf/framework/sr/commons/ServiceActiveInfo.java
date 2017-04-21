/*     */ package com.huawei.csc.usf.framework.sr.commons;
/*     */ 
/*     */ import com.huawei.csc.usf.framework.sr.ProviderConfigurationInstanceInner;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
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
/*     */ public class ServiceActiveInfo
/*     */ {
/*     */   private ServiceState current;
/*     */   private ProviderConfigurationInstanceInner configurationInstanceInner;
/*     */   private ServiceInstanceInner instance;
/*  38 */   private boolean isActive = true;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  43 */   private boolean finish = false;
/*     */   
/*     */ 
/*     */   public ServiceActiveInfo(ServiceInstanceInner instance, ProviderConfigurationInstanceInner configurationInstanceInner)
/*     */   {
/*  48 */     this.instance = instance;
/*  49 */     this.configurationInstanceInner = configurationInstanceInner;
/*     */   }
/*     */   
/*     */   public ServiceState getCurrent()
/*     */   {
/*  54 */     return this.current;
/*     */   }
/*     */   
/*     */   public void setCurrent(ServiceState current)
/*     */   {
/*  59 */     this.current = current;
/*     */   }
/*     */   
/*     */   public boolean isFinish()
/*     */   {
/*  64 */     return this.finish;
/*     */   }
/*     */   
/*     */   public void setFinish(boolean finish)
/*     */   {
/*  69 */     this.finish = finish;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void changeState()
/*     */   {
/*  77 */     if (!this.finish)
/*     */     {
/*  79 */       this.current.handleServiceState(this);
/*     */     }
/*     */   }
/*     */   
/*     */   public ProviderConfigurationInstanceInner getConfigurationInstanceInner()
/*     */   {
/*  85 */     return this.configurationInstanceInner;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setConfigurationInstanceInner(ProviderConfigurationInstanceInner configurationInstanceInner)
/*     */   {
/*  91 */     this.configurationInstanceInner = configurationInstanceInner;
/*     */   }
/*     */   
/*     */   public ServiceInstanceInner getInstance()
/*     */   {
/*  96 */     return this.instance;
/*     */   }
/*     */   
/*     */   public void setInstance(ServiceInstanceInner instance)
/*     */   {
/* 101 */     this.instance = instance;
/*     */   }
/*     */   
/*     */   public boolean isActive()
/*     */   {
/* 106 */     return this.isActive;
/*     */   }
/*     */   
/*     */   public void setActive(boolean isActive)
/*     */   {
/* 111 */     this.isActive = isActive;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\commons\ServiceActiveInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */