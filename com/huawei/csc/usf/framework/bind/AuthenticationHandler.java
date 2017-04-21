package com.huawei.csc.usf.framework.bind;

import com.huawei.csc.remoting.common.connection.InternalMessagingService;

public abstract interface AuthenticationHandler
{
  public abstract void init(Object paramObject);
  
  public abstract Object authentication(InternalMessagingService paramInternalMessagingService, Object paramObject)
    throws Exception;
  
  public abstract Object processAuthentication(Object paramObject1, Object paramObject2)
    throws Exception;
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\bind\AuthenticationHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */