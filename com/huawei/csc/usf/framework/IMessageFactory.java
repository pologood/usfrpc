package com.huawei.csc.usf.framework;

import com.huawei.csc.usf.framework.config.SystemConfig;
import com.huawei.csc.usf.framework.sr.ServiceTypeAware;

public abstract interface IMessageFactory
  extends ServiceTypeAware
{
  public abstract IMessage createRequest(String paramString1, String paramString2, String paramString3);
  
  public abstract IMessage createReplyMessage(IMessage paramIMessage);
  
  public abstract IMessage createMessage();
  
  public abstract void setSystemConfig(SystemConfig paramSystemConfig);
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\IMessageFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */