/*    */ package com.huawei.csc.usf.framework.pojo;
/*    */ 
/*    */ import java.lang.reflect.Proxy;
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
/*    */ public class DefaultProxyHandler
/*    */   implements ProxyHandler
/*    */ {
/*    */   public boolean isProxyClass(Class<?> clazz)
/*    */   {
/* 33 */     return Proxy.isProxyClass(clazz);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Class<?> getProxyClass(Object proxy, Class<?> interFace)
/*    */   {
/* 40 */     return interFace;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\pojo\DefaultProxyHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */