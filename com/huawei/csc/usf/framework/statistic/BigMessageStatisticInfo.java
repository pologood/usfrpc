/*     */ package com.huawei.csc.usf.framework.statistic;
/*     */ 
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
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
/*     */ public class BigMessageStatisticInfo
/*     */ {
/*     */   private long messageId;
/*     */   private String serviceName;
/*     */   private String operationName;
/*     */   private Object params;
/*     */   private int messageSize;
/*  48 */   private boolean isServer = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  53 */   private ServiceType serviceType = ServiceType.DSF;
/*     */   
/*     */ 
/*     */   public BigMessageStatisticInfo(long messageId, String serviceName, String operationName, int messageSize)
/*     */   {
/*  58 */     this.messageId = messageId;
/*  59 */     this.serviceName = serviceName;
/*  60 */     this.operationName = operationName;
/*  61 */     this.messageSize = messageSize;
/*     */   }
/*     */   
/*     */   public long getMessageId()
/*     */   {
/*  66 */     return this.messageId;
/*     */   }
/*     */   
/*     */   public void setMessageId(long messageId)
/*     */   {
/*  71 */     this.messageId = messageId;
/*     */   }
/*     */   
/*     */   public String getServiceName()
/*     */   {
/*  76 */     return this.serviceName;
/*     */   }
/*     */   
/*     */   public void setServiceName(String serviceName)
/*     */   {
/*  81 */     this.serviceName = serviceName;
/*     */   }
/*     */   
/*     */   public String getOperationName()
/*     */   {
/*  86 */     return this.operationName;
/*     */   }
/*     */   
/*     */   public void setOperationName(String operationName)
/*     */   {
/*  91 */     this.operationName = operationName;
/*     */   }
/*     */   
/*     */   public Object getParams()
/*     */   {
/*  96 */     return this.params;
/*     */   }
/*     */   
/*     */   public <T> void setParams(T params)
/*     */   {
/* 101 */     this.params = params;
/*     */   }
/*     */   
/*     */   public int getMessageSize()
/*     */   {
/* 106 */     return this.messageSize;
/*     */   }
/*     */   
/*     */   public void setMessageSize(int messageSize)
/*     */   {
/* 111 */     this.messageSize = messageSize;
/*     */   }
/*     */   
/*     */   public boolean isServer()
/*     */   {
/* 116 */     return this.isServer;
/*     */   }
/*     */   
/*     */   public void setServer(boolean isServer)
/*     */   {
/* 121 */     this.isServer = isServer;
/*     */   }
/*     */   
/*     */   public ServiceType getServiceType()
/*     */   {
/* 126 */     return this.serviceType;
/*     */   }
/*     */   
/*     */   public void setServiceType(ServiceType serviceType)
/*     */   {
/* 131 */     this.serviceType = serviceType;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 137 */     StringBuilder sb = new StringBuilder();
/* 138 */     sb.append("messageId->");
/* 139 */     sb.append(getMessageId());
/* 140 */     sb.append(", serviceName->");
/* 141 */     sb.append(getOperationName());
/* 142 */     sb.append(", operationName->");
/* 143 */     sb.append(getOperationName());
/*     */     
/*     */ 
/*     */ 
/* 147 */     sb.append(", params->");
/* 148 */     sb.append(getParams());
/*     */     
/* 150 */     sb.append(", serviceType->");
/* 151 */     sb.append(getServiceType().toString());
/* 152 */     sb.append(", messageSize->");
/* 153 */     sb.append(getMessageSize());
/* 154 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\statistic\BigMessageStatisticInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */