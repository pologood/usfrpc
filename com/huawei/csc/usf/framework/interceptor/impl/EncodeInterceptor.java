/*     */ package com.huawei.csc.usf.framework.interceptor.impl;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.Connector;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.event.FrameworkEvent;
/*     */ import com.huawei.csc.usf.framework.event.ServiceFrameworkEvent;
/*     */ import com.huawei.csc.usf.framework.event.ServiceFrameworkEventPublisher;
/*     */ import com.huawei.csc.usf.framework.interceptor.FaultInterceptor;
/*     */ import com.huawei.csc.usf.framework.interceptor.OutInterceptor;
/*     */ import com.huawei.csc.usf.framework.monitor.delay.ServiceDelayTimeCountCenter;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*     */ import com.huawei.csc.usf.framework.util.LogTraceUtil;
/*     */ import com.huawei.csc.usf.framework.util.USFContext;
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
/*     */ public class EncodeInterceptor
/*     */   extends AbstractInterceptor
/*     */   implements OutInterceptor, FaultInterceptor
/*     */ {
/*  47 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(EncodeInterceptor.class);
/*     */   
/*     */ 
/*  50 */   private int outWeight = 2000;
/*     */   
/*  52 */   private int faultWeight = 150;
/*     */   
/*     */   public static final String NAME = "encode";
/*     */   
/*     */   protected static final String START_TIME = "start";
/*     */   
/*     */   private ServiceEngine serviceEngine;
/*     */   
/*  60 */   private boolean hasEbusAdapter = false;
/*     */   
/*     */   public void setServiceEngine(ServiceEngine serviceEngine)
/*     */   {
/*  64 */     this.serviceEngine = serviceEngine;
/*     */   }
/*     */   
/*     */ 
/*     */   public void init()
/*     */   {
/*  70 */     this.hasEbusAdapter = this.serviceEngine.getSystemConfig().hasEbusAdapter();
/*     */   }
/*     */   
/*     */   public void doInvoke(Context context)
/*     */     throws Exception
/*     */   {
/*  76 */     Connector connector = context.getSrcConnector();
/*  77 */     if (skipEncodeInterceptor(context.getServiceType(), connector))
/*     */     {
/*  79 */       return;
/*     */     }
/*  81 */     Exception ex = null;
/*  82 */     IMessage replyMessage = context.getReplyMessage();
/*  83 */     if ((null != connector) && (null != replyMessage))
/*     */     {
/*     */       try
/*     */       {
/*  87 */         LogTraceUtil.updateMDCTraceOrder(replyMessage);
/*  88 */         Object replyObject = connector.encode(context, replyMessage);
/*  89 */         context.setReplyObject(replyObject);
/*  90 */         USFContext.getContext().rpcCtx2UsfCtx(replyMessage);
/*     */         
/*  92 */         if ((DEBUGGER.isDebugEnable()) && (null != replyObject))
/*     */         {
/*  94 */           DEBUGGER.debug("after encode,message headers: " + replyMessage.getHeaders().toString() + ",reply object:" + replyObject);
/*     */         }
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */         ServiceFrameworkEvent event;
/*     */         
/* 101 */         if (DEBUGGER.isErrorEnable())
/*     */         {
/* 103 */           DEBUGGER.error("Encode message error. Message is[" + context.getReplyMessage() + "]", e);
/*     */         }
/*     */         
/* 106 */         ex = e;
/* 107 */         throw e;
/*     */       }
/*     */       finally
/*     */       {
/* 111 */         if ((!ServiceFrameworkEventPublisher.isEmpty()) && (!context.isFlowControlReject()) && (!context.isServer()) && (null == context.getException()))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 116 */           ServiceFrameworkEvent event = FrameworkEvent.creatEvent(context.getReceivedMessage(), connector.getConnectorType(), context, Boolean.valueOf(true), Boolean.valueOf(!connector.isAsync()), replyMessage, Long.valueOf(System.nanoTime() - context.getStartTime()), ex);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 121 */           ServiceFrameworkEventPublisher.publish("usf.framework.encodeReplyFinally", event);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 129 */     if ((!context.isServer()) && (!context.isLocal()))
/*     */     {
/* 131 */       ServiceDelayTimeCountCenter.getInstance().putServiceDelayTime(context);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private boolean skipEncodeInterceptor(ServiceType type, Connector srcConnector)
/*     */   {
/* 138 */     if ((type.equals(ServiceType.EBUS)) && (this.hasEbusAdapter))
/*     */     {
/* 140 */       return true;
/*     */     }
/*     */     
/* 143 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 149 */     return "encode";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getOutWeight()
/*     */   {
/* 156 */     return this.outWeight;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFaultWeight()
/*     */   {
/* 162 */     return this.faultWeight;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\interceptor\impl\EncodeInterceptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */