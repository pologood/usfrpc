/*    */ package com.huawei.csc.usf.framework.interceptor;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.Context;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class InterceptorExecutor<T extends Interceptor>
/*    */ {
/*    */   private List<T> interceptors;
/* 34 */   private int current = -1;
/*    */   
/* 36 */   private int interceptorStyle = -1;
/*    */   
/*    */ 
/*    */   public InterceptorExecutor(List<T> interceptors)
/*    */   {
/* 41 */     this.interceptors = interceptors;
/*    */   }
/*    */   
/*    */   public void addInterceptor(T interceptor)
/*    */   {
/* 46 */     this.interceptors.add(interceptor);
/*    */   }
/*    */   
/*    */   public void invok(Context context) throws Exception
/*    */   {
/* 51 */     this.current += 1;
/* 52 */     if ((this.current == this.interceptors.size()) || (context.isBroken()))
/*    */     {
/* 54 */       return;
/*    */     }
/*    */     
/* 57 */     Interceptor interceptor = (Interceptor)this.interceptors.get(this.current);
/*    */     
/*    */ 
/* 60 */     interceptor.invoke(this, context);
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\interceptor\InterceptorExecutor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */