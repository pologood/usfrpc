package com.huawei.csc.usf.framework.pojo;

public abstract interface ProxyHandler
{
  public abstract boolean isProxyClass(Class<?> paramClass);
  
  public abstract Class<?> getProxyClass(Object paramObject, Class<?> paramClass);
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\pojo\ProxyHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */