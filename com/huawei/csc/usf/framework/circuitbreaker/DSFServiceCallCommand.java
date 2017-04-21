/*     */ package com.huawei.csc.usf.framework.circuitbreaker;
/*     */ 
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.ExceptionUtils;
/*     */ import com.huawei.csc.usf.framework.ExceptionUtilsHolder;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.interceptor.impl.BusinessExecutorInterceptor.ServiceCallTask;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DSFServiceCallCommand
/*     */ {
/*     */   private DSFThreadPool dsfThreadPool;
/*     */   private TryableSemaphore semaphore;
/*     */   private String application;
/*     */   private String service;
/*     */   private CommandMetrics metrics;
/*     */   private DsfCircuitBreaker dsfCircuitBreak;
/*     */   
/*     */   public DSFServiceCallCommand(String serviceName)
/*     */   {
/*  28 */     this.service = serviceName;
/*     */     
/*  30 */     this.metrics = CommandMetrics.getInstance(this.service);
/*     */     
/*  32 */     this.dsfCircuitBreak = DsfCircuitBreaker.Factory.getInstance(this.service, this.metrics);
/*     */   }
/*     */   
/*     */   public void execute(BusinessExecutorInterceptor.ServiceCallTask serviceCallTask)
/*     */     throws Exception
/*     */   {
/*  38 */     Context context = serviceCallTask.getContext();
/*     */     
/*  40 */     if (!context.isSysnFirstNode())
/*     */     {
/*  42 */       executeTask(serviceCallTask);
/*  43 */       return;
/*     */     }
/*     */     
/*  46 */     if (isServiceDegradeBreaker(context))
/*     */     {
/*  48 */       throw new Exception("Service degrade, no need call remoting service");
/*     */     }
/*     */     
/*     */ 
/*  52 */     boolean circuitBreakerEnable = Boolean.parseBoolean(context.getCircuitBreakProperties().getCircuitBreakerEnable());
/*     */     
/*     */ 
/*  55 */     if (circuitBreakerEnable)
/*     */     {
/*  57 */       if (!this.dsfCircuitBreak.allowRequest(context))
/*     */       {
/*  59 */         this.metrics.markShortCircuited();
/*  60 */         throw ExceptionUtilsHolder.getExceptionUtils(context.getServiceType()).clientCircuitBreaker(context.getReceivedMessage().getHeaders().getServiceName(), context.getCircuitBreakProperties().getRequestVolumeThreadHold().toString(), context.getCircuitBreakProperties().getErrThreadHoldPercentage().toString());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  71 */     if (DSFFaultToleranceProperties.DsfBulkheadIsolationStategy.SEMAPHORE.name().equals(context.getCircuitBreakProperties().getIsolationStategy()))
/*     */     {
/*     */ 
/*  74 */       semaphoreIsolation(serviceCallTask);
/*     */ 
/*     */     }
/*  77 */     else if (DSFFaultToleranceProperties.DsfBulkheadIsolationStategy.THREAD.name().equals(context.getCircuitBreakProperties().getIsolationStategy()))
/*     */     {
/*     */ 
/*  80 */       threadPoolIsolation(serviceCallTask);
/*     */     }
/*     */     else
/*     */     {
/*  84 */       executeTask(serviceCallTask);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean isServiceDegradeBreaker(Context context)
/*     */   {
/*  92 */     String degradeType = (String)context.getAttachment("ServiceDegrade");
/*     */     
/*  94 */     if (("FalseReturnException".equals(degradeType)) || ("FalseReturnNull".equals(degradeType)))
/*     */     {
/*     */ 
/*  97 */       return true;
/*     */     }
/*     */     
/* 100 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void semaphoreIsolation(BusinessExecutorInterceptor.ServiceCallTask serviceCallTask)
/*     */     throws Exception
/*     */   {
/* 108 */     Context context = serviceCallTask.getContext();
/*     */     
/* 110 */     TryableSemaphore semaphore = TryableSemaphoreCache.getInstance(this.service, context);
/*     */     
/*     */ 
/* 113 */     if (semaphore.tryAcquire())
/*     */     {
/*     */       try
/*     */       {
/* 117 */         executeTask(serviceCallTask);
/*     */       }
/*     */       finally
/*     */       {
/* 121 */         semaphore.release();
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 126 */       this.metrics.markSemaphoreRejection();
/* 127 */       throw ExceptionUtilsHolder.getExceptionUtils(context.getServiceType()).clientSemaphoreReject(context.getReceivedMessage().getHeaders().getServiceName(), context.getCircuitBreakProperties().getBulkheadMaxConcurrentRequests().toString());
/*     */     }
/*     */   }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void threadPoolIsolation(BusinessExecutorInterceptor.ServiceCallTask serviceCallTask)
/*     */     throws Exception
/*     */   {}
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DSFThreadPool getDsfThreadPool()
/*     */   {
/* 173 */     return this.dsfThreadPool;
/*     */   }
/*     */   
/*     */   public void setDsfThreadPool(DSFThreadPool dsfThreadPool)
/*     */   {
/* 178 */     this.dsfThreadPool = dsfThreadPool;
/*     */   }
/*     */   
/*     */   public TryableSemaphore getSemaphore()
/*     */   {
/* 183 */     return this.semaphore;
/*     */   }
/*     */   
/*     */   public void setSemaphore(TryableSemaphore semaphore)
/*     */   {
/* 188 */     this.semaphore = semaphore;
/*     */   }
/*     */   
/*     */   public String getApplication()
/*     */   {
/* 193 */     return this.application;
/*     */   }
/*     */   
/*     */   public void setApplication(String application)
/*     */   {
/* 198 */     this.application = application;
/*     */   }
/*     */   
/*     */   public String getService()
/*     */   {
/* 203 */     return this.service;
/*     */   }
/*     */   
/*     */   public void setService(String service)
/*     */   {
/* 208 */     this.service = service;
/*     */   }
/*     */   
/*     */   private void executeTask(BusinessExecutorInterceptor.ServiceCallTask serviceCallTask)
/*     */     throws Exception
/*     */   {
/* 214 */     Context context = serviceCallTask.getContext();
/*     */     
/*     */     try
/*     */     {
/* 218 */       serviceCallTask.call();
/*     */ 
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 223 */       if (context.isTimeoutException())
/*     */       {
/* 225 */         this.metrics.markTimeout();
/* 226 */         throw ex;
/*     */       }
/*     */       
/*     */ 
/* 230 */       this.metrics.markFailure();
/* 231 */       throw ex;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 236 */     IMessage replyMessage = context.getReplyMessage();
/* 237 */     if ((null != replyMessage) && (replyMessage.isFault()))
/*     */     {
/* 239 */       this.metrics.markFailure();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CommandMetrics getMetrics()
/*     */   {
/* 250 */     return this.metrics;
/*     */   }
/*     */   
/*     */   public void setMetrics(CommandMetrics metrics)
/*     */   {
/* 255 */     this.metrics = metrics;
/*     */   }
/*     */   
/*     */   public String getCommandKey()
/*     */   {
/* 260 */     return this.application + this.service;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\circuitbreaker\DSFServiceCallCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */