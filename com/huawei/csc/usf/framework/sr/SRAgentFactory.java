/*     */ package com.huawei.csc.usf.framework.sr;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.exception.USFException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.apache.commons.lang.StringUtils;
/*     */ import org.springframework.context.ApplicationContext;
/*     */ 
/*     */ 
/*     */ public class SRAgentFactory
/*     */ {
/*  20 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(SRAgentFactory.class);
/*     */   
/*     */ 
/*  23 */   private Map<String, ServiceRegistryAgent> srAgentManager = new HashMap();
/*     */   
/*  25 */   private ServiceRegistryAgent defaultSRAgent = null;
/*     */   
/*  27 */   private String DEFAULT_REGID = "dsf_default";
/*     */   
/*     */   private SystemConfig config;
/*     */   
/*  31 */   private List<String> zkIds = new ArrayList();
/*     */   
/*     */   public void init(ApplicationContext app, SystemConfig config)
/*     */     throws Exception
/*     */   {
/*  36 */     this.config = config;
/*  37 */     boolean isZkExits = true;
/*     */     
/*     */ 
/*  40 */     Map<String, Registry> idRegistryMap = new HashMap();
/*     */     RegistryStartup zkStartup;
/*     */     try {
/*  43 */       zkStartup = (RegistryStartup)app.getBean("ZKStartup");
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  47 */       if (LOGGER.isErrorEnable())
/*     */       {
/*  49 */         LOGGER.error("missing zookeeper register lib or zk.ebus.service.xml");
/*     */       }
/*  51 */       zkStartup = null;
/*  52 */       isZkExits = false;
/*     */     }
/*  54 */     zookeeperIdInit(isZkExits);
/*  55 */     if (zkStartup != null)
/*     */     {
/*  57 */       zkStartup.init(config, this.zkIds);
/*  58 */       idRegistryMap = zkStartup.getIdRegistryMap();
/*  59 */       if (!idRegistryMap.containsKey(this.DEFAULT_REGID))
/*     */       {
/*  61 */         DefaultRegistry registry = new DefaultRegistry();
/*  62 */         idRegistryMap.put(this.DEFAULT_REGID, registry);
/*     */       }
/*     */       else
/*     */       {
/*  66 */         idRegistryMap.get(this.DEFAULT_REGID);
/*  67 */         SRAgentImpl sr = new SRAgentImpl(this.DEFAULT_REGID);
/*  68 */         sr.setRegistry((Registry)idRegistryMap.get(this.DEFAULT_REGID));
/*  69 */         UsfZkRegistryAdapter zkAdapter = new UsfZkRegistryAdapter();
/*  70 */         DsfZookeeperDataManager dataManager = new DsfZookeeperDataManager();
/*  71 */         zkAdapter.setRegisterId(this.DEFAULT_REGID);
/*  72 */         dataManager.setId(this.DEFAULT_REGID);
/*  73 */         dataManager.setSrFactory(this);
/*  74 */         zkAdapter.setSystemConfig(config);
/*  75 */         sr.setRegistryAdapter(zkAdapter);
/*  76 */         sr.setZookeeperDataManager(dataManager);
/*  77 */         sr.init();
/*  78 */         this.srAgentManager.put(this.DEFAULT_REGID, sr);
/*     */       }
/*  80 */       for (Map.Entry<String, Registry> entry : idRegistryMap.entrySet())
/*     */       {
/*  82 */         String registerId = (String)entry.getKey();
/*  83 */         String defaultUrl = config.getDefaultZkUrl();
/*  84 */         String registerUrl = config.getZkUrl(registerId);
/*  85 */         if (!defaultUrl.equals(registerUrl))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  91 */           SRAgentImpl sr = new SRAgentImpl(registerId);
/*  92 */           sr.setRegistry((Registry)entry.getValue());
/*  93 */           UsfZkRegistryAdapter zkAdapter = new UsfZkRegistryAdapter();
/*  94 */           DsfZookeeperDataManager dataManager = new DsfZookeeperDataManager();
/*  95 */           zkAdapter.setRegisterId(registerId);
/*  96 */           dataManager.setId(registerId);
/*  97 */           dataManager.setSrFactory(this);
/*  98 */           zkAdapter.setSystemConfig(config);
/*  99 */           sr.setRegistryAdapter(zkAdapter);
/* 100 */           sr.setZookeeperDataManager(dataManager);
/* 101 */           sr.init();
/* 102 */           this.srAgentManager.put(registerId, sr);
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 108 */       for (String id : this.zkIds)
/*     */       {
/* 110 */         DefaultRegistry registry = new DefaultRegistry();
/* 111 */         SRAgentImpl sr = new SRAgentImpl(id);
/* 112 */         sr.setRegistry(registry);
/* 113 */         UsfZkRegistryAdapter zkAdapter = new UsfZkRegistryAdapter();
/* 114 */         DsfZookeeperDataManager dataManager = new DsfZookeeperDataManager();
/* 115 */         dataManager.setId(id);
/* 116 */         dataManager.setSrFactory(this);
/* 117 */         zkAdapter.setRegisterId(id);
/* 118 */         zkAdapter.setSystemConfig(config);
/* 119 */         sr.setRegistryAdapter(zkAdapter);
/* 120 */         sr.setZookeeperDataManager(dataManager);
/* 121 */         sr.init();
/* 122 */         if (!this.srAgentManager.containsKey(id))
/*     */         {
/* 124 */           this.srAgentManager.put(id, sr);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 129 */     this.defaultSRAgent = ((ServiceRegistryAgent)this.srAgentManager.get(this.DEFAULT_REGID));
/*     */   }
/*     */   
/*     */   public void removeSRAgent(String regId)
/*     */   {
/* 134 */     this.srAgentManager.remove(regId);
/*     */   }
/*     */   
/*     */   public ServiceRegistryAgent getSRAgent(String regId)
/*     */   {
/* 139 */     if (isDefaultId(regId))
/*     */     {
/* 141 */       return (ServiceRegistryAgent)this.srAgentManager.get(this.DEFAULT_REGID);
/*     */     }
/* 143 */     return (ServiceRegistryAgent)this.srAgentManager.get(regId);
/*     */   }
/*     */   
/*     */   public void uninit()
/*     */   {
/* 148 */     for (ServiceRegistryAgent srAgent : this.srAgentManager.values())
/*     */     {
/*     */       try
/*     */       {
/* 152 */         srAgent.uninit();
/* 153 */         srAgent.getZookeeperDataManager().clearAllCache();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 157 */         LOGGER.error("failed to init SRAgent");
/*     */       }
/*     */     }
/* 160 */     this.zkIds.clear();
/* 161 */     this.srAgentManager.clear();
/*     */   }
/*     */   
/*     */   public void start()
/*     */   {
/* 166 */     for (ServiceRegistryAgent srAgent : this.srAgentManager.values())
/*     */     {
/* 168 */       srAgent.start();
/*     */     }
/*     */   }
/*     */   
/*     */   public void stop()
/*     */   {
/* 174 */     for (ServiceRegistryAgent srAgent : this.srAgentManager.values())
/*     */     {
/* 176 */       srAgent.stop();
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
/*     */   public List<ServiceInstanceInner> findInstancesOnSRs(String name)
/*     */   {
/* 190 */     List<ServiceInstanceInner> retInstances = new ArrayList();
/* 191 */     for (ServiceRegistryAgent srAgent : this.srAgentManager.values())
/*     */     {
/* 193 */       retInstances.addAll(srAgent.findInstances(name));
/*     */     }
/* 195 */     return retInstances;
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
/*     */   public List<ServiceInstanceInner> findInstances(String regId, String name)
/*     */   {
/* 208 */     if (!StringUtils.isEmpty(regId))
/*     */     {
/* 210 */       ServiceRegistryAgent srAgent = null;
/* 211 */       if (isDefaultId(regId))
/*     */       {
/* 213 */         srAgent = (ServiceRegistryAgent)this.srAgentManager.get(this.DEFAULT_REGID);
/*     */       }
/*     */       else
/*     */       {
/* 217 */         srAgent = (ServiceRegistryAgent)this.srAgentManager.get(regId);
/*     */       }
/* 219 */       if (null != srAgent)
/*     */       {
/* 221 */         return srAgent.findInstances(name);
/*     */       }
/*     */       
/*     */ 
/* 225 */       LOGGER.error("registryId:" + regId + " is not found.");
/* 226 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 231 */     LOGGER.warn("regId is empty, find Instances on default registry.");
/* 232 */     return this.defaultSRAgent.findInstances(name);
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
/*     */   public void registerServicesOnDefaultSR(List<ServiceInner> services, boolean delay)
/*     */   {
/* 247 */     if (this.defaultSRAgent != null)
/*     */     {
/* 249 */       this.defaultSRAgent.registerServices(services, delay);
/*     */     }
/*     */     else
/*     */     {
/* 253 */       LOGGER.error("default SRAgent is not initialized");
/*     */     }
/*     */   }
/*     */   
/*     */   public void unregisterServicesOnDefaultSR(List<ServiceInner> services)
/*     */   {
/* 259 */     if (this.defaultSRAgent != null)
/*     */     {
/* 261 */       this.defaultSRAgent.unregisterServices(services);
/*     */     }
/*     */     else
/*     */     {
/* 265 */       LOGGER.error("defaultSRAgent is not initialized");
/*     */     }
/*     */   }
/*     */   
/*     */   public void addServiceUpdateListenerOnSRs(ServiceUpdateListener listener)
/*     */   {
/* 271 */     for (ServiceRegistryAgent srAgent : this.srAgentManager.values())
/*     */     {
/* 273 */       srAgent.addServiceUpdateListener(listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addProviderConfigUpdateListenerOnSRs(ProviderConfigUpdateListener listener)
/*     */   {
/* 280 */     for (ServiceRegistryAgent srAgent : this.srAgentManager.values())
/*     */     {
/* 282 */       srAgent.getZookeeperDataManager().addProviderConfigUpdateListener(listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addConsumerConfigUpdateListenerOnSRs(ConsumerConfigUpdateListener listener)
/*     */   {
/* 290 */     for (ServiceRegistryAgent srAgent : this.srAgentManager.values())
/*     */     {
/* 292 */       srAgent.getZookeeperDataManager().addConsumerConfigUpdateListener(listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeServiceUpdateListenerOnSRs(ServiceUpdateListener listener)
/*     */   {
/* 299 */     for (ServiceRegistryAgent srAgent : this.srAgentManager.values())
/*     */     {
/* 301 */       srAgent.removeServiceUpdateListener(listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeProviderConfigUpdateListenerOnSRs(ProviderConfigUpdateListener listener)
/*     */   {
/* 308 */     for (ServiceRegistryAgent srAgent : this.srAgentManager.values())
/*     */     {
/* 310 */       srAgent.getZookeeperDataManager().removeProviderConfigUpdateListener(listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeConsumerConfigUpdateListenerOnSRs(ConsumerConfigUpdateListener listener)
/*     */   {
/* 318 */     for (ServiceRegistryAgent srAgent : this.srAgentManager.values())
/*     */     {
/* 320 */       srAgent.getZookeeperDataManager().removeConsumerConfigUpdateListener(listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void unregisterAllLocalInstanceOnSRs()
/*     */   {
/* 328 */     for (ServiceRegistryAgent srAgent : this.srAgentManager.values())
/*     */     {
/* 330 */       srAgent.unregisterAllLocalInstance();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setListenAddressOnSRs(ServiceType type, String address)
/*     */   {
/* 336 */     for (ServiceRegistryAgent srAgent : this.srAgentManager.values())
/*     */     {
/* 338 */       srAgent.setListenAddress(type, address);
/*     */     }
/*     */   }
/*     */   
/*     */   public ServiceInner findServiceOnDefaultSR(String name)
/*     */   {
/* 344 */     if (this.defaultSRAgent != null)
/*     */     {
/* 346 */       return this.defaultSRAgent.findService(name);
/*     */     }
/*     */     
/*     */ 
/* 350 */     LOGGER.error("defaultSRAgent is not initialized");
/* 351 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void delInstancesOnDefaultSR(List<ServiceInstanceInner> instances)
/*     */   {
/* 357 */     if (this.defaultSRAgent != null)
/*     */     {
/* 359 */       this.defaultSRAgent.delInstances(instances);
/*     */     }
/*     */     else
/*     */     {
/* 363 */       LOGGER.error("defaultSRAgent is not initialized");
/*     */     }
/*     */   }
/*     */   
/*     */   public void registerConsumersOnSRs(List<ServiceInner> consumers)
/*     */     throws Exception
/*     */   {
/* 370 */     Map<String, LinkedList<ServiceInner>> servInnerListMap = new HashMap();
/* 371 */     for (Iterator i$ = consumers.iterator(); i$.hasNext();) { consumer = (ServiceInner)i$.next();
/*     */       
/* 373 */       List<ConsumerInstanceInner> list = consumer.getConsumerInstances();
/* 374 */       for (ConsumerInstanceInner consumerInstanceInner : list)
/*     */       {
/* 376 */         String regId = consumerInstanceInner.getRegId();
/* 377 */         LinkedList<ServiceInner> servInnerList = (LinkedList)servInnerListMap.get(regId);
/*     */         
/* 379 */         if (servInnerList != null)
/*     */         {
/* 381 */           servInnerList.add(consumer);
/*     */         }
/*     */         else
/*     */         {
/* 385 */           servInnerList = new LinkedList();
/* 386 */           servInnerList.add(consumer);
/* 387 */           servInnerListMap.put(regId, servInnerList);
/*     */         }
/*     */       } }
/*     */     ServiceInner consumer;
/* 391 */     if (!servInnerListMap.isEmpty())
/*     */     {
/* 393 */       for (Map.Entry<String, LinkedList<ServiceInner>> entry : servInnerListMap.entrySet())
/*     */       {
/*     */ 
/* 396 */         String regId = (String)entry.getKey();
/* 397 */         if (getSRAgent(regId) == null)
/*     */         {
/* 399 */           String exception = "SRAgent with the regId:" + regId + " is not found.";
/*     */           
/* 401 */           LOGGER.error(exception);
/* 402 */           throw new USFException(exception);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 407 */         getSRAgent(regId).registerConsumers((List)entry.getValue());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void findServiceDataFromZk(String regId, String name)
/*     */   {
/* 416 */     ServiceRegistryAgent srAgent = null;
/* 417 */     if (isDefaultId(regId))
/*     */     {
/* 419 */       srAgent = (ServiceRegistryAgent)this.srAgentManager.get(this.DEFAULT_REGID);
/*     */     }
/*     */     else
/*     */     {
/* 423 */       srAgent = (ServiceRegistryAgent)this.srAgentManager.get(regId);
/*     */     }
/* 425 */     if (null != srAgent)
/*     */     {
/* 427 */       srAgent.findServiceDataFromZk(name);
/*     */     }
/*     */     else
/*     */     {
/* 431 */       LOGGER.error("regId:" + regId + " is not found.");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addServiceWatcherOnDefaultSR(List<ServiceInner> services)
/*     */   {
/* 438 */     if (this.defaultSRAgent != null)
/*     */     {
/* 440 */       this.defaultSRAgent.addServiceWatcher(services);
/*     */     }
/*     */     else
/*     */     {
/* 444 */       LOGGER.error("defaultSRAgent is not initialized");
/*     */     }
/*     */   }
/*     */   
/*     */   public ServiceRegistryAgent getDefaultSRAgent()
/*     */   {
/* 450 */     return this.defaultSRAgent;
/*     */   }
/*     */   
/*     */   public boolean isReadyToDestroy()
/*     */   {
/* 455 */     for (ServiceRegistryAgent srAgent : this.srAgentManager.values())
/*     */     {
/* 457 */       if (!srAgent.isReadyToDestroy())
/*     */       {
/* 459 */         return false;
/*     */       }
/*     */     }
/* 462 */     return true;
/*     */   }
/*     */   
/*     */   private void zookeeperIdInit(boolean isZkExis) throws Exception
/*     */   {
/* 467 */     List<String> zkUrlKeys = this.config.getZkUrlKeys();
/*     */     
/* 469 */     for (String zkUrlKey : zkUrlKeys)
/*     */     {
/* 471 */       if (("dsf.zk.server.url".equals(zkUrlKey)) || ("zk.server.url".equals(zkUrlKey)))
/*     */       {
/*     */ 
/* 474 */         this.zkIds.add(this.DEFAULT_REGID);
/*     */       }
/*     */       else {
/* 477 */         String[] zkid = zkUrlKey.split("\\.");
/* 478 */         if ("dsf".equals(zkid[0]))
/*     */         {
/* 480 */           if (this.DEFAULT_REGID.equals(zkid[2]))
/*     */           {
/* 482 */             boolean isOff = this.config.isZkOff(this.DEFAULT_REGID);
/* 483 */             if (isZkExis)
/*     */             {
/* 485 */               if (!isOff)
/*     */               {
/* 487 */                 String strTips = "zookeeper register id can not use " + this.DEFAULT_REGID + ", please set it again";
/*     */                 
/* 489 */                 LOGGER.error(strTips);
/* 490 */                 throw new USFException(strTips);
/*     */               }
/*     */               
/*     */             }
/*     */             
/*     */           }
/* 496 */           else if (!this.config.isZkOff(zkid[2]))
/*     */           {
/* 498 */             if (!this.zkIds.contains(zkid[2]))
/*     */             {
/* 500 */               this.zkIds.add(zkid[2]);
/*     */             }
/*     */             else
/*     */             {
/* 504 */               boolean isOff = this.config.isZkOff(zkid[2]);
/* 505 */               if (isZkExis)
/*     */               {
/* 507 */                 if (isOff)
/*     */                 {
/* 509 */                   String strTips = "zookeeper register id  is repeated, register id is:" + zkid[2];
/*     */                   
/* 511 */                   LOGGER.error(strTips);
/* 512 */                   throw new USFException(strTips);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public Map<String, ServiceRegistryAgent> getServiceAgentMap()
/*     */   {
/* 524 */     return this.srAgentManager;
/*     */   }
/*     */   
/*     */   public boolean isDefaultId(String registerId)
/*     */   {
/* 529 */     if (this.config != null)
/*     */     {
/* 531 */       if (this.config.getDefaultZkUrl().equals(this.config.getZkUrl(registerId)))
/*     */       {
/* 533 */         return true;
/*     */       }
/*     */     }
/* 536 */     return false;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\SRAgentFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */