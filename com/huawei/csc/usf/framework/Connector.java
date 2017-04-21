package com.huawei.csc.usf.framework;

public abstract interface Connector
  extends ServiceEngineAware
{
  public abstract void init()
    throws Exception;
  
  public abstract void destroy()
    throws Exception;
  
  public abstract boolean isReadyToDestroy();
  
  public abstract <T> T onReceive(Context paramContext)
    throws Exception;
  
  public abstract IMessage handle(Context paramContext, IMessage paramIMessage)
    throws Exception;
  
  public abstract IMessage decode(Context paramContext)
    throws Exception;
  
  public abstract <T> T encode(Context paramContext, IMessage paramIMessage)
    throws Exception;
  
  public abstract boolean isAsync();
  
  public abstract String getConnectorType();
  
  public abstract void startTraceLog(IMessage paramIMessage);
  
  public abstract void asyncEndTraceLog(IMessage paramIMessage);
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\Connector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */