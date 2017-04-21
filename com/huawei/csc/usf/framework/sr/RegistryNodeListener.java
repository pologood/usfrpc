/*    */ package com.huawei.csc.usf.framework.sr;
/*    */ 
/*    */ 
/*    */ public abstract interface RegistryNodeListener
/*    */ {
/*    */   public abstract void onNodeEvent(NodeChangeEvent paramNodeChangeEvent, String paramString, byte[] paramArrayOfByte);
/*    */   
/*    */   public static enum NodeChangeEvent
/*    */   {
/* 10 */     CREATED,  DELETED,  DATA_CHANGED,  CHILDREN_CHANGED;
/*    */     
/*    */     private NodeChangeEvent() {}
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\RegistryNodeListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */