package com.huawei.csc.usf.framework.bus;

import com.huawei.csc.remoting.common.EncodeInfo;
import com.huawei.csc.remoting.common.FrameworkMessage;
import com.huawei.csc.remoting.common.buf.ProtoBuf;
import com.huawei.csc.remoting.common.decode.DecodeInfo;

public abstract interface MessageObjectCoder
{
  public abstract EncodeInfo encode(EncodeInfo paramEncodeInfo, FrameworkMessage paramFrameworkMessage, Object paramObject);
  
  public abstract DecodeInfo decode(ProtoBuf paramProtoBuf);
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\bus\MessageObjectCoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */