/*     */ package com.huawei.csc.usf.framework.circuitbreaker;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.sr.ConsumerConfigUpdateListener;
/*     */ import com.huawei.csc.usf.framework.sr.DsfZookeeperDataManager;
/*     */ import com.huawei.csc.usf.framework.sr.SRAgentFactory;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceRegistryAgent;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ public class TryableSemaphoreCache
/*     */   implements ConsumerConfigUpdateListener
/*     */ {
/*  21 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(TryableSemaphoreCache.class);
/*     */   
/*     */ 
/*  24 */   public static final HashMap<String, HashMap<String, TryableSemaphore>> semps = new HashMap();
/*     */   
/*  26 */   public static final Object lock = new Object();
/*     */   
/*     */ 
/*     */   public ServiceEngine engine;
/*     */   
/*     */ 
/*     */   public void init(ServiceEngine engine)
/*     */   {
/*  34 */     this.engine = engine;
/*  35 */     Map<String, ServiceRegistryAgent> serviceAgentMap = engine.getSrAgentFactory().getServiceAgentMap();
/*     */     
/*  37 */     for (Map.Entry<String, ServiceRegistryAgent> entry : serviceAgentMap.entrySet())
/*     */     {
/*     */ 
/*  40 */       ((ServiceRegistryAgent)entry.getValue()).getZookeeperDataManager().addConsumerConfigUpdateListener(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TryableSemaphore getInstance(String serviceName, Context context)
/*     */   {
/*  49 */     HashMap<String, TryableSemaphore> region = (HashMap)semps.get(context.getRegistry());
/*     */     
/*     */ 
/*  52 */     if ((null == region) || (null == region.get(serviceName)))
/*     */     {
/*  54 */       synchronized (lock)
/*     */       {
/*  56 */         region = (HashMap)semps.get(context.getRegistry());
/*  57 */         if (null != region)
/*     */         {
/*  59 */           if (null == region.get(serviceName))
/*     */           {
/*  61 */             DSFFaultToleranceProperties circuitBreakerProperties = new DSFFaultToleranceProperties().buildCircuitBreakerProperties(context);
/*     */             
/*  63 */             region.put(serviceName, new TryableSemaphore(circuitBreakerProperties.getBulkheadMaxConcurrentRequests().intValue()));
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/*  71 */           DSFFaultToleranceProperties circuitBreakerProperties = new DSFFaultToleranceProperties().buildCircuitBreakerProperties(context);
/*     */           
/*  73 */           HashMap<String, TryableSemaphore> semaphore = new HashMap();
/*  74 */           semaphore.put(serviceName, new TryableSemaphore(circuitBreakerProperties.getBulkheadMaxConcurrentRequests().intValue()));
/*     */           
/*     */ 
/*     */ 
/*  78 */           semps.put(context.getRegistry(), semaphore);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*  83 */     return (TryableSemaphore)((HashMap)semps.get(context.getRegistry())).get(serviceName);
/*     */   }
/*     */   
/*     */   public void onConsumerConfigUpdate(String zkId, Collection<String> names)
/*     */   {
/*     */     HashMap<String, TryableSemaphore> region;
/*  89 */     synchronized (lock)
/*     */     {
/*  91 */       region = (HashMap)semps.get(zkId);
/*  92 */       if (null != region)
/*     */       {
/*  94 */         for (String name : names)
/*     */         {
/*  96 */           Integer limit = getZkConfigSemaphoreLimit(zkId, name);
/*     */           
/*  98 */           TryableSemaphore sempahore = (TryableSemaphore)region.get(name);
/*     */           
/* 100 */           if (null != sempahore)
/*     */           {
/* 102 */             if (null != limit)
/*     */             {
/* 104 */               sempahore.setNumberOfPermits(limit.intValue());
/*     */             }
/*     */             else
/*     */             {
/* 108 */               sempahore.setNumberOfPermits(new DSFFaultToleranceProperties().getBulkheadMaxConcurrentRequests().intValue());
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Integer getZkConfigSemaphoreLimit(String zkId, String serviceName)
/*     */   {
/* 122 */     String dsfApplication = this.engine.getSystemConfig().getDsfApplication();
/*     */     
/* 124 */     String rpcAddr = getRpcAddr();
/* 125 */     Object object = this.engine.getSrAgentFactory().getSRAgent(zkId).getZookeeperDataManager().getConfigFromConfiguration(serviceName, "&" + serviceName, rpcAddr, dsfApplication, "maxConcurrentRequests", "consumer");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 130 */     Integer maxConcurrentRequests = null;
/* 131 */     if (object != null)
/*     */     {
/* 133 */       maxConcurrentRequests = Integer.valueOf(Integer.parseInt(String.valueOf(object)));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 139 */     return maxConcurrentRequests;
/*     */   }
/*     */   
/*     */   private String getRpcAddr()
/*     */   {
/* 144 */     SystemConfig systemConfig = this.engine.getSystemConfig();
/* 145 */     String rpcAddr = systemConfig.getRPCAddress(ServiceType.DSF);
/*     */     
/* 147 */     rpcAddr = null == rpcAddr ? systemConfig.getRPCAddress(ServiceType.EBUS) : rpcAddr;
/*     */     
/*     */ 
/* 150 */     rpcAddr = null == rpcAddr ? systemConfig.getRPCAddress(ServiceType.USF) : rpcAddr;
/*     */     
/*     */ 
/* 153 */     return rpcAddr;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\circuitbreaker\TryableSemaphoreCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */