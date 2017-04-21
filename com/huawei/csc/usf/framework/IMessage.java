package com.huawei.csc.usf.framework;

public abstract interface IMessage
{
  public abstract MessageHeaders getHeaders();
  
  public abstract <T> T getPayload();
  
  public abstract <T> void setPayload(T paramT);
  
  public abstract void setException(Exception paramException);
  
  public abstract boolean isFault();
  
  public abstract <T> void setReturn(T paramT);
  
  public abstract boolean hasException();
  
  public abstract GroupNameInterpret getGroupNameInterpret();
  
  public abstract void setGroupNameInterpret(GroupNameInterpret paramGroupNameInterpret);
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\IMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */