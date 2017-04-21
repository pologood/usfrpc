/*    */ package com.huawei.csc.usf.framework.monitor.threadpool;
/*    */ 
/*    */ 
/*    */ public class ThreadPoolMonitorInfo
/*    */ {
/*    */   private String threadPoolBeanName;
/*    */   
/*    */   private int activeNum;
/*    */   
/*    */   private int idleNum;
/*    */   
/*    */   private int maxNum;
/*    */   
/*    */   private int currQueueSize;
/*    */   private long avgWaitingMillis;
/*    */   
/*    */   public String getThreadPoolBeanName()
/*    */   {
/* 19 */     return this.threadPoolBeanName;
/*    */   }
/*    */   
/*    */   public void setThreadPoolBeanName(String threadPoolBeanName)
/*    */   {
/* 24 */     this.threadPoolBeanName = threadPoolBeanName;
/*    */   }
/*    */   
/*    */   public int getActiveNum()
/*    */   {
/* 29 */     return this.activeNum;
/*    */   }
/*    */   
/*    */   public void setActiveNum(int activeNum)
/*    */   {
/* 34 */     this.activeNum = activeNum;
/*    */   }
/*    */   
/*    */   public int getIdleNum()
/*    */   {
/* 39 */     return this.idleNum;
/*    */   }
/*    */   
/*    */   public void setIdleNum(int idleNum)
/*    */   {
/* 44 */     this.idleNum = idleNum;
/*    */   }
/*    */   
/*    */   public int getCurrQueueSize()
/*    */   {
/* 49 */     return this.currQueueSize;
/*    */   }
/*    */   
/*    */   public void setCurrQueueSize(int currQueueSize)
/*    */   {
/* 54 */     this.currQueueSize = currQueueSize;
/*    */   }
/*    */   
/*    */   public long getAvgWaitingMillis()
/*    */   {
/* 59 */     return this.avgWaitingMillis;
/*    */   }
/*    */   
/*    */   public void setAvgWaitingMillis(long avgWaitingMillis)
/*    */   {
/* 64 */     this.avgWaitingMillis = avgWaitingMillis;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getMaxNum()
/*    */   {
/* 70 */     return this.maxNum;
/*    */   }
/*    */   
/*    */   public void setMaxNum(int maxNum)
/*    */   {
/* 75 */     this.maxNum = maxNum;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 81 */     return "ThreadPoolMonitorInfo [threadPoolBeanName=" + this.threadPoolBeanName + ", activeNum=" + this.activeNum + ", idleNum=" + this.idleNum + ", currQueueSize=" + this.currQueueSize + ", avgWaitingMillis=" + this.avgWaitingMillis + "]";
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\monitor\threadpool\ThreadPoolMonitorInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */