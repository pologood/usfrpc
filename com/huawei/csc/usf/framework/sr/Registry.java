package com.huawei.csc.usf.framework.sr;

public abstract interface Registry
{
  public abstract void init(ServiceRegistryAgent paramServiceRegistryAgent)
    throws Exception;
  
  public abstract void start();
  
  public abstract void stop();
  
  public abstract void uninit();
  
  public abstract boolean isReadyToDestroy();
  
  public abstract void register(String paramString1, byte[] paramArrayOfByte, boolean paramBoolean, String paramString2, String paramString3);
  
  public abstract void register(String paramString, byte[] paramArrayOfByte, boolean paramBoolean);
  
  public abstract void registerEphemeral(String paramString, byte[] paramArrayOfByte, boolean paramBoolean);
  
  public abstract void unregister(String paramString);
  
  public abstract void addConnectionListener(RegistryConnectionListener paramRegistryConnectionListener);
  
  public abstract void removeConnectionListener(RegistryConnectionListener paramRegistryConnectionListener);
  
  public abstract void addBuiltinKey(String paramString);
  
  public abstract void addWatcherService(String paramString);
  
  public abstract void removeBuiltinKey(String paramString);
  
  public abstract void setRootPath(String paramString);
  
  public abstract void getServiceDatafromzk(String paramString);
  
  public abstract String getRegId();
  
  public abstract void setRegId(String paramString);
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\Registry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */