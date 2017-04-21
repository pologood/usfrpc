package com.huawei.csc.usf.framework;

import java.util.Map;

public abstract interface ServiceDefinition
{
  public abstract String getServiceName();
  
  public abstract void setServiceName(String paramString);
  
  public abstract String getConnectorType();
  
  public abstract void setConnectorType(String paramString);
  
  public abstract void setProtocolType(String paramString);
  
  public abstract String getProtocolType();
  
  public abstract void setRegistry(String paramString);
  
  public abstract String getRegistry();
  
  public abstract void setTimeout(long paramLong);
  
  public abstract long getTimeout();
  
  public abstract void setGroup(String paramString);
  
  public abstract String getGroup();
  
  public abstract boolean isServer();
  
  public abstract void setIsServer(boolean paramBoolean);
  
  public abstract String getServiceAddress();
  
  public abstract void setServiceAddress(String paramString);
  
  public abstract String getServiceInterface();
  
  public abstract void setServiceInterface(String paramString);
  
  public abstract String getThreadPool();
  
  public abstract void setThreadPool(String paramString);
  
  public abstract String getRouterId();
  
  public abstract void setRouterId(String paramString);
  
  public abstract String getFailPolicy();
  
  public abstract String getBeanName();
  
  public abstract void setBeanName(String paramString);
  
  public abstract void setFailPolicy(String paramString);
  
  public abstract void addMethodDefinition(String paramString, MethodDefinition paramMethodDefinition);
  
  public abstract MethodDefinition getMethodDefinition(String paramString);
  
  public abstract Map<String, MethodDefinition> getMethodDefinitions();
  
  public abstract int getExecutes();
  
  public abstract void setExecutes(int paramInt);
  
  public abstract String getThreshold();
  
  public abstract void setThreshold(String paramString);
  
  public abstract String getVersion();
  
  public abstract void setVersion(String paramString);
  
  public abstract String getRestUrl();
  
  public abstract void setResturl(String paramString);
  
  public abstract String getRestProtocolDirection();
  
  public abstract void setRestProtocolDirection(String paramString);
  
  public abstract <T> T getProperty(String paramString);
  
  public abstract void setProperty(String paramString, Object paramObject);
  
  public abstract void setSerialization(String paramString);
  
  public abstract String getSerialization();
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\ServiceDefinition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */