/*     */ package com.huawei.csc.usf.framework.flowcontrol;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.Connector;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.ExceptionUtils;
/*     */ import com.huawei.csc.usf.framework.ExceptionUtilsHolder;
/*     */ import com.huawei.csc.usf.framework.ExecutesRepository;
/*     */ import com.huawei.csc.usf.framework.ExecutesSemaphore;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.IMessageFactory;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.MethodDefinition;
/*     */ import com.huawei.csc.usf.framework.ServiceDefinition;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.event.FrameworkEvent;
/*     */ import com.huawei.csc.usf.framework.event.ServiceFrameworkEvent;
/*     */ import com.huawei.csc.usf.framework.event.ServiceFrameworkEventPublisher;
/*     */ import com.huawei.csc.usf.framework.pojo.PojoConnector;
/*     */ import com.huawei.csc.usf.framework.pojo.PojoServerInner;
/*     */ import com.huawei.csc.usf.framework.sr.DsfZookeeperDataManager;
/*     */ import com.huawei.csc.usf.framework.sr.SRAgentFactory;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceRegistryAgent;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
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
/*     */ public class FlowControlManager
/*     */ {
/*  47 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(FlowControlManager.class);
/*     */   
/*     */ 
/*  50 */   private static final ConcurrentHashMap<String, AtomicInteger> tpsMap = new ConcurrentHashMap();
/*     */   
/*  52 */   private static FlowControlManager instance = new FlowControlManager();
/*     */   
/*     */ 
/*     */   private ScheduledExecutorService executor;
/*     */   
/*  57 */   private ExecutesRepository executesRepository = new ExecutesRepository();
/*     */   
/*  59 */   private Object lock = new Object();
/*     */   
/*     */   private static final int DEFAULT_TPS = 0;
/*     */   
/*     */   private static final String TPS_THRESHOLD = "tpsThreshold";
/*     */   
/*     */   private static final String EXECUTES = "executes";
/*     */   
/*     */   public static FlowControlManager getInstance()
/*     */   {
/*  69 */     return instance;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void init()
/*     */   {
/*  79 */     TimerTask tps = new TimerTask();
/*  80 */     this.executor = Executors.newSingleThreadScheduledExecutor();
/*  81 */     this.executor.scheduleAtFixedRate(tps, 0L, 1000L, TimeUnit.MILLISECONDS);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void resetTps()
/*     */   {
/*  91 */     if ((null != tpsMap) && (tpsMap.size() != 0))
/*     */     {
/*  93 */       for (Map.Entry<String, AtomicInteger> tps : tpsMap.entrySet())
/*     */       {
/*     */ 
/*  96 */         ((AtomicInteger)tps.getValue()).getAndSet(0);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void releaseConcurrent(Context context)
/*     */   {
/* 110 */     String service = context.getReceivedMessage().getHeaders().getServiceName();
/*     */     
/* 112 */     String operation = context.getReceivedMessage().getHeaders().getOperation();
/*     */     
/* 114 */     ExecutesSemaphore executesSemaphore = this.executesRepository.getExecutesSemaphore(makeKey(service, operation));
/*     */     
/* 116 */     if (null != executesSemaphore)
/*     */     {
/* 118 */       executesSemaphore.release();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isTpsFlowcontrol(Context context)
/*     */     throws Exception
/*     */   {
/* 133 */     boolean isTpsFlowcontrol = false;
/*     */     
/* 135 */     String service = context.getReceivedMessage().getHeaders().getServiceName();
/*     */     
/* 137 */     String operation = context.getReceivedMessage().getHeaders().getOperation();
/*     */     
/*     */ 
/*     */ 
/* 141 */     int threshold = getThreshold(service, operation, "tpsThreshold", context);
/*     */     
/*     */ 
/* 144 */     if (0 == threshold)
/*     */     {
/* 146 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 150 */     if (checkTpsReject(service, operation, context, threshold))
/*     */     {
/* 152 */       isTpsFlowcontrol = true;
/*     */     }
/*     */     
/* 155 */     return isTpsFlowcontrol;
/*     */   }
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
/*     */   public ConcurrentFlowControlResult isConcurrentFlowcontrol(Context context)
/*     */   {
/* 169 */     String service = context.getReceivedMessage().getHeaders().getServiceName();
/*     */     
/* 171 */     String operation = context.getReceivedMessage().getHeaders().getOperation();
/*     */     
/*     */ 
/*     */ 
/* 175 */     int executes = getThreshold(service, operation, "executes", context);
/*     */     
/*     */ 
/* 178 */     if (0 == executes)
/*     */     {
/* 180 */       return ConcurrentFlowControlResult.NOT_EXCECUTE;
/*     */     }
/*     */     
/* 183 */     if (checkConcurrentReject(service, operation, context, executes))
/*     */     {
/* 185 */       return ConcurrentFlowControlResult.EXCECUTE_RESULTE_REJECT;
/*     */     }
/*     */     
/* 188 */     return ConcurrentFlowControlResult.EXCECUTE_RESULTE_NOT_REJECT;
/*     */   }
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
/*     */   private boolean checkConcurrentReject(String service, String operation, Context context, int executes)
/*     */   {
/* 209 */     boolean reject = false;
/* 210 */     String key = makeKey(service, operation);
/* 211 */     if (!this.executesRepository.containsKey(key))
/*     */     {
/* 213 */       synchronized (this.lock)
/*     */       {
/* 215 */         if (!this.executesRepository.containsKey(key))
/*     */         {
/* 217 */           this.executesRepository.add(key);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 222 */     ExecutesSemaphore executesSemaphore = this.executesRepository.getExecutesSemaphore(key);
/*     */     
/*     */ 
/*     */ 
/* 226 */     if (!executesSemaphore.tryAcquire(executes))
/*     */     {
/* 228 */       rejectReply(context, service, operation, executes, executesSemaphore.getConcurrences(), "executes");
/*     */       
/* 230 */       reject = true;
/*     */     }
/* 232 */     return reject;
/*     */   }
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
/*     */   private boolean checkTpsReject(String service, String operation, Context context, int threshold)
/*     */     throws Exception
/*     */   {
/* 253 */     boolean reject = false;
/* 254 */     String key = makeKey(service, operation);
/*     */     
/* 256 */     AtomicInteger count = (AtomicInteger)tpsMap.get(key);
/* 257 */     if (null == count)
/*     */     {
/* 259 */       tpsMap.putIfAbsent(key, new AtomicInteger(0));
/* 260 */       count = (AtomicInteger)tpsMap.get(key); }
/*     */     int currentTps;
/*     */     int currentTps;
/* 263 */     if (null == count)
/*     */     {
/* 265 */       currentTps = 1;
/*     */     }
/*     */     else
/*     */     {
/* 269 */       currentTps = count.incrementAndGet();
/*     */     }
/* 271 */     if (currentTps > threshold)
/*     */     {
/* 273 */       reject = true;
/*     */       
/* 275 */       rejectReply(context, service, operation, threshold, currentTps, "tpsThreshold");
/*     */     }
/*     */     
/*     */ 
/* 279 */     return reject;
/*     */   }
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
/*     */   private int getThreshold(String service, String operation, String thresholdType, Context context)
/*     */   {
/* 299 */     int thresholdDefault = 0;
/*     */     
/*     */ 
/*     */ 
/* 303 */     SystemConfig systemConfig = context.getSrcConnector().getServiceEngine().getSystemConfig();
/*     */     
/* 305 */     String adress = getAddress(context.getServiceType(), context.getSrcConnector());
/*     */     
/* 307 */     String dsfApplication = systemConfig.getDsfApplication();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 312 */     Integer finalThreshold = (Integer)context.getSrcConnector().getServiceEngine().getSrAgentFactory().getSRAgent(context.getRegistry()).getZookeeperDataManager().getConfigFromConfiguration(service, operation, adress, dsfApplication, thresholdType, "provider");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 320 */     if (null != finalThreshold)
/*     */     {
/* 322 */       DEBUGGER.info("Get executes from zookeeper configurations, current executes is: " + finalThreshold.toString());
/*     */       
/* 324 */       return finalThreshold.intValue();
/*     */     }
/*     */     
/*     */ 
/* 328 */     PojoConnector pConnector = (PojoConnector)context.getDestConnector();
/*     */     
/* 330 */     PojoServerInner pojoServer = pConnector.getPojoServer(service);
/* 331 */     ServiceDefinition serviceDefinition = pojoServer.getServiceDefinition();
/*     */     
/*     */ 
/* 334 */     MethodDefinition methodDefinition = serviceDefinition.getMethodDefinition(operation);
/*     */     
/*     */ 
/* 337 */     String serviceThreshold = getServiceThreshold(serviceDefinition, methodDefinition, thresholdType, systemConfig);
/*     */     
/*     */ 
/* 340 */     if (DEBUGGER.isDebugEnable())
/*     */     {
/* 342 */       DEBUGGER.debug("Get executes from local properties, current executes is: " + serviceThreshold);
/*     */     }
/*     */     
/* 345 */     if (null == serviceThreshold)
/*     */     {
/* 347 */       return thresholdDefault;
/*     */     }
/* 349 */     return Integer.parseInt(serviceThreshold);
/*     */   }
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
/*     */   private String getServiceThreshold(ServiceDefinition serviceDefinition, MethodDefinition methodDefinition, String thresholdType, SystemConfig systemConfig)
/*     */   {
/* 375 */     String serviceThreshold = null;
/* 376 */     if ("tpsThreshold".equals(thresholdType))
/*     */     {
/* 378 */       if (null != methodDefinition)
/*     */       {
/* 380 */         serviceThreshold = methodDefinition.getThreshold() == null ? serviceDefinition.getThreshold() : methodDefinition.getThreshold();
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 385 */         serviceThreshold = serviceDefinition.getThreshold();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 391 */       if (null == serviceThreshold)
/*     */       {
/* 393 */         serviceThreshold = systemConfig.getDsfTpsThreshold();
/*     */       }
/*     */       
/*     */ 
/*     */     }
/* 398 */     else if ("executes".equals(thresholdType))
/*     */     {
/*     */ 
/* 401 */       if (null != methodDefinition)
/*     */       {
/* 403 */         serviceThreshold = methodDefinition.getExecutes() == null ? String.valueOf(serviceDefinition.getExecutes()) : methodDefinition.getExecutes();
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 409 */         serviceThreshold = String.valueOf(serviceDefinition.getExecutes());
/*     */       }
/*     */     }
/*     */     
/* 413 */     return serviceThreshold;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 422 */     if (null != this.executor)
/*     */     {
/* 424 */       this.executor.shutdown();
/*     */     }
/*     */   }
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
/*     */   private void rejectReply(Context context, String service, String operation, int threshold, int current, String thresholdType)
/*     */   {
/* 441 */     IMessage rejectMessage = context.getDestConnector().getServiceEngine().getMessageFactory(context.getServiceType()).createReplyMessage(context.getReceivedMessage());
/*     */     
/*     */ 
/*     */ 
/* 445 */     Exception rejectException = null;
/*     */     
/* 447 */     if ("executes".equals(thresholdType))
/*     */     {
/* 449 */       rejectException = ExceptionUtilsHolder.getExceptionUtils(context.getServiceType()).accessOverConcurrentflow(service, operation, threshold, current);
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 455 */       rejectException = ExceptionUtilsHolder.getExceptionUtils(context.getServiceType()).accessOverTpsflow(service, operation, threshold, current);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 460 */     rejectMessage.setException(rejectException);
/* 461 */     context.setReplyMessage(rejectMessage);
/*     */     
/*     */ 
/* 464 */     if (DEBUGGER.isErrorEnable())
/*     */     {
/* 466 */       DEBUGGER.error(String.format("The access is rejected by flow control,cause by overflow the threshold. service=[%s], operation=[%s], threshold =[%s], current =[%s].", new Object[] { service, operation, Integer.valueOf(threshold), Integer.valueOf(current) }));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 471 */     ServiceFrameworkEvent event = FrameworkEvent.creatEvent(context.getReceivedMessage(), context.getSrcConnector().getConnectorType(), context, Boolean.valueOf(false), Boolean.valueOf(!context.getSrcConnector().isAsync()), context.getReplyMessage(), null, rejectException);
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 477 */       ServiceFrameworkEventPublisher.publish("usf.framework.flowControl", event);
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 482 */       if (DEBUGGER.isErrorEnable())
/*     */       {
/* 484 */         DEBUGGER.error("Excpetion occured while processing flowControl event ", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private String getAddress(ServiceType serviceType, Connector connector)
/*     */   {
/* 491 */     String rpcAddr = connector.getServiceEngine().getSystemConfig().getRPCAddress(serviceType);
/*     */     
/* 493 */     return rpcAddr;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String makeKey(String service, String method)
/*     */   {
/* 505 */     return service + "|" + method + "|" + "C$C";
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\flowcontrol\FlowControlManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */