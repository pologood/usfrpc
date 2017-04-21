/*     */ package com.huawei.csc.usf.framework.pojo;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.Connector;
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
/*     */ import com.huawei.csc.usf.framework.sr.SRAgentFactory;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceInner;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceRegistryAgent;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*     */ import com.huawei.csc.usf.framework.sr.ZkRegistryAdapter;
/*     */ import com.huawei.csc.usf.framework.trace.InvokeServiceListenerManager;
/*     */ import com.huawei.csc.usf.framework.trace.ServiceContext;
/*     */ import com.huawei.csc.usf.framework.util.Utils;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.lang.StringUtils;
/*     */ 
/*     */ public class PojoServerInner
/*     */ {
/*  34 */   private static final DebugLog DEBUGGER = com.huawei.csc.kernel.api.log.LogFactory.getDebugLog(PojoServerInner.class);
/*     */   
/*     */ 
/*     */   protected Object ref;
/*     */   
/*     */   protected Class<?> intf;
/*     */   
/*     */   protected Connector connector;
/*     */   
/*  43 */   private Map<String, Object> extendProperties = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  48 */   protected ServiceDefinition serviceDefinition = new DefaultServiceDefinition();
/*     */   
/*  50 */   protected Map<String, Method> methods = new HashMap();
/*     */   
/*     */   private ServiceType serviceType;
/*     */   
/*     */   private static final String DEFAULT_GROUP = "default";
/*     */   
/*     */   public Class<?> getIntf()
/*     */   {
/*  58 */     if (this.intf != null)
/*     */     {
/*  60 */       return this.intf;
/*     */     }
/*     */     
/*  63 */     if (this.ref != null)
/*     */     {
/*  65 */       return this.ref.getClass();
/*     */     }
/*     */     
/*  68 */     throw new RuntimeException("get inf falied");
/*     */   }
/*     */   
/*     */   public void setIntf(Class<?> intf)
/*     */   {
/*  73 */     this.intf = intf;
/*  74 */     afterPropertiesSet();
/*     */   }
/*     */   
/*     */   public String getServiceName()
/*     */   {
/*  79 */     return this.serviceDefinition.getServiceName();
/*     */   }
/*     */   
/*     */   public Object getRef()
/*     */   {
/*  84 */     return this.ref;
/*     */   }
/*     */   
/*     */   public void setRef(Object ref)
/*     */   {
/*  89 */     this.ref = ref;
/*     */   }
/*     */   
/*     */   public Map<String, Method> getMethods()
/*     */   {
/*  94 */     return this.methods;
/*     */   }
/*     */   
/*     */   public void setMethods(Map<String, Method> methods)
/*     */   {
/*  99 */     this.methods = methods;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ServiceDefinition getServiceDefinition()
/*     */   {
/* 109 */     return this.serviceDefinition;
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
/* 120 */     this.serviceDefinition = serviceDefinition;
/*     */   }
/*     */   
/*     */   public IMessage invoke(IMessage msg) throws Exception
/*     */   {
/* 125 */     if ((msg instanceof com.huawei.csc.usf.framework.message.MessageImpl))
/*     */     {
/* 127 */       return pojoMsgInvoke(msg);
/*     */     }
/*     */     
/*     */ 
/* 131 */     MessageObjectInvoke moInvoker = this.connector.getServiceEngine().getMoInvoker();
/*     */     
/* 133 */     if (null == moInvoker)
/*     */     {
/* 135 */       String errMsg = "Implementation not found of interface \"MessageObjectInvoke\"";
/* 136 */       throw ExceptionUtilsHolder.getExceptionUtils(getServiceType()).implNotFound(errMsg);
/*     */     }
/*     */     
/* 139 */     return moInvoker.invoke(this, msg);
/*     */   }
/*     */   
/*     */ 
/*     */   public Connector getConnector()
/*     */   {
/* 145 */     return this.connector;
/*     */   }
/*     */   
/*     */   private IMessage pojoMsgInvoke(IMessage msg)
/*     */   {
/* 150 */     Object ret = null;
/*     */     
/* 152 */     ServiceEngine serviceEngine = this.connector.getServiceEngine();
/* 153 */     IMessageFactory factory = serviceEngine.getMessageFactory(getServiceType());
/*     */     
/*     */ 
/* 156 */     IMessage reply = factory.createReplyMessage(msg);
/* 157 */     String operationName = msg.getHeaders().getOperation();
/* 158 */     Method method = (Method)this.methods.get(operationName);
/* 159 */     Object[] argObjs = (Object[])msg.getPayload();
/*     */     
/* 161 */     ServiceContext linkContext = InvokeServiceListenerManager.newServiceContext(getServiceName(), operationName);
/*     */     
/*     */ 
/* 164 */     if (null == method)
/*     */     {
/* 166 */       if (DEBUGGER.isErrorEnable())
/*     */       {
/* 168 */         DEBUGGER.error("The method[" + operationName + "] of the service[" + getServiceName() + "] not found.");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 173 */       Exception exception = ExceptionUtilsHolder.getExceptionUtils(getServiceType()).pojoOperationNotFound(new Object[] { getServiceName(), operationName });
/*     */       
/*     */ 
/* 176 */       reply.setPayload(exception);
/* 177 */       InvokeServiceListenerManager.invokeOnException(linkContext, exception);
/*     */       
/* 179 */       InvokeServiceListenerManager.handleInvokeResult(linkContext, reply);
/* 180 */       return reply;
/*     */     }
/*     */     try
/*     */     {
/* 184 */       if (DEBUGGER.isDebugEnable())
/*     */       {
/* 186 */         DEBUGGER.debug("Invoke method[" + operationName + "]");
/*     */       }
/*     */       
/* 189 */       InvokeServiceListenerManager.invokeOnBegin(linkContext);
/*     */       
/* 191 */       ret = method.invoke(this.ref, argObjs);
/*     */       
/* 193 */       InvokeServiceListenerManager.invokeOnEnd(linkContext);
/*     */     }
/*     */     catch (InvocationTargetException e)
/*     */     {
/* 197 */       if (DEBUGGER.isErrorEnable())
/*     */       {
/* 199 */         DEBUGGER.error("Failed to invoke method[" + msg.getHeaders().getOperation() + "], ref[" + this.ref + "]", e.getTargetException());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 204 */       reply.setPayload(e.getCause());
/*     */       
/* 206 */       InvokeServiceListenerManager.invokeOnException(linkContext, e.getCause());
/*     */       
/* 208 */       InvokeServiceListenerManager.handleInvokeResult(linkContext, reply);
/*     */       
/* 210 */       return reply;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 214 */       if (DEBUGGER.isErrorEnable())
/*     */       {
/* 216 */         DEBUGGER.error("Failed to invoke method[" + msg.getHeaders().getOperation() + "], ref[" + this.ref + "]");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 221 */       reply.setPayload(e);
/*     */       
/* 223 */       InvokeServiceListenerManager.invokeOnException(linkContext, e);
/* 224 */       InvokeServiceListenerManager.handleInvokeResult(linkContext, reply);
/*     */       
/* 226 */       return reply;
/*     */     }
/*     */     
/* 229 */     reply.setPayload(ret);
/*     */     
/* 231 */     InvokeServiceListenerManager.handleInvokeResult(linkContext, reply);
/*     */     
/* 233 */     return reply;
/*     */   }
/*     */   
/*     */   public void afterPropertiesSet()
/*     */   {
/* 238 */     Class<?> refClazz = getRefClazz();
/* 239 */     Method[] methods = null;
/*     */     
/* 241 */     if (null != refClazz)
/*     */     {
/* 243 */       methods = refClazz.getMethods();
/*     */     }
/*     */     else
/*     */     {
/* 247 */       methods = this.intf.getMethods();
/*     */     }
/*     */     
/* 250 */     for (Method method : methods)
/*     */     {
/* 252 */       this.methods.put(method.getName(), method);
/*     */     }
/*     */   }
/*     */   
/*     */   private Class<?> getRefClazz()
/*     */   {
/* 258 */     if (null == this.ref)
/*     */     {
/* 260 */       return null;
/*     */     }
/* 262 */     Class<?> descriptorClass = this.ref.getClass();
/* 263 */     return descriptorClass;
/*     */   }
/*     */   
/*     */   public void setConnector(Connector connector)
/*     */   {
/* 268 */     this.connector = connector;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<String, Object> getExtendProperties()
/*     */   {
/* 278 */     return this.extendProperties;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setExtendProperties(Map<String, Object> extendProperties)
/*     */   {
/* 289 */     this.extendProperties = extendProperties;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ServiceInner parseService(Connector connector)
/*     */   {
/* 301 */     List<String> methodNames = new ArrayList();
/* 302 */     if (connector == null)
/*     */     {
/* 304 */       return null;
/*     */     }
/*     */     
/* 307 */     String connectorType = connector.getConnectorType();
/* 308 */     ServiceEngine serviceEngine = connector.getServiceEngine();
/* 309 */     if (serviceEngine == null)
/*     */     {
/* 311 */       return null;
/*     */     }
/* 313 */     SystemConfig config = serviceEngine.getSystemConfig();
/* 314 */     ServiceInner service = createService(connectorType, config);
/* 315 */     SdlParse sdlParse = serviceEngine.getSdlParser();
/* 316 */     if (null != sdlParse)
/*     */     {
/*     */ 
/* 319 */       String sdl = sdlParse.parse(getIntf());
/* 320 */       if (null != sdl)
/*     */       {
/* 322 */         service.setSdl(sdl);
/*     */       }
/*     */     }
/* 325 */     service.setExecutes(getServiceDefinition().getExecutes());
/* 326 */     String serviceName = getServiceName();
/* 327 */     String protocolStr = getServiceDefinition().getProtocolType();
/* 328 */     List<String> protocols = Utils.resolveProtocols(protocolStr);
/* 329 */     Method[] objectMethods = Object.class.getMethods();
/*     */     
/* 331 */     methodNames.addAll(this.methods.keySet());
/* 332 */     for (Method intfMethod : this.methods.values())
/*     */     {
/*     */ 
/* 335 */       for (Method method : objectMethods)
/*     */       {
/* 337 */         if (method.getName().equals(intfMethod.getName()))
/*     */         {
/* 339 */           methodNames.remove(intfMethod.getName());
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 344 */     if (protocols.isEmpty())
/*     */     {
/* 346 */       protocols.add("POJO");
/*     */     }
/* 348 */     String groupStr = service.getGroup();
/* 349 */     List<String> groups = Utils.resolveGroups(groupStr);
/* 350 */     String group = "default";
/* 351 */     if (groups.size() > 1)
/*     */     {
/* 353 */       group = (String)groups.get(0);
/* 354 */       DEBUGGER.error("config the service lable is more than one group, service name is" + serviceName + "," + "so set the group is" + group);
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 360 */       group = (String)groups.get(0);
/*     */     }
/*     */     
/* 363 */     ServiceRegistryAgent srAgent = serviceEngine.getSrAgentFactory().getDefaultSRAgent();
/*     */     
/* 365 */     if (srAgent == null)
/*     */     {
/* 367 */       return null;
/*     */     }
/* 369 */     ZkRegistryAdapter adapter = srAgent.getRegistryAdapter();
/* 370 */     if (adapter == null)
/*     */     {
/* 372 */       return null;
/*     */     }
/* 374 */     for (String protocol : protocols)
/*     */     {
/* 376 */       String address = connector.getServiceEngine().getSystemConfig().getRPCAddress(this.serviceType);
/*     */       
/* 378 */       int weight = adapter.getSystemConfig().getServerWeight();
/* 379 */       SystemConfig systemConfig = this.connector.getServiceEngine().getSystemConfig();
/*     */       
/* 381 */       String dsfApplication = systemConfig.getDsfApplication();
/* 382 */       String servicePrefixName = systemConfig.getServicePrefixName();
/* 383 */       ServiceInstanceInner instance = service.createInstance(group, serviceName, this.serviceType.toString(), address, weight, dsfApplication, servicePrefixName);
/*     */       
/*     */ 
/* 386 */       instance.setMethods(methodNames);
/*     */       
/* 388 */       instance.setType(protocol);
/* 389 */       if ("rest".equalsIgnoreCase(protocol))
/*     */       {
/* 391 */         String restUrl = systemConfig.getRestUrl().trim();
/* 392 */         if (!StringUtils.isEmpty(restUrl))
/*     */         {
/* 394 */           instance.setRestfulUrl(restUrl);
/*     */         }
/*     */       }
/* 397 */       Map<String, Object> extendAttrs = service.getAttrs();
/* 398 */       if ((extendAttrs != null) && (!extendAttrs.isEmpty()))
/*     */       {
/* 400 */         instance.getAttrs().putAll(extendAttrs);
/*     */       }
/*     */       
/* 403 */       service.addInstance(instance);
/*     */     }
/*     */     
/* 406 */     return service;
/*     */   }
/*     */   
/*     */   private ServiceInner createService(String connectorType, SystemConfig config)
/*     */   {
/* 411 */     ServiceInner service = new ServiceInner();
/*     */     
/* 413 */     String infName = getIntf().getName();
/* 414 */     service.setName(infName);
/*     */     
/*     */ 
/* 417 */     String group = getServceGroup(config);
/* 418 */     service.setGroup(group);
/*     */     
/* 420 */     String version = this.serviceDefinition.getVersion();
/* 421 */     service.setVersion(version);
/*     */     
/* 423 */     service.setType(connectorType);
/*     */     
/* 425 */     service.setImporter(false);
/*     */     
/* 427 */     List<String> methodNames = new ArrayList();
/* 428 */     Map<String, Method> methodMap = getMethods();
/* 429 */     if (methodMap != null)
/*     */     {
/* 431 */       methodNames.addAll(methodMap.keySet());
/*     */       
/* 433 */       for (String methodName : methodNames)
/*     */       {
/* 435 */         if (getServiceDefinition().getMethodDefinition(methodName) != null)
/*     */         {
/* 437 */           String methodExcutes = getServiceDefinition().getMethodDefinition(methodName).getExecutes();
/*     */           
/*     */ 
/* 440 */           if (methodExcutes != null)
/*     */           {
/* 442 */             methodName = methodName + "." + methodExcutes;
/* 443 */             service.addMethodExecutes(methodName);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 448 */     service.setMethods(methodNames);
/*     */     
/* 450 */     service.setAttrs(getExtendProperties());
/*     */     
/* 452 */     service.setServiceType(this.serviceType.toString());
/*     */     
/* 454 */     return service;
/*     */   }
/*     */   
/*     */   private String getServceGroup(SystemConfig config)
/*     */   {
/* 459 */     String groupStr = this.serviceDefinition.getGroup();
/* 460 */     if ((!"default".equals(groupStr)) && (!StringUtils.isBlank(groupStr)))
/*     */     {
/*     */ 
/* 463 */       return groupStr;
/*     */     }
/*     */     
/* 466 */     String providerGroupName = config.getProviderGroupName();
/* 467 */     if (!StringUtils.isBlank(providerGroupName))
/*     */     {
/*     */ 
/* 470 */       return providerGroupName;
/*     */     }
/*     */     
/*     */ 
/* 474 */     return "default";
/*     */   }
/*     */   
/*     */   public ServiceType getServiceType()
/*     */   {
/* 479 */     return this.serviceType;
/*     */   }
/*     */   
/*     */   public void setServiceType(ServiceType serviceType)
/*     */   {
/* 484 */     this.serviceType = serviceType;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\pojo\PojoServerInner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */