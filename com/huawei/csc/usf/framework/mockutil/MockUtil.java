/*     */ package com.huawei.csc.usf.framework.mockutil;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.ObjectMapper;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.ExceptionUtils;
/*     */ import com.huawei.csc.usf.framework.ExceptionUtilsHolder;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.ServiceDefinition;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.pojo.PojoClientInner;
/*     */ import com.huawei.csc.usf.framework.pojo.PojoConnector;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ public class MockUtil
/*     */ {
/*  19 */   private static final Map<String, String> mockCache = new ConcurrentHashMap();
/*     */   
/*  21 */   private static final Map<String, Class<?>> returnTypeCache = new ConcurrentHashMap();
/*     */   
/*  23 */   private static final DebugLog DEBUGGER = com.huawei.csc.kernel.api.log.LogFactory.getDebugLog(MockUtil.class);
/*     */   
/*     */ 
/*     */ 
/*     */   public static void setMockValue(String service, String operation, String mockValue)
/*     */   {
/*  29 */     if (null == service)
/*     */     {
/*  31 */       DEBUGGER.error("The service can not be null.");
/*  32 */       return;
/*     */     }
/*     */     
/*  35 */     if (null == operation)
/*     */     {
/*  37 */       DEBUGGER.error("The operation can not be null.");
/*  38 */       return;
/*     */     }
/*     */     
/*  41 */     if (null == mockValue)
/*     */     {
/*  43 */       DEBUGGER.error("The mockValue can not be null.");
/*  44 */       return;
/*     */     }
/*     */     
/*  47 */     String key = makeKey(service, operation);
/*  48 */     mockCache.put(key, mockValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void setMockValueWithReturnType(String service, String operation, String mockValue, Class<?> returntype)
/*     */   {
/*  55 */     if (null == service)
/*     */     {
/*  57 */       DEBUGGER.error("The service can not be null.");
/*  58 */       return;
/*     */     }
/*     */     
/*  61 */     if (null == operation)
/*     */     {
/*  63 */       DEBUGGER.error("The operation can not be null.");
/*  64 */       return;
/*     */     }
/*     */     
/*  67 */     if (null == mockValue)
/*     */     {
/*  69 */       DEBUGGER.error("The mockValue can not be null.");
/*  70 */       return;
/*     */     }
/*     */     
/*     */ 
/*  74 */     if (null == returntype)
/*     */     {
/*  76 */       DEBUGGER.error("The return type can not be null");
/*     */     }
/*  78 */     String key = makeKey(service, operation);
/*  79 */     mockCache.put(key, mockValue);
/*  80 */     returnTypeCache.put(key, returntype);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void unsetMock(String service, String operation)
/*     */   {
/*  86 */     if (null == service)
/*     */     {
/*  88 */       DEBUGGER.error("The service can not be null.");
/*  89 */       return;
/*     */     }
/*     */     
/*  92 */     if (null == operation)
/*     */     {
/*  94 */       DEBUGGER.error("The operation can not be null.");
/*  95 */       return;
/*     */     }
/*  97 */     mockCache.remove(makeKey(service, operation));
/*  98 */     returnTypeCache.remove(makeKey(service, operation));
/*     */   }
/*     */   
/*     */   public static void mock(Context context) throws Exception
/*     */   {
/* 103 */     String service = context.getReceivedMessage().getHeaders().getServiceName();
/*     */     
/* 105 */     String operation = context.getReceivedMessage().getHeaders().getOperation();
/*     */     
/*     */ 
/* 108 */     String mockValue = (String)mockCache.get(makeKey(service, operation));
/* 109 */     if (null == mockValue)
/* 110 */       return;
/* 111 */     ObjectMapper objectMapper = new ObjectMapper();
/*     */     
/* 113 */     Class<?> returnType = (Class)returnTypeCache.get(makeKey(service, operation));
/* 114 */     if (returnType != null)
/*     */     {
/* 116 */       if (("void".equals(returnType.getName())) || ("java.lang.Void".equals(returnType.getName())))
/*     */       {
/*     */ 
/*     */ 
/* 120 */         context.setBroken(true);
/* 121 */         return;
/*     */       }
/* 123 */       Object object = null;
/*     */       try
/*     */       {
/* 126 */         object = objectMapper.readValue(mockValue, returnType);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 130 */         if (DEBUGGER.isErrorEnable())
/*     */         {
/* 132 */           DEBUGGER.error("The mock " + service + " deserialized, exception:" + e);
/*     */         }
/*     */         
/* 135 */         throw ExceptionUtilsHolder.getExceptionUtils(context.getServiceType()).mockDeserializeFailed();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 140 */       context.getReceivedMessage().setReturn(object);
/* 141 */       context.setReplyMessage(context.getReceivedMessage());
/* 142 */       context.setBroken(true);
/* 143 */       return;
/*     */     }
/*     */     
/*     */ 
/* 147 */     PojoConnector srcConnector = (PojoConnector)context.getSrcConnector();
/* 148 */     ServiceEngine engine = srcConnector.getServiceEngine();
/* 149 */     Map<String, ServiceDefinition> map = engine.getClientServiceDefinition();
/*     */     
/*     */ 
/* 152 */     PojoClientInner pojoClientInner = (PojoClientInner)srcConnector.getClientMapper().get("&" + ((ServiceDefinition)map.get(service)).getBeanName());
/*     */     
/* 154 */     if (null == pojoClientInner)
/* 155 */       return;
/* 156 */     Class<?> clazz = pojoClientInner.getServiceInterface();
/*     */     
/* 158 */     Method[] methods = clazz.getMethods();
/*     */     
/* 160 */     for (Method method : methods)
/*     */     {
/* 162 */       if (operation.equals(method.getName()))
/*     */       {
/* 164 */         Class<?> returntype = method.getReturnType();
/*     */         
/* 166 */         if (("void".equals(returntype.getName())) || ("java.lang.Void".equals(returntype.getName())))
/*     */         {
/*     */ 
/*     */ 
/* 170 */           context.setBroken(true);
/* 171 */           return;
/*     */         }
/* 173 */         Object object = null;
/*     */         try
/*     */         {
/* 176 */           object = objectMapper.readValue(mockValue, returntype);
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 180 */           if (DEBUGGER.isErrorEnable())
/*     */           {
/* 182 */             DEBUGGER.error("The mock " + service + " deserialized, exception:" + e);
/*     */           }
/*     */           
/* 185 */           throw ExceptionUtilsHolder.getExceptionUtils(context.getServiceType()).mockDeserializeFailed();
/*     */         }
/*     */         
/* 188 */         context.getReceivedMessage().setReturn(object);
/* 189 */         context.setReplyMessage(context.getReceivedMessage());
/* 190 */         context.setBroken(true);
/*     */       }
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
/*     */   private static String makeKey(String service, String method)
/*     */   {
/* 205 */     return service + "|" + method + "|" + "C$C";
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\mockutil\MockUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */