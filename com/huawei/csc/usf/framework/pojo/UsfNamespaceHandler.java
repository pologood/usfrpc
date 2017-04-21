/*     */ package com.huawei.csc.usf.framework.pojo;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.kernel.api.spring.core.io.Resource;
/*     */ import com.huawei.csc.kernel.commons.util.ClassUtils;
/*     */ import com.huawei.csc.kernel.commons.util.IOUtils;
/*     */ import com.huawei.csc.kernel.commons.util.ResourceResolverUtil;
/*     */ import com.huawei.csc.usf.framework.util.CopyOnWriteHashMap;
/*     */ import java.io.InputStream;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Properties;
/*     */ import org.springframework.beans.factory.xml.BeanDefinitionParser;
/*     */ import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UsfNamespaceHandler
/*     */   extends NamespaceHandlerSupport
/*     */ {
/*  22 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(UsfNamespaceHandler.class);
/*     */   
/*     */ 
/*  25 */   private Map<String, BeanDefinitionParser> registedparsers = new CopyOnWriteHashMap();
/*     */   
/*     */ 
/*     */   public void init()
/*     */   {
/*  30 */     Properties properties = loadProperties();
/*  31 */     if (null == properties)
/*     */     {
/*  33 */       throw new IllegalArgumentException("Properties is null.");
/*     */     }
/*  35 */     doload(properties);
/*     */   }
/*     */   
/*     */   private void doload(Properties properties)
/*     */   {
/*  40 */     for (Map.Entry<Object, Object> entry : properties.entrySet())
/*     */     {
/*  42 */       String className = entry.getValue().toString();
/*  43 */       Class<?> clazz = ClassUtils.getClass(className);
/*     */       try
/*     */       {
/*  46 */         Object object = clazz.newInstance();
/*  47 */         if (this.registedparsers.containsKey(entry.getKey().toString()))
/*     */         {
/*  49 */           if (DEBUGGER.isErrorEnable())
/*     */           {
/*  51 */             DEBUGGER.error("duplicated label name :" + entry.getKey().toString() + ", inogred it.");
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/*  56 */           registerBeanDefinitionParser(entry.getKey().toString(), (BeanDefinitionParser)object);
/*     */           
/*  58 */           this.registedparsers.put(entry.getKey().toString(), (BeanDefinitionParser)object);
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       catch (InstantiationException e)
/*     */       {
/*     */ 
/*  66 */         if (DEBUGGER.isInfoEnable())
/*     */         {
/*  68 */           DEBUGGER.info("Failed to create BeanDefinitionParser instance of " + className);
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       catch (IllegalAccessException e)
/*     */       {
/*     */ 
/*  77 */         if (DEBUGGER.isInfoEnable())
/*     */         {
/*  79 */           DEBUGGER.info("Failed to create BeanDefinitionParser instance of " + className);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected Properties loadProperties()
/*     */   {
/*  88 */     Resource[] resArr = ResourceResolverUtil.getFullResources("classpath*:META-INF/spring/usf.namespace.properties");
/*     */     
/*  90 */     if (null == resArr)
/*     */     {
/*  92 */       DEBUGGER.error("can not find the usf.namespace.properties files!");
/*  93 */       return null;
/*     */     }
/*     */     
/*  96 */     Properties properties = new Properties();
/*  97 */     for (Resource url : resArr)
/*     */     {
/*  99 */       if (url.exists())
/*     */       {
/*     */ 
/*     */ 
/* 103 */         InputStream is = null;
/*     */         try
/*     */         {
/* 106 */           is = url.getInputStream();
/* 107 */           properties.load(is);
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 111 */           if (DEBUGGER.isErrorEnable())
/*     */           {
/* 113 */             DEBUGGER.error("load usf.namespace.properties error.", e);
/*     */           }
/*     */         }
/*     */         finally
/*     */         {
/* 118 */           IOUtils.closeQuietly(is);
/*     */         }
/*     */       } }
/* 121 */     return properties;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\pojo\UsfNamespaceHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */