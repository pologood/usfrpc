/*    */ package com.huawei.csc.usf.framework.routing;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.IMessage;
/*    */ 
/*    */ 
/*    */ public class RoutingContext
/*    */ {
/*    */   private String listenAddress;
/*    */   private IMessage msg;
/*    */   
/*    */   public String getListenAddress()
/*    */   {
/* 13 */     return this.listenAddress;
/*    */   }
/*    */   
/*    */   public void setListenAddress(String listenAddress)
/*    */   {
/* 18 */     this.listenAddress = listenAddress;
/*    */   }
/*    */   
/*    */   public IMessage getMsg()
/*    */   {
/* 23 */     return this.msg;
/*    */   }
/*    */   
/*    */   public void setMsg(IMessage msg)
/*    */   {
/* 28 */     this.msg = msg;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\RoutingContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */