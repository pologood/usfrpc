/*     */ package com.huawei.csc.usf.framework.event;
/*     */ 
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.IMessage;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FrameworkEvent
/*     */ {
/*     */   public static final String U_FRAMEWORK_AFTER_DECODER = "usf.framework.afterDecode";
/*     */   public static final String U_FRAMEWORK_REJECT_AFTER_DECODER = "usf.framework.rejectAfterDecode";
/*     */   public static final String U_FRAMEWORK_DISPATCH_EXCEPTION = "usf.framework.doDispatchException";
/*     */   public static final String U_FRAMEWORK_ENCODE_REPLY_FINALLY = "usf.framework.encodeReplyFinally";
/*     */   public static final String U_FRAMEWORK_BEFORE_DO_SEND = "usf.framework.beforeDoSend";
/*     */   public static final String U_FRAMEWORK_AFTER_DO_SEND = "usf.framework.afterDoSend";
/*     */   public static final String U_FRAMEWORK_EXCEPTION_DO_SEND = "usf.framework.DoSendException";
/*     */   public static final String U_FRAMEWORK_FLOWCONTROL = "usf.framework.flowControl";
/*     */   public static final String U_FRAMEWORK_REMOTING_CLIENTSTATUS_BINDED = "usf.framework.remoting.clientStatus.binded";
/*     */   public static final String U_FRAMEWORK_REMOTING_CLIENTSTATUS_ERROE = "usf.framework.remoting.clientStatus.error";
/*     */   public static final String U_FRAMEWORK_SERVER_ESTABLISHED = "usf.framework.remoting.server.established";
/*     */   public static final String U_FRAMEWORK_SERVER_DISCONNECTED = "usf.framework.remoting.server.disconnected";
/*     */   public static final String U_FRAMEWORK_THREADPOOL_CROWED = "usf.framework.threadPool.crowed";
/*     */   public static final String E_ASYNC_CONNECTOR_REPLY_TIMEOUT = "ebus.async.connector.reply.timeout";
/*     */   public static final String REQUEST_MESSAGE = "requestMessage";
/*     */   public static final String REPLY_MESSAGE = "replyMessage";
/*     */   public static final String PROTOCOLCONTEXT = "protocolContext";
/*     */   public static final String ISIMPORTER = "isImporter";
/*     */   public static final String CONNECTORTYPE = "connectorType";
/*     */   public static final String ISSYNC = "isSync";
/*     */   public static final String ELAPSEDTIME = "elapsedTime";
/*     */   public static final String EXCEPTION = "exception";
/*     */   
/*     */   public static final void putRequestMessage(IMessage message, ServiceFrameworkEvent event)
/*     */   {
/* 146 */     event.put("requestMessage", message);
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
/*     */   public static final void putReplyMessage(IMessage message, ServiceFrameworkEvent event)
/*     */   {
/* 159 */     event.put("replyMessage", message);
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
/*     */   public static final void putProtocolContext(Context protocolContext, ServiceFrameworkEvent event)
/*     */   {
/* 173 */     event.put("protocolContext", protocolContext);
/*     */   }
/*     */   
/*     */   public static final void putIsImporter(Boolean isImporter, ServiceFrameworkEvent event)
/*     */   {
/* 178 */     event.put("isImporter", isImporter);
/*     */   }
/*     */   
/*     */   public static final void putConnectorType(String connectorType, ServiceFrameworkEvent event)
/*     */   {
/* 183 */     event.put("connectorType", connectorType);
/*     */   }
/*     */   
/*     */   public static final void putIsSync(Boolean isSync, ServiceFrameworkEvent event)
/*     */   {
/* 188 */     event.put("isSync", isSync);
/*     */   }
/*     */   
/*     */   public static final void putElapsedTime(Long elapsedTime, ServiceFrameworkEvent event)
/*     */   {
/* 193 */     event.put("elapsedTime", elapsedTime);
/*     */   }
/*     */   
/*     */   public static final void putException(Throwable th, ServiceFrameworkEvent event)
/*     */   {
/* 198 */     event.put("exception", th);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final ServiceFrameworkEvent creatEvent(IMessage requestMessage, String connectorType, Context protocolContext, Boolean isImporter, Boolean isSync, IMessage replyMessage, Long elapsedTime, Throwable th)
/*     */   {
/* 207 */     ServiceFrameworkEvent event = ServiceFrameworkEvent.createEvent();
/*     */     
/* 209 */     putConnectorType(connectorType, event);
/* 210 */     putElapsedTime(elapsedTime, event);
/* 211 */     putException(th, event);
/* 212 */     putIsImporter(isImporter, event);
/* 213 */     putIsSync(isSync, event);
/* 214 */     putProtocolContext(protocolContext, event);
/* 215 */     putReplyMessage(replyMessage, event);
/* 216 */     putRequestMessage(requestMessage, event);
/*     */     
/* 218 */     return event;
/*     */   }
/*     */   
/*     */   public static final IMessage getRequestMessage(ServiceFrameworkEvent event)
/*     */   {
/* 223 */     return (IMessage)event.get("requestMessage");
/*     */   }
/*     */   
/*     */   public static final IMessage getReplyMessage(ServiceFrameworkEvent event)
/*     */   {
/* 228 */     return (IMessage)event.get("replyMessage");
/*     */   }
/*     */   
/*     */   public static final Context getProtocolContext(ServiceFrameworkEvent event)
/*     */   {
/* 233 */     return (Context)event.get("protocolContext");
/*     */   }
/*     */   
/*     */   public static final Boolean isImporter(ServiceFrameworkEvent event)
/*     */   {
/* 238 */     return (Boolean)event.get("isImporter");
/*     */   }
/*     */   
/*     */   public static final String getConnectorType(ServiceFrameworkEvent event)
/*     */   {
/* 243 */     return (String)event.get("connectorType");
/*     */   }
/*     */   
/*     */   public static final Long getElapsedTime(ServiceFrameworkEvent event)
/*     */   {
/* 248 */     return (Long)event.get("elapsedTime");
/*     */   }
/*     */   
/*     */   public static final Throwable getException(ServiceFrameworkEvent event)
/*     */   {
/* 253 */     return (Throwable)event.get("exception");
/*     */   }
/*     */   
/*     */   public static final Boolean isSync(ServiceFrameworkEvent event)
/*     */   {
/* 258 */     return (Boolean)event.get("isSync");
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\event\FrameworkEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */