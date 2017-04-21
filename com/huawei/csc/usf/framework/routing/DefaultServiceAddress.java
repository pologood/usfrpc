/*    */ package com.huawei.csc.usf.framework.routing;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.IMessage;
/*    */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ 
/*    */ public class DefaultServiceAddress implements ServiceAddress
/*    */ {
/* 11 */   protected ConcurrentHashMap<String, AtomicInteger> idxMap = new ConcurrentHashMap();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ServiceInstanceInner getRouterAddress(List<ServiceInstanceInner> serviceInstances, RoutingContext routingContext)
/*    */   {
/* 21 */     for (ServiceInstanceInner serviceInstanceInner : serviceInstances)
/*    */     {
/* 23 */       if (routingContext.getListenAddress().equals(serviceInstanceInner.getAddress()))
/*    */       {
/*    */ 
/* 26 */         return serviceInstanceInner;
/*    */       }
/*    */     }
/*    */     
/* 30 */     String serviceName = routingContext.getMsg().getHeaders().getServiceName();
/*    */     
/* 32 */     AtomicInteger idx = (AtomicInteger)this.idxMap.get(serviceName);
/* 33 */     if (null == idx)
/*    */     {
/* 35 */       this.idxMap.putIfAbsent(serviceName, new AtomicInteger(0));
/* 36 */       idx = (AtomicInteger)this.idxMap.get(serviceName);
/*    */     }
/* 38 */     ServiceInstanceInner instance = null;
/* 39 */     if (null != idx)
/*    */     {
/* 41 */       instance = (ServiceInstanceInner)serviceInstances.get(Math.abs(idx.getAndIncrement() % serviceInstances.size()));
/*    */     }
/*    */     
/* 44 */     return instance;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\DefaultServiceAddress.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */