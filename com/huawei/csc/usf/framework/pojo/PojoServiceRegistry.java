/*     */ package com.huawei.csc.usf.framework.pojo;
/*     */ 
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.service.ServicePublishListener;
/*     */ import com.huawei.csc.usf.framework.sr.SRAgentFactory;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceInner;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CopyOnWriteArraySet;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PojoServiceRegistry
/*     */ {
/*  43 */   private Set<ServicePublishListener> listeners = new CopyOnWriteArraySet();
/*     */   
/*     */   protected PojoConnector pojoConnector;
/*     */   
/*     */   public void setPojoConnector(PojoConnector connector)
/*     */   {
/*  49 */     this.pojoConnector = connector;
/*     */   }
/*     */   
/*     */   public ServiceType getType()
/*     */   {
/*  54 */     return ServiceType.USF;
/*     */   }
/*     */   
/*     */ 
/*     */   public void addServicePublishListener(ServicePublishListener servicePublishListener)
/*     */   {
/*  60 */     this.listeners.add(servicePublishListener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean register(PojoServerInner pojoServer)
/*     */   {
/*  71 */     String serviceName = pojoServer.getServiceName();
/*     */     
/*  73 */     Map<String, PojoServerInner> serviceMap = new HashMap();
/*  74 */     serviceMap.put(serviceName, pojoServer);
/*  75 */     this.pojoConnector.setServerMapper(serviceMap);
/*     */     
/*  77 */     ServiceEngine engine = this.pojoConnector.getServiceEngine();
/*     */     
/*  79 */     List<String> serviceNames = new ArrayList();
/*  80 */     serviceNames.add(serviceName);
/*  81 */     engine.addServiceConnectorMapping(serviceNames, this.pojoConnector);
/*     */     
/*  83 */     ServiceInner service = pojoServer.parseService(this.pojoConnector);
/*  84 */     List<ServiceInner> services = new ArrayList();
/*  85 */     if (null != service)
/*     */     {
/*  87 */       services.add(service);
/*     */     }
/*     */     
/*  90 */     engine.getSrAgentFactory().registerServicesOnDefaultSR(services, false);
/*  91 */     notifyListeners(services);
/*  92 */     return true;
/*     */   }
/*     */   
/*     */   private void notifyListeners(List<ServiceInner> services)
/*     */   {
/*  97 */     for (ServicePublishListener servicePublishListener : this.listeners)
/*     */     {
/*  99 */       servicePublishListener.onServicePublish(services);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean unregister(String serviceName)
/*     */   {
/* 106 */     PojoServerInner pojoServer = (PojoServerInner)this.pojoConnector.getServerMapper().remove(serviceName);
/*     */     
/* 108 */     if (null == pojoServer)
/*     */     {
/* 110 */       return true;
/*     */     }
/*     */     
/* 113 */     ServiceEngine engine = this.pojoConnector.getServiceEngine();
/* 114 */     List<String> serviceNames = new ArrayList();
/* 115 */     serviceNames.add(serviceName);
/* 116 */     engine.removeServiceConnectorMapping(serviceNames);
/*     */     
/* 118 */     ServiceInner service = pojoServer.parseService(this.pojoConnector);
/* 119 */     List<ServiceInner> services = new ArrayList();
/* 120 */     services.add(service);
/*     */     
/* 122 */     engine.getSrAgentFactory().unregisterServicesOnDefaultSR(services);
/*     */     
/* 124 */     return true;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\pojo\PojoServiceRegistry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */