/*     */ package com.huawei.csc.usf.framework.circuitbreaker;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.Connector;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.sr.SRAgentFactory;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceRegistryAgent;
/*     */ 
/*     */ public class DSFFaultToleranceProperties
/*     */ {
/*  14 */   private static final DebugLog DEBUGGER = com.huawei.csc.kernel.api.log.LogFactory.getDebugLog(DSFFaultToleranceProperties.class);
/*     */   private String circuitBreakerEnable;
/*     */   
/*  17 */   public DSFFaultToleranceProperties() { this.circuitBreakerEnable = "false";
/*     */     
/*  19 */     this.healthSnapshotInterval = Integer.valueOf(0);
/*     */     
/*  21 */     this.requestVolumeThreadHold = Integer.valueOf(Integer.MAX_VALUE);
/*     */     
/*  23 */     this.errThreadHoldPercentage = Integer.valueOf(100);
/*     */     
/*  25 */     this.sleepWindow = Integer.valueOf(5000);
/*     */     
/*  27 */     this.isolationStategy = null;
/*     */     
/*  29 */     this.bulkheadThreadNum = Integer.valueOf(10);
/*     */     
/*  31 */     this.bulkheadQueueSize = Integer.valueOf(10);
/*     */     
/*  33 */     this.bulkheadMaxConcurrentRequests = Integer.valueOf(Integer.MAX_VALUE); }
/*     */   
/*     */   private Integer healthSnapshotInterval;
/*     */   private Integer requestVolumeThreadHold;
/*     */   private Integer errThreadHoldPercentage;
/*     */   private Integer sleepWindow;
/*     */   private String isolationStategy;
/*     */   
/*  41 */   public Integer getHealthSnapshotInterval() { return this.healthSnapshotInterval; }
/*     */   
/*     */   private Integer bulkheadThreadNum;
/*     */   private Integer bulkheadQueueSize;
/*     */   
/*  46 */   public Integer getRequestVolumeThreadHold() { return this.requestVolumeThreadHold; }
/*     */   
/*     */   private Integer bulkheadMaxConcurrentRequests;
/*     */   private Integer bulkheadTimeInMilliseconds;
/*     */   private Integer bulkheadNumberOfBuckets;
/*  51 */   public Integer getErrThreadHoldPercentage() { return this.errThreadHoldPercentage; }
/*     */   
/*     */ 
/*     */   public Integer getSleepWindow()
/*     */   {
/*  56 */     return this.sleepWindow;
/*     */   }
/*     */   
/*     */   public String getIsolationStategy()
/*     */   {
/*  61 */     return this.isolationStategy;
/*     */   }
/*     */   
/*     */   public Integer getBulkheadThreadNum()
/*     */   {
/*  66 */     return this.bulkheadThreadNum;
/*     */   }
/*     */   
/*     */   public Integer getBulkheadQueueSize()
/*     */   {
/*  71 */     return this.bulkheadQueueSize;
/*     */   }
/*     */   
/*     */   public Integer getBulkheadMaxConcurrentRequests()
/*     */   {
/*  76 */     return this.bulkheadMaxConcurrentRequests;
/*     */   }
/*     */   
/*     */   public String getCircuitBreakerEnable()
/*     */   {
/*  81 */     return this.circuitBreakerEnable;
/*     */   }
/*     */   
/*     */   public void setCircuitBreakerEnable(String circuitBreakerEnable)
/*     */   {
/*  86 */     this.circuitBreakerEnable = circuitBreakerEnable;
/*     */   }
/*     */   
/*     */   public void setHealthSnapshotInterval(Integer healthSnapshotInterval)
/*     */   {
/*  91 */     this.healthSnapshotInterval = healthSnapshotInterval;
/*     */   }
/*     */   
/*     */   public void setRequestVolumeThreadHold(Integer requestVolumeThreadHold)
/*     */   {
/*  96 */     this.requestVolumeThreadHold = requestVolumeThreadHold;
/*     */   }
/*     */   
/*     */   public void setErrThreadHoldPercentage(Integer errThreadHoldPercentage)
/*     */   {
/* 101 */     this.errThreadHoldPercentage = errThreadHoldPercentage;
/*     */   }
/*     */   
/*     */   public void setSleepWindow(Integer sleepWindow)
/*     */   {
/* 106 */     this.sleepWindow = sleepWindow;
/*     */   }
/*     */   
/*     */   public void setIsolationStategy(String isolationStategy)
/*     */   {
/* 111 */     this.isolationStategy = isolationStategy;
/*     */   }
/*     */   
/*     */   public void setBulkheadThreadNum(Integer bulkheadThreadNum)
/*     */   {
/* 116 */     this.bulkheadThreadNum = bulkheadThreadNum;
/*     */   }
/*     */   
/*     */   public void setBulkheadQueueSize(Integer bulkheadQueueSize)
/*     */   {
/* 121 */     this.bulkheadQueueSize = bulkheadQueueSize;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setBulkheadMaxConcurrentRequests(Integer bulkheadMaxConcurrentRequests)
/*     */   {
/* 127 */     this.bulkheadMaxConcurrentRequests = bulkheadMaxConcurrentRequests;
/*     */   }
/*     */   
/*     */   public Integer getBulkheadTimeInMilliseconds()
/*     */   {
/* 132 */     return this.bulkheadTimeInMilliseconds;
/*     */   }
/*     */   
/*     */   public void setBulkheadTimeInMilliseconds(Integer bulkheadTimeInMilliseconds)
/*     */   {
/* 137 */     this.bulkheadTimeInMilliseconds = bulkheadTimeInMilliseconds;
/*     */   }
/*     */   
/*     */   public Integer getBulkheadNumberOfBuckets()
/*     */   {
/* 142 */     return this.bulkheadNumberOfBuckets;
/*     */   }
/*     */   
/*     */   public void setBulkheadNumberOfBuckets(Integer bulkheadNumberOfBuckets)
/*     */   {
/* 147 */     this.bulkheadNumberOfBuckets = bulkheadNumberOfBuckets;
/*     */   }
/*     */   
/*     */ 
/*     */   public DSFFaultToleranceProperties buildCircuitBreakerProperties(Context context)
/*     */   {
/* 153 */     IMessage receivedMessage = context.getReceivedMessage();
/* 154 */     String serviceName = receivedMessage.getHeaders().getServiceName();
/* 155 */     DSFFaultToleranceProperties circuitBreakerProperties = new DSFFaultToleranceProperties();
/* 156 */     String dsfApplication = context.getSrcConnector().getServiceEngine().getSystemConfig().getDsfApplication();
/*     */     
/* 158 */     SRAgentFactory srAgentFactory = context.getSrcConnector().getServiceEngine().getSrAgentFactory();
/*     */     
/* 160 */     String address = context.getSrcConnector().getServiceEngine().getSystemConfig().getRPCAddress(context.getServiceType());
/*     */     
/*     */ 
/* 163 */     DSFFaultToleranceProperties zkconfigCircuitBreakerProperties = (DSFFaultToleranceProperties)srAgentFactory.getSRAgent(context.getRegistry()).getZookeeperDataManager().getConfigFromConfiguration(serviceName, "&" + serviceName, address, dsfApplication, "circuitBreaker", "consumer");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 172 */     if (null != zkconfigCircuitBreakerProperties)
/*     */     {
/* 174 */       if (DEBUGGER.isInfoEnable())
/*     */       {
/* 176 */         DEBUGGER.info("Get executes from zookeeper configurations, current circuitBreakEnable is: " + zkconfigCircuitBreakerProperties.getCircuitBreakerEnable() + "healthSnapshotInterval is:" + zkconfigCircuitBreakerProperties.getHealthSnapshotInterval() + " maxConcurrentRequests is:" + zkconfigCircuitBreakerProperties.getBulkheadMaxConcurrentRequests() + " queueSize is:" + zkconfigCircuitBreakerProperties.getBulkheadQueueSize() + " threadNum is:" + zkconfigCircuitBreakerProperties.getBulkheadThreadNum() + " errThreadHoldPercentage is:" + zkconfigCircuitBreakerProperties.getErrThreadHoldPercentage() + " requestVolume is:" + zkconfigCircuitBreakerProperties.getRequestVolumeThreadHold() + " sleepWindow is:" + zkconfigCircuitBreakerProperties.getSleepWindow() + " isolationStategy is:" + zkconfigCircuitBreakerProperties.getIsolationStategy());
/*     */       }
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
/* 204 */       if (null != zkconfigCircuitBreakerProperties.getCircuitBreakerEnable())
/*     */       {
/*     */ 
/* 207 */         circuitBreakerProperties.setCircuitBreakerEnable(zkconfigCircuitBreakerProperties.getCircuitBreakerEnable());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 212 */       if (null != zkconfigCircuitBreakerProperties.getBulkheadMaxConcurrentRequests())
/*     */       {
/*     */ 
/* 215 */         circuitBreakerProperties.setBulkheadMaxConcurrentRequests(zkconfigCircuitBreakerProperties.getBulkheadMaxConcurrentRequests());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 220 */       if (null != zkconfigCircuitBreakerProperties.getBulkheadQueueSize())
/*     */       {
/* 222 */         circuitBreakerProperties.setBulkheadQueueSize(zkconfigCircuitBreakerProperties.getBulkheadQueueSize());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 227 */       if (null != zkconfigCircuitBreakerProperties.getBulkheadThreadNum())
/*     */       {
/* 229 */         circuitBreakerProperties.setBulkheadThreadNum(zkconfigCircuitBreakerProperties.getBulkheadThreadNum());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 234 */       if (null != zkconfigCircuitBreakerProperties.getErrThreadHoldPercentage())
/*     */       {
/*     */ 
/* 237 */         circuitBreakerProperties.setErrThreadHoldPercentage(zkconfigCircuitBreakerProperties.getErrThreadHoldPercentage());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 242 */       if (null != zkconfigCircuitBreakerProperties.getRequestVolumeThreadHold())
/*     */       {
/*     */ 
/* 245 */         circuitBreakerProperties.setRequestVolumeThreadHold(zkconfigCircuitBreakerProperties.getRequestVolumeThreadHold());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 250 */       if (null != zkconfigCircuitBreakerProperties.getSleepWindow())
/*     */       {
/* 252 */         circuitBreakerProperties.setSleepWindow(zkconfigCircuitBreakerProperties.getSleepWindow());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 257 */       if (null != zkconfigCircuitBreakerProperties.getIsolationStategy())
/*     */       {
/* 259 */         circuitBreakerProperties.setIsolationStategy(zkconfigCircuitBreakerProperties.getIsolationStategy());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 264 */       if (null != zkconfigCircuitBreakerProperties.getHealthSnapshotInterval())
/*     */       {
/*     */ 
/* 267 */         circuitBreakerProperties.setHealthSnapshotInterval(zkconfigCircuitBreakerProperties.getHealthSnapshotInterval());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 274 */     if (DEBUGGER.isDebugEnable())
/*     */     {
/* 276 */       DEBUGGER.debug("CircuitBreakerProperties config, current circuitBreakEnable is: " + circuitBreakerProperties.getCircuitBreakerEnable() + "healthSnapshotInterval is:" + circuitBreakerProperties.getHealthSnapshotInterval() + " maxConcurrentRequests is:" + circuitBreakerProperties.getBulkheadMaxConcurrentRequests() + " queueSize is:" + circuitBreakerProperties.getBulkheadQueueSize() + " threadNum is:" + circuitBreakerProperties.getBulkheadThreadNum() + " errThreadHoldPercentage is:" + circuitBreakerProperties.getErrThreadHoldPercentage() + " requestVolume is:" + circuitBreakerProperties.getRequestVolumeThreadHold() + " sleepWindow is:" + circuitBreakerProperties.getSleepWindow() + " isolationStategy is:" + circuitBreakerProperties.getIsolationStategy());
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/* 296 */     return circuitBreakerProperties;
/*     */   }
/*     */   
/*     */ 
/*     */   public static enum DsfBulkheadIsolationStategy
/*     */   {
/* 302 */     THREAD,  SEMAPHORE;
/*     */     
/*     */     private DsfBulkheadIsolationStategy() {}
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\circuitbreaker\DSFFaultToleranceProperties.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */