/*    */ package com.huawei.csc.usf.framework.sr;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ConfigurationParameterInstanceInner
/*    */ {
/*    */   private String serviceName;
/*    */   private String operation;
/* 12 */   private Map<String, Object> parameter = new HashMap();
/*    */   
/*    */   public String getServiceName()
/*    */   {
/* 16 */     return this.serviceName;
/*    */   }
/*    */   
/*    */   public void setServiceName(String serviceName)
/*    */   {
/* 21 */     this.serviceName = serviceName;
/*    */   }
/*    */   
/*    */   public String getOperation()
/*    */   {
/* 26 */     return this.operation;
/*    */   }
/*    */   
/*    */   public void setOperation(String operation)
/*    */   {
/* 31 */     this.operation = operation;
/*    */   }
/*    */   
/*    */   public void addParameter(String key, Object value)
/*    */   {
/* 36 */     this.parameter.put(key, value);
/*    */   }
/*    */   
/*    */   public Object getParameter(String key)
/*    */   {
/* 41 */     Object value = null;
/* 42 */     value = this.parameter.get(key);
/* 43 */     return value;
/*    */   }
/*    */   
/*    */   public boolean isContainsKey(String key)
/*    */   {
/* 48 */     if (this.parameter.containsKey(key))
/*    */     {
/* 50 */       return true;
/*    */     }
/*    */     
/*    */ 
/* 54 */     return false;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\ConfigurationParameterInstanceInner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */