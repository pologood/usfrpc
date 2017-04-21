/*     */ package com.huawei.csc.usf.framework.routing;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.monitor.delay.AdaptiveAlgorithmUtil;
/*     */ import com.huawei.csc.usf.framework.monitor.delay.ServiceDelayTimeCountCenter;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DelayTimeServiceAddress
/*     */   implements ServiceAddress
/*     */ {
/*  24 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(DelayTimeServiceAddress.class);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  30 */   protected ConcurrentHashMap<String, AtomicLong> serviceIndexMap = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  35 */   protected ConcurrentHashMap<String, AtomicInteger> cwMap = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ServiceInstanceInner getRouterAddress(List<ServiceInstanceInner> serviceInstances, RoutingContext routingContext)
/*     */   {
/*  42 */     for (ServiceInstanceInner serviceInstanceInner : serviceInstances)
/*     */     {
/*  44 */       if (routingContext.getListenAddress().equals(serviceInstanceInner.getAddress()))
/*     */       {
/*     */ 
/*  47 */         return serviceInstanceInner;
/*     */       }
/*     */     }
/*  50 */     return getSelectedServiceAddr(serviceInstances, ServiceDelayTimeCountCenter.getInstance().getServiceInstancePowerMap(), routingContext.getMsg());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ServiceInstanceInner getSelectedServiceAddr(List<ServiceInstanceInner> serviceInstances, Map<String, Integer> powerMap, IMessage msg)
/*     */   {
/*  59 */     List<Integer> values = new ArrayList();
/*  60 */     MessageHeaders headers = msg.getHeaders();
/*  61 */     String serviceName = headers.getServiceName();
/*  62 */     for (ServiceInstanceInner serviceInstanceInner : serviceInstances)
/*     */     {
/*  64 */       StringBuilder sBuilder = new StringBuilder();
/*  65 */       sBuilder.append(headers.getGroup()).append("#").append(headers.getServiceName()).append("#").append(serviceInstanceInner.getAddress());
/*     */       
/*     */ 
/*  68 */       String key = sBuilder.toString();
/*  69 */       if (!powerMap.containsKey(key))
/*     */       {
/*  71 */         powerMap.put(key, AdaptiveAlgorithmUtil.INIT_WEIGHT);
/*     */       }
/*  73 */       values.add(powerMap.get(key));
/*     */     }
/*  75 */     ServiceInstanceInner instance = (ServiceInstanceInner)serviceInstances.get(nextIndex(serviceName, values, serviceInstances.size()));
/*     */     
/*     */ 
/*  78 */     if (DEBUGGER.isDebugEnable())
/*     */     {
/*  80 */       StringBuilder sBuilder = new StringBuilder();
/*  81 */       sBuilder.append(headers.getGroup()).append("#").append(headers.getServiceName()).append("#").append(instance.getAddress());
/*     */       
/*     */ 
/*  84 */       String key = sBuilder.toString();
/*  85 */       DEBUGGER.debug(MessageFormat.format("selected service key  is   {0}   and current cw is {1}", new Object[] { key, this.cwMap.get(serviceName) }));
/*     */     }
/*     */     
/*     */ 
/*  89 */     return instance;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected int nextIndex(String serviceName, List<Integer> values, int serviceNum)
/*     */   {
/*  96 */     AtomicInteger currentCw = (AtomicInteger)this.cwMap.get(serviceName);
/*  97 */     if (null == currentCw)
/*     */     {
/*  99 */       this.cwMap.putIfAbsent(serviceName, new AtomicInteger(0));
/* 100 */       currentCw = (AtomicInteger)this.cwMap.get(serviceName);
/*     */     }
/*     */     
/* 103 */     AtomicLong idx = (AtomicLong)this.serviceIndexMap.get(serviceName);
/* 104 */     if (null == idx)
/*     */     {
/* 106 */       this.serviceIndexMap.putIfAbsent(serviceName, new AtomicLong(-1L));
/* 107 */       idx = (AtomicLong)this.serviceIndexMap.get(serviceName);
/*     */     }
/* 109 */     int pickIndex = 0;
/* 110 */     if ((null != idx) && (null != currentCw))
/*     */     {
/* 112 */       pickIndex = AdaptiveAlgorithmUtil.nextAddrIndex(values, idx, currentCw, serviceNum);
/*     */     }
/*     */     
/* 115 */     return pickIndex;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\DelayTimeServiceAddress.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */