/*     */ package com.huawei.csc.usf.framework.interceptor.impl;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.Connector;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.IMessageFactory;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.circuitbreaker.CommandMetrics;
/*     */ import com.huawei.csc.usf.framework.exception.ServiceDegradeException;
/*     */ import com.huawei.csc.usf.framework.interceptor.FaultInterceptor;
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
/*     */ public class FailReturnFaultInterceptor
/*     */   extends AbstractInterceptor
/*     */   implements FaultInterceptor
/*     */ {
/*  34 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(DecodeInterceptor.class);
/*     */   
/*     */ 
/*  37 */   private int faultWeight = 50;
/*     */   
/*     */   public static final String NAME = "failReturn";
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  44 */     return "failReturn";
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFaultWeight()
/*     */   {
/*  50 */     return this.faultWeight;
/*     */   }
/*     */   
/*     */   protected void doInvoke(Context context)
/*     */     throws Exception
/*     */   {
/*  56 */     if (!context.isSysnFirstNode())
/*     */     {
/*  58 */       return;
/*     */     }
/*     */     
/*  61 */     String serviceDegrade = (String)context.getAttachment("ServiceDegrade");
/*     */     
/*  63 */     String serviceName = context.getReceivedMessage().getHeaders().getServiceName();
/*     */     
/*  65 */     CommandMetrics metrics = CommandMetrics.getInstance(serviceName);
/*     */     
/*  67 */     metrics.markFallback();
/*     */     
/*  69 */     if (null == serviceDegrade)
/*     */     {
/*  71 */       metrics.markFallbackMissing();
/*  72 */       return;
/*     */     }
/*     */     
/*     */ 
/*  76 */     if (serviceDegrade.equals("FailReturn"))
/*     */     {
/*  78 */       metrics.markFallbackSuccess();
/*  79 */       handleFailReturn(context);
/*     */     }
/*  81 */     else if (serviceDegrade.equals("FalseReturnException"))
/*     */     {
/*     */ 
/*  84 */       metrics.markFallbackFailure();
/*  85 */       handleFalseReturnExeption(context);
/*     */     }
/*  87 */     else if (serviceDegrade.equals("FalseReturnNull"))
/*     */     {
/*  89 */       metrics.markFallbackSuccess();
/*  90 */       handleFalseReturnNull(context);
/*     */     }
/*     */   }
/*     */   
/*     */   public void handleFailReturn(Context context)
/*     */   {
/*  96 */     Throwable exception = context.getException();
/*     */     
/*  98 */     if (null != exception)
/*     */     {
/* 100 */       IMessage receivedMessage = context.getReceivedMessage();
/* 101 */       IMessage replyMessage = context.getSrcConnector().getServiceEngine().getMessageFactory(context.getServiceType()).createReplyMessage(receivedMessage);
/*     */       
/*     */ 
/*     */ 
/* 105 */       context.setReplyMessage(replyMessage);
/* 106 */       context.setException(null);
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 111 */       IMessage replyMessage = context.getReplyMessage();
/* 112 */       replyMessage.setException(null);
/* 113 */       context.setReplyMessage(replyMessage);
/*     */     }
/*     */   }
/*     */   
/*     */   public void handleFalseReturnExeption(Context context)
/*     */   {
/* 119 */     context.setException(null);
/* 120 */     String execptionCode = (String)context.getAttachment("serviceDegradeExceptionCode");
/*     */     
/* 122 */     ServiceDegradeException exception = new ServiceDegradeException(execptionCode, "Service degrade to return exception with given exception code: " + execptionCode);
/*     */     
/*     */ 
/*     */ 
/* 126 */     throw exception;
/*     */   }
/*     */   
/*     */   public void handleFalseReturnNull(Context context)
/*     */   {
/* 131 */     context.setException(null);
/* 132 */     ServiceEngine serviceEngine = context.getSrcConnector().getServiceEngine();
/*     */     
/* 134 */     IMessageFactory messageFactory = serviceEngine.getMessageFactory(context.getServiceType());
/*     */     
/* 136 */     IMessage replyMessage = messageFactory.createReplyMessage(context.getReceivedMessage());
/*     */     
/* 138 */     context.setReplyMessage(replyMessage);
/* 139 */     context.getReceivedMessage().setReturn(null);
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\interceptor\impl\FailReturnFaultInterceptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */