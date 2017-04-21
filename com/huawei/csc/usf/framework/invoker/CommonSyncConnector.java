/*     */ package com.huawei.csc.usf.framework.invoker;
/*     */ 
/*     */ import com.huawei.csc.remoting.common.util.CastUtil;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.IMessageFactory;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.pojo.PojoConnector;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceInner;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
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
/*     */ public class CommonSyncConnector
/*     */   extends PojoConnector
/*     */ {
/*     */   public IMessage decode(Context context)
/*     */     throws Exception
/*     */   {
/*  31 */     Object[] nativeMessage = context.getNativeInputs();
/*  32 */     String serviceName = (String)nativeMessage[0];
/*  33 */     String operation = (String)nativeMessage[1];
/*  34 */     Object[] arguments = (Object[])nativeMessage[2];
/*  35 */     ServiceInvokeAttributes attr = (ServiceInvokeAttributes)nativeMessage[3];
/*  36 */     String version = attr.getVersion();
/*     */     
/*     */ 
/*  39 */     if (0L == context.getMsTimeout())
/*     */     {
/*  41 */       int timeout = this.serviceEngine.getSystemConfig().getTimeout();
/*  42 */       context.setMsTimeout(timeout);
/*     */     }
/*  44 */     IMessage request = invocationToMessage(getConnectorType(), serviceName, operation, arguments, context.getServiceType());
/*     */     
/*  46 */     String group = attr.getGroup();
/*  47 */     request.getHeaders().setGroup(group);
/*  48 */     if (StringUtils.isBlank(version))
/*     */     {
/*  50 */       version = null;
/*     */     }
/*  52 */     request.getHeaders().setVersion(version);
/*  53 */     String destAddr = attr.getDestAddr();
/*  54 */     if (!StringUtils.isEmpty(destAddr))
/*     */     {
/*  56 */       request.getHeaders().setDestAddr(destAddr);
/*     */     }
/*  58 */     if (context.getServiceType().equals(ServiceType.DSF))
/*     */     {
/*  60 */       String serializeType = attr.getSerialization();
/*  61 */       if (null != serializeType)
/*     */       {
/*  63 */         request.getHeaders().setAttachValue("serialization", serializeType);
/*     */       }
/*     */     }
/*     */     
/*  67 */     return request;
/*     */   }
/*     */   
/*     */ 
/*     */   public IMessage invocationToMessage(String protocol, String serviceName, String operationName, Object[] args, ServiceType serviceType)
/*     */   {
/*  73 */     IMessageFactory factory = this.serviceEngine.getMessageFactory(serviceType);
/*  74 */     IMessage request = factory.createRequest(protocol, serviceName, operationName);
/*     */     
/*     */ 
/*  77 */     request.setPayload(args);
/*     */     
/*  79 */     return request;
/*     */   }
/*     */   
/*     */ 
/*     */   public <T> T encode(Context context, IMessage message)
/*     */     throws Exception
/*     */   {
/*  86 */     return (T)CastUtil.cast(message);
/*     */   }
/*     */   
/*     */ 
/*     */   public Collection<String> getExports()
/*     */   {
/*  92 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public List<ServiceInner> getServiceList()
/*     */   {
/*  98 */     return new ArrayList();
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
/* 110 */     return "COMMONSYNC";
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\invoker\CommonSyncConnector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */