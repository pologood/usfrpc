package com.huawei.csc.usf.framework.interceptor;

public abstract interface CustomOutInterceptor
  extends OutInterceptor
{
  public abstract String getBefore();
  
  public abstract void setBefore(String paramString);
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\interceptor\CustomOutInterceptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */