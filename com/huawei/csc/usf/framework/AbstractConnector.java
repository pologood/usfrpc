/*     */ package com.huawei.csc.usf.framework;
/*     */ 
/*     */ import com.huawei.csc.usf.framework.bus.MsgSerializerFactory;
/*     */ import com.huawei.csc.usf.framework.config.USFConfig;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*     */ import com.huawei.csc.usf.framework.statistic.ProcessDelayTracker;
/*     */ import com.huawei.csc.usf.framework.trace.ContextHolder;
/*     */ import com.huawei.csc.usf.framework.trace.ITraceLinkInitializer;
/*     */ import com.huawei.csc.usf.framework.trace.TraceLinkInfo;
/*     */ import com.huawei.csc.usf.framework.trace.TraceLinkInitializerFactory;
/*     */ import com.huawei.csc.usf.framework.trace.TraceLinkUtilHelper;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import org.apache.commons.lang.StringUtils;
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
/*     */ public abstract class AbstractConnector
/*     */   implements Connector
/*     */ {
/*     */   protected String serviceType;
/*     */   protected ServiceEngine serviceEngine;
/*  55 */   protected AtomicLong numInProcess = new AtomicLong(0L);
/*     */   
/*     */ 
/*     */   public void setServiceEngine(ServiceEngine serviceEngine)
/*     */   {
/*  60 */     this.serviceEngine = serviceEngine;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ServiceEngine getServiceEngine()
/*     */   {
/*  67 */     return this.serviceEngine;
/*     */   }
/*     */   
/*     */   public <T> T onReceive(Context context)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/*  75 */       this.numInProcess.incrementAndGet();
/*     */       
/*  77 */       if (this.serviceEngine == null)
/*     */       {
/*  79 */         throw new RuntimeException("connector not inited.");
/*     */       }
/*     */       
/*     */ 
/*  83 */       if (this.serviceEngine.isDestroyed())
/*     */       {
/*  85 */         if (!context.isServer())
/*     */         {
/*  87 */           throw ExceptionUtilsHolder.getExceptionUtils(context.getServiceType()).clientMessageRefused("reject request because of client shutdown");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*  93 */         throw ExceptionUtilsHolder.getExceptionUtils(context.getServiceType()).serverMessageRefused("reject request because of server shutdown");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*  98 */       doDecode(context);
/*  99 */       doHeadBegin(context);
/* 100 */       doClientBegin(context);
/* 101 */       T reply = doOnReceive(context);
/* 102 */       return reply;
/*     */     }
/*     */     finally
/*     */     {
/* 106 */       doClientEnd(context);
/* 107 */       doHeadEnd(context);
/* 108 */       this.numInProcess.decrementAndGet();
/*     */     }
/*     */   }
/*     */   
/*     */   public IMessage handle(Context context, IMessage message)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 117 */       this.numInProcess.incrementAndGet();
/* 118 */       doServerBegin(context);
/* 119 */       IMessage replyMessage = doHandle(context, message);
/* 120 */       if (null != replyMessage)
/*     */       {
/* 122 */         context.setReplyMessage(replyMessage);
/*     */       }
/* 124 */       return replyMessage;
/*     */     }
/*     */     finally
/*     */     {
/* 128 */       doServerEnd(context);
/* 129 */       this.numInProcess.decrementAndGet();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isReadyToDestroy()
/*     */   {
/* 136 */     return this.numInProcess.get() <= 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract <T> T doOnReceive(Context paramContext)
/*     */     throws Exception;
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract IMessage doHandle(Context paramContext, IMessage paramIMessage)
/*     */     throws Exception;
/*     */   
/*     */ 
/*     */   protected void doDecode(Context context)
/*     */     throws Exception
/*     */   {
/* 153 */     context.setSrcConnector(this);
/* 154 */     IMessage message = null;
/* 155 */     Object[] nativeInputs = context.getNativeInputs();
/* 156 */     if ((null != nativeInputs) && (null != this))
/*     */     {
/* 158 */       message = decode(context);
/* 159 */       if ((!context.isServer()) && (context.getServiceType().equals(ServiceType.DSF)))
/*     */       {
/*     */ 
/* 162 */         String serializeType = message.getHeaders().getAttachValue("serialization");
/*     */         
/* 164 */         if (StringUtils.isBlank(serializeType))
/*     */         {
/* 166 */           serializeType = this.serviceEngine.getSerializerFactory().getConfigSerializeType();
/*     */         }
/*     */         
/* 169 */         byte serializeTag = this.serviceEngine.getSerializerFactory().getSerializeTag(serializeType);
/*     */         
/* 171 */         message.getHeaders().setAttachValue("serialization", serializeTag + "");
/*     */       }
/*     */       
/*     */ 
/* 175 */       context.setReceivedMessage(message);
/*     */       
/* 177 */       ProcessDelayTracker tracker = context.getProcessDelayTracker();
/* 178 */       if (null != tracker)
/*     */       {
/* 180 */         MessageHeaders headers = message.getHeaders();
/* 181 */         tracker.setMsgId(String.valueOf(headers.getRequestId()));
/* 182 */         tracker.setOperation(headers.getOperation());
/* 183 */         tracker.setServiceName(headers.getServiceName());
/*     */       }
/*     */       
/* 186 */       startTraceLog(message);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void doHeadBegin(Context context)
/*     */   {
/* 198 */     if (!USFConfig.isTraceSwitch())
/*     */     {
/* 200 */       return;
/*     */     }
/*     */     
/* 203 */     if (null == context.getReceivedMessage())
/*     */     {
/* 205 */       return;
/*     */     }
/* 207 */     ContextHolder contextHolder = new ContextHolder(context.getReceivedMessage().getHeaders());
/*     */     
/* 209 */     ITraceLinkInitializer traceLinkInitializer = TraceLinkInitializerFactory.getTraceLinkInitializer();
/*     */     
/* 211 */     if (null != traceLinkInitializer)
/*     */     {
/* 213 */       traceLinkInitializer.initTraceContext(contextHolder);
/*     */     }
/* 215 */     TraceLinkInfo traceLinkInfo = TraceLinkUtilHelper.headServerRec(contextHolder);
/*     */     
/* 217 */     context.addAttachment("traceLinkInfo", traceLinkInfo);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void doHeadEnd(Context context)
/*     */   {
/* 228 */     if (!USFConfig.isTraceSwitch())
/*     */     {
/* 230 */       return;
/*     */     }
/* 232 */     Object traceLinkInfoObject = context.getAttachment("traceLinkInfo");
/* 233 */     if (null != traceLinkInfoObject)
/*     */     {
/* 235 */       TraceLinkInfo traceLinkInfo = (TraceLinkInfo)traceLinkInfoObject;
/* 236 */       TraceLinkUtilHelper.headServerSend(traceLinkInfo, context);
/*     */     }
/*     */   }
/*     */   
/*     */   protected abstract void doClientBegin(Context paramContext);
/*     */   
/*     */   protected abstract void doClientEnd(Context paramContext);
/*     */   
/*     */   protected abstract void doServerBegin(Context paramContext);
/*     */   
/*     */   protected abstract void doServerEnd(Context paramContext);
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\AbstractConnector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */