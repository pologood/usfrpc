package com.huawei.csc.usf.framework.service;

import com.huawei.csc.usf.framework.sr.ServiceInner;
import java.util.List;

public abstract interface ServicePublishListener
{
  public abstract void onServicePublish(List<ServiceInner> paramList);
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\service\ServicePublishListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */