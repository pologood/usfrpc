/*    */ package com.huawei.csc.usf.framework;
/*    */ 
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ 
/*    */ public class ExecutesRepository
/*    */ {
/*  8 */   private Map<String, ExecutesSemaphore> repo = new ConcurrentHashMap();
/*    */   
/*    */   public ExecutesSemaphore getExecutesSemaphore(String key)
/*    */   {
/* 12 */     return (ExecutesSemaphore)this.repo.get(key);
/*    */   }
/*    */   
/*    */   public void add(String key)
/*    */   {
/* 17 */     this.repo.put(key, new ExecutesSemaphore());
/*    */   }
/*    */   
/*    */   public boolean containsKey(String key)
/*    */   {
/* 22 */     return this.repo.containsKey(key);
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\ExecutesRepository.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */