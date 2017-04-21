/*     */ package com.huawei.csc.usf.framework.pojo;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.Connector;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.DefaultServiceDefinition;
/*     */ import com.huawei.csc.usf.framework.ExceptionUtils;
/*     */ import com.huawei.csc.usf.framework.ExceptionUtilsHolder;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.IMessageFactory;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.MethodDefinition;
/*     */ import com.huawei.csc.usf.framework.ServiceDefinition;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.sr.ConsumerInstanceInner;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceInner;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceTypeAware;
/*     */ import com.huawei.csc.usf.framework.util.DSFRoutingUtil;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.aopalliance.intercept.MethodInterceptor;
/*     */ import org.aopalliance.intercept.MethodInvocation;
/*     */ import org.apache.commons.lang.StringUtils;
/*     */ import org.springframework.aop.framework.ProxyFactory;
/*     */ import org.springframework.aop.support.AopUtils;
/*     */ import org.springframework.beans.factory.FactoryBean;
/*     */ import org.springframework.beans.factory.InitializingBean;
/*     */ import org.springframework.remoting.support.RemoteAccessor;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PojoClientInner
/*     */   extends RemoteAccessor
/*     */   implements FactoryBean, MethodInterceptor, InitializingBean, ServiceTypeAware
/*     */ {
/*  42 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(PojoClientInner.class);
/*     */   
/*     */ 
/*     */ 
/*     */   private static final String DEFAULT_GROUP = "default";
/*     */   
/*     */ 
/*     */   private Connector connector;
/*     */   
/*     */ 
/*     */   private Object serviceProxy;
/*     */   
/*     */ 
/*  55 */   protected ServiceDefinition serviceDefinition = new DefaultServiceDefinition();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  61 */   private String localServiceName = null;
/*     */   
/*     */   private ServiceType serviceType;
/*     */   
/*     */   public void setConnector(Connector connector)
/*     */   {
/*  67 */     this.connector = connector;
/*     */   }
/*     */   
/*     */   public Connector getConnector()
/*     */   {
/*  72 */     return this.connector;
/*     */   }
/*     */   
/*     */   public String getServiceName()
/*     */   {
/*  77 */     return this.serviceDefinition.getServiceName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getLocalServiceName()
/*     */   {
/*  87 */     return this.localServiceName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setLocalServiceName(String localServiceName)
/*     */   {
/*  98 */     this.localServiceName = localServiceName;
/*     */   }
/*     */   
/*     */   protected Object genServiceProxy()
/*     */   {
/* 103 */     return new ProxyFactory(getServiceInterface(), this).getProxy(getClass().getClassLoader());
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getProxy()
/*     */   {
/* 109 */     return this.serviceProxy;
/*     */   }
/*     */   
/*     */   public void setServiceProxy(Object serviceProxy)
/*     */   {
/* 114 */     this.serviceProxy = serviceProxy;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ServiceDefinition getServiceDefinition()
/*     */   {
/* 124 */     return this.serviceDefinition;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setServiceDefinition(ServiceDefinition serviceDefinition)
/*     */   {
/* 135 */     this.serviceDefinition = serviceDefinition;
/*     */   }
/*     */   
/*     */ 
/*     */   public void afterPropertiesSet()
/*     */   {
/* 141 */     if (getServiceInterface() == null)
/*     */     {
/* 143 */       throw new IllegalArgumentException("Property 'serviceInterface' is required");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 148 */     this.serviceProxy = genServiceProxy();
/*     */     
/* 150 */     if (StringUtils.isEmpty(this.localServiceName))
/*     */     {
/* 152 */       this.localServiceName = getServiceName();
/*     */     }
/*     */     
/*     */ 
/* 156 */     if ((null != this.serviceDefinition.getRouterId()) && (this.serviceDefinition.getRouterId().equals("serviceDelayTimeRouter")))
/*     */     {
/*     */ 
/* 159 */       DSFRoutingUtil.setDelayRouter(true);
/*     */     }
/*     */     
/* 162 */     if (LOGGER.isInfoEnable())
/*     */     {
/* 164 */       LOGGER.info("pojo client created, service name[" + getServiceName() + "],service type[" + getServiceType() + "],protocol[" + this.serviceDefinition.getProtocolType() + "],group[" + this.serviceDefinition.getGroup() + "],router id[" + this.serviceDefinition.getRouterId() + "],server address[" + this.serviceDefinition.getServiceAddress() + "]");
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
/*     */   public Object invoke(MethodInvocation invocation)
/*     */     throws Exception
/*     */   {
/* 178 */     if (AopUtils.isToStringMethod(invocation.getMethod()))
/*     */     {
/* 180 */       return "POJO client proxy for " + this.serviceType + " service [" + getServiceName() + "]";
/*     */     }
/*     */     
/*     */ 
/* 184 */     Context context = new Context(new Object[] { this, invocation });
/* 185 */     context.setServiceType(getServiceType());
/*     */     
/* 187 */     if (0L == context.getMsTimeout())
/*     */     {
/*     */ 
/* 190 */       long methodTimeout = getLocalTimeout(invocation.getMethod().getName());
/*     */       
/* 192 */       context.setMsTimeout(methodTimeout);
/*     */     }
/*     */     
/* 195 */     context.setRouterType(this.serviceDefinition.getRouterId());
/* 196 */     context.setFailPolicy(this.serviceDefinition.getFailPolicy());
/* 197 */     context.setRegistry(this.serviceDefinition.getRegistry());
/* 198 */     context.setRestProtocolDirection(this.serviceDefinition.getRestProtocolDirection());
/*     */     
/*     */ 
/* 201 */     if (LOGGER.isDebugEnable())
/*     */     {
/* 203 */       LOGGER.debug("do invoke in pojo client, method name: " + invocation.getMethod());
/*     */     }
/*     */     
/*     */ 
/* 207 */     return doInvoke(context, invocation);
/*     */   }
/*     */   
/*     */ 
/*     */   protected Object doInvoke(Context context, MethodInvocation invocation)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 216 */       if (null == this.connector)
/*     */       {
/* 218 */         StringBuilder sb = new StringBuilder().append("can't find connector for service ").append(this.serviceDefinition.getServiceName()).append(", please check whether the file classpath*:/META-INF/spring/*.service.xml which defines the service or defines the needed PojoConnector bean loaded in ServiceEngine or not.");
/*     */         
/*     */ 
/*     */ 
/* 222 */         LOGGER.error(sb.toString());
/* 223 */         throw ExceptionUtilsHolder.getExceptionUtils(context.getServiceType()).configInitFailed(new NullPointerException());
/*     */       }
/*     */       
/*     */ 
/* 227 */       Object obj = this.connector.onReceive(context);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 235 */       return obj;
/*     */     }
/*     */     finally
/*     */     {
/* 239 */       context.done();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected long getLocalTimeout(String operationName)
/*     */   {
/* 251 */     String strTimeout = null;
/*     */     
/* 253 */     Map<String, MethodDefinition> methodControlMap = this.serviceDefinition.getMethodDefinitions();
/*     */     
/*     */ 
/* 256 */     MethodDefinition methodParameters = (MethodDefinition)methodControlMap.get(operationName);
/*     */     
/* 258 */     if (null != methodParameters)
/*     */     {
/* 260 */       strTimeout = methodParameters.getTimeout();
/*     */     }
/*     */     
/* 263 */     if ((StringUtils.isNotBlank(strTimeout)) && (!StringUtils.equals(strTimeout, "0")))
/*     */     {
/*     */ 
/*     */       try
/*     */       {
/*     */ 
/* 269 */         return Long.parseLong(strTimeout);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 273 */         if (LOGGER.isErrorEnable())
/*     */         {
/* 275 */           LOGGER.error("Parse Method timeout failed. Method name is: " + operationName, e);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 282 */     return getServiceDefinition().getTimeout();
/*     */   }
/*     */   
/*     */   public IMessage invocationToMessage(MethodInvocation invocation, String protocol)
/*     */     throws Exception
/*     */   {
/* 288 */     IMessageFactory factory = this.connector.getServiceEngine().getMessageFactory(getServiceType());
/*     */     
/* 290 */     if ("rest".equalsIgnoreCase(this.serviceDefinition.getProtocolType()))
/*     */     {
/* 292 */       protocol = this.serviceDefinition.getProtocolType();
/*     */     }
/* 294 */     IMessage req = factory.createRequest(protocol, getServiceName(), invocation.getMethod().getName());
/*     */     
/*     */ 
/* 297 */     req.setPayload(invocation.getArguments());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 302 */     if (!StringUtils.isEmpty(this.localServiceName))
/*     */     {
/* 304 */       req.getHeaders().setPolicyServiceName(this.localServiceName);
/*     */     }
/*     */     
/* 307 */     if (this.serviceDefinition.getServiceAddress() != null)
/*     */     {
/* 309 */       req.getHeaders().setDestAddr(this.serviceDefinition.getServiceAddress());
/*     */     }
/*     */     
/* 312 */     if ("rest".equalsIgnoreCase(this.serviceDefinition.getProtocolType()))
/*     */     {
/* 314 */       if (this.serviceDefinition.getRestUrl() != null)
/*     */       {
/* 316 */         if (!"out".equalsIgnoreCase(this.serviceDefinition.getRestProtocolDirection()))
/*     */         {
/*     */ 
/* 319 */           throw ExceptionUtilsHolder.getExceptionUtils(this.serviceType).lackConfig("importer rest service as the client,lack the direction configuration item");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 324 */         req.getHeaders().setDestAddr(this.serviceDefinition.getRestUrl());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 329 */     req.getHeaders().setGroup(getServiceGroup(this.connector));
/* 330 */     req.getHeaders().setVersion(this.serviceDefinition.getVersion());
/*     */     
/* 332 */     if (getServiceType().equals(ServiceType.DSF))
/*     */     {
/* 334 */       String serializeType = this.serviceDefinition.getSerialization();
/* 335 */       if (!StringUtils.isBlank(serializeType))
/*     */       {
/* 337 */         req.getHeaders().setAttachValue("serialization", serializeType);
/*     */       }
/*     */     }
/*     */     
/* 341 */     return req;
/*     */   }
/*     */   
/*     */   public Object getObject()
/*     */   {
/* 346 */     return this.serviceProxy;
/*     */   }
/*     */   
/*     */   public Class<?> getObjectType()
/*     */   {
/* 351 */     return getServiceInterface();
/*     */   }
/*     */   
/*     */   public boolean isSingleton()
/*     */   {
/* 356 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ServiceInner parseService(Connector connector)
/*     */   {
/* 367 */     Class<?> clazz = getServiceInterface();
/* 368 */     String serviceName = getLocalServiceName();
/* 369 */     String group = getServiceGroup(connector);
/* 370 */     String beanName = this.serviceDefinition.getBeanName();
/* 371 */     String failPolicy = this.serviceDefinition.getFailPolicy();
/* 372 */     long timeOut = this.serviceDefinition.getTimeout();
/* 373 */     String router = this.serviceDefinition.getRouterId();
/* 374 */     if (timeOut == 0L)
/*     */     {
/* 376 */       timeOut = connector.getServiceEngine().getSystemConfig().getTimeout();
/*     */     }
/*     */     
/* 379 */     if (StringUtils.isEmpty(failPolicy))
/*     */     {
/* 381 */       failPolicy = connector.getServiceEngine().getSystemConfig().getServerFailPolicy();
/*     */     }
/*     */     
/* 384 */     if (StringUtils.isEmpty(router))
/*     */     {
/* 386 */       router = connector.getServiceEngine().getSystemConfig().getDefaultRouter();
/*     */     }
/*     */     
/* 389 */     String type = connector.getConnectorType();
/* 390 */     String rpcAddress = connector.getServiceEngine().getSystemConfig().getRPCAddress(this.serviceType);
/*     */     
/* 392 */     ServiceInner service = new ServiceInner();
/* 393 */     service.setName(serviceName);
/* 394 */     service.setGroup(group);
/* 395 */     service.setImporter(true);
/* 396 */     service.setServiceType(this.serviceType.toString());
/* 397 */     ConsumerInstanceInner consumerInstance = service.createConsumerInstance(serviceName, type, this.serviceType.toString(), rpcAddress);
/*     */     
/*     */ 
/* 400 */     if (null != clazz)
/*     */     {
/* 402 */       List<String> mets = new ArrayList();
/* 403 */       Method[] methods = clazz.getMethods();
/*     */       
/* 405 */       for (Method method : methods)
/*     */       {
/* 407 */         mets.add(method.getName());
/* 408 */         if (this.serviceDefinition.getMethodDefinition(method.getName()) != null)
/*     */         {
/*     */ 
/* 411 */           MethodDefinition methodDefinition = this.serviceDefinition.getMethodDefinition(method.getName());
/*     */           
/* 413 */           String timeout = methodDefinition.getTimeout();
/* 414 */           if (timeout != null)
/*     */           {
/* 416 */             String methodTimeOut = method.getName() + "." + timeout;
/* 417 */             consumerInstance.addMethodTimeOut(methodTimeOut);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 423 */       service.setMethods(mets);
/*     */     }
/* 425 */     service.setType(type);
/* 426 */     consumerInstance.setMethods(service.getMethods());
/* 427 */     consumerInstance.setGroup(group);
/* 428 */     consumerInstance.setType(type);
/* 429 */     consumerInstance.setBeanName(beanName);
/* 430 */     consumerInstance.setMethodTimeOut(timeOut);
/* 431 */     consumerInstance.setRegId(this.serviceDefinition.getRegistry());
/* 432 */     consumerInstance.setClusterPolicy(failPolicy);
/* 433 */     int retries = 0;
/* 434 */     if ("failOver".equalsIgnoreCase(failPolicy))
/*     */     {
/* 436 */       retries = connector.getServiceEngine().getSystemConfig().getResendTimes();
/*     */     }
/*     */     
/* 439 */     consumerInstance.setReSentTime(retries);
/* 440 */     consumerInstance.setRouter(router);
/* 441 */     service.addConsumerInstance(consumerInstance);
/* 442 */     return service;
/*     */   }
/*     */   
/*     */   private String getServiceGroup(Connector connector)
/*     */   {
/* 447 */     String groupsStr = this.serviceDefinition.getGroup();
/* 448 */     if ((!"default".equals(groupsStr)) && (!StringUtils.isBlank(groupsStr)))
/*     */     {
/*     */ 
/* 451 */       return groupsStr;
/*     */     }
/*     */     
/* 454 */     ServiceEngine serviceEngine = connector.getServiceEngine();
/* 455 */     SystemConfig config = serviceEngine.getSystemConfig();
/* 456 */     groupsStr = config.getConsumerGroupNames();
/* 457 */     if (!StringUtils.isBlank(groupsStr))
/*     */     {
/*     */ 
/* 460 */       return groupsStr;
/*     */     }
/*     */     
/*     */ 
/* 464 */     return "default";
/*     */   }
/*     */   
/*     */   public final ServiceType getServiceType()
/*     */   {
/* 469 */     return this.serviceType;
/*     */   }
/*     */   
/*     */   public void setServiceType(ServiceType serviceType)
/*     */   {
/* 474 */     this.serviceType = serviceType;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\pojo\PojoClientInner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */