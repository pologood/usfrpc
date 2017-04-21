package com.huawei.csc.usf.framework.trace;

public abstract class AbstractInvokeServiceListener
  implements InvokeServiceListener
{
  public void onBegin(ServiceContext context) {}
  
  public void onEnd(ServiceContext context) {}
  
  public void onException(ServiceContext context) {}
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\trace\AbstractInvokeServiceListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */