/*     */ package com.huawei.csc.usf.framework;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.statistic.ProcessDelayTracker;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public class ReplyInterceptor
/*     */ {
/*  22 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(ReplyInterceptor.class);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  28 */   private Map<Long, Context> requestMap = new ConcurrentHashMap();
/*     */   
/*  30 */   private final AtomicLong elementCount = new AtomicLong();
/*     */   
/*     */ 
/*     */   private long maxElementCount;
/*     */   
/*     */ 
/*     */   private ServiceEngine serviceEngine;
/*     */   
/*     */ 
/*     */   public void init()
/*     */   {
/*  41 */     this.maxElementCount = this.serviceEngine.getSystemConfig().getRPCAsynWaitQueueSize();
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
/*     */   public boolean putWait(long reqID, Context ctx)
/*     */   {
/*  56 */     this.requestMap.put(Long.valueOf(reqID), ctx);
/*  57 */     if (null != ctx)
/*     */     {
/*  59 */       Connector srcConnector = ctx.getSrcConnector();
/*  60 */       Connector destConnector = ctx.getDestConnector();
/*     */       
/*  62 */       if ((null != srcConnector) && (null != destConnector) && (srcConnector.isAsync()) && (destConnector.isAsync()))
/*     */       {
/*     */ 
/*     */ 
/*  66 */         if (this.elementCount.incrementAndGet() > this.maxElementCount)
/*     */         {
/*  68 */           return false;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*  73 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Context removeWait(long reqID)
/*     */   {
/*  85 */     Context ctx = (Context)this.requestMap.remove(Long.valueOf(reqID));
/*  86 */     if (null != ctx)
/*     */     {
/*  88 */       Connector srcConnector = ctx.getSrcConnector();
/*  89 */       Connector destConnector = ctx.getDestConnector();
/*     */       
/*  91 */       if ((null != srcConnector) && (null != destConnector) && (srcConnector.isAsync()) && (destConnector.isAsync()))
/*     */       {
/*     */ 
/*  94 */         this.elementCount.decrementAndGet();
/*     */       }
/*     */     }
/*     */     
/*  98 */     return ctx;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Context getContext(long reqID)
/*     */   {
/* 109 */     return (Context)this.requestMap.get(Long.valueOf(reqID));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void onMessage(IMessage reply, Context context)
/*     */     throws Exception
/*     */   {
/* 117 */     ProcessDelayTracker repleyTracker = ProcessDelayTracker.next(context.getProcessDelayTracker(), "reply.notify");
/*     */     
/*     */ 
/* 120 */     long reqID = reply.getHeaders().getRequestId();
/*     */     
/*     */ 
/* 123 */     Context ctx = removeWait(reqID);
/*     */     try
/*     */     {
/* 126 */       if (null == ctx) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 136 */       if (!ctx.getSrcConnector().isAsync())
/*     */       {
/*     */ 
/* 139 */         ctx.resume(reply);
/*     */         
/*     */ 
/* 142 */         doNotify(reqID, ctx);
/*     */       }
/*     */       else
/*     */       {
/* 146 */         ctx.setReplyMessage(reply);
/* 147 */         if (reply.isFault())
/*     */         {
/* 149 */           if ((reply.getPayload() instanceof Throwable))
/*     */           {
/* 151 */             context.setException((Throwable)reply.getPayload());
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 156 */         NestedReplyListener nestedReplyListener = new NestedReplyListener(this.serviceEngine.getInterceptorManager(), ctx.getReplyMessageListener());
/*     */         
/*     */ 
/*     */ 
/* 160 */         nestedReplyListener.onReply(ctx, reply);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 165 */       ProcessDelayTracker.done(repleyTracker);
/*     */       
/*     */ 
/* 168 */       if ((null != ctx) && (ctx.getSrcConnector().isAsync()))
/*     */       {
/*     */ 
/* 171 */         ctx.done();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void doNotify(long reqID, Context ctx)
/*     */   {
/* 178 */     if (!ctx.getSrcConnector().isAsync())
/*     */     {
/* 180 */       synchronized (ctx)
/*     */       {
/* 182 */         if (ctx.getReplyMessage() == null)
/*     */         {
/* 184 */           if (LOGGER.isInfoEnable())
/*     */           {
/* 186 */             LOGGER.info("The reply message is null, request id:" + reqID);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 192 */         ctx.notifyAll();
/*     */       }
/* 194 */       return;
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
/*     */   public void setServiceEngine(ServiceEngine serviceEngine)
/*     */   {
/* 214 */     this.serviceEngine = serviceEngine;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ServiceEngine getServiceEngine()
/*     */   {
/* 226 */     return this.serviceEngine;
/*     */   }
/*     */   
/*     */   public Map<Long, Context> getRequestMap()
/*     */   {
/* 231 */     return this.requestMap;
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
/*     */   public void onFaultMessage(long reqID)
/*     */     throws Exception
/*     */   {
/* 252 */     Context ctx = removeWait(reqID);
/* 253 */     if (null == ctx)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 260 */       return;
/*     */     }
/* 262 */     ProcessDelayTracker repleyTracker = ProcessDelayTracker.next(ctx.getProcessDelayTracker(), "reply.notify");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 270 */       Exception notReachableException = ExceptionUtilsHolder.getExceptionUtils(ctx.getServiceType()).remoteNotReachable(ctx.getReceivedMessage().getHeaders().getDestAddr());
/*     */       
/*     */ 
/*     */ 
/* 274 */       ctx.setException(notReachableException);
/*     */       
/* 276 */       if (!ctx.getSrcConnector().isAsync())
/*     */       {
/* 278 */         ctx.resume(null);
/*     */         
/*     */ 
/* 281 */         doNotify(reqID, ctx);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 286 */         NestedReplyListener nestedReplyListener = new NestedReplyListener(this.serviceEngine.getInterceptorManager(), ctx.getReplyMessageListener());
/*     */         
/*     */ 
/* 289 */         ServiceEngine serviceEngine = ctx.getSrcConnector().getServiceEngine();
/*     */         
/* 291 */         IMessage reply = serviceEngine.getMessageFactory(ctx.getServiceType()).createReplyMessage(ctx.getReceivedMessage());
/*     */         
/*     */ 
/* 294 */         nestedReplyListener.onReply(ctx, reply);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 299 */       ProcessDelayTracker.done(repleyTracker);
/*     */       
/*     */ 
/* 302 */       if (ctx.getSrcConnector().isAsync())
/*     */       {
/*     */ 
/* 305 */         ctx.done();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public AtomicLong getElementCount()
/*     */   {
/* 312 */     return this.elementCount;
/*     */   }
/*     */   
/*     */   public long getMaxElementCount()
/*     */   {
/* 317 */     return this.maxElementCount;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\ReplyInterceptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */