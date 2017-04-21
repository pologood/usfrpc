/*     */ package com.huawei.csc.usf.framework.sr;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.Properties;
/*     */ import org.apache.commons.lang.StringUtils;
/*     */ 
/*     */ public class UsfZkRegistryAdapter
/*     */   extends ZkRegistryAdapter
/*     */ {
/*  18 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(UsfZkRegistryAdapter.class);
/*     */   
/*     */ 
/*     */   private static final String DSF_ROOT = "dsf2/";
/*     */   
/*     */   private static final String PROVIDERS = "/providers/";
/*     */   
/*     */   private static final String CONSUMERS = "/consumers/";
/*     */   
/*     */   private static final String SERVICE = "/metadata/";
/*     */   
/*     */   private String registerId;
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  34 */     return ServiceType.USF.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getInstanceNS()
/*     */   {
/*  41 */     String zooKeeperRootDir = this.config.getZkRootDir(this.registerId);
/*  42 */     String instanceNsPath; String instanceNsPath; if (!StringUtils.isEmpty(zooKeeperRootDir))
/*     */     {
/*  44 */       String[] paths = zooKeeperRootDir.split("/");
/*  45 */       StringBuilder bulider = new StringBuilder();
/*  46 */       for (String path : paths)
/*     */       {
/*  48 */         if (!StringUtils.isEmpty(path))
/*     */         {
/*  50 */           bulider.append(path).append("/");
/*     */         }
/*     */       }
/*  53 */       instanceNsPath = "/" + bulider.toString() + "dsf2/";
/*     */     }
/*     */     else
/*     */     {
/*  57 */       instanceNsPath = "/dsf2/";
/*     */     }
/*  59 */     return instanceNsPath;
/*     */   }
/*     */   
/*     */ 
/*     */   public void init(ServiceRegistryAgent sr)
/*     */   {
/*  65 */     super.init(sr);
/*  66 */     Registry registry = sr.getRegistry();
/*  67 */     String zooKeeperRootDir = this.config.getZkRootDir(this.registerId);
/*  68 */     if (!StringUtils.isEmpty(zooKeeperRootDir))
/*     */     {
/*     */ 
/*  71 */       String[] paths = zooKeeperRootDir.split("/");
/*  72 */       StringBuilder bulider = new StringBuilder();
/*  73 */       for (String path : paths)
/*     */       {
/*  75 */         if (!StringUtils.isEmpty(path))
/*     */         {
/*  77 */           bulider.append(path).append("/");
/*  78 */           registry.addBuiltinKey("/" + bulider.toString());
/*     */         }
/*     */       }
/*     */     }
/*  82 */     registry.addBuiltinKey(getInstanceNS());
/*     */   }
/*     */   
/*     */ 
/*     */   public String getProviderRegistryKey(ServiceInstanceInner instance)
/*     */   {
/*  88 */     StringBuilder bulider = new StringBuilder();
/*  89 */     bulider.append(getInstanceNS());
/*  90 */     bulider.append(instance.getInstanceName());
/*  91 */     bulider.append("/providers/");
/*     */     try
/*     */     {
/*  94 */       bulider.append(URLEncoder.encode(instance.toUrl(this.config.getDsfApplication()), "UTF-8"));
/*     */ 
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/*  99 */       LOGGER.error("encode url failed");
/* 100 */       throw new RuntimeException(e.getMessage(), e);
/*     */     }
/* 102 */     return bulider.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] serializeForSR(ServiceInstanceInner instance)
/*     */   {
/* 109 */     return new byte[0];
/*     */   }
/*     */   
/*     */ 
/*     */   public String getInstanceNameFromKey(String registryKey)
/*     */   {
/* 115 */     String shortKey = registryKey.substring(registryKey.lastIndexOf('/') + 1);
/*     */     
/* 117 */     String[] data = shortKey.split("#");
/* 118 */     return data[2];
/*     */   }
/*     */   
/*     */ 
/*     */   public String getShortKey(String fullkey)
/*     */   {
/* 124 */     return fullkey.substring(fullkey.lastIndexOf('/') + 1);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getNameFromShortKey(String shortKey)
/*     */   {
/* 130 */     String[] data = shortKey.split("#");
/* 131 */     return data[2];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ServiceInstanceInner deserializeInstanceData(String lastPathSeg, byte[] data)
/*     */   {
/* 138 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getAddress()
/*     */   {
/* 144 */     return this.config.getRPCAddress(ServiceType.USF);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getRegistryKey(ServiceInner service)
/*     */   {
/* 150 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] serializeForSR(Properties props)
/*     */   {
/* 161 */     ByteArrayOutputStream bio = new ByteArrayOutputStream();
/*     */     
/*     */     try
/*     */     {
/* 165 */       props.store(bio, null);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 169 */       if (LOGGER.isErrorEnable())
/*     */       {
/* 171 */         LOGGER.error("serializeForSR ServiceInner error.", e);
/*     */       }
/*     */     }
/* 174 */     byte[] datas = bio.toByteArray();
/* 175 */     return datas;
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
/*     */   public ServiceInner deserializeServiceData(String lastPathSeg, byte[] data)
/*     */   {
/* 192 */     Properties kv = null;
/*     */     try
/*     */     {
/* 195 */       kv = parseData(data);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 199 */       LOGGER.error("Fail to parse data", e);
/* 200 */       return null;
/*     */     }
/*     */     
/* 203 */     return buildByProps(kv);
/*     */   }
/*     */   
/*     */   private Properties parseData(byte[] data) throws IOException
/*     */   {
/* 208 */     InputStream is = new ByteArrayInputStream(data);
/* 209 */     Properties props = new Properties();
/* 210 */     props.load(is);
/* 211 */     return props;
/*     */   }
/*     */   
/*     */   private ServiceInner buildByProps(Properties kv)
/*     */   {
/* 216 */     ServiceInner inner = new ServiceInner();
/* 217 */     String inf = StringUtils.defaultString(kv.getProperty("interface"));
/*     */     
/* 219 */     inner.setName(inf);
/* 220 */     String sdl = StringUtils.defaultString(kv.getProperty("sdl"));
/*     */     
/* 222 */     inner.setSdl(sdl);
/* 223 */     String serviceType = StringUtils.defaultString(kv.getProperty("serviceType"));
/*     */     
/* 225 */     inner.setServiceType(serviceType);
/*     */     
/* 227 */     String dsfVersion = StringUtils.defaultString(kv.getProperty("dsf"));
/*     */     
/* 229 */     inner.setDsf(dsfVersion);
/* 230 */     return inner;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getConsumerRegistryKey(ConsumerInstanceInner instance)
/*     */   {
/* 236 */     StringBuilder bulider = new StringBuilder();
/* 237 */     bulider.append(getInstanceNS());
/* 238 */     bulider.append(instance.getInstanceName());
/* 239 */     bulider.append("/consumers/");
/*     */     try
/*     */     {
/* 242 */       bulider.append(URLEncoder.encode(instance.toUrl(this.config.getDsfApplication()), "UTF-8"));
/*     */ 
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/* 247 */       LOGGER.error("encode url failed");
/* 248 */       throw new RuntimeException(e.getMessage(), e);
/*     */     }
/* 250 */     return bulider.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getSdlRegistryKey(ServiceInstanceInner instance)
/*     */   {
/* 256 */     StringBuilder bulider = new StringBuilder();
/* 257 */     bulider.append(getInstanceNS());
/* 258 */     bulider.append(instance.getInstanceName());
/* 259 */     bulider.append("/metadata/");
/* 260 */     return bulider.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public void setRegisterId(String id)
/*     */   {
/* 266 */     this.registerId = id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getRegisterId()
/*     */   {
/* 273 */     return this.registerId;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String byteArrayToCheckSum(byte[] byteArray)
/*     */   {
/* 305 */     char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*     */     
/*     */ 
/* 308 */     char[] resultCharArray = new char[byteArray.length * 2];
/*     */     
/* 310 */     int index = 0;
/* 311 */     for (byte b : byteArray)
/*     */     {
/* 313 */       resultCharArray[(index++)] = hexDigits[(b >>> 4 & 0xF)];
/* 314 */       resultCharArray[(index++)] = hexDigits[(b & 0xF)];
/*     */     }
/*     */     
/*     */ 
/* 318 */     return new String(resultCharArray);
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\UsfZkRegistryAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */