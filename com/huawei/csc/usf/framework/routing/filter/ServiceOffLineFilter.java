/*    */ package com.huawei.csc.usf.framework.routing.filter;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.Connector;
/*    */ import com.huawei.csc.usf.framework.Context;
/*    */ import com.huawei.csc.usf.framework.IMessage;
/*    */ import com.huawei.csc.usf.framework.MessageHeaders;
/*    */ import com.huawei.csc.usf.framework.ServiceEngine;
/*    */ import com.huawei.csc.usf.framework.sr.DsfZookeeperDataManager;
/*    */ import com.huawei.csc.usf.framework.sr.SRAgentFactory;
/*    */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
/*    */ import com.huawei.csc.usf.framework.sr.ServiceRegistryAgent;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ServiceOffLineFilter
/*    */   implements RoutingServiceInstanceFilter
/*    */ {
/*    */   public void filter(List<ServiceInstanceInner> instances, Context context)
/*    */   {
/* 30 */     IMessage receivedMessage = context.getReceivedMessage();
/* 31 */     MessageHeaders headers = receivedMessage.getHeaders();
/* 32 */     String serviceName = headers.getServiceName();
/*    */     
/*    */ 
/*    */ 
/* 36 */     DsfZookeeperDataManager zookeeperDataManager = context.getSrcConnector().getServiceEngine().getSrAgentFactory().getSRAgent(context.getRegistry()).getZookeeperDataManager();
/*    */     
/* 38 */     Iterator<ServiceInstanceInner> iterator = instances.iterator();
/* 39 */     while (iterator.hasNext())
/*    */     {
/* 41 */       ServiceInstanceInner instance = (ServiceInstanceInner)iterator.next();
/* 42 */       if (!zookeeperDataManager.isActive(serviceName, instance))
/*    */       {
/* 44 */         iterator.remove();
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public String getFilterName()
/*    */   {
/* 52 */     return "service off line filter";
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\filter\ServiceOffLineFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */