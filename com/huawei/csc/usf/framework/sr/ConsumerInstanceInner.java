/*     */ package com.huawei.csc.usf.framework.sr;
/*     */ 
/*     */ import com.huawei.csc.usf.framework.util.Utils;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.RuntimeMXBean;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class ConsumerInstanceInner
/*     */ {
/*     */   public static final String CONTENT_DELIMITER = "&";
/*     */   private String regId;
/*     */   private String instanceName;
/*     */   private String type;
/*  42 */   private String version = "0.0.0";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  47 */   private String group = "default";
/*     */   
/*     */ 
/*     */ 
/*     */   private String serviceType;
/*     */   
/*     */ 
/*  54 */   private long timeout = 10000L;
/*     */   
/*  56 */   private int retries = 2;
/*     */   
/*  58 */   private String failPolicy = "failfast";
/*     */   
/*  60 */   private String router = "poll";
/*     */   
/*     */ 
/*     */ 
/*     */   private String addrs;
/*     */   
/*     */ 
/*     */ 
/*     */   private String beanName;
/*     */   
/*     */ 
/*     */   private boolean immutable;
/*     */   
/*     */ 
/*  74 */   private List<String> methods = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  79 */   private List<String> methodTimeOut = new ArrayList();
/*     */   
/*     */ 
/*     */   public ConsumerInstanceInner(String instanceName, String type, String serviceType, String addr)
/*     */   {
/*  84 */     this.instanceName = instanceName;
/*  85 */     this.type = type;
/*  86 */     this.serviceType = serviceType;
/*  87 */     this.addrs = addr;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getVersion()
/*     */   {
/*  98 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setClusterPolicy(String failPolicy)
/*     */   {
/* 109 */     this.failPolicy = failPolicy;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getClusterPolicy()
/*     */   {
/* 120 */     return this.failPolicy;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setVersion(String version)
/*     */   {
/* 131 */     this.version = version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAddress()
/*     */   {
/* 142 */     return this.addrs;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAddress(String Address)
/*     */   {
/* 153 */     this.addrs = Address;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getGroup()
/*     */   {
/* 164 */     return this.group;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setGroup(String group)
/*     */   {
/* 175 */     this.group = group;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getInstanceName()
/*     */   {
/* 185 */     return this.instanceName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setInstanceName(String instanceName)
/*     */   {
/* 196 */     this.instanceName = instanceName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getBeanName()
/*     */   {
/* 206 */     return this.beanName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setBeanName(String beanName)
/*     */   {
/* 217 */     this.beanName = beanName;
/*     */   }
/*     */   
/*     */   public String getType()
/*     */   {
/* 222 */     return this.type;
/*     */   }
/*     */   
/*     */   public void setType(String type)
/*     */   {
/* 227 */     this.type = type;
/*     */   }
/*     */   
/*     */   public String getRouter()
/*     */   {
/* 232 */     return this.router;
/*     */   }
/*     */   
/*     */   public void setRouter(String router)
/*     */   {
/* 237 */     this.router = router;
/*     */   }
/*     */   
/*     */   public int getReSentTime()
/*     */   {
/* 242 */     return this.retries;
/*     */   }
/*     */   
/*     */   public void setReSentTime(int reSent)
/*     */   {
/* 247 */     this.retries = reSent;
/*     */   }
/*     */   
/*     */   public long getMethodTimeOut()
/*     */   {
/* 252 */     return this.timeout;
/*     */   }
/*     */   
/*     */   public void setMethodTimeOut(long timeout)
/*     */   {
/* 257 */     this.timeout = timeout;
/*     */   }
/*     */   
/*     */   public String getServiceType()
/*     */   {
/* 262 */     return this.serviceType;
/*     */   }
/*     */   
/*     */   public void setServiceType(String serviceType)
/*     */   {
/* 267 */     this.serviceType = serviceType;
/*     */   }
/*     */   
/*     */   public boolean isImmutable()
/*     */   {
/* 272 */     return this.immutable;
/*     */   }
/*     */   
/*     */   public void setImmutable(boolean immutable)
/*     */   {
/* 277 */     this.immutable = immutable;
/*     */   }
/*     */   
/*     */   public List<String> getMethods()
/*     */   {
/* 282 */     return this.methods;
/*     */   }
/*     */   
/*     */   public void setMethods(List<String> methods)
/*     */   {
/* 287 */     this.methods = methods;
/*     */   }
/*     */   
/*     */   public List<String> getMethodExceutes()
/*     */   {
/* 292 */     return this.methodTimeOut;
/*     */   }
/*     */   
/*     */   public void setMethodExceutes(List<String> methods)
/*     */   {
/* 297 */     this.methodTimeOut = methods;
/*     */   }
/*     */   
/*     */   public void addMethodTimeOut(String methodTimeout)
/*     */   {
/* 302 */     this.methodTimeOut.add(methodTimeout);
/*     */   }
/*     */   
/*     */   public void setRegId(String regId) {
/* 306 */     this.regId = regId;
/*     */   }
/*     */   
/*     */   public String getRegId()
/*     */   {
/* 311 */     return this.regId;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 316 */     StringBuilder sb = new StringBuilder();
/* 317 */     sb.append("{address=");
/* 318 */     sb.append(this.addrs);
/* 319 */     sb.append(", instanceName=");
/* 320 */     sb.append(this.instanceName);
/* 321 */     sb.append(", type=");
/* 322 */     sb.append(this.type);
/* 323 */     sb.append(",group=");
/* 324 */     sb.append(this.group);
/* 325 */     sb.append(",serviceType=");
/* 326 */     sb.append(this.serviceType);
/* 327 */     sb.append(",beanName=");
/* 328 */     sb.append(this.beanName);
/* 329 */     sb.append("}");
/*     */     
/* 331 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 337 */     int prime = 31;
/* 338 */     int result = 1;
/* 339 */     result = 31 * result + (this.addrs == null ? 0 : this.addrs.hashCode());
/* 340 */     result = 31 * result + (this.group == null ? 0 : this.group.hashCode());
/* 341 */     result = 31 * result + (this.instanceName == null ? 0 : this.instanceName.hashCode());
/*     */     
/* 343 */     result = 31 * result + (this.beanName == null ? 0 : this.beanName.hashCode());
/*     */     
/* 345 */     result = 31 * result + (this.version == null ? 0 : this.version.hashCode());
/* 346 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 352 */     if (this == obj)
/* 353 */       return true;
/* 354 */     if (obj == null)
/* 355 */       return false;
/* 356 */     if (getClass() != obj.getClass())
/* 357 */       return false;
/* 358 */     ConsumerInstanceInner other = (ConsumerInstanceInner)obj;
/* 359 */     if (this.addrs == null)
/*     */     {
/* 361 */       if (other.addrs != null) {
/* 362 */         return false;
/*     */       }
/* 364 */     } else if (!this.addrs.equals(other.addrs))
/* 365 */       return false;
/* 366 */     if (this.group == null)
/*     */     {
/* 368 */       if (other.group != null) {
/* 369 */         return false;
/*     */       }
/* 371 */     } else if (!this.group.equals(other.group))
/* 372 */       return false;
/* 373 */     if (this.instanceName == null)
/*     */     {
/* 375 */       if (other.instanceName != null) {
/* 376 */         return false;
/*     */       }
/* 378 */     } else if (!this.instanceName.equals(other.instanceName))
/* 379 */       return false;
/* 380 */     if (this.serviceType == null)
/*     */     {
/* 382 */       if (other.serviceType != null) {
/* 383 */         return false;
/*     */       }
/* 385 */     } else if (!this.serviceType.equals(other.serviceType))
/* 386 */       return false;
/* 387 */     if (this.version == null)
/*     */     {
/* 389 */       if (other.version != null) {
/* 390 */         return false;
/*     */       }
/* 392 */     } else if (!this.version.equals(other.version))
/* 393 */       return false;
/* 394 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toUrl(String application)
/*     */   {
/* 401 */     StringBuilder builder = new StringBuilder();
/* 402 */     builder.append("consumer");
/* 403 */     builder.append("://");
/* 404 */     builder.append(Utils.getHostIp());
/* 405 */     builder.append("/");
/* 406 */     builder.append(this.instanceName);
/* 407 */     builder.append("?");
/* 408 */     builder.append("category=");
/* 409 */     builder.append("consumers");
/* 410 */     builder.append("&");
/* 411 */     builder.append("application=");
/* 412 */     builder.append(application);
/* 413 */     builder.append("&");
/* 414 */     builder.append("version=");
/* 415 */     builder.append(this.version);
/* 416 */     builder.append("&");
/* 417 */     if (this.group != null)
/*     */     {
/* 419 */       builder.append("group=");
/* 420 */       builder.append(this.group);
/* 421 */       builder.append("&");
/*     */     }
/* 423 */     builder.append("timeout=");
/* 424 */     builder.append(this.timeout);
/* 425 */     builder.append("&");
/* 426 */     builder.append("retries=");
/* 427 */     builder.append(this.retries);
/* 428 */     builder.append("&");
/* 429 */     builder.append("cluster=");
/* 430 */     builder.append(this.failPolicy);
/* 431 */     builder.append("&");
/* 432 */     builder.append("router=");
/* 433 */     builder.append(this.router);
/* 434 */     builder.append("&");
/* 435 */     builder.append("pid=");
/* 436 */     String pidName = ManagementFactory.getRuntimeMXBean().getName();
/* 437 */     String pid = pidName.split("@")[0];
/* 438 */     builder.append(pid);
/* 439 */     builder.append("&");
/* 440 */     if (!this.methods.isEmpty())
/*     */     {
/* 442 */       builder.append("methods=");
/* 443 */       for (String methodName : this.methods)
/*     */       {
/* 445 */         builder.append(methodName);
/* 446 */         builder.append(",");
/*     */       }
/* 448 */       builder.deleteCharAt(builder.length() - 1);
/* 449 */       builder.append("&");
/*     */     }
/* 451 */     if (!this.methodTimeOut.isEmpty())
/*     */     {
/* 453 */       for (String timeout : this.methodTimeOut)
/*     */       {
/* 455 */         String[] methods = timeout.split("\\.");
/* 456 */         if (methods.length >= 2)
/*     */         {
/* 458 */           builder.append(methods[0]).append(".timeout=");
/* 459 */           builder.append(methods[1]);
/* 460 */           builder.append("&");
/*     */         }
/*     */       }
/*     */     }
/* 464 */     builder.append("serviceType=");
/* 465 */     builder.append(this.serviceType);
/* 466 */     builder.append("&");
/* 467 */     builder.append("type=");
/* 468 */     builder.append(this.type);
/* 469 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\ConsumerInstanceInner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */