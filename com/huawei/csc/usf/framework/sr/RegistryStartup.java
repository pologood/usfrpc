package com.huawei.csc.usf.framework.sr;

import com.huawei.csc.usf.framework.config.SystemConfig;
import java.util.List;
import java.util.Map;

public abstract interface RegistryStartup
{
  public static final String DEFAULT_REGID = "dsf_default";
  
  public abstract void init(SystemConfig paramSystemConfig, List<String> paramList)
    throws Exception;
  
  public abstract Map<String, Registry> getIdRegistryMap();
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\RegistryStartup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */