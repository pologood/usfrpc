/*     */ package com.huawei.csc.usf.framework.bind;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.encryption.Encryption;
/*     */ import com.huawei.csc.kernel.api.encryption.EncryptionFactory;
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.remoting.common.connection.InternalMessagingService;
/*     */ import com.huawei.csc.remoting.common.exception.RemotingException;
/*     */ import com.huawei.csc.remoting.common.impl.ResponseFuture;
/*     */ import com.huawei.csc.remoting.handler.BindResponseMessage;
/*     */ import com.huawei.csc.remoting.handler.OpeartionStatus;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.message.MessageImpl;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import org.apache.commons.lang.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MultiAuthenticationHandlerImpl
/*     */   implements AuthenticationHandler
/*     */ {
/*  29 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(SimpleAuthenticationHandlerImpl.class);
/*     */   
/*     */ 
/*  32 */   private SystemConfig sysConfig = null;
/*     */   
/*  34 */   private Map<String, byte[]> hashCache = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void init(Object resource)
/*     */   {
/*  43 */     this.sysConfig = ((SystemConfig)resource);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object authentication(InternalMessagingService internalMessagingService, Object ext)
/*     */     throws Exception
/*     */   {
/*  51 */     byte[] hashValue = requestForHashValue(internalMessagingService);
/*  52 */     if (null == hashValue)
/*     */     {
/*     */ 
/*  55 */       throw new Exception("cann't get hash value from server");
/*     */     }
/*     */     
/*  58 */     if (hashValue.length != 32)
/*     */     {
/*     */ 
/*  61 */       throw new Exception("hash bytes length is " + hashValue.length + ", not equals 32");
/*     */     }
/*     */     
/*     */ 
/*  65 */     String rpcloginIdentity = this.sysConfig.getRpcLoginIdentity();
/*  66 */     if (StringUtils.isEmpty(rpcloginIdentity))
/*     */     {
/*     */ 
/*  69 */       throw new Exception("rpc login identity is null or empty");
/*     */     }
/*     */     
/*  72 */     String identity = generatePBKDF2(rpcloginIdentity, hashValue);
/*  73 */     if (StringUtils.isEmpty(identity))
/*     */     {
/*     */ 
/*  76 */       throw new Exception("gen PBKDF2 failed on client side");
/*     */     }
/*     */     
/*  79 */     String result = null;
/*     */     try
/*     */     {
/*  82 */       result = doBind(internalMessagingService, identity);
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  87 */       throw new Exception("cann't get loggin result from server");
/*     */     }
/*     */     
/*  90 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public Object processAuthentication(Object request, Object ext)
/*     */     throws Exception
/*     */   {
/*  97 */     boolean result = false;
/*  98 */     boolean isOldVersion = false;
/*  99 */     BindResponseMessage bindResponseMessage = new BindResponseMessage(OpeartionStatus.BINDING);
/*     */     
/* 101 */     IMessage responseMessage = new MessageImpl();
/* 102 */     responseMessage.getHeaders().setType("bindResponse");
/* 103 */     bindResponseMessage.setMessage(responseMessage);
/* 104 */     if ((null == request) || (null == ext))
/*     */     {
/* 106 */       if (LOGGER.isErrorEnable())
/*     */       {
/* 108 */         LOGGER.error("Parameter is null, request message: " + request + ", ext: " + ext);
/*     */       }
/*     */       
/* 111 */       responseMessage.setPayload("BIND_FAILED");
/* 112 */       bindResponseMessage.setStatus(OpeartionStatus.FAILED);
/* 113 */       return bindResponseMessage;
/*     */     }
/*     */     
/* 116 */     IMessage requestMessage = (IMessage)request;
/* 117 */     String version = requestMessage.getHeaders().getAttachValue("bindVersion");
/*     */     
/* 119 */     if (StringUtils.isEmpty(version))
/*     */     {
/* 121 */       isOldVersion = true;
/*     */     }
/* 123 */     if ((isOldVersion) || (false == version.equals("bindVersionV2")))
/*     */     {
/*     */ 
/* 126 */       if (LOGGER.isErrorEnable())
/*     */       {
/* 128 */         LOGGER.error("bind policy mismatch");
/*     */       }
/* 130 */       responseMessage.setPayload("BIND_POLICY_MISMATCH");
/* 131 */       bindResponseMessage.setStatus(OpeartionStatus.FAILED);
/* 132 */       if (isOldVersion)
/*     */       {
/* 134 */         setVersionKey(responseMessage, "bindVersionV1");
/*     */       }
/* 136 */       return bindResponseMessage;
/*     */     }
/*     */     
/* 139 */     String key = (String)ext;
/* 140 */     String clientLoginIdentity = (String)requestMessage.getPayload();
/* 141 */     if (!StringUtils.isEmpty(clientLoginIdentity))
/*     */     {
/* 143 */       if (clientLoginIdentity.equals("bindRequestHashValue"))
/*     */       {
/*     */ 
/* 146 */         byte[] hashValue = doGetHashKey();
/* 147 */         responseMessage.setPayload(hashValue);
/* 148 */         this.hashCache.put(key, hashValue);
/*     */       }
/*     */       else
/*     */       {
/* 152 */         result = doProcessBind(key, clientLoginIdentity, this.sysConfig);
/* 153 */         if (false == result)
/*     */         {
/* 155 */           bindResponseMessage.setStatus(OpeartionStatus.FAILED);
/* 156 */           responseMessage.setPayload("BIND_FAILED");
/*     */         }
/*     */         else
/*     */         {
/* 160 */           bindResponseMessage.setStatus(OpeartionStatus.SUCCESS);
/* 161 */           responseMessage.setPayload("BIND_SUCCESS");
/*     */         }
/* 163 */         if (this.hashCache.containsKey(key))
/*     */         {
/* 165 */           this.hashCache.remove(key);
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 171 */       if (LOGGER.isErrorEnable())
/*     */       {
/* 173 */         LOGGER.error("client loggin identity is null or empty");
/*     */       }
/* 175 */       bindResponseMessage.setStatus(OpeartionStatus.FAILED);
/* 176 */       responseMessage.setPayload("BIND_FAILED");
/*     */     }
/*     */     
/* 179 */     return bindResponseMessage;
/*     */   }
/*     */   
/*     */   private byte[] doGetHashKey()
/*     */   {
/* 184 */     SecureRandom random = new SecureRandom();
/* 185 */     byte[] bytes = new byte[32];
/* 186 */     random.nextBytes(bytes);
/* 187 */     return bytes;
/*     */   }
/*     */   
/*     */ 
/*     */   private boolean doProcessBind(String hashKey, String identity, SystemConfig sysConfig)
/*     */   {
/* 193 */     String loginIdentity = null;
/* 194 */     if (!StringUtils.isEmpty(identity))
/*     */     {
/* 196 */       byte[] hashValue = null;
/* 197 */       if (this.hashCache.containsKey(hashKey))
/*     */       {
/* 199 */         hashValue = (byte[])this.hashCache.get(hashKey);
/*     */       }
/*     */       else
/*     */       {
/* 203 */         if (LOGGER.isErrorEnable())
/*     */         {
/* 205 */           LOGGER.error("can not get hash value from cache, hash key: " + hashKey);
/*     */         }
/*     */         
/* 208 */         return false;
/*     */       }
/*     */       
/* 211 */       String serverLoginIdentity = sysConfig.getRpcLoginIdentity();
/* 212 */       if (StringUtils.isEmpty(serverLoginIdentity))
/*     */       {
/* 214 */         if (LOGGER.isErrorEnable())
/*     */         {
/* 216 */           LOGGER.error("server rpc login identity is null or empty");
/*     */         }
/* 218 */         return false;
/*     */       }
/*     */       
/* 221 */       loginIdentity = generatePBKDF2(serverLoginIdentity, hashValue);
/* 222 */       return loginIdentity.equals(identity);
/*     */     }
/* 224 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   private String doBind(InternalMessagingService internalMessagingService, String identity)
/*     */     throws RemotingException, InterruptedException, ExecutionException
/*     */   {
/* 231 */     String result = null;
/* 232 */     IMessage message = new MessageImpl();
/* 233 */     message.getHeaders().setType("bindRequest");
/* 234 */     message.getHeaders().setAttachValue("bindVersion", "bindVersionV2");
/*     */     
/* 236 */     message.setPayload(identity);
/*     */     try
/*     */     {
/* 239 */       ResponseFuture send = internalMessagingService.send(message);
/* 240 */       Object bindResponse = send.get();
/* 241 */       if (null != bindResponse)
/*     */       {
/* 243 */         IMessage bindResponseMsg = (IMessage)bindResponse;
/* 244 */         result = (String)bindResponseMsg.getPayload();
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 249 */       if (LOGGER.isErrorEnable())
/*     */       {
/* 251 */         LOGGER.error("do bind failed, " + e.getMessage());
/*     */       }
/*     */     }
/*     */     
/* 255 */     return result;
/*     */   }
/*     */   
/*     */   private String generatePBKDF2(String rpcIdentity, byte[] hashvalue)
/*     */   {
/* 260 */     String loginIdentity = null;
/* 261 */     String plaintext = null;
/*     */     try
/*     */     {
/* 264 */       plaintext = EncryptionFactory.getEncyption().decode(rpcIdentity);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 268 */       if (LOGGER.isErrorEnable())
/*     */       {
/* 270 */         LOGGER.error("decyption rpc identity failed, rpc identity: " + rpcIdentity);
/*     */       }
/*     */       
/* 273 */       return null;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 278 */       loginIdentity = EncryptionFactory.getEncyption().genPBKDF2(plaintext, hashvalue, 10000);
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 283 */       if (LOGGER.isErrorEnable())
/*     */       {
/* 285 */         LOGGER.error("gen PBKDF2 failed");
/*     */       }
/* 287 */       return null;
/*     */     }
/*     */     
/* 290 */     return loginIdentity;
/*     */   }
/*     */   
/*     */   private byte[] requestForHashValue(InternalMessagingService internalMessagingService)
/*     */     throws Exception
/*     */   {
/* 296 */     byte[] hashValue = null;
/* 297 */     IMessage message = new MessageImpl();
/* 298 */     message.getHeaders().setType("bindRequest");
/* 299 */     message.setPayload("bindRequestHashValue");
/* 300 */     message.getHeaders().setAttachValue("bindVersion", "bindVersionV2");
/*     */     
/*     */     try
/*     */     {
/* 304 */       ResponseFuture send = internalMessagingService.send(message);
/* 305 */       Object bindResponse = send.get();
/* 306 */       if (null != bindResponse)
/*     */       {
/* 308 */         IMessage bindResponseMsg = (IMessage)bindResponse;
/* 309 */         Object payload = bindResponseMsg.getPayload();
/* 310 */         if (null != payload)
/*     */         {
/* 312 */           if ((payload instanceof String))
/*     */           {
/* 314 */             String result = (String)payload;
/* 315 */             if (result.equals("BIND_POLICY_MISMATCH"))
/*     */             {
/* 317 */               throw new Exception("bind policy mismatch");
/*     */             }
/*     */           }
/*     */           
/* 321 */           if ((payload instanceof byte[]))
/*     */           {
/* 323 */             return (byte[])payload;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 330 */       if (LOGGER.isErrorEnable())
/*     */       {
/* 332 */         LOGGER.error("get hash value failed, " + e.getMessage());
/*     */       }
/*     */     }
/*     */     
/* 336 */     return hashValue;
/*     */   }
/*     */   
/*     */   private void setVersionKey(IMessage msg, String version)
/*     */   {
/* 341 */     msg.getHeaders().setAttachValue("bindVersion", version);
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\bind\MultiAuthenticationHandlerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */