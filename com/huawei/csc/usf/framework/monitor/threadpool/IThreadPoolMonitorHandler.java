package com.huawei.csc.usf.framework.monitor.threadpool;

import java.util.List;

public abstract interface IThreadPoolMonitorHandler
{
  public abstract void invoke(List<ThreadPoolMonitorInfo> paramList);
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\monitor\threadpool\IThreadPoolMonitorHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */