/*    */ package com.huawei.csc.usf.framework.monitor.jmx.impl;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.monitor.jmx.management.ThreadPoolMonitorManagement;
/*    */ import com.huawei.csc.usf.framework.monitor.threadpool.ThreadPoolMonitorCenter;
/*    */ import com.huawei.csc.usf.framework.monitor.threadpool.ThreadPoolMonitorInfo;
/*    */ import com.huawei.csc.usf.framework.util.JMXUtils;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ public class ThreadPoolManagermentImpl
/*    */   implements ThreadPoolMonitorManagement
/*    */ {
/*    */   public void init()
/*    */   {
/* 15 */     JMXUtils.registerMBean(this, ThreadPoolMonitorManagement.class, "com.huawei.usf:Name=ThreadPoolMonitor");
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void destroy()
/*    */   {
/* 22 */     JMXUtils.unregisterMBean("com.huawei.usf:Name=ThreadPoolMonitor");
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public List<ThreadPoolMonitorInfo> getThreadPoolMonitorData()
/*    */   {
/* 29 */     List<ThreadPoolMonitorInfo> threadPoolMonitorInfos = ThreadPoolMonitorCenter.getInstance().gethreadPoolMonitorInfos();
/*    */     
/*    */ 
/* 32 */     return threadPoolMonitorInfos;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\monitor\jmx\impl\ThreadPoolManagermentImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */