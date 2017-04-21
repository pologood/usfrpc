package com.huawei.csc.usf.framework.bus;

public abstract interface MsgSerializer
{
  public abstract byte getSerializeTag();
  
  public abstract byte[] serialize(Object paramObject);
  
  public abstract Object deserialize(byte[] paramArrayOfByte);
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\bus\MsgSerializer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */