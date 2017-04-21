package com.huawei.csc.usf.framework;

public abstract interface ServiceEngineInitCallback
{
  public abstract void beforeInitCallback(ServiceEngine paramServiceEngine)
    throws Exception;
  
  public abstract void afterInitCallback(ServiceEngine paramServiceEngine)
    throws Exception;
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\ServiceEngineInitCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */