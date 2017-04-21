/*     */ package com.huawei.csc.usf.framework.interceptor;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.Connector;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.circuitbreaker.CommandMetrics;
/*     */ import com.huawei.csc.usf.framework.circuitbreaker.DsfCircuitBreaker;
/*     */ import com.huawei.csc.usf.framework.circuitbreaker.DsfCircuitBreaker.Factory;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.event.FrameworkEvent;
/*     */ import com.huawei.csc.usf.framework.event.ServiceFrameworkEvent;
/*     */ import com.huawei.csc.usf.framework.event.ServiceFrameworkEventPublisher;
/*     */ import com.huawei.csc.usf.framework.interceptor.impl.SelectFaultExecutorInterceptor;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class InterceptorManager
/*     */ {
/*  45 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(InterceptorManager.class);
/*     */   
/*     */ 
/*  48 */   private List<InInterceptor> inInterceptors = new ArrayList();
/*     */   
/*  50 */   private List<OutInterceptor> outInterceptors = new ArrayList();
/*     */   
/*  52 */   private List<FaultInterceptor> faultInterceptors = new ArrayList();
/*     */   
/*  54 */   private List<TimeoutReplyFaultInterceptor> timeoutReplyFaultInterceptors = new ArrayList();
/*     */   
/*     */   private SelectFaultExecutorInterceptor reSendHandler;
/*     */   
/*     */   public void dispatch(Context context)
/*     */     throws Exception
/*     */   {
/*  61 */     if (context.getSrcConnector().isAsync())
/*     */     {
/*  63 */       asyncDispatch(context);
/*     */     }
/*     */     else
/*     */     {
/*  67 */       syncDispatch(context);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void syncDispatch(Context context)
/*     */     throws Exception
/*     */   {
/*  75 */     sendRequest(context);
/*     */     
/*  77 */     if (context.isFault())
/*     */     {
/*  79 */       sendFault(context);
/*     */       
/*  81 */       Throwable exception = context.getException();
/*  82 */       int reSendTimes = context.getSrcConnector().getServiceEngine().getSystemConfig().getResendTimes();
/*     */       
/*  84 */       if (null != exception)
/*     */       {
/*  86 */         if (context.getSrcConnector().getServiceEngine().getSystemConfig().getServerFailPolicy().equalsIgnoreCase("failfast"))
/*     */         {
/*     */ 
/*     */ 
/*  90 */           DEBUGGER.error("Request interceptor execetor error " + exception);
/*     */ 
/*     */         }
/*  93 */         else if (context.getTimesBeenSent() >= reSendTimes)
/*     */         {
/*  95 */           DEBUGGER.error("Request interceptor execetor error " + exception);
/*     */         }
/*     */         
/*  98 */         if (this.reSendHandler != null)
/*     */         {
/* 100 */           this.reSendHandler.reSend(context);
/*     */         }
/*     */         else
/*     */         {
/* 104 */           this.reSendHandler = new SelectFaultExecutorInterceptor();
/* 105 */           this.reSendHandler.reSend(context);
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 111 */       CommandMetrics metrics = null;
/*     */       
/* 113 */       if (context.isSysnFirstNode())
/*     */       {
/* 115 */         String serviceName = context.getReceivedMessage().getHeaders().getServiceName();
/*     */         
/*     */ 
/* 118 */         metrics = CommandMetrics.getInstance(serviceName);
/*     */       }
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 124 */         syncSendReply(context);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 128 */         context.setException(e);
/* 129 */         context.setFault(true);
/*     */       }
/* 131 */       if (context.isFault())
/*     */       {
/* 133 */         if ((context.isSysnFirstNode()) && (!isServiceDegradeBreaker(context)))
/*     */         {
/*     */ 
/* 136 */           metrics.markFailure();
/*     */         }
/* 138 */         sendFault(context);
/*     */ 
/*     */ 
/*     */       }
/* 142 */       else if ((context.isSysnFirstNode()) && (!isServiceDegradeBreaker(context)))
/*     */       {
/*     */ 
/* 145 */         String serviceName = context.getReceivedMessage().getHeaders().getServiceName();
/*     */         
/*     */ 
/* 148 */         DsfCircuitBreaker dsfCircuitBreak = DsfCircuitBreaker.Factory.getInstance(serviceName, metrics);
/*     */         
/* 150 */         dsfCircuitBreak.markSuccess();
/* 151 */         metrics.markSuccess();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 156 */       Throwable exception = context.getException();
/* 157 */       if (null != exception)
/*     */       {
/* 159 */         DEBUGGER.error("interceptor execetor error " + exception);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean isServiceDegradeBreaker(Context context)
/*     */   {
/* 168 */     String degradeType = (String)context.getAttachment("ServiceDegrade");
/*     */     
/* 170 */     if (("FalseReturnException".equals(degradeType)) || ("FalseReturnNull".equals(degradeType)))
/*     */     {
/*     */ 
/* 173 */       return true;
/*     */     }
/*     */     
/* 176 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public void asyncDispatch(Context context)
/*     */     throws Exception
/*     */   {
/* 183 */     sendRequest(context);
/*     */     
/* 185 */     if (context.isFault())
/*     */     {
/* 187 */       sendFault(context);
/*     */     }
/*     */     
/* 190 */     Throwable exception = context.getException();
/* 191 */     int reSendTimes = context.getSrcConnector().getServiceEngine().getSystemConfig().getResendTimes();
/*     */     
/* 193 */     if (null != exception)
/*     */     {
/* 195 */       if (context.getSrcConnector().getServiceEngine().getSystemConfig().getServerFailPolicy().equalsIgnoreCase("failfast"))
/*     */       {
/*     */ 
/* 198 */         DEBUGGER.error("Request interceptor execetor error " + exception);
/*     */ 
/*     */       }
/* 201 */       else if (context.getTimesBeenSent() >= reSendTimes)
/*     */       {
/* 203 */         DEBUGGER.error("Request interceptor execetor error " + exception);
/*     */       }
/*     */       
/* 206 */       if (this.reSendHandler != null)
/*     */       {
/* 208 */         this.reSendHandler.reSend(context);
/*     */       }
/*     */       else
/*     */       {
/* 212 */         this.reSendHandler = new SelectFaultExecutorInterceptor();
/* 213 */         this.reSendHandler.reSend(context);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void syncSendReply(Context context)
/*     */     throws Exception
/*     */   {
/* 221 */     sendReply(context);
/*     */   }
/*     */   
/*     */   public void sendReply(Context context)
/*     */     throws Exception
/*     */   {
/* 227 */     context.setBroken(false);
/* 228 */     InterceptorExecutor<OutInterceptor> replyExecutor = new InterceptorExecutor(this.outInterceptors);
/*     */     
/* 230 */     replyExecutor.invok(context);
/*     */   }
/*     */   
/*     */   public void sendFault(Context context)
/*     */     throws Exception
/*     */   {
/* 236 */     context.setBroken(false);
/*     */     
/* 238 */     if (context.isRepeatFault())
/* 239 */       return;
/* 240 */     InterceptorExecutor<FaultInterceptor> exceptionExecutor = new InterceptorExecutor(this.faultInterceptors);
/*     */     
/* 242 */     exceptionExecutor.invok(context);
/*     */     
/* 244 */     context.setRepeatFault(true);
/*     */   }
/*     */   
/*     */   public void asyncCallback(Context context)
/*     */     throws Exception
/*     */   {
/* 250 */     IMessage reply = context.getReplyMessage();
/* 251 */     if ((null != reply) && (reply.isFault()))
/*     */     {
/* 253 */       if ((reply.getPayload() instanceof Throwable))
/*     */       {
/* 255 */         context.setException((Throwable)reply.getPayload());
/*     */       }
/* 257 */       context.setFault(true);
/*     */     }
/*     */     
/* 260 */     if (context.isFault())
/*     */     {
/* 262 */       sendFault(context);
/*     */     }
/*     */     else
/*     */     {
/*     */       try
/*     */       {
/* 268 */         sendReply(context);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 272 */         context.setException(e);
/* 273 */         context.setFault(true);
/*     */       }
/*     */       
/* 276 */       IMessage replyMessage = context.getReplyMessage();
/* 277 */       if ((null != replyMessage) && (replyMessage.isFault()))
/*     */       {
/* 279 */         if ((replyMessage.getPayload() instanceof Throwable))
/*     */         {
/* 281 */           context.setException((Throwable)replyMessage.getPayload());
/*     */         }
/* 283 */         context.setFault(true);
/*     */       }
/* 285 */       if (context.isFault())
/*     */       {
/* 287 */         sendFault(context);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 292 */     Throwable exception = context.getException();
/* 293 */     if (null != exception)
/*     */     {
/* 295 */       DEBUGGER.error("interceptor execetor error " + exception);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void sendRequest(Context context)
/*     */     throws Exception
/*     */   {
/* 303 */     context.setBroken(false);
/* 304 */     InterceptorExecutor<InInterceptor> sendRequestExecutor = new InterceptorExecutor(this.inInterceptors);
/*     */     
/*     */     try
/*     */     {
/* 308 */       context.setStartTime(System.nanoTime());
/* 309 */       sendRequestExecutor.invok(context);
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 314 */       context.setException(e);
/* 315 */       context.setFault(true);
/* 316 */       IMessage receivedMessage = context.getReceivedMessage();
/*     */       
/*     */ 
/* 319 */       if ((!context.isServer()) && (receivedMessage != null))
/*     */       {
/* 321 */         ServiceFrameworkEvent event = FrameworkEvent.creatEvent(receivedMessage, context.getSrcConnector().getConnectorType(), context, Boolean.valueOf(true), Boolean.valueOf(!context.getSrcConnector().isAsync()), null, Long.valueOf(System.nanoTime() - context.getStartTime()), e);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 327 */         ServiceFrameworkEventPublisher.publish("usf.framework.doDispatchException", event);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 333 */     IMessage replyMessage = context.getReplyMessage();
/* 334 */     if ((null != replyMessage) && (replyMessage.isFault()))
/*     */     {
/* 336 */       if ((replyMessage.getPayload() instanceof Throwable))
/*     */       {
/* 338 */         context.setException((Throwable)replyMessage.getPayload());
/*     */       }
/* 340 */       context.setFault(true);
/*     */     }
/*     */   }
/*     */   
/*     */   public void sendTimeoutReply(Context context)
/*     */     throws Exception
/*     */   {
/* 347 */     InterceptorExecutor<TimeoutReplyFaultInterceptor> timeoutReplyExecutor = new InterceptorExecutor(this.timeoutReplyFaultInterceptors);
/*     */     
/* 349 */     timeoutReplyExecutor.invok(context);
/*     */   }
/*     */   
/*     */   public void addInInterceptor(InInterceptor inInterceptor)
/*     */   {
/* 354 */     this.inInterceptors.add(inInterceptor);
/*     */   }
/*     */   
/*     */   public void addOutInterceptor(OutInterceptor outInterceptor)
/*     */   {
/* 359 */     this.outInterceptors.add(outInterceptor);
/*     */   }
/*     */   
/*     */   public void addFaultInterceptor(FaultInterceptor faultInterceptor)
/*     */   {
/* 364 */     this.faultInterceptors.add(faultInterceptor);
/*     */   }
/*     */   
/*     */ 
/*     */   public void addTimeoutReplyFaultInterceptor(TimeoutReplyFaultInterceptor faultInterceptor)
/*     */   {
/* 370 */     this.timeoutReplyFaultInterceptors.add(faultInterceptor);
/*     */   }
/*     */   
/*     */   public List<InInterceptor> getInInterceptors()
/*     */   {
/* 375 */     return this.inInterceptors;
/*     */   }
/*     */   
/*     */   public List<OutInterceptor> getOutInterceptors()
/*     */   {
/* 380 */     return this.outInterceptors;
/*     */   }
/*     */   
/*     */   public List<FaultInterceptor> getFaultInterceptors()
/*     */   {
/* 385 */     return this.faultInterceptors;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\interceptor\InterceptorManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */