package com.huawei.csc.usf.framework.trace;

public abstract interface InvokeServiceListener
{
  public abstract void onBegin(ServiceContext paramServiceContext);
  
  public abstract void onEnd(ServiceContext paramServiceContext);
  
  public abstract void onException(ServiceContext paramServiceContext);
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\trace\InvokeServiceListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */