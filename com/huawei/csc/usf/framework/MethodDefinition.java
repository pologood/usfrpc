package com.huawei.csc.usf.framework;

public abstract interface MethodDefinition
{
  public abstract String getMethodName();
  
  public abstract void setMethodName(String paramString);
  
  public abstract String getTimeout();
  
  public abstract void setTimeout(String paramString);
  
  public abstract String getExecutes();
  
  public abstract void setExecutes(String paramString);
  
  public abstract String getThreshold();
  
  public abstract void setThreshold(String paramString);
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\MethodDefinition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */