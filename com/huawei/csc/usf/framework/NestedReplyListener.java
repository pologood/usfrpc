/*    */ package com.huawei.csc.usf.framework;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.interceptor.InterceptorManager;
/*    */ import com.huawei.csc.usf.framework.invoker.CommonAsyncConnector;
/*    */ import com.huawei.csc.usf.framework.trace.MessageType;
/*    */ import com.huawei.csc.usf.framework.trace.TraceLinkUtilHelper;
/*    */ import com.huawei.csc.usf.framework.util.LogTraceUtil;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NestedReplyListener
/*    */   implements ReplyMessageListener
/*    */ {
/*    */   private InterceptorManager manager;
/*    */   private ReplyMessageListener replyMessageListener;
/*    */   
/*    */   public NestedReplyListener(InterceptorManager manager, ReplyMessageListener replyMessageListener)
/*    */   {
/* 48 */     this.manager = manager;
/* 49 */     this.replyMessageListener = replyMessageListener;
/*    */   }
/*    */   
/*    */   public void onReply(Context ctx, Object reply)
/*    */     throws Exception
/*    */   {
/* 55 */     if (!ctx.isServer())
/*    */     {
/*    */ 
/* 58 */       LogTraceUtil.updateMDCTraceOrder((IMessage)reply);
/* 59 */       ctx.getSrcConnector().asyncEndTraceLog((IMessage)reply);
/*    */       
/* 61 */       TraceLinkUtilHelper.clientlinkTraceEnd(ctx, MessageType.MSGRECV);
/*    */     }
/*    */     
/* 64 */     ctx.setReplyMessage((IMessage)reply);
/* 65 */     this.manager.asyncCallback(ctx);
/*    */     
/* 67 */     if (null != this.replyMessageListener)
/*    */     {
/* 69 */       if ((ctx.getSrcConnector() instanceof CommonAsyncConnector))
/*    */       {
/* 71 */         this.replyMessageListener.onReply(ctx, reply);
/*    */       }
/*    */       else
/*    */       {
/* 75 */         Object replyObject = ctx.getReplyObject();
/* 76 */         this.replyMessageListener.onReply(ctx, replyObject);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\NestedReplyListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */