/*     */ package com.huawei.csc.usf.framework.bind;
/*     */ 
/*     */ import com.huawei.csc.container.api.ContextRegistry;
/*     */ import com.huawei.csc.container.api.IContextHolder;
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.remoting.client.ClientConfig;
/*     */ import com.huawei.csc.remoting.common.connection.InternalMessagingService;
/*     */ import com.huawei.csc.remoting.common.exception.BindException;
/*     */ import com.huawei.csc.remoting.common.exception.UnBindException;
/*     */ import com.huawei.csc.remoting.handler.BindHandler;
/*     */ import com.huawei.csc.remoting.handler.BindResponseMessage;
/*     */ import com.huawei.csc.remoting.handler.OpeartionStatus;
/*     */ import com.huawei.csc.remoting.handler.UnBindResponseMessage;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.message.MessageImpl;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BindHandlerImpl
/*     */   implements BindHandler
/*     */ {
/*  28 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(BindHandlerImpl.class);
/*     */   
/*     */ 
/*  31 */   private SystemConfig sysConfig = null;
/*     */   
/*  33 */   private AuthenticationHandler authenticationHandler = null;
/*     */   
/*     */   public BindHandlerImpl(SystemConfig config)
/*     */   {
/*  37 */     this.sysConfig = config;
/*     */     
/*     */     try
/*     */     {
/*  41 */       Map<String, AuthenticationHandler> beanMap = ContextRegistry.getContextHolder().getBeansOfType(AuthenticationHandler.class);
/*     */       
/*     */ 
/*     */ 
/*  45 */       for (Map.Entry<String, AuthenticationHandler> element : beanMap.entrySet())
/*     */       {
/*     */ 
/*  48 */         if (((String)element.getKey()).contains("dsf"))
/*     */         {
/*  50 */           if (LOGGER.isInfoEnable())
/*     */           {
/*  52 */             LOGGER.info("Customed dsf authentication handler, bean id: " + (String)element.getKey());
/*     */           }
/*     */           
/*  55 */           this.authenticationHandler = ((AuthenticationHandler)element.getValue());
/*  56 */           break;
/*     */         }
/*     */       }
/*     */       
/*  60 */       if (null == this.authenticationHandler)
/*     */       {
/*  62 */         if (LOGGER.isInfoEnable())
/*     */         {
/*  64 */           LOGGER.info("no customed authentication handler, use simple authentication handler");
/*     */         }
/*  66 */         this.authenticationHandler = new SimpleAuthenticationHandlerImpl();
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  71 */       if (LOGGER.isInfoEnable())
/*     */       {
/*  73 */         LOGGER.info("Exception occured while get authentication handler, use simple authentication handler");
/*     */       }
/*  75 */       this.authenticationHandler = new SimpleAuthenticationHandlerImpl();
/*     */     }
/*     */     
/*  78 */     this.authenticationHandler.init(this.sysConfig);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void bind(ClientConfig clientConfig, InternalMessagingService internalMessagingService)
/*     */     throws BindException
/*     */   {
/*     */     try
/*     */     {
/*  88 */       Object obj = this.authenticationHandler.authentication(internalMessagingService, null);
/*     */       
/*  90 */       if (null == obj)
/*     */       {
/*  92 */         throw new BindException("can not get loggin result from server");
/*     */       }
/*     */       
/*  95 */       String result = (String)obj;
/*  96 */       if (result.equals("BIND_POLICY_MISMATCH"))
/*     */       {
/*  98 */         throw new BindException("loggin policy mismatch");
/*     */       }
/* 100 */       if (false == result.equals("BIND_SUCCESS"))
/*     */       {
/* 102 */         throw new BindException("loggin identity mismatch");
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 107 */       if (LOGGER.isErrorEnable())
/*     */       {
/* 109 */         LOGGER.error("Client loggin failed: " + e.getMessage());
/*     */       }
/* 111 */       throw new BindException("Client loggin failed");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void unBind(ClientConfig clientConfig, InternalMessagingService internalMessagingService)
/*     */     throws UnBindException
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */   public BindResponseMessage processBindMessage(Object request)
/*     */   {
/* 126 */     BindResponseMessage bindResponseMessage = null;
/*     */     try
/*     */     {
/* 129 */       Object[] requestPara = (Object[])request;
/* 130 */       bindResponseMessage = (BindResponseMessage)this.authenticationHandler.processAuthentication(requestPara[0], requestPara[1]);
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 135 */       if (LOGGER.isErrorEnable())
/*     */       {
/* 137 */         LOGGER.error("Exception occured while loggin: " + e.getMessage());
/*     */       }
/*     */       
/* 140 */       bindResponseMessage = new BindResponseMessage(OpeartionStatus.FAILED);
/*     */       
/* 142 */       IMessage message = new MessageImpl();
/* 143 */       message.getHeaders().setType("bindResponse");
/* 144 */       message.setPayload("BIND_FAILED");
/*     */       
/* 146 */       bindResponseMessage.setMessage(message);
/*     */     }
/* 148 */     return bindResponseMessage;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public UnBindResponseMessage processUnBindMessage(Object request)
/*     */   {
/* 155 */     return null;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\bind\BindHandlerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */