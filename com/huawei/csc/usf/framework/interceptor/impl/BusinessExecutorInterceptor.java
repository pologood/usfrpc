/*     */ package com.huawei.csc.usf.framework.interceptor.impl;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.Connector;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.ExceptionUtils;
/*     */ import com.huawei.csc.usf.framework.ExceptionUtilsHolder;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.IMessageFactory;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.NestedReplyListener;
/*     */ import com.huawei.csc.usf.framework.ReplyInterceptor;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.bus.BusConnector;
/*     */ import com.huawei.csc.usf.framework.circuitbreaker.DSFFaultToleranceProperties;
/*     */ import com.huawei.csc.usf.framework.circuitbreaker.DSFFaultTolerancePropertiesCache;
/*     */ import com.huawei.csc.usf.framework.circuitbreaker.DSFServiceCallCommand;
/*     */ import com.huawei.csc.usf.framework.circuitbreaker.DSFServiceCallCommandFactory;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.event.FrameworkEvent;
/*     */ import com.huawei.csc.usf.framework.event.ServiceFrameworkEvent;
/*     */ import com.huawei.csc.usf.framework.event.ServiceFrameworkEventPublisher;
/*     */ import com.huawei.csc.usf.framework.executor.NamedThreadFactory;
/*     */ import com.huawei.csc.usf.framework.flowcontrol.ConcurrentFlowControlResult;
/*     */ import com.huawei.csc.usf.framework.flowcontrol.FlowControlManager;
/*     */ import com.huawei.csc.usf.framework.interceptor.InInterceptor;
/*     */ import com.huawei.csc.usf.framework.sr.DsfZookeeperDataManager;
/*     */ import com.huawei.csc.usf.framework.sr.SRAgentFactory;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceRegistryAgent;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*     */ import com.huawei.csc.usf.framework.statistic.ProcessDelayTracker;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicLong;
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
/*     */ public class BusinessExecutorInterceptor
/*     */   extends AbstractInterceptor
/*     */   implements InInterceptor
/*     */ {
/*  62 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(BusinessExecutorInterceptor.class);
/*     */   
/*     */ 
/*  65 */   private int inWeight = 2000;
/*     */   
/*     */   public static final String NAME = "invoke";
/*     */   
/*     */   private static final long MS_TO_NS = 1000000L;
/*     */   
/*  71 */   private FlowControlManager manager = FlowControlManager.getInstance();
/*     */   
/*     */   public void doInvoke(Context context)
/*     */     throws Exception
/*     */   {
/*  76 */     IMessage receivedMessage = context.getReceivedMessage();
/*     */     
/*  78 */     Connector srcConnector = context.getSrcConnector();
/*  79 */     if (!context.isServer())
/*     */     {
/*  81 */       setTimeout(context);
/*     */     }
/*  83 */     if ((null == receivedMessage) || (null == srcConnector))
/*     */     {
/*  85 */       DEBUGGER.error("receieved message or src connector is null");
/*  86 */       return;
/*     */     }
/*     */     
/*  89 */     ServiceEngine engine = srcConnector.getServiceEngine();
/*     */     
/*     */ 
/*  92 */     if ((null != receivedMessage.getHeaders()) && ("reply".equals(receivedMessage.getHeaders().getType())))
/*     */     {
/*     */ 
/*     */ 
/*  96 */       if (!srcConnector.isAsync())
/*     */       {
/*  98 */         context.setReplyMessage(receivedMessage);
/*  99 */         return;
/*     */       }
/*     */       
/*     */ 
/* 103 */       receivedMessage = context.getReceivedMessage();
/* 104 */       context.setReplyMessage(receivedMessage);
/* 105 */       NestedReplyListener nestedReplyListener = new NestedReplyListener(engine.getInterceptorManager(), context.getReplyMessageListener());
/*     */       
/*     */ 
/*     */ 
/* 109 */       nestedReplyListener.onReply(context, context.getReplyMessage());
/* 110 */       return;
/*     */     }
/*     */     
/*     */ 
/* 114 */     Connector destConnector = engine.findConnectorByContext(context);
/*     */     
/* 116 */     context.setDestConnector(destConnector);
/*     */     
/*     */ 
/* 119 */     if (srcConnector.isAsync())
/*     */     {
/* 121 */       if (destConnector.isAsync())
/*     */       {
/* 123 */         handleASyncToAsyncMessage(destConnector, context, receivedMessage, engine.getReplyInterceptor(), engine.getMessageFactory(context.getServiceType()));
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 129 */         handleSyncToSyncWithFlowControl(destConnector, context, receivedMessage);
/*     */         
/*     */ 
/* 132 */         IMessage replyMessage = context.getReplyMessage();
/* 133 */         NestedReplyListener nestedReplyListener = new NestedReplyListener(engine.getInterceptorManager(), context.getReplyMessageListener());
/*     */         
/*     */ 
/*     */ 
/* 137 */         nestedReplyListener.onReply(context, replyMessage);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 142 */       String serviceName = receivedMessage.getHeaders().getServiceName();
/* 143 */       DSFFaultToleranceProperties circuitBreakerProperties = DSFFaultTolerancePropertiesCache.getInstance(serviceName, context);
/*     */       
/* 145 */       context.setCircuitBreakProperties(circuitBreakerProperties);
/* 146 */       DSFServiceCallCommandFactory.getDsfServiceCallCommand(serviceName).execute(new ServiceCallTask(context));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public class ServiceCallTask
/*     */     implements Callable<Object>
/*     */   {
/*     */     private Context context;
/*     */     
/*     */ 
/*     */     public Context getContext()
/*     */     {
/* 159 */       return this.context;
/*     */     }
/*     */     
/*     */     public ServiceCallTask(Context context)
/*     */     {
/* 164 */       this.context = context;
/*     */     }
/*     */     
/*     */     public Object call()
/*     */       throws Exception
/*     */     {
/* 170 */       if (this.context.getDestConnector().isAsync())
/*     */       {
/* 172 */         BusinessExecutorInterceptor.this.handleSyncToAsyncMessage(this.context.getDestConnector(), this.context, this.context.getReceivedMessage(), this.context.getSrcConnector().getServiceEngine().getReplyInterceptor());
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 178 */         BusinessExecutorInterceptor.this.handleSyncToSyncWithFlowControl(this.context.getDestConnector(), this.context, this.context.getReceivedMessage());
/*     */       }
/*     */       
/* 181 */       return null;
/*     */     }
/*     */   }
/*     */   
/*     */   protected void handleSyncToSyncMessage(Connector destConnector, Context context, IMessage receivedMessage)
/*     */     throws Exception
/*     */   {
/* 188 */     IMessage replyMessage = handleMessageByTracker(destConnector, context, receivedMessage);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected IMessage handleMessageByTracker(Connector destConnector, Context context, IMessage receivedMessage)
/*     */     throws Exception
/*     */   {
/* 197 */     long startTime = System.nanoTime();
/* 198 */     IMessage replyMessage = null;
/* 199 */     ProcessDelayTracker tracker = null;
/* 200 */     ProcessDelayTracker nextTracker = null;
/* 201 */     tracker = context.getProcessDelayTracker();
/*     */     
/* 203 */     nextTracker = ProcessDelayTracker.next(tracker, "invoke.handle");
/*     */     
/*     */     try
/*     */     {
/* 207 */       if (((context.isServer()) || (!isSendToBus(context))) && (!ServiceFrameworkEventPublisher.isEmpty()))
/*     */       {
/*     */ 
/* 210 */         ServiceFrameworkEvent event = FrameworkEvent.creatEvent(receivedMessage, destConnector.getConnectorType(), context, Boolean.valueOf(false), Boolean.valueOf(!destConnector.isAsync()), null, null, null);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 215 */         ServiceFrameworkEventPublisher.publish("usf.framework.beforeDoSend", event);
/*     */       }
/*     */       
/*     */ 
/* 219 */       replyMessage = destConnector.handle(context, receivedMessage);
/*     */       
/* 221 */       if (((context.isServer()) || (!isSendToBus(context))) && (!ServiceFrameworkEventPublisher.isEmpty()))
/*     */       {
/* 223 */         ServiceFrameworkEvent event = FrameworkEvent.creatEvent(receivedMessage, destConnector.getConnectorType(), context, Boolean.valueOf(false), Boolean.valueOf(!destConnector.isAsync()), replyMessage, Long.valueOf(System.nanoTime() - startTime), null);
/*     */         
/*     */ 
/*     */ 
/* 227 */         ServiceFrameworkEventPublisher.publish("usf.framework.afterDoSend", event);
/*     */       }
/*     */       
/*     */ 
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 234 */       if (DEBUGGER.isErrorEnable())
/*     */       {
/* 236 */         DEBUGGER.error("Send message error, message is[" + receivedMessage.toString() + "]", ex);
/*     */       }
/*     */       
/* 239 */       if ((context.isServer()) || (!isSendToBus(context)))
/*     */       {
/* 241 */         ServiceFrameworkEvent event = FrameworkEvent.creatEvent(receivedMessage, destConnector.getConnectorType(), context, Boolean.valueOf(false), Boolean.valueOf(!destConnector.isAsync()), replyMessage, Long.valueOf(System.nanoTime() - startTime), ex);
/*     */         
/*     */ 
/*     */ 
/* 245 */         ServiceFrameworkEventPublisher.publish("usf.framework.DoSendException", event);
/*     */       }
/*     */       
/* 248 */       throw ex;
/*     */     }
/*     */     finally
/*     */     {
/* 252 */       ProcessDelayTracker.done(nextTracker);
/*     */     }
/* 254 */     return replyMessage;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void handleSyncToAsyncMessage(Connector destConnector, Context context, IMessage receivedMessage, ReplyInterceptor replyInterceptor)
/*     */     throws Exception
/*     */   {
/* 262 */     long msTimeout = context.getMsTimeout();
/*     */     
/*     */ 
/* 265 */     long msWaited = 0L;
/*     */     try
/*     */     {
/* 268 */       replyInterceptor.putWait(receivedMessage.getHeaders().getRequestId(), context);
/*     */       
/*     */ 
/* 271 */       handleMessageByTracker(destConnector, context, receivedMessage);
/*     */       
/* 273 */       if (context.getReplyMessage() == null)
/*     */       {
/* 275 */         synchronized (context)
/*     */         {
/*     */ 
/* 278 */           if ((msTimeout < 1L) || (msTimeout > 2147483647L))
/*     */           {
/* 280 */             ServiceEngine serivceEngine = context.getSrcConnector().getServiceEngine();
/*     */             
/* 282 */             msTimeout = serivceEngine.getSystemConfig().getTimeout();
/*     */           }
/*     */           
/*     */ 
/* 286 */           long curMs = System.nanoTime() / 1000000L;
/* 287 */           long endMs = curMs + msTimeout;
/*     */           
/*     */ 
/* 290 */           while ((null == context.getReplyMessage()) && (null == context.getException()) && (curMs < endMs))
/*     */           {
/* 292 */             context.wait(endMs - curMs);
/* 293 */             curMs = System.nanoTime() / 1000000L;
/*     */           }
/* 295 */           msWaited = endMs - curMs;
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 301 */       if (DEBUGGER.isErrorEnable())
/*     */       {
/* 303 */         StringBuilder sb = new StringBuilder();
/* 304 */         sb.append("Connector[").append(destConnector).append("] is failed to handle msg[").append(receivedMessage.getHeaders()).append("] in [").append(msTimeout).append("]ms");
/*     */         
/*     */ 
/*     */ 
/* 308 */         DEBUGGER.error(sb.toString(), e);
/*     */       }
/*     */       
/*     */ 
/* 312 */       replyInterceptor.removeWait(receivedMessage.getHeaders().getRequestId());
/*     */       
/* 314 */       throw e;
/*     */     }
/*     */     
/* 317 */     handleException(replyInterceptor, receivedMessage, context, msTimeout, msWaited);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static void handleException(ReplyInterceptor replyInterceptor, IMessage request, Context context, long msTimeout, long msWaited)
/*     */     throws Exception
/*     */   {
/* 325 */     if (null == context.getReplyMessage())
/*     */     {
/*     */ 
/* 328 */       MessageHeaders headers = request.getHeaders();
/* 329 */       replyInterceptor.removeWait(headers.getRequestId());
/*     */       
/*     */ 
/*     */ 
/* 333 */       if (null == context.getException())
/*     */       {
/* 335 */         if (DEBUGGER.isErrorEnable())
/*     */         {
/* 337 */           DEBUGGER.error("Wait for reply timed out, messageId: " + headers.getRequestId() + ", msTimeout : " + msTimeout + " ms");
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */       }
/* 344 */       else if (DEBUGGER.isErrorEnable())
/*     */       {
/* 346 */         DEBUGGER.error("Exception occurs during waiting for reply , messageId: " + headers.getRequestId() + ", exception:", context.getException());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 353 */       replyInterceptor.getServiceEngine();
/* 354 */       if (null == context.getException())
/*     */       {
/* 356 */         context.setTimeoutException(true);
/* 357 */         throw ExceptionUtilsHolder.getExceptionUtils(context.getServiceType()).timeOutErr(new Object[] { Long.valueOf(headers.getRequestId()), headers.getServiceName(), headers.getOperation(), Long.valueOf(msTimeout) });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 365 */       throw ((Exception)context.getException());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 373 */     return "invoke";
/*     */   }
/*     */   
/* 376 */   private final ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(2, new NamedThreadFactory("cache scheduledService"));
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void handleASyncToAsyncMessage(Connector destConnector, Context context, IMessage receivedMessage, final ReplyInterceptor replyInterceptor, final IMessageFactory messagefactory)
/*     */     throws Exception
/*     */   {
/* 388 */     long msTimeout = context.getMsTimeout();
/* 389 */     final long reqid = receivedMessage.getHeaders().getRequestId();
/*     */     
/* 391 */     boolean result = replyInterceptor.putWait(reqid, context);
/*     */     
/* 393 */     if (!result)
/*     */     {
/* 395 */       Context ctx = replyInterceptor.removeWait(reqid);
/* 396 */       if (null == ctx)
/*     */       {
/* 398 */         return;
/*     */       }
/* 400 */       MessageHeaders headers = ctx.getReceivedMessage().getHeaders();
/* 401 */       long currentCount = replyInterceptor.getElementCount().longValue() + 1L;
/* 402 */       Exception exception = ExceptionUtilsHolder.getExceptionUtils(ctx.getServiceType()).clientAsynWaitQueueFull(new Object[] { Long.valueOf(headers.getRequestId()), headers.getServiceName(), headers.getOperation(), Long.valueOf(currentCount), Long.valueOf(replyInterceptor.getMaxElementCount()) });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 409 */       sendAsynExceptionReply(ctx, messagefactory, replyInterceptor, exception);
/*     */       
/* 411 */       if (DEBUGGER.isErrorEnable())
/*     */       {
/* 413 */         DEBUGGER.error("Asyn wait queue is full, currentQueueVaule : " + currentCount + "   maxQueueValue:" + replyInterceptor.getMaxElementCount());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 418 */       return;
/*     */     }
/*     */     
/* 421 */     if ((msTimeout < 1L) || (msTimeout > 2147483647L))
/*     */     {
/* 423 */       ServiceEngine serivceEngine = context.getSrcConnector().getServiceEngine();
/*     */       
/* 425 */       msTimeout = serivceEngine.getSystemConfig().getTimeout();
/*     */     }
/*     */     
/* 428 */     if (0L < msTimeout)
/*     */     {
/*     */ 
/* 431 */       long timeout = msTimeout;
/*     */       
/* 433 */       this.scheduledService.schedule(new Runnable()
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/* 438 */           Context ctx = replyInterceptor.removeWait(reqid);
/*     */           
/* 440 */           if (ctx == null)
/*     */           {
/* 442 */             return;
/*     */           }
/*     */           
/* 445 */           MessageHeaders headers = ctx.getReceivedMessage().getHeaders();
/*     */           
/*     */ 
/* 448 */           Exception exception = ExceptionUtilsHolder.getExceptionUtils(ctx.getServiceType()).timeOutErr(new Object[] { Long.valueOf(headers.getRequestId()), headers.getServiceName(), headers.getOperation(), Long.valueOf(messagefactory) });
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 454 */           BusinessExecutorInterceptor.this.sendAsynExceptionReply(ctx, this.val$messagefactory, replyInterceptor, exception);
/*     */           
/*     */ 
/* 457 */           if (BusinessExecutorInterceptor.DEBUGGER.isErrorEnable())
/*     */           {
/* 459 */             BusinessExecutorInterceptor.DEBUGGER.error("Wait for reply timed out, msTimeout : " + messagefactory + " ms"); } } }, msTimeout, TimeUnit.MILLISECONDS);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 466 */     handleMessageByTracker(destConnector, context, receivedMessage);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getInWeight()
/*     */   {
/* 472 */     return this.inWeight;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void sendAsynExceptionReply(Context ctx, IMessageFactory messagefactory, ReplyInterceptor replyInterceptor, Exception exception)
/*     */   {
/*     */     try
/*     */     {
/* 481 */       IMessage replyMessage = messagefactory.createReplyMessage(ctx.getReceivedMessage());
/*     */       
/* 483 */       replyInterceptor.getServiceEngine();
/* 484 */       ctx.setException(exception);
/*     */       
/*     */ 
/* 487 */       NestedReplyListener nestedReplyListener = new NestedReplyListener(ctx.getSrcConnector().getServiceEngine().getInterceptorManager(), ctx.getReplyMessageListener());
/*     */       
/*     */ 
/*     */ 
/* 491 */       nestedReplyListener.onReply(ctx, replyMessage);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 495 */       if (DEBUGGER.isErrorEnable())
/*     */       {
/*     */ 
/* 498 */         DEBUGGER.error("Unexpect exception when create reply", e);
/*     */       }
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
/*     */   private void handleSyncToSyncWithFlowControl(Connector destConnector, Context context, IMessage receivedMessage)
/*     */     throws Exception
/*     */   {
/* 515 */     if (!destConnector.getConnectorType().equals("POJO"))
/*     */     {
/*     */ 
/* 518 */       handleSyncToSyncMessage(destConnector, context, receivedMessage);
/* 519 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 524 */     if (!this.manager.isTpsFlowcontrol(context))
/*     */     {
/*     */ 
/* 527 */       ConcurrentFlowControlResult result = this.manager.isConcurrentFlowcontrol(context);
/*     */       
/* 529 */       if (ConcurrentFlowControlResult.NOT_EXCECUTE == result)
/*     */       {
/*     */ 
/* 532 */         handleSyncToSyncMessage(destConnector, context, receivedMessage);
/*     */       }
/* 534 */       else if (ConcurrentFlowControlResult.EXCECUTE_RESULTE_NOT_REJECT == result)
/*     */       {
/*     */         try
/*     */         {
/*     */ 
/* 539 */           handleSyncToSyncMessage(destConnector, context, receivedMessage);
/*     */ 
/*     */         }
/*     */         finally
/*     */         {
/* 544 */           this.manager.releaseConcurrent(context);
/*     */         }
/*     */         
/*     */       }
/*     */       else {
/* 549 */         this.manager.releaseConcurrent(context);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private String getAddress(ServiceType serviceType, Connector connector)
/*     */   {
/* 557 */     String rpcAddr = connector.getServiceEngine().getSystemConfig().getRPCAddress(serviceType);
/*     */     
/* 559 */     return rpcAddr;
/*     */   }
/*     */   
/*     */ 
/*     */   private void setTimeout(Context context)
/*     */   {
/* 565 */     IMessage receivedMessage = context.getReceivedMessage();
/* 566 */     String serviceName = receivedMessage.getHeaders().getServiceName();
/* 567 */     String operation = receivedMessage.getHeaders().getOperation();
/* 568 */     String adress = getAddress(context.getServiceType(), context.getSrcConnector());
/*     */     
/* 570 */     String dsfApplication = context.getSrcConnector().getServiceEngine().getSystemConfig().getDsfApplication();
/*     */     
/* 572 */     Long timeoutNum = (Long)context.getSrcConnector().getServiceEngine().getSrAgentFactory().getSRAgent(context.getRegistry()).getZookeeperDataManager().getConfigFromConfiguration(serviceName, operation, adress, dsfApplication, "timeout", "consumer");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 582 */     if (null == timeoutNum)
/*     */     {
/* 584 */       if (DEBUGGER.isDebugEnable())
/*     */       {
/* 586 */         DEBUGGER.debug("Get timeout from local properties, current timeout is: " + String.valueOf(context.getMsTimeout()));
/*     */       }
/*     */       
/* 589 */       return;
/*     */     }
/* 591 */     if (DEBUGGER.isDebugEnable())
/*     */     {
/* 593 */       DEBUGGER.debug(String.format("zk configuration about timeout become effective, zk configuration as follows: service=[%s], operation=[%s], timeout=[%s], .", new Object[] { serviceName, operation, timeoutNum }));
/*     */     }
/*     */     
/*     */ 
/* 597 */     context.setMsTimeout(timeoutNum.longValue());
/*     */   }
/*     */   
/*     */   private boolean isSendToBus(Context context)
/*     */   {
/* 602 */     return BusConnector.class.isAssignableFrom(context.getDestConnector().getClass());
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\interceptor\impl\BusinessExecutorInterceptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */