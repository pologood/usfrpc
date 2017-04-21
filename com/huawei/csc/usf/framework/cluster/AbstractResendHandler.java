/*    */ package com.huawei.csc.usf.framework.cluster;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.ClusterFaultProcess;
/*    */ import com.huawei.csc.usf.framework.Connector;
/*    */ import com.huawei.csc.usf.framework.Context;
/*    */ import com.huawei.csc.usf.framework.ServiceEngine;
/*    */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*    */ import com.huawei.csc.usf.framework.interceptor.InterceptorManager;
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
/*    */ public abstract class AbstractResendHandler
/*    */   implements ClusterFaultProcess
/*    */ {
/*    */   public void processFault(Exception exception, Context context)
/*    */     throws Exception
/*    */   {
/* 29 */     if (isResend(exception, context))
/*    */     {
/*    */ 
/* 32 */       int reSendTimes = context.getSrcConnector().getServiceEngine().getSystemConfig().getResendTimes();
/*    */       
/*    */ 
/* 35 */       Connector srcConnector = context.getSrcConnector();
/* 36 */       InterceptorManager interceptorManger = srcConnector.getServiceEngine().getInterceptorManager();
/*    */       
/* 38 */       if (interceptorManger != null)
/*    */       {
/* 40 */         if (context.getTimesBeenSent() < reSendTimes)
/*    */         {
/* 42 */           context.setRepeatFault(false);
/* 43 */           context.sendTimeIncrement();
/* 44 */           context.setIsReSend(true);
/* 45 */           context.setReplyMessage(null);
/* 46 */           context.setException(null);
/* 47 */           context.setReplyObject(null);
/* 48 */           interceptorManger.dispatch(context);
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public abstract boolean isResend(Exception paramException, Context paramContext);
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\cluster\AbstractResendHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */