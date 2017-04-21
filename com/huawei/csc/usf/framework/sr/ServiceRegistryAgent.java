package com.huawei.csc.usf.framework.sr;

import java.util.List;

public abstract interface ServiceRegistryAgent
{
  public abstract void init()
    throws Exception;
  
  public abstract void start();
  
  public abstract void stop();
  
  public abstract void uninit();
  
  public abstract String getRegId();
  
  public abstract boolean isReadyToDestroy();
  
  public abstract List<ServiceInstanceInner> findInstances(String paramString);
  
  public abstract ServiceInner findService(String paramString);
  
  public abstract List<ServiceInner> findAllServices();
  
  public abstract void registerServices(List<ServiceInner> paramList, boolean paramBoolean);
  
  public abstract void registerConsumers(List<ServiceInner> paramList);
  
  public abstract void unregisterServices(List<ServiceInner> paramList);
  
  public abstract void addServiceUpdateListener(ServiceUpdateListener paramServiceUpdateListener);
  
  public abstract void removeServiceUpdateListener(ServiceUpdateListener paramServiceUpdateListener);
  
  public abstract void setListenAddress(ServiceType paramServiceType, String paramString);
  
  public abstract String getListenAddress(ServiceType paramServiceType);
  
  public abstract ZkRegistryAdapter getRegistryAdapter();
  
  public abstract void setRegistryAdapter(ZkRegistryAdapter paramZkRegistryAdapter);
  
  public abstract Registry getRegistry();
  
  public abstract void setRegistry(Registry paramRegistry);
  
  public abstract void unregisterAllLocalInstance();
  
  public abstract void delInstances(List<ServiceInstanceInner> paramList);
  
  public abstract void setServiceInstaceList(List<String> paramList, boolean paramBoolean);
  
  public abstract void setServiceandSdl(String paramString, byte[] paramArrayOfByte);
  
  public abstract void delService(String paramString);
  
  public abstract void delInstances();
  
  public abstract void delAllService();
  
  public abstract void delServiceinstance(List<String> paramList);
  
  public abstract void delSdl(String paramString);
  
  public abstract void addWatchOnService(String paramString, boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract Boolean findWatcerIsOnService(String paramString);
  
  public abstract void findServiceDataFromZk(String paramString);
  
  public abstract void registerDelayedServices();
  
  public abstract void addServiceWatcher(List<ServiceInner> paramList);
  
  public abstract void setZookeeperDataManager(DsfZookeeperDataManager paramDsfZookeeperDataManager);
  
  public abstract DsfZookeeperDataManager getZookeeperDataManager();
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\ServiceRegistryAgent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */