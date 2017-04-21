/*    */ package com.huawei.csc.usf.framework.routing.filter;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.Connector;
/*    */ import com.huawei.csc.usf.framework.Context;
/*    */ import com.huawei.csc.usf.framework.ServiceEngine;
/*    */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*    */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import org.apache.commons.lang.StringUtils;
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
/*    */ public class ServicePrefixNameFilter
/*    */   implements RoutingServiceInstanceFilter
/*    */ {
/*    */   public void filter(List<ServiceInstanceInner> instances, Context context)
/*    */   {
/* 31 */     SystemConfig systemConfig = context.getSrcConnector().getServiceEngine().getSystemConfig();
/*    */     
/* 33 */     String servicePrefixName = systemConfig.getServicePrefixName();
/*    */     
/* 35 */     boolean isServicePrefixNameBlank = StringUtils.isBlank(servicePrefixName);
/*    */     
/*    */ 
/*    */ 
/* 39 */     Iterator<ServiceInstanceInner> iterator = instances.iterator();
/* 40 */     while (iterator.hasNext())
/*    */     {
/* 42 */       ServiceInstanceInner instance = (ServiceInstanceInner)iterator.next();
/* 43 */       String instanceServicePrefixName = instance.getServiceGroup();
/*    */       
/* 45 */       if (!StringUtils.isBlank(instanceServicePrefixName))
/*    */       {
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 51 */         if (isServicePrefixNameBlank)
/*    */         {
/*    */ 
/* 54 */           iterator.remove();
/*    */         }
/* 56 */         else if (!servicePrefixName.equalsIgnoreCase(instanceServicePrefixName))
/*    */         {
/*    */ 
/*    */ 
/* 60 */           iterator.remove();
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public String getFilterName()
/*    */   {
/* 68 */     return "service prefix name filter";
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\filter\ServicePrefixNameFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */