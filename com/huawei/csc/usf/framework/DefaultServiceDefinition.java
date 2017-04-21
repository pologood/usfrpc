/*     */ package com.huawei.csc.usf.framework;
/*     */ 
/*     */ import com.huawei.csc.remoting.common.util.CastUtil;
/*     */ import com.huawei.csc.usf.framework.util.CopyOnWriteHashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultServiceDefinition
/*     */   implements ServiceDefinition
/*     */ {
/*  34 */   private String serviceName = null;
/*     */   
/*  36 */   private String beanName = null;
/*     */   
/*  38 */   private String connectorType = null;
/*     */   
/*  40 */   private String protocol = null;
/*     */   
/*  42 */   private boolean isServer = false;
/*     */   
/*  44 */   private String serialization = null;
/*     */   
/*  46 */   private long timeout = 0L;
/*     */   
/*  48 */   private Map<String, Object> properties = new CopyOnWriteHashMap();
/*     */   
/*  50 */   private Map<String, MethodDefinition> methodDefinitions = new CopyOnWriteHashMap();
/*     */   
/*  52 */   private String group = "default";
/*     */   
/*  54 */   private String version = null;
/*     */   
/*  56 */   private String serviceAddress = null;
/*     */   
/*  58 */   private String serviceInterface = null;
/*     */   
/*  60 */   private String threadPool = null;
/*     */   
/*  62 */   private String routId = null;
/*     */   
/*  64 */   private String failPolicy = null;
/*     */   
/*  66 */   private int executes = 0;
/*     */   
/*  68 */   private String threshold = null;
/*     */   
/*  70 */   private String registry = "dsf_default";
/*  71 */   private String restUrl = null;
/*     */   
/*  73 */   private String restProtocolDirection = null;
/*     */   
/*     */ 
/*     */   public String getServiceName()
/*     */   {
/*  78 */     return this.serviceName;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setServiceName(String name)
/*     */   {
/*  84 */     this.serviceName = name;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getConnectorType()
/*     */   {
/*  90 */     return this.connectorType;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setConnectorType(String connectorType)
/*     */   {
/*  96 */     this.connectorType = connectorType;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isServer()
/*     */   {
/* 102 */     return this.isServer;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setIsServer(boolean isServer)
/*     */   {
/* 108 */     this.isServer = isServer;
/*     */   }
/*     */   
/*     */ 
/*     */   public <T> T getProperty(String key)
/*     */   {
/* 114 */     return (T)CastUtil.cast(this.properties.get(key));
/*     */   }
/*     */   
/*     */ 
/*     */   public void setProperty(String key, Object object)
/*     */   {
/* 120 */     this.properties.put(key, object);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setProtocolType(String protocol)
/*     */   {
/* 126 */     this.protocol = protocol;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getProtocolType()
/*     */   {
/* 132 */     return this.protocol;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getTimeout()
/*     */   {
/* 142 */     return this.timeout;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTimeout(long timeout)
/*     */   {
/* 153 */     this.timeout = timeout;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getSerialization()
/*     */   {
/* 159 */     return this.serialization;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setSerialization(String serialization)
/*     */   {
/* 165 */     this.serialization = serialization;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addMethodDefinition(String methodName, MethodDefinition methodDefinition)
/*     */   {
/* 172 */     this.methodDefinitions.put(methodName, methodDefinition);
/*     */   }
/*     */   
/*     */ 
/*     */   public MethodDefinition getMethodDefinition(String methodName)
/*     */   {
/* 178 */     return (MethodDefinition)this.methodDefinitions.get(methodName);
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, MethodDefinition> getMethodDefinitions()
/*     */   {
/* 184 */     return this.methodDefinitions;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setGroup(String group)
/*     */   {
/* 190 */     this.group = group;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getGroup()
/*     */   {
/* 196 */     return this.group;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getServiceAddress()
/*     */   {
/* 206 */     return this.serviceAddress;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setServiceAddress(String serviceAddress)
/*     */   {
/* 217 */     this.serviceAddress = serviceAddress;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getServiceInterface()
/*     */   {
/* 227 */     return this.serviceInterface;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setServiceInterface(String serviceInterface)
/*     */   {
/* 238 */     this.serviceInterface = serviceInterface;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getThreadPool()
/*     */   {
/* 248 */     return this.threadPool;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setThreadPool(String threadPool)
/*     */   {
/* 259 */     this.threadPool = threadPool;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getRouterId()
/*     */   {
/* 269 */     return this.routId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRouterId(String routId)
/*     */   {
/* 280 */     this.routId = routId;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFailPolicy()
/*     */   {
/* 286 */     return this.failPolicy;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setFailPolicy(String failPolicy)
/*     */   {
/* 292 */     this.failPolicy = failPolicy;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getExecutes()
/*     */   {
/* 299 */     return this.executes;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setExecutes(int executes)
/*     */   {
/* 305 */     this.executes = executes;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getBeanName()
/*     */   {
/* 311 */     return this.beanName;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setBeanName(String beanName)
/*     */   {
/* 317 */     this.beanName = beanName;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getVersion()
/*     */   {
/* 323 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setVersion(String version)
/*     */   {
/* 329 */     this.version = version;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getThreshold()
/*     */   {
/* 335 */     return this.threshold;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setThreshold(String threshold)
/*     */   {
/* 341 */     this.threshold = threshold;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setRegistry(String registry)
/*     */   {
/* 347 */     this.registry = registry;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getRegistry()
/*     */   {
/* 353 */     return this.registry;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getRestUrl()
/*     */   {
/* 360 */     return this.restUrl;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setResturl(String url)
/*     */   {
/* 366 */     this.restUrl = url;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getRestProtocolDirection()
/*     */   {
/* 372 */     return this.restProtocolDirection;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setRestProtocolDirection(String direction)
/*     */   {
/* 378 */     this.restProtocolDirection = direction;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\DefaultServiceDefinition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */