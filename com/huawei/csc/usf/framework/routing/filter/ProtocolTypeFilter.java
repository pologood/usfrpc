/*    */ package com.huawei.csc.usf.framework.routing.filter;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.Context;
/*    */ import com.huawei.csc.usf.framework.IMessage;
/*    */ import com.huawei.csc.usf.framework.MessageHeaders;
/*    */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
/*    */ import com.huawei.csc.usf.framework.util.Utils;
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
/*    */ 
/*    */ public class ProtocolTypeFilter
/*    */   implements RoutingServiceInstanceFilter
/*    */ {
/*    */   public void filter(List<ServiceInstanceInner> instances, Context context)
/*    */   {
/* 33 */     String protocolType = context.getReceivedMessage().getHeaders().getPolicyProtocol();
/*    */     
/* 35 */     if (Utils.isEmpty(protocolType))
/*    */     {
/* 37 */       return;
/*    */     }
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
/* 52 */     Iterator<ServiceInstanceInner> iterator = instances.iterator();
/* 53 */     while (iterator.hasNext())
/*    */     {
/* 55 */       ServiceInstanceInner instance = (ServiceInstanceInner)iterator.next();
/* 56 */       if ("rest".equalsIgnoreCase(protocolType))
/*    */       {
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 65 */         if (!protocolType.equals(instance.getType()))
/*    */         {
/* 67 */           iterator.remove();
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public String getFilterName()
/*    */   {
/* 77 */     return "protocol type filter";
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\filter\ProtocolTypeFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */