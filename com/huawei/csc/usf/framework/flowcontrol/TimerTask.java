/*    */ package com.huawei.csc.usf.framework.flowcontrol;
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
/*    */ public class TimerTask
/*    */   implements Runnable
/*    */ {
/*    */   public void run()
/*    */   {
/* 23 */     FlowControlManager.getInstance().resetTps();
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\flowcontrol\TimerTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */