package com.huawei.csc.usf.framework;

import com.huawei.csc.usf.framework.util.USFCtxObject;
import java.util.Map;

public abstract interface MessageHeaders
{
  public static final String REQUEST_TYPE = "request";
  public static final String REPLY_TYPE = "reply";
  public static final String HEARTBEAT_REQUEST_TYPE = "heartBeat";
  public static final String BIND_REQUEST_TYPE = "bindRequest";
  public static final String BIND_RESPONSE_TYPE = "bindResponse";
  public static final String BTYE_BUFF_TYPE = "byteBuffType";
  
  public abstract String getDestAddr();
  
  public abstract void setDestAddr(String paramString);
  
  public abstract String getGroup();
  
  public abstract void setGroup(String paramString);
  
  public abstract String getVersion();
  
  public abstract void setVersion(String paramString);
  
  public abstract String getServiceName();
  
  public abstract void setServiceName(String paramString);
  
  public abstract String getOperation();
  
  public abstract void setOperation(String paramString);
  
  public abstract String getPolicyServiceName();
  
  public abstract void setPolicyServiceName(String paramString);
  
  public abstract String getPolicyOperation();
  
  public abstract void setPolicyOperation(String paramString);
  
  public abstract String getPolicyProtocol();
  
  public abstract void setPolicyProtocol(String paramString);
  
  public abstract String getSrcAddr();
  
  public abstract void setSrcAddr(String paramString);
  
  public abstract String getType();
  
  public abstract void setType(String paramString);
  
  public abstract boolean isType(String paramString);
  
  public abstract long getRequestId();
  
  public abstract void setRequestId(long paramLong);
  
  public abstract Map<String, USFCtxObject> getAttachment();
  
  public abstract void setAttachment(Map<String, USFCtxObject> paramMap);
  
  public abstract String getAttachValue(String paramString);
  
  public abstract void setAttachValue(String paramString1, String paramString2);
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\MessageHeaders.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */