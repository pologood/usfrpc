/*     */ package com.huawei.csc.usf.framework.service;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public class ClassLoaderManager
/*     */ {
/*  33 */   private static final ClassLoaderManager INSTANCE = new ClassLoaderManager();
/*     */   
/*  35 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(ClassLoaderManager.class);
/*     */   
/*     */ 
/*  38 */   private Map<String, ClassLoader> classLoaderMapping = new HashMap();
/*     */   
/*  40 */   private Object classloaderLocker = new Object();
/*     */   
/*     */   public static ClassLoaderManager getInstance()
/*     */   {
/*  44 */     return INSTANCE;
/*     */   }
/*     */   
/*     */ 
/*     */   public void addClassLoaderMapping(String serviceName, ClassLoader classLoader)
/*     */   {
/*  50 */     synchronized (this.classloaderLocker)
/*     */     {
/*  52 */       Map<String, ClassLoader> newMapping = new HashMap(this.classLoaderMapping);
/*     */       
/*     */ 
/*  55 */       if (newMapping.containsKey(serviceName))
/*     */       {
/*  57 */         return;
/*     */       }
/*     */       
/*  60 */       newMapping.put(serviceName, classLoader);
/*     */       
/*  62 */       if (DEBUGGER.isInfoEnable())
/*     */       {
/*  64 */         DEBUGGER.info("add classLoader mapping, serviceName : [" + serviceName + "], classLoader : " + classLoader);
/*     */       }
/*     */       
/*  67 */       this.classLoaderMapping = newMapping;
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeClassLoaderMapping(String serviceName)
/*     */   {
/*  73 */     synchronized (this.classloaderLocker)
/*     */     {
/*  75 */       Map<String, ClassLoader> newMapping = new HashMap(this.classLoaderMapping);
/*     */       
/*     */ 
/*  78 */       if (!newMapping.containsKey(serviceName))
/*     */       {
/*  80 */         return;
/*     */       }
/*  82 */       newMapping.remove(serviceName);
/*     */       
/*  84 */       if (DEBUGGER.isInfoEnable())
/*     */       {
/*  86 */         DEBUGGER.info("remove classLoader mapping, serviceName : [" + serviceName + "]");
/*     */       }
/*     */       
/*  89 */       this.classLoaderMapping = newMapping;
/*     */     }
/*     */   }
/*     */   
/*     */   public ClassLoader getClassLoaderByServiceName(String serviceName)
/*     */   {
/*  95 */     ClassLoader classLoader = null;
/*     */     
/*  97 */     if (this.classLoaderMapping.containsKey(serviceName))
/*     */     {
/*  99 */       classLoader = (ClassLoader)this.classLoaderMapping.get(serviceName);
/*     */     }
/*     */     
/* 102 */     return classLoader;
/*     */   }
/*     */   
/*     */   public void destroy()
/*     */   {
/* 107 */     this.classLoaderMapping.clear();
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\service\ClassLoaderManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */