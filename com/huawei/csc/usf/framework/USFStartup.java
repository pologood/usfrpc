/*     */ package com.huawei.csc.usf.framework;
/*     */ 
/*     */ import com.huawei.csc.container.api.ContextRegistry;
/*     */ import com.huawei.csc.container.api.IContextHolder;
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.exception.USFException;
/*     */ import org.springframework.context.ApplicationContext;
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
/*     */ public class USFStartup
/*     */ {
/*  35 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(USFStartup.class);
/*     */   
/*     */ 
/*  38 */   private static USFStartup MANAGER = new USFStartup();
/*     */   
/*     */ 
/*     */   private static volatile boolean initialized;
/*     */   
/*     */ 
/*     */   private ServiceEngine serviceEngine;
/*     */   
/*     */ 
/*     */   public static synchronized void init()
/*     */   {
/*  49 */     if (!initialized)
/*     */     {
/*  51 */       ServiceEngine engine = new ServiceEngine();
/*  52 */       ApplicationContext context = ContextRegistry.getContextHolder().getContext();
/*     */       
/*     */       try
/*     */       {
/*  56 */         engine.init(context);
/*  57 */         MANAGER.serviceEngine = engine;
/*  58 */         initialized = true;
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*  62 */         if (DEBUGGER.isErrorEnable())
/*     */         {
/*  64 */           DEBUGGER.error("Failed to init USF!", e);
/*     */         }
/*  66 */         throw new USFException("405180806", e);
/*     */       }
/*     */       
/*     */ 
/*     */     }
/*  71 */     else if (DEBUGGER.isWarnEnable())
/*     */     {
/*  73 */       DEBUGGER.warn("repeat init USF. ");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized void destroy()
/*     */   {
/*  83 */     if ((initialized) && (MANAGER.serviceEngine != null))
/*     */     {
/*  85 */       MANAGER.serviceEngine.destroy();
/*  86 */       ContextRegistry.getContextHolder().clearCtx();
/*  87 */       MANAGER.serviceEngine = null;
/*  88 */       initialized = false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized void destroyNow()
/*     */   {
/*  97 */     if ((initialized) && (MANAGER.serviceEngine != null))
/*     */     {
/*  99 */       MANAGER.serviceEngine.destroyNow();
/* 100 */       ContextRegistry.getContextHolder().clearCtx();
/* 101 */       MANAGER.serviceEngine = null;
/* 102 */       initialized = false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\USFStartup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */