/*     */ package com.huawei.csc.usf.framework.message;
/*     */ 
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.IMessageFactory;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
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
/*     */ public class MessageFactoryImpl
/*     */   implements IMessageFactory
/*     */ {
/*     */   private SystemConfig systemConfig;
/*     */   private ServiceType serviceType;
/*     */   
/*     */   public MessageFactoryImpl() {}
/*     */   
/*     */   public MessageFactoryImpl(SystemConfig systemConfig)
/*     */   {
/*  44 */     this.systemConfig = systemConfig;
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
/*     */   public IMessage createRequest(String protocol, String serviceName, String operationName)
/*     */   {
/*  59 */     IMessage msg = new MessageImpl();
/*  60 */     MessageHeaders header = msg.getHeaders();
/*  61 */     header.setPolicyProtocol(protocol);
/*  62 */     header.setPolicyServiceName(serviceName);
/*  63 */     header.setServiceName(serviceName);
/*  64 */     header.setPolicyOperation(operationName);
/*  65 */     header.setOperation(operationName);
/*  66 */     header.setType("request");
/*  67 */     header.setRequestId(MessageIdGenerator.nextId());
/*  68 */     header.setSrcAddr(this.systemConfig.getRPCAddress(getServiceType()));
/*     */     
/*     */ 
/*  71 */     return msg;
/*     */   }
/*     */   
/*     */ 
/*     */   public IMessage createMessage()
/*     */   {
/*  77 */     return new MessageImpl();
/*     */   }
/*     */   
/*     */ 
/*     */   public IMessage createReplyMessage(IMessage request)
/*     */   {
/*  83 */     IMessage reply = new MessageImpl();
/*  84 */     reply.getHeaders().setPolicyProtocol(request.getHeaders().getPolicyProtocol());
/*     */     
/*  86 */     reply.getHeaders().setPolicyServiceName(request.getHeaders().getPolicyServiceName());
/*     */     
/*  88 */     reply.getHeaders().setServiceName(request.getHeaders().getServiceName());
/*     */     
/*  90 */     reply.getHeaders().setPolicyOperation(request.getHeaders().getPolicyOperation());
/*     */     
/*  92 */     reply.getHeaders().setOperation(request.getHeaders().getOperation());
/*  93 */     reply.getHeaders().setType("reply");
/*  94 */     reply.getHeaders().setRequestId(request.getHeaders().getRequestId());
/*  95 */     reply.getHeaders().setSrcAddr(request.getHeaders().getDestAddr());
/*  96 */     reply.getHeaders().setDestAddr(request.getHeaders().getSrcAddr());
/*  97 */     reply.getHeaders().setGroup(request.getHeaders().getGroup());
/*     */     
/*  99 */     if (request.getHeaders().getAttachValue("async").equals("true"))
/*     */     {
/* 101 */       reply.getHeaders().setAttachValue("async", "true");
/*     */     }
/*     */     
/* 104 */     return reply;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setServiceType(ServiceType serviceType)
/*     */   {
/* 110 */     this.serviceType = serviceType;
/*     */   }
/*     */   
/*     */ 
/*     */   public ServiceType getServiceType()
/*     */   {
/* 116 */     return this.serviceType;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setSystemConfig(SystemConfig config)
/*     */   {
/* 122 */     this.systemConfig = config;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\message\MessageFactoryImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */