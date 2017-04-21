/*    */ package com.huawei.csc.usf.framework.cluster;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.Context;
/*    */ import com.huawei.csc.usf.framework.exception.USFException;
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
/*    */ public class FailOverCluster
/*    */   extends AbstractResendHandler
/*    */ {
/*    */   public boolean isResend(Exception exception, Context context)
/*    */   {
/* 27 */     if ((exception == null) || (context.isServer()))
/*    */     {
/* 29 */       return false;
/*    */     }
/* 31 */     if ((exception instanceof USFException))
/*    */     {
/* 33 */       if (((USFException)exception).getExceptionCode().equalsIgnoreCase("405180800"))
/*    */       {
/*    */ 
/* 36 */         return true;
/*    */       }
/* 38 */       if (((USFException)exception).getExceptionCode().equalsIgnoreCase("405180809"))
/*    */       {
/*    */ 
/* 41 */         return true;
/*    */       }
/*    */       
/* 44 */       if (((USFException)exception).getExceptionCode().equalsIgnoreCase("405180807"))
/*    */       {
/*    */ 
/* 47 */         return true;
/*    */       }
/*    */       
/*    */ 
/*    */ 
/* 52 */       return false;
/*    */     }
/*    */     
/*    */ 
/* 56 */     return false;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\cluster\FailOverCluster.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */