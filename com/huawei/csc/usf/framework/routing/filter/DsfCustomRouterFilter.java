/*    */ package com.huawei.csc.usf.framework.routing.filter;
/*    */ 
/*    */ import com.huawei.csc.container.api.ContextRegistry;
/*    */ import com.huawei.csc.container.api.IContextHolder;
/*    */ import com.huawei.csc.kernel.api.log.LogFactory;
/*    */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*    */ import com.huawei.csc.usf.framework.Context;
/*    */ import com.huawei.csc.usf.framework.routing.RoutingProcessorManager;
/*    */ import com.huawei.csc.usf.framework.routing.ServiceAddress;
/*    */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
/*    */ import com.huawei.csc.usf.framework.sr.ServiceType;
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
/*    */ 
/*    */ 
/*    */ public class DsfCustomRouterFilter
/*    */   implements RoutingServiceInstanceFilter
/*    */ {
/* 34 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(DsfCustomRouterFilter.class);
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 40 */   private boolean dsfRouterNotExistFlag = false;
/*    */   
/*    */ 
/*    */ 
/*    */   public void filter(List<ServiceInstanceInner> instances, Context context)
/*    */   {
/* 46 */     if (!ServiceType.DSF.equals(context.getServiceType()))
/*    */     {
/* 48 */       return;
/*    */     }
/*    */     
/*    */ 
/* 52 */     String routerType = context.getRouterType();
/* 53 */     if (StringUtils.isEmpty(routerType))
/*    */     {
/* 55 */       return;
/*    */     }
/*    */     
/*    */ 
/* 59 */     Object router = null;
/*    */     try
/*    */     {
/* 62 */       router = ContextRegistry.getContextHolder().getBean(routerType);
/*    */ 
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 67 */       if ((DEBUGGER.isErrorEnable()) && (!this.dsfRouterNotExistFlag))
/*    */       {
/* 69 */         this.dsfRouterNotExistFlag = true;
/* 70 */         DEBUGGER.error("the router named " + routerType + " don't exist.Please check the user's router.");
/*    */       }
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 76 */     if ((null == router) || ((router instanceof ServiceAddress)))
/*    */     {
/* 78 */       return;
/*    */     }
/*    */     
/*    */ 
/* 82 */     RoutingProcessorManager.process(router, context, instances);
/*    */   }
/*    */   
/*    */ 
/*    */   public String getFilterName()
/*    */   {
/* 88 */     return "dsf custom router filter";
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\filter\DsfCustomRouterFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */