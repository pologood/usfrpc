/*    */ package com.huawei.csc.usf.framework.circuitbreaker;
/*    */ 
/*    */ import com.huawei.csc.kernel.api.log.LogFactory;
/*    */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*    */ import com.huawei.csc.usf.framework.Context;
/*    */ import com.huawei.csc.usf.framework.ServiceEngine;
/*    */ import com.huawei.csc.usf.framework.sr.ConsumerConfigUpdateListener;
/*    */ import com.huawei.csc.usf.framework.sr.DsfZookeeperDataManager;
/*    */ import com.huawei.csc.usf.framework.sr.SRAgentFactory;
/*    */ import com.huawei.csc.usf.framework.sr.ServiceRegistryAgent;
/*    */ import java.util.Collection;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.concurrent.atomic.AtomicBoolean;
/*    */ 
/*    */ public class DSFFaultTolerancePropertiesCache
/*    */   implements ConsumerConfigUpdateListener
/*    */ {
/* 20 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(DSFFaultTolerancePropertiesCache.class);
/*    */   
/*    */ 
/* 23 */   private static HashMap<String, DSFFaultToleranceProperties> faultToleranceProperties = new HashMap();
/*    */   
/* 25 */   private static final Object lock = new Object();
/*    */   
/*    */ 
/*    */ 
/*    */   public void init(ServiceEngine engine)
/*    */   {
/* 31 */     Map<String, ServiceRegistryAgent> serviceAgentMap = engine.getSrAgentFactory().getServiceAgentMap();
/* 32 */     for (Map.Entry<String, ServiceRegistryAgent> entry : serviceAgentMap.entrySet())
/*    */     {
/* 34 */       ((ServiceRegistryAgent)entry.getValue()).getZookeeperDataManager().addConsumerConfigUpdateListener(this);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public static DSFFaultToleranceProperties getInstance(String serviceName, Context context)
/*    */   {
/* 41 */     DSFFaultToleranceProperties circuitBreakerProperties = (DSFFaultToleranceProperties)faultToleranceProperties.get(serviceName);
/*    */     
/*    */ 
/* 44 */     if (circuitBreakerProperties == null)
/*    */     {
/*    */ 
/* 47 */       synchronized (lock)
/*    */       {
/*    */ 
/* 50 */         circuitBreakerProperties = (DSFFaultToleranceProperties)faultToleranceProperties.get(serviceName);
/*    */         
/* 52 */         if (null == circuitBreakerProperties)
/*    */         {
/* 54 */           circuitBreakerProperties = new DSFFaultToleranceProperties().buildCircuitBreakerProperties(context);
/*    */           
/* 56 */           faultToleranceProperties.put(serviceName, circuitBreakerProperties);
/*    */           
/*    */ 
/* 59 */           DsfCircuitBreaker dsfCircuitBreaker = DsfCircuitBreaker.Factory.getInstance(serviceName);
/* 60 */           boolean circuitBreakerEnable = Boolean.parseBoolean(circuitBreakerProperties.getCircuitBreakerEnable());
/* 61 */           if ((null != dsfCircuitBreaker) && (dsfCircuitBreaker.getCircuitOpen().get()) && (!circuitBreakerEnable))
/*    */           {
/*    */ 
/* 64 */             dsfCircuitBreaker.reset();
/*    */           }
/*    */         }
/*    */       }
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 72 */     return circuitBreakerProperties;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void onConsumerConfigUpdate(String zkId, Collection<String> names)
/*    */   {
/* 79 */     synchronized (lock)
/*    */     {
/* 81 */       HashMap<String, DSFFaultToleranceProperties> tmpFaultToleranceProperties = new HashMap(faultToleranceProperties);
/*    */       
/* 83 */       for (String name : names)
/*    */       {
/* 85 */         tmpFaultToleranceProperties.remove(name);
/*    */       }
/*    */       
/* 88 */       faultToleranceProperties = tmpFaultToleranceProperties;
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\circuitbreaker\DSFFaultTolerancePropertiesCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */