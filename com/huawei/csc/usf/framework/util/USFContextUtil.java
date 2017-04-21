/*    */ package com.huawei.csc.usf.framework.util;
/*    */ 
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class USFContextUtil
/*    */ {
/*    */   public static void clearUSFContext()
/*    */   {
/* 11 */     USFContext.getContext().clear();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static void setAttribute(String key, String value)
/*    */   {
/* 23 */     USFContext.getContext().setAttribute(key, value);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static void setAttribute(String key, Object value)
/*    */   {
/* 35 */     USFContext.getContext().setAttribute(key, value);
/*    */   }
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
/*    */   public static void setAttribute(String key, Object value, ImplicitArgsType type)
/*    */   {
/* 50 */     USFContext.getContext().setAttribute(key, value, type);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static void removeAttribute(String key)
/*    */   {
/* 61 */     if (USFContext.getContext().getAttributes().containsKey(key))
/*    */     {
/* 63 */       USFContext.getContext().removeAttribute(key);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static Object getAttribute(String key)
/*    */   {
/* 76 */     return USFContext.getContext().getAttributeValue(key);
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\util\USFContextUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */