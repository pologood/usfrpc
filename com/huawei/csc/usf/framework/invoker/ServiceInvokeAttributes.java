/*    */ package com.huawei.csc.usf.framework.invoker;
/*    */ 
/*    */ 
/*    */ public class ServiceInvokeAttributes
/*    */ {
/*    */   private String destAddr;
/*    */   
/*    */   private String serialization;
/*    */   
/*    */   private String registry;
/*    */   
/*    */   private String group;
/*    */   
/*    */   private String version;
/*    */   private long timeout;
/*    */   
/*    */   public String getVersion()
/*    */   {
/* 19 */     return this.version;
/*    */   }
/*    */   
/*    */   public void setVersion(String version)
/*    */   {
/* 24 */     this.version = version;
/*    */   }
/*    */   
/*    */   public String getDestAddr()
/*    */   {
/* 29 */     return this.destAddr;
/*    */   }
/*    */   
/*    */   public void setDestAddr(String destAddr)
/*    */   {
/* 34 */     this.destAddr = destAddr;
/*    */   }
/*    */   
/*    */   public String getSerialization()
/*    */   {
/* 39 */     return this.serialization;
/*    */   }
/*    */   
/*    */   public void setSerialization(String serialization)
/*    */   {
/* 44 */     this.serialization = serialization;
/*    */   }
/*    */   
/*    */   public String getRegistry()
/*    */   {
/* 49 */     return this.registry;
/*    */   }
/*    */   
/*    */   public void setRegistry(String registry)
/*    */   {
/* 54 */     this.registry = registry;
/*    */   }
/*    */   
/*    */   public String getGroup()
/*    */   {
/* 59 */     return this.group;
/*    */   }
/*    */   
/*    */   public void setGroup(String group)
/*    */   {
/* 64 */     this.group = group;
/*    */   }
/*    */   
/*    */   public long getTimeout()
/*    */   {
/* 69 */     return this.timeout;
/*    */   }
/*    */   
/*    */   public void setTimeout(long timeout)
/*    */   {
/* 74 */     this.timeout = timeout;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\invoker\ServiceInvokeAttributes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */