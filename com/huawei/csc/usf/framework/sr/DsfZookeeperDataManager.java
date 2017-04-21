/*      */ package com.huawei.csc.usf.framework.sr;
/*      */ 
/*      */ import com.huawei.csc.kernel.api.log.LogFactory;
/*      */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*      */ import com.huawei.csc.usf.framework.circuitbreaker.DSFFaultToleranceProperties;
/*      */ import com.huawei.csc.usf.framework.common.URL;
/*      */ import com.huawei.csc.usf.framework.degrade.ServiceDegrade;
/*      */ import com.huawei.csc.usf.framework.sr.route.Router;
/*      */ import com.huawei.csc.usf.framework.sr.route.Routers;
/*      */ import com.huawei.csc.usf.framework.util.CopyOnWriteHashMap;
/*      */ import com.huawei.csc.usf.framework.util.Utils;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.net.URLDecoder;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.CopyOnWriteArraySet;
/*      */ import org.apache.commons.lang.StringUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DsfZookeeperDataManager
/*      */ {
/*   46 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(DsfZookeeperDataManager.class);
/*      */   
/*      */ 
/*      */   private String regId;
/*      */   
/*   51 */   private Map<String, Routers> routersMap = new ConcurrentHashMap();
/*      */   
/*      */ 
/*   54 */   private Set<RouterUpdateListener> routerUpdateListeners = new CopyOnWriteArraySet();
/*      */   
/*   56 */   private Map<String, ProviderConfigurationInstanceInner> providerConfigurations = new ConcurrentHashMap();
/*      */   
/*   58 */   private Map<String, ServiceGroup> serviceGroup = new ConcurrentHashMap();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   63 */   private Map<String, ConsumerConfigurationInstanceInner> consumerConfigurations = new ConcurrentHashMap();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   68 */   private Set<ProviderConfigUpdateListener> providerConfigUpdateListeners = new CopyOnWriteArraySet();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   73 */   private Set<ConsumerConfigUpdateListener> consumerConfigUpdateListeners = new CopyOnWriteArraySet();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   78 */   private Map<String, Map<String, ConfigurationParameterInstanceInner>> consumerParameterCache = new ConcurrentHashMap();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   83 */   private Map<String, Map<String, ConfigurationParameterInstanceInner>> providerParameterCache = new ConcurrentHashMap();
/*      */   
/*      */ 
/*      */   private static final String GROUP = "group";
/*      */   
/*      */ 
/*      */   private static final String TPS_THRESHOLD = "tpsThreshold";
/*      */   
/*      */ 
/*      */   private static final String EXECUTES = "executes";
/*      */   
/*   94 */   private Map<String, Integer> weightParameterCache = new CopyOnWriteHashMap();
/*      */   
/*      */ 
/*      */   private static final String TIMEOUT = "timeout";
/*      */   
/*      */ 
/*      */   private static final String METHODTIMEOUT = ".timeout";
/*      */   
/*      */ 
/*      */   private static final String METHOD_CONNECT = ".";
/*      */   
/*      */   private static final String ROUTER = "router";
/*      */   
/*      */   private static final String METHOD_ROUTER = ".router";
/*      */   
/*      */   private static final String MOCK = "mock";
/*      */   
/*      */   private static final String METHODMOCK = ".mock";
/*      */   
/*      */   private static final String ALLMATCHIP = "0.0.0.0";
/*      */   
/*      */   private static final String SIDE = "side";
/*      */   
/*      */   private static final String CONSUMER = "consumer";
/*      */   
/*      */   private static final String PROVIDER = "provider";
/*      */   
/*      */   private static final String FALSE = "false";
/*      */   
/*      */   private static final String ENABLED = "enabled";
/*      */   
/*      */   private static final String APPLICATION = "application";
/*      */   
/*      */   private static final String SERVICEDEGRADE = "serviceDegrade";
/*      */   
/*      */   private static final String CIRCUITBREAKER = "circuitBreaker";
/*      */   
/*      */   private static final String DSF_CRICUTBREAKER_HEALTH_SNAPSHOT_INTERVAL = "circuitBreaker.healthSnapshotIntervalInMilliseconds";
/*      */   
/*      */   private static final String MAXCONCURRENTREQUESTS = "maxConcurrentRequests";
/*      */   
/*      */   private static final String CIRCUITBREAKER_ENABLED = "circuitBreaker.enabled";
/*      */   
/*      */   private static final String CIRCUITBREAKER_REQUESTVOLUMETHRESHOLD = "circuitBreaker.requestVolumeThreshold";
/*      */   
/*      */   private static final String CIRCUITBREAKER_SLEEPWINDOWINMILLISECONDS = "circuitBreaker.sleepWindowInMilliseconds";
/*      */   
/*      */   private static final String CIRCUITBREAKER_ERRORTHRESHOLDPERCENTAGE = "circuitBreaker.errorThresholdPercentage";
/*      */   
/*      */   private static final String BULKHEAD_ISOLATIONSTATEGY = "bulkhead.isolationStategy";
/*      */   
/*      */   private static final String BULKHEAD_THREADNUM = "bulkhead.threadNum";
/*      */   
/*      */   private static final String BULKHEAD_QUEUESIZE = "bulkhead.queueSize";
/*      */   
/*      */   private static final String BULKHEAD_MAXCONCURRENTREQUESTS = "bulkhead.maxConcurrentRequests";
/*      */   
/*      */   private static final String ALL_MATCHER = "ALL";
/*      */   
/*      */   private static final String WEIGHT = "weight";
/*      */   
/*      */   private SRAgentFactory srFactory;
/*      */   
/*      */ 
/*      */   public void setSrFactory(SRAgentFactory srFactory)
/*      */   {
/*  160 */     this.srFactory = srFactory;
/*      */   }
/*      */   
/*      */   public SRAgentFactory getSrFactory()
/*      */   {
/*  165 */     return this.srFactory;
/*      */   }
/*      */   
/*      */ 
/*      */   public void updateRoutersCache(String serviceName, List<String> routersChildren)
/*      */   {
/*  171 */     if (Utils.isEmpty(routersChildren))
/*      */     {
/*  173 */       return;
/*      */     }
/*  175 */     if (LOGGER.isInfoEnable())
/*      */     {
/*  177 */       LOGGER.info("Get routers from zookeeper for service [" + serviceName + "], begin to parse route urls:" + routersChildren);
/*      */     }
/*      */     
/*      */ 
/*  181 */     List<Router> routers = url2Routers(serviceName, routersChildren);
/*  182 */     if ((null == routers) || (routers.isEmpty()))
/*      */     {
/*  184 */       return;
/*      */     }
/*      */     
/*  187 */     Routers routersObj = (Routers)this.routersMap.get(serviceName);
/*  188 */     if (null == routersObj)
/*      */     {
/*  190 */       routersObj = new Routers();
/*  191 */       this.routersMap.put(serviceName, routersObj);
/*      */     }
/*  193 */     routersObj.setRouters(routers);
/*      */     
/*  195 */     List<String> names = new ArrayList();
/*  196 */     names.add(serviceName);
/*  197 */     notifyRouterUpdateListeners(names);
/*      */   }
/*      */   
/*      */ 
/*      */   private List<Router> url2Routers(String serviceName, List<String> routerUrls)
/*      */   {
/*  203 */     List<Router> routers = new ArrayList();
/*  204 */     for (String child : routerUrls)
/*      */     {
/*  206 */       String decodedValue = null;
/*      */       try
/*      */       {
/*  209 */         decodedValue = URLDecoder.decode(child, "UTF-8");
/*      */       }
/*      */       catch (UnsupportedEncodingException e)
/*      */       {
/*  213 */         LOGGER.error("Decode the route url failed, route: " + child, e);
/*      */       }
/*  215 */       if (null != decodedValue)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  220 */         URL url = null;
/*      */         
/*      */         try
/*      */         {
/*  224 */           url = URL.valueOf(decodedValue);
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/*  228 */           LOGGER.error("The url is invalid.", e);
/*      */         }
/*  230 */         if (null != url)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  235 */           if (!StringUtils.equals(serviceName, url.getPath()))
/*      */           {
/*  237 */             LOGGER.error("The url is not match the service: " + serviceName);
/*      */           }
/*      */           else
/*      */           {
/*  241 */             Router router = url2Router(url);
/*  242 */             if (null != router)
/*      */             {
/*  244 */               routers.add(router);
/*      */             }
/*  246 */             if (LOGGER.isInfoEnable())
/*      */             {
/*  248 */               LOGGER.info("Route url [" + child + "] will become effective."); }
/*      */           } }
/*      */       } }
/*  251 */     return routers;
/*      */   }
/*      */   
/*      */   public Router url2Router(URL url)
/*      */   {
/*  256 */     Router router = null;
/*      */     try
/*      */     {
/*  259 */       router = new Router(url);
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*  263 */       LOGGER.error("Convert router url to router error, url: " + url, e);
/*      */     }
/*  265 */     return router;
/*      */   }
/*      */   
/*      */   public List<Router> getMatchedRouters(String serviceName)
/*      */   {
/*  270 */     Routers routersObj = (Routers)this.routersMap.get(serviceName);
/*  271 */     if (null == routersObj)
/*      */     {
/*  273 */       return Collections.emptyList();
/*      */     }
/*      */     
/*  276 */     List<Router> routers = routersObj.getAllRouters();
/*  277 */     if (Utils.isNotEmpty(routers))
/*      */     {
/*  279 */       Collections.sort(routers, new Comparator()
/*      */       {
/*      */ 
/*      */         public int compare(Router o1, Router o2)
/*      */         {
/*      */ 
/*  285 */           return o1.getPriority() > o2.getPriority() ? 1 : o1.getPriority() == o2.getPriority() ? 0 : -1;
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*  291 */     return routers;
/*      */   }
/*      */   
/*      */   public void deleteAllCache()
/*      */   {
/*  296 */     this.routersMap.clear();
/*  297 */     notifyRouterUpdateListeners(this.routersMap.keySet());
/*      */     
/*  299 */     if (LOGGER.isInfoEnable())
/*      */     {
/*  301 */       LOGGER.info("all routers have been cleared !, because /dsf2 node has been deleted. ");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void delRoutersCache(String serviceName)
/*      */   {
/*  308 */     this.routersMap.remove(serviceName);
/*      */     
/*  310 */     List<String> names = new ArrayList();
/*  311 */     names.add(serviceName);
/*  312 */     notifyRouterUpdateListeners(names);
/*  313 */     if (LOGGER.isInfoEnable())
/*      */     {
/*  315 */       LOGGER.info("Routers for [" + serviceName + "] have been removed !");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void deleteAllGroupCache()
/*      */   {
/*  326 */     this.serviceGroup.clear();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void deleteGroupCache(String groupName)
/*      */   {
/*  340 */     this.serviceGroup.remove(groupName);
/*      */   }
/*      */   
/*      */   public void delRoutersCache(String serviceName, List<String> children)
/*      */   {
/*  345 */     Routers routers = (Routers)this.routersMap.get(serviceName);
/*  346 */     if (null == routers)
/*      */     {
/*  348 */       return;
/*      */     }
/*  350 */     List<Router> url2Routers = url2Routers(serviceName, children);
/*  351 */     routers.deleteRouters(url2Routers);
/*  352 */     List<Router> allRouters = routers.getAllRouters();
/*  353 */     if (allRouters.isEmpty())
/*      */     {
/*  355 */       this.routersMap.remove(serviceName);
/*      */     }
/*      */     
/*  358 */     List<String> names = new ArrayList();
/*  359 */     names.add(serviceName);
/*  360 */     notifyRouterUpdateListeners(names);
/*      */   }
/*      */   
/*      */ 
/*      */   public void addRouterUpdateListener(RouterUpdateListener listener)
/*      */   {
/*  366 */     this.routerUpdateListeners.add(listener);
/*      */   }
/*      */   
/*      */   private void notifyRouterUpdateListeners(Collection<String> names)
/*      */   {
/*  371 */     for (RouterUpdateListener listener : this.routerUpdateListeners)
/*      */     {
/*  373 */       listener.onRouterUpdate(names);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void updateGroupCache(List<ServiceGroup> groups)
/*      */   {
/*  389 */     if (Utils.isEmpty(groups))
/*      */     {
/*  391 */       return;
/*      */     }
/*      */     
/*  394 */     for (ServiceGroup group : groups)
/*      */     {
/*      */       try
/*      */       {
/*  398 */         Utils.parseServiceGroup(group);
/*      */       }
/*      */       catch (IllegalArgumentException e)
/*      */       {
/*  402 */         group.setRegGroup(null);
/*  403 */         LOGGER.error(e.getMessage());
/*      */       }
/*  405 */       this.serviceGroup.put(group.getGroupName(), group);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void updateLocalCache(String serviceName, List<String> configurationChildren)
/*      */   {
/*  422 */     if ((StringUtils.isEmpty(serviceName)) || (Utils.isEmpty(configurationChildren)))
/*      */     {
/*      */ 
/*  425 */       return;
/*      */     }
/*      */     
/*  428 */     if (LOGGER.isInfoEnable())
/*      */     {
/*  430 */       LOGGER.info("Get configurations from zookeeper for service [" + serviceName + "], begin to parse config urls:" + configurationChildren);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  435 */     boolean addedProviderConfig = false;
/*  436 */     boolean addedConsumerConfig = false;
/*      */     
/*  438 */     for (String child : configurationChildren)
/*      */     {
/*      */ 
/*  441 */       String confMessage = null;
/*      */       try
/*      */       {
/*  444 */         confMessage = URLDecoder.decode(child, "UTF-8");
/*      */       }
/*      */       catch (UnsupportedEncodingException e)
/*      */       {
/*  448 */         LOGGER.error("When decode this configuration node :[ " + child + "] ,happen exception and decode unsuccessfully.");
/*      */       }
/*  450 */       continue;
/*      */       
/*      */ 
/*      */ 
/*  454 */       ConfigurationInstanceInner instance = Utils.toConfigurationInstanceInner(confMessage);
/*      */       
/*  456 */       if (!serviceName.equals(instance.getServiceName()))
/*      */       {
/*  458 */         LOGGER.error("This configuration node :[ " + confMessage + "] is wrong,because ServiceName is unmatched!");
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*  464 */         Map<String, String> attributes = instance.getAttributes();
/*  465 */         if ("false".equals(attributes.get("enabled")))
/*      */         {
/*  467 */           if (LOGGER.isInfoEnable())
/*      */           {
/*  469 */             LOGGER.info("tThis configuration node :[ " + confMessage + "]   will not  become effective, because : enabled=false");
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*  477 */           String side = (String)attributes.get("side");
/*  478 */           String address = instance.getAddress();
/*  479 */           String application = (String)attributes.get("application");
/*      */           
/*      */ 
/*  482 */           if ((null == side) || ((!"consumer".equals(side)) && (!"provider".equals(side))))
/*      */           {
/*      */ 
/*  485 */             LOGGER.error("This configuration node [ " + confMessage + "] will be pass, because side [" + side + "] is illegal.");
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/*  491 */             if ("consumer".equals(side))
/*      */             {
/*  493 */               ConsumerConfigurationInstanceInner consumerconfig = (ConsumerConfigurationInstanceInner)this.consumerConfigurations.get(serviceName);
/*      */               
/*  495 */               if (consumerconfig == null)
/*      */               {
/*  497 */                 consumerconfig = new ConsumerConfigurationInstanceInner();
/*      */               }
/*      */               
/*      */ 
/*  501 */               if ("0.0.0.0".equals(address))
/*      */               {
/*      */ 
/*  504 */                 if (null == application)
/*      */                 {
/*  506 */                   consumerconfig.setAllMatchConfiguration(instance);
/*      */ 
/*      */                 }
/*      */                 else
/*      */                 {
/*      */ 
/*  512 */                   consumerconfig.putApplicationConfiguration(application, instance);
/*      */ 
/*      */                 }
/*      */                 
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*  520 */                 consumerconfig.putIpConfiguration(address, instance);
/*      */               }
/*      */               
/*      */ 
/*  524 */               this.consumerConfigurations.put(serviceName, consumerconfig);
/*      */               
/*      */ 
/*  527 */               this.consumerParameterCache.remove(serviceName);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  535 */               addedConsumerConfig = true;
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*      */ 
/*  541 */               ProviderConfigurationInstanceInner providerconfig = (ProviderConfigurationInstanceInner)this.providerConfigurations.get(serviceName);
/*      */               
/*  543 */               if (null == providerconfig)
/*      */               {
/*  545 */                 providerconfig = new ProviderConfigurationInstanceInner();
/*      */               }
/*      */               
/*      */ 
/*  549 */               if ("0.0.0.0".equals(address))
/*      */               {
/*      */ 
/*  552 */                 if (null != application)
/*      */                 {
/*  554 */                   providerconfig.putApplicationConfiguration(application, instance);
/*      */ 
/*      */ 
/*      */                 }
/*      */                 else
/*      */                 {
/*      */ 
/*  561 */                   providerconfig.setAllMatchConfiguration(instance);
/*      */ 
/*      */ 
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */               }
/*  569 */               else if (address.contains(":"))
/*      */               {
/*  571 */                 providerconfig.putPortConfiguration(instance.getAddress(), instance);
/*      */ 
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*      */ 
/*  578 */                 providerconfig.putIpConfiguration(instance.getAddress(), instance);
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  584 */               this.providerConfigurations.put(serviceName, providerconfig);
/*      */               
/*      */ 
/*  587 */               this.providerParameterCache.remove(serviceName);
/*      */               
/*      */ 
/*  590 */               addedProviderConfig = true;
/*      */             }
/*  592 */             if (LOGGER.isInfoEnable())
/*      */             {
/*  594 */               LOGGER.info("This configuration node :[ " + confMessage + "] will become effective, because : enabled=true"); }
/*      */           }
/*      */         }
/*      */       } }
/*  598 */     notifyListeners(serviceName, addedProviderConfig, addedConsumerConfig);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void deleteAllConfigCache()
/*      */   {
/*  607 */     notifyProviderConfigUpdateListeners(this.providerConfigurations.keySet());
/*  608 */     notifyConsumerConfigUpdateListeners(this.consumerConfigurations.keySet());
/*      */     
/*      */ 
/*      */ 
/*  612 */     this.providerConfigurations.clear();
/*      */     
/*  614 */     this.consumerConfigurations.clear();
/*      */     
/*      */ 
/*      */ 
/*  618 */     this.providerParameterCache.clear();
/*      */     
/*  620 */     this.consumerParameterCache.clear();
/*      */     
/*  622 */     this.weightParameterCache.clear();
/*      */     
/*  624 */     if (LOGGER.isInfoEnable())
/*      */     {
/*  626 */       LOGGER.info("all configurations have been cleared !, because : dsf2 node has been deleted. ");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void delConfigurationCache(String serviceName)
/*      */   {
/*  640 */     this.providerConfigurations.remove(serviceName);
/*      */     
/*  642 */     this.consumerConfigurations.remove(serviceName);
/*      */     
/*      */ 
/*      */ 
/*  646 */     this.providerParameterCache.remove(serviceName);
/*      */     
/*  648 */     this.consumerParameterCache.remove(serviceName);
/*  649 */     if (serviceName.equals("ALL"))
/*      */     {
/*  651 */       this.weightParameterCache.clear();
/*      */     }
/*      */     
/*  654 */     List<String> names = new ArrayList();
/*  655 */     names.add(serviceName);
/*  656 */     notifyProviderConfigUpdateListeners(names);
/*  657 */     notifyConsumerConfigUpdateListeners(names);
/*      */     
/*  659 */     if (LOGGER.isInfoEnable())
/*      */     {
/*  661 */       LOGGER.info("All conifguraion caches  about " + serviceName + " hava been removed!");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void deleteCache(String serviceName, List<String> delList)
/*      */   {
/*  675 */     if ((StringUtils.isEmpty(serviceName)) || (Utils.isEmpty(delList)))
/*      */     {
/*  677 */       return;
/*      */     }
/*  679 */     if (LOGGER.isInfoEnable())
/*      */     {
/*  681 */       LOGGER.info("Zk configurations have been removed for service [" + serviceName + "], begin to parse config urls:" + delList);
/*      */     }
/*      */     
/*  684 */     boolean deletedProviderConfig = false;
/*  685 */     boolean addedConsumerConfig = false;
/*      */     
/*  687 */     for (String del : delList)
/*      */     {
/*      */ 
/*  690 */       String configuration = null;
/*      */       try
/*      */       {
/*  693 */         configuration = URLDecoder.decode(del, "UTF-8");
/*      */       }
/*      */       catch (UnsupportedEncodingException e)
/*      */       {
/*  697 */         LOGGER.error("when decode this configuration node :[ " + del + "] ,happen exception and decode unsuccessfully.");
/*      */       }
/*  699 */       continue;
/*      */       
/*      */ 
/*      */ 
/*  703 */       ConfigurationInstanceInner instance = Utils.toConfigurationInstanceInner(configuration);
/*      */       
/*  705 */       if (!serviceName.equals(instance.getServiceName()))
/*      */       {
/*  707 */         LOGGER.error("this configuration node :[ " + configuration + "] is wrong,because ServiceName is unmatched!");
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*  713 */         Map<String, String> attributes = instance.getAttributes();
/*  714 */         if (!"false".equals(attributes.get("enabled")))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  720 */           String side = (String)attributes.get("side");
/*  721 */           String address = instance.getAddress();
/*  722 */           String application = (String)attributes.get("application");
/*      */           
/*      */ 
/*  725 */           if ("consumer".equals(side))
/*      */           {
/*  727 */             ConsumerConfigurationInstanceInner consumerconfig = (ConsumerConfigurationInstanceInner)this.consumerConfigurations.get(serviceName);
/*      */             
/*  729 */             if (null != consumerconfig)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  735 */               if ("0.0.0.0".equals(address))
/*      */               {
/*      */ 
/*  738 */                 if (null == application)
/*      */                 {
/*  740 */                   consumerconfig.delAllMatchConfiguration();
/*      */ 
/*      */                 }
/*      */                 else
/*      */                 {
/*      */ 
/*  746 */                   consumerconfig.delApplicationConfiguration(application);
/*      */                 }
/*      */                 
/*      */ 
/*      */               }
/*      */               else {
/*  752 */                 consumerconfig.delIpConfiguration(address);
/*      */               }
/*      */               
/*      */ 
/*  756 */               if ((null == consumerconfig.getAllMatchConfiguration()) && (consumerconfig.getApplicationConfigurations().isEmpty()) && (consumerconfig.getIpConfigurations().isEmpty()))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*  761 */                 this.consumerConfigurations.remove(serviceName);
/*      */               }
/*      */               else
/*      */               {
/*  765 */                 this.consumerConfigurations.put(serviceName, consumerconfig);
/*      */               }
/*      */               
/*      */ 
/*  769 */               this.consumerParameterCache.remove(serviceName);
/*      */               
/*  771 */               addedConsumerConfig = true;
/*      */               
/*  773 */               if (LOGGER.isInfoEnable())
/*      */               {
/*  775 */                 LOGGER.info("this configuration node:" + configuration + " has been deleted !");
/*      */               }
/*      */               
/*      */             }
/*      */             
/*      */           }
/*  781 */           else if ("provider".equals(side))
/*      */           {
/*  783 */             ProviderConfigurationInstanceInner providerconfig = (ProviderConfigurationInstanceInner)this.providerConfigurations.get(serviceName);
/*      */             
/*  785 */             if (null != providerconfig)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  791 */               if ("0.0.0.0".equals(address))
/*      */               {
/*      */ 
/*  794 */                 if (null == application)
/*      */                 {
/*  796 */                   providerconfig.delAllMatchConfiguration();
/*      */ 
/*      */                 }
/*      */                 else
/*      */                 {
/*      */ 
/*  802 */                   providerconfig.delApplicationConfiguration(application);
/*      */ 
/*      */ 
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */               }
/*  810 */               else if (address.contains(":"))
/*      */               {
/*  812 */                 providerconfig.delPortConfiguration(address);
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*      */ 
/*  818 */                 providerconfig.delIpConfiguration(address);
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*  823 */               if ((null == providerconfig.getAllMatchConfiguration()) && (providerconfig.getApplicationConfigurations().isEmpty()) && (providerconfig.getIpConfigurations().isEmpty()) && (providerconfig.getPortConfigurations().isEmpty()))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  829 */                 this.providerConfigurations.remove(serviceName);
/*      */               }
/*      */               else
/*      */               {
/*  833 */                 this.providerConfigurations.put(serviceName, providerconfig);
/*      */               }
/*      */               
/*      */ 
/*  837 */               if ("ALL".equals(serviceName))
/*      */               {
/*  839 */                 Iterator<Map.Entry<String, Map<String, ConfigurationParameterInstanceInner>>> it = this.providerParameterCache.entrySet().iterator();
/*      */                 
/*  841 */                 while (it.hasNext())
/*      */                 {
/*  843 */                   Map.Entry<String, Map<String, ConfigurationParameterInstanceInner>> next = (Map.Entry)it.next();
/*  844 */                   if (((String)next.getKey()).contains("ALL"))
/*      */                   {
/*  846 */                     it.remove();
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*  851 */               this.providerParameterCache.remove(serviceName);
/*      */               
/*      */ 
/*  854 */               deletedProviderConfig = true;
/*      */               
/*  856 */               if (LOGGER.isInfoEnable())
/*      */               {
/*  858 */                 LOGGER.info("this configuration node:" + configuration + " has been deleted !"); }
/*      */             }
/*      */           }
/*      */         }
/*      */       } }
/*  863 */     notifyListeners(serviceName, deletedProviderConfig, addedConsumerConfig);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void notifyListeners(String serviceName, boolean providerConfig, boolean consumerConfig)
/*      */   {
/*  870 */     if (providerConfig)
/*      */     {
/*  872 */       if (LOGGER.isInfoEnable())
/*      */       {
/*  874 */         LOGGER.info("Provider configurations for service [" + serviceName + "] have chanaged, begin to notify listeners");
/*      */       }
/*      */       
/*      */ 
/*  878 */       pushWeight();
/*  879 */       List<String> names = new ArrayList();
/*  880 */       names.add(serviceName);
/*  881 */       notifyProviderConfigUpdateListeners(names);
/*      */     }
/*      */     
/*      */ 
/*  885 */     if (consumerConfig)
/*      */     {
/*  887 */       if (LOGGER.isInfoEnable())
/*      */       {
/*  889 */         LOGGER.info("Consumer configurations for service [" + serviceName + "] have chanaged, begin to notify listeners");
/*      */       }
/*      */       
/*      */ 
/*  893 */       List<String> names = new ArrayList();
/*  894 */       names.add(serviceName);
/*  895 */       notifyConsumerConfigUpdateListeners(names);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object getConfigFromConfiguration(String service, String operation, String host, String dsfApplication, String serviceStyle, String side)
/*      */   {
/*  930 */     Object object = null;
/*      */     
/*  932 */     if (!"group".equals(serviceStyle))
/*      */     {
/*  934 */       object = getFromCache(service, operation, side, serviceStyle);
/*      */     }
/*      */     else
/*      */     {
/*  938 */       String loaclListenAddress = host.split("\\:")[0];
/*  939 */       String newService = service + operation + host;
/*      */       
/*  941 */       object = getFromCache(newService, operation, side, serviceStyle);
/*  942 */       if (object == null)
/*      */       {
/*  944 */         newService = service + operation + loaclListenAddress;
/*  945 */         object = getFromCache(newService, operation, side, serviceStyle);
/*      */       }
/*  947 */       if (object == null)
/*      */       {
/*  949 */         newService = service + operation + "0.0.0.0";
/*  950 */         object = getFromCache(newService, operation, side, serviceStyle);
/*      */       }
/*      */     }
/*      */     
/*  954 */     if (object != null)
/*      */     {
/*  956 */       if (LOGGER.isDebugEnable())
/*      */       {
/*  958 */         String info = " " + serviceStyle + " : ";
/*  959 */         StringBuilder sb = new StringBuilder();
/*  960 */         sb.append("get the config of ");
/*  961 */         sb.append(serviceStyle);
/*  962 */         sb.append("from ");
/*  963 */         sb.append(side);
/*  964 */         sb.append(" ParameterCache! serviceName: ");
/*  965 */         sb.append(service);
/*  966 */         sb.append(", operation:");
/*  967 */         sb.append(operation);
/*  968 */         if ("maxConcurrentRequests".equals(serviceStyle))
/*      */         {
/*  970 */           sb.append(info + String.valueOf(object));
/*      */         }
/*  972 */         else if ("group".equals(serviceStyle))
/*      */         {
/*  974 */           sb.append(info + object);
/*      */         }
/*  976 */         else if (("tpsThreshold".equals(serviceStyle)) || ("executes".equals(serviceStyle)))
/*      */         {
/*      */ 
/*  979 */           sb.append(info + String.valueOf(object));
/*      */         }
/*  981 */         else if ("timeout".equals(serviceStyle))
/*      */         {
/*  983 */           sb.append(info + String.valueOf(object));
/*      */         }
/*  985 */         else if ("router".equals(serviceStyle))
/*      */         {
/*  987 */           sb.append(info + String.valueOf(object));
/*      */         }
/*  989 */         LOGGER.debug(sb.toString());
/*      */       }
/*  991 */       return object;
/*      */     }
/*      */     
/*  994 */     ConsumerConfigurationInstanceInner consumerconfig = (ConsumerConfigurationInstanceInner)this.consumerConfigurations.get(service);
/*      */     
/*  996 */     ProviderConfigurationInstanceInner providerConfig = (ProviderConfigurationInstanceInner)this.providerConfigurations.get(service);
/*      */     
/*  998 */     if ("consumer".equals(side))
/*      */     {
/* 1000 */       if (null == consumerconfig)
/*      */       {
/* 1002 */         return object;
/*      */       }
/*      */       
/*      */ 
/* 1006 */       if (StringUtils.isNotBlank(host))
/*      */       {
/*      */ 
/* 1009 */         Map<String, ConfigurationInstanceInner> ipConfiguration = consumerconfig.getIpConfigurations();
/*      */         
/* 1011 */         String loaclListenAddress = host.split("\\:")[0];
/* 1012 */         ConfigurationInstanceInner localConfiguration = (ConfigurationInstanceInner)ipConfiguration.get(loaclListenAddress);
/*      */         
/* 1014 */         if (null != localConfiguration)
/*      */         {
/* 1016 */           object = getDataFromConfiguration(localConfiguration, service, operation, host, null, "consumer", serviceStyle);
/*      */           
/*      */ 
/* 1019 */           if (null != object)
/*      */           {
/* 1021 */             return object;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1027 */       Map<String, ConfigurationInstanceInner> allMatchConfigurationApp = consumerconfig.getApplicationConfigurations();
/*      */       
/* 1029 */       ConfigurationInstanceInner applicationConfiguration = (ConfigurationInstanceInner)allMatchConfigurationApp.get(dsfApplication);
/*      */       
/* 1031 */       if (null != applicationConfiguration)
/*      */       {
/* 1033 */         object = getDataFromConfiguration(applicationConfiguration, service, operation, "0.0.0.0", dsfApplication, "consumer", serviceStyle);
/*      */         
/*      */ 
/*      */ 
/* 1037 */         if (null != object)
/*      */         {
/* 1039 */           return object;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1044 */       ConfigurationInstanceInner allMatchConfiguration = consumerconfig.getAllMatchConfiguration();
/*      */       
/* 1046 */       if (allMatchConfiguration != null)
/*      */       {
/* 1048 */         object = getDataFromConfiguration(allMatchConfiguration, service, operation, "0.0.0.0", null, "consumer", serviceStyle);
/*      */         
/*      */ 
/*      */ 
/* 1052 */         if (null != object)
/*      */         {
/* 1054 */           return object;
/*      */         }
/*      */       }
/*      */     }
/* 1058 */     else if ("provider".equals(side))
/*      */     {
/* 1060 */       if (null == providerConfig)
/*      */       {
/* 1062 */         return object;
/*      */       }
/* 1064 */       if (StringUtils.isNotBlank(host))
/*      */       {
/*      */ 
/* 1067 */         Map<String, ConfigurationInstanceInner> portConfiguration = providerConfig.getPortConfigurations();
/*      */         
/* 1069 */         ConfigurationInstanceInner localPortConfiguration = (ConfigurationInstanceInner)portConfiguration.get(host);
/*      */         
/* 1071 */         if (null != localPortConfiguration)
/*      */         {
/* 1073 */           object = getDataFromConfiguration(localPortConfiguration, service, operation, host, null, "provider", serviceStyle);
/*      */           
/*      */ 
/*      */ 
/* 1077 */           if (null != object)
/*      */           {
/* 1079 */             return object;
/*      */           }
/*      */         }
/* 1082 */         Map<String, ConfigurationInstanceInner> ipConfiguration = providerConfig.getIpConfigurations();
/*      */         
/* 1084 */         String loaclListenAddress = host.split("\\:")[0];
/* 1085 */         ConfigurationInstanceInner localIpConfiguration = (ConfigurationInstanceInner)ipConfiguration.get(loaclListenAddress);
/*      */         
/* 1087 */         if (null != localIpConfiguration)
/*      */         {
/* 1089 */           object = getDataFromConfiguration(localIpConfiguration, service, operation, host, null, "provider", serviceStyle);
/*      */           
/*      */ 
/* 1092 */           if (null != object)
/*      */           {
/* 1094 */             return object;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1100 */       Map<String, ConfigurationInstanceInner> allMatchConfigurationApp = providerConfig.getApplicationConfigurations();
/*      */       
/* 1102 */       ConfigurationInstanceInner applicationConfiguration = (ConfigurationInstanceInner)allMatchConfigurationApp.get(dsfApplication);
/*      */       
/* 1104 */       if (null != applicationConfiguration)
/*      */       {
/* 1106 */         object = getDataFromConfiguration(applicationConfiguration, service, operation, " 0.0.0.0 ", dsfApplication, "consumer", serviceStyle);
/*      */         
/*      */ 
/* 1109 */         if (null != object)
/*      */         {
/* 1111 */           return object;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1116 */       ConfigurationInstanceInner allMatchConfiguration = providerConfig.getAllMatchConfiguration();
/*      */       
/* 1118 */       if (null != allMatchConfiguration)
/*      */       {
/* 1120 */         object = getDataFromConfiguration(allMatchConfiguration, service, operation, " 0.0.0.0 ", null, "consumer", serviceStyle);
/*      */         
/*      */ 
/* 1123 */         if (null != object)
/*      */         {
/* 1125 */           return object;
/*      */         }
/*      */       }
/*      */     }
/* 1129 */     return object;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private Object getDataFromConfiguration(ConfigurationInstanceInner configuration, String service, String operation, String type, String dsfApplication, String side, String serviceStyle)
/*      */   {
/* 1137 */     Object config = null;
/* 1138 */     String info = "," + serviceStyle + "=";
/* 1139 */     if ("serviceDegrade".equals(serviceStyle))
/*      */     {
/* 1141 */       config = commonServiceDegrade(configuration, service, operation);
/*      */     }
/* 1143 */     else if ("maxConcurrentRequests".equals(serviceStyle))
/*      */     {
/* 1145 */       config = commonMethod(configuration, service, operation, "bulkhead.maxConcurrentRequests", null);
/*      */       
/* 1147 */       info = info + String.valueOf(config);
/*      */     }
/* 1149 */     else if ("circuitBreaker".equals(serviceStyle))
/*      */     {
/* 1151 */       config = commonCircuitBreaker(configuration, service);
/*      */     }
/* 1153 */     else if ("group".equals(serviceStyle))
/*      */     {
/* 1155 */       config = commonGroup(configuration, "group");
/* 1156 */       info = info + config;
/*      */     }
/* 1158 */     else if (("tpsThreshold".equals(serviceStyle)) || ("executes".equals(serviceStyle)))
/*      */     {
/*      */ 
/* 1161 */       config = getThreshold(serviceStyle, operation, configuration, serviceStyle);
/*      */       
/* 1163 */       info = info + String.valueOf(config);
/*      */     }
/* 1165 */     else if ("timeout".equals(serviceStyle))
/*      */     {
/* 1167 */       config = getTimeout(serviceStyle, operation, configuration);
/* 1168 */       info = info + String.valueOf(config);
/*      */     }
/* 1170 */     else if ("router".equals(serviceStyle))
/*      */     {
/* 1172 */       config = commonMethod(configuration, service, operation, "router", ".router");
/*      */       
/* 1174 */       info = info + String.valueOf(config);
/*      */     }
/* 1176 */     if (config != null)
/*      */     {
/* 1178 */       if (LOGGER.isDebugEnable())
/*      */       {
/*      */ 
/* 1181 */         StringBuilder sb = new StringBuilder();
/* 1182 */         sb.append("get the config about " + serviceStyle + " from ");
/* 1183 */         sb.append(type);
/* 1184 */         sb.append(" configuration node! ");
/* 1185 */         sb.append(" serviceName: ");
/* 1186 */         sb.append(service);
/* 1187 */         sb.append(", operation: ");
/* 1188 */         sb.append(operation);
/*      */         
/* 1190 */         if (StringUtils.isNotBlank(dsfApplication))
/*      */         {
/* 1192 */           sb.append(" , application: ");
/* 1193 */           sb.append(dsfApplication);
/*      */         }
/* 1195 */         sb.append(info);
/* 1196 */         LOGGER.debug(sb.toString());
/*      */       }
/* 1198 */       if (("ALL".equals(service)) && ("group".equals(serviceStyle)))
/*      */       {
/* 1200 */         service = service + operation + type;
/*      */       }
/* 1202 */       addToCache(service, operation, side, serviceStyle, config);
/*      */     }
/* 1204 */     return config;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private ServiceDegrade commonServiceDegrade(ConfigurationInstanceInner configuration, String service, String operation)
/*      */   {
/* 1211 */     boolean methodForceReturnNull = false;
/* 1212 */     boolean methodForceReturnException = false;
/* 1213 */     boolean methodForceReturnMockService = false;
/* 1214 */     boolean methodFailReturnNull = false;
/* 1215 */     boolean methodFalseReturn = false;
/* 1216 */     boolean forceReturnNull = false;
/* 1217 */     boolean forceReturnException = false;
/* 1218 */     boolean forceReturnMockService = false;
/* 1219 */     boolean failReturnNull = false;
/* 1220 */     boolean falseReturn = false;
/*      */     
/* 1222 */     ServiceDegrade zkconfigServiceDegrade = null;
/*      */     
/* 1224 */     if (configuration.getAttributes().containsKey(operation + ".mock"))
/*      */     {
/*      */ 
/* 1227 */       String temp = (String)configuration.getAttributes().get(operation + ".mock");
/*      */       
/* 1229 */       if (null != temp)
/*      */       {
/*      */         try
/*      */         {
/* 1233 */           temp = URLDecoder.decode(temp, "UTF-8");
/*      */         }
/*      */         catch (UnsupportedEncodingException e)
/*      */         {
/* 1237 */           LOGGER.error("when decode information of " + operation + ".mock" + " :[ " + temp + "] ,happen exception and decode unsuccessfully.");
/*      */           
/*      */ 
/* 1240 */           return zkconfigServiceDegrade;
/*      */         }
/* 1242 */         zkconfigServiceDegrade = new ServiceDegrade();
/* 1243 */         if (temp.contains("force:return null"))
/*      */         {
/* 1245 */           methodForceReturnNull = true;
/*      */         }
/* 1247 */         else if (temp.contains("force:return exception"))
/*      */         {
/* 1249 */           methodForceReturnException = true;
/* 1250 */           zkconfigServiceDegrade.setExceptionCode(getString(temp));
/*      */         }
/* 1252 */         else if (temp.contains("force:return mockservice"))
/*      */         {
/* 1254 */           methodForceReturnMockService = true;
/* 1255 */           zkconfigServiceDegrade.setMockServiceName(getString(temp));
/*      */         }
/* 1257 */         else if (temp.contains("fail:return null"))
/*      */         {
/* 1259 */           methodFailReturnNull = true;
/*      */         }
/* 1261 */         else if (temp.contains("false"))
/*      */         {
/* 1263 */           methodFalseReturn = true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1269 */     if (zkconfigServiceDegrade == null)
/*      */     {
/* 1271 */       if (configuration.getAttributes().containsKey("mock"))
/*      */       {
/*      */ 
/* 1274 */         String temp = (String)configuration.getAttributes().get("mock");
/* 1275 */         if (null != temp)
/*      */         {
/*      */           try
/*      */           {
/* 1279 */             temp = URLDecoder.decode(temp, "UTF-8");
/*      */           }
/*      */           catch (UnsupportedEncodingException e)
/*      */           {
/* 1283 */             LOGGER.error("when decode information of mock :[ " + temp + "] ,happen exception and decode unsuccessfully.");
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 1288 */             return zkconfigServiceDegrade;
/*      */           }
/* 1290 */           zkconfigServiceDegrade = new ServiceDegrade();
/* 1291 */           if (temp.contains("force:return null"))
/*      */           {
/* 1293 */             forceReturnNull = true;
/*      */           }
/* 1295 */           else if (temp.contains("force:return exception"))
/*      */           {
/* 1297 */             forceReturnException = true;
/* 1298 */             zkconfigServiceDegrade.setExceptionCode(getString(temp));
/*      */ 
/*      */           }
/* 1301 */           else if (temp.contains("force:return mockservice"))
/*      */           {
/* 1303 */             forceReturnMockService = true;
/* 1304 */             zkconfigServiceDegrade.setMockServiceName(getString(temp));
/*      */ 
/*      */           }
/* 1307 */           else if (temp.contains("fail:return null"))
/*      */           {
/* 1309 */             failReturnNull = true;
/*      */           }
/* 1311 */           else if (temp.contains("false"))
/*      */           {
/* 1313 */             falseReturn = true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1319 */     if (null == zkconfigServiceDegrade)
/*      */     {
/* 1321 */       return null;
/*      */     }
/* 1323 */     zkconfigServiceDegrade.setForceReturnException(forceReturnException);
/* 1324 */     zkconfigServiceDegrade.setForceReturnMockService(forceReturnMockService);
/*      */     
/* 1326 */     zkconfigServiceDegrade.setForceReturnNull(forceReturnNull);
/* 1327 */     zkconfigServiceDegrade.setFailReturnNull(failReturnNull);
/*      */     
/* 1329 */     zkconfigServiceDegrade.setMethodForceReturnException(methodForceReturnException);
/*      */     
/* 1331 */     zkconfigServiceDegrade.setMethodForceReturnMockService(methodForceReturnMockService);
/*      */     
/* 1333 */     zkconfigServiceDegrade.setMethodForceReturnNull(methodForceReturnNull);
/* 1334 */     zkconfigServiceDegrade.setMethodFailReturnNull(methodFailReturnNull);
/*      */     
/* 1336 */     zkconfigServiceDegrade.setFalseReturn(falseReturn);
/* 1337 */     zkconfigServiceDegrade.setMethodFalseReturn(methodFalseReturn);
/*      */     
/* 1339 */     return zkconfigServiceDegrade;
/*      */   }
/*      */   
/*      */ 
/*      */   private DSFFaultToleranceProperties commonCircuitBreaker(ConfigurationInstanceInner configuration, String service)
/*      */   {
/* 1345 */     String circuitBreakerEnable = null;
/*      */     
/* 1347 */     Integer healthSnapshotInterval = null;
/* 1348 */     Integer requestVolumeThreadHold = null;
/*      */     
/* 1350 */     Integer errThreadHoldPercentage = null;
/*      */     
/* 1352 */     Integer sleepWindow = null;
/*      */     
/* 1354 */     String isolationStategy = null;
/*      */     
/* 1356 */     Integer bulkheadThreadNum = null;
/*      */     
/* 1358 */     Integer bulkheadQueueSize = null;
/*      */     
/* 1360 */     Integer bulkheadMaxConcurrentRequests = null;
/*      */     
/* 1362 */     DSFFaultToleranceProperties circuitBreakerProperties = new DSFFaultToleranceProperties();
/*      */     
/* 1364 */     Map<String, String> attributes = configuration.getAttributes();
/*      */     
/* 1366 */     if (null != attributes.get("circuitBreaker.enabled"))
/*      */     {
/* 1368 */       circuitBreakerEnable = getBooleanVaule((String)attributes.get("circuitBreaker.enabled"));
/*      */       
/*      */ 
/* 1371 */       if (null != circuitBreakerEnable)
/*      */       {
/* 1373 */         circuitBreakerProperties.setCircuitBreakerEnable(circuitBreakerEnable);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1378 */     if (null != attributes.get("circuitBreaker.healthSnapshotIntervalInMilliseconds"))
/*      */     {
/* 1380 */       healthSnapshotInterval = getPositiveIntegerValue((String)attributes.get("circuitBreaker.healthSnapshotIntervalInMilliseconds"));
/*      */       
/* 1382 */       if (null != healthSnapshotInterval)
/*      */       {
/* 1384 */         circuitBreakerProperties.setHealthSnapshotInterval(healthSnapshotInterval);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1390 */     if (null != attributes.get("circuitBreaker.requestVolumeThreshold"))
/*      */     {
/* 1392 */       requestVolumeThreadHold = getPositiveIntegerValue((String)attributes.get("circuitBreaker.requestVolumeThreshold"));
/*      */       
/* 1394 */       if (null != requestVolumeThreadHold)
/*      */       {
/* 1396 */         circuitBreakerProperties.setRequestVolumeThreadHold(requestVolumeThreadHold);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1401 */     if (null != attributes.get("circuitBreaker.sleepWindowInMilliseconds"))
/*      */     {
/* 1403 */       sleepWindow = getPositiveIntegerValue((String)attributes.get("circuitBreaker.sleepWindowInMilliseconds"));
/*      */       
/* 1405 */       if (null != sleepWindow)
/*      */       {
/* 1407 */         circuitBreakerProperties.setSleepWindow(sleepWindow);
/*      */       }
/*      */     }
/*      */     
/* 1411 */     if (null != attributes.get("circuitBreaker.errorThresholdPercentage"))
/*      */     {
/* 1413 */       errThreadHoldPercentage = getPositiveIntegerValue((String)attributes.get("circuitBreaker.errorThresholdPercentage"));
/*      */       
/* 1415 */       if (null != errThreadHoldPercentage)
/*      */       {
/* 1417 */         circuitBreakerProperties.setErrThreadHoldPercentage(errThreadHoldPercentage);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1422 */     if (null != attributes.get("bulkhead.isolationStategy"))
/*      */     {
/* 1424 */       isolationStategy = getisolationStategyVaule((String)attributes.get("bulkhead.isolationStategy"));
/*      */       
/* 1426 */       if (null != isolationStategy)
/*      */       {
/* 1428 */         circuitBreakerProperties.setIsolationStategy(isolationStategy);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1433 */     if (null != attributes.get("bulkhead.threadNum"))
/*      */     {
/* 1435 */       bulkheadThreadNum = getPositiveIntegerValue((String)attributes.get("bulkhead.threadNum"));
/*      */       
/* 1437 */       if (null != bulkheadThreadNum)
/*      */       {
/* 1439 */         circuitBreakerProperties.setBulkheadThreadNum(bulkheadThreadNum);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1444 */     if (null != attributes.get("bulkhead.maxConcurrentRequests"))
/*      */     {
/* 1446 */       bulkheadMaxConcurrentRequests = getPositiveIntegerValue((String)attributes.get("bulkhead.maxConcurrentRequests"));
/*      */       
/* 1448 */       if (null != bulkheadMaxConcurrentRequests)
/*      */       {
/* 1450 */         circuitBreakerProperties.setBulkheadMaxConcurrentRequests(bulkheadMaxConcurrentRequests);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1456 */     if (null != attributes.get("bulkhead.queueSize"))
/*      */     {
/* 1458 */       bulkheadQueueSize = getPositiveIntegerValue((String)attributes.get("bulkhead.queueSize"));
/*      */       
/* 1460 */       if (null != bulkheadQueueSize)
/*      */       {
/* 1462 */         circuitBreakerProperties.setBulkheadQueueSize(bulkheadQueueSize);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1469 */     return circuitBreakerProperties;
/*      */   }
/*      */   
/*      */ 
/*      */   private String getString(String str)
/*      */   {
/* 1475 */     String str1 = str.split("\\(")[1];
/* 1476 */     str1 = str1.substring(1, str1.length() - 2);
/* 1477 */     return str1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private Integer getThreshold(String service, String operation, ConfigurationInstanceInner configurationInstance, String thresholdType)
/*      */   {
/* 1484 */     String excutesValue = commonMethod(configurationInstance, service, operation, thresholdType, "." + thresholdType);
/*      */     
/*      */ 
/* 1487 */     if (null == excutesValue)
/*      */     {
/* 1489 */       return null;
/*      */     }
/* 1491 */     return getIntegerValue(excutesValue);
/*      */   }
/*      */   
/*      */ 
/*      */   private Long getTimeout(String service, String operation, ConfigurationInstanceInner localConfiguration)
/*      */   {
/* 1497 */     String timeoutValue = commonMethod(localConfiguration, service, operation, "timeout", ".timeout");
/*      */     
/* 1499 */     if (null == timeoutValue)
/*      */     {
/* 1501 */       return null;
/*      */     }
/* 1503 */     return getLongValue(timeoutValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String commonMethod(ConfigurationInstanceInner Configuration, String service, String operation, String serviceStyle, String operationType)
/*      */   {
/* 1510 */     String operationKey = null;
/* 1511 */     if (null == operationType)
/*      */     {
/* 1513 */       operationKey = operation;
/*      */     }
/*      */     else
/*      */     {
/* 1517 */       operationKey = operation + operationType;
/*      */     }
/*      */     
/* 1520 */     String operationValue = (String)Configuration.getAttributes().get(operationKey);
/* 1521 */     if (null != operationValue)
/*      */     {
/* 1523 */       return operationValue;
/*      */     }
/* 1525 */     String serviceValue = (String)Configuration.getAttributes().get(serviceStyle);
/* 1526 */     return serviceValue;
/*      */   }
/*      */   
/*      */   private String commonGroup(ConfigurationInstanceInner config, String type)
/*      */   {
/* 1531 */     String group = config.getAttribute(type);
/* 1532 */     return group;
/*      */   }
/*      */   
/*      */   private String getisolationStategyVaule(String temp)
/*      */   {
/* 1537 */     if (null == temp)
/*      */     {
/* 1539 */       return null;
/*      */     }
/* 1541 */     if ("SEMAPHORE".equals(temp))
/*      */     {
/* 1543 */       return temp;
/*      */     }
/*      */     
/*      */ 
/* 1547 */     if (LOGGER.isWarnEnable())
/*      */     {
/* 1549 */       LOGGER.warn("Can not get the isolationStategy value, the original string is: " + temp);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1554 */     return null;
/*      */   }
/*      */   
/*      */   private String getBooleanVaule(String temp)
/*      */   {
/* 1559 */     if (null == temp)
/*      */     {
/* 1561 */       return null;
/*      */     }
/*      */     
/* 1564 */     if (("true".equals(temp)) || ("false".equals(temp)))
/*      */     {
/* 1566 */       return temp;
/*      */     }
/*      */     
/*      */ 
/* 1570 */     if (LOGGER.isWarnEnable())
/*      */     {
/* 1572 */       LOGGER.warn("Can not get the boolean value, the original string is: " + temp);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1577 */     return null;
/*      */   }
/*      */   
/*      */   private Long getLongValue(String temp)
/*      */   {
/* 1582 */     Long vaule = null;
/*      */     try
/*      */     {
/* 1585 */       vaule = Long.valueOf(temp);
/*      */     }
/*      */     catch (NumberFormatException exception)
/*      */     {
/* 1589 */       if (LOGGER.isWarnEnable())
/*      */       {
/* 1591 */         LOGGER.warn("Can not get the Long value, the original string is: " + temp);
/*      */       }
/*      */       
/* 1594 */       return null;
/*      */     }
/* 1596 */     return vaule;
/*      */   }
/*      */   
/*      */   private Integer getPositiveIntegerValue(String temp)
/*      */   {
/* 1601 */     Integer vaule = getIntegerValue(temp);
/* 1602 */     if ((vaule != null) && (vaule.intValue() <= 0))
/*      */     {
/* 1604 */       if (LOGGER.isWarnEnable())
/*      */       {
/* 1606 */         LOGGER.warn("Can not get the Positive Integer value, the original string is: " + temp);
/*      */       }
/*      */       
/* 1609 */       return null;
/*      */     }
/* 1611 */     return vaule;
/*      */   }
/*      */   
/*      */   private Integer getIntegerValue(String temp)
/*      */   {
/* 1616 */     Integer vaule = null;
/*      */     try
/*      */     {
/* 1619 */       vaule = Integer.valueOf(temp);
/*      */     }
/*      */     catch (NumberFormatException exception)
/*      */     {
/* 1623 */       if (LOGGER.isWarnEnable())
/*      */       {
/* 1625 */         LOGGER.warn("Can not get the Integer value, the original string is: " + temp);
/*      */       }
/*      */       
/* 1628 */       return null;
/*      */     }
/* 1630 */     return vaule;
/*      */   }
/*      */   
/*      */ 
/*      */   private Object getFromCache(String service, String operation, String side, String serviceStyle)
/*      */   {
/* 1636 */     Object value = null;
/* 1637 */     Map<String, ConfigurationParameterInstanceInner> serviceParameter = null;
/* 1638 */     if (side.equals("consumer"))
/*      */     {
/* 1640 */       serviceParameter = (Map)this.consumerParameterCache.get(service);
/*      */     }
/* 1642 */     else if (side.equals("provider"))
/*      */     {
/* 1644 */       serviceParameter = (Map)this.providerParameterCache.get(service);
/*      */     }
/*      */     
/* 1647 */     if (serviceParameter != null)
/*      */     {
/* 1649 */       ConfigurationParameterInstanceInner operationParameter = (ConfigurationParameterInstanceInner)serviceParameter.get(operation);
/*      */       
/* 1651 */       if (operationParameter != null)
/*      */       {
/* 1653 */         value = operationParameter.getParameter(serviceStyle);
/* 1654 */         if (value != null)
/*      */         {
/* 1656 */           if (LOGGER.isDebugEnable())
/*      */           {
/* 1658 */             LOGGER.debug("Get the config of " + serviceStyle + " from parameter cache!" + "serviceName: " + service + ", operation: " + operation);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1666 */     return value;
/*      */   }
/*      */   
/*      */ 
/*      */   private void addToCache(String service, String operation, String side, String serviceStyle, Object value)
/*      */   {
/* 1672 */     Map<String, ConfigurationParameterInstanceInner> serviceParameter = null;
/* 1673 */     if (side.equals("consumer"))
/*      */     {
/* 1675 */       serviceParameter = (Map)this.consumerParameterCache.get(service);
/*      */     }
/* 1677 */     else if (side.equals("provider"))
/*      */     {
/* 1679 */       serviceParameter = (Map)this.providerParameterCache.get(service);
/*      */     }
/* 1681 */     if (serviceParameter == null)
/*      */     {
/* 1683 */       serviceParameter = new HashMap();
/*      */     }
/* 1685 */     ConfigurationParameterInstanceInner operationParameter = (ConfigurationParameterInstanceInner)serviceParameter.get(operation);
/*      */     
/* 1687 */     if (operationParameter == null)
/*      */     {
/* 1689 */       operationParameter = new ConfigurationParameterInstanceInner();
/*      */     }
/* 1691 */     operationParameter.addParameter(serviceStyle, value);
/* 1692 */     serviceParameter.put(operation, operationParameter);
/* 1693 */     if (side.equals("consumer"))
/*      */     {
/* 1695 */       this.consumerParameterCache.put(service, serviceParameter);
/*      */     }
/* 1697 */     else if (side.equals("provider"))
/*      */     {
/* 1699 */       this.providerParameterCache.put(service, serviceParameter);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isActive(String serviceName, ServiceInstanceInner instance)
/*      */   {
/* 1715 */     if (StringUtils.isEmpty(serviceName))
/*      */     {
/*      */ 
/* 1718 */       return true;
/*      */     }
/*      */     
/* 1721 */     ProviderConfigurationInstanceInner serviceOnOffLineConfig = (ProviderConfigurationInstanceInner)this.providerConfigurations.get(serviceName);
/*      */     
/* 1723 */     if (null != serviceOnOffLineConfig)
/*      */     {
/*      */ 
/* 1726 */       return serviceOnOffLineConfig.isActive(this.regId, instance, this);
/*      */     }
/* 1728 */     ProviderConfigurationInstanceInner allOnOffLineConfig = getConfigurationInstanceInnerForAll();
/* 1729 */     if (null != allOnOffLineConfig)
/*      */     {
/* 1731 */       return allOnOffLineConfig.isActive(this.regId, instance, this);
/*      */     }
/* 1733 */     return true;
/*      */   }
/*      */   
/*      */   public ProviderConfigurationInstanceInner getConfigurationInstanceInnerForAll()
/*      */   {
/* 1738 */     return (ProviderConfigurationInstanceInner)this.providerConfigurations.get("ALL");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addProviderConfigUpdateListener(ProviderConfigUpdateListener listener)
/*      */   {
/* 1751 */     this.providerConfigUpdateListeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addConsumerConfigUpdateListener(ConsumerConfigUpdateListener listener)
/*      */   {
/* 1764 */     this.consumerConfigUpdateListeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeConsumerConfigUpdateListener(ConsumerConfigUpdateListener listener)
/*      */   {
/* 1770 */     this.consumerConfigUpdateListeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeProviderConfigUpdateListener(ProviderConfigUpdateListener listener)
/*      */   {
/* 1783 */     this.providerConfigUpdateListeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void notifyProviderConfigUpdateListeners(Collection<String> names)
/*      */   {
/* 1797 */     for (ProviderConfigUpdateListener listener : this.providerConfigUpdateListeners)
/*      */     {
/* 1799 */       listener.onProviderConfigUpdate(names);
/*      */     }
/*      */   }
/*      */   
/*      */   private void notifyConsumerConfigUpdateListeners(Collection<String> names)
/*      */   {
/* 1805 */     for (ConsumerConfigUpdateListener listener : this.consumerConfigUpdateListeners)
/*      */     {
/* 1807 */       listener.onConsumerConfigUpdate(this.regId, names);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setId(String id)
/*      */   {
/* 1813 */     this.regId = id;
/*      */   }
/*      */   
/*      */   public String getId()
/*      */   {
/* 1818 */     return this.regId;
/*      */   }
/*      */   
/*      */   public void clearAllCache()
/*      */   {
/* 1823 */     this.routersMap.clear();
/* 1824 */     this.routerUpdateListeners.clear();
/* 1825 */     this.providerConfigurations.clear();
/* 1826 */     this.consumerConfigurations.clear();
/* 1827 */     this.providerConfigUpdateListeners.clear();
/* 1828 */     this.consumerConfigUpdateListeners.clear();
/* 1829 */     this.consumerParameterCache.clear();
/* 1830 */     this.providerParameterCache.clear();
/* 1831 */     this.weightParameterCache.clear();
/*      */   }
/*      */   
/*      */   public Map<String, ServiceGroup> getServiceGroup()
/*      */   {
/* 1836 */     return this.serviceGroup;
/*      */   }
/*      */   
/*      */   public void setServiceGroup(Map<String, ServiceGroup> serviceGroup)
/*      */   {
/* 1841 */     this.serviceGroup = serviceGroup;
/*      */   }
/*      */   
/*      */   public Integer getWeight(String address)
/*      */   {
/* 1846 */     return (Integer)this.weightParameterCache.get(address);
/*      */   }
/*      */   
/*      */   private void pushWeight()
/*      */   {
/* 1851 */     this.weightParameterCache.clear();
/* 1852 */     ProviderConfigurationInstanceInner allConfInstance = getConfigurationInstanceInnerForAll();
/* 1853 */     if (null != allConfInstance)
/*      */     {
/* 1855 */       for (Map.Entry<String, ConfigurationInstanceInner> entry : allConfInstance.getPortConfigurations().entrySet())
/*      */       {
/*      */ 
/* 1858 */         ConfigurationInstanceInner confInstance = (ConfigurationInstanceInner)entry.getValue();
/* 1859 */         if (null != confInstance)
/*      */         {
/* 1861 */           Integer weight = getIntegerValue(confInstance.getAttribute("weight"));
/*      */           
/* 1863 */           if ((null != weight) && (null != confInstance.getAddress()))
/*      */           {
/* 1865 */             this.weightParameterCache.put(confInstance.getAddress(), weight);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\DsfZookeeperDataManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */