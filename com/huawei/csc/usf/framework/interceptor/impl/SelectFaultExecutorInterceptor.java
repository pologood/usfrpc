/*     */ package com.huawei.csc.usf.framework.interceptor.impl;
/*     */ 
/*     */ import com.huawei.csc.container.api.ContextRegistry;
/*     */ import com.huawei.csc.container.api.IContextHolder;
/*     */ import com.huawei.csc.usf.framework.Connector;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.cluster.AbstractResendHandler;
/*     */ import com.huawei.csc.usf.framework.cluster.FailFastCluster;
/*     */ import com.huawei.csc.usf.framework.cluster.FailOverCluster;
/*     */ import com.huawei.csc.usf.framework.cluster.FailResendCluster;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public class SelectFaultExecutorInterceptor
/*     */ {
/*  34 */   private AbstractResendHandler reSendHandler = null;
/*     */   
/*  36 */   protected Map<String, AbstractResendHandler> faultProcessMaping = new ConcurrentHashMap();
/*     */   
/*     */   public SelectFaultExecutorInterceptor()
/*     */   {
/*  40 */     init();
/*     */   }
/*     */   
/*     */   public void reSend(Context context) throws Exception
/*     */   {
/*  45 */     if ((!ServiceType.USF.equals(context.getServiceType())) && (!ServiceType.DSF.equals(context.getServiceType())))
/*     */     {
/*     */ 
/*  48 */       return;
/*     */     }
/*     */     
/*  51 */     String failPolicy = modifyCluster(context);
/*     */     
/*  53 */     if (this.faultProcessMaping.containsKey(failPolicy))
/*     */     {
/*  55 */       this.reSendHandler = ((AbstractResendHandler)this.faultProcessMaping.get(failPolicy));
/*     */     }
/*     */     else
/*     */     {
/*  59 */       this.reSendHandler = ((AbstractResendHandler)this.faultProcessMaping.get("failFast"));
/*     */     }
/*     */     
/*  62 */     if (this.reSendHandler != null)
/*     */     {
/*  64 */       Throwable throwable = context.getException();
/*  65 */       if ((throwable instanceof Exception))
/*     */       {
/*  67 */         this.reSendHandler.processFault((Exception)throwable, context);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void init()
/*     */   {
/*  74 */     initFailPolicyMap();
/*     */   }
/*     */   
/*     */   private void initFailPolicyMap()
/*     */   {
/*  79 */     Map<String, AbstractResendHandler> failPolicyMap = ContextRegistry.getContextHolder().getBeansOfType(AbstractResendHandler.class);
/*     */     
/*     */ 
/*  82 */     if (!failPolicyMap.isEmpty())
/*     */     {
/*  84 */       this.faultProcessMaping.putAll(failPolicyMap);
/*     */     }
/*  86 */     this.faultProcessMaping.put("failFast", new FailFastCluster());
/*  87 */     this.faultProcessMaping.put("failOver", new FailOverCluster());
/*  88 */     this.faultProcessMaping.put("failResend", new FailResendCluster());
/*     */   }
/*     */   
/*     */ 
/*     */   private String modifyCluster(Context context)
/*     */   {
/*  94 */     String failPolicy = context.getFailPolicy();
/*  95 */     if (StringUtils.isEmpty(failPolicy))
/*     */     {
/*  97 */       failPolicy = context.getSrcConnector().getServiceEngine().getSystemConfig().getServerFailPolicy();
/*     */     }
/*     */     
/* 100 */     if (!StringUtils.isEmpty(failPolicy))
/*     */     {
/* 102 */       if (ServiceType.DSF.equals(context.getServiceType()))
/*     */       {
/* 104 */         if (failPolicy.equalsIgnoreCase("failOver"))
/*     */         {
/* 106 */           failPolicy = "dsfFailOver";
/*     */         }
/* 108 */         if (failPolicy.equalsIgnoreCase("failFast"))
/*     */         {
/* 110 */           failPolicy = "dsfFailFast";
/*     */         }
/* 112 */         if (failPolicy.equalsIgnoreCase("failResend"))
/*     */         {
/* 114 */           failPolicy = "dsfFailResend";
/*     */         }
/*     */       }
/* 117 */       return failPolicy;
/*     */     }
/*     */     
/*     */ 
/* 121 */     return "failFast";
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\interceptor\impl\SelectFaultExecutorInterceptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */