/*     */ package com.huawei.csc.usf.framework.routing;
/*     */ 
/*     */ import com.huawei.csc.container.api.ContextRegistry;
/*     */ import com.huawei.csc.container.api.IContextHolder;
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.Connector;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.ExceptionUtils;
/*     */ import com.huawei.csc.usf.framework.ExceptionUtilsHolder;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.interceptor.InInterceptor;
/*     */ import com.huawei.csc.usf.framework.interceptor.impl.AbstractInterceptor;
/*     */ import com.huawei.csc.usf.framework.monitor.delay.ServiceDelayTimeCountCenter;
/*     */ import com.huawei.csc.usf.framework.routing.filter.BusConnectorBlackListFilter;
/*     */ import com.huawei.csc.usf.framework.routing.filter.DsfCustomRouterFilter;
/*     */ import com.huawei.csc.usf.framework.routing.filter.GroupNameFilter;
/*     */ import com.huawei.csc.usf.framework.routing.filter.ProtocolTypeFilter;
/*     */ import com.huawei.csc.usf.framework.routing.filter.ResendAddressFilter;
/*     */ import com.huawei.csc.usf.framework.routing.filter.RouterGovernanceFilter;
/*     */ import com.huawei.csc.usf.framework.routing.filter.RoutingServiceInstanceFilterContext;
/*     */ import com.huawei.csc.usf.framework.routing.filter.RoutingServiceInstanceFilterManager;
/*     */ import com.huawei.csc.usf.framework.routing.filter.ServiceOffLineFilter;
/*     */ import com.huawei.csc.usf.framework.routing.filter.ServicePrefixNameFilter;
/*     */ import com.huawei.csc.usf.framework.routing.filter.ServiceTypeFilter;
/*     */ import com.huawei.csc.usf.framework.routing.filter.VersionFilter;
/*     */ import com.huawei.csc.usf.framework.sr.ConsumerConfigUpdateListener;
/*     */ import com.huawei.csc.usf.framework.sr.DsfZookeeperDataManager;
/*     */ import com.huawei.csc.usf.framework.sr.ProviderConfigUpdateListener;
/*     */ import com.huawei.csc.usf.framework.sr.SRAgentFactory;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceRegistryAgent;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceUpdateListener;
/*     */ import com.huawei.csc.usf.framework.util.Utils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public class RoutingInterceptor
/*     */   extends AbstractInterceptor
/*     */   implements ServiceUpdateListener, ProviderConfigUpdateListener, ConsumerConfigUpdateListener, InInterceptor
/*     */ {
/*  68 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(RoutingInterceptor.class);
/*     */   
/*     */ 
/*  71 */   private int inWeight = 1000;
/*     */   
/*     */   public static final String NAME = "routing";
/*     */   
/*     */   private static final String DSF_ROUTING_PROCESSOR = "dsfRoutingProcessor";
/*     */   
/*  77 */   private final Object LOCK = new Object();
/*     */   
/*  79 */   private SRAgentFactory srAgentFactory = null;
/*     */   
/*  81 */   private final Object serviceAddressMapperLock = new Object();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  86 */   private Map<String, List<ServiceInstanceInner>> serviceInstanceCache = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */   private ServiceEngine serviceEngine;
/*     */   
/*     */ 
/*  93 */   private Map<String, ServiceAddress> serviceAddressMapper = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  98 */   private Map<String, ServiceAddress> beanServiceAddressMapper = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 103 */   private RoutingServiceInstanceFilterManager beforeCacheFilterManager = new RoutingServiceInstanceFilterManager();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 108 */   private RoutingServiceInstanceFilterManager afterCacheFilterManager = new RoutingServiceInstanceFilterManager();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final String SERVICE_INSTANCE_CACHE_KEY_CONNECTOR = "#";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final String ALL_MATCHER = "ALL";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void init()
/*     */   {
/* 125 */     if (this.srAgentFactory == null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 135 */       this.srAgentFactory = this.serviceEngine.getSrAgentFactory();
/*     */     }
/*     */     
/* 138 */     this.srAgentFactory.addServiceUpdateListenerOnSRs(this);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 143 */     this.srAgentFactory.addProviderConfigUpdateListenerOnSRs(this);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 148 */     this.srAgentFactory.addConsumerConfigUpdateListenerOnSRs(this);
/*     */     
/*     */ 
/* 151 */     initServiceAddress();
/*     */     
/*     */ 
/* 154 */     initTimeCountCenter();
/*     */     
/*     */ 
/* 157 */     initDSFRoutingProcessor();
/*     */     
/*     */ 
/* 160 */     initFilterManagers();
/*     */   }
/*     */   
/*     */   public void setSrAgentFactory(SRAgentFactory srAgentFactory)
/*     */   {
/* 165 */     this.srAgentFactory = srAgentFactory;
/*     */   }
/*     */   
/*     */   private void initServiceAddress()
/*     */   {
/* 170 */     Map<String, ServiceAddress> serviceAddressMap = ContextRegistry.getContextHolder().getBeansOfType(ServiceAddress.class);
/*     */     
/* 172 */     if (!serviceAddressMap.isEmpty())
/*     */     {
/* 174 */       this.beanServiceAddressMapper.putAll(serviceAddressMap);
/*     */     }
/*     */   }
/*     */   
/*     */   private void initTimeCountCenter()
/*     */   {
/* 180 */     ServiceDelayTimeCountCenter timeCountCenter = ServiceDelayTimeCountCenter.getInstance();
/*     */     
/* 182 */     timeCountCenter.init(this.serviceEngine, this.srAgentFactory);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void initDSFRoutingProcessor()
/*     */   {
/* 189 */     if (ContextRegistry.getContextHolder().containsBean("dsfRoutingProcessor"))
/*     */     {
/*     */ 
/* 192 */       RoutingProcessor routingProcessor = (RoutingProcessor)ContextRegistry.getContextHolder().getBean("dsfRoutingProcessor");
/*     */       
/* 194 */       RoutingProcessorManager.addRoutingProcessor(routingProcessor);
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 199 */       if (DEBUGGER.isInfoEnable())
/*     */       {
/* 201 */         DEBUGGER.info("it doesn't exist dsf adapter jar");
/*     */       }
/* 203 */       return;
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
/*     */   private void initFilterManagers()
/*     */   {
/* 217 */     ServiceTypeFilter serviceTypeFilter = new ServiceTypeFilter();
/* 218 */     this.beforeCacheFilterManager.addFilter(serviceTypeFilter);
/*     */     
/* 220 */     ServiceOffLineFilter serviceOffLineFilter = new ServiceOffLineFilter();
/* 221 */     this.beforeCacheFilterManager.addFilter(serviceOffLineFilter);
/*     */     
/* 223 */     GroupNameFilter groupNameFilter = new GroupNameFilter();
/* 224 */     this.beforeCacheFilterManager.addFilter(groupNameFilter);
/*     */     
/* 226 */     ServicePrefixNameFilter servicePrefixNameFilter = new ServicePrefixNameFilter();
/* 227 */     this.beforeCacheFilterManager.addFilter(servicePrefixNameFilter);
/*     */     
/* 229 */     VersionFilter versionFilter = new VersionFilter();
/* 230 */     this.beforeCacheFilterManager.addFilter(versionFilter);
/*     */     
/*     */ 
/* 233 */     ProtocolTypeFilter protocolFilter = new ProtocolTypeFilter();
/* 234 */     this.beforeCacheFilterManager.addFilter(protocolFilter);
/*     */     
/*     */ 
/* 237 */     RouterGovernanceFilter routerGovernanceFilter = new RouterGovernanceFilter();
/* 238 */     this.afterCacheFilterManager.addFilter(routerGovernanceFilter);
/*     */     
/* 240 */     BusConnectorBlackListFilter busConnectorBlackListFilter = new BusConnectorBlackListFilter();
/* 241 */     this.afterCacheFilterManager.addFilter(busConnectorBlackListFilter);
/*     */     
/* 243 */     ResendAddressFilter resendAddressFilter = new ResendAddressFilter();
/* 244 */     this.afterCacheFilterManager.addFilter(resendAddressFilter);
/*     */     
/* 246 */     DsfCustomRouterFilter dsfCustomRouterFilter = new DsfCustomRouterFilter();
/* 247 */     this.afterCacheFilterManager.addFilter(dsfCustomRouterFilter);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 256 */     this.srAgentFactory.removeServiceUpdateListenerOnSRs(this);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 262 */     this.srAgentFactory.removeProviderConfigUpdateListenerOnSRs(this);
/*     */     
/* 264 */     this.srAgentFactory.removeConsumerConfigUpdateListenerOnSRs(this);
/*     */     
/* 266 */     this.serviceInstanceCache.clear();
/*     */     
/*     */ 
/* 269 */     destroyFilterManagers();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void destroyFilterManagers()
/*     */   {
/* 279 */     this.beforeCacheFilterManager.clearFilters();
/* 280 */     this.afterCacheFilterManager.clearFilters();
/*     */   }
/*     */   
/*     */   protected void doInvoke(Context context)
/*     */     throws Exception
/*     */   {
/* 286 */     if (context.isServer())
/*     */     {
/*     */ 
/* 289 */       return;
/*     */     }
/*     */     
/* 292 */     if ("reply".equals(context.getReceivedMessage().getHeaders().getType()))
/*     */     {
/*     */ 
/* 295 */       return;
/*     */     }
/* 297 */     IMessage receivedMessage = context.getReceivedMessage();
/* 298 */     MessageHeaders headers = receivedMessage.getHeaders();
/* 299 */     String lastRequestDestAddress = headers.getDestAddr();
/*     */     
/* 301 */     ServiceType serviceType = context.getServiceType();
/* 302 */     String listenAddress = this.serviceEngine.getSystemConfig().getRPCAddress(serviceType);
/*     */     
/*     */ 
/* 305 */     boolean isReSend = context.getIsReSend();
/*     */     
/* 307 */     if ((lastRequestDestAddress != null) && (!lastRequestDestAddress.equals(listenAddress)) && (!isReSend))
/*     */     {
/*     */ 
/*     */ 
/* 311 */       return;
/*     */     }
/*     */     
/*     */ 
/* 315 */     String serviceName = headers.getServiceName();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 321 */     RoutingServiceInstanceFilterContext routingContext = new RoutingServiceInstanceFilterContext();
/*     */     
/* 323 */     if (isReSend)
/*     */     {
/* 325 */       String failPolicy = context.getFailPolicy();
/* 326 */       if (StringUtils.isEmpty(failPolicy))
/*     */       {
/* 328 */         failPolicy = context.getSrcConnector().getServiceEngine().getSystemConfig().getServerFailPolicy();
/*     */       }
/*     */       
/* 331 */       if ("failResend".equalsIgnoreCase(failPolicy))
/*     */       {
/* 333 */         if (Utils.isEmpty(lastRequestDestAddress))
/*     */         {
/* 335 */           logAndThrowException(routingContext, context);
/*     */         }
/*     */         else
/*     */         {
/* 339 */           headers.setDestAddr(lastRequestDestAddress);
/*     */         }
/* 341 */         return;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 347 */     List<ServiceInstanceInner> instances = findServiceInstancesFromChache(routingContext, context);
/*     */     
/*     */ 
/* 350 */     setRouterType(context.getRegistry(), serviceName, listenAddress, headers.getOperation(), this.serviceEngine.getSystemConfig().getDsfApplication(), context);
/*     */     
/*     */ 
/*     */ 
/* 354 */     routingContext.setCacheServiceInstanceSize(instances.size());
/*     */     
/*     */ 
/* 357 */     this.afterCacheFilterManager.doFilter(instances, routingContext, context);
/*     */     
/*     */ 
/* 360 */     ServiceAddress address = findServiceAddress(serviceName, context.getRouterType(), isReSend);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 365 */     String destAddress = null;
/* 366 */     if (instances.size() > 0)
/*     */     {
/*     */ 
/* 369 */       RoutingContext rContext = new RoutingContext();
/* 370 */       rContext.setListenAddress(listenAddress);
/* 371 */       rContext.setMsg(receivedMessage);
/* 372 */       ServiceInstanceInner destInstance = address.getRouterAddress(instances, rContext);
/*     */       
/* 374 */       if (null != destInstance)
/*     */       {
/* 376 */         String restUrl = destInstance.getRestfulUrl();
/* 377 */         if ((!StringUtils.isEmpty(restUrl)) && ("out".equalsIgnoreCase(context.getRestProtocolDirection())))
/*     */         {
/*     */ 
/*     */ 
/* 381 */           destAddress = restUrl;
/*     */         }
/*     */         else
/*     */         {
/* 385 */           destAddress = destInstance.getAddress();
/*     */         }
/* 387 */         context.setDestServiceType(ServiceType.valueOf(destInstance.getServiceType()));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 393 */     if (Utils.isEmpty(destAddress))
/*     */     {
/* 395 */       logAndThrowException(routingContext, context);
/*     */     }
/*     */     
/*     */ 
/* 399 */     headers.setDestAddr(destAddress);
/* 400 */     if (DEBUGGER.isDebugEnable())
/*     */     {
/* 402 */       DEBUGGER.debug("after going through route capability,find available service instance for " + serviceName + ",adress " + destAddress);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setRouterType(String regId, String serviceName, String listenAddress, String operation, String application, Context context)
/*     */   {
/* 414 */     String routerType = (String)this.srAgentFactory.getSRAgent(regId).getZookeeperDataManager().getConfigFromConfiguration(serviceName, operation, listenAddress, application, "router", "consumer");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 420 */     if (null != routerType)
/*     */     {
/* 422 */       context.setRouterType(routerType);
/* 423 */       if (DEBUGGER.isInfoEnable())
/*     */       {
/* 425 */         DEBUGGER.info(String.format("governance configuration about router type become effective, governance configuration: service=[%s], operation=[%s], router=[%s].", new Object[] { serviceName, operation, routerType }));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 430 */       if ("serviceDelayTimeRouter".equals(routerType))
/*     */       {
/* 432 */         ServiceDelayTimeCountCenter.getInstance().startServiceDelayTimer();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private List<ServiceInstanceInner> findServiceInstancesFromChache(RoutingServiceInstanceFilterContext routingContext, Context context)
/*     */   {
/* 453 */     IMessage receivedMessage = context.getReceivedMessage();
/* 454 */     MessageHeaders headers = receivedMessage.getHeaders();
/* 455 */     String serviceName = headers.getServiceName();
/* 456 */     if (StringUtils.isEmpty(serviceName))
/*     */     {
/*     */ 
/* 459 */       return new ArrayList();
/*     */     }
/*     */     
/* 462 */     String serviceInstanceCacheKey = getServiceInstanceCacheKey(context);
/*     */     
/*     */ 
/*     */ 
/* 466 */     List<ServiceInstanceInner> instances = (List)this.serviceInstanceCache.get(serviceInstanceCacheKey);
/*     */     
/* 468 */     if (Utils.isEmpty(instances))
/*     */     {
/* 470 */       synchronized (this.LOCK)
/*     */       {
/* 472 */         instances = (List)this.serviceInstanceCache.get(serviceInstanceCacheKey);
/* 473 */         if (Utils.isEmpty(instances))
/*     */         {
/*     */ 
/* 476 */           instances = findServiceInstancesFromSRorZK(context);
/*     */           
/* 478 */           this.beforeCacheFilterManager.doFilter(instances, routingContext, context);
/*     */           
/*     */ 
/*     */ 
/* 482 */           updateServiceWeight(instances, context.getRegistry());
/*     */           
/*     */ 
/* 485 */           if (!Utils.isEmpty(instances))
/*     */           {
/* 487 */             this.serviceInstanceCache.put(serviceInstanceCacheKey, instances);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 494 */     return null == instances ? new ArrayList() : new ArrayList(instances);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void updateServiceWeight(List<ServiceInstanceInner> instances, String registryId)
/*     */   {
/* 501 */     for (ServiceInstanceInner instance : instances)
/*     */     {
/* 503 */       String address = instance.getAddress();
/* 504 */       Integer weight = this.srAgentFactory.getSRAgent(registryId).getZookeeperDataManager().getWeight(address);
/*     */       
/* 506 */       if ((null != weight) && (weight.intValue() >= 1))
/*     */       {
/* 508 */         if (DEBUGGER.isInfoEnable())
/*     */         {
/* 510 */           DEBUGGER.info("Get weight of " + instance.getInstanceName() + " from zookeeper configurations, current weight is: " + weight.toString());
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 515 */         instance.setWeight(weight.intValue());
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
/*     */ 
/*     */ 
/*     */ 
/*     */   private List<ServiceInstanceInner> findServiceInstancesFromSRorZK(Context context)
/*     */   {
/* 533 */     IMessage receivedMessage = context.getReceivedMessage();
/* 534 */     MessageHeaders headers = receivedMessage.getHeaders();
/* 535 */     String serviceName = headers.getServiceName();
/* 536 */     if (StringUtils.isEmpty(serviceName))
/*     */     {
/*     */ 
/* 539 */       return new ArrayList();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 544 */     List<ServiceInstanceInner> instances = this.srAgentFactory.findInstances(context.getRegistry(), serviceName);
/*     */     
/* 546 */     if (Utils.isEmpty(instances))
/*     */     {
/* 548 */       this.srAgentFactory.findServiceDataFromZk(context.getRegistry(), serviceName);
/*     */       
/* 550 */       instances = this.srAgentFactory.findInstances(context.getRegistry(), serviceName);
/*     */     }
/*     */     
/*     */ 
/* 554 */     if (null == instances)
/*     */     {
/* 556 */       if (DEBUGGER.isErrorEnable())
/*     */       {
/* 558 */         DEBUGGER.error("Find no service instance from sr/zk.");
/*     */       }
/* 560 */       return new ArrayList();
/*     */     }
/* 562 */     return new ArrayList(instances);
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
/*     */   private String getServiceInstanceCacheKey(Context context)
/*     */   {
/* 575 */     StringBuilder key = new StringBuilder();
/*     */     
/*     */ 
/* 578 */     IMessage receivedMessage = context.getReceivedMessage();
/* 579 */     MessageHeaders headers = receivedMessage.getHeaders();
/* 580 */     String policyProtocol = headers.getPolicyProtocol();
/* 581 */     if ("rest".equalsIgnoreCase(policyProtocol))
/*     */     {
/* 583 */       if ("out".equalsIgnoreCase(context.getRestProtocolDirection()))
/*     */       {
/* 585 */         key.append(policyProtocol);
/* 586 */         key.append("#");
/*     */       }
/*     */     }
/*     */     
/* 590 */     String srId = context.getRegistry();
/* 591 */     key.append(srId);
/* 592 */     key.append("#");
/*     */     
/*     */ 
/* 595 */     key.append(headers.getVersion());
/* 596 */     key.append("#");
/*     */     
/*     */ 
/* 599 */     key.append(headers.getGroup());
/* 600 */     key.append("#");
/*     */     
/*     */ 
/* 603 */     key.append(headers.getServiceName());
/*     */     
/* 605 */     return key.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   private ServiceAddress findServiceAddress(String serviceName, String routerType, boolean isReSend)
/*     */   {
/* 611 */     if (isReSend)
/*     */     {
/* 613 */       return (ServiceAddress)this.beanServiceAddressMapper.get("poll");
/*     */     }
/* 615 */     ServiceAddress address = (ServiceAddress)this.serviceAddressMapper.get(serviceName);
/* 616 */     if (null == address)
/*     */     {
/* 618 */       synchronized (this.serviceAddressMapperLock)
/*     */       {
/* 620 */         address = (ServiceAddress)this.serviceAddressMapper.get(serviceName);
/* 621 */         if (null == address)
/*     */         {
/* 623 */           address = getServiceAddress(routerType);
/* 624 */           Map<String, ServiceAddress> newMapper = new HashMap(this.serviceAddressMapper);
/*     */           
/* 626 */           newMapper.put(serviceName, address);
/* 627 */           this.serviceAddressMapper = newMapper;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 632 */     return address;
/*     */   }
/*     */   
/*     */   private ServiceAddress getServiceAddress(String routerType)
/*     */   {
/* 637 */     ServiceAddress serviceAddress = null;
/* 638 */     String defaultRouter = this.serviceEngine.getSystemConfig().getDefaultRouter();
/*     */     
/*     */ 
/* 641 */     if (StringUtils.isEmpty(routerType))
/*     */     {
/* 643 */       serviceAddress = (ServiceAddress)this.beanServiceAddressMapper.get(defaultRouter);
/*     */     }
/*     */     else
/*     */     {
/* 647 */       serviceAddress = (ServiceAddress)this.beanServiceAddressMapper.get(routerType);
/* 648 */       if (null == serviceAddress)
/*     */       {
/* 650 */         serviceAddress = (ServiceAddress)this.beanServiceAddressMapper.get(defaultRouter);
/*     */       }
/*     */     }
/* 653 */     if (null == serviceAddress)
/*     */     {
/* 655 */       serviceAddress = (ServiceAddress)this.beanServiceAddressMapper.get("poll");
/*     */     }
/* 657 */     return serviceAddress;
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
/*     */   private void logAndThrowException(RoutingServiceInstanceFilterContext routingContext, Context context)
/*     */     throws Exception
/*     */   {
/* 674 */     IMessage receivedMessage = context.getReceivedMessage();
/* 675 */     ServiceType serviceType = context.getServiceType();
/*     */     
/* 677 */     MessageHeaders headers = receivedMessage.getHeaders();
/* 678 */     String serviceName = headers.getServiceName();
/* 679 */     if (DEBUGGER.isErrorEnable())
/*     */     {
/*     */ 
/* 682 */       StringBuilder routerInfo = new StringBuilder();
/* 683 */       routerInfo.append("Num of services finded from [zookeeper/sr/cache] :");
/*     */       
/* 685 */       routerInfo.append(routingContext.getCacheServiceInstanceSize());
/* 686 */       routerInfo.append(".");
/*     */       
/*     */ 
/* 689 */       routerInfo.append(this.beforeCacheFilterManager.getFilterInfo(routingContext));
/*     */       
/* 691 */       routerInfo.append(this.afterCacheFilterManager.getFilterInfo(routingContext));
/*     */       
/*     */ 
/*     */ 
/* 695 */       routerInfo.append("Service[" + serviceName + "], ");
/* 696 */       routerInfo.append("Group[" + headers.getGroup() + "], ");
/* 697 */       routerInfo.append("Version[" + headers.getVersion() + "] does not have active instance.");
/*     */       
/* 699 */       DEBUGGER.error(routerInfo.toString());
/*     */     }
/*     */     
/* 702 */     throw ExceptionUtilsHolder.getExceptionUtils(serviceType).routeFailedErr(new Object[] { serviceName });
/*     */   }
/*     */   
/*     */ 
/*     */   public void setServiceEngine(ServiceEngine serviceEngine)
/*     */   {
/* 708 */     this.serviceEngine = serviceEngine;
/*     */   }
/*     */   
/*     */ 
/*     */   public void onServiceUpdate(Collection<String> names)
/*     */   {
/* 714 */     synchronized (this.LOCK)
/*     */     {
/* 716 */       Map<String, List<ServiceInstanceInner>> newServiceInstanceCache = new HashMap(this.serviceInstanceCache);
/*     */       
/*     */ 
/* 719 */       for (String name : names)
/*     */       {
/* 721 */         if ("ALL".equals(name))
/*     */         {
/* 723 */           newServiceInstanceCache.clear();
/* 724 */           if (!DEBUGGER.isInfoEnable())
/*     */             break;
/* 726 */           DEBUGGER.info("All service instance has been updated."); break;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 732 */         removeServiceInstanceByName(newServiceInstanceCache, name);
/*     */         
/* 734 */         if (DEBUGGER.isInfoEnable())
/*     */         {
/* 736 */           DEBUGGER.info("Service instance has been updated in " + toString() + ".name:" + name);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 741 */       this.serviceInstanceCache = newServiceInstanceCache;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void removeServiceInstanceByName(Map<String, List<ServiceInstanceInner>> instances, String name)
/*     */   {
/* 748 */     String nameTmp = "#" + name;
/* 749 */     Set<String> keySet = instances.keySet();
/* 750 */     Iterator<String> iterator = keySet.iterator();
/* 751 */     while (iterator.hasNext())
/*     */     {
/* 753 */       String key = (String)iterator.next();
/* 754 */       if (key.endsWith(nameTmp))
/*     */       {
/* 756 */         iterator.remove();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void onProviderConfigUpdate(Collection<String> names)
/*     */   {
/* 764 */     onServiceUpdate(names);
/*     */   }
/*     */   
/*     */ 
/*     */   public void onConsumerConfigUpdate(String zkId, Collection<String> names)
/*     */   {
/* 770 */     synchronized (this.serviceAddressMapperLock)
/*     */     {
/* 772 */       Map<String, ServiceAddress> newMapper = new HashMap(this.serviceAddressMapper);
/*     */       
/*     */ 
/* 775 */       for (String name : names)
/*     */       {
/* 777 */         newMapper.remove(name);
/*     */         
/* 779 */         if (DEBUGGER.isInfoEnable())
/*     */         {
/* 781 */           DEBUGGER.info("Service address has been updated in " + toString() + ".name:" + name);
/*     */         }
/*     */       }
/*     */       
/* 785 */       this.serviceAddressMapper = newMapper;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 792 */     return "routing";
/*     */   }
/*     */   
/*     */ 
/*     */   public int getInWeight()
/*     */   {
/* 798 */     return this.inWeight;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\RoutingInterceptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */