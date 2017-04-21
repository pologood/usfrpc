package com.huawei.csc.usf.framework.bus;

import com.huawei.csc.remoting.common.buf.ProtoBuf;
import com.huawei.csc.usf.framework.util.USFCtxObject;
import java.util.Map;

public abstract interface DSFObjectCoder
{
  public abstract void encodeCtxObject(ProtoBuf paramProtoBuf, Map<String, USFCtxObject> paramMap, MsgSerializer paramMsgSerializer)
    throws Exception;
  
  public abstract Map<String, USFCtxObject> decodeCtxObject(ProtoBuf paramProtoBuf, MsgSerializer paramMsgSerializer);
  
  public abstract void encodeException(ProtoBuf paramProtoBuf, Object paramObject, MsgSerializer paramMsgSerializer)
    throws Exception;
  
  public abstract Object decodeException(ProtoBuf paramProtoBuf, MsgSerializer paramMsgSerializer);
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\bus\DSFObjectCoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */