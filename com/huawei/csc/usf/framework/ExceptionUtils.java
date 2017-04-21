/*     */ package com.huawei.csc.usf.framework;
/*     */ 
/*     */ import com.huawei.bme.commons.exception.BMEException;
/*     */ import com.huawei.csc.usf.framework.exception.USFException;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExceptionUtils
/*     */ {
/*     */   public ServiceType getServiceType()
/*     */   {
/*  24 */     return ServiceType.USF;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setServiceType() {}
/*     */   
/*     */ 
/*     */   public Exception routeFailedErr(Object[] paras)
/*     */   {
/*  34 */     return new USFException("405180800", paras);
/*     */   }
/*     */   
/*     */   public Exception invalidArgument(Exception cause)
/*     */   {
/*  39 */     return new USFException("405180801", cause);
/*     */   }
/*     */   
/*     */   public Exception commoninvokeParameterNullErr(Object[] paras)
/*     */   {
/*  44 */     return new USFException("405180802", paras);
/*     */   }
/*     */   
/*     */ 
/*     */   public Exception pojoOperationNotFound(Object[] paras)
/*     */   {
/*  50 */     return new USFException("405180803", paras);
/*     */   }
/*     */   
/*     */   public Exception timeOutErr(Object[] paras)
/*     */   {
/*  55 */     return new USFException("405180804", paras);
/*     */   }
/*     */   
/*     */   public Exception clientAsynWaitQueueFull(Object[] paras)
/*     */   {
/*  60 */     return new USFException("405180814", paras);
/*     */   }
/*     */   
/*     */   public Exception canNotFindConnector(String serviceName)
/*     */   {
/*  65 */     return new USFException("405180805", serviceName);
/*     */   }
/*     */   
/*     */ 
/*     */   public Exception initFailed(Exception cause)
/*     */   {
/*  71 */     return new USFException("405180806", cause);
/*     */   }
/*     */   
/*     */   public Exception serverMessageRefused(String msg)
/*     */   {
/*  76 */     return new USFException("405180807", msg);
/*     */   }
/*     */   
/*     */   public Exception clientMessageRefused(String msg)
/*     */   {
/*  81 */     return new USFException("405180808", msg);
/*     */   }
/*     */   
/*     */   public Exception remoteNotReachable(String msg)
/*     */   {
/*  86 */     return new USFException("405180809", msg);
/*     */   }
/*     */   
/*     */   public Exception lackConfig(String msg)
/*     */   {
/*  91 */     return new USFException("405180819", msg);
/*     */   }
/*     */   
/*     */ 
/*     */   public Exception accessOverConcurrentflow(String service, String operation, int executes, int current)
/*     */   {
/*  97 */     return new USFException("405180810", new Object[] { service, operation, Integer.valueOf(executes), Integer.valueOf(current) });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Exception accessOverTpsflow(String service, String operation, int executes, int current)
/*     */   {
/* 104 */     return new USFException("405180815", new Object[] { service, operation, Integer.valueOf(executes), Integer.valueOf(current) });
/*     */   }
/*     */   
/*     */ 
/*     */   public Exception configInitFailed(Exception cause)
/*     */   {
/* 110 */     return new USFException("405180811", cause);
/*     */   }
/*     */   
/*     */   public Exception ipBindFailed(String ip, String port)
/*     */   {
/* 115 */     return new USFException("405180812", new Object[] { ip, port });
/*     */   }
/*     */   
/*     */ 
/*     */   public Exception sslInvailedFilePath(String path)
/*     */   {
/* 121 */     return new USFException("405180813", new Object[] { path });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Exception clientCircuitBreaker(String serviceName, String requestThreadHold, String errPercentage)
/*     */   {
/* 128 */     return new USFException("405180816", new Object[] { serviceName, requestThreadHold, errPercentage });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Exception clientSemaphoreReject(String serviceName, String semaphoreLimit)
/*     */   {
/* 135 */     return new USFException("405180817", new Object[] { serviceName, semaphoreLimit });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Exception clientThreadPoolReject(String serviceName, String coreThreadNum, String waitQueueNum)
/*     */   {
/* 142 */     return new USFException("405180818", new Object[] { serviceName, coreThreadNum, waitQueueNum });
/*     */   }
/*     */   
/*     */ 
/*     */   public Exception mockDeserializeFailed()
/*     */   {
/* 148 */     return new USFException("405180820");
/*     */   }
/*     */   
/*     */   public Exception implNotFound(String msg)
/*     */   {
/* 153 */     return null;
/*     */   }
/*     */   
/*     */   public BMEException coreJsonErr(String msg)
/*     */   {
/* 158 */     return null;
/*     */   }
/*     */   
/*     */   public BMEException coreClassNotFound(Exception e)
/*     */   {
/* 163 */     return null;
/*     */   }
/*     */   
/*     */   public BMEException coreMsgToPojoErr(Object[] args)
/*     */   {
/* 168 */     return null;
/*     */   }
/*     */   
/*     */   public BMEException coreUnknownRemote(Exception e)
/*     */   {
/* 173 */     return null;
/*     */   }
/*     */   
/*     */   public BMEException nullValue(Object[] args)
/*     */   {
/* 178 */     return null;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\ExceptionUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */