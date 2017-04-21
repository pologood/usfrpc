/*      */ package com.huawei.csc.usf.framework.sr;
/*      */ 
/*      */ import com.huawei.csc.kernel.api.log.LogFactory;
/*      */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*      */ import com.huawei.csc.usf.framework.util.CopyOnWriteHashMap;
/*      */ import com.huawei.csc.usf.framework.util.Utils;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.net.URLDecoder;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.CopyOnWriteArraySet;
/*      */ import java.util.concurrent.locks.Lock;
/*      */ import java.util.concurrent.locks.ReadWriteLock;
/*      */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*      */ import org.apache.commons.lang.StringUtils;
/*      */ import org.springframework.beans.BeanUtils;
/*      */ 
/*      */ 
/*      */ public class SRAgentImpl
/*      */   implements ServiceRegistryAgent, RegistryConnectionListener, RegistryNodeListener
/*      */ {
/*   29 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(SRAgentImpl.class);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   35 */   private Map<String, List<ServiceInstanceInner>> instanceCache = new CopyOnWriteHashMap();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   41 */   private Map<String, List<ServiceInner>> serviceCache = new CopyOnWriteHashMap();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   46 */   private Map<String, String> nameCache = new CopyOnWriteHashMap();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   51 */   private Map<String, Boolean> serviceWatcherCache = new CopyOnWriteHashMap();
/*      */   
/*   53 */   private Map<String, Boolean> opsWatcherCache = new CopyOnWriteHashMap();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   58 */   private Map<String, List<ConsumerInstanceInner>> consumerinstanceMap = new CopyOnWriteHashMap();
/*      */   
/*   60 */   private List<ServiceInner> delayServicesList = new ArrayList();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   65 */   private final ReadWriteLock cacheLock = new ReentrantReadWriteLock();
/*      */   
/*   67 */   private final ReadWriteLock serviceCacheLock = new ReentrantReadWriteLock();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   75 */   private Set<String> registeredNames = new CopyOnWriteArraySet();
/*      */   
/*   77 */   private Set<String> registeredConsumerNames = new CopyOnWriteArraySet();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   84 */   private Set<ServiceUpdateListener> listeners = new CopyOnWriteArraySet();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   91 */   protected String[] listenAddress = new String[ServiceType.size()];
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   96 */   private ZkRegistryAdapter registryAdapter = null;
/*      */   
/*   98 */   private DsfZookeeperDataManager dataManager = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  103 */   private Registry registry = null;
/*      */   
/*      */   private String regId;
/*      */   
/*      */   public SRAgentImpl(String regId)
/*      */   {
/*  109 */     this.regId = regId;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getRegId()
/*      */   {
/*  115 */     return this.regId;
/*      */   }
/*      */   
/*      */   public void init() throws Exception
/*      */   {
/*  120 */     this.registry.init(this);
/*  121 */     this.registryAdapter.init(this);
/*      */     
/*      */ 
/*  124 */     this.registry.addConnectionListener(this);
/*  125 */     this.registry.setRootPath(this.registryAdapter.getInstanceNS());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void uninit()
/*      */   {
/*  132 */     this.registry.uninit();
/*      */     
/*  134 */     Lock writeLock = this.cacheLock.writeLock();
/*  135 */     writeLock.lock();
/*      */     
/*      */     try
/*      */     {
/*  139 */       this.instanceCache.clear();
/*  140 */       this.serviceCache.clear();
/*  141 */       this.nameCache.clear();
/*  142 */       this.consumerinstanceMap.clear();
/*  143 */       this.serviceWatcherCache.clear();
/*  144 */       this.delayServicesList.clear();
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*  148 */       LOGGER.error("Zookeeper:" + this.regId + " uninit failed!", t);
/*      */     }
/*      */     finally
/*      */     {
/*  152 */       writeLock.unlock();
/*      */     }
/*      */     
/*  155 */     this.registeredNames.clear();
/*  156 */     this.registeredConsumerNames.clear();
/*  157 */     this.listeners.clear();
/*      */   }
/*      */   
/*      */ 
/*      */   public void start()
/*      */   {
/*  163 */     if (this.registry != null)
/*      */     {
/*  165 */       this.registry.start();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void stop()
/*      */   {
/*  172 */     if (this.registry != null)
/*      */     {
/*  174 */       this.registry.stop();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isReadyToDestroy()
/*      */   {
/*  181 */     if (this.registry != null)
/*      */     {
/*  183 */       return this.registry.isReadyToDestroy();
/*      */     }
/*      */     
/*      */ 
/*  187 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void onNodeEvent(RegistryNodeListener.NodeChangeEvent event, String key, byte[] data)
/*      */   {
/*  194 */     if (key.startsWith(this.registryAdapter.getInstanceNS()))
/*      */     {
/*  196 */       onInstanceChange(event, key, data, this.registryAdapter);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void onConnectionChanged(RegistryConnectionListener.ConnectionEvent event)
/*      */   {
/*  204 */     if (event == RegistryConnectionListener.ConnectionEvent.SESSION_REBUILT)
/*      */     {
/*  206 */       if (LOGGER.isInfoEnable())
/*      */       {
/*  208 */         LOGGER.info("Zookeeper:" + this.regId + " session rebuilt");
/*      */       }
/*  210 */       Lock writeLock = this.serviceCacheLock.writeLock();
/*  211 */       writeLock.lock();
/*      */       
/*      */       try
/*      */       {
/*  215 */         this.serviceWatcherCache.clear();
/*  216 */         this.opsWatcherCache.clear();
/*      */         
/*  218 */         if (LOGGER.isInfoEnable())
/*      */         {
/*  220 */           LOGGER.info("Zookeeper:" + this.regId + " connection is rebuilted, clear the service Watcher Cache");
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/*      */ 
/*  228 */         LOGGER.error("Zookeeper:" + this.regId + " clear service watcher cache failed!", t);
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/*  233 */         writeLock.unlock();
/*      */       }
/*  235 */       registerAllLocalInstance();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public List<ServiceInstanceInner> findInstances(String name)
/*      */   {
/*  242 */     if (StringUtils.isEmpty(name))
/*      */     {
/*  244 */       LOGGER.error("Zookeeper:" + this.regId + " find instance failed,the instance name is empty");
/*      */       
/*  246 */       return null;
/*      */     }
/*  248 */     findServiceDataFromZk(name);
/*  249 */     List<ServiceInstanceInner> retInstances = new ArrayList();
/*      */     
/*  251 */     Lock readLock = this.cacheLock.readLock();
/*  252 */     readLock.lock();
/*      */     
/*      */     try
/*      */     {
/*  256 */       String infName = (String)this.nameCache.get(name);
/*  257 */       if (StringUtils.isEmpty(infName))
/*      */       {
/*  259 */         return null;
/*      */       }
/*      */       
/*  262 */       Object totInstances = (List)this.instanceCache.get(infName);
/*      */       
/*  264 */       if (totInstances != null)
/*      */       {
/*      */ 
/*  267 */         for (ServiceInstanceInner inst : (List)totInstances)
/*      */         {
/*  269 */           ServiceInstanceInner instance = new ServiceInstanceInner();
/*  270 */           BeanUtils.copyProperties(inst, instance);
/*  271 */           if (name.equals(instance.getInstanceName()))
/*      */           {
/*  273 */             retInstances.add(instance);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*  280 */       LOGGER.error("Zookeeper:" + this.regId + " find instance failed!", t);
/*      */     }
/*      */     finally
/*      */     {
/*  284 */       readLock.unlock();
/*      */     }
/*      */     
/*  287 */     return retInstances;
/*      */   }
/*      */   
/*      */ 
/*      */   public void registerServices(List<ServiceInner> services, boolean delay)
/*      */   {
/*  293 */     if (Utils.isEmpty(services))
/*      */     {
/*  295 */       return;
/*      */     }
/*  297 */     if (delay)
/*      */     {
/*  299 */       this.delayServicesList.addAll(services);
/*  300 */       return;
/*      */     }
/*  302 */     List<String> names = getNames(services, true, false);
/*  303 */     synchronized (this.registeredNames)
/*      */     {
/*  305 */       checkRegisteredNames(names, false);
/*  306 */       addRegisteredNames(names);
/*      */     }
/*      */     
/*  309 */     List<ServiceInstanceInner> instancesList = new ArrayList();
/*      */     
/*      */ 
/*  312 */     for (ServiceInner service : services)
/*      */     {
/*  314 */       instancesList.addAll(service.getInstances());
/*      */     }
/*      */     
/*      */ 
/*  318 */     for (ServiceInstanceInner instance : instancesList)
/*      */     {
/*  320 */       instance.setImmutable(true);
/*  321 */       addInstance(instance);
/*  322 */       registerInstance(instance);
/*      */     }
/*      */     
/*  325 */     Object instanceNames = getNames(services, false, false);
/*  326 */     notifyListeners(new HashSet((Collection)instanceNames));
/*      */     
/*  328 */     for (ServiceInner service : services)
/*      */     {
/*      */ 
/*  331 */       String serviceType = service.getServiceType();
/*  332 */       if ((serviceType.equals(ServiceType.EBUS.toString())) || (serviceType.equals(ServiceType.DSF.toString())))
/*      */       {
/*      */ 
/*  335 */         addService(service);
/*  336 */         registerService(service);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void unregisterServices(List<ServiceInner> services)
/*      */   {
/*  344 */     if (Utils.isEmpty(services))
/*      */     {
/*  346 */       return;
/*      */     }
/*  348 */     List<ServiceInstanceInner> instancesList = new ArrayList();
/*      */     
/*      */ 
/*  351 */     for (ServiceInner service : services)
/*      */     {
/*  353 */       instancesList.addAll(service.getInstances());
/*      */     }
/*      */     
/*  356 */     delInstances(instancesList);
/*      */   }
/*      */   
/*      */ 
/*      */   public void addServiceUpdateListener(ServiceUpdateListener listener)
/*      */   {
/*  362 */     this.listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeServiceUpdateListener(ServiceUpdateListener listener)
/*      */   {
/*  368 */     this.listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setRegistryAdapter(ZkRegistryAdapter registryAdapter)
/*      */   {
/*  374 */     this.registryAdapter = registryAdapter;
/*      */   }
/*      */   
/*      */ 
/*      */   public Registry getRegistry()
/*      */   {
/*  380 */     return this.registry;
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
/*      */   private void onInstanceChange(RegistryNodeListener.NodeChangeEvent event, String key, byte[] data, ZkRegistryAdapter registryAdapter)
/*      */   {
/*  396 */     String shortKey = registryAdapter.getShortKey(key);
/*  397 */     String instanceName = registryAdapter.getNameFromShortKey(shortKey);
/*      */     
/*  399 */     ServiceInstanceInner oldOne = findInstance(instanceName, key, registryAdapter);
/*      */     
/*      */ 
/*      */ 
/*  403 */     List<String> listAddress = Arrays.asList(this.listenAddress);
/*  404 */     if ((oldOne != null) && (listAddress.contains(oldOne.getAddress())))
/*      */     {
/*      */ 
/*  407 */       return;
/*      */     }
/*      */     
/*  410 */     Set<String> names = new HashSet();
/*  411 */     names.add(instanceName);
/*      */     
/*  413 */     if (event == RegistryNodeListener.NodeChangeEvent.DELETED)
/*      */     {
/*  415 */       if (oldOne == null)
/*      */       {
/*  417 */         return;
/*      */       }
/*  419 */       if (LOGGER.isInfoEnable())
/*      */       {
/*  421 */         LOGGER.info(String.format("Zookeeper:" + this.regId + " remove service instance [%s] from cache: %s", new Object[] { key, oldOne.toString() }));
/*      */       }
/*      */       
/*      */ 
/*  425 */       removeInstance(oldOne);
/*  426 */       names.add(oldOne.getServiceName());
/*      */     }
/*  428 */     else if ((event == RegistryNodeListener.NodeChangeEvent.CREATED) || (event == RegistryNodeListener.NodeChangeEvent.DATA_CHANGED))
/*      */     {
/*      */ 
/*  431 */       ServiceInstanceInner newOne = registryAdapter.deserializeInstanceData(shortKey, data);
/*      */       
/*      */ 
/*  434 */       if (newOne != null)
/*      */       {
/*  436 */         if (LOGGER.isInfoEnable())
/*      */         {
/*  438 */           LOGGER.info(String.format("Zookeeper:" + this.regId + " add service instance [%s] to cache: %s", new Object[] { key, newOne.toString() }));
/*      */         }
/*      */         
/*      */ 
/*  442 */         addInstance(newOne);
/*  443 */         names.add(newOne.getServiceName());
/*      */       }
/*      */     }
/*      */     
/*  447 */     notifyListeners(names);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private ServiceInstanceInner findInstance(String instanceName, String key, ZkRegistryAdapter registryAdapter)
/*      */   {
/*  454 */     List<ServiceInstanceInner> instanceList = findInstances(instanceName);
/*  455 */     if (Utils.isEmpty(instanceList))
/*      */     {
/*  457 */       return null;
/*      */     }
/*  459 */     for (ServiceInstanceInner instance : instanceList)
/*      */     {
/*  461 */       if (key.equals(registryAdapter.getProviderRegistryKey(instance)))
/*      */       {
/*  463 */         return instance;
/*      */       }
/*      */     }
/*      */     
/*  467 */     return null;
/*      */   }
/*      */   
/*      */   private void notifyListeners(Collection<String> names)
/*      */   {
/*  472 */     for (ServiceUpdateListener listener : this.listeners)
/*      */     {
/*      */       try
/*      */       {
/*  476 */         listener.onServiceUpdate(names);
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/*  480 */         LOGGER.error("Zookeeper:" + this.regId + " unexpected exception:", t);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void addInstance(ServiceInstanceInner instance)
/*      */   {
/*  488 */     Lock writeLock = this.cacheLock.writeLock();
/*  489 */     writeLock.lock();
/*      */     
/*      */     try
/*      */     {
/*  493 */       String infName = instance.getServiceName();
/*      */       
/*  495 */       List<ServiceInstanceInner> instanceList = (List)this.instanceCache.get(infName);
/*      */       
/*  497 */       if (instanceList == null)
/*      */       {
/*  499 */         instanceList = new ArrayList();
/*  500 */         instanceList.add(instance);
/*  501 */         this.instanceCache.put(infName, instanceList);
/*      */ 
/*      */ 
/*      */       }
/*  505 */       else if (!instanceList.contains(instance))
/*      */       {
/*  507 */         instanceList.add(instance);
/*      */       }
/*      */       
/*      */ 
/*  511 */       if (LOGGER.isInfoEnable())
/*      */       {
/*  513 */         LOGGER.info("Zookeeper:" + this.regId + " add service instance to instanceCache,instance name: " + instance.getInstanceName() + ",group: " + instance.getGroup() + ",service type: " + instance.getServiceType());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  521 */       updateNameCache(instance, true);
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*  525 */       LOGGER.error("Zookeeper:" + this.regId + " add instance failed!", t);
/*      */     }
/*      */     finally
/*      */     {
/*  529 */       writeLock.unlock();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void removeInstance(ServiceInstanceInner instance)
/*      */   {
/*  536 */     Lock writeLock = this.cacheLock.writeLock();
/*  537 */     writeLock.lock();
/*      */     
/*      */     try
/*      */     {
/*  541 */       String infName = instance.getServiceName();
/*  542 */       List<ServiceInstanceInner> oldList = (List)this.instanceCache.get(infName);
/*  543 */       if (Utils.isEmpty(oldList)) {
/*      */         return;
/*      */       }
/*  546 */       Iterator<ServiceInstanceInner> iter = oldList.iterator();
/*  547 */       while (iter.hasNext())
/*      */       {
/*  549 */         ServiceInstanceInner inst = (ServiceInstanceInner)iter.next();
/*  550 */         if (inst.equals(instance))
/*      */         {
/*  552 */           iter.remove();
/*  553 */           break;
/*      */         }
/*      */       }
/*      */       
/*  557 */       if (oldList.isEmpty())
/*      */       {
/*  559 */         this.instanceCache.remove(infName);
/*      */       }
/*      */       
/*  562 */       updateNameCache(instance, false);
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*  566 */       LOGGER.error("Zookeeper:" + this.regId + " remove instance failed!", t);
/*      */     }
/*      */     finally
/*      */     {
/*  570 */       writeLock.unlock();
/*      */     }
/*      */   }
/*      */   
/*      */   private void removeServiceByName(String name)
/*      */   {
/*  576 */     Lock writeLock = this.cacheLock.writeLock();
/*  577 */     writeLock.lock();
/*      */     
/*      */     try
/*      */     {
/*  581 */       Collection<List<ServiceInstanceInner>> values = this.instanceCache.values();
/*      */       
/*  583 */       for (List<ServiceInstanceInner> list : values)
/*      */       {
/*  585 */         Iterator<ServiceInstanceInner> iter = list.iterator();
/*  586 */         while (iter.hasNext())
/*      */         {
/*  588 */           if (name.equals(((ServiceInstanceInner)iter.next()).getInstanceName()))
/*      */           {
/*  590 */             iter.remove();
/*      */           }
/*      */         }
/*      */       }
/*  594 */       this.nameCache.remove(name);
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*  598 */       LOGGER.error("Zookeeper:" + this.regId + " remove instance failed!", t);
/*      */     }
/*      */     finally
/*      */     {
/*  602 */       writeLock.unlock();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void updateNameCache(ServiceInstanceInner instance, boolean add)
/*      */   {
/*  609 */     if (add)
/*      */     {
/*  611 */       String infName = instance.getServiceName();
/*  612 */       String intanceName = instance.getInstanceName();
/*  613 */       this.nameCache.put(intanceName, infName);
/*      */     }
/*      */     else
/*      */     {
/*  617 */       String intanceName = instance.getInstanceName();
/*  618 */       String infName = instance.getServiceName();
/*  619 */       if ((!this.instanceCache.containsKey(infName)) && (this.nameCache.containsKey(intanceName)))
/*      */       {
/*      */ 
/*  622 */         this.nameCache.remove(intanceName);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void addRegisteredNames(List<String> names)
/*      */   {
/*  629 */     this.registeredNames.addAll(names);
/*      */   }
/*      */   
/*      */   private void addRegisteredConsumerNames(List<String> names)
/*      */   {
/*  634 */     this.registeredConsumerNames.addAll(names);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private List<String> getNames(List<ServiceInner> services, boolean isRegistered, boolean consumer)
/*      */   {
/*  641 */     List<String> names = new ArrayList();
/*      */     
/*  643 */     if (Utils.isEmpty(services))
/*      */     {
/*  645 */       return names;
/*      */     }
/*      */     
/*  648 */     if (consumer)
/*      */     {
/*  650 */       for (ServiceInner service : services)
/*      */       {
/*  652 */         List<ConsumerInstanceInner> instances = service.getConsumerInstances();
/*      */         
/*  654 */         names.addAll(getNamesFromConsumerInstances(instances, isRegistered));
/*      */       }
/*      */       
/*      */ 
/*  658 */       return names;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  664 */     for (ServiceInner service : services)
/*      */     {
/*  666 */       List<ServiceInstanceInner> instances = service.getInstances();
/*  667 */       names.addAll(getNamesFromInstances(instances, isRegistered));
/*      */     }
/*      */     
/*  670 */     return names;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private List<String> getNamesFromInstances(List<ServiceInstanceInner> instances, boolean isRegistered)
/*      */   {
/*  677 */     List<String> names = new ArrayList();
/*      */     
/*  679 */     if (Utils.isEmpty(instances))
/*      */     {
/*  681 */       return names;
/*      */     }
/*  683 */     for (ServiceInstanceInner instance : instances)
/*      */     {
/*  685 */       if (isRegistered)
/*      */       {
/*      */ 
/*  688 */         names.add(instance.getGroup() + ":" + instance.getInstanceName() + ":" + instance.getType() + ":" + instance.getAddress() + ":" + instance.getVersion());
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*  695 */         names.add(instance.getInstanceName());
/*      */       }
/*      */     }
/*      */     
/*  699 */     return names;
/*      */   }
/*      */   
/*      */ 
/*      */   private List<String> getNamesFromConsumerInstances(List<ConsumerInstanceInner> instances, boolean isRegistered)
/*      */   {
/*  705 */     List<String> names = new ArrayList();
/*      */     
/*  707 */     if (Utils.isEmpty(instances))
/*      */     {
/*  709 */       return names;
/*      */     }
/*  711 */     for (ConsumerInstanceInner instance : instances)
/*      */     {
/*  713 */       if (isRegistered)
/*      */       {
/*      */ 
/*  716 */         if ((instance.getBeanName() != null) && (instance.getType().equals("POJO")))
/*      */         {
/*      */ 
/*  719 */           names.add(instance.getGroup() + ":" + instance.getInstanceName() + ":" + instance.getAddress() + ":" + instance.getVersion() + instance.getBeanName());
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  727 */         names.add(instance.getInstanceName());
/*      */       }
/*      */     }
/*      */     
/*  731 */     return names;
/*      */   }
/*      */   
/*      */   private void checkRegisteredNames(List<String> names, boolean consumer) {
/*      */     Set<String> nameList;
/*      */     Set<String> nameList;
/*  737 */     if (consumer)
/*      */     {
/*  739 */       nameList = new HashSet();
/*  740 */       for (String name : names)
/*      */       {
/*      */ 
/*  743 */         if (nameList.contains(name))
/*      */         {
/*  745 */           String exceptionInfo = "register service  repeatedly, the repeated instanceName exists in this register operation. instanceName:" + name;
/*      */           
/*  747 */           RuntimeException e = new RuntimeException("register service or consumer repeatedly failed.");
/*      */           
/*  749 */           LOGGER.error(exceptionInfo, e);
/*  750 */           throw e;
/*      */         }
/*  752 */         nameList.add(name);
/*  753 */         if (this.registeredConsumerNames.contains(name))
/*      */         {
/*  755 */           String exceptionInfo = "register service repeatedly, the intance is already registered. instanceName:" + name;
/*      */           
/*  757 */           RuntimeException e = new RuntimeException("register service repeatedly failed.");
/*      */           
/*  759 */           LOGGER.error(exceptionInfo, e);
/*  760 */           throw e;
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  766 */       nameList = new HashSet();
/*  767 */       for (String name : names)
/*      */       {
/*      */ 
/*  770 */         if (nameList.contains(name))
/*      */         {
/*  772 */           String exceptionInfo = "register service  repeatedly, the repeated instanceName exists in this register operation. instanceName:" + name;
/*      */           
/*  774 */           RuntimeException e = new RuntimeException("register service or consumer repeatedly failed.");
/*      */           
/*  776 */           LOGGER.error(exceptionInfo, e);
/*  777 */           throw e;
/*      */         }
/*  779 */         nameList.add(name);
/*      */         
/*      */ 
/*  782 */         if (this.registeredNames.contains(name))
/*      */         {
/*  784 */           String exceptionInfo = "register service repeatedly, the intance is already registered. instanceName:" + name;
/*      */           
/*  786 */           RuntimeException e = new RuntimeException("register service repeatedly failed.");
/*      */           
/*  788 */           LOGGER.error(exceptionInfo, e);
/*  789 */           throw e;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void registerInstance(ServiceInstanceInner instance)
/*      */   {
/*  798 */     this.registryAdapter.registerProviderInstance(instance);
/*      */   }
/*      */   
/*      */   private void registerService(ServiceInner service)
/*      */   {
/*  803 */     if (null != this.registryAdapter)
/*      */     {
/*  805 */       String instanceNS = this.registryAdapter.getInstanceNS();
/*  806 */       this.registryAdapter.registerSdl(service, instanceNS);
/*      */     }
/*      */     else
/*      */     {
/*  810 */       LOGGER.error("Current registryAdapter is NULL!");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void unregisterInstance(ServiceInstanceInner instance)
/*      */   {
/*  817 */     if (LOGGER.isDebugEnable())
/*      */     {
/*  819 */       LOGGER.debug("Zookeeper:" + this.regId + " unregister instance: " + instance);
/*      */     }
/*      */     
/*  822 */     String adapterName = instance.getServiceType();
/*  823 */     Set<String> adapterNames = new HashSet();
/*  824 */     adapterNames.add(adapterName);
/*      */     
/*  826 */     this.registryAdapter.deregisterInstance(instance);
/*      */   }
/*      */   
/*      */ 
/*      */   private void registerAllLocalInstance()
/*      */   {
/*  832 */     Lock writeLock = this.cacheLock.writeLock();
/*  833 */     writeLock.lock();
/*      */     try
/*      */     {
/*  836 */       for (List<ServiceInstanceInner> instanceList : this.instanceCache.values())
/*      */       {
/*      */ 
/*  839 */         for (ServiceInstanceInner instance : instanceList)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  844 */           List<String> listAddress = Arrays.asList(this.listenAddress);
/*  845 */           if (listAddress.contains(instance.getAddress()))
/*      */           {
/*  847 */             registerInstance(instance);
/*  848 */             if (LOGGER.isInfoEnable())
/*      */             {
/*  850 */               LOGGER.info("Zookeeper:" + this.regId + " session rebuilt, register the instance, instance name is" + instance.getInstanceName() + "instance address is" + instance.getAddress() + "instance group is" + instance.getGroup() + "instance type is" + instance.getServiceType());
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  863 */       for (List<ConsumerInstanceInner> consumerList : this.consumerinstanceMap.values())
/*      */       {
/*      */ 
/*  866 */         for (ConsumerInstanceInner consumerInstance : consumerList)
/*      */         {
/*  868 */           if (consumerInstance.getType().equals("POJO"))
/*      */           {
/*  870 */             registerConsumersInstance(consumerInstance);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*  877 */       LOGGER.error("Zookeeper:" + this.regId + " register instances failed when session rebuilt!", t);
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*  882 */       writeLock.unlock();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void unregisterAllLocalInstance()
/*      */   {
/*  890 */     for (List<ServiceInstanceInner> instanceList : this.instanceCache.values())
/*      */     {
/*  892 */       for (ServiceInstanceInner instance : instanceList)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  897 */         List<String> listAddress = Arrays.asList(this.listenAddress);
/*  898 */         if (listAddress.contains(instance.getAddress()))
/*  899 */           unregisterInstance(instance);
/*      */       }
/*      */     }
/*  902 */     for (List<ConsumerInstanceInner> consumerList : this.consumerinstanceMap.values())
/*      */     {
/*      */ 
/*  905 */       for (ConsumerInstanceInner consumerInstance : consumerList)
/*      */       {
/*  907 */         this.registryAdapter.deregisterConsumerInstance(consumerInstance);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setListenAddress(ServiceType type, String address)
/*      */   {
/*  916 */     this.listenAddress[type.toNumber()] = address;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getListenAddress(ServiceType type)
/*      */   {
/*  922 */     return this.listenAddress[type.toNumber()];
/*      */   }
/*      */   
/*      */ 
/*      */   public ServiceInner findService(String name)
/*      */   {
/*  928 */     if (StringUtils.isEmpty(name))
/*      */     {
/*  930 */       return null;
/*      */     }
/*      */     
/*  933 */     ServiceInner retService = null;
/*      */     
/*  935 */     Lock readLock = this.cacheLock.readLock();
/*  936 */     readLock.lock();
/*      */     
/*      */     try
/*      */     {
/*  940 */       String infName = (String)this.nameCache.get(name);
/*      */       
/*  942 */       if (StringUtils.isEmpty(infName))
/*      */       {
/*  944 */         return null;
/*      */       }
/*      */       
/*  947 */       Object totServices = (List)this.serviceCache.get(infName);
/*  948 */       if (!Utils.isEmpty((Collection)totServices))
/*      */       {
/*  950 */         retService = (ServiceInner)((List)totServices).get(0);
/*      */       }
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*  955 */       LOGGER.error("Zookeeper:" + this.regId + " find service failed!", t);
/*      */     }
/*      */     finally
/*      */     {
/*  959 */       readLock.unlock();
/*      */     }
/*      */     
/*  962 */     return retService;
/*      */   }
/*      */   
/*      */ 
/*      */   public List<ServiceInner> findAllServices()
/*      */   {
/*  968 */     List<ServiceInner> services = new ArrayList();
/*  969 */     Lock readLock = this.cacheLock.readLock();
/*  970 */     readLock.lock();
/*      */     
/*      */     try
/*      */     {
/*  974 */       for (i$ = this.serviceCache.entrySet().iterator(); i$.hasNext();) { entry = (Map.Entry)i$.next();
/*      */         
/*      */ 
/*  977 */         for (ServiceInner service : (List)entry.getValue())
/*      */         {
/*  979 */           services.add(service);
/*      */         }
/*      */       }
/*      */     } catch (Throwable t) {
/*      */       Iterator i$;
/*      */       Map.Entry<String, List<ServiceInner>> entry;
/*  985 */       LOGGER.error("Zookeeper:" + this.regId + " find service failed!", t);
/*  986 */       return null;
/*      */     }
/*      */     finally
/*      */     {
/*  990 */       readLock.unlock();
/*      */     }
/*      */     
/*  993 */     return services;
/*      */   }
/*      */   
/*      */ 
/*      */   public void delInstances(List<ServiceInstanceInner> instances)
/*      */   {
/*  999 */     if ((null == instances) || (instances.isEmpty()))
/*      */     {
/* 1001 */       return;
/*      */     }
/*      */     
/* 1004 */     for (ServiceInstanceInner instance : instances)
/*      */     {
/* 1006 */       removeInstance(instance);
/* 1007 */       unregisterInstance(instance);
/*      */     }
/*      */     
/* 1010 */     List<String> names = getNamesFromInstances(instances, false);
/* 1011 */     notifyListeners(new HashSet(names));
/*      */     
/*      */ 
/* 1014 */     List<String> delNames = getNamesFromInstances(instances, true);
/* 1015 */     synchronized (this.registeredNames)
/*      */     {
/* 1017 */       this.registeredNames.removeAll(delNames);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void setServiceInstaceList(List<String> list, boolean update)
/*      */   {
/* 1024 */     if (!list.isEmpty())
/*      */     {
/*      */ 
/* 1027 */       Set<String> names = new HashSet();
/* 1028 */       for (String urlPath : list)
/*      */       {
/*      */         String url;
/*      */         try
/*      */         {
/* 1033 */           url = URLDecoder.decode(urlPath, "UTF-8");
/*      */         }
/*      */         catch (UnsupportedEncodingException e)
/*      */         {
/* 1037 */           LOGGER.error("Zookeeper:" + this.regId + " decoder url failed");
/* 1038 */           throw new RuntimeException(e.getMessage(), e);
/*      */         }
/*      */         
/* 1041 */         ServiceInstanceInner instanceInner = Utils.toInstanceInner(url);
/* 1042 */         names.add(instanceInner.getInstanceName());
/* 1043 */         addInstance(instanceInner);
/* 1044 */         if (LOGGER.isInfoEnable())
/*      */         {
/* 1046 */           LOGGER.info("Zookeeper:" + this.regId + " add service instance inner,service name is" + instanceInner.getInstanceName() + "service address is" + instanceInner.getAddress() + "servicer group is" + instanceInner.getGroup() + "service type is" + instanceInner.getServiceType());
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1055 */       if (update)
/*      */       {
/* 1057 */         notifyListeners(names);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void delService(String name)
/*      */   {
/* 1065 */     Set<String> names = new HashSet();
/* 1066 */     names.add(name);
/* 1067 */     removeServiceByName(name);
/* 1068 */     notifyListeners(names);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void delInstances()
/*      */   {
/* 1075 */     Lock writeLock = this.cacheLock.writeLock();
/* 1076 */     writeLock.lock();
/*      */     try
/*      */     {
/* 1079 */       Set<String> names = new HashSet();
/* 1080 */       for (String name : this.nameCache.keySet())
/*      */       {
/* 1082 */         names.add(name);
/*      */       }
/* 1084 */       notifyListeners(names);
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/* 1088 */       LOGGER.error("Zookeeper:" + this.regId + " remove all instances failed!", t);
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/* 1093 */       writeLock.unlock();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void delAllService()
/*      */   {
/* 1102 */     Lock writeLock = this.cacheLock.writeLock();
/* 1103 */     writeLock.lock();
/*      */     
/*      */     try
/*      */     {
/* 1107 */       Set<String> names = new HashSet();
/* 1108 */       this.instanceCache.clear();
/* 1109 */       this.serviceCache.clear();
/* 1110 */       for (String name : this.nameCache.keySet())
/*      */       {
/* 1112 */         names.add(name);
/*      */       }
/* 1114 */       this.nameCache.clear();
/* 1115 */       notifyListeners(names);
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/* 1119 */       LOGGER.error("Zookeeper:" + this.regId + " remove all service failed!", t);
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/* 1124 */       writeLock.unlock();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void delServiceinstance(List<String> list)
/*      */   {
/* 1134 */     if (!list.isEmpty())
/*      */     {
/* 1136 */       Set<String> names = new HashSet();
/* 1137 */       for (String urlPath : list)
/*      */       {
/*      */         String url;
/*      */         try
/*      */         {
/* 1142 */           url = URLDecoder.decode(urlPath, "UTF-8");
/*      */         }
/*      */         catch (UnsupportedEncodingException e)
/*      */         {
/* 1146 */           LOGGER.error("Zookeeper:" + this.regId + " decoder url failed");
/* 1147 */           throw new RuntimeException(e.getMessage(), e);
/*      */         }
/*      */         
/* 1150 */         ServiceInstanceInner instanceInner = Utils.toInstanceInner(url);
/* 1151 */         names.add(instanceInner.getInstanceName());
/* 1152 */         removeInstance(instanceInner);
/* 1153 */         if (LOGGER.isInfoEnable())
/*      */         {
/* 1155 */           LOGGER.info("Zookeeper:" + this.regId + " delete service instance inner, service name is " + instanceInner.getInstanceName() + ", service address is " + instanceInner.getAddress() + ", servicer group is " + instanceInner.getGroup() + ", service type is " + instanceInner.getServiceType());
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1166 */       notifyListeners(names);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void registerConsumers(List<ServiceInner> consumers)
/*      */   {
/* 1174 */     if (Utils.isEmpty(consumers))
/*      */     {
/* 1176 */       return;
/*      */     }
/* 1178 */     List<String> names = getNames(consumers, true, true);
/* 1179 */     synchronized (this.registeredConsumerNames)
/*      */     {
/* 1181 */       checkRegisteredNames(names, true);
/* 1182 */       addRegisteredConsumerNames(names);
/*      */     }
/* 1184 */     List<ConsumerInstanceInner> instancesList = new ArrayList();
/* 1185 */     for (ServiceInner consumer : consumers)
/*      */     {
/* 1187 */       instancesList.addAll(consumer.getConsumerInstances());
/*      */     }
/*      */     
/* 1190 */     for (ConsumerInstanceInner instance : instancesList)
/*      */     {
/* 1192 */       if ((instance.getType().equals("POJO")) && (instance.getBeanName() != null))
/*      */       {
/*      */ 
/* 1195 */         addConsumerInstance(instance);
/* 1196 */         registerConsumersInstance(instance);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addConsumerInstance(ConsumerInstanceInner instance)
/*      */   {
/* 1209 */     String beanName = instance.getBeanName();
/* 1210 */     if (beanName != null)
/*      */     {
/* 1212 */       List<ConsumerInstanceInner> list = (List)this.consumerinstanceMap.get(beanName);
/*      */       
/* 1214 */       if (list == null)
/*      */       {
/* 1216 */         List<ConsumerInstanceInner> consumerList = new ArrayList();
/* 1217 */         consumerList.add(instance);
/* 1218 */         this.consumerinstanceMap.put(beanName, consumerList);
/*      */ 
/*      */ 
/*      */       }
/* 1222 */       else if (!list.contains(instance))
/*      */       {
/* 1224 */         list.add(instance);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void registerConsumersInstance(ConsumerInstanceInner instance)
/*      */   {
/* 1232 */     this.registryAdapter.registerConsumerInstance(instance);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setServiceandSdl(String service, byte[] data)
/*      */   {
/* 1238 */     if (null == this.registryAdapter)
/*      */     {
/* 1240 */       return;
/*      */     }
/*      */     
/* 1243 */     ServiceInner serviceInner = this.registryAdapter.deserializeServiceData(service, data);
/*      */     
/* 1245 */     if (serviceInner != null)
/*      */     {
/* 1247 */       if (LOGGER.isInfoEnable())
/*      */       {
/* 1249 */         LOGGER.info(String.format("Zookeeper:" + this.regId + " add service [%s] to cache: %s", new Object[] { service, serviceInner.toString() }));
/*      */       }
/*      */       
/*      */ 
/* 1253 */       addService(serviceInner);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void delSdl(String serviceName)
/*      */   {
/* 1261 */     if (this.nameCache.containsKey(serviceName))
/*      */     {
/* 1263 */       String interfaceName = (String)this.nameCache.get(serviceName);
/* 1264 */       removeService(interfaceName);
/*      */     }
/*      */   }
/*      */   
/*      */   private void addService(ServiceInner service)
/*      */   {
/* 1270 */     Lock writeLock = this.cacheLock.writeLock();
/* 1271 */     writeLock.lock();
/*      */     
/*      */     try
/*      */     {
/* 1275 */       String infName = service.getName();
/*      */       
/* 1277 */       List<ServiceInner> serviceList = (List)this.serviceCache.get(infName);
/* 1278 */       if (serviceList == null)
/*      */       {
/* 1280 */         serviceList = new ArrayList();
/* 1281 */         serviceList.add(service);
/* 1282 */         this.serviceCache.put(infName, serviceList);
/*      */ 
/*      */ 
/*      */       }
/* 1286 */       else if (!serviceList.contains(service))
/*      */       {
/* 1288 */         serviceList.add(service);
/*      */       }
/*      */       
/*      */ 
/* 1292 */       if (LOGGER.isInfoEnable())
/*      */       {
/*      */ 
/* 1295 */         LOGGER.info("add service to serviceCache, service name: " + service.getName() + ",group: " + service.getGroup() + ",service type: " + service.getServiceType());
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*      */ 
/* 1303 */       LOGGER.error("add service failed!", t);
/*      */     }
/*      */     finally
/*      */     {
/* 1307 */       writeLock.unlock();
/*      */     }
/*      */   }
/*      */   
/*      */   private void removeService(String infName)
/*      */   {
/* 1313 */     Lock writeLock = this.cacheLock.writeLock();
/* 1314 */     writeLock.lock();
/*      */     
/*      */     try
/*      */     {
/* 1318 */       this.serviceCache.remove(infName);
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/* 1322 */       LOGGER.error("Zookeeper:" + this.regId + " remove service failed!", t);
/*      */     }
/*      */     finally
/*      */     {
/* 1326 */       writeLock.unlock();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Boolean findWatcerIsOnService(String name)
/*      */   {
/* 1333 */     Lock readLock = this.serviceCacheLock.readLock();
/* 1334 */     readLock.lock();
/* 1335 */     Boolean isWatcher = null;
/*      */     try
/*      */     {
/* 1338 */       isWatcher = (Boolean)this.serviceWatcherCache.get(name);
/* 1339 */       if (isWatcher == null)
/*      */       {
/* 1341 */         isWatcher = Boolean.valueOf(false);
/*      */       }
/*      */       else
/*      */       {
/* 1345 */         isWatcher = Boolean.valueOf(true);
/*      */       }
/*      */       
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/* 1351 */       LOGGER.error("Zookeeper:" + this.regId + " find watcher on service failed!", t);
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/* 1356 */       readLock.unlock();
/*      */     }
/* 1358 */     return isWatcher;
/*      */   }
/*      */   
/*      */ 
/*      */   public void addWatchOnService(String serviceName, boolean isWatch, boolean ops)
/*      */   {
/* 1364 */     Lock writeLock = this.serviceCacheLock.writeLock();
/* 1365 */     writeLock.lock();
/*      */     
/*      */     try
/*      */     {
/* 1369 */       if (ops)
/*      */       {
/* 1371 */         this.opsWatcherCache.put(serviceName, Boolean.valueOf(isWatch));
/*      */       }
/*      */       else
/*      */       {
/* 1375 */         this.serviceWatcherCache.put(serviceName, Boolean.valueOf(isWatch));
/*      */       }
/* 1377 */       if (LOGGER.isInfoEnable())
/*      */       {
/* 1379 */         LOGGER.info("add watcher on the service instance to instanceCache,instance name: " + serviceName);
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/* 1386 */       LOGGER.error("add watcher on instance failed!", t);
/*      */     }
/*      */     finally
/*      */     {
/* 1390 */       writeLock.unlock();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void findServiceDataFromZk(String name)
/*      */   {
/* 1397 */     if (!findWatcerIsOnService(name).booleanValue())
/*      */     {
/* 1399 */       this.registry.getServiceDatafromzk(name);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void registerDelayedServices()
/*      */   {
/* 1406 */     if (!this.delayServicesList.isEmpty())
/*      */     {
/* 1408 */       registerServices(this.delayServicesList, false);
/*      */     }
/*      */   }
/*      */   
/*      */   public void addServiceWatcher(List<ServiceInner> services)
/*      */   {
/* 1414 */     List<ServiceInstanceInner> instancesList = new ArrayList();
/* 1415 */     for (ServiceInner service : services)
/*      */     {
/* 1417 */       instancesList.addAll(service.getInstances());
/*      */     }
/* 1419 */     for (ServiceInstanceInner instance : instancesList)
/*      */     {
/* 1421 */       if ((instance.getInstanceName() != null) && (!StringUtils.isEmpty(instance.getInstanceName())))
/*      */       {
/*      */ 
/* 1424 */         this.registry.addWatcherService(instance.getInstanceName());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public ZkRegistryAdapter getRegistryAdapter()
/*      */   {
/* 1432 */     return this.registryAdapter;
/*      */   }
/*      */   
/*      */ 
/*      */   public void setRegistry(Registry registry)
/*      */   {
/* 1438 */     this.registry = registry;
/*      */   }
/*      */   
/*      */ 
/*      */   public void setZookeeperDataManager(DsfZookeeperDataManager dataManager)
/*      */   {
/* 1444 */     this.dataManager = dataManager;
/*      */   }
/*      */   
/*      */ 
/*      */   public DsfZookeeperDataManager getZookeeperDataManager()
/*      */   {
/* 1450 */     return this.dataManager;
/*      */   }
/*      */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\SRAgentImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */