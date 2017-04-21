/*     */ package com.huawei.csc.usf.framework.invoker;
/*     */ 
/*     */ import com.huawei.csc.container.api.ContextRegistry;
/*     */ import com.huawei.csc.container.api.IContextHolder;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.ReplyMessageListener;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
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
/*     */ public class CommonInvokerImpl
/*     */ {
/*     */   private static final String DEFAULT_GROUP_NAME = "default";
/*     */   private static final String DEFAULT_REGID = "dsf_default";
/*     */   
/*     */   public static IMessage invokeMessage(String group, String serviceName, String operation, Object[] arguments, long msTimeout, ServiceType serviceType)
/*     */     throws Exception
/*     */   {
/*  38 */     ServiceInvokeAttributes attr = new ServiceInvokeAttributes();
/*  39 */     attr.setGroup(group);
/*  40 */     attr.setTimeout(msTimeout);
/*  41 */     return invokeMessage(serviceName, operation, arguments, attr, serviceType);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static IMessage invokeMessage(String group, String serviceName, String version, String operation, Object[] arguments, long msTimeout, ServiceType serviceType)
/*     */     throws Exception
/*     */   {
/*  68 */     ServiceInvokeAttributes attr = new ServiceInvokeAttributes();
/*  69 */     attr.setGroup(group);
/*  70 */     attr.setTimeout(msTimeout);
/*  71 */     attr.setVersion(version);
/*  72 */     return invokeMessage(serviceName, operation, arguments, attr, serviceType);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static IMessage invokeMessage(String group, String serviceName, String operation, Object[] arguments, long msTimeout, String address, ServiceType serviceType)
/*     */     throws Exception
/*     */   {
/*  99 */     ServiceInvokeAttributes attr = new ServiceInvokeAttributes();
/* 100 */     attr.setGroup(group);
/* 101 */     attr.setTimeout(msTimeout);
/* 102 */     attr.setDestAddr(address);
/* 103 */     return invokeMessage(serviceName, operation, arguments, attr, serviceType);
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
/*     */ 
/*     */ 
/*     */   public static IMessage invokeMessage(String serviceName, String operation, Object[] arguments, ServiceInvokeAttributes attr, ServiceType serviceType)
/*     */     throws Exception
/*     */   {
/* 126 */     if (null == attr)
/*     */     {
/* 128 */       attr = new ServiceInvokeAttributes();
/*     */     }
/* 130 */     String group = attr.getGroup();
/* 131 */     String registerId = attr.getRegistry();
/* 132 */     if (StringUtils.isEmpty(group))
/*     */     {
/* 134 */       group = "default";
/* 135 */       attr.setGroup(group);
/*     */     }
/* 137 */     if (StringUtils.isEmpty(registerId))
/*     */     {
/* 139 */       registerId = "dsf_default";
/* 140 */       attr.setRegistry(registerId);
/*     */     }
/* 142 */     Context context = new Context(new Object[] { serviceName, operation, arguments, attr });
/* 143 */     context.setServiceType(serviceType);
/* 144 */     context.setSysnFirstNode(true);
/* 145 */     context.setRegistry(registerId);
/* 146 */     context.setMsTimeout(attr.getTimeout());
/*     */     try
/*     */     {
/* 149 */       return (IMessage)getSyncConnector().onReceive(context);
/*     */     }
/*     */     finally
/*     */     {
/* 153 */       context.done();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void sendMessage(String group, String serviceName, String operation, Object[] arguments, ReplyMessageListener replyListener, long msTimeout, ServiceType serviceType)
/*     */     throws Exception
/*     */   {
/* 180 */     ServiceInvokeAttributes attr = new ServiceInvokeAttributes();
/* 181 */     attr.setGroup(group);
/* 182 */     attr.setTimeout(msTimeout);
/* 183 */     sendMessage(serviceName, operation, arguments, replyListener, attr, serviceType);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void sendMessage(String group, String serviceName, String version, String operation, Object[] arguments, ReplyMessageListener replyListener, long msTimeout, ServiceType serviceType)
/*     */     throws Exception
/*     */   {
/* 212 */     ServiceInvokeAttributes attr = new ServiceInvokeAttributes();
/* 213 */     attr.setGroup(group);
/* 214 */     attr.setTimeout(msTimeout);
/* 215 */     attr.setVersion(version);
/* 216 */     sendMessage(serviceName, operation, arguments, replyListener, attr, serviceType);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void sendMessage(String group, String serviceName, String operation, Object[] arguments, ReplyMessageListener replyListener, long msTimeout, String destAddr, ServiceType serviceType)
/*     */     throws Exception
/*     */   {
/* 245 */     ServiceInvokeAttributes attr = new ServiceInvokeAttributes();
/* 246 */     attr.setGroup(group);
/* 247 */     attr.setTimeout(msTimeout);
/* 248 */     attr.setDestAddr(destAddr);
/* 249 */     sendMessage(serviceName, operation, arguments, replyListener, attr, serviceType);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void sendMessage(String group, String serviceName, String operation, Object[] arguments, ReplyMessageListener replyListener, ServiceType serviceType)
/*     */     throws Exception
/*     */   {
/* 274 */     ServiceInvokeAttributes attr = new ServiceInvokeAttributes();
/* 275 */     attr.setGroup(group);
/* 276 */     sendMessage(serviceName, operation, arguments, replyListener, attr, serviceType);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void sendMessage(String group, String serviceName, String operation, Object[] arguments, ReplyMessageListener replyListener, String destAddr, ServiceType serviceType)
/*     */     throws Exception
/*     */   {
/* 303 */     ServiceInvokeAttributes attr = new ServiceInvokeAttributes();
/* 304 */     attr.setGroup(group);
/* 305 */     attr.setDestAddr(destAddr);
/* 306 */     sendMessage(serviceName, operation, arguments, replyListener, attr, serviceType);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void sendMessage(String serviceName, String operation, Object[] arguments, ReplyMessageListener replyListener, ServiceInvokeAttributes attr, ServiceType serviceType)
/*     */     throws Exception
/*     */   {
/* 332 */     String registerId = attr.getRegistry();
/* 333 */     if (StringUtils.isEmpty(attr.getGroup()))
/*     */     {
/* 335 */       attr.setGroup("default");
/*     */     }
/* 337 */     if (StringUtils.isEmpty(registerId))
/*     */     {
/* 339 */       registerId = "dsf_default";
/* 340 */       attr.setRegistry("dsf_default");
/*     */     }
/*     */     
/* 343 */     Context context = new Context(new Object[] { serviceName, operation, arguments, attr });
/* 344 */     context.setServiceType(serviceType);
/* 345 */     context.setMsTimeout(attr.getTimeout());
/* 346 */     context.setRegistry(registerId);
/* 347 */     context.setReplyMessageListener(replyListener);
/*     */     try
/*     */     {
/* 350 */       getAsyncConnector(serviceType).onReceive(context);
/*     */     }
/*     */     finally
/*     */     {
/* 354 */       context.done();
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
/*     */ 
/*     */   public static void sendMessage(String group, String serviceName, String operation, Object[] arguments, ServiceType serviceType)
/*     */     throws Exception
/*     */   {
/* 376 */     ServiceInvokeAttributes attr = new ServiceInvokeAttributes();
/* 377 */     attr.setGroup(group);
/* 378 */     sendMessage(serviceName, operation, arguments, attr, serviceType);
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
/*     */ 
/*     */ 
/*     */   public static void sendMessage(String group, String serviceName, String operation, Object[] arguments, String destAddr, ServiceType serviceType)
/*     */     throws Exception
/*     */   {
/* 401 */     ServiceInvokeAttributes attr = new ServiceInvokeAttributes();
/* 402 */     attr.setGroup(group);
/* 403 */     attr.setDestAddr(destAddr);
/* 404 */     sendMessage(serviceName, operation, arguments, attr, serviceType);
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
/*     */   public static void sendMessage(String serviceName, String operation, Object[] arguments, ServiceInvokeAttributes attr, ServiceType serviceType)
/*     */     throws Exception
/*     */   {
/* 425 */     if (null == attr)
/*     */     {
/* 427 */       attr = new ServiceInvokeAttributes();
/*     */     }
/* 429 */     if (StringUtils.isEmpty(attr.getGroup()))
/*     */     {
/* 431 */       attr.setGroup("default");
/*     */     }
/* 433 */     Context context = new Context(new Object[] { serviceName, operation, arguments, attr });
/* 434 */     context.setServiceType(serviceType);
/*     */     try
/*     */     {
/* 437 */       getAsyncConnector(serviceType).onReceive(context);
/*     */     }
/*     */     finally
/*     */     {
/* 441 */       context.done();
/*     */     }
/*     */   }
/*     */   
/*     */   private static CommonSyncConnector getSyncConnector()
/*     */   {
/* 447 */     return (CommonSyncConnector)ContextRegistry.getContextHolder().getBean("usfCommonSyncConnector");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static CommonAsyncConnector getAsyncConnector(ServiceType serviceType)
/*     */   {
/* 454 */     if (ServiceType.EBUS.equals(serviceType))
/*     */     {
/* 456 */       return (CommonAsyncConnector)ContextRegistry.getContextHolder().getBean("ebusCommonAsyncConnector");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 461 */     return (CommonAsyncConnector)ContextRegistry.getContextHolder().getBean("usfCommonAsyncConnector");
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\invoker\CommonInvokerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */