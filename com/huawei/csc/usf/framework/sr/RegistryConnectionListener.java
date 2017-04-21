/*    */ package com.huawei.csc.usf.framework.sr;
/*    */ 
/*    */ 
/*    */ public abstract interface RegistryConnectionListener
/*    */ {
/*    */   public abstract void onConnectionChanged(ConnectionEvent paramConnectionEvent);
/*    */   
/*    */ 
/*    */   public static enum ConnectionEvent
/*    */   {
/* 11 */     CONNECTED, 
/*    */     
/* 13 */     DISCONNECTED, 
/*    */     
/* 15 */     SESSION_EXPIRED, 
/*    */     
/* 17 */     SESSION_RECOVERED, 
/*    */     
/*    */ 
/* 20 */     SESSION_REBUILT;
/*    */     
/*    */     private ConnectionEvent() {}
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\RegistryConnectionListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */