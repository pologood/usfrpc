/*    */ package com.huawei.csc.usf.framework.monitor.threadpool;
/*    */ 
/*    */ import com.huawei.csc.kernel.api.log.LogFactory;
/*    */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ public class DefaultThreadPoolMonitorHandler
/*    */   implements IThreadPoolMonitorHandler
/*    */ {
/* 11 */   private static final DebugLog LOGGER = LogFactory.getDebugLog("default_threadpool_monitor_handler");
/*    */   
/*    */ 
/*    */ 
/*    */   public void invoke(List<ThreadPoolMonitorInfo> threadPoolMonitorInfoList)
/*    */   {
/* 17 */     if (LOGGER.isInfoEnable())
/*    */     {
/* 19 */       for (ThreadPoolMonitorInfo threadPoolMonitorInfo : threadPoolMonitorInfoList)
/*    */       {
/* 21 */         LOGGER.info(threadPoolMonitorInfo.toString());
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\monitor\threadpool\DefaultThreadPoolMonitorHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */