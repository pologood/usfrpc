/*     */ package com.huawei.csc.usf.framework.statistic;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BigMessageStatisticCenter
/*     */ {
/*  24 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog("bigMessage");
/*     */   
/*     */ 
/*  27 */   private boolean start = false;
/*     */   
/*  29 */   private boolean logged = false;
/*     */   
/*     */   private long clientMessageThreshold;
/*     */   
/*     */   private long serverMessageThreshold;
/*     */   
/*     */   private ServiceEngine serviceEngine;
/*     */   
/*     */   public void init(ServiceEngine serviceEngine)
/*     */   {
/*  39 */     this.serviceEngine = serviceEngine;
/*  40 */     SystemConfig systemConfig = this.serviceEngine.getSystemConfig();
/*  41 */     if (!systemConfig.isBigMessageOpen())
/*     */     {
/*  43 */       return;
/*     */     }
/*  45 */     this.start = true;
/*  46 */     this.clientMessageThreshold = systemConfig.getClientMessageSizeThreshold();
/*     */     
/*  48 */     this.serverMessageThreshold = systemConfig.getServerMessageSizeThreshold();
/*     */     
/*  50 */     if (DEBUGGER.isInfoEnable())
/*     */     {
/*  52 */       DEBUGGER.info("BigMessageStatistisCenter start successfully.The message's size threshold of server is [" + this.serverMessageThreshold + "] KB,and the message's size of client is [" + this.clientMessageThreshold + "] KB");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void close()
/*     */   {
/*  61 */     if (!this.start)
/*     */     {
/*  63 */       return;
/*     */     }
/*  65 */     if (DEBUGGER.isInfoEnable())
/*     */     {
/*  67 */       DEBUGGER.info("BigMessageStatistisCenter close successfully.");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void logBigMessageStatisticInfo(BigMessageStatisticInfo bigMessageStatistic)
/*     */   {
/*  74 */     if (!this.start)
/*     */     {
/*  76 */       return;
/*     */     }
/*  78 */     if (DEBUGGER.isDebugEnable())
/*     */     {
/*  80 */       DEBUGGER.debug("Put the bigMessageStatisticInfo[" + bigMessageStatistic.toString() + "] in the center.");
/*     */     }
/*     */     
/*  83 */     long messageSize = bigMessageStatistic.getMessageSize();
/*  84 */     if (bigMessageStatistic.isServer())
/*     */     {
/*     */ 
/*  87 */       if (messageSize > this.serverMessageThreshold)
/*     */       {
/*  89 */         DEBUGGER.warn(bigMessageStatistic.toString());
/*  90 */         this.logged = true;
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */     }
/*  96 */     else if (messageSize > this.clientMessageThreshold)
/*     */     {
/*  98 */       DEBUGGER.warn(bigMessageStatistic.toString());
/*  99 */       this.logged = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isLog()
/*     */   {
/* 107 */     return this.logged;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\statistic\BigMessageStatisticCenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */