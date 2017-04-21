/*    */ package com.huawei.csc.usf.framework.cluster;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.Context;
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
/*    */ public class FailFastCluster
/*    */   extends AbstractResendHandler
/*    */ {
/*    */   public boolean isResend(Exception exception, Context context)
/*    */   {
/* 31 */     return false;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\cluster\FailFastCluster.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */