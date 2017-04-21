/*    */ package com.huawei.csc.usf.framework.exception;
/*    */ 
/*    */ import com.huawei.csc.kernel.commons.exception.BMEException;
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
/*    */ public class USFException
/*    */   extends BMEException
/*    */ {
/*    */   private static final long serialVersionUID = -8478410483483026316L;
/*    */   
/*    */   public USFException(String errorCode, Object[] args)
/*    */   {
/* 33 */     super(errorCode, args);
/*    */   }
/*    */   
/*    */   public USFException(String errorCode, Exception exception)
/*    */   {
/* 38 */     super(errorCode, exception);
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
/*    */   public USFException(String errorCode, String msg)
/*    */   {
/* 51 */     super(errorCode, msg);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public USFException() {}
/*    */   
/*    */ 
/*    */   public USFException(String errorCode, Object[] args, Throwable cause)
/*    */   {
/* 61 */     super(errorCode, args, cause);
/*    */   }
/*    */   
/*    */   public USFException(String errorCode, String msg, Throwable cause)
/*    */   {
/* 66 */     super(errorCode, msg, cause);
/*    */   }
/*    */   
/*    */   public USFException(String errorCode, Throwable cause)
/*    */   {
/* 71 */     super(errorCode, cause);
/*    */   }
/*    */   
/*    */   public USFException(String errorCode)
/*    */   {
/* 76 */     super(errorCode);
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\exception\USFException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */