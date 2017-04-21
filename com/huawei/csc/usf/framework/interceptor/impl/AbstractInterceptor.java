/*    */ package com.huawei.csc.usf.framework.interceptor.impl;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.Context;
/*    */ import com.huawei.csc.usf.framework.interceptor.Interceptor;
/*    */ import com.huawei.csc.usf.framework.interceptor.InterceptorExecutor;
/*    */ import com.huawei.csc.usf.framework.statistic.ProcessDelayTracker;
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
/*    */ public abstract class AbstractInterceptor
/*    */   implements Interceptor
/*    */ {
/*    */   public void invoke(InterceptorExecutor executor, Context context)
/*    */     throws Exception
/*    */   {
/* 38 */     ProcessDelayTracker tracker = ProcessDelayTracker.next(context.getProcessDelayTracker(), getName());
/*    */     
/*    */ 
/*    */     try
/*    */     {
/* 43 */       doInvoke(context);
/*    */     }
/*    */     finally
/*    */     {
/* 47 */       ProcessDelayTracker.done(tracker);
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 52 */     executor.invok(context);
/*    */   }
/*    */   
/*    */   public void init() {}
/*    */   
/*    */   public void destroy() {}
/*    */   
/*    */   protected abstract void doInvoke(Context paramContext)
/*    */     throws Exception;
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\interceptor\impl\AbstractInterceptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */