/*    */ package com.huawei.csc.usf.framework.sr;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.util.Utils;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ConfigurationInstanceInner
/*    */ {
/*    */   private String serviceName;
/*    */   private String address;
/*    */   private Map<String, String> attributes;
/* 16 */   private boolean hasRouterType = false;
/*    */   
/*    */   public String getServiceName()
/*    */   {
/* 20 */     return this.serviceName;
/*    */   }
/*    */   
/*    */   public void setServiceName(String serviceName)
/*    */   {
/* 25 */     this.serviceName = serviceName;
/*    */   }
/*    */   
/*    */   public String getAddress()
/*    */   {
/* 30 */     return this.address;
/*    */   }
/*    */   
/*    */   public void setAddress(String address)
/*    */   {
/* 35 */     this.address = address;
/*    */   }
/*    */   
/*    */   public Map<String, String> getAttributes()
/*    */   {
/* 40 */     return this.attributes;
/*    */   }
/*    */   
/*    */   public void setAttributes(Map<String, String> attributes)
/*    */   {
/* 45 */     this.attributes = attributes;
/*    */   }
/*    */   
/*    */   public String getAttribute(String key)
/*    */   {
/* 50 */     if (Utils.isNotEmpty(this.attributes))
/*    */     {
/* 52 */       return (String)this.attributes.get(key);
/*    */     }
/*    */     
/* 55 */     return null;
/*    */   }
/*    */   
/*    */   public void putAttribute(String key, String value)
/*    */   {
/* 60 */     if ((Utils.isEmpty(key)) || (Utils.isEmpty(value)))
/*    */     {
/* 62 */       return;
/*    */     }
/* 64 */     this.attributes.put(key, value);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public boolean isHasRouterType()
/*    */   {
/* 71 */     return this.hasRouterType;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setHasRouterType(boolean hasRouterType)
/*    */   {
/* 80 */     this.hasRouterType = hasRouterType;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\ConfigurationInstanceInner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */