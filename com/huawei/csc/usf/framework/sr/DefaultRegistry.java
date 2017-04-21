/*     */ package com.huawei.csc.usf.framework.sr;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultRegistry
/*     */   implements Registry
/*     */ {
/*     */   private String regId;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void init(ServiceRegistryAgent sr) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void start() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void stop() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void uninit() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isReadyToDestroy()
/*     */   {
/*  35 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void register(String key, byte[] value, boolean isMetaData, String checkSum, String version) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void register(String key, byte[] value, boolean isDelete) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void registerEphemeral(String key, byte[] value, boolean isDelete) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void unregister(String key) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addConnectionListener(RegistryConnectionListener listener) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeConnectionListener(RegistryConnectionListener listener) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addBuiltinKey(String key) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeBuiltinKey(String key) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRootPath(String path) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void getServiceDatafromzk(String serviceName) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addWatcherService(String serviceName) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getRegId()
/*     */   {
/* 105 */     return this.regId;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setRegId(String regId)
/*     */   {
/* 111 */     this.regId = regId;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\DefaultRegistry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */