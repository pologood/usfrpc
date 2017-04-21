/*    */ package com.huawei.csc.usf.framework.trace;
/*    */ 
/*    */ import com.huawei.csc.kernel.api.trace.ITraceLinkMessage;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TraceLinkInfo
/*    */ {
/*    */   private ITraceLinkMessage linkMessage;
/*    */   private ContextHolder contextHolder;
/*    */   private boolean headFlag;
/*    */   
/*    */   public ITraceLinkMessage getLinkMessage()
/*    */   {
/* 15 */     return this.linkMessage;
/*    */   }
/*    */   
/*    */   public void setLinkMessage(ITraceLinkMessage linkMessage)
/*    */   {
/* 20 */     this.linkMessage = linkMessage;
/*    */   }
/*    */   
/*    */   public boolean isHeadFlag()
/*    */   {
/* 25 */     return this.headFlag;
/*    */   }
/*    */   
/*    */   public void setHeadFlag(boolean headFlag)
/*    */   {
/* 30 */     this.headFlag = headFlag;
/*    */   }
/*    */   
/*    */   public ContextHolder getContextHolder()
/*    */   {
/* 35 */     return this.contextHolder;
/*    */   }
/*    */   
/*    */   public void setContextHolder(ContextHolder contextHolder)
/*    */   {
/* 40 */     this.contextHolder = contextHolder;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\trace\TraceLinkInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */