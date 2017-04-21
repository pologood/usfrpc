/*     */ package com.huawei.csc.usf.framework.message;
/*     */ 
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.util.USFCtxObject;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.lang.StringUtils;
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
/*     */ public class MessageHeadersImpl
/*     */   implements MessageHeaders
/*     */ {
/*     */   private String destAddr;
/*     */   private String group;
/*     */   private String serviceName;
/*     */   private String operation;
/*     */   private String policyServiceName;
/*     */   private String policyOperation;
/*     */   private String policyProtocol;
/*     */   private String srcAddr;
/*     */   private String type;
/*     */   private String version;
/*     */   private long requestId;
/*  53 */   private Map<String, USFCtxObject> attachment = new HashMap();
/*     */   
/*     */ 
/*     */   public String getDestAddr()
/*     */   {
/*  58 */     return this.destAddr;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setDestAddr(String destAddr)
/*     */   {
/*  64 */     this.destAddr = destAddr;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getGroup()
/*     */   {
/*  70 */     return this.group;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setGroup(String group)
/*     */   {
/*  76 */     this.group = group;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getServiceName()
/*     */   {
/*  82 */     return this.serviceName;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setServiceName(String serviceName)
/*     */   {
/*  88 */     this.serviceName = serviceName;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getOperation()
/*     */   {
/*  94 */     return this.operation;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setOperation(String operation)
/*     */   {
/* 100 */     this.operation = operation;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getPolicyServiceName()
/*     */   {
/* 106 */     return this.policyServiceName;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setPolicyServiceName(String policyServiceName)
/*     */   {
/* 112 */     this.policyServiceName = policyServiceName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getPolicyOperation()
/*     */   {
/* 119 */     return this.policyOperation;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setPolicyOperation(String policyOperation)
/*     */   {
/* 125 */     this.policyOperation = policyOperation;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getPolicyProtocol()
/*     */   {
/* 131 */     return this.policyProtocol;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setPolicyProtocol(String policyProtocol)
/*     */   {
/* 137 */     this.policyProtocol = policyProtocol;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getSrcAddr()
/*     */   {
/* 143 */     return this.srcAddr;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setSrcAddr(String srcAddr)
/*     */   {
/* 149 */     this.srcAddr = srcAddr;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getType()
/*     */   {
/* 155 */     return this.type;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setType(String type)
/*     */   {
/* 161 */     this.type = type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isType(String type)
/*     */   {
/* 168 */     return this.type.equals(type);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getRequestId()
/*     */   {
/* 174 */     return this.requestId;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setRequestId(long requestId)
/*     */   {
/* 180 */     this.requestId = requestId;
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, USFCtxObject> getAttachment()
/*     */   {
/* 186 */     return this.attachment;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setAttachment(Map<String, USFCtxObject> attachment)
/*     */   {
/* 192 */     this.attachment = attachment;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 198 */     return "MessageHeadersImpl [destAddr=" + this.destAddr + ", serviceName=" + this.serviceName + ", operation=" + this.operation + ", policyServiceName=" + this.policyServiceName + ", policyOperation=" + this.policyOperation + ", policyProtocol=" + this.policyProtocol + ", srcAddr=" + this.srcAddr + ", type=" + this.type + ", requestId=" + this.requestId + "]";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAttachValue(String key)
/*     */   {
/* 209 */     if (StringUtils.isBlank(key))
/*     */     {
/* 211 */       return "";
/*     */     }
/* 213 */     return this.attachment.get(key) == null ? "" : ((USFCtxObject)this.attachment.get(key)).toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setAttachValue(String key, String value)
/*     */   {
/* 220 */     if ((StringUtils.isBlank(key)) || (null == value))
/*     */     {
/* 222 */       return;
/*     */     }
/* 224 */     USFCtxObject ctxObject = new USFCtxObject(value);
/* 225 */     ctxObject.setValue(value);
/* 226 */     this.attachment.put(key, ctxObject);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getVersion()
/*     */   {
/* 232 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setVersion(String version)
/*     */   {
/* 238 */     this.version = version;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\message\MessageHeadersImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */