/*    */ package com.huawei.csc.usf.framework.sr.route;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.concurrent.ConcurrentHashMap;
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
/*    */ public class Routers
/*    */ {
/* 33 */   private Map<String, Router> routersMap = new ConcurrentHashMap();
/*    */   
/*    */   public void addRouter(Router router)
/*    */   {
/* 37 */     if (router.isEnabled())
/*    */     {
/* 39 */       String name = router.getName();
/* 40 */       this.routersMap.put(name, router);
/*    */     }
/*    */   }
/*    */   
/*    */   public void setRouters(List<Router> routers)
/*    */   {
/* 46 */     for (Router router : routers)
/*    */     {
/* 48 */       addRouter(router);
/*    */     }
/*    */   }
/*    */   
/*    */   public void deleteRouter(String routerName)
/*    */   {
/* 54 */     this.routersMap.remove(routerName);
/*    */   }
/*    */   
/*    */   public List<Router> getAllRouters()
/*    */   {
/* 59 */     List<Router> routers = new ArrayList();
/* 60 */     for (Map.Entry<String, Router> entry : this.routersMap.entrySet())
/*    */     {
/* 62 */       routers.add(entry.getValue());
/*    */     }
/* 64 */     return routers;
/*    */   }
/*    */   
/*    */   public void deleteRouters(List<Router> url2Routers)
/*    */   {
/* 69 */     for (Router router : url2Routers)
/*    */     {
/* 71 */       deleteRouter(router.getName());
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\route\Routers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */