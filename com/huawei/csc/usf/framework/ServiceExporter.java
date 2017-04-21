package com.huawei.csc.usf.framework;

import com.huawei.csc.usf.framework.sr.ServiceInner;
import java.util.Collection;
import java.util.List;

public abstract interface ServiceExporter
{
  public abstract void loadService(Object... paramVarArgs);
  
  public abstract Collection<String> getExports();
  
  public abstract List<ServiceInner> getServiceList();
  
  public abstract List<ServiceInner> getExportServiceList();
  
  public abstract List<ServiceDefinition> getServiceDefinitions();
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\ServiceExporter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */