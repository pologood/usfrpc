package com.huawei.csc.usf.framework.routing;

import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
import java.util.List;

public abstract interface ServiceAddress
{
  public abstract ServiceInstanceInner getRouterAddress(List<ServiceInstanceInner> paramList, RoutingContext paramRoutingContext);
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\ServiceAddress.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */