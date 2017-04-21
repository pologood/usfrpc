/*     */ package com.huawei.csc.usf.framework.invoker;
/*     */ 
/*     */ import com.huawei.csc.remoting.common.util.CastUtil;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.IMessageFactory;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.NestedReplyListener;
/*     */ import com.huawei.csc.usf.framework.ReplyMessageListener;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.pojo.PojoConnector;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceInner;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*     */ import com.huawei.csc.usf.framework.util.LogTraceUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
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
/*     */ public class CommonAsyncConnector
/*     */   extends PojoConnector
/*     */ {
/*     */   public <T> T doOnReceive(Context context)
/*     */     throws Exception
/*     */   {
/*  50 */     context.setSrcConnector(this);
/*  51 */     this.serviceEngine.onReceive(context);
/*     */     
/*  53 */     Throwable th = context.getException();
/*     */     
/*  55 */     if ((th != null) && ((th instanceof Exception)))
/*     */     {
/*  57 */       ReplyMessageListener replyListener = context.getReplyMessageListener();
/*     */       
/*  59 */       if (replyListener != null)
/*     */       {
/*  61 */         if (context.getReplyMessage() != null)
/*     */         {
/*  63 */           NestedReplyListener nestedReplyListener = new NestedReplyListener(this.serviceEngine.getInterceptorManager(), replyListener);
/*     */           
/*     */ 
/*  66 */           nestedReplyListener.onReply(context, context.getReplyMessage());
/*     */ 
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/*  73 */           IMessage reply = this.serviceEngine.getMessageFactory(context.getServiceType()).createReplyMessage(context.getReceivedMessage());
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*  78 */           reply.setPayload(th);
/*     */           
/*  80 */           NestedReplyListener nestedReplyListener = new NestedReplyListener(this.serviceEngine.getInterceptorManager(), replyListener);
/*     */           
/*     */ 
/*  83 */           nestedReplyListener.onReply(context, reply);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*  88 */       throw ((Exception)context.getException());
/*     */     }
/*  90 */     if (null != th)
/*     */     {
/*  92 */       throw new RuntimeException("Unexpected exception.", th);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 103 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   protected IMessage invocationToMessage(String protocol, String serviceName, String operation, Object[] arguments, ServiceType serviceType)
/*     */   {
/* 109 */     IMessageFactory factory = this.serviceEngine.getMessageFactory(serviceType);
/* 110 */     IMessage request = factory.createRequest(protocol, serviceName, operation);
/*     */     
/*     */ 
/* 113 */     request.setPayload(arguments);
/*     */     
/* 115 */     return request;
/*     */   }
/*     */   
/*     */   public IMessage decode(Context context)
/*     */     throws Exception
/*     */   {
/* 121 */     Object[] nativeMessage = context.getNativeInputs();
/* 122 */     String serviceName = (String)nativeMessage[0];
/* 123 */     String operation = (String)nativeMessage[1];
/* 124 */     Object[] arguments = (Object[])nativeMessage[2];
/* 125 */     ServiceInvokeAttributes attr = (ServiceInvokeAttributes)nativeMessage[3];
/* 126 */     String version = attr.getVersion();
/*     */     
/*     */ 
/* 129 */     if (0L == context.getMsTimeout())
/*     */     {
/* 131 */       int timeout = this.serviceEngine.getSystemConfig().getTimeout();
/* 132 */       context.setMsTimeout(timeout);
/*     */     }
/*     */     
/* 135 */     IMessage request = invocationToMessage(getConnectorType(), serviceName, operation, arguments, context.getServiceType());
/*     */     
/* 137 */     request.getHeaders().setGroup(attr.getGroup());
/* 138 */     if (StringUtils.isBlank(version))
/*     */     {
/* 140 */       version = null;
/*     */     }
/* 142 */     request.getHeaders().setVersion(version);
/*     */     
/* 144 */     String destAddr = attr.getDestAddr();
/* 145 */     if (!StringUtils.isEmpty(destAddr))
/*     */     {
/* 147 */       request.getHeaders().setDestAddr(destAddr);
/*     */     }
/*     */     
/* 150 */     if (context.getServiceType().equals(ServiceType.DSF))
/*     */     {
/* 152 */       request.getHeaders().setAttachValue("async", "true");
/* 153 */       String serializeType = attr.getSerialization();
/* 154 */       if (null != serializeType)
/*     */       {
/* 156 */         request.getHeaders().setAttachValue("serialization", serializeType);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 161 */     return request;
/*     */   }
/*     */   
/*     */   public <T> T encode(Context context, IMessage message)
/*     */     throws Exception
/*     */   {
/* 167 */     return (T)CastUtil.cast(message);
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */     throws Exception
/*     */   {}
/*     */   
/*     */ 
/*     */   public Collection<String> getExports()
/*     */   {
/* 178 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public List<ServiceInner> getServiceList()
/*     */   {
/* 184 */     return new ArrayList();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void loadService(Object... objects) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public String getConnectorType()
/*     */   {
/* 196 */     return "COMMONASYNC";
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isAsync()
/*     */   {
/* 202 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void init()
/*     */     throws Exception
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */   protected void doClientEnd(Context context) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public IMessage doHandle(Context context, IMessage message)
/*     */     throws Exception
/*     */   {
/* 220 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void startTraceLog(IMessage message)
/*     */   {
/* 226 */     LogTraceUtil.doStartTrace(message);
/*     */   }
/*     */   
/*     */ 
/*     */   public void asyncEndTraceLog(IMessage message)
/*     */   {
/* 232 */     LogTraceUtil.doAsyncEndTraceLog(message);
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\invoker\CommonAsyncConnector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */