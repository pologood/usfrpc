/*     */ package com.huawei.csc.usf.framework;
/*     */ 
/*     */ import com.huawei.csc.remoting.common.util.CastUtil;
/*     */ import com.huawei.csc.usf.framework.circuitbreaker.DSFFaultToleranceProperties;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*     */ import com.huawei.csc.usf.framework.statistic.ProcessDelayTracker;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Context
/*     */ {
/*     */   public static final String SERVICE_TYPE = "service_type";
/*  42 */   private IMessage receivedMessage = null;
/*     */   
/*  44 */   private IMessage replyMessage = null;
/*     */   
/*  46 */   private Throwable exception = null;
/*     */   
/*  48 */   protected Object[] nativeInputs = null;
/*     */   
/*  50 */   private Map<String, Object> attachments = new HashMap();
/*     */   
/*  52 */   private Connector srcConnector = null;
/*     */   
/*  54 */   private Connector destConnector = null;
/*     */   
/*  56 */   private Object replyObject = null;
/*     */   
/*  58 */   private long msTimeout = 0L;
/*     */   
/*  60 */   private long startTime = 0L;
/*     */   
/*  62 */   private boolean isServer = false;
/*     */   
/*  64 */   private boolean local = false;
/*     */   
/*     */ 
/*  67 */   private boolean isBroken = false;
/*     */   
/*     */ 
/*  70 */   private boolean isFault = false;
/*     */   
/*     */ 
/*  73 */   private boolean repeatFault = false;
/*     */   
/*     */ 
/*  76 */   private boolean isTimeoutException = false;
/*     */   
/*     */ 
/*  79 */   private boolean isSysnFirstNode = false;
/*     */   
/*     */ 
/*  82 */   private boolean ebusException = false;
/*     */   
/*     */ 
/*     */   private DSFFaultToleranceProperties circuitBreakProperties;
/*     */   
/*  87 */   private String restProtocolDirection = "in";
/*     */   
/*     */   public boolean isFault()
/*     */   {
/*  91 */     return this.isFault;
/*     */   }
/*     */   
/*     */   public void setFault(boolean isFault)
/*     */   {
/*  96 */     this.isFault = isFault;
/*     */   }
/*     */   
/*     */   public boolean isRepeatFault()
/*     */   {
/* 101 */     return this.repeatFault;
/*     */   }
/*     */   
/*     */   public void setRepeatFault(boolean repeatFault)
/*     */   {
/* 106 */     this.repeatFault = repeatFault;
/*     */   }
/*     */   
/* 109 */   private String registry = "dsf_default";
/*     */   
/*     */   public String getRegistry()
/*     */   {
/* 113 */     return this.registry;
/*     */   }
/*     */   
/*     */   public void setRegistry(String registry)
/*     */   {
/* 118 */     this.registry = registry;
/*     */   }
/*     */   
/*     */   public boolean isBroken()
/*     */   {
/* 123 */     return this.isBroken;
/*     */   }
/*     */   
/*     */   public void setBroken(boolean isBroken)
/*     */   {
/* 128 */     this.isBroken = isBroken;
/*     */   }
/*     */   
/*     */   public boolean isLocal()
/*     */   {
/* 133 */     return this.local;
/*     */   }
/*     */   
/*     */   public void setLocal(boolean local)
/*     */   {
/* 138 */     this.local = local;
/*     */   }
/*     */   
/*     */   public boolean isServer()
/*     */   {
/* 143 */     return this.isServer;
/*     */   }
/*     */   
/*     */   public void setServer(boolean isServer)
/*     */   {
/* 148 */     this.isServer = isServer;
/*     */   }
/*     */   
/*     */   public long getStartTime()
/*     */   {
/* 153 */     return this.startTime;
/*     */   }
/*     */   
/*     */   public void setStartTime(long startTime)
/*     */   {
/* 158 */     this.startTime = startTime;
/*     */   }
/*     */   
/* 161 */   private int timesBeenSent = 0;
/*     */   
/* 163 */   private String failPolicy = null;
/*     */   
/* 165 */   private boolean isReSend = false;
/*     */   
/* 167 */   private boolean isFlowControlReject = false;
/*     */   
/* 169 */   private String routerType = null;
/*     */   
/*     */   private ReplyMessageListener replyMessageListener;
/*     */   
/* 173 */   private ProcessDelayTracker processDelayTracker = ProcessDelayTracker.createTracker("main");
/*     */   
/*     */ 
/*     */ 
/* 177 */   private ServiceType serviceType = ServiceType.EBUS;
/*     */   
/*     */   private ServiceType destServiceType;
/*     */   
/*     */   public ServiceType getDestServiceType()
/*     */   {
/* 183 */     return this.destServiceType;
/*     */   }
/*     */   
/*     */   public void setDestServiceType(ServiceType destServiceType)
/*     */   {
/* 188 */     this.destServiceType = destServiceType;
/*     */   }
/*     */   
/*     */   public Context(Object... nativeInputs)
/*     */   {
/* 193 */     this.nativeInputs = nativeInputs;
/*     */     
/* 195 */     ContextListenerManager.onContextCreated(this);
/*     */   }
/*     */   
/*     */   public Object[] getNativeInputs()
/*     */   {
/* 200 */     return this.nativeInputs;
/*     */   }
/*     */   
/*     */   public Map<String, Object> getAttachments()
/*     */   {
/* 205 */     return this.attachments;
/*     */   }
/*     */   
/*     */   public void addAttachment(String name, Object value)
/*     */   {
/* 210 */     this.attachments.put(name, value);
/*     */   }
/*     */   
/*     */   public Object getAttachment(String name)
/*     */   {
/* 215 */     return this.attachments.get(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IMessage getReceivedMessage()
/*     */   {
/* 225 */     return this.receivedMessage;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setReceivedMessage(IMessage receivedMessage)
/*     */   {
/* 236 */     this.receivedMessage = receivedMessage;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IMessage getReplyMessage()
/*     */   {
/* 246 */     return this.replyMessage;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setReplyMessage(IMessage replyMessage)
/*     */   {
/* 257 */     this.replyMessage = replyMessage;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Throwable getException()
/*     */   {
/* 267 */     return this.exception;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setException(Throwable exception)
/*     */   {
/* 278 */     this.exception = exception;
/* 279 */     if ((null == this.processDelayTracker) || (null == exception))
/*     */     {
/* 281 */       return;
/*     */     }
/*     */     
/* 284 */     this.processDelayTracker.setErrorMessage(exception.getMessage());
/*     */   }
/*     */   
/*     */   public Connector getSrcConnector()
/*     */   {
/* 289 */     return this.srcConnector;
/*     */   }
/*     */   
/*     */   public void setSrcConnector(Connector srcConnector)
/*     */   {
/* 294 */     this.srcConnector = srcConnector;
/*     */   }
/*     */   
/*     */   public Connector getDestConnector()
/*     */   {
/* 299 */     return this.destConnector;
/*     */   }
/*     */   
/*     */   public void setDestConnector(Connector destConnector)
/*     */   {
/* 304 */     this.destConnector = destConnector;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public <T> T getReplyObject()
/*     */   {
/* 314 */     return (T)CastUtil.cast(this.replyObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setReplyObject(Object replyObject)
/*     */   {
/* 325 */     this.replyObject = replyObject;
/*     */   }
/*     */   
/*     */   public void resume(IMessage reply) throws Exception
/*     */   {
/* 330 */     setReplyMessage(reply);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getMsTimeout()
/*     */   {
/* 337 */     return this.msTimeout;
/*     */   }
/*     */   
/*     */   public void setMsTimeout(long msTimeout)
/*     */   {
/* 342 */     this.msTimeout = msTimeout;
/*     */   }
/*     */   
/*     */   public String getRouterType()
/*     */   {
/* 347 */     return this.routerType;
/*     */   }
/*     */   
/*     */   public void setRouterType(String routerType)
/*     */   {
/* 352 */     this.routerType = routerType;
/*     */   }
/*     */   
/*     */   public ReplyMessageListener getReplyMessageListener()
/*     */   {
/* 357 */     return this.replyMessageListener;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setReplyMessageListener(ReplyMessageListener replyMessageListener)
/*     */   {
/* 363 */     this.replyMessageListener = replyMessageListener;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ProcessDelayTracker getProcessDelayTracker()
/*     */   {
/* 373 */     return this.processDelayTracker;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setProcessDelayTracker(ProcessDelayTracker processDelayTracker)
/*     */   {
/* 384 */     this.processDelayTracker = processDelayTracker;
/*     */   }
/*     */   
/*     */   public void done()
/*     */   {
/* 389 */     if (null != this.processDelayTracker)
/*     */     {
/* 391 */       ProcessDelayTracker.done(this.processDelayTracker);
/* 392 */       ProcessDelayTracker.log(this.processDelayTracker);
/*     */     }
/*     */     
/* 395 */     ContextListenerManager.onContextDone(this);
/*     */   }
/*     */   
/*     */   public int getTimesBeenSent()
/*     */   {
/* 400 */     return this.timesBeenSent;
/*     */   }
/*     */   
/*     */   public void sendTimeIncrement()
/*     */   {
/* 405 */     this.timesBeenSent += 1;
/*     */   }
/*     */   
/*     */   public void setTimesBeenSent(int times)
/*     */   {
/* 410 */     this.timesBeenSent = times;
/*     */   }
/*     */   
/*     */   public boolean isFlowControlReject()
/*     */   {
/* 415 */     return this.isFlowControlReject;
/*     */   }
/*     */   
/*     */   public void setFlowControlReject(boolean isFlowControlReject)
/*     */   {
/* 420 */     this.isFlowControlReject = isFlowControlReject;
/*     */   }
/*     */   
/*     */   public void setFailPolicy(String failPolicy)
/*     */   {
/* 425 */     if ("failOver".equalsIgnoreCase(failPolicy))
/*     */     {
/* 427 */       this.failPolicy = "failOver";
/*     */     }
/* 429 */     else if ("failFast".equalsIgnoreCase(failPolicy))
/*     */     {
/* 431 */       this.failPolicy = "failFast";
/*     */     }
/*     */     else
/*     */     {
/* 435 */       this.failPolicy = failPolicy;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFailPolicy()
/*     */   {
/* 442 */     return this.failPolicy;
/*     */   }
/*     */   
/*     */   public void setIsReSend(boolean isReSend)
/*     */   {
/* 447 */     this.isReSend = isReSend;
/*     */   }
/*     */   
/*     */   public boolean getIsReSend()
/*     */   {
/* 452 */     return this.isReSend;
/*     */   }
/*     */   
/*     */   public ServiceType getServiceType()
/*     */   {
/* 457 */     return this.serviceType;
/*     */   }
/*     */   
/*     */   public void setServiceType(ServiceType serviceType)
/*     */   {
/* 462 */     this.serviceType = serviceType;
/*     */   }
/*     */   
/*     */   public boolean isTimeoutException()
/*     */   {
/* 467 */     return this.isTimeoutException;
/*     */   }
/*     */   
/*     */   public void setTimeoutException(boolean isTimeoutException)
/*     */   {
/* 472 */     this.isTimeoutException = isTimeoutException;
/*     */   }
/*     */   
/*     */   public boolean isEbusException()
/*     */   {
/* 477 */     return this.ebusException;
/*     */   }
/*     */   
/*     */   public void setEbusException(boolean ebusException)
/*     */   {
/* 482 */     this.ebusException = ebusException;
/*     */   }
/*     */   
/*     */   public DSFFaultToleranceProperties getCircuitBreakProperties()
/*     */   {
/* 487 */     return this.circuitBreakProperties;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setCircuitBreakProperties(DSFFaultToleranceProperties circuitBreakProperties)
/*     */   {
/* 493 */     this.circuitBreakProperties = circuitBreakProperties;
/*     */   }
/*     */   
/*     */   public String getRestProtocolDirection()
/*     */   {
/* 498 */     return this.restProtocolDirection;
/*     */   }
/*     */   
/*     */   public void setRestProtocolDirection(String restProtocolDirection)
/*     */   {
/* 503 */     this.restProtocolDirection = restProtocolDirection;
/*     */   }
/*     */   
/*     */   public boolean isSysnFirstNode()
/*     */   {
/* 508 */     return this.isSysnFirstNode;
/*     */   }
/*     */   
/*     */   public void setSysnFirstNode(boolean isSysnFirstNode)
/*     */   {
/* 513 */     this.isSysnFirstNode = isSysnFirstNode;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\Context.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */