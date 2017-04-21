package com.huawei.csc.usf.framework.routing;

import com.huawei.csc.usf.framework.Context;
import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
import java.util.List;

public abstract interface RoutingProcessor
{
  public abstract void process(Object paramObject, Context paramContext, List<ServiceInstanceInner> paramList);
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\RoutingProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */