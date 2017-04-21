/*    */ package com.huawei.csc.usf.framework.routing.filter;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.Connector;
/*    */ import com.huawei.csc.usf.framework.Context;
/*    */ import com.huawei.csc.usf.framework.ServiceEngine;
/*    */ import com.huawei.csc.usf.framework.bus.BusConnector;
/*    */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BusConnectorBlackListFilter
/*    */   implements RoutingServiceInstanceFilter
/*    */ {
/*    */   public void filter(List<ServiceInstanceInner> instances, Context context)
/*    */   {
/* 32 */     Connector srcConnector = context.getSrcConnector();
/* 33 */     ServiceEngine serviceEngine = srcConnector.getServiceEngine();
/* 34 */     BusConnector busConnector = serviceEngine.getBusConnector(context.getServiceType());
/*    */     
/* 36 */     if ((null == busConnector) || (busConnector.isUnusableServerEmpty()))
/*    */     {
/* 38 */       return;
/*    */     }
/*    */     
/* 41 */     Iterator<ServiceInstanceInner> iterator = instances.iterator();
/* 42 */     while (iterator.hasNext())
/*    */     {
/* 44 */       ServiceInstanceInner instance = (ServiceInstanceInner)iterator.next();
/*    */       
/* 46 */       if (busConnector.isInUnusableServer(instance.getAddress()))
/*    */       {
/* 48 */         iterator.remove();
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public String getFilterName()
/*    */   {
/* 56 */     return "bus connector black list filter";
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\filter\BusConnectorBlackListFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */