/*    */ package com.huawei.csc.usf.framework.routing.filter;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.Connector;
/*    */ import com.huawei.csc.usf.framework.Context;
/*    */ import com.huawei.csc.usf.framework.IMessage;
/*    */ import com.huawei.csc.usf.framework.MessageHeaders;
/*    */ import com.huawei.csc.usf.framework.ServiceEngine;
/*    */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*    */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
/*    */ import com.huawei.csc.usf.framework.util.RouterUtils;
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
/*    */ public class RouterGovernanceFilter
/*    */   implements RoutingServiceInstanceFilter
/*    */ {
/*    */   public void filter(List<ServiceInstanceInner> instances, Context context)
/*    */   {
/* 32 */     IMessage receivedMessage = context.getReceivedMessage();
/* 33 */     MessageHeaders headers = receivedMessage.getHeaders();
/*    */     
/* 35 */     Connector srcConnector = context.getSrcConnector();
/* 36 */     ServiceEngine serviceEngine = srcConnector.getServiceEngine();
/* 37 */     SystemConfig config = serviceEngine.getSystemConfig();
/*    */     
/* 39 */     RouterUtils.filterByRouterGovernance(context, headers.getServiceName(), config.getRPCAddress(context.getServiceType()), headers.getOperation(), config.getDsfApplication(), instances);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getFilterName()
/*    */   {
/* 48 */     return "router governance";
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\filter\RouterGovernanceFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */