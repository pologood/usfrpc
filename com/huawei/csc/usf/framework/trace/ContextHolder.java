/*    */ package com.huawei.csc.usf.framework.trace;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.MessageHeaders;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.apache.commons.lang.StringUtils;
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
/*    */ public class ContextHolder
/*    */ {
/*    */   private String proxyService;
/*    */   private String proxyOperation;
/*    */   private String proxyProtocol;
/* 34 */   private Map<String, Object> attrs = new HashMap();
/*    */   
/*    */   public ContextHolder(MessageHeaders messageHeaders)
/*    */   {
/* 38 */     if (null != messageHeaders)
/*    */     {
/* 40 */       this.proxyService = messageHeaders.getPolicyServiceName();
/* 41 */       this.proxyOperation = messageHeaders.getPolicyOperation();
/* 42 */       this.proxyProtocol = messageHeaders.getPolicyProtocol();
/*    */     }
/*    */   }
/*    */   
/*    */   public String getProxyService()
/*    */   {
/* 48 */     return this.proxyService;
/*    */   }
/*    */   
/*    */   public void setProxyService(String proxyService)
/*    */   {
/* 53 */     this.proxyService = proxyService;
/*    */   }
/*    */   
/*    */   public String getProxyOperation()
/*    */   {
/* 58 */     return this.proxyOperation;
/*    */   }
/*    */   
/*    */   public void setProxyOperation(String proxyOperation)
/*    */   {
/* 63 */     this.proxyOperation = proxyOperation;
/*    */   }
/*    */   
/*    */   public String getProxyProtocol()
/*    */   {
/* 68 */     return this.proxyProtocol;
/*    */   }
/*    */   
/*    */   public void setProxyProtocol(String proxyProtocol)
/*    */   {
/* 73 */     this.proxyProtocol = proxyProtocol;
/*    */   }
/*    */   
/*    */   public void setAttribute(String key, Object value)
/*    */   {
/* 78 */     if ((StringUtils.isBlank(key)) || (null == value))
/*    */     {
/* 80 */       return;
/*    */     }
/* 82 */     this.attrs.put(key, value);
/*    */   }
/*    */   
/*    */   public Object getAttribute(String key)
/*    */   {
/* 87 */     if (StringUtils.isBlank(key))
/*    */     {
/* 89 */       return "";
/*    */     }
/* 91 */     return this.attrs.get(key) == null ? "" : this.attrs.get(key);
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\trace\ContextHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */