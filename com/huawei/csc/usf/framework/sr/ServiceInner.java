/*     */ package com.huawei.csc.usf.framework.sr;
/*     */ 
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.RuntimeMXBean;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServiceInner
/*     */ {
/*     */   private String name;
/*  19 */   private List<ServiceInstanceInner> instances = new ArrayList();
/*     */   
/*  21 */   private List<ConsumerInstanceInner> consumerInstances = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  26 */   private Map<String, Object> attrs = new HashMap();
/*     */   
/*  28 */   private List<String> methods = new ArrayList();
/*     */   
/*  30 */   private List<String> methodExcetes = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  35 */   private String type = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  40 */   private boolean isImporter = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  45 */   private String version = "0.0.0";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  50 */   private String dsf = "2.0";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  55 */   private String group = "default";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  60 */   private boolean immutable = false;
/*     */   
/*     */ 
/*     */   private String sdl;
/*     */   
/*     */ 
/*     */   private String serviceType;
/*     */   
/*     */   private int executes;
/*     */   
/*     */ 
/*     */   public String getVersion()
/*     */   {
/*  73 */     return this.version;
/*     */   }
/*     */   
/*     */   public void setVersion(String version)
/*     */   {
/*  78 */     this.version = version;
/*     */   }
/*     */   
/*     */   public String getDsf()
/*     */   {
/*  83 */     return this.dsf;
/*     */   }
/*     */   
/*     */   public void setDsf(String dsf)
/*     */   {
/*  88 */     this.dsf = dsf;
/*     */   }
/*     */   
/*     */   public String getGroup()
/*     */   {
/*  93 */     return this.group;
/*     */   }
/*     */   
/*     */   public void setGroup(String group)
/*     */   {
/*  98 */     this.group = group;
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
/* 109 */     return this.executes;
/*     */   }
/*     */   
/*     */   public void setExecutes(int executes)
/*     */   {
/* 114 */     this.executes = executes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 124 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/* 135 */     this.name = name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<ServiceInstanceInner> getInstances()
/*     */   {
/* 145 */     return this.instances;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setInstances(List<ServiceInstanceInner> instances)
/*     */   {
/* 156 */     this.instances = instances;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addInstance(ServiceInstanceInner instance)
/*     */   {
/* 167 */     this.instances.add(instance);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addConsumerInstance(ConsumerInstanceInner instance)
/*     */   {
/* 178 */     this.consumerInstances.add(instance);
/*     */   }
/*     */   
/*     */   public List<ConsumerInstanceInner> getConsumerInstances()
/*     */   {
/* 183 */     return this.consumerInstances;
/*     */   }
/*     */   
/*     */   public String getType()
/*     */   {
/* 188 */     return this.type;
/*     */   }
/*     */   
/*     */   public void setType(String type)
/*     */   {
/* 193 */     this.type = type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isImporter()
/*     */   {
/* 203 */     return this.isImporter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setImporter(boolean isImporter)
/*     */   {
/* 214 */     this.isImporter = isImporter;
/*     */   }
/*     */   
/*     */   public Map<String, Object> getAttrs()
/*     */   {
/* 219 */     return this.attrs;
/*     */   }
/*     */   
/*     */   public void setAttrs(Map<String, Object> attrs)
/*     */   {
/* 224 */     this.attrs = attrs;
/*     */   }
/*     */   
/*     */   public void putAttr(String key, Object value)
/*     */   {
/* 229 */     this.attrs.put(key, value);
/*     */   }
/*     */   
/*     */   public Object getAttr(String key)
/*     */   {
/* 234 */     return this.attrs.get(key);
/*     */   }
/*     */   
/*     */   public boolean isImmutable()
/*     */   {
/* 239 */     return this.immutable;
/*     */   }
/*     */   
/*     */   public void setImmutable(boolean immutable)
/*     */   {
/* 244 */     this.immutable = immutable;
/*     */   }
/*     */   
/*     */   public List<String> getMethods()
/*     */   {
/* 249 */     return this.methods;
/*     */   }
/*     */   
/*     */   public void setMethods(List<String> methods)
/*     */   {
/* 254 */     this.methods = methods;
/*     */   }
/*     */   
/*     */   public List<String> getMethodExecutes()
/*     */   {
/* 259 */     return this.methodExcetes;
/*     */   }
/*     */   
/*     */   public void addMethodExecutes(String methodNameExcutes)
/*     */   {
/* 264 */     this.methodExcetes.add(methodNameExcutes);
/*     */   }
/*     */   
/*     */   public String getSdl()
/*     */   {
/* 269 */     return this.sdl;
/*     */   }
/*     */   
/*     */   public void setSdl(String sdl)
/*     */   {
/* 274 */     this.sdl = sdl;
/*     */   }
/*     */   
/*     */   public String getServiceType()
/*     */   {
/* 279 */     return this.serviceType;
/*     */   }
/*     */   
/*     */   public void setServiceType(String serviceType)
/*     */   {
/* 284 */     this.serviceType = serviceType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ServiceInstanceInner createInstance(String group, String serviceName, String serviceType, String address, String dsfApplication)
/*     */   {
/* 291 */     return createInstance(group, serviceName, serviceType, address, 10, dsfApplication);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ConsumerInstanceInner createConsumerInstance(String instanceName, String type, String serviceType, String addr)
/*     */   {
/* 298 */     ConsumerInstanceInner consumerInstance = new ConsumerInstanceInner(instanceName, type, serviceType, addr);
/*     */     
/* 300 */     return consumerInstance;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ServiceInstanceInner createInstance(String group, String serviceName, String serviceType, String address, int weight, String dsfApplication)
/*     */   {
/* 307 */     return createInstance(group, serviceName, serviceType, address, weight, dsfApplication, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ServiceInstanceInner createInstance(String group, String serviceName, String serviceType, String address, int weight, String dsfApplication, String servicePrefixName)
/*     */   {
/* 315 */     ServiceInstanceInner instance = new ServiceInstanceInner();
/*     */     
/* 317 */     instance.setGroup(group);
/* 318 */     instance.setVersion(this.version);
/* 319 */     instance.setInstanceName(serviceName);
/* 320 */     instance.setServiceType(serviceType);
/* 321 */     instance.setAddress(address);
/* 322 */     instance.setServiceName(getName());
/* 323 */     instance.setType(getType());
/* 324 */     instance.setWeight(weight);
/* 325 */     instance.setExecutes(this.executes);
/* 326 */     instance.setAttr("methods", getMethods());
/* 327 */     instance.setMethods(this.methods);
/* 328 */     instance.setMethodExceutes(this.methodExcetes);
/* 329 */     instance.setApplication(dsfApplication);
/* 330 */     instance.setServiceGroup(servicePrefixName);
/*     */     
/* 332 */     String pidName = ManagementFactory.getRuntimeMXBean().getName();
/* 333 */     String pid = pidName.split("@")[0];
/* 334 */     instance.setPid(pid);
/* 335 */     return instance;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 341 */     return "Service [name=" + this.name + ", type=" + this.type + ", version=" + this.version + ", group=" + this.group + ", serviceType=" + this.serviceType + "]";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 349 */     int prime = 31;
/* 350 */     int result = 1;
/* 351 */     result = 31 * result + (this.group == null ? 0 : this.group.hashCode());
/* 352 */     result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
/* 353 */     result = 31 * result + (this.serviceType == null ? 0 : this.serviceType.hashCode());
/*     */     
/* 355 */     result = 31 * result + (this.type == null ? 0 : this.type.hashCode());
/* 356 */     result = 31 * result + (this.version == null ? 0 : this.version.hashCode());
/* 357 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 363 */     if (this == obj)
/* 364 */       return true;
/* 365 */     if (obj == null)
/* 366 */       return false;
/* 367 */     if (getClass() != obj.getClass())
/* 368 */       return false;
/* 369 */     ServiceInner other = (ServiceInner)obj;
/* 370 */     if (this.group == null)
/*     */     {
/* 372 */       if (other.group != null) {
/* 373 */         return false;
/*     */       }
/* 375 */     } else if (!this.group.equals(other.group))
/* 376 */       return false;
/* 377 */     if (this.name == null)
/*     */     {
/* 379 */       if (other.name != null) {
/* 380 */         return false;
/*     */       }
/* 382 */     } else if (!this.name.equals(other.name))
/* 383 */       return false;
/* 384 */     if (this.serviceType == null)
/*     */     {
/* 386 */       if (other.serviceType != null) {
/* 387 */         return false;
/*     */       }
/* 389 */     } else if (!this.serviceType.equals(other.serviceType))
/* 390 */       return false;
/* 391 */     if (this.type == null)
/*     */     {
/* 393 */       if (other.type != null) {
/* 394 */         return false;
/*     */       }
/* 396 */     } else if (!this.type.equals(other.type))
/* 397 */       return false;
/* 398 */     if (this.version == null)
/*     */     {
/* 400 */       if (other.version != null) {
/* 401 */         return false;
/*     */       }
/* 403 */     } else if (!this.version.equals(other.version))
/* 404 */       return false;
/* 405 */     return true;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\ServiceInner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */