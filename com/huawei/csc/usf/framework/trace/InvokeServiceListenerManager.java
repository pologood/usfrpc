/*     */ package com.huawei.csc.usf.framework.trace;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.util.USFCtxObject;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class InvokeServiceListenerManager
/*     */ {
/*  14 */   private static final DebugLog DEBUG_LOGGER = LogFactory.getDebugLog(InvokeServiceListenerManager.class);
/*     */   
/*     */ 
/*  17 */   private static List<InvokeServiceListener> listeners = new ArrayList();
/*     */   
/*     */ 
/*     */   public static ServiceContext newServiceContext(String service, String operation)
/*     */   {
/*  22 */     if (true == listeners.isEmpty())
/*     */     {
/*  24 */       DEBUG_LOGGER.debug("no invoke service listener.");
/*  25 */       return null;
/*     */     }
/*     */     
/*  28 */     return new ServiceContext(service, operation);
/*     */   }
/*     */   
/*     */   public static void addListener(InvokeServiceListener listener)
/*     */   {
/*  33 */     listeners.add(listener);
/*     */   }
/*     */   
/*     */   public static void removeListener(InvokeServiceListener listener)
/*     */   {
/*  38 */     listeners.remove(listener);
/*     */   }
/*     */   
/*     */   public static void invokeOnBegin(ServiceContext context)
/*     */   {
/*  43 */     if (null == context)
/*     */     {
/*  45 */       DEBUG_LOGGER.debug("service context is null.");
/*  46 */       return;
/*     */     }
/*     */     
/*  49 */     for (InvokeServiceListener listener : listeners)
/*     */     {
/*  51 */       listener.onBegin(context);
/*  52 */       if (DEBUG_LOGGER.isDebugEnable())
/*     */       {
/*  54 */         DEBUG_LOGGER.debug("invoked on begin, listener: " + listener);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void invokeOnEnd(ServiceContext context)
/*     */   {
/*  61 */     if (null == context)
/*     */     {
/*  63 */       DEBUG_LOGGER.debug("service context is null.");
/*  64 */       return;
/*     */     }
/*     */     
/*  67 */     for (InvokeServiceListener listener : listeners)
/*     */     {
/*  69 */       listener.onEnd(context);
/*  70 */       if (DEBUG_LOGGER.isDebugEnable())
/*     */       {
/*  72 */         DEBUG_LOGGER.debug("invoked on end, listener: " + listener);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void invokeOnException(ServiceContext context, Throwable exception)
/*     */   {
/*  80 */     if (null == context)
/*     */     {
/*  82 */       DEBUG_LOGGER.debug("service context is null.");
/*  83 */       return;
/*     */     }
/*     */     
/*  86 */     context.setException(exception);
/*     */     
/*  88 */     for (InvokeServiceListener listener : listeners)
/*     */     {
/*  90 */       listener.onException(context);
/*  91 */       if (DEBUG_LOGGER.isDebugEnable())
/*     */       {
/*  93 */         DEBUG_LOGGER.debug("invoked on exception, listener: " + listener);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void handleInvokeResult(ServiceContext context, IMessage message)
/*     */   {
/* 102 */     if (null == context)
/*     */     {
/* 104 */       DEBUG_LOGGER.debug("service context is null.");
/* 105 */       return;
/*     */     }
/*     */     
/* 108 */     if (null == message)
/*     */     {
/* 110 */       DEBUG_LOGGER.debug("message is null.");
/* 111 */       return;
/*     */     }
/*     */     
/* 114 */     Map<String, Object> attrs = context.getAttributes();
/* 115 */     message.getHeaders().getAttachment().put("attrs_map", new USFCtxObject(attrs));
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\trace\InvokeServiceListenerManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */