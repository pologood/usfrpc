/*     */ package com.huawei.csc.usf.framework.invoker;
/*     */ 
/*     */ import com.huawei.csc.remoting.common.util.CastUtil;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.ReplyMessageListener;
/*     */ import com.huawei.csc.usf.framework.exception.USFException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CommonInvoker
/*     */ {
/*     */   private static final String DEFAULT_GROUP_NAME = "default";
/*     */   
/*     */   public static <T> T invoke(String serviceName, String operation, Object[] arguments)
/*     */     throws Exception
/*     */   {
/*  41 */     return (T)CastUtil.cast(invoke(null, serviceName, operation, arguments));
/*     */   }
/*     */   
/*     */   public static <T> T invoke(String serviceName, String operation, Object[] arguments, String address)
/*     */     throws Exception
/*     */   {
/*  47 */     return (T)CastUtil.cast(invoke(null, serviceName, operation, arguments, address));
/*     */   }
/*     */   
/*     */   public static <T> T invoke(String group, String serviceName, String operation, Object[] arguments)
/*     */     throws Exception
/*     */   {
/*  53 */     return (T)CastUtil.cast(invoke(group, serviceName, operation, arguments, null));
/*     */   }
/*     */   
/*     */ 
/*     */   public static <T> T invoke(String group, String serviceName, String operation, Object[] arguments, String address)
/*     */     throws Exception
/*     */   {
/*  60 */     return (T)CastUtil.cast(invokeCommon(group, serviceName, operation, arguments, address));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static <T> T invokeCommon(String group, String serviceName, String operation, Object[] arguments, String address)
/*     */     throws Exception
/*     */   {
/*  68 */     if ((StringUtils.isEmpty(serviceName)) || (StringUtils.isEmpty(operation)))
/*     */     {
/*  70 */       throw new USFException("405180802", new Object[] { serviceName, operation });
/*     */     }
/*     */     
/*     */ 
/*  74 */     if (StringUtils.isEmpty(group))
/*     */     {
/*  76 */       group = "default";
/*     */     }
/*     */     
/*  79 */     IMessage reply = null;
/*  80 */     if (StringUtils.isEmpty(address))
/*     */     {
/*  82 */       reply = invokeMessage(group, serviceName, operation, arguments, 0L);
/*     */     }
/*     */     else
/*     */     {
/*  86 */       reply = invokeMessage(group, serviceName, operation, arguments, 0L, address);
/*     */     }
/*     */     
/*  89 */     return (T)CastUtil.cast(reply.getPayload());
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
/*     */   public static IMessage invokeMessage(String serviceName, String operation, Object[] arguments, long msTimeout)
/*     */     throws Exception
/*     */   {
/* 110 */     return invokeMessage(null, serviceName, operation, arguments, msTimeout);
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
/*     */   public static IMessage invokeMessage(String serviceName, String operation, Object[] arguments, long msTimeout, String address)
/*     */     throws Exception
/*     */   {
/* 134 */     return invokeMessage(null, serviceName, operation, arguments, msTimeout, address);
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
/*     */   public static IMessage invokeMessage(String group, String serviceName, String operation, Object[] arguments, long msTimeout)
/*     */     throws Exception
/*     */   {
/* 159 */     return CommonInvokerImpl.invokeMessage(group, serviceName, operation, arguments, msTimeout, ServiceType.USF);
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
/*     */   public static IMessage invokeMessage(String group, String serviceName, String operation, Object[] arguments, long msTimeout, String address)
/*     */     throws Exception
/*     */   {
/* 186 */     return CommonInvokerImpl.invokeMessage(group, serviceName, operation, arguments, msTimeout, address, ServiceType.USF);
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
/*     */   public static void sendMessage(String serviceName, String operation, Object[] arguments, ReplyMessageListener replyListener, long msTimeout)
/*     */     throws Exception
/*     */   {
/* 210 */     sendMessage(null, serviceName, operation, arguments, replyListener, msTimeout);
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
/*     */   public static void sendMessage(String group, String serviceName, String operation, Object[] arguments, ReplyMessageListener replyListener, long msTimeout)
/*     */     throws Exception
/*     */   {
/* 237 */     CommonInvokerImpl.sendMessage(group, serviceName, operation, arguments, replyListener, msTimeout, ServiceType.USF);
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
/*     */   public static void sendMessage(String group, String serviceName, String operation, Object[] arguments, ReplyMessageListener replyListener, long msTimeout, String destAddr)
/*     */     throws Exception
/*     */   {
/* 266 */     CommonInvokerImpl.sendMessage(group, serviceName, operation, arguments, replyListener, msTimeout, destAddr, ServiceType.USF);
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
/*     */   public static void sendMessage(String group, String serviceName, String operation, Object[] arguments, ReplyMessageListener replyListener)
/*     */     throws Exception
/*     */   {
/* 290 */     CommonInvokerImpl.sendMessage(group, serviceName, operation, arguments, replyListener, ServiceType.USF);
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
/*     */   public static void sendMessage(String group, String serviceName, String operation, Object[] arguments, ReplyMessageListener replyListener, String destAddr)
/*     */     throws Exception
/*     */   {
/* 317 */     CommonInvokerImpl.sendMessage(group, serviceName, operation, arguments, replyListener, destAddr, ServiceType.USF);
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
/*     */   public static void sendMessage(String group, String serviceName, String operation, Object[] arguments)
/*     */     throws Exception
/*     */   {
/* 338 */     CommonInvokerImpl.sendMessage(group, serviceName, operation, arguments, ServiceType.USF);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void sendMessage(String serviceName, String operation, Object[] arguments, ServiceType serviceType)
/*     */     throws Exception
/*     */   {
/* 345 */     CommonInvokerImpl.sendMessage(null, serviceName, operation, arguments, serviceType);
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
/*     */   public static void sendMessage(String group, String serviceName, String operation, Object[] arguments, String destAddr)
/*     */     throws Exception
/*     */   {
/* 369 */     CommonInvokerImpl.sendMessage(group, serviceName, operation, arguments, destAddr, ServiceType.USF);
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\invoker\CommonInvoker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */