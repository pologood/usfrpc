/*    */ package com.huawei.csc.usf.framework.message;
/*    */ 
/*    */ import com.huawei.csc.remoting.common.util.CastUtil;
/*    */ import com.huawei.csc.usf.framework.GroupNameInterpret;
/*    */ import com.huawei.csc.usf.framework.IMessage;
/*    */ import com.huawei.csc.usf.framework.MessageHeaders;
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
/*    */ public class MessageImpl
/*    */   implements IMessage
/*    */ {
/* 29 */   private MessageHeaders headers = new MessageHeadersImpl();
/*    */   
/*    */   private Object payload;
/*    */   
/*    */ 
/*    */   public final MessageHeaders getHeaders()
/*    */   {
/* 36 */     return this.headers;
/*    */   }
/*    */   
/*    */ 
/*    */   public <T> T getPayload()
/*    */   {
/* 42 */     return (T)CastUtil.cast(this.payload);
/*    */   }
/*    */   
/*    */ 
/*    */   public <T> void setPayload(T payload)
/*    */   {
/* 48 */     this.payload = payload;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public boolean isFault()
/*    */   {
/* 55 */     Object object = getPayload();
/* 56 */     if (null == object)
/*    */     {
/* 58 */       return false;
/*    */     }
/*    */     
/* 61 */     if ((object instanceof Throwable))
/*    */     {
/* 63 */       return true;
/*    */     }
/* 65 */     return false;
/*    */   }
/*    */   
/*    */ 
/*    */   public void setException(Exception e)
/*    */   {
/* 71 */     this.payload = e;
/*    */   }
/*    */   
/*    */ 
/*    */   public <T> void setReturn(T payload)
/*    */   {
/* 77 */     this.payload = payload;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public boolean hasException()
/*    */   {
/* 84 */     if ((null != this.payload) && ((this.payload instanceof Exception)))
/*    */     {
/* 86 */       return true;
/*    */     }
/* 88 */     return false;
/*    */   }
/*    */   
/*    */ 
/*    */   public GroupNameInterpret getGroupNameInterpret()
/*    */   {
/* 94 */     return GroupNameInterpret.Compare;
/*    */   }
/*    */   
/*    */   public void setGroupNameInterpret(GroupNameInterpret groupNameInterpret) {}
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\message\MessageImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */