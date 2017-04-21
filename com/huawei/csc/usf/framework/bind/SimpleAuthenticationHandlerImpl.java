/*     */ package com.huawei.csc.usf.framework.bind;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.encryption.Encryption;
/*     */ import com.huawei.csc.kernel.api.encryption.EncryptionFactory;
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.kernel.commons.encryption.EncryptionEnum.EncodeType;
/*     */ import com.huawei.csc.remoting.common.connection.InternalMessagingService;
/*     */ import com.huawei.csc.remoting.common.impl.ResponseFuture;
/*     */ import com.huawei.csc.remoting.handler.BindResponseMessage;
/*     */ import com.huawei.csc.remoting.handler.OpeartionStatus;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.message.MessageImpl;
/*     */ import org.apache.commons.lang.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SimpleAuthenticationHandlerImpl
/*     */   implements AuthenticationHandler
/*     */ {
/*  24 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(SimpleAuthenticationHandlerImpl.class);
/*     */   
/*     */ 
/*  27 */   private SystemConfig sysConfig = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void init(Object resource)
/*     */   {
/*  36 */     this.sysConfig = ((SystemConfig)resource);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object authentication(InternalMessagingService internalMessagingService, Object ext)
/*     */     throws Exception
/*     */   {
/*  44 */     Object result = null;
/*  45 */     IMessage message = new MessageImpl();
/*  46 */     message.getHeaders().setType("bindRequest");
/*  47 */     String bindObject = this.sysConfig.getClientLogginPassword();
/*  48 */     if (StringUtils.isEmpty(bindObject))
/*     */     {
/*  50 */       throw new Exception("client loggin identity is null or empty");
/*     */     }
/*  52 */     message.setPayload(bindObject);
/*  53 */     message.getHeaders().setAttachValue("bindVersion", "bindVersionV1");
/*     */     
/*     */     try
/*     */     {
/*  57 */       ResponseFuture send = internalMessagingService.send(message);
/*  58 */       Object bindResponse = send.get();
/*  59 */       if (null != bindResponse)
/*     */       {
/*  61 */         IMessage bindResponseMsg = (IMessage)bindResponse;
/*  62 */         result = bindResponseMsg.getPayload();
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  67 */       throw new Exception("exception occured while client loggin, " + e.getMessage());
/*     */     }
/*     */     
/*  70 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public Object processAuthentication(Object request, Object ext)
/*     */     throws Exception
/*     */   {
/*  77 */     boolean result = false;
/*  78 */     boolean isOldVersion = false;
/*     */     
/*  80 */     BindResponseMessage bindResponseMessage = new BindResponseMessage(OpeartionStatus.BINDING);
/*     */     
/*  82 */     if (null != request)
/*     */     {
/*  84 */       IMessage requestMsg = (IMessage)request;
/*  85 */       String bindVersion = requestMsg.getHeaders().getAttachValue("bindVersion");
/*     */       
/*  87 */       if (StringUtils.isEmpty(bindVersion))
/*     */       {
/*  89 */         isOldVersion = true;
/*     */       }
/*  91 */       else if (false == bindVersion.equals("bindVersionV1"))
/*     */       {
/*  93 */         if (LOGGER.isErrorEnable())
/*     */         {
/*  95 */           LOGGER.error("Bind policy mismatch");
/*     */         }
/*  97 */         IMessage message = new MessageImpl();
/*  98 */         message.getHeaders().setType("bindResponse");
/*  99 */         message.setPayload("BIND_POLICY_MISMATCH");
/* 100 */         bindResponseMessage.setMessage(message);
/* 101 */         bindResponseMessage.setStatus(OpeartionStatus.FAILED);
/* 102 */         return bindResponseMessage;
/*     */       }
/*     */       
/*     */       try
/*     */       {
/* 107 */         result = doProcessAuthentication(requestMsg.getPayload());
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 111 */         if (LOGGER.isErrorEnable())
/*     */         {
/* 113 */           LOGGER.error("Exception occured while Server Loggin:　" + e.getMessage());
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/*     */     }
/* 120 */     else if (LOGGER.isErrorEnable())
/*     */     {
/* 122 */       LOGGER.error("Bind request message is null");
/*     */     }
/*     */     
/*     */ 
/* 126 */     IMessage message = new MessageImpl();
/* 127 */     if (result)
/*     */     {
/* 129 */       message.getHeaders().setType("bindResponse");
/* 130 */       message.setPayload("BIND_SUCCESS");
/* 131 */       bindResponseMessage.setMessage(message);
/* 132 */       bindResponseMessage.setStatus(OpeartionStatus.SUCCESS);
/*     */     }
/*     */     else
/*     */     {
/* 136 */       message.getHeaders().setType("bindResponse");
/* 137 */       message.setPayload("BIND_FAILED");
/* 138 */       bindResponseMessage.setMessage(message);
/* 139 */       bindResponseMessage.setStatus(OpeartionStatus.FAILED);
/*     */     }
/*     */     
/* 142 */     if (isOldVersion)
/*     */     {
/* 144 */       message.getHeaders().setAttachValue("bindVersion", "bindVersionV1");
/*     */     }
/*     */     
/*     */ 
/* 148 */     return bindResponseMessage;
/*     */   }
/*     */   
/*     */   private boolean doProcessAuthentication(Object indentity)
/*     */   {
/* 153 */     if (null == indentity)
/*     */     {
/* 155 */       if (LOGGER.isErrorEnable())
/*     */       {
/* 157 */         LOGGER.error("Client loggin identity is null or empty");
/*     */       }
/* 159 */       return false;
/*     */     }
/*     */     
/* 162 */     String serverLoginPassword = this.sysConfig.getServerLogginPassword();
/* 163 */     if (StringUtils.isEmpty(serverLoginPassword))
/*     */     {
/* 165 */       if (LOGGER.isErrorEnable())
/*     */       {
/* 167 */         LOGGER.error("Server loggin identity is null or empty");
/*     */       }
/* 169 */       return false;
/*     */     }
/* 171 */     String plaintext = "";
/* 172 */     String ciphertext = "";
/*     */     
/*     */     try
/*     */     {
/* 176 */       plaintext = EncryptionFactory.getEncyption().decode(serverLoginPassword);
/*     */       
/* 178 */       ciphertext = EncryptionFactory.getEncyption().encode(plaintext, EncryptionEnum.EncodeType.noReversible);
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 183 */       if (LOGGER.isErrorEnable())
/*     */       {
/* 185 */         LOGGER.error("encyption failed on server side: " + e.getMessage());
/*     */       }
/*     */       
/* 188 */       return false;
/*     */     }
/*     */     
/* 191 */     if (ciphertext.equals(indentity))
/*     */     {
/* 193 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 197 */     if (LOGGER.isWarnEnable())
/*     */     {
/* 199 */       LOGGER.warn("loggin identity mismatch");
/*     */     }
/*     */     
/* 202 */     return false;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\bind\SimpleAuthenticationHandlerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */