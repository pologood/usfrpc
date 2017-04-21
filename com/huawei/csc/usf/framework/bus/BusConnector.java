/*      */ package com.huawei.csc.usf.framework.bus;
/*      */ 
/*      */ import com.huawei.csc.kernel.api.log.LogFactory;
/*      */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*      */ import com.huawei.csc.kernel.commons.util.ResourceResolverUtil;
/*      */ import com.huawei.csc.remoting.RemotingService;
/*      */ import com.huawei.csc.remoting.client.ClientConfig;
/*      */ import com.huawei.csc.remoting.client.ClientSSLConfig;
/*      */ import com.huawei.csc.remoting.client.ConnectionStatus;
/*      */ import com.huawei.csc.remoting.client.ConnectionStatusEvent;
/*      */ import com.huawei.csc.remoting.client.ConnectionStatusListener;
/*      */ import com.huawei.csc.remoting.client.RemotingClient;
/*      */ import com.huawei.csc.remoting.client.impl.RemotingClientImpl;
/*      */ import com.huawei.csc.remoting.common.Config;
/*      */ import com.huawei.csc.remoting.common.ConfigOption;
/*      */ import com.huawei.csc.remoting.common.ExecutorPool;
/*      */ import com.huawei.csc.remoting.common.FrameworkMessage;
/*      */ import com.huawei.csc.remoting.common.MessageProcessorType;
/*      */ import com.huawei.csc.remoting.common.OneWayMessageProcessor;
/*      */ import com.huawei.csc.remoting.common.RemotingEventLoopFactory;
/*      */ import com.huawei.csc.remoting.common.RemotingEventLoopGroup;
/*      */ import com.huawei.csc.remoting.common.SSLProvider;
/*      */ import com.huawei.csc.remoting.common.buf.ProtoBuf;
/*      */ import com.huawei.csc.remoting.common.buf.ProtoBufAllocator;
/*      */ import com.huawei.csc.remoting.common.decode.MessageHeaderDecoderFactory;
/*      */ import com.huawei.csc.remoting.common.decode.impl.MessageDecodeTask;
/*      */ import com.huawei.csc.remoting.common.exception.InvalidArgumentException;
/*      */ import com.huawei.csc.remoting.common.exception.RemotingException;
/*      */ import com.huawei.csc.remoting.common.impl.ConnectFuture;
/*      */ import com.huawei.csc.remoting.common.impl.EventLoopType;
/*      */ import com.huawei.csc.remoting.common.util.CastUtil;
/*      */ import com.huawei.csc.remoting.handler.HeartBeatHandler;
/*      */ import com.huawei.csc.remoting.server.ClientConnectionListener;
/*      */ import com.huawei.csc.remoting.server.RemotingServer;
/*      */ import com.huawei.csc.remoting.server.ServerConfig;
/*      */ import com.huawei.csc.remoting.server.ServerSSLConfig;
/*      */ import com.huawei.csc.usf.framework.AbstractConnector;
/*      */ import com.huawei.csc.usf.framework.ExceptionUtils;
/*      */ import com.huawei.csc.usf.framework.ExceptionUtilsHolder;
/*      */ import com.huawei.csc.usf.framework.IMessage;
/*      */ import com.huawei.csc.usf.framework.IMessageFactory;
/*      */ import com.huawei.csc.usf.framework.MessageHeaders;
/*      */ import com.huawei.csc.usf.framework.ReplyInterceptor;
/*      */ import com.huawei.csc.usf.framework.ServiceEngine;
/*      */ import com.huawei.csc.usf.framework.bind.BindHandlerImpl;
/*      */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*      */ import com.huawei.csc.usf.framework.event.FrameworkEvent;
/*      */ import com.huawei.csc.usf.framework.event.ServiceFrameworkEvent;
/*      */ import com.huawei.csc.usf.framework.event.ServiceFrameworkEventPublisher;
/*      */ import com.huawei.csc.usf.framework.exception.USFException;
/*      */ import com.huawei.csc.usf.framework.executor.ExecutePoolManager;
/*      */ import com.huawei.csc.usf.framework.executor.ExecuteThreadPool;
/*      */ import com.huawei.csc.usf.framework.executor.NamedThreadFactory;
/*      */ import com.huawei.csc.usf.framework.pojo.PojoServiceRegistry;
/*      */ import com.huawei.csc.usf.framework.service.ServicePublishListener;
/*      */ import com.huawei.csc.usf.framework.service.ServiceRegistrySupport;
/*      */ import com.huawei.csc.usf.framework.sr.ServiceInner;
/*      */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*      */ import com.huawei.csc.usf.framework.sr.ServiceTypeAware;
/*      */ import com.huawei.csc.usf.framework.statistic.ProcessDelayTracker;
/*      */ import com.huawei.csc.usf.framework.statistic.RemotingDelayTrackerListener;
/*      */ import com.huawei.csc.usf.framework.util.LogTraceUtil;
/*      */ import com.huawei.csc.usf.framework.util.USFContext;
/*      */ import java.io.File;
/*      */ import java.net.BindException;
/*      */ import java.net.URL;
/*      */ import java.nio.charset.Charset;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ExecutionException;
/*      */ import java.util.concurrent.ExecutorService;
/*      */ import java.util.concurrent.LinkedBlockingQueue;
/*      */ import java.util.concurrent.RejectedExecutionException;
/*      */ import java.util.concurrent.SynchronousQueue;
/*      */ import java.util.concurrent.ThreadPoolExecutor;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.TimeoutException;
/*      */ import java.util.concurrent.atomic.AtomicLong;
/*      */ import java.util.concurrent.locks.Lock;
/*      */ import java.util.concurrent.locks.ReadWriteLock;
/*      */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*      */ import org.apache.commons.lang.StringUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class BusConnector
/*      */   extends AbstractConnector
/*      */   implements ServiceTypeAware, ServicePublishListener
/*      */ {
/*  114 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(BusConnector.class);
/*      */   private final Object LOCK;
/*      */   
/*  117 */   public BusConnector() { this.LOCK = new Object();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  127 */     this.clientLock = new ReentrantReadWriteLock();
/*      */     
/*  129 */     this.lock = new Object();
/*      */     
/*  131 */     this.clientPool = new HashMap();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  137 */     this.poolManager = ExecutePoolManager.getInstance();
/*      */     
/*  139 */     this.clientEventLoopGroup = null;
/*      */     
/*  141 */     this.remotingDelayTrackerListener = null;
/*      */     
/*      */ 
/*  144 */     this.unusableServers = new HashSet();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  150 */     this.callbackThreadPool = new ThreadPoolExecutor(2, 8, 60000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(10000), new NamedThreadFactory("remoting-callback"));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  156 */     this.ioInProcess = new AtomicLong(0L);
/*      */     
/*      */ 
/*  159 */     this.ioFinishBegin = -1L;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  164 */     this.startServerFlag = false;
/*      */   }
/*      */   
/*      */   public void init() throws Exception
/*      */   {
/*  169 */     this.remotingDelayTrackerListener = new RemotingDelayTrackerListener(this.serviceEngine);
/*      */     
/*  171 */     if (this.clientEventLoopGroup == null)
/*      */     {
/*  173 */       int rpcClientIOWorkers = this.serviceEngine.getSystemConfig().getRPCClientIOWorkers();
/*      */       
/*  175 */       this.clientEventLoopGroup = RemotingEventLoopFactory.createEventLoopGroup(EventLoopType.NIO, rpcClientIOWorkers, new NamedThreadFactory("io-client", false));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  181 */     this.msgCoder = new MsgCoder(this.serviceEngine, getServiceType());
/*      */     
/*  183 */     ServiceRegistrySupport.getInstance().getSerivceRegistry(getServiceType()).addServicePublishListener(this);
/*      */   }
/*      */   
/*      */   protected static final int MINMESSAGELEN = 12;
/*      */   protected static final int TYPE_LEN_OFFSET = 8;
/*      */   protected static final int SERVICE_LEN_OFFSET = 16;
/*      */   protected static final int SERVICE_NAME_OFFSET = 20;
/*      */   protected final ReadWriteLock clientLock;
/*      */   private final Object lock;
/*      */   protected Map<String, ClientGroup> clientPool;
/*      */   protected MsgCoder msgCoder;
/*      */   public void start()
/*      */     throws Exception
/*      */   {
/*  197 */     if (this.serviceEngine.getStartBusConnectorFlag().contains(getServiceType().toString()))
/*      */     {
/*      */ 
/*  200 */       startServer();
/*      */     }
/*      */   }
/*      */   
/*      */   protected void startServer() throws Exception
/*      */   {
/*  206 */     this.server = createServer();
/*      */     try
/*      */     {
/*  209 */       this.server.start().get(6000L, TimeUnit.MILLISECONDS);
/*  210 */       this.startServerFlag = true;
/*      */     }
/*      */     catch (ExecutionException executionException)
/*      */     {
/*  214 */       handleStartException(executionException);
/*      */     }
/*      */   }
/*      */   
/*      */   public void handleStartException(ExecutionException executionException)
/*      */     throws Exception
/*      */   {
/*  221 */     Throwable throwable = executionException.getCause();
/*  222 */     if ((throwable instanceof BindException))
/*      */     {
/*  224 */       String rpcAddr = this.serviceEngine.getSystemConfig().getRPCAddress(getServiceType());
/*      */       
/*  226 */       String[] ipport = rpcAddr.split(":");
/*  227 */       throw ExceptionUtilsHolder.getExceptionUtils(getServiceType()).ipBindFailed(ipport[0], ipport[1]);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  232 */     throw executionException;
/*      */   }
/*      */   
/*      */ 
/*      */   public <T> T doOnReceive(com.huawei.csc.usf.framework.Context context)
/*      */     throws Exception
/*      */   {
/*  239 */     context.setSrcConnector(this);
/*  240 */     this.serviceEngine.onReceive(context);
/*      */     
/*  242 */     return (T)CastUtil.cast(context.getReplyMessage());
/*      */   }
/*      */   
/*      */   public IMessage decode(com.huawei.csc.usf.framework.Context context)
/*      */     throws Exception
/*      */   {
/*  248 */     Object[] obj = context.getNativeInputs();
/*  249 */     IMessage buf = (IMessage)obj[0];
/*  250 */     return buf;
/*      */   }
/*      */   
/*      */   public <T> T encode(com.huawei.csc.usf.framework.Context context, IMessage message)
/*      */     throws Exception
/*      */   {
/*  256 */     return (T)CastUtil.cast(message);
/*      */   }
/*      */   
/*      */   protected void dispatch(com.huawei.csc.usf.framework.Context context, IMessage message) throws Exception
/*      */   {
/*  261 */     this.serviceEngine.getReplyInterceptor().onMessage(context.getReplyMessage(), context);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public IMessage doHandle(com.huawei.csc.usf.framework.Context context, IMessage request)
/*      */     throws Exception
/*      */   {
/*  269 */     ProcessDelayTracker getClientTracker = ProcessDelayTracker.next(context.getProcessDelayTracker(), "invoke.handle.findIoClient");
/*      */     
/*  271 */     RemotingClient client = null;
/*  272 */     String addr = request.getHeaders().getDestAddr();
/*      */     
/*      */     try
/*      */     {
/*  276 */       client = selectClient(addr);
/*  277 */       if (client == null)
/*      */       {
/*  279 */         client = buildClient(addr);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  290 */       if ((client == null) || (!client.isConnected()))
/*      */       {
/*  292 */         addUnusableServer(addr);
/*  293 */         throw ExceptionUtilsHolder.getExceptionUtils(context.getServiceType()).remoteNotReachable(addr);
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     catch (TimeoutException e)
/*      */     {
/*  300 */       LOGGER.error("connect to server failed. server address: " + addr);
/*  301 */       throw ExceptionUtilsHolder.getExceptionUtils(context.getServiceType()).remoteNotReachable(addr);
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*  306 */       ProcessDelayTracker.done(getClientTracker);
/*      */     }
/*      */     
/*      */ 
/*  310 */     return sendReq(context, client, request);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected IMessage sendReq(com.huawei.csc.usf.framework.Context context, RemotingClient client, IMessage request)
/*      */     throws Exception
/*      */   {
/*  320 */     USFContext.getContext().usfCtx2RpcCtx(request);
/*  321 */     if (null != client)
/*      */     {
/*      */ 
/*      */       try
/*      */       {
/*      */ 
/*  327 */         if (LOGGER.isDebugEnable())
/*      */         {
/*  329 */           LOGGER.debug("before send request,request message header info:" + request.getHeaders().toString());
/*      */         }
/*      */         
/*      */ 
/*  333 */         ProcessDelayTracker tracker = ProcessDelayTracker.next(context.getProcessDelayTracker(), "invoke.handle.sendMsg");
/*      */         
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/*  339 */           com.huawei.csc.remoting.common.Context rContext = new com.huawei.csc.remoting.common.Context();
/*  340 */           if (ProcessDelayTracker.isTrackerOpen())
/*      */           {
/*  342 */             rContext.addContext("usf.tracker", context.getProcessDelayTracker());
/*      */           }
/*      */           
/*      */ 
/*  346 */           client.sendOneWay(request, rContext);
/*      */         }
/*      */         finally
/*      */         {
/*  350 */           ProcessDelayTracker.done(tracker);
/*      */         }
/*      */         
/*      */       }
/*      */       catch (RemotingException e)
/*      */       {
/*  356 */         if (LOGGER.isErrorEnable())
/*      */         {
/*  358 */           LOGGER.error("Failed to send message [" + request + "]");
/*      */         }
/*  360 */         throw ExceptionUtilsHolder.getExceptionUtils(context.getServiceType()).remoteNotReachable(request.getHeaders().getDestAddr());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  365 */       ((RemotingClientImpl)client).insertReqID(request.getHeaders().getRequestId());
/*      */     }
/*      */     
/*  368 */     return null;
/*      */   }
/*      */   
/*      */   private RemotingClient selectClient(String addr)
/*      */   {
/*  373 */     Lock lock = this.clientLock.readLock();
/*  374 */     lock.lock();
/*      */     try
/*      */     {
/*  377 */       ClientGroup clientGroup = (ClientGroup)this.clientPool.get(addr);
/*  378 */       RemotingClient localRemotingClient; if (clientGroup == null)
/*      */       {
/*  380 */         return null;
/*      */       }
/*  382 */       return clientGroup.select();
/*      */     }
/*      */     finally
/*      */     {
/*  386 */       lock.unlock();
/*      */     }
/*      */   }
/*      */   
/*      */   public void destroy()
/*      */     throws Exception
/*      */   {
/*  393 */     if (null != this.clientEventLoopGroup)
/*      */     {
/*  395 */       this.clientEventLoopGroup.shutdown();
/*      */     }
/*      */     
/*  398 */     for (Map.Entry<String, ClientGroup> entry : this.clientPool.entrySet())
/*      */     {
/*      */       try
/*      */       {
/*  402 */         ClientGroup cg = (ClientGroup)entry.getValue();
/*  403 */         cg.close();
/*      */       }
/*      */       catch (Exception ex)
/*      */       {
/*  407 */         LOGGER.warn("Close client group[" + ((ClientGroup)entry.getValue()).remoteAddr + "] failed.", ex);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  412 */     this.clientPool.clear();
/*      */     
/*  414 */     if (null != this.server)
/*      */     {
/*  416 */       this.server.stop();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getConnectorType()
/*      */   {
/*  425 */     return "BUS";
/*      */   }
/*      */   
/*      */   protected RemotingServer createServer() throws RemotingException
/*      */   {
/*  430 */     SystemConfig systemConfig = this.serviceEngine.getSystemConfig();
/*  431 */     ServerConfig serverConfig = new ServerConfig();
/*      */     
/*  433 */     serverConfig.setThreadFactory(new NamedThreadFactory("io-server", false));
/*      */     
/*      */ 
/*      */ 
/*  437 */     String whiteList = systemConfig.getWhiteList();
/*  438 */     String blackList = systemConfig.getBlackList();
/*      */     
/*  440 */     if (!StringUtils.isEmpty(whiteList))
/*      */     {
/*  442 */       serverConfig.addOption(ConfigOption.IP_WHITE_LIST, whiteList);
/*      */     }
/*  444 */     if (!StringUtils.isEmpty(blackList))
/*      */     {
/*  446 */       serverConfig.addOption(ConfigOption.IP_BLACK_LIST, blackList);
/*      */     }
/*  448 */     if ((!StringUtils.isEmpty(whiteList)) && (!StringUtils.isEmpty(blackList)))
/*      */     {
/*  450 */       LOGGER.warn("busconnector:both whitelist and blacklist has been configed. so the blacklist will not effective.");
/*      */     }
/*      */     
/*  453 */     String rpcAddr = systemConfig.getRPCAddress(ServiceType.USF);
/*  454 */     String[] ipport = rpcAddr.split(":");
/*  455 */     serverConfig.setHost(ipport[0]);
/*  456 */     serverConfig.setPort(Integer.parseInt(ipport[1]));
/*  457 */     serverConfig.setWorkerThreadCount(systemConfig.getRPCServerIOWorkers());
/*  458 */     serverConfig.addOption(ConfigOption.SO_BACKLOG, Integer.valueOf(128));
/*  459 */     serverConfig.addOption(ConfigOption.SO_KEEPALIVE, Boolean.valueOf(true));
/*  460 */     serverConfig.addOption(ConfigOption.TCP_NODELAY, Boolean.valueOf(true));
/*  461 */     serverConfig.addOption(ConfigOption.CONNECTION_TIMEOUT, Integer.valueOf(systemConfig.getConnectionTimeout() * 1000));
/*      */     
/*  463 */     serverConfig.addOption(ConfigOption.SO_RCVBUF, Integer.valueOf(8388608));
/*  464 */     serverConfig.addOption(ConfigOption.SO_SNDBUF, Integer.valueOf(8388608));
/*  465 */     serverConfig.addOption(ConfigOption.HEARTBEAT_INTERVEL, Integer.valueOf(systemConfig.getRPCHeartBeatInterval() * 1000));
/*      */     
/*  467 */     serverConfig.addOption(ConfigOption.HEARTBEAT_NO_RESP_COUNT, Integer.valueOf(systemConfig.getRPCHeartBeatMaxLostCount()));
/*      */     
/*  469 */     serverConfig.addOption(ConfigOption.IS_TRANSACTION_ENABLED, Boolean.valueOf(false));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  474 */     serverConfig.addOption(ConfigOption.HEARTBEAT_HANDLER, new HeartBeatHandlerImpl());
/*      */     
/*  476 */     if (systemConfig.isServerAuthenticationEnable())
/*      */     {
/*  478 */       serverConfig.addOption(ConfigOption.BIND_HANDLER, new BindHandlerImpl(systemConfig));
/*      */     }
/*      */     
/*      */ 
/*  482 */     if (systemConfig.isSslEnable())
/*      */     {
/*  484 */       addServerSSLSupport(serverConfig, systemConfig);
/*      */     }
/*  486 */     serverConfig.addOption(ConfigOption.DECODER_THREAD_POOL, new ServicePool(this.poolManager, this.ioInProcess, null));
/*      */     
/*  488 */     serverConfig.addOption(ConfigOption.MESSAGE_ENCODER, this.msgCoder);
/*  489 */     serverConfig.addOption(ConfigOption.MESSAGE_DECODER, this.msgCoder);
/*  490 */     serverConfig.addOption(ConfigOption.MESSAGE_HEADER_DECODER, MessageHeaderDecoderFactory.createLengthBasedHeaderDecoder(Integer.MAX_VALUE, 4, 4, -4, 0, false));
/*      */     
/*      */ 
/*  493 */     int ioStatisticsInterval = systemConfig.getIoStatisticsInterval();
/*  494 */     if (ioStatisticsInterval > 0)
/*      */     {
/*  496 */       RemotingService.getInstance().startIOStatistics(Long.valueOf(ioStatisticsInterval * 1000L));
/*      */     }
/*      */     
/*  499 */     RemotingServer server = RemotingService.getInstance().createServer(serverConfig);
/*      */     
/*  501 */     server.addClientConnectionListener(new ClientConnectionListenerImpl());
/*      */     
/*  503 */     if (ProcessDelayTracker.isTrackerOpen())
/*      */     {
/*  505 */       server.registerEventListener(this.remotingDelayTrackerListener);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  512 */     if (LOGGER.isInfoEnable())
/*      */     {
/*  514 */       LOGGER.info("Start usf protocol server,listening address: " + ipport[0] + ",port: " + ipport[1] + ",white list: " + whiteList + ",black list: " + blackList);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  519 */     return server;
/*      */   }
/*      */   
/*      */ 
/*      */   protected RemotingServer server;
/*      */   
/*      */   protected ExecutePoolManager poolManager;
/*      */   protected RemotingEventLoopGroup clientEventLoopGroup;
/*      */   protected RemotingDelayTrackerListener remotingDelayTrackerListener;
/*      */   protected Set<String> unusableServers;
/*      */   protected ThreadPoolExecutor callbackThreadPool;
/*      */   protected AtomicLong ioInProcess;
/*      */   protected long ioFinishBegin;
/*      */   protected boolean startServerFlag;
/*      */   protected void addServerSSLSupport(ServerConfig serverConfig, SystemConfig systemConfig)
/*      */     throws InvalidArgumentException, USFException
/*      */   {
/*  536 */     serverConfig.addOption(ConfigOption.IS_SSL_ENABLED, Boolean.valueOf(true));
/*  537 */     ServerSSLConfig ssl = new ServerSSLConfig();
/*  538 */     String trustStorePath = systemConfig.getTrustStorePath();
/*  539 */     URL trustStoreUrl = ResourceResolverUtil.getResource(trustStorePath);
/*  540 */     if (null != trustStoreUrl)
/*      */     {
/*  542 */       if (LOGGER.isInfoEnable())
/*      */       {
/*  544 */         LOGGER.info("Server's ssl truststore file path:" + trustStoreUrl.getPath());
/*      */       }
/*      */       
/*  547 */       File trustStoreFile = new File(trustStoreUrl.getPath());
/*  548 */       ssl.setCertificateChainFile(trustStoreFile);
/*      */     }
/*      */     else
/*      */     {
/*  552 */       LOGGER.error("Server's ssl truststore file path:" + trustStorePath);
/*  553 */       throw new USFException("405180813", new Object[] { trustStorePath });
/*      */     }
/*      */     
/*      */ 
/*  557 */     String keyStorePath = systemConfig.getKeyStorePath();
/*  558 */     URL keyStoreUrl = ResourceResolverUtil.getResource(keyStorePath);
/*  559 */     if (null != keyStoreUrl)
/*      */     {
/*  561 */       if (LOGGER.isInfoEnable())
/*      */       {
/*  563 */         LOGGER.info("Server's ssl keystore file path:" + keyStoreUrl.getPath());
/*      */       }
/*      */       
/*  566 */       File keyStoreFile = new File(keyStoreUrl.getPath());
/*  567 */       ssl.setKeyFile(keyStoreFile);
/*  568 */       ssl.setKeyPassword(systemConfig.getKeyStorePassword());
/*      */     }
/*      */     else
/*      */     {
/*  572 */       LOGGER.info("Server's ssl keystore file path:" + keyStorePath);
/*  573 */       throw new USFException("405180813", new Object[] { keyStorePath });
/*      */     }
/*      */     
/*  576 */     ssl.setProvider(SSLProvider.JDK);
/*  577 */     serverConfig.addOption(ConfigOption.SERVER_SSL_CONFIG, ssl);
/*      */   }
/*      */   
/*      */ 
/*      */   public static ExecutorService newCachedThreadPool(int maxPoolSize, int queueCapacity, String name)
/*      */   {
/*  583 */     return new ThreadPoolExecutor(0, maxPoolSize, 600L, TimeUnit.SECONDS, new SynchronousQueue(), new NamedThreadFactory(name));
/*      */   }
/*      */   
/*      */ 
/*      */   protected class ClientGroup
/*      */   {
/*  589 */     private AtomicLong selectIdx = new AtomicLong();
/*      */     
/*      */     private RemotingClient[] clientArray;
/*      */     
/*      */     private String remoteAddr;
/*      */     
/*      */     private int size;
/*      */     
/*      */     public ClientGroup(String remoteAddr, int size)
/*      */     {
/*  599 */       this.remoteAddr = remoteAddr;
/*  600 */       this.size = size;
/*      */     }
/*      */     
/*      */     public void startGroup() throws Exception
/*      */     {
/*  605 */       this.clientArray = new RemotingClient[this.size];
/*      */       
/*  607 */       for (int i = 0; i < this.size; i++)
/*      */       {
/*  609 */         this.clientArray[i] = BusConnector.this.createClient(this.remoteAddr);
/*      */       }
/*      */     }
/*      */     
/*      */     public RemotingClient select()
/*      */     {
/*  615 */       RemotingClient client = null;
/*  616 */       if (this.clientArray != null)
/*      */       {
/*  618 */         int clientIndex = 0;
/*      */         
/*      */ 
/*  621 */         while (((client == null) || (!client.isConnected())) && (clientIndex < this.size))
/*      */         {
/*  623 */           clientIndex++;
/*  624 */           client = this.clientArray[((int)(this.selectIdx.getAndIncrement() % this.clientArray.length))];
/*      */         }
/*      */       }
/*  627 */       return client;
/*      */     }
/*      */     
/*      */     public void close()
/*      */     {
/*  632 */       if (this.clientArray != null)
/*      */       {
/*  634 */         for (RemotingClient client : this.clientArray)
/*      */         {
/*      */           try
/*      */           {
/*  638 */             if (null != client)
/*      */             {
/*      */ 
/*      */ 
/*  642 */               client.registerEventListener(BusConnector.this.remotingDelayTrackerListener);
/*  643 */               ConnectFuture future = client.shutdown();
/*  644 */               if (null != future)
/*      */               {
/*  646 */                 future.get(3000L, TimeUnit.MILLISECONDS);
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Exception e) {
/*  651 */             if (BusConnector.LOGGER.isErrorEnable())
/*      */             {
/*  653 */               BusConnector.LOGGER.error("Failed to disconnect the client", e);
/*      */             }
/*      */           }
/*      */         }
/*  657 */         this.clientArray = null;
/*      */       }
/*      */     }
/*      */     
/*      */     public boolean isAllDisconnect()
/*      */     {
/*  663 */       for (int clientIndex = 0; clientIndex < this.size; clientIndex++)
/*      */       {
/*  665 */         RemotingClient client = this.clientArray[Math.abs(clientIndex % this.clientArray.length)];
/*      */         
/*  667 */         if ((client != null) && (client.isConnected()))
/*      */         {
/*  669 */           return false;
/*      */         }
/*      */       }
/*  672 */       return true;
/*      */     }
/*      */   }
/*      */   
/*      */   protected RemotingClient createClient(String addr) throws Exception
/*      */   {
/*  678 */     Lock lock = this.clientLock.writeLock();
/*  679 */     lock.lock();
/*  680 */     RemotingClient client = null;
/*      */     try
/*      */     {
/*  683 */       SystemConfig systemConfig = this.serviceEngine.getSystemConfig();
/*      */       
/*  685 */       String[] ipPort = addr.split(":");
/*  686 */       ClientConfig clientConfig = new ClientConfig();
/*  687 */       clientConfig.setRemoteHost(ipPort[0]);
/*  688 */       clientConfig.setRemotePort(Integer.parseInt(ipPort[1]));
/*      */       
/*  690 */       clientConfig.addOption(ConfigOption.CONNECTION_TIMEOUT, Integer.valueOf(300000));
/*      */       
/*  692 */       clientConfig.addOption(ConfigOption.TRANSACTION_TIMEOUT, Integer.valueOf(systemConfig.getTransactionTimeout() * 1000));
/*      */       
/*  694 */       clientConfig.addOption(ConfigOption.TCP_NODELAY, Boolean.valueOf(true));
/*  695 */       clientConfig.addOption(ConfigOption.SO_KEEPALIVE, Boolean.valueOf(true));
/*  696 */       clientConfig.addOption(ConfigOption.SO_RCVBUF, Integer.valueOf(8388608));
/*  697 */       clientConfig.addOption(ConfigOption.SO_SNDBUF, Integer.valueOf(8388608));
/*  698 */       clientConfig.addOption(ConfigOption.HEARTBEAT_INTERVEL, Integer.valueOf(systemConfig.getRPCHeartBeatInterval() * 1000));
/*      */       
/*  700 */       clientConfig.addOption(ConfigOption.HEARTBEAT_NO_RESP_COUNT, Integer.valueOf(systemConfig.getRPCHeartBeatMaxLostCount()));
/*      */       
/*  702 */       clientConfig.addOption(ConfigOption.RECONNECT_INTERVAL, Integer.valueOf(systemConfig.getRPCClientReconnectInterval() * 1000));
/*      */       
/*  704 */       clientConfig.addOption(ConfigOption.DECODER_THREAD_POOL, new ClientPool(this.poolManager));
/*      */       
/*      */ 
/*      */ 
/*  708 */       clientConfig.addOption(ConfigOption.MESSAGE_DECODER, this.msgCoder);
/*  709 */       clientConfig.addOption(ConfigOption.MESSAGE_ENCODER, this.msgCoder);
/*  710 */       clientConfig.addOption(ConfigOption.MESSAGE_HEADER_DECODER, MessageHeaderDecoderFactory.createLengthBasedHeaderDecoder(Integer.MAX_VALUE, 4, 4, -4, 0, false));
/*      */       
/*      */ 
/*  713 */       clientConfig.addOption(ConfigOption.REMOTING_EVENT_LOOP_GROUP, this.clientEventLoopGroup);
/*      */       
/*  715 */       clientConfig.addOption(ConfigOption.IS_TRANSACTION_ENABLED, Boolean.valueOf(false));
/*  716 */       clientConfig.addOption(ConfigOption.NOTIFICATION_CALLBACK_THREADPOOL_EXECUTOR, this.callbackThreadPool);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  721 */       clientConfig.addOption(ConfigOption.HEARTBEAT_HANDLER, new HeartBeatHandlerImpl());
/*      */       
/*  723 */       if (systemConfig.isClientAuthenticationEnable())
/*      */       {
/*  725 */         clientConfig.addOption(ConfigOption.BIND_HANDLER, new BindHandlerImpl(systemConfig));
/*      */       }
/*      */       
/*  728 */       if (systemConfig.isSslEnable())
/*      */       {
/*  730 */         addClientSSLSupport(clientConfig, systemConfig);
/*      */       }
/*      */       
/*  733 */       int ioStatisticsInterval = systemConfig.getIoStatisticsInterval();
/*  734 */       if (ioStatisticsInterval > 0)
/*      */       {
/*  736 */         RemotingService.getInstance().startIOStatistics(Long.valueOf(ioStatisticsInterval * 1000L));
/*      */       }
/*      */       
/*  739 */       client = RemotingService.getInstance().createClient(clientConfig);
/*  740 */       client.addConnectionStatusListener(new ConnectionStatusListenerImpl(client, this));
/*      */       
/*      */ 
/*  743 */       client.connect().get(5000L, TimeUnit.MILLISECONDS);
/*  744 */       client.setMessageProcessor(new ClientMessageProcessorImpl(client), MessageProcessorType.ONEWAY);
/*      */       
/*      */ 
/*  747 */       if (ProcessDelayTracker.isTrackerOpen())
/*      */       {
/*  749 */         client.registerEventListener(this.remotingDelayTrackerListener);
/*      */       }
/*  751 */       if (LOGGER.isInfoEnable())
/*      */       {
/*  753 */         LOGGER.info("create client successfully,remoting address:" + addr);
/*      */       }
/*      */       
/*      */ 
/*  757 */       return client;
/*      */ 
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*  762 */       if (LOGGER.isErrorEnable())
/*      */       {
/*  764 */         LOGGER.error("Failed to creat client", e);
/*      */       }
/*  766 */       throw e;
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*  771 */       lock.unlock();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addClientSSLSupport(ClientConfig clientConfig, SystemConfig systemConfig)
/*      */     throws USFException, InvalidArgumentException
/*      */   {
/*  787 */     clientConfig.addOption(ConfigOption.IS_SSL_ENABLED, Boolean.valueOf(true));
/*  788 */     ClientSSLConfig ssl = new ClientSSLConfig();
/*  789 */     String trustStorePath = systemConfig.getTrustStorePath();
/*  790 */     URL trustStoreUrl = ResourceResolverUtil.getResource(trustStorePath);
/*  791 */     if (null != trustStoreUrl)
/*      */     {
/*  793 */       if (LOGGER.isInfoEnable())
/*      */       {
/*  795 */         LOGGER.info("Client's ssl truststore file path:" + trustStoreUrl.getPath());
/*      */       }
/*      */       
/*  798 */       File trustStoreFile = new File(trustStoreUrl.getPath());
/*  799 */       ssl.setCertificateChainFile(trustStoreFile);
/*      */     }
/*      */     else
/*      */     {
/*  803 */       LOGGER.error("Client's ssl truststore file path:" + trustStorePath);
/*  804 */       throw new USFException("405180813", new Object[] { trustStorePath });
/*      */     }
/*      */     
/*  807 */     clientConfig.addOption(ConfigOption.CLIENT_SSL_CONFIG, ssl);
/*      */   }
/*      */   
/*      */   protected RemotingClient buildClient(String addr) throws Exception
/*      */   {
/*  812 */     RemotingClient client = selectClient(addr);
/*  813 */     if (client != null)
/*      */     {
/*  815 */       return client;
/*      */     }
/*      */     
/*  818 */     Lock lock = this.clientLock.writeLock();
/*  819 */     lock.lock();
/*      */     try
/*      */     {
/*  822 */       ClientGroup clientGroup = (ClientGroup)this.clientPool.get(addr);
/*  823 */       RemotingClient localRemotingClient1; if (clientGroup != null)
/*      */       {
/*  825 */         return selectClient(addr);
/*      */       }
/*      */       
/*      */ 
/*  829 */       clientGroup = new ClientGroup(addr, this.serviceEngine.getSystemConfig().getRPCChannelNum());
/*      */       
/*  831 */       clientGroup.startGroup();
/*      */       
/*  833 */       this.clientPool.put(addr, clientGroup);
/*  834 */       return clientGroup.select();
/*      */     }
/*      */     finally
/*      */     {
/*  838 */       lock.unlock();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isAsync()
/*      */   {
/*  845 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected class ClientConnectionListenerImpl
/*      */     implements ClientConnectionListener
/*      */   {
/*      */     public ClientConnectionListenerImpl() {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void clientConnectionEstablished(Long clientID)
/*      */     {
/*  865 */       if (BusConnector.LOGGER.isInfoEnable())
/*      */       {
/*  867 */         BusConnector.LOGGER.info("client connection established, clientID: " + clientID + ",connect with the server:" + BusConnector.this.server.getConfig().getHost() + ":" + BusConnector.this.server.getConfig().getPort());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/*  874 */         BusConnector.this.server.setMessageProcessor(clientID, new BusConnector.ServerMessageProcessorImpl(BusConnector.this, clientID), MessageProcessorType.ONEWAY);
/*      */ 
/*      */       }
/*      */       catch (RemotingException e)
/*      */       {
/*      */ 
/*  880 */         if (BusConnector.LOGGER.isErrorEnable())
/*      */         {
/*  882 */           BusConnector.LOGGER.error("Failed to add server message processor, server:" + BusConnector.this.server.getConfig().getHost() + ":" + BusConnector.this.server.getConfig().getPort(), e);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  890 */       ServiceFrameworkEvent frameworkEvent = FrameworkEvent.creatEvent(null, null, null, Boolean.valueOf(true), null, null, null, null);
/*      */       
/*      */       try
/*      */       {
/*  894 */         ServiceFrameworkEventPublisher.publish("usf.framework.remoting.server.established", frameworkEvent);
/*      */ 
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*      */ 
/*  900 */         if (BusConnector.LOGGER.isErrorEnable())
/*      */         {
/*  902 */           BusConnector.LOGGER.error("Excpetion occured while processing client connection established event ", e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void clientConnectionLoggedIn(Long clientID)
/*      */     {
/*  912 */       if (BusConnector.LOGGER.isInfoEnable())
/*      */       {
/*  914 */         BusConnector.LOGGER.info("client connection logged in, clientID: " + clientID + ",login in the server:" + BusConnector.this.server.getConfig().getHost() + ":" + BusConnector.this.server.getConfig().getPort());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void clientConnectionDisconnected(Long clientID)
/*      */     {
/*  926 */       ServiceFrameworkEvent frameworkEvent = FrameworkEvent.creatEvent(null, null, null, Boolean.valueOf(true), null, null, null, null);
/*      */       
/*      */       try
/*      */       {
/*  930 */         ServiceFrameworkEventPublisher.publish("usf.framework.remoting.server.disconnected", frameworkEvent);
/*      */ 
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*      */ 
/*  936 */         if (BusConnector.LOGGER.isErrorEnable())
/*      */         {
/*  938 */           BusConnector.LOGGER.error("Excpetion occured while processing client connection disconnected event ", e);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  944 */       if (BusConnector.LOGGER.isInfoEnable())
/*      */       {
/*  946 */         BusConnector.LOGGER.info("client connection disconnected, clientID: " + clientID + ",disconnect with the server:" + BusConnector.this.server.getConfig().getHost() + ":" + BusConnector.this.server.getConfig().getPort());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected class ClientMessageProcessorImpl
/*      */     implements OneWayMessageProcessor
/*      */   {
/*      */     RemotingClient client;
/*      */     
/*      */ 
/*      */     public ClientMessageProcessorImpl(RemotingClient client)
/*      */     {
/*  961 */       this.client = client;
/*      */     }
/*      */     
/*      */ 
/*      */     public void onMessage(FrameworkMessage frameworkMessage)
/*      */     {
/*  967 */       Object message = frameworkMessage.getMessage();
/*  968 */       IMessage reply = (IMessage)message;
/*      */       
/*  970 */       com.huawei.csc.usf.framework.Context context = BusConnector.this.serviceEngine.getReplyInterceptor().getContext(reply.getHeaders().getRequestId());
/*      */       
/*      */ 
/*  973 */       boolean justCreated = false;
/*  974 */       if (null == context)
/*      */       {
/*      */ 
/*  977 */         context = new com.huawei.csc.usf.framework.Context(new Object[] { message });
/*  978 */         context.setServiceType(BusConnector.this.getServiceType());
/*      */         
/*  980 */         ProcessDelayTracker tracker = context.getProcessDelayTracker();
/*  981 */         if (null != tracker)
/*      */         {
/*  983 */           tracker.setClient(true);
/*  984 */           tracker.setMsgId(String.valueOf(reply.getHeaders().getRequestId()));
/*      */           
/*  986 */           tracker.setOperation(reply.getHeaders().getOperation());
/*  987 */           tracker.setServiceName(reply.getHeaders().getServiceName());
/*      */         }
/*  989 */         context.setSrcConnector(BusConnector.this);
/*  990 */         justCreated = true;
/*      */       }
/*      */       
/*  993 */       ProcessDelayTracker tracker = context.getProcessDelayTracker();
/*  994 */       ProcessDelayTracker procRespMsgTracker = ProcessDelayTracker.next(tracker, "io.onMessageReceive");
/*      */       
/*      */ 
/*      */ 
/*  998 */       LogTraceUtil.updateMessageTraceOrder(reply);
/*      */       
/* 1000 */       context.setReplyMessage(reply);
/*      */       
/* 1002 */       ((RemotingClientImpl)this.client).removeReqID(reply.getHeaders().getRequestId());
/*      */       
/*      */       try
/*      */       {
/* 1006 */         BusConnector.this.dispatch(context, reply);
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/* 1010 */         if (BusConnector.LOGGER.isErrorEnable())
/*      */         {
/* 1012 */           BusConnector.LOGGER.error("dispatch exception.", e);
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 1017 */         ProcessDelayTracker.done(procRespMsgTracker);
/* 1018 */         if (justCreated)
/*      */         {
/* 1020 */           context.done();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class ServerMessageProcessorImpl
/*      */     implements OneWayMessageProcessor
/*      */   {
/*      */     private Long clientId;
/*      */     
/*      */ 
/*      */ 
/*      */     public ServerMessageProcessorImpl(Long clientId)
/*      */     {
/* 1037 */       this.clientId = clientId;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void onMessage(FrameworkMessage frameworkMessage)
/*      */     {
/* 1050 */       Object message = frameworkMessage.getMessage();
/*      */       
/* 1052 */       com.huawei.csc.remoting.common.Context remotingContext = frameworkMessage.getContext();
/*      */       
/*      */ 
/* 1055 */       ProcessDelayTracker remotingTracker = (ProcessDelayTracker)remotingContext.removeContext("newResponseTracker");
/*      */       
/*      */ 
/* 1058 */       IMessage reply = null;
/* 1059 */       ((IMessage)message).getHeaders().setSrcAddr(BusConnector.this.server.getClientAddress(this.clientId));
/*      */       
/* 1061 */       ((IMessage)message).getHeaders().setDestAddr(BusConnector.this.server.getAddress());
/* 1062 */       com.huawei.csc.usf.framework.Context context = new com.huawei.csc.usf.framework.Context(new Object[] { message });
/* 1063 */       context.setServiceType(BusConnector.this.getServiceType());
/*      */       
/* 1065 */       if (null != remotingTracker)
/*      */       {
/* 1067 */         ProcessDelayTracker.setIsClient(remotingTracker, false);
/* 1068 */         context.setProcessDelayTracker(remotingTracker);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/* 1080 */         context.setServer(true);
/* 1081 */         ProcessDelayTracker onRceviveTracker = null;
/* 1082 */         if (null != remotingTracker)
/*      */         {
/* 1084 */           onRceviveTracker = ProcessDelayTracker.next(remotingTracker, "onRcevive");
/*      */         }
/*      */         
/*      */ 
/*      */         try
/*      */         {
/* 1090 */           reply = (IMessage)BusConnector.this.onReceive(context);
/*      */ 
/*      */         }
/*      */         catch (Throwable th)
/*      */         {
/* 1095 */           context.setException(th);
/*      */         }
/*      */         finally
/*      */         {
/* 1099 */           if (null != onRceviveTracker)
/*      */           {
/* 1101 */             ProcessDelayTracker.done(onRceviveTracker);
/*      */           }
/*      */         }
/*      */         
/* 1105 */         Throwable throwable = context.getException();
/* 1106 */         if ((null == reply) && (throwable != null))
/*      */         {
/* 1108 */           reply = BusConnector.this.serviceEngine.getMessageFactory(BusConnector.this.getServiceType()).createReplyMessage((IMessage)message);
/*      */           
/* 1110 */           reply.setPayload(throwable);
/* 1111 */           context.setReplyMessage(reply);
/*      */         }
/*      */         
/* 1114 */         if (reply == null)
/*      */         {
/* 1116 */           if (BusConnector.LOGGER.isInfoEnable())
/*      */           {
/* 1118 */             MessageHeaders header = ((IMessage)message).getHeaders();
/*      */             
/* 1120 */             BusConnector.LOGGER.info(String.format("reply is null, request info: id[%d], srcAddr[%s], type[%s], group[%s], serviceName[%s], operationName[%s].", new Object[] { Long.valueOf(header.getRequestId()), header.getSrcAddr(), header.getType(), header.getGroup(), header.getServiceName(), header.getOperation() }));
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*      */ 
/* 1131 */           if (context.getServiceType().equals(ServiceType.DSF))
/*      */           {
/* 1133 */             String serializeType = context.getReceivedMessage().getHeaders().getAttachValue("serialization");
/*      */             
/*      */ 
/*      */ 
/* 1137 */             reply.getHeaders().setAttachValue("serialization", serializeType);
/*      */           }
/*      */           
/*      */ 
/* 1141 */           if (BusConnector.LOGGER.isDebugEnable())
/*      */           {
/* 1143 */             BusConnector.LOGGER.debug("finished invoke method,reply message header info: " + reply.getHeaders().toString());
/*      */           }
/*      */           
/*      */ 
/* 1147 */           ProcessDelayTracker onServerSendTracker = null;
/* 1148 */           if (null != remotingTracker)
/*      */           {
/* 1150 */             onServerSendTracker = ProcessDelayTracker.next(remotingTracker, "onServerSend");
/*      */           }
/*      */           
/*      */           try
/*      */           {
/* 1155 */             if (ProcessDelayTracker.isTrackerOpen())
/*      */             {
/* 1157 */               remotingContext.addContext("usf.tracker", context.getProcessDelayTracker());
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 1163 */             USFContext.getContext().usfCtx2RpcCtx(reply);
/*      */             
/* 1165 */             LogTraceUtil.moveTraceInfo((IMessage)message, reply);
/*      */             
/* 1167 */             BusConnector.this.server.sendOneWay(reply, this.clientId, remotingContext);
/*      */           }
/*      */           finally
/*      */           {
/* 1171 */             if (null != onServerSendTracker)
/*      */             {
/* 1173 */               ProcessDelayTracker.done(onServerSendTracker);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Exception e) {
/* 1179 */         if (BusConnector.LOGGER.isErrorEnable())
/*      */         {
/* 1181 */           BusConnector.LOGGER.error("Failed to run the task", e);
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 1186 */         context.done();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   class HeartBeatHandlerImpl
/*      */     implements HeartBeatHandler
/*      */   {
/*      */     private static final int MAGIC = -15;
/*      */     private static final int HEARTBEATLEN = 12;
/*      */     
/*      */     HeartBeatHandlerImpl() {}
/*      */     
/*      */     public Object getHeartBeatMessage(Config config)
/*      */     {
/* 1202 */       ProtoBuf buffer = ProtoBufAllocator.buffer(12);
/* 1203 */       buffer.writeInt(-15);
/* 1204 */       buffer.writeInt(8);
/* 1205 */       buffer.writeInt(2);
/* 1206 */       return buffer;
/*      */     }
/*      */     
/*      */ 
/*      */     public Object processHeartBeatRequest(Object object)
/*      */     {
/* 1212 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static class ConnectionStatusListenerImpl
/*      */     implements ConnectionStatusListener
/*      */   {
/* 1223 */     private static final DebugLog LOGGER = LogFactory.getDebugLog(ConnectionStatusListenerImpl.class);
/*      */     
/*      */ 
/*      */     private RemotingClient client;
/*      */     
/*      */     private BusConnector busConnector;
/*      */     
/*      */ 
/*      */     public ConnectionStatusListenerImpl(RemotingClient client, BusConnector busConnector)
/*      */     {
/* 1233 */       this.client = client;
/* 1234 */       this.busConnector = busConnector;
/*      */     }
/*      */     
/*      */ 
/*      */     public void statusChange(ConnectionStatusEvent event)
/*      */     {
/* 1240 */       if (LOGGER.isInfoEnable())
/*      */       {
/* 1242 */         LOGGER.info("remoting client status change: previous[" + event.getPreviousStatus() + "], current[" + event.getCurrentStatus() + "], clientId[" + this.client.getConfig().getConnectionID() + "], local[" + this.client.getConfig().getClientLocalHost() + ":" + this.client.getConfig().getLocalPort() + "], remote[" + this.client.getConfig().getRemoteHost() + ":" + this.client.getConfig().getRemotePort() + "]");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1252 */       String addr = this.client.getConfig().getRemoteHost() + ":" + this.client.getConfig().getRemotePort();
/*      */       
/* 1254 */       if (event.getCurrentStatus() == ConnectionStatus.BINDED)
/*      */       {
/*      */ 
/* 1257 */         this.busConnector.removeUnusableServer(addr);
/*      */         
/*      */ 
/* 1260 */         ServiceFrameworkEvent frameworkEvent = FrameworkEvent.creatEvent(null, null, null, Boolean.valueOf(true), null, null, null, null);
/*      */         
/*      */ 
/*      */         try
/*      */         {
/* 1265 */           ServiceFrameworkEventPublisher.publish("usf.framework.remoting.clientStatus.binded", frameworkEvent);
/*      */ 
/*      */ 
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/*      */ 
/* 1272 */           if (LOGGER.isErrorEnable())
/*      */           {
/* 1274 */             LOGGER.error("Excpetion occured while processing server usable event ", e);
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1283 */         ServiceFrameworkEvent frameworkEvent = FrameworkEvent.creatEvent(null, null, null, Boolean.valueOf(true), null, null, null, null);
/*      */         
/*      */ 
/*      */         try
/*      */         {
/* 1288 */           ServiceFrameworkEventPublisher.publish("usf.framework.remoting.clientStatus.error", frameworkEvent);
/*      */ 
/*      */ 
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/*      */ 
/* 1295 */           if (LOGGER.isErrorEnable())
/*      */           {
/* 1297 */             LOGGER.error("Excpetion occured while processing server usable event ", e);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1304 */         if (this.busConnector.isRemoteAllDisconnect(addr))
/*      */         {
/* 1306 */           this.busConnector.addUnusableServer(addr);
/*      */         }
/* 1308 */         if (!this.client.isConnected())
/*      */         {
/* 1310 */           for (Long iReqID : ((RemotingClientImpl)this.client).getReqIDs())
/*      */           {
/*      */             try
/*      */             {
/*      */ 
/* 1315 */               this.busConnector.getServiceEngine().getReplyInterceptor().onFaultMessage(iReqID.longValue());
/*      */ 
/*      */             }
/*      */             catch (Exception e)
/*      */             {
/*      */ 
/* 1321 */               LOGGER.error("failed to call ReplyInterceptor.onFaultMessage( " + iReqID + " ): ", e);
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 1326 */           ((RemotingClientImpl)this.client).getReqIDs().clear();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isRemoteAllDisconnect(String addr)
/*      */   {
/* 1335 */     Lock lock = this.clientLock.readLock();
/* 1336 */     if (!lock.tryLock())
/*      */     {
/* 1338 */       return false;
/*      */     }
/*      */     
/*      */     try
/*      */     {
/* 1343 */       ClientGroup clientGroup = (ClientGroup)this.clientPool.get(addr);
/* 1344 */       boolean bool; if ((clientGroup == null) || (clientGroup.isAllDisconnect()))
/*      */       {
/* 1346 */         return true;
/*      */       }
/* 1348 */       return false;
/*      */     }
/*      */     finally
/*      */     {
/* 1352 */       lock.unlock();
/*      */     }
/*      */   }
/*      */   
/*      */   private static class ServicePool implements ExecutorPool
/*      */   {
/* 1358 */     private ExecutePoolManager pManager = null;
/*      */     
/*      */     private AtomicLong ioInProcess;
/*      */     
/*      */     private ServicePool(ExecutePoolManager pManage, AtomicLong ioInProcess)
/*      */     {
/* 1364 */       this.pManager = pManage;
/* 1365 */       this.ioInProcess = ioInProcess;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void initilize(Config config) {}
/*      */     
/*      */ 
/*      */ 
/*      */     public void execute(Runnable task)
/*      */     {
/* 1376 */       MessageDecodeTask msgdecode = (MessageDecodeTask)task;
/* 1377 */       ProtoBuf buf = msgdecode.getMessage();
/* 1378 */       execute(task, buf);
/*      */     }
/*      */     
/*      */ 
/*      */     public void execute(Runnable task, ProtoBuf byteBuffer)
/*      */     {
/*      */       try
/*      */       {
/* 1386 */         this.ioInProcess.incrementAndGet();
/* 1387 */         ExecuteThreadPool executor = null;
/* 1388 */         if (this.pManager.isServicePool())
/*      */         {
/* 1390 */           String service = BusConnector.getServiceName(byteBuffer);
/* 1391 */           executor = this.pManager.getExecutor(service);
/*      */         }
/*      */         else
/*      */         {
/* 1395 */           executor = this.pManager.getSharedExecutor(null);
/*      */         }
/* 1397 */         if (null != executor)
/*      */         {
/*      */           try
/*      */           {
/* 1401 */             executor.execute(task);
/*      */           }
/*      */           catch (RejectedExecutionException rejectedEx)
/*      */           {
/* 1405 */             task.run();
/*      */           }
/*      */           
/*      */         }
/*      */         else {
/* 1410 */           task.run();
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 1415 */         this.ioInProcess.decrementAndGet();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void shutdown() {}
/*      */     
/*      */ 
/*      */ 
/*      */     public void shutdownNow() {}
/*      */   }
/*      */   
/*      */ 
/*      */   protected static class ClientPool
/*      */     implements ExecutorPool
/*      */   {
/* 1432 */     private ExecutePoolManager pManager = null;
/*      */     
/*      */     public ClientPool(ExecutePoolManager pManage)
/*      */     {
/* 1436 */       this.pManager = pManage;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void initilize(Config config) {}
/*      */     
/*      */ 
/*      */ 
/*      */     public void execute(Runnable task)
/*      */     {
/* 1447 */       ExecuteThreadPool threadPool = this.pManager.getResExecutorEx();
/* 1448 */       if (null != threadPool)
/*      */       {
/*      */         try
/*      */         {
/* 1452 */           threadPool.execute(task);
/*      */         }
/*      */         catch (RejectedExecutionException rejectedEx)
/*      */         {
/* 1456 */           task.run();
/*      */         }
/*      */         
/*      */       }
/*      */       else {
/* 1461 */         BusConnector.LOGGER.error("Could not obtain response execute thread pool, the task [" + task + "] can not be executed.");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void execute(Runnable task, ProtoBuf byteBuffer)
/*      */     {
/* 1470 */       execute(task);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void shutdown() {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void shutdownNow() {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getServiceName(ProtoBuf buf)
/*      */   {
/* 1492 */     int buflen = buf.readableBytes();
/*      */     
/* 1494 */     if (buflen <= 12)
/*      */     {
/* 1496 */       return "";
/*      */     }
/*      */     
/*      */ 
/* 1500 */     int type = buf.getInt(8);
/*      */     
/* 1502 */     if ((type == 2) || (type == 3) || (type == 4))
/*      */     {
/*      */ 
/* 1505 */       return "";
/*      */     }
/*      */     
/* 1508 */     int servicelen = buf.getInt(16);
/* 1509 */     byte[] contentBytes = new byte[servicelen];
/* 1510 */     buf.getBytes(20, contentBytes, 0, servicelen);
/*      */     
/* 1512 */     String serviceName = new String(contentBytes, Charset.forName("UTF-8"));
/*      */     
/* 1514 */     return serviceName;
/*      */   }
/*      */   
/*      */   public String getListenAddr()
/*      */   {
/* 1519 */     if (!this.startServerFlag)
/*      */     {
/* 1521 */       return null;
/*      */     }
/* 1523 */     return getServiceEngine().getSystemConfig().getRPCAddress(getServiceType());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ServiceType getServiceType()
/*      */   {
/* 1530 */     return ServiceType.USF;
/*      */   }
/*      */   
/*      */   public boolean isUnusableServerEmpty()
/*      */   {
/* 1535 */     return this.unusableServers.isEmpty();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isInUnusableServer(String addr)
/*      */   {
/* 1542 */     return this.unusableServers.contains(addr);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addUnusableServer(String addr)
/*      */   {
/* 1549 */     if (StringUtils.isEmpty(addr))
/*      */     {
/* 1551 */       return;
/*      */     }
/* 1553 */     synchronized (this.LOCK)
/*      */     {
/* 1555 */       Set<String> newSet = new HashSet(this.unusableServers);
/* 1556 */       boolean ret = newSet.add(addr);
/* 1557 */       if (ret)
/*      */       {
/* 1559 */         LOGGER.info("add " + addr + " to unusable servers.");
/*      */       }
/* 1561 */       this.unusableServers = newSet;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeUnusableServer(String addr)
/*      */   {
/* 1568 */     if (StringUtils.isEmpty(addr))
/*      */     {
/* 1570 */       return;
/*      */     }
/* 1572 */     synchronized (this.LOCK)
/*      */     {
/* 1574 */       Set<String> newSet = new HashSet(this.unusableServers);
/* 1575 */       boolean ret = newSet.remove(addr);
/* 1576 */       if (ret)
/*      */       {
/* 1578 */         LOGGER.info("remove " + addr + " from unusable servers.");
/*      */       }
/* 1580 */       this.unusableServers = newSet;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setServiceType(ServiceType serviceType) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isReadyToDestroy()
/*      */   {
/* 1596 */     if (!super.isReadyToDestroy())
/*      */     {
/* 1598 */       return false;
/*      */     }
/* 1600 */     if (this.ioInProcess.get() <= 0L)
/*      */     {
/* 1602 */       long now = System.currentTimeMillis();
/* 1603 */       if (this.ioFinishBegin <= 0L)
/*      */       {
/* 1605 */         this.ioFinishBegin = now;
/* 1606 */         return false;
/*      */       }
/* 1608 */       if (now - this.ioFinishBegin >= 1000L)
/*      */       {
/* 1610 */         return true;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1615 */       this.ioFinishBegin = -1L;
/*      */     }
/*      */     
/* 1618 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public void startTraceLog(IMessage message)
/*      */   {
/* 1624 */     LogTraceUtil.doServerStartTraceLog(message);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void asyncEndTraceLog(IMessage message) {}
/*      */   
/*      */ 
/*      */ 
/*      */   public void onServicePublish(List<ServiceInner> services)
/*      */   {
/* 1636 */     if (this.startServerFlag)
/*      */     {
/* 1638 */       return;
/*      */     }
/* 1640 */     synchronized (this.lock)
/*      */     {
/* 1642 */       if (this.startServerFlag)
/*      */       {
/* 1644 */         return;
/*      */       }
/* 1646 */       for (ServiceInner serviceInner : services)
/*      */       {
/* 1648 */         if (getServiceType().toString().equals(serviceInner.getServiceType()))
/*      */         {
/*      */           try
/*      */           {
/*      */ 
/* 1653 */             startServer();
/* 1654 */             return;
/*      */           }
/*      */           catch (Exception e)
/*      */           {
/* 1658 */             LOGGER.debug("failed to start server ", e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void doServerBegin(com.huawei.csc.usf.framework.Context context) {}
/*      */   
/*      */   protected void doServerEnd(com.huawei.csc.usf.framework.Context context) {}
/*      */   
/*      */   protected void doClientEnd(com.huawei.csc.usf.framework.Context context) {}
/*      */   
/*      */   protected void doClientBegin(com.huawei.csc.usf.framework.Context context) {}
/*      */   
/*      */   public void doHeadBegin(com.huawei.csc.usf.framework.Context context) {}
/*      */   
/*      */   public void doHeadEnd(com.huawei.csc.usf.framework.Context context) {}
/*      */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\bus\BusConnector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */