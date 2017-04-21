/*    */ package com.huawei.csc.usf.framework.routing.filter;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.Context;
/*    */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
/*    */ import com.huawei.csc.usf.framework.sr.ServiceType;
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
/*    */ public class ServiceTypeFilter
/*    */   implements RoutingServiceInstanceFilter
/*    */ {
/*    */   public void filter(List<ServiceInstanceInner> instances, Context context)
/*    */   {
/* 29 */     String serviceType = context.getServiceType().toString();
/* 30 */     if ((Utils.isEmpty(serviceType)) || (serviceType.equals(ServiceType.EBUS.toString())))
/*    */     {
/*    */ 
/* 33 */       return;
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
/* 48 */     Iterator<ServiceInstanceInner> iterator = instances.iterator();
/* 49 */     while (iterator.hasNext())
/*    */     {
/* 51 */       ServiceInstanceInner instance = (ServiceInstanceInner)iterator.next();
/* 52 */       if (!serviceType.equals(instance.getServiceType()))
/*    */       {
/* 54 */         iterator.remove();
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public String getFilterName()
/*    */   {
/* 62 */     return "service type filter";
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\filter\ServiceTypeFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */