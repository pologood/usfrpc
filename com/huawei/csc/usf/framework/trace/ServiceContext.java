/*    */ package com.huawei.csc.usf.framework.trace;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class ServiceContext
/*    */ {
/*  8 */   private Map<String, Object> attrs = new HashMap();
/*    */   
/*    */   private Throwable exception;
/*    */   
/*    */   private String service;
/*    */   
/*    */   private String operation;
/*    */   
/*    */ 
/*    */   public ServiceContext(String service, String operation)
/*    */   {
/* 19 */     this.service = service;
/* 20 */     this.operation = operation;
/*    */   }
/*    */   
/*    */   public Map<String, Object> getAttributes()
/*    */   {
/* 25 */     return this.attrs;
/*    */   }
/*    */   
/*    */   public void setAttribute(String key, Object value)
/*    */   {
/* 30 */     this.attrs.put(key, value);
/*    */   }
/*    */   
/*    */   public Object getAttribute(String key)
/*    */   {
/* 35 */     return this.attrs.get(key);
/*    */   }
/*    */   
/*    */   public Throwable getException()
/*    */   {
/* 40 */     return this.exception;
/*    */   }
/*    */   
/*    */   public void setException(Throwable exception)
/*    */   {
/* 45 */     this.exception = exception;
/*    */   }
/*    */   
/*    */   public String getService()
/*    */   {
/* 50 */     return this.service;
/*    */   }
/*    */   
/*    */   public void setService(String service)
/*    */   {
/* 55 */     this.service = service;
/*    */   }
/*    */   
/*    */   public String getOperation()
/*    */   {
/* 60 */     return this.operation;
/*    */   }
/*    */   
/*    */   public void setOperation(String operation)
/*    */   {
/* 65 */     this.operation = operation;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\trace\ServiceContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */