/*    */ package com.huawei.csc.usf.framework.util;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class USFCtxObject
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 4214832356371640763L;
/*    */   private Object value;
/* 16 */   private ImplicitArgsType attType = ImplicitArgsType.LOCAL;
/*    */   
/*    */   public USFCtxObject(Object value, ImplicitArgsType attType)
/*    */   {
/* 20 */     this.value = value;
/* 21 */     this.attType = attType;
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 26 */     if (null == this.value)
/*    */     {
/* 28 */       return "";
/*    */     }
/* 30 */     return this.value.toString();
/*    */   }
/*    */   
/*    */   public void setAttType(ImplicitArgsType attType)
/*    */   {
/* 35 */     this.attType = attType;
/*    */   }
/*    */   
/*    */   public ImplicitArgsType getAttType()
/*    */   {
/* 40 */     return this.attType;
/*    */   }
/*    */   
/*    */   public void setValue(Object value)
/*    */   {
/* 45 */     this.value = value;
/*    */   }
/*    */   
/*    */   public Object getValue()
/*    */   {
/* 50 */     return this.value;
/*    */   }
/*    */   
/*    */   public USFCtxObject(Object value)
/*    */   {
/* 55 */     this.value = value;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\util\USFCtxObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */