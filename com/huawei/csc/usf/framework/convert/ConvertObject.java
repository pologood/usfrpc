package com.huawei.csc.usf.framework.convert;

import java.io.IOException;
import java.io.InputStream;

public abstract interface ConvertObject
{
  public abstract Object convert(Object paramObject, InputStream paramInputStream, String paramString)
    throws IOException;
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\convert\ConvertObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */