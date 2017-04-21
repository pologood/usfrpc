/*    */ package com.huawei.csc.usf.framework;
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
/*    */ public class DefaultMethodDefinition
/*    */   implements MethodDefinition
/*    */ {
/* 28 */   private String methodName = null;
/*    */   
/* 30 */   private String timeout = null;
/*    */   
/* 32 */   private String executes = null;
/*    */   
/* 34 */   private String threshold = null;
/*    */   
/*    */ 
/*    */   public String getMethodName()
/*    */   {
/* 39 */     return this.methodName;
/*    */   }
/*    */   
/*    */ 
/*    */   public void setMethodName(String methodName)
/*    */   {
/* 45 */     this.methodName = methodName;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getTimeout()
/*    */   {
/* 55 */     return this.timeout;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setTimeout(String timeout)
/*    */   {
/* 66 */     this.timeout = timeout;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getExecutes()
/*    */   {
/* 72 */     return this.executes;
/*    */   }
/*    */   
/*    */ 
/*    */   public void setExecutes(String executes)
/*    */   {
/* 78 */     this.executes = executes;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getThreshold()
/*    */   {
/* 84 */     return this.threshold;
/*    */   }
/*    */   
/*    */ 
/*    */   public void setThreshold(String threshold)
/*    */   {
/* 90 */     this.threshold = threshold;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\DefaultMethodDefinition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */