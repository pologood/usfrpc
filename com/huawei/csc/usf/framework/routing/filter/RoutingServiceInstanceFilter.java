package com.huawei.csc.usf.framework.routing.filter;

import com.huawei.csc.usf.framework.Context;
import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
import java.util.List;

public abstract interface RoutingServiceInstanceFilter
{
  public abstract void filter(List<ServiceInstanceInner> paramList, Context paramContext);
  
  public abstract String getFilterName();
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\filter\RoutingServiceInstanceFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */