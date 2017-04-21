/*     */ package com.huawei.csc.usf.framework.routing;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.monitor.delay.AdaptiveAlgorithmUtil;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicLong;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WeightServiceAddress
/*     */   implements ServiceAddress
/*     */ {
/*  37 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(WeightServiceAddress.class);
/*     */   
/*     */ 
/*  40 */   private ConcurrentHashMap<String, AtomicInteger> currentCwMap = new ConcurrentHashMap();
/*     */   
/*  42 */   private ConcurrentHashMap<String, AtomicLong> idxMap = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ServiceInstanceInner getRouterAddress(List<ServiceInstanceInner> serviceInstances, RoutingContext routingContext)
/*     */   {
/*  52 */     for (ServiceInstanceInner serviceInstanceInner : serviceInstances)
/*     */     {
/*  54 */       if (routingContext.getListenAddress().equals(serviceInstanceInner.getAddress()))
/*     */       {
/*     */ 
/*  57 */         return serviceInstanceInner;
/*     */       }
/*     */     }
/*  60 */     return getSelectedServiceAddr(serviceInstances, routingContext.getMsg());
/*     */   }
/*     */   
/*     */ 
/*     */   private ServiceInstanceInner getSelectedServiceAddr(List<ServiceInstanceInner> serviceInstances, IMessage msg)
/*     */   {
/*  66 */     MessageHeaders headers = msg.getHeaders();
/*  67 */     String serviceName = headers.getServiceName();
/*  68 */     ServiceInstanceInner instance = null;
/*  69 */     AtomicInteger currentCw = (AtomicInteger)this.currentCwMap.get(serviceName);
/*  70 */     if (null == currentCw)
/*     */     {
/*  72 */       this.currentCwMap.putIfAbsent(serviceName, new AtomicInteger(0));
/*  73 */       currentCw = (AtomicInteger)this.currentCwMap.get(serviceName);
/*     */     }
/*  75 */     AtomicLong idx = (AtomicLong)this.idxMap.get(serviceName);
/*  76 */     if (null == idx)
/*     */     {
/*  78 */       this.idxMap.putIfAbsent(serviceName, new AtomicLong(-1L));
/*  79 */       idx = (AtomicLong)this.idxMap.get(serviceName);
/*     */     }
/*  81 */     int serviceNum = serviceInstances.size();
/*  82 */     List<Integer> values = new ArrayList();
/*     */     
/*  84 */     for (int i = 0; i < serviceNum; i++)
/*     */     {
/*  86 */       ServiceInstanceInner serviceInstanceInner = (ServiceInstanceInner)serviceInstances.get(i);
/*  87 */       values.add(Integer.valueOf(serviceInstanceInner.getWeight()));
/*     */     }
/*  89 */     if ((idx != null) && (currentCw != null))
/*     */     {
/*  91 */       instance = (ServiceInstanceInner)serviceInstances.get(AdaptiveAlgorithmUtil.nextAddrIndex(values, idx, currentCw, serviceNum));
/*     */     }
/*     */     
/*     */ 
/*  95 */     if (DEBUGGER.isDebugEnable())
/*     */     {
/*  97 */       StringBuilder sBuilder = new StringBuilder();
/*  98 */       sBuilder.append(headers.getGroup()).append("#").append(headers.getServiceName()).append("#").append(null == instance ? "" : instance.getAddress());
/*     */       
/*     */ 
/* 101 */       String key = sBuilder.toString();
/* 102 */       DEBUGGER.debug(MessageFormat.format("selected service key  is   {0} ", new Object[] { key }));
/*     */     }
/*     */     
/* 105 */     return instance;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\WeightServiceAddress.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */