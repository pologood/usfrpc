package com.huawei.csc.usf.framework.monitor.jmx.management;

import com.huawei.csc.usf.framework.jmx.management.Managementable;
import com.huawei.csc.usf.framework.monitor.threadpool.ThreadPoolMonitorInfo;
import java.util.List;
import javax.management.MXBean;

@MXBean(true)
public abstract interface ThreadPoolMonitorManagement
  extends Managementable
{
  public abstract List<ThreadPoolMonitorInfo> getThreadPoolMonitorData();
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\monitor\jmx\management\ThreadPoolMonitorManagement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */