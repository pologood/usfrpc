/*     */ package com.huawei.csc.usf.framework.sr;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import org.apache.commons.lang.StringUtils;
/*     */ import org.springframework.beans.BeanUtils;
/*     */ 
/*     */ 
/*     */ public abstract class ZkRegistryAdapter
/*     */ {
/*  19 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(ZkRegistryAdapter.class);
/*     */   
/*     */ 
/*     */   protected ServiceRegistryAgent sr;
/*     */   
/*     */   private static final String SERVICE = "/metadata/";
/*     */   
/*     */   protected SystemConfig config;
/*     */   
/*  28 */   private MessageDigest md = null;
/*     */   
/*     */   private static final String OLDEBUS_SERVICE_NAMESPACE = "/ebus/service/";
/*     */   
/*     */   private static final String OLDEBUS_INSTANCE_NAMESPACE = "/ebus/instances/";
/*     */   
/*     */   private static final char OLDEBUS_CONTENT_DELIMITER = '#';
/*     */   
/*  36 */   protected Map<String, String> zooKeeperRootDirs = new HashMap();
/*     */   
/*     */   public SystemConfig getSystemConfig()
/*     */   {
/*  40 */     return this.config;
/*     */   }
/*     */   
/*     */   public void setSystemConfig(SystemConfig config)
/*     */   {
/*  45 */     this.config = config;
/*     */   }
/*     */   
/*     */   public void setZookeeperRootDir(String regId, String root)
/*     */   {
/*  50 */     this.zooKeeperRootDirs.put(regId, root);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void init(ServiceRegistryAgent sr)
/*     */   {
/*  58 */     this.sr = sr;
/*     */   }
/*     */   
/*     */   public String getOldEbusRegistryServiceKey(ServiceInstanceInner instance)
/*     */   {
/*  63 */     StringBuilder sb = new StringBuilder();
/*  64 */     sb.append("/ebus/instances/");
/*  65 */     sb.append(instance.getInstanceName());
/*  66 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public String getOldEbusParentKey(ServiceInstanceInner instance)
/*     */   {
/*  71 */     return "/ebus/instances/";
/*     */   }
/*     */   
/*     */   public String getOldEbusRegistryKey(ServiceInstanceInner instance)
/*     */   {
/*  76 */     StringBuilder sb = new StringBuilder();
/*  77 */     sb.append("/ebus/instances/");
/*  78 */     sb.append(instance.getInstanceName());
/*  79 */     sb.append("/");
/*  80 */     sb.append(instance.getGroup());
/*  81 */     sb.append('#');
/*  82 */     sb.append(instance.getVersion());
/*  83 */     sb.append('#');
/*  84 */     sb.append(instance.getInstanceName());
/*  85 */     sb.append('#');
/*  86 */     sb.append(instance.getAddress());
/*  87 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public void registerProviderInstance(ServiceInstanceInner instance)
/*     */   {
/*  92 */     String key = getProviderRegistryKey(instance);
/*  93 */     if (key == null)
/*     */     {
/*  95 */       return;
/*     */     }
/*  97 */     this.sr.getRegistry().registerEphemeral(key, null, false);
/*  98 */     if (this.config.getSyncToOldEbus())
/*     */     {
/* 100 */       if (instance.getServiceType().equals(ServiceType.DSF.toString()))
/*     */       {
/* 102 */         ServiceInstanceInner ebusInstance = new ServiceInstanceInner();
/* 103 */         BeanUtils.copyProperties(instance, ebusInstance);
/* 104 */         ebusInstance.setAddress(this.config.getRPCAddress(ServiceType.EBUS));
/*     */         
/* 106 */         String dataInstance = toOldEbusInstanceInner(ebusInstance);
/*     */         try
/*     */         {
/* 109 */           byte[] data = dataInstance.getBytes("UTF-8");
/* 110 */           String serviceKey = getOldEbusRegistryServiceKey(ebusInstance);
/* 111 */           String parentKey = getOldEbusParentKey(ebusInstance);
/* 112 */           key = getOldEbusRegistryKey(ebusInstance);
/* 113 */           this.sr.getRegistry().register(parentKey, null, false);
/* 114 */           if (serviceKey != null)
/*     */           {
/* 116 */             this.sr.getRegistry().register(serviceKey, null, false);
/*     */           }
/*     */           
/* 119 */           this.sr.getRegistry().registerEphemeral(key, data, false);
/*     */         }
/*     */         catch (UnsupportedEncodingException e)
/*     */         {
/* 123 */           LOGGER.error("failed to convert metaData to byte[].", e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void registerConsumerInstance(ConsumerInstanceInner instance)
/*     */   {
/* 131 */     String key = getConsumerRegistryKey(instance);
/* 132 */     if (key == null)
/*     */     {
/* 134 */       return;
/*     */     }
/* 136 */     this.sr.getRegistry().registerEphemeral(key, null, true);
/*     */   }
/*     */   
/*     */   public void deregisterInstance(ServiceInstanceInner instance)
/*     */   {
/* 141 */     String key = getProviderRegistryKey(instance);
/* 142 */     if (key == null)
/*     */     {
/* 144 */       return;
/*     */     }
/* 146 */     this.sr.getRegistry().unregister(key);
/* 147 */     if (!instance.getServiceType().equals(ServiceType.USF.toString()))
/*     */     {
/* 149 */       String sdlKey = getSdlRegistryKey(instance);
/* 150 */       if (sdlKey == null)
/*     */       {
/* 152 */         return;
/*     */       }
/* 154 */       this.sr.getRegistry().unregister(sdlKey);
/*     */     }
/* 156 */     if (this.config.getSyncToOldEbus())
/*     */     {
/* 158 */       if (instance.getServiceType().equals(ServiceType.DSF.toString()))
/*     */       {
/* 160 */         ServiceInstanceInner ebusInstance = new ServiceInstanceInner();
/* 161 */         BeanUtils.copyProperties(instance, ebusInstance);
/* 162 */         ebusInstance.setAddress(this.config.getRPCAddress(ServiceType.EBUS));
/* 163 */         key = getOldEbusRegistryKey(ebusInstance);
/* 164 */         this.sr.getRegistry().unregister(key);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void deregisterConsumerInstance(ConsumerInstanceInner instance)
/*     */   {
/* 171 */     String key = getConsumerRegistryKey(instance);
/* 172 */     if (key == null)
/*     */     {
/* 174 */       return;
/*     */     }
/* 176 */     this.sr.getRegistry().unregister(key);
/*     */   }
/*     */   
/*     */   public void registerSdl(ServiceInner service, String path)
/*     */   {
/* 181 */     String instanceName = null;
/* 182 */     if (service.getInstances().size() > 0)
/*     */     {
/* 184 */       instanceName = ((ServiceInstanceInner)service.getInstances().get(0)).getInstanceName();
/*     */     }
/*     */     
/* 187 */     Properties props = toProperties(service);
/*     */     try
/*     */     {
/* 190 */       this.md = MessageDigest.getInstance("MD5");
/*     */     }
/*     */     catch (NoSuchAlgorithmException e)
/*     */     {
/* 194 */       LOGGER.error("get MessageDigest failed", e);
/*     */     }
/* 196 */     byte[] thedigest = this.md.digest(props.toString().getBytes());
/* 197 */     String checkSum = byteArrayToCheckSum(thedigest);
/* 198 */     String version = service.getVersion();
/* 199 */     checkSum = checkSum + "#" + version;
/* 200 */     String key = path + instanceName + "/metadata/";
/* 201 */     byte[] data = serializeForSR(props);
/* 202 */     if (null != data)
/* 203 */       this.sr.getRegistry().register(key, data, true, checkSum, version);
/* 204 */     if (this.config.getSyncToOldEbus())
/*     */     {
/* 206 */       if (service.getServiceType().equals(ServiceType.DSF.toString()))
/*     */       {
/* 208 */         String metaData = toOldEbusMetaData(service);
/*     */         try
/*     */         {
/* 211 */           data = metaData.getBytes("UTF-8");
/* 212 */           key = "/ebus/service/" + service.getName();
/* 213 */           this.sr.getRegistry().register(key, data, true);
/*     */         }
/*     */         catch (UnsupportedEncodingException e)
/*     */         {
/* 217 */           LOGGER.error("failed to convert metaData to byte[].", e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public Properties toProperties(ServiceInner inner)
/*     */   {
/* 225 */     Properties props = new Properties();
/*     */     
/* 227 */     if (!StringUtils.isEmpty(inner.getName()))
/*     */     {
/* 229 */       props.put("interface", inner.getName());
/*     */     }
/*     */     
/* 232 */     if (!StringUtils.isEmpty(inner.getSdl()))
/*     */     {
/* 234 */       props.put("sdl", inner.getSdl());
/*     */     }
/*     */     
/* 237 */     if (!StringUtils.isEmpty(inner.getServiceType()))
/*     */     {
/* 239 */       props.put("serviceType", inner.getServiceType());
/*     */     }
/* 241 */     if (!StringUtils.isEmpty(inner.getDsf()))
/*     */     {
/* 243 */       props.put("dsf", inner.getDsf());
/*     */     }
/* 245 */     return props;
/*     */   }
/*     */   
/*     */   public String toOldEbusMetaData(ServiceInner inner)
/*     */   {
/* 250 */     StringBuilder metaData = new StringBuilder();
/* 251 */     metaData.append("{\"_defClass\":\"");
/* 252 */     metaData.append(inner.getClass().getName());
/* 253 */     metaData.append("\",\"name\":\"").append(inner.getName());
/* 254 */     metaData.append("\",\"instances\":{\"_defClass\":\"");
/* 255 */     metaData.append(inner.getInstances().getClass().getName());
/* 256 */     metaData.append("\",\"$list\":[]},\"attrs\":{\"_defClass\":\"");
/* 257 */     metaData.append(inner.getAttrs().getClass().getName());
/* 258 */     metaData.append("\",\"$list\":[]},\"methods\":{\"_defClass\":\"");
/* 259 */     metaData.append(inner.getMethods().getClass().getName());
/* 260 */     metaData.append("\",\"$list\":[]},\"type\":");
/* 261 */     if (null != inner.getType())
/*     */     {
/* 263 */       metaData.append("\"").append(inner.getType()).append("\",");
/*     */     }
/*     */     else
/*     */     {
/* 267 */       metaData.append("null,");
/*     */     }
/* 269 */     if (null != inner.getVersion())
/*     */     {
/* 271 */       metaData.append("\"version\":\"").append(inner.getVersion()).append("\",");
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 276 */       metaData.append("\"version\":null,");
/*     */     }
/* 278 */     if (null != inner.getGroup())
/*     */     {
/* 280 */       metaData.append("\"group\":\"").append(inner.getGroup()).append("\",");
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 285 */       metaData.append("\"group\":null,");
/*     */     }
/* 287 */     if (null != inner.getDsf())
/*     */     {
/* 289 */       metaData.append("\"dsf\":\"").append(inner.getDsf()).append("\",");
/*     */     }
/*     */     else
/*     */     {
/* 293 */       metaData.append("\"dsf\":null,");
/*     */     }
/* 295 */     metaData.append("\"immutable\":").append(inner.isImmutable()).append(",");
/*     */     
/* 297 */     if (null != inner.getSdl())
/*     */     {
/* 299 */       metaData.append("\"sdl\":\"").append(inner.getSdl().replace("\r", "\\r").replace("\n", "\\n")).append("\",");
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 305 */       metaData.append("\"sdl\":null,");
/*     */     }
/* 307 */     if (null != inner.getServiceType())
/*     */     {
/* 309 */       metaData.append("\"serviceType\":\"").append(inner.getServiceType()).append("\"}");
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 314 */       metaData.append("\"serviceType\":null}");
/*     */     }
/* 316 */     return metaData.toString();
/*     */   }
/*     */   
/*     */   public String toOldEbusInstanceInner(ServiceInstanceInner inner)
/*     */   {
/* 321 */     StringBuilder instanceInner = new StringBuilder();
/* 322 */     instanceInner.append("{\"_defClass\":\"");
/* 323 */     instanceInner.append(inner.getClass().getName()).append("\",");
/* 324 */     if (null != inner.getServiceName())
/*     */     {
/* 326 */       instanceInner.append("\"serviceName\":\"");
/* 327 */       instanceInner.append(inner.getServiceName()).append("\",");
/*     */     }
/*     */     else
/*     */     {
/* 331 */       instanceInner.append("\"serviceName\":null,");
/*     */     }
/* 333 */     if (null != inner.getInstanceName())
/*     */     {
/* 335 */       instanceInner.append("\"instanceName\":\"");
/* 336 */       instanceInner.append(inner.getInstanceName()).append("\",");
/*     */     }
/*     */     else
/*     */     {
/* 340 */       instanceInner.append("\"instanceName\":null,");
/*     */     }
/* 342 */     if (null != inner.getAddress())
/*     */     {
/* 344 */       instanceInner.append("\"address\":\"");
/* 345 */       instanceInner.append(inner.getAddress()).append("\",");
/*     */     }
/*     */     else
/*     */     {
/* 349 */       instanceInner.append("\"address\":null,");
/*     */     }
/* 351 */     instanceInner.append("\"alive\":");
/* 352 */     instanceInner.append(inner.isAlive()).append(",");
/* 353 */     if (null != inner.getType())
/*     */     {
/* 355 */       instanceInner.append("\"type\":\"");
/* 356 */       instanceInner.append(inner.getType()).append("\",");
/*     */     }
/*     */     else
/*     */     {
/* 360 */       instanceInner.append("\"type\":null,");
/*     */     }
/* 362 */     if (null != inner.getVersion())
/*     */     {
/* 364 */       instanceInner.append("\"version\":\"");
/* 365 */       instanceInner.append(inner.getVersion()).append("\",");
/*     */     }
/*     */     else
/*     */     {
/* 369 */       instanceInner.append("\"version\":null,");
/*     */     }
/* 371 */     if (null != inner.getDsf())
/*     */     {
/* 373 */       instanceInner.append("\"dsf\":\"");
/* 374 */       instanceInner.append(inner.getDsf()).append("\",");
/*     */     }
/*     */     else
/*     */     {
/* 378 */       instanceInner.append("\"dsf\":null,");
/*     */     }
/* 380 */     if (null != inner.getGroup())
/*     */     {
/* 382 */       instanceInner.append("\"group\":\"");
/* 383 */       instanceInner.append(inner.getGroup()).append("\",");
/*     */     }
/*     */     else
/*     */     {
/* 387 */       instanceInner.append("\"group\":null,");
/*     */     }
/* 389 */     if (null != inner.getServiceType())
/*     */     {
/* 391 */       instanceInner.append("\"serviceType\":\"");
/* 392 */       instanceInner.append(inner.getServiceType()).append("\",");
/*     */     }
/*     */     else
/*     */     {
/* 396 */       instanceInner.append("\"serviceType\":null,");
/*     */     }
/* 398 */     instanceInner.append("\"weight\":");
/* 399 */     instanceInner.append(inner.getWeight()).append(",");
/* 400 */     instanceInner.append("\"attrs\":{\"_defClass\":\"");
/* 401 */     instanceInner.append(inner.getAttrs().getClass().getName());
/* 402 */     instanceInner.append("\",\"$list\":[]},\"immutable\":");
/* 403 */     instanceInner.append(inner.isImmutable()).append("}");
/* 404 */     return instanceInner.toString();
/*     */   }
/*     */   
/*     */   public abstract String getName();
/*     */   
/*     */   public abstract String getInstanceNS();
/*     */   
/*     */   public abstract String getProviderRegistryKey(ServiceInstanceInner paramServiceInstanceInner);
/*     */   
/*     */   public abstract String getSdlRegistryKey(ServiceInstanceInner paramServiceInstanceInner);
/*     */   
/*     */   public abstract String getConsumerRegistryKey(ConsumerInstanceInner paramConsumerInstanceInner);
/*     */   
/*     */   public abstract String getRegistryKey(ServiceInner paramServiceInner);
/*     */   
/*     */   public abstract byte[] serializeForSR(ServiceInstanceInner paramServiceInstanceInner);
/*     */   
/*     */   public abstract byte[] serializeForSR(Properties paramProperties);
/*     */   
/*     */   public abstract String getInstanceNameFromKey(String paramString);
/*     */   
/*     */   public abstract String getShortKey(String paramString);
/*     */   
/*     */   public abstract String getNameFromShortKey(String paramString);
/*     */   
/*     */   public abstract ServiceInstanceInner deserializeInstanceData(String paramString, byte[] paramArrayOfByte);
/*     */   
/*     */   public abstract ServiceInner deserializeServiceData(String paramString, byte[] paramArrayOfByte);
/*     */   
/*     */   public abstract String getAddress();
/*     */   
/*     */   public abstract void setRegisterId(String paramString);
/*     */   
/*     */   public abstract String getRegisterId();
/*     */   
/*     */   public abstract String byteArrayToCheckSum(byte[] paramArrayOfByte);
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\ZkRegistryAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */