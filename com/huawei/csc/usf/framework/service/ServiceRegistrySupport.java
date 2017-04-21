/*    */ package com.huawei.csc.usf.framework.service;
/*    */ 
/*    */ import com.huawei.csc.container.api.ContextRegistry;
/*    */ import com.huawei.csc.container.api.IContextHolder;
/*    */ import com.huawei.csc.usf.framework.pojo.PojoServiceRegistry;
/*    */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*    */ import com.huawei.csc.usf.framework.util.CopyOnWriteHashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
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
/*    */ public class ServiceRegistrySupport
/*    */ {
/* 34 */   private static final ServiceRegistrySupport INSTANCE = new ServiceRegistrySupport();
/*    */   
/* 36 */   private Map<ServiceType, PojoServiceRegistry> serviceRegistryMap = new CopyOnWriteHashMap();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static ServiceRegistrySupport getInstance()
/*    */   {
/* 48 */     return INSTANCE;
/*    */   }
/*    */   
/*    */   public PojoServiceRegistry getSerivceRegistry(ServiceType serviceType)
/*    */   {
/* 53 */     intServiceRegistryMap();
/* 54 */     PojoServiceRegistry serviceRegitry = (PojoServiceRegistry)this.serviceRegistryMap.get(serviceType);
/*    */     
/*    */ 
/* 57 */     return serviceRegitry;
/*    */   }
/*    */   
/*    */   private void intServiceRegistryMap()
/*    */   {
/* 62 */     if (this.serviceRegistryMap.isEmpty())
/*    */     {
/* 64 */       Map<String, PojoServiceRegistry> map = ContextRegistry.getContextHolder().getBeansOfType(PojoServiceRegistry.class);
/*    */       
/*    */ 
/*    */ 
/* 68 */       for (Map.Entry<String, PojoServiceRegistry> entry : map.entrySet())
/*    */       {
/* 70 */         PojoServiceRegistry sr = (PojoServiceRegistry)entry.getValue();
/* 71 */         this.serviceRegistryMap.put(sr.getType(), sr);
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public void cleanServiceResitryMap()
/*    */   {
/* 78 */     this.serviceRegistryMap.clear();
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\service\ServiceRegistrySupport.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */