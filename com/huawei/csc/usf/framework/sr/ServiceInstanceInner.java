/*     */ package com.huawei.csc.usf.framework.sr;
/*     */ 
/*     */ import com.huawei.csc.usf.framework.util.Utils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
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
/*     */ public class ServiceInstanceInner
/*     */ {
/*     */   public static final String TIME_DELAY = "timeDelay";
/*     */   public static final String CONTENT_DELIMITER = "&";
/*     */   public static final String PAAS_HOST_ID = "PAAS_HOST_ID";
/*     */   public static final String DEFAULT_GROUP = "default";
/*     */   public static final String DEFAULT_VERSION = "0.0.0";
/*     */   private String serviceName;
/*     */   private String instanceName;
/*     */   private String address;
/*  45 */   private boolean alive = true;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private String type;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private String restUrl;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  60 */   private String version = "0.0.0";
/*     */   
/*     */ 
/*     */ 
/*     */   private ServiceVersion serviceVersion;
/*     */   
/*     */ 
/*  67 */   private String group = "default";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  72 */   private String dsf = "2.0";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  77 */   private String serviceGroup = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private String application;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private String serviceType;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  92 */   private int weight = 10;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  97 */   private int executes = 0;
/*     */   
/*  99 */   private long timeout = 0L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 104 */   private String tpsThreshold = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 109 */   private String pid = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 114 */   private String router = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 119 */   private Map<String, Object> attrs = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean immutable;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 131 */   private List<String> methods = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 136 */   private List<String> methodExcetes = new ArrayList();
/*     */   
/* 138 */   private List<String> methodTimeout = new ArrayList();
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
/*     */   public String getVersion()
/*     */   {
/* 162 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ServiceVersion getServiceVersion()
/*     */   {
/* 173 */     return this.serviceVersion;
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
/* 184 */     this.version = version;
/* 185 */     this.serviceVersion = new ServiceVersion(version);
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
/* 196 */     return this.group;
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
/* 207 */     this.group = group;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getRestfulUrl()
/*     */   {
/* 218 */     return this.restUrl;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRestfulUrl(String restUrl)
/*     */   {
/* 229 */     this.restUrl = restUrl;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getServiceGroup()
/*     */   {
/* 240 */     return this.serviceGroup;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setServiceGroup(String serviceGroup)
/*     */   {
/* 251 */     this.serviceGroup = serviceGroup;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getApplication()
/*     */   {
/* 262 */     return this.application;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setApplication(String application)
/*     */   {
/* 273 */     this.application = application;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getInstanceName()
/*     */   {
/* 283 */     return this.instanceName;
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
/* 294 */     this.instanceName = instanceName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getServiceName()
/*     */   {
/* 304 */     return this.serviceName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setServiceName(String serviceName)
/*     */   {
/* 315 */     this.serviceName = serviceName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAddress()
/*     */   {
/* 325 */     return this.address;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAddress(String address)
/*     */   {
/* 336 */     this.address = address;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isAlive()
/*     */   {
/* 346 */     return this.alive;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAlive(boolean alive)
/*     */   {
/* 357 */     this.alive = alive;
/*     */   }
/*     */   
/*     */   public String getType()
/*     */   {
/* 362 */     return this.type;
/*     */   }
/*     */   
/*     */   public void setType(String type)
/*     */   {
/* 367 */     this.type = type;
/*     */   }
/*     */   
/*     */   public String getServiceType()
/*     */   {
/* 372 */     return this.serviceType;
/*     */   }
/*     */   
/*     */   public void setServiceType(String serviceType)
/*     */   {
/* 377 */     this.serviceType = serviceType;
/*     */   }
/*     */   
/*     */   public Map<String, Object> getAttrs()
/*     */   {
/* 382 */     return this.attrs;
/*     */   }
/*     */   
/*     */   public void setAttrs(Map<String, Object> attrs)
/*     */   {
/* 387 */     this.attrs = attrs;
/*     */   }
/*     */   
/*     */   public Object getAttr(Object key)
/*     */   {
/* 392 */     return this.attrs.get(key);
/*     */   }
/*     */   
/*     */   public void setAttr(String key, Object value)
/*     */   {
/* 397 */     this.attrs.put(key, value);
/*     */   }
/*     */   
/*     */   public boolean isImmutable()
/*     */   {
/* 402 */     return this.immutable;
/*     */   }
/*     */   
/*     */   public void setImmutable(boolean immutable)
/*     */   {
/* 407 */     this.immutable = immutable;
/*     */   }
/*     */   
/*     */   public int getWeight()
/*     */   {
/* 412 */     return this.weight;
/*     */   }
/*     */   
/*     */   public void setWeight(int weight)
/*     */   {
/* 417 */     this.weight = weight;
/*     */   }
/*     */   
/*     */   public List<String> getMethods()
/*     */   {
/* 422 */     return this.methods;
/*     */   }
/*     */   
/*     */   public void setMethods(List<String> methods)
/*     */   {
/* 427 */     this.methods = methods;
/*     */   }
/*     */   
/*     */   public List<String> getMethodExceutes()
/*     */   {
/* 432 */     return this.methodExcetes;
/*     */   }
/*     */   
/*     */   public void setMethodExceutes(List<String> methods)
/*     */   {
/* 437 */     this.methodExcetes = methods;
/*     */   }
/*     */   
/*     */   public List<String> getMethodTimeout()
/*     */   {
/* 442 */     return this.methodTimeout;
/*     */   }
/*     */   
/*     */   public void addMethodTimeout(List<String> timeout)
/*     */   {
/* 447 */     this.methodTimeout = timeout;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 453 */     StringBuilder sb = new StringBuilder();
/* 454 */     sb.append("{address=");
/* 455 */     sb.append(this.address);
/* 456 */     sb.append(", alive=");
/* 457 */     sb.append(this.alive);
/* 458 */     sb.append(", instanceName=");
/* 459 */     sb.append(this.instanceName);
/* 460 */     sb.append(", serviceName=");
/* 461 */     sb.append(this.serviceName);
/* 462 */     sb.append(", group=");
/* 463 */     sb.append(this.group);
/* 464 */     sb.append(", serviceGroup=");
/* 465 */     sb.append(this.serviceGroup);
/* 466 */     sb.append(", application=");
/* 467 */     sb.append(this.application);
/* 468 */     sb.append(", type=");
/* 469 */     sb.append(this.type);
/* 470 */     sb.append(", weight=");
/* 471 */     sb.append(this.weight);
/* 472 */     sb.append(",serviceType=");
/* 473 */     sb.append(this.serviceType);
/* 474 */     if (this.pid != null)
/*     */     {
/* 476 */       sb.append(",pid=");
/* 477 */       sb.append(this.pid);
/*     */     }
/* 479 */     if (this.version != null)
/*     */     {
/* 481 */       sb.append(",version=");
/* 482 */       sb.append(this.version);
/*     */     }
/* 484 */     sb.append("}");
/*     */     
/* 486 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 492 */     int prime = 31;
/* 493 */     int result = 1;
/* 494 */     result = 31 * result + (this.type == null ? 0 : this.type.hashCode());
/* 495 */     result = 31 * result + (this.address == null ? 0 : this.address.hashCode());
/* 496 */     result = 31 * result + (this.group == null ? 0 : this.group.hashCode());
/* 497 */     result = 31 * result + (this.instanceName == null ? 0 : this.instanceName.hashCode());
/*     */     
/* 499 */     result = 31 * result + (this.serviceName == null ? 0 : this.serviceName.hashCode());
/*     */     
/* 501 */     result = 31 * result + (this.serviceType == null ? 0 : this.serviceType.hashCode());
/*     */     
/* 503 */     result = 31 * result + (this.version == null ? 0 : this.version.hashCode());
/* 504 */     result = 31 * result + (this.pid == null ? 0 : this.pid.hashCode());
/* 505 */     result = 31 * result + this.weight;
/* 506 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 512 */     if (this == obj)
/* 513 */       return true;
/* 514 */     if (obj == null)
/* 515 */       return false;
/* 516 */     if (getClass() != obj.getClass())
/* 517 */       return false;
/* 518 */     ServiceInstanceInner other = (ServiceInstanceInner)obj;
/* 519 */     if (this.address == null)
/*     */     {
/* 521 */       if (other.address != null) {
/* 522 */         return false;
/*     */       }
/* 524 */     } else if (!this.address.equals(other.address))
/* 525 */       return false;
/* 526 */     if (this.group == null)
/*     */     {
/* 528 */       if (other.group != null) {
/* 529 */         return false;
/*     */       }
/* 531 */     } else if (!this.group.equals(other.group))
/* 532 */       return false;
/* 533 */     if (this.instanceName == null)
/*     */     {
/* 535 */       if (other.instanceName != null) {
/* 536 */         return false;
/*     */       }
/* 538 */     } else if (!this.instanceName.equals(other.instanceName))
/* 539 */       return false;
/* 540 */     if (this.serviceName == null)
/*     */     {
/* 542 */       if (other.serviceName != null) {
/* 543 */         return false;
/*     */       }
/* 545 */     } else if (!this.serviceName.equals(other.serviceName))
/* 546 */       return false;
/* 547 */     if (this.serviceType == null)
/*     */     {
/* 549 */       if (other.serviceType != null) {
/* 550 */         return false;
/*     */       }
/* 552 */     } else if (!this.serviceType.equals(other.serviceType))
/* 553 */       return false;
/* 554 */     if (this.type == null)
/*     */     {
/* 556 */       if (other.type != null) {
/* 557 */         return false;
/*     */       }
/* 559 */     } else if (!this.type.equals(other.type))
/* 560 */       return false;
/* 561 */     if (this.version == null)
/*     */     {
/* 563 */       if (other.version != null) {
/* 564 */         return false;
/*     */       }
/* 566 */     } else if (!this.version.equals(other.version))
/* 567 */       return false;
/* 568 */     if (this.weight != other.weight)
/* 569 */       return false;
/* 570 */     if (this.pid == null)
/*     */     {
/* 572 */       if (other.pid != null) {
/* 573 */         return false;
/*     */       }
/* 575 */     } else if (!this.pid.equals(other.pid)) {
/* 576 */       return false;
/*     */     }
/* 578 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getExecutes()
/*     */   {
/* 589 */     return this.executes;
/*     */   }
/*     */   
/*     */   public void setExecutes(int executes)
/*     */   {
/* 594 */     this.executes = executes;
/*     */   }
/*     */   
/*     */   public String getTpsThreshold()
/*     */   {
/* 599 */     return this.tpsThreshold;
/*     */   }
/*     */   
/*     */   public void setTpsThreshold(String tpsThreshold)
/*     */   {
/* 604 */     this.tpsThreshold = tpsThreshold;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toUrl(String application)
/*     */   {
/* 611 */     StringBuilder builder = new StringBuilder();
/* 612 */     builder.append(this.type);
/* 613 */     builder.append("://");
/* 614 */     builder.append(this.address);
/* 615 */     builder.append("/");
/* 616 */     builder.append(this.instanceName);
/* 617 */     builder.append("?");
/* 618 */     builder.append("category=");
/* 619 */     builder.append("providers");
/* 620 */     builder.append("&");
/* 621 */     builder.append("application=");
/* 622 */     builder.append(application);
/* 623 */     builder.append("&");
/* 624 */     builder.append("version=");
/* 625 */     builder.append(this.version);
/* 626 */     builder.append("&");
/* 627 */     builder.append("dsf=");
/* 628 */     builder.append(this.dsf);
/* 629 */     builder.append("&");
/* 630 */     builder.append("group=");
/* 631 */     builder.append(this.group);
/* 632 */     builder.append("&");
/*     */     
/* 634 */     if (!StringUtils.isEmpty(this.router))
/*     */     {
/* 636 */       builder.append("router=");
/* 637 */       builder.append(this.router);
/* 638 */       builder.append("&");
/*     */     }
/*     */     
/* 641 */     if (!StringUtils.isEmpty(this.serviceGroup))
/*     */     {
/* 643 */       builder.append("serviceGroup=");
/* 644 */       builder.append(this.serviceGroup);
/* 645 */       builder.append("&");
/*     */     }
/* 647 */     if (this.timeout != 0L)
/*     */     {
/* 649 */       builder.append("timeout=");
/* 650 */       builder.append(this.timeout);
/* 651 */       builder.append("&");
/*     */     }
/* 653 */     builder.append("weight=");
/* 654 */     builder.append(this.weight);
/* 655 */     builder.append("&");
/* 656 */     builder.append("executes=");
/* 657 */     builder.append(this.executes);
/* 658 */     builder.append("&");
/* 659 */     builder.append("pid=");
/* 660 */     builder.append(this.pid);
/*     */     
/* 662 */     builder.append("&");
/* 663 */     if (!this.methods.isEmpty())
/*     */     {
/* 665 */       builder.append("methods=");
/* 666 */       for (String methodName : this.methods)
/*     */       {
/* 668 */         builder.append(methodName);
/* 669 */         builder.append(",");
/*     */       }
/* 671 */       builder.deleteCharAt(builder.length() - 1);
/* 672 */       builder.append("&");
/*     */     }
/* 674 */     if (!this.methodExcetes.isEmpty())
/*     */     {
/* 676 */       for (String methodexcetes : this.methodExcetes)
/*     */       {
/* 678 */         String[] methods = methodexcetes.split("\\.");
/* 679 */         if (methods.length >= 2)
/*     */         {
/* 681 */           builder.append(methods[0]).append(".exceutes=");
/* 682 */           builder.append(methods[1]);
/* 683 */           builder.append("&");
/*     */         }
/*     */       }
/*     */     }
/* 687 */     if (!this.methodTimeout.isEmpty())
/*     */     {
/* 689 */       for (String methodtimeout : this.methodTimeout)
/*     */       {
/* 691 */         String[] methods = methodtimeout.split("\\.");
/* 692 */         if (methods.length >= 2)
/*     */         {
/* 694 */           builder.append(methods[0]).append(".timeout=");
/* 695 */           builder.append(methods[1]);
/* 696 */           builder.append("&");
/*     */         }
/*     */       }
/*     */     }
/* 700 */     if (!StringUtils.isEmpty(this.serviceName))
/*     */     {
/* 702 */       builder.append("interface=");
/* 703 */       builder.append(this.serviceName);
/* 704 */       builder.append("&");
/*     */     }
/*     */     
/* 707 */     if (!StringUtils.isEmpty(this.restUrl))
/*     */     {
/* 709 */       builder.append("rest.exportUrl=");
/* 710 */       builder.append(this.restUrl);
/* 711 */       builder.append("&");
/*     */     }
/* 713 */     builder.append("type=");
/* 714 */     builder.append(this.type);
/* 715 */     builder.append("&");
/* 716 */     builder.append("serviceType=");
/* 717 */     builder.append(this.serviceType);
/* 718 */     builder.append("&");
/* 719 */     String paasHostId = StringUtils.isBlank(System.getenv("PAAS_HOST_ID")) ? Utils.getHostIp() : System.getenv("PAAS_HOST_ID");
/*     */     
/* 721 */     builder.append("appInstanceId=");
/* 722 */     builder.append(paasHostId);
/* 723 */     return builder.toString();
/*     */   }
/*     */   
/*     */   public String getDsf()
/*     */   {
/* 728 */     return this.dsf;
/*     */   }
/*     */   
/*     */   public void setDsf(String dsf)
/*     */   {
/* 733 */     this.dsf = dsf;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getPid()
/*     */   {
/* 741 */     return this.pid;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPid(String pid)
/*     */   {
/* 750 */     this.pid = pid;
/*     */   }
/*     */   
/*     */   public long getTimeout()
/*     */   {
/* 755 */     return this.timeout;
/*     */   }
/*     */   
/*     */   public void setTimeout(long timeout)
/*     */   {
/* 760 */     this.timeout = timeout;
/*     */   }
/*     */   
/*     */   public String getRouter()
/*     */   {
/* 765 */     return this.router;
/*     */   }
/*     */   
/*     */   public void setRouter(String router)
/*     */   {
/* 770 */     this.router = router;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\ServiceInstanceInner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */