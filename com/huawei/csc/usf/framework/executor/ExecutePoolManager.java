/*     */ package com.huawei.csc.usf.framework.executor;
/*     */ 
/*     */ import com.huawei.csc.container.api.ContextRegistry;
/*     */ import com.huawei.csc.container.api.IContextHolder;
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.ServiceDefinition;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.pojo.PojoServerInner;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.apache.commons.lang.StringUtils;
/*     */ 
/*     */ public class ExecutePoolManager
/*     */ {
/*  26 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(ExecutePoolManager.class);
/*     */   
/*     */ 
/*  29 */   private static ExecutePoolManager instance = new ExecutePoolManager();
/*     */   
/*  31 */   private Map<String, List<String>> serviceName2ePoolName = new HashMap();
/*     */   
/*  33 */   private Map<String, Set<ExecuteThreadPool>> serviceName2ePool = new HashMap();
/*     */   
/*  35 */   private Map<String, ExecuteThreadPool> allBeanName2ePool = new HashMap();
/*     */   
/*  37 */   private boolean isServicePool = false;
/*     */   
/*     */ 
/*  40 */   private ExecuteThreadPool[] sharedPool = null;
/*     */   
/*     */ 
/*  43 */   private ExecuteThreadPool[] allExecutePool = null;
/*     */   
/*     */ 
/*  46 */   private ExecuteThreadPool responseThreadPool = null;
/*     */   
/*  48 */   private boolean isInitExecutor = false;
/*     */   
/*     */ 
/*     */   public static final String RESPONSE_POOL = "responseThreadPool";
/*     */   
/*  53 */   private AtomicInteger executorIdx = new AtomicInteger();
/*     */   
/*     */   public ExecuteThreadPool[] getAllSharedPools()
/*     */   {
/*  57 */     return this.sharedPool;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExecuteThreadPool getResExecutorEx()
/*     */   {
/*  68 */     ExecuteThreadPool responsePool = this.responseThreadPool;
/*  69 */     if (null == responsePool)
/*     */     {
/*  71 */       responsePool = getExecutor(null);
/*  72 */       if (LOGGER.isErrorEnable())
/*     */       {
/*  74 */         LOGGER.error("response pool is empty, use shared pool to execute.");
/*     */       }
/*     */     }
/*  77 */     return responsePool;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExecuteThreadPool getResExecutor()
/*     */   {
/*  87 */     return this.responseThreadPool;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExecuteThreadPool getSharedExecutor(String serviceName)
/*     */   {
/*  97 */     if (null == this.sharedPool)
/*     */     {
/*  99 */       if (LOGGER.isErrorEnable())
/*     */       {
/* 101 */         LOGGER.error("get shared executor fail!");
/*     */       }
/* 103 */       return null;
/*     */     }
/*     */     
/* 106 */     int index = Math.abs(this.executorIdx.getAndIncrement() % this.sharedPool.length);
/*     */     
/* 108 */     if ((LOGGER.isDebugEnable()) && (!StringUtils.isEmpty(serviceName)))
/*     */     {
/* 110 */       LOGGER.debug(this.sharedPool[index].getBeanName() + " begin to exe task: " + serviceName);
/*     */     }
/*     */     
/* 113 */     return this.sharedPool[index];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExecuteThreadPool getExecutor(String serviceName)
/*     */   {
/* 125 */     if ((StringUtils.isBlank(serviceName)) || (!getServiceName2ePool().containsKey(serviceName)))
/*     */     {
/*     */ 
/* 128 */       return getSharedExecutor(serviceName);
/*     */     }
/*     */     
/* 131 */     Set<ExecuteThreadPool> ePoolBeanSet = (Set)getServiceName2ePool().get(serviceName);
/*     */     
/*     */ 
/*     */ 
/* 135 */     ExecuteThreadPool ePoolBean = getRandomEle(ePoolBeanSet);
/*     */     
/* 137 */     if (LOGGER.isDebugEnable())
/*     */     {
/* 139 */       LOGGER.debug(ePoolBean.getBeanName() + " begin to exe task " + serviceName);
/*     */     }
/*     */     
/* 142 */     return ePoolBean;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ExecuteThreadPool getRandomEle(Set<ExecuteThreadPool> set)
/*     */   {
/* 152 */     SecureRandom r = new SecureRandom();
/* 153 */     int random = r.nextInt(set.size());
/*     */     
/* 155 */     Iterator<ExecuteThreadPool> it = set.iterator();
/* 156 */     for (int i = 0; i < random; i++)
/*     */     {
/* 158 */       it.next();
/*     */     }
/* 160 */     return (ExecuteThreadPool)it.next();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<String> mapValueGetKeys(Map<String, List<String>> map, String value)
/*     */   {
/* 169 */     List<String> keys = new ArrayList();
/*     */     
/* 171 */     Set<Map.Entry<String, List<String>>> set = map.entrySet();
/* 172 */     Iterator<Map.Entry<String, List<String>>> it = set.iterator();
/*     */     
/* 174 */     while (it.hasNext())
/*     */     {
/* 176 */       Map.Entry<String, List<String>> entry = (Map.Entry)it.next();
/*     */       
/* 178 */       if (((List)entry.getValue()).contains(value))
/*     */       {
/* 180 */         keys.add((String)entry.getKey());
/*     */       }
/*     */     }
/*     */     
/* 184 */     return keys;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setServiceName2ePoolNameMap()
/*     */   {
/* 192 */     Map<String, PojoServerInner> exportServices = ContextRegistry.getContextHolder().getBeansOfType(PojoServerInner.class);
/*     */     
/*     */ 
/* 195 */     for (Map.Entry<String, PojoServerInner> entry : exportServices.entrySet())
/*     */     {
/* 197 */       PojoServerInner service = (PojoServerInner)entry.getValue();
/*     */       
/* 199 */       String executePoolName = service.getServiceDefinition().getThreadPool();
/*     */       
/*     */ 
/*     */ 
/* 203 */       if (!StringUtils.isEmpty(executePoolName))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 209 */         List<String> list = Arrays.asList(executePoolName.split(","));
/* 210 */         this.serviceName2ePoolName.put(service.getServiceName(), list);
/*     */       } }
/* 212 */     if (!this.serviceName2ePoolName.isEmpty())
/*     */     {
/* 214 */       this.isServicePool = true;
/*     */     }
/*     */   }
/*     */   
/*     */   public void initExecutorPool(SystemConfig conf)
/*     */   {
/* 220 */     synchronized (this)
/*     */     {
/* 222 */       if (!this.isInitExecutor)
/*     */       {
/* 224 */         Map<String, ExecuteThreadPool> poolBeans = ContextRegistry.getContextHolder().getBeansOfType(ExecuteThreadPool.class);
/*     */         
/*     */ 
/* 227 */         boolean containShared = false;
/* 228 */         boolean confResponseBean = false;
/*     */         
/* 230 */         setServiceName2ePoolNameMap();
/* 231 */         List<ExecuteThreadPool> sharedExePool = new java.util.LinkedList();
/*     */         
/*     */ 
/* 234 */         if (!poolBeans.isEmpty())
/*     */         {
/* 236 */           for (Map.Entry<String, ExecuteThreadPool> entry : poolBeans.entrySet())
/*     */           {
/*     */ 
/* 239 */             ExecuteThreadPool instanceBean = (ExecuteThreadPool)entry.getValue();
/* 240 */             String beanName = (String)entry.getKey();
/* 241 */             instanceBean.setBeanName(beanName);
/*     */             
/*     */ 
/* 244 */             if (StringUtils.equals(beanName, "responseBean"))
/*     */             {
/* 246 */               instanceBean.initialize("handleResponseThreads");
/* 247 */               this.responseThreadPool = instanceBean;
/* 248 */               confResponseBean = true;
/* 249 */               this.allBeanName2ePool.put("responseBean", instanceBean);
/*     */             }
/*     */             else
/*     */             {
/* 253 */               boolean shared = instanceBean.share;
/*     */               
/*     */ 
/* 256 */               List<String> serviceNames = mapValueGetKeys(this.serviceName2ePoolName, beanName);
/*     */               
/*     */ 
/* 259 */               if ((serviceNames.isEmpty()) && (!shared))
/*     */               {
/*     */ 
/* 262 */                 instanceBean.setBeanIsEnable(false);
/* 263 */                 if (LOGGER.isWarnEnable())
/*     */                 {
/* 265 */                   LOGGER.warn(beanName + " is not shared and no service can binding, so set to disable");
/*     */                 }
/*     */                 
/*     */ 
/*     */               }
/*     */               else
/*     */               {
/* 272 */                 instanceBean.initialize(StringUtils.join(serviceNames, ","));
/*     */                 
/*     */ 
/*     */ 
/* 276 */                 if (shared)
/*     */                 {
/* 278 */                   containShared = true;
/* 279 */                   sharedExePool.add(instanceBean);
/*     */                 }
/*     */                 
/* 282 */                 this.allBeanName2ePool.put(beanName, instanceBean);
/* 283 */                 adjustServiceName2ePool(serviceNames, instanceBean);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 288 */         if (!containShared)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 294 */           ExecuteThreadPool exeService = createDefaultBean(conf);
/* 295 */           sharedExePool.add(exeService);
/* 296 */           this.allBeanName2ePool.put("shared", exeService);
/*     */         }
/*     */         
/*     */ 
/* 300 */         if (!confResponseBean)
/*     */         {
/* 302 */           this.responseThreadPool = createResponseBean(conf);
/* 303 */           this.allBeanName2ePool.put("responseBean", this.responseThreadPool);
/*     */         }
/*     */         
/* 306 */         this.allExecutePool = new ExecuteThreadPool[this.allBeanName2ePool.size()];
/* 307 */         this.allBeanName2ePool.values().toArray(this.allExecutePool);
/*     */         
/* 309 */         this.sharedPool = new ExecuteThreadPool[sharedExePool.size()];
/* 310 */         sharedExePool.toArray(this.sharedPool);
/*     */       }
/* 312 */       this.isInitExecutor = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static ExecutePoolManager getInstance()
/*     */   {
/* 319 */     return instance;
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
/*     */   private void adjustServiceName2ePool(List<String> serviceName, ExecuteThreadPool bean)
/*     */   {
/* 336 */     for (String name : serviceName)
/*     */     {
/* 338 */       if (this.serviceName2ePool.containsKey(name))
/*     */       {
/* 340 */         ((Set)this.serviceName2ePool.get(name)).add(bean);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 345 */         Set<ExecuteThreadPool> poolList = new HashSet();
/* 346 */         poolList.add(bean);
/* 347 */         this.serviceName2ePool.put(name, poolList);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private ExecuteThreadPool createDefaultBean(SystemConfig conf)
/*     */   {
/* 354 */     ExecuteThreadPool exeService = new ExecuteThreadPool(conf.getWorkerCoreSize(), conf.getWorkerMaxSize(), 30, conf.getWorkerQueues(), conf.getWorkerGroupSize(), true);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 360 */     exeService.setBeanName("share-d");
/* 361 */     exeService.initialize();
/*     */     
/* 363 */     return exeService;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ExecuteThreadPool createResponseBean(SystemConfig conf)
/*     */   {
/* 374 */     ExecuteThreadPool exeService = null;
/*     */     try
/*     */     {
/* 377 */       exeService = new ExecuteThreadPool(conf.getResponseCoreSize(), conf.getResponseMaxSize(), 30, conf.getResponseQueues(), conf.getResponseGroupSize(), true);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 383 */       exeService.setBeanName("responseBean");
/* 384 */       exeService.initialize("handleResponseThreads");
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 388 */       LOGGER.error("create response pool fail", e);
/*     */     }
/*     */     
/*     */ 
/* 392 */     return exeService;
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, Set<ExecuteThreadPool>> getServiceName2ePool()
/*     */   {
/* 398 */     return this.serviceName2ePool;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExecuteThreadPool getExecutePoolFromBean(String beanName)
/*     */   {
/* 410 */     ExecuteThreadPool servicePool = null;
/*     */     
/* 412 */     if (StringUtils.isBlank(beanName))
/*     */     {
/* 414 */       LOGGER.debug("Given bean name is null or empty!");
/* 415 */       return servicePool;
/*     */     }
/*     */     
/* 418 */     if (!this.allBeanName2ePool.containsKey(beanName))
/*     */     {
/* 420 */       if (LOGGER.isDebugEnable())
/*     */       {
/* 422 */         LOGGER.debug("can not find bean:" + beanName);
/*     */       }
/* 424 */       return servicePool;
/*     */     }
/*     */     
/* 427 */     servicePool = (ExecuteThreadPool)this.allBeanName2ePool.get(beanName);
/*     */     
/* 429 */     if (!servicePool.getBeanIsEnable())
/*     */     {
/* 431 */       if (LOGGER.isDebugEnable())
/*     */       {
/* 433 */         LOGGER.debug(servicePool.getBeanName() + "is not enable");
/*     */       }
/* 435 */       return null;
/*     */     }
/*     */     
/* 438 */     return servicePool;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExecuteThreadPool[] getAllExecutePool()
/*     */   {
/* 449 */     return this.allExecutePool;
/*     */   }
/*     */   
/*     */   public void destory()
/*     */   {
/* 454 */     synchronized (this)
/*     */     {
/* 456 */       if (this.isInitExecutor)
/*     */       {
/* 458 */         threadPoolShutdownNow(this.allExecutePool);
/*     */         
/* 460 */         this.isInitExecutor = false;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void destroyGracefully()
/*     */   {
/* 467 */     synchronized (this)
/*     */     {
/* 469 */       if (this.isInitExecutor)
/*     */       {
/* 471 */         threadPoolShutdown(this.allExecutePool);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isReadyToDestroy()
/*     */   {
/* 478 */     boolean isReady = true;
/* 479 */     synchronized (this)
/*     */     {
/* 481 */       if (this.isInitExecutor)
/*     */       {
/* 483 */         for (ExecuteThreadPool pool : this.allExecutePool)
/*     */         {
/* 485 */           if (!pool.isReadyToDestroy())
/*     */           {
/* 487 */             isReady = false;
/* 488 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 493 */     return isReady;
/*     */   }
/*     */   
/*     */   private void threadPoolShutdownNow(ExecuteThreadPool... pools)
/*     */   {
/* 498 */     if (null == pools)
/*     */     {
/* 500 */       return;
/*     */     }
/* 502 */     for (ExecuteThreadPool pool : pools)
/*     */     {
/* 504 */       if (null != pool)
/*     */       {
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/* 510 */           pool.shutdownNow();
/*     */         }
/*     */         catch (Exception ex)
/*     */         {
/* 514 */           LOGGER.warn("Executor thread pool [" + pool + "] +shoutdown failed.", ex);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void threadPoolShutdown(ExecuteThreadPool... pools)
/*     */   {
/* 522 */     if (null == pools)
/*     */     {
/* 524 */       return;
/*     */     }
/* 526 */     for (ExecuteThreadPool pool : pools)
/*     */     {
/* 528 */       if (null != pool)
/*     */       {
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/* 534 */           pool.shutdown();
/*     */         }
/*     */         catch (Exception ex)
/*     */         {
/* 538 */           LOGGER.warn("Executor thread pool [" + pool + "] +shoutdown failed.", ex);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public final boolean isServicePool()
/*     */   {
/* 547 */     return this.isServicePool;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\executor\ExecutePoolManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */