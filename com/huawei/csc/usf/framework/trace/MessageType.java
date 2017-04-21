/*    */ package com.huawei.csc.usf.framework.trace;
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
/*    */ public enum MessageType
/*    */ {
/* 18 */   REQUEST, 
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 23 */   REPLY, 
/*    */   
/*    */ 
/* 26 */   CLIENTSEND, 
/*    */   
/*    */ 
/* 29 */   CLIENTRECV, 
/*    */   
/*    */ 
/* 32 */   SERVERSEND, 
/*    */   
/*    */ 
/* 35 */   SERVERRECV, 
/*    */   
/*    */ 
/* 38 */   MSGSEND, 
/*    */   
/*    */ 
/* 41 */   MSGRECV;
/*    */   
/*    */   private MessageType() {}
/*    */   
/*    */   public String toString()
/*    */   {
/* 47 */     switch (this)
/*    */     {
/*    */     case CLIENTSEND: 
/* 50 */       return "clientSend";
/*    */     case CLIENTRECV: 
/* 52 */       return "clientRecv";
/*    */     case SERVERSEND: 
/* 54 */       return "serverSend";
/*    */     case SERVERRECV: 
/* 56 */       return "serverRecv";
/*    */     case MSGSEND: 
/* 58 */       return "msgSend";
/*    */     case MSGRECV: 
/* 60 */       return "msgRecv";
/*    */     }
/* 62 */     return super.toString();
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\trace\MessageType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */