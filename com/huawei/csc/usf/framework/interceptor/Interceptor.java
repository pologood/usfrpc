package com.huawei.csc.usf.framework.interceptor;

import com.huawei.csc.usf.framework.Context;

public abstract interface Interceptor
{
  public abstract String getName();
  
  public abstract void init();
  
  public abstract void invoke(InterceptorExecutor<?> paramInterceptorExecutor, Context paramContext)
    throws Exception;
  
  public abstract void destroy();
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\interceptor\Interceptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */