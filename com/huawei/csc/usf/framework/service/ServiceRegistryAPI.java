/*    */ package com.huawei.csc.usf.framework.service;
/*    */ 
/*    */ import com.huawei.csc.container.api.ContextRegistry;
/*    */ import com.huawei.csc.container.api.IContextHolder;
/*    */ import com.huawei.csc.usf.framework.ServiceDefinition;
/*    */ import com.huawei.csc.usf.framework.pojo.PojoServerInner;
/*    */ import com.huawei.csc.usf.framework.pojo.PojoServiceRegistry;
/*    */ import com.huawei.csc.usf.framework.sr.SRAgentFactory;
/*    */ import com.huawei.csc.usf.framework.sr.ServiceRegistryAgent;
/*    */ import com.huawei.csc.usf.framework.sr.ServiceType;
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
/*    */ public class ServiceRegistryAPI
/*    */ {
/*    */   private static final String DEFAULT_GROUP_NAME = "default";
/*    */   private static final String DEFAULT_VERSION = "0.0.0";
/*    */   
/*    */   public static void publish(ServiceDefinition serviceDefinition, Object instance)
/*    */   {
/* 50 */     PojoServerInner pojoService = new PojoServerInner();
/* 51 */     pojoService.setServiceType(ServiceType.USF);
/* 52 */     pojoService.setRef(instance);
/* 53 */     pojoService.setIntf(instance.getClass());
/* 54 */     pojoService.setServiceDefinition(serviceDefinition);
/*    */     
/* 56 */     if (StringUtils.isBlank(serviceDefinition.getGroup()))
/*    */     {
/* 58 */       serviceDefinition.setGroup("default");
/*    */     }
/* 60 */     if (StringUtils.isBlank(serviceDefinition.getVersion()))
/*    */     {
/* 62 */       serviceDefinition.setVersion("0.0.0");
/*    */     }
/*    */     
/* 65 */     getServiceRegistry().register(pojoService);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static void unpublish(ServiceDefinition serviceDefinition)
/*    */   {
/* 75 */     getServiceRegistry().unregister(serviceDefinition.getServiceName());
/*    */   }
/*    */   
/*    */   private static PojoServiceRegistry getServiceRegistry()
/*    */   {
/* 80 */     PojoServiceRegistry serivceRegistry = ServiceRegistrySupport.getInstance().getSerivceRegistry(ServiceType.USF);
/*    */     
/* 82 */     return serivceRegistry;
/*    */   }
/*    */   
/*    */ 
/*    */   public static void registerDelayedServices()
/*    */   {
/* 88 */     SRAgentFactory srAgentFactory = (SRAgentFactory)ContextRegistry.getContextHolder().getBean("usfSrAgentFactory");
/*    */     
/* 90 */     ServiceRegistryAgent srAgent = srAgentFactory.getDefaultSRAgent();
/* 91 */     srAgent.registerDelayedServices();
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\service\ServiceRegistryAPI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */