/*      */ package com.huawei.csc.usf.framework.config;
/*      */ 
/*      */ import com.huawei.csc.container.api.ContextRegistry;
/*      */ import com.huawei.csc.container.api.IContextHolder;
/*      */ import com.huawei.csc.kernel.api.log.LogFactory;
/*      */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*      */ import com.huawei.csc.kernel.api.uconfig.UConfigHelper;
/*      */ import com.huawei.csc.kernel.api.uconfig.UConfiguration;
/*      */ import com.huawei.csc.kernel.commons.util.ResourceResolverUtil;
/*      */ import com.huawei.csc.usf.framework.bus.BusConnector;
/*      */ import com.huawei.csc.usf.framework.exception.USFException;
/*      */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*      */ import com.huawei.csc.usf.framework.util.Utils;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import javax.xml.parsers.DocumentBuilder;
/*      */ import javax.xml.parsers.DocumentBuilderFactory;
/*      */ import org.apache.commons.lang.StringUtils;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DefaultSystemConfig
/*      */   implements SystemConfig
/*      */ {
/*   41 */   protected static final DebugLog DEBUGGER = LogFactory.getDebugLog(DefaultSystemConfig.class);
/*      */   
/*      */ 
/*   44 */   protected UConfiguration uconfig = UConfigHelper.getDefaultPropertyConfiguration("globalProperties");
/*      */   
/*      */ 
/*   47 */   private UConfiguration dynamiConfiguration = UConfigHelper.getDefaultPropertyConfiguration("dynamicProperties");
/*      */   
/*      */ 
/*   50 */   private static String RPC_ADDRESS = "rpc.address";
/*      */   
/*   52 */   private static String DSF_RPC_ADDRESS = "dsf.protocol.tcp.address";
/*      */   
/*   54 */   private static String EBUS_RPC_ADDRESS = "ebus.core.busListenAddress";
/*      */   
/*      */ 
/*      */ 
/*   58 */   private static String RPC_HEART_BEAT_INTERVAL = "rpc.heartbeat.interval";
/*      */   
/*   60 */   private static String RPC_HEART_BEAT_LOST_COUNT = "rpc.heartbeat.lost.count";
/*      */   
/*   62 */   private static String RPC_SERVER_IO_WORKERS = "rpc.server.io.workers";
/*      */   
/*   64 */   private static String RPC_SERVER_IO_QueueSize = "rpc.server.io.queue.size";
/*      */   
/*   66 */   private static String RPC_ASYN_WAIT_QueueSize = "rpc.client.async.wait.queues";
/*      */   
/*   68 */   private static String RPC_RECONNECT_INTERVAL = "rpc.reconnect.interval";
/*      */   
/*   70 */   private static String RPC_CHANNEL_NUM = "rpc.channel.num";
/*      */   
/*   72 */   private static String RPC_CLIENT_IO_WORKERS = "rpc.client.io.workers";
/*      */   
/*   74 */   private static String RPC_CLIENT_IO_QueueSize = "rpc.client.io.queue.size";
/*      */   
/*   76 */   private static String IP_WHITE_LIST = "ip.white.list";
/*      */   
/*   78 */   private static String IP_BLACK_LIST = "ip.black.list";
/*      */   
/*   80 */   private static String RPC_SERVICE_TIMEOUT = "usf.service.timeout";
/*      */   
/*   82 */   private static String DSF_RPC_HEART_BEAT_INTERVAL = "dsf.protocol.tcp.heartbeat.interval";
/*      */   
/*   84 */   private static String DSF_RPC_HEART_BEAT_LOST_COUNT = "dsf.protocol.tcp.heartbeat.lostcount";
/*      */   
/*   86 */   private static String DSF_RPC_SERVER_IO_WORKERS = "dsf.protocol.tcp.iothreads";
/*      */   
/*   88 */   private static String DSF_RPC_SERVER_IO_QueueSize = "dsf.protocol.tcp.queues";
/*      */   
/*   90 */   private static String DSF_RPC_RECONNECT_INTERVAL = "dsf.protocol.tcp.client.reconnect.interval";
/*      */   
/*   92 */   private static String DSF_RPC_CHANNEL_NUM = "dsf.protocol.tcp.client.group.size";
/*      */   
/*   94 */   private static String DSF_RPC_CLIENT_IO_WORKERS = "dsf.protocol.tcp.client.iothreads";
/*      */   
/*   96 */   private static String DSF_RPC_CLIENT_IO_QueueSize = "dsf.protocol.tcp.client.queues";
/*      */   
/*   98 */   private static String DSF_IP_WHITE_LIST = "dsf.protocol.tcp.iplimit.whitelist";
/*      */   
/*  100 */   private static String DSF_IP_BLACK_LIST = "dsf.protocol.tcp.iplimit.blacklist";
/*      */   
/*  102 */   private static String DSF_RPC_SERVICE_TIMEOUT = "dsf.service.timeout";
/*      */   
/*  104 */   private static String EBUS_RPC_SERVICE_TIMEOUT = "ebus.connector.integration.timeout";
/*      */   
/*  106 */   private static String DSF_DEFAULT_ROUTER = "dsf.router.default";
/*      */   
/*  108 */   private static String EBUS_RPC_HEART_BEAT_INTERVAL = "ebus.busconnector.heartbeat.cycle";
/*      */   
/*  110 */   private static String EBUS_IP_WHITE_LIST = "ebus.security.ip.white";
/*      */   
/*  112 */   private static String EBUS_IP_BLACK_LIST = "ebus.security.ip.black";
/*      */   
/*  114 */   private static String IP_ENABLED = "ebus.security.ip.enabled";
/*      */   
/*  116 */   private static String SYNCTOOLDEBUS = "dsf.sync2oldebus";
/*      */   
/*  118 */   private static String DSF_BIGNUMBER_MAX_DIGITS_SUPPORTED = "dsf.bignumber.max.digits.supported";
/*      */   
/*  120 */   private static String EBUS_CORE_BIGNUMBER_MAX_DIGITS_SUPPORTED = "ebus.core.bignumber.max.digits.supported";
/*      */   
/*  122 */   private static String DSF_TRANSFORMATION_USE_GLOBAL_REFERENCE = "dsf.transformation.use.global.reference";
/*      */   
/*  124 */   private static String EBUS_TRANSFORMATION_CONTEXT_USE_GLOBAL_REFERENCE = "ebus.transformation.context.use.global.reference";
/*      */   
/*  126 */   private static String EBUS_BUSCONNECTOR_VERSION = "ebus.busconnector.version";
/*      */   
/*      */ 
/*  129 */   private static String WORKER_CORE_SIZE = "worker.core.size";
/*      */   
/*  131 */   private static String WORKER_GROUP_SIZE = "worker.group.size";
/*      */   
/*  133 */   private static String WORKER_MAX_SIZE = "worker.max.size";
/*      */   
/*  135 */   private static String WORKER_QUEUES = "worker.queues";
/*      */   
/*  137 */   private static String RESPONSE_CORE_SIZE = "response.core.size";
/*      */   
/*  139 */   private static String RESPONSE_GROUP_SIZE = "response.group.size";
/*      */   
/*  141 */   private static String RESPONSE_MAX_SIZE = "response.max.size";
/*      */   
/*  143 */   private static String RESPONSE_QUEUES = "response.queues";
/*      */   
/*      */ 
/*  146 */   private static String DSF_WORKER_CORE_SIZE = "dsf.worker.core.size";
/*      */   
/*  148 */   private static String DSF_WORKER_GROUP_SIZE = "dsf.worker.group.size";
/*      */   
/*  150 */   private static String DSF_WORKER_MAX_SIZE = "dsf.worker.max.size";
/*      */   
/*  152 */   private static String DSF_WORKER_QUEUES = "dsf.worker.queues";
/*      */   
/*      */ 
/*  155 */   private static String EBUS_WORKER_CORE_SIZE = "ebus.busconnector.threads.core";
/*      */   
/*  157 */   private static String EBUS_WORKER_MAX_SIZE = "ebus.busconnector.threads.max";
/*      */   
/*  159 */   private static String EBUS_RESPONSE_CORE_SIZE = "ebus.busconnector.reply.threads.core";
/*      */   
/*  161 */   private static String EBUS_RESPONSE_MAX_SIZE = "ebus.busconnector.reply.threads.max";
/*      */   
/*  163 */   private static String USF_SERVICE_FAILPOLICY = "usf.service.failpolicy";
/*      */   
/*  165 */   private static String DSF_SERVICE_FAILPOLICY = "dsf.service.failpolicy";
/*      */   
/*  167 */   private static String USF_RESEND_TIMES = "usf.resend.times";
/*      */   
/*  169 */   private static String DSF_RESEND_TIMES = "dsf.resend.times";
/*      */   
/*      */ 
/*      */ 
/*  173 */   private static String CLIENT_LOGGIN_PASSWORD = "client.loggin.password";
/*      */   
/*  175 */   private static String SERVER_LOGGIN_PASSWORD = "server.loggin.password";
/*      */   
/*  177 */   private static String HTTP_ADDRESS = "http.address";
/*      */   
/*  179 */   private static String THREAD_POOL_MONITOR_SWITCH = "threadpool.monitor.switch";
/*      */   
/*  181 */   private static String THREAD_POOL_MONITOR_INTERVAL = "threadpool.monitor.interval";
/*      */   
/*  183 */   private static String SLOW_SERVICE_CLIENT_TIME = "slow.service.client.time";
/*      */   
/*  185 */   private static String SLOW_SERVICE_SERVER_TIME = "slow.service.server.time";
/*      */   
/*  187 */   private static String SLOW_SERVICE_OPEN = "slow.service.open";
/*      */   
/*  189 */   private static String AUTHENTICATION_ENABLE = "authentication.enable";
/*      */   
/*  191 */   private static String DSF_AUTHENTICATION_ENABLE = "dsf.authentication.enable";
/*      */   
/*  193 */   private static String DSF_CLIENT_AUTHENTICATION_ENABLE = "dsf.client.authentication.enable";
/*      */   
/*  195 */   private static String DSF_SERVER_AUTHENTICATION_ENABLE = "dsf.server.authentication.enable";
/*      */   
/*  197 */   private static String GRACEFUL_DESTROY_TIMEOUT = "graceful.destroy.timeout";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  202 */   private static String MONITOR_TIME_INTERVAL = "monitor.time.interval";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  207 */   private static String IO_STATISTICS_INTERVAL = "io.statistics.interval";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  212 */   private static String DSF_RPC_SERIALIZATION = "dsf.rpc.serialization";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  217 */   private static String SERVER_WEIGHT = "server.weight";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  222 */   private static String DSF_APPLICATION = "dsf.application";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  227 */   private static String EBUS_CONSUMER_GROUP_NAMES = "ebus.nodeinfo.consumer_group_list_name";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  232 */   private static String DSF_CONSUMER_GROUP_NAMES = "dsf.consumer.group";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  237 */   private static String EBUS_PROVIDER_GROUP_NAMES = "ebus.nodeinfo.provider_group_name";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  242 */   private static String DSF_PROVIDER_GROUP_NAMES = "dsf.provider.group";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  247 */   private static String SERVICE_PREFIX_NAME = "ebus.service.prefixName";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  252 */   private static String DEFAULT_ROUTER = "default.router";
/*      */   
/*  254 */   private static String CONNECTION_TIME_OUT = "connection.timeout";
/*      */   
/*  256 */   private static String SSL_ENABLE = "ssl.enable";
/*      */   
/*  258 */   private static String TRUST_STORE_PATH = "truststore.path";
/*      */   
/*  260 */   private static String KEY_STORE_PATH = "keystore.path";
/*      */   
/*  262 */   private static String KEY_STORE_PASSWORD = "keystore.password";
/*      */   
/*  264 */   private static String DSF_RPC_LOGIN_IDENTITY = "dsf.rpc.login.identity";
/*      */   
/*  266 */   private static String DSF_TRANSACTION_TIMEOUT = "dsf.transaction.timeout";
/*      */   
/*  268 */   private static String USF_STATISTIC_ENABLE = "dsf.statistic.enable";
/*      */   
/*  270 */   private static String USF_STATISTIC_TIME_DISTRIBUTION = "usf.statistic.time.distributing";
/*      */   
/*  272 */   private static String USF_STATISTIC_ENABLE_TIMESTAMP_DATA = "dsf.statistic.enable.timeStamp.data";
/*      */   
/*  274 */   private static String DEFAULT_TIME_DISTRIBUTION = "(S,50]:(50,300]:(300,500]:(500,1000]:(1000,M)";
/*      */   
/*  276 */   private static String SUPPORT_DELAY_REGISTER = "dsf.service.registry.delay";
/*      */   
/*  278 */   protected String DEFAULT_REGID = "dsf_default";
/*      */   
/*  280 */   private static String DSF_TPS_THRESHOLD = "dsf.tps.threshold";
/*      */   
/*      */ 
/*      */   private static final String DSF_CRICUTBREAKER_BULKHEAD_TIMEINMILLISECONDS = "dsf.bulkhead.timeInMilliseconds";
/*      */   
/*      */ 
/*      */   private static final String DSF_CRICUTBREAKER_BULKHEAD_NUMBEROFBUCKETS = "dsf.bulkhead.numberOfBuckets";
/*      */   
/*      */ 
/*      */   private static final String DSF_CRICUTBREAKER_BULKHEAD_MAXQUEUESIZE = "dsf.bulkhead.maxQueueSize";
/*      */   
/*      */ 
/*  292 */   private static String HTTP_CLIENT_SSL_CLIENT_AUTH = "http.client.ssl.needClientAuth";
/*      */   
/*  294 */   private static String HTTP_CLIENT_SSL_TRUSTSTORE_FILE = "http.client.ssl.truststorefile";
/*      */   
/*  296 */   private static String HTTP_CLIENT_SSL_TRUSTSTORE_PWD = "http.client.ssl.truststorepassword";
/*      */   
/*  298 */   private static String HTTP_CLIENT_SSL_KEYSTORE_FILE = "http.client.ssl.keystorefile";
/*      */   
/*  300 */   private static String HTTP_CLIENT_SSL_KEYSTORE_PWD = "http.client.ssl.keystorepassword";
/*      */   
/*  302 */   private static String HTTP_CLIENT_IP = "http.client.ip";
/*      */   
/*  304 */   private static String HTTP_CLIENT_SSL_ENABLE = "http.client.ssl.enable";
/*      */   
/*  306 */   private static String HTTP_CLIENT_MAX_THREADS = "http.client.max.threads";
/*      */   
/*  308 */   private static String DSF_REST_URL = "dsf.rest.exportUrl";
/*      */   
/*  310 */   private static String DSF_ZK_URL = "dsf.zk.server.url";
/*      */   
/*  312 */   private static String ZK_URL = "zk.server.url";
/*      */   
/*  314 */   private static String EBUS_ZK_URL = "ebus.zookeeper.server.connectString";
/*      */   
/*  316 */   private static String EBUS_ZK_OFF = "ebus.zookeeper.off";
/*      */   
/*  318 */   private static String CLIENT_MESSAGE_THRESHOLD = "client.message.threshold";
/*      */   
/*  320 */   private static String SERVER_MESSAGE_THRESHOLD = "server.message.threshold";
/*      */   
/*  322 */   private static String BIG_MESSAGE_OPEN = "big.message.open";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  328 */   protected String[] realListenAddress = new String[ServiceType.size()];
/*      */   
/*  330 */   protected Object lock = new Object();
/*      */   
/*  332 */   protected String validIpWhiteList = "";
/*      */   
/*  334 */   protected String validIpBlackList = "";
/*      */   
/*  336 */   private boolean hasDsfAdapter = true;
/*      */   
/*  338 */   private boolean hasEbusAdapter = true;
/*      */   
/*      */   private Boolean syncToOldEbus;
/*      */   
/*  342 */   private int timeout = 0;
/*      */   
/*  344 */   private String dsfApplication = null;
/*      */   
/*  346 */   private boolean timeStampEnabled = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  351 */   private String consumerGroupNames = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  356 */   private String providerGroupName = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  361 */   private String servicePrefixName = null;
/*      */   
/*  363 */   private String defaultZkUrl = null;
/*      */   
/*  365 */   private Integer defaultZkSessionTimeout = null;
/*      */   
/*  367 */   private String failPolicy = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public DefaultSystemConfig()
/*      */   {
/*      */     try
/*      */     {
/*  376 */       getClass().getClassLoader().loadClass("com.huawei.openas.dsf.DSFStartup");
/*      */ 
/*      */     }
/*      */     catch (ClassNotFoundException e)
/*      */     {
/*  381 */       this.hasDsfAdapter = false;
/*      */     }
/*      */     
/*      */     try
/*      */     {
/*  386 */       getClass().getClassLoader().loadClass("com.huawei.ebus.integration.BMEAdapter");
/*      */ 
/*      */     }
/*      */     catch (ClassNotFoundException e)
/*      */     {
/*  391 */       this.hasEbusAdapter = false;
/*      */     }
/*      */     
/*  394 */     DEBUGGER.info("hasDsfAdapter: " + this.hasDsfAdapter + ", hasEbusAdapter: " + this.hasEbusAdapter);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void init()
/*      */   {
/*  402 */     String usf_address = this.uconfig.getString(RPC_ADDRESS, "0:2618");
/*      */     
/*  404 */     this.realListenAddress[ServiceType.USF.toNumber()] = usf_address;
/*      */     
/*  406 */     if (this.hasDsfAdapter)
/*      */     {
/*      */ 
/*  409 */       String dsf_address = this.uconfig.getString(DSF_RPC_ADDRESS, "0:2617");
/*      */       
/*  411 */       this.realListenAddress[ServiceType.DSF.toNumber()] = dsf_address;
/*      */     }
/*  413 */     if (this.hasEbusAdapter)
/*      */     {
/*      */ 
/*      */ 
/*  417 */       this.realListenAddress[ServiceType.EBUS.toNumber()] = getAddr(EBUS_RPC_ADDRESS, "0:2619");
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*  423 */       this.realListenAddress[ServiceType.EBUS.toNumber()] = this.realListenAddress[ServiceType.DSF.toNumber()];
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  428 */     if (this.uconfig.containsKey(RPC_SERVICE_TIMEOUT))
/*      */     {
/*  430 */       this.timeout = this.uconfig.getInt(RPC_SERVICE_TIMEOUT);
/*      */     }
/*  432 */     else if ((this.hasDsfAdapter) && (this.uconfig.containsKey(DSF_RPC_SERVICE_TIMEOUT)))
/*      */     {
/*      */ 
/*  435 */       this.timeout = this.uconfig.getInt(DSF_RPC_SERVICE_TIMEOUT);
/*      */     }
/*  437 */     else if (this.hasEbusAdapter)
/*      */     {
/*  439 */       if (this.uconfig.containsKey(EBUS_RPC_SERVICE_TIMEOUT))
/*      */       {
/*  441 */         this.timeout = this.uconfig.getInt(EBUS_RPC_SERVICE_TIMEOUT);
/*      */       }
/*      */       else
/*      */       {
/*  445 */         this.timeout = 60000;
/*      */       }
/*      */       
/*      */     }
/*      */     else {
/*  450 */       this.timeout = 10000;
/*      */     }
/*      */     
/*      */ 
/*  454 */     if (this.uconfig.containsKey(DSF_PROVIDER_GROUP_NAMES))
/*      */     {
/*  456 */       this.providerGroupName = this.uconfig.getString(DSF_PROVIDER_GROUP_NAMES);
/*      */     }
/*  458 */     else if ((this.hasEbusAdapter) && (this.uconfig.containsKey(EBUS_PROVIDER_GROUP_NAMES)))
/*      */     {
/*      */ 
/*  461 */       this.providerGroupName = this.uconfig.getString(EBUS_PROVIDER_GROUP_NAMES);
/*      */     }
/*      */     
/*  464 */     if (this.uconfig.containsKey(DSF_CONSUMER_GROUP_NAMES))
/*      */     {
/*  466 */       this.consumerGroupNames = this.uconfig.getString(DSF_CONSUMER_GROUP_NAMES);
/*      */     }
/*  468 */     else if ((this.hasEbusAdapter) && (this.uconfig.containsKey(EBUS_CONSUMER_GROUP_NAMES)))
/*      */     {
/*      */ 
/*  471 */       this.consumerGroupNames = this.uconfig.getString(EBUS_CONSUMER_GROUP_NAMES);
/*      */     }
/*      */     
/*  474 */     this.dsfApplication = this.uconfig.getString(DSF_APPLICATION, Utils.getHostIp());
/*      */     
/*  476 */     this.timeStampEnabled = this.uconfig.getBoolean(USF_STATISTIC_ENABLE_TIMESTAMP_DATA, Boolean.FALSE).booleanValue();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  484 */     this.servicePrefixName = this.uconfig.getString(SERVICE_PREFIX_NAME, "");
/*      */     
/*  486 */     int bulkheadNum = getBulkheadNumberOfBuckets();
/*      */     
/*  488 */     if (0 == bulkheadNum)
/*      */     {
/*  490 */       String strTips = "The number of bucket can not be zero,Please the config dsf.bulkhead.numberOfBuckets";
/*  491 */       DEBUGGER.error(strTips);
/*  492 */       throw new USFException(strTips);
/*      */     }
/*      */     
/*  495 */     this.failPolicy = getDsfFailPolicy();
/*      */     
/*  497 */     if (this.failPolicy.equalsIgnoreCase("failFast"))
/*      */     {
/*  499 */       this.failPolicy = "failFast";
/*      */     }
/*  501 */     else if (this.failPolicy.equalsIgnoreCase("failOver"))
/*      */     {
/*  503 */       this.failPolicy = "failOver";
/*      */     }
/*  505 */     else if (this.failPolicy.equalsIgnoreCase("failResend"))
/*      */     {
/*  507 */       this.failPolicy = "failResend";
/*      */     }
/*      */     else
/*      */     {
/*  511 */       this.failPolicy = "failFast";
/*      */     }
/*  513 */     this.defaultZkUrl = getDefaultZkUrl();
/*      */   }
/*      */   
/*      */   private String getAddr(String configName, String defaultAddr)
/*      */   {
/*  518 */     String addr = null;
/*  519 */     String ipAddr = this.uconfig.getString(configName, defaultAddr);
/*  520 */     InetSocketAddress socketListenAddress = Utils.parseIpPortToAddress(ipAddr);
/*      */     
/*  522 */     if (null != socketListenAddress)
/*      */     {
/*  524 */       addr = socketListenAddress.getAddress().getHostAddress() + ":" + socketListenAddress.getPort();
/*      */     }
/*      */     
/*  527 */     return addr;
/*      */   }
/*      */   
/*      */ 
/*      */   @Deprecated
/*      */   public String getRPCAddress()
/*      */   {
/*  534 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getRPCAddress(ServiceType serviceType)
/*      */   {
/*  540 */     String rpcAddr = getRPCAddress();
/*  541 */     if (rpcAddr != null)
/*      */     {
/*  543 */       return rpcAddr;
/*      */     }
/*      */     
/*  546 */     return this.realListenAddress[serviceType.toNumber()];
/*      */   }
/*      */   
/*      */ 
/*      */   public int getRPCHeartBeatInterval()
/*      */   {
/*  552 */     if (this.uconfig.containsKey(RPC_HEART_BEAT_INTERVAL))
/*      */     {
/*  554 */       return this.uconfig.getInt(RPC_HEART_BEAT_INTERVAL);
/*      */     }
/*  556 */     if ((this.hasDsfAdapter) && (this.uconfig.containsKey(DSF_RPC_HEART_BEAT_INTERVAL)))
/*      */     {
/*      */ 
/*  559 */       return this.uconfig.getInt(DSF_RPC_HEART_BEAT_INTERVAL);
/*      */     }
/*  561 */     if ((this.hasEbusAdapter) && (this.uconfig.containsKey(EBUS_RPC_HEART_BEAT_INTERVAL)))
/*      */     {
/*      */ 
/*  564 */       return this.uconfig.getInt(EBUS_RPC_HEART_BEAT_INTERVAL) / 1000;
/*      */     }
/*  566 */     return 10;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getRPCHeartBeatMaxLostCount()
/*      */   {
/*  572 */     if (this.uconfig.containsKey(RPC_HEART_BEAT_LOST_COUNT))
/*      */     {
/*  574 */       return this.uconfig.getInt(RPC_HEART_BEAT_LOST_COUNT);
/*      */     }
/*  576 */     if ((this.hasDsfAdapter) && (this.uconfig.containsKey(DSF_RPC_HEART_BEAT_LOST_COUNT)))
/*      */     {
/*      */ 
/*  579 */       return this.uconfig.getInt(DSF_RPC_HEART_BEAT_LOST_COUNT);
/*      */     }
/*      */     
/*  582 */     return 5;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getRPCServerIOWorkers()
/*      */   {
/*  588 */     if (this.uconfig.containsKey(RPC_SERVER_IO_WORKERS))
/*      */     {
/*  590 */       return this.uconfig.getInt(RPC_SERVER_IO_WORKERS);
/*      */     }
/*  592 */     if ((this.hasDsfAdapter) && (this.uconfig.containsKey(DSF_RPC_SERVER_IO_WORKERS)))
/*      */     {
/*      */ 
/*  595 */       return this.uconfig.getInt(DSF_RPC_SERVER_IO_WORKERS);
/*      */     }
/*      */     
/*  598 */     return 10;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getRPCServerIOQueueSize()
/*      */   {
/*  604 */     if (this.uconfig.containsKey(RPC_SERVER_IO_QueueSize))
/*      */     {
/*  606 */       return this.uconfig.getInt(RPC_SERVER_IO_QueueSize);
/*      */     }
/*  608 */     if ((this.hasDsfAdapter) && (this.uconfig.containsKey(DSF_RPC_SERVER_IO_QueueSize)))
/*      */     {
/*      */ 
/*  611 */       return this.uconfig.getInt(DSF_RPC_SERVER_IO_QueueSize);
/*      */     }
/*      */     
/*  614 */     return 100000;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getRPCAsynWaitQueueSize()
/*      */   {
/*  620 */     if (this.uconfig.containsKey(RPC_ASYN_WAIT_QueueSize))
/*      */     {
/*  622 */       return this.uconfig.getLong(RPC_ASYN_WAIT_QueueSize);
/*      */     }
/*  624 */     if ((this.hasDsfAdapter) && (this.uconfig.containsKey(RPC_ASYN_WAIT_QueueSize)))
/*      */     {
/*      */ 
/*  627 */       return this.uconfig.getLong(RPC_ASYN_WAIT_QueueSize);
/*      */     }
/*      */     
/*  630 */     return 1000000L;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getRPCClientReconnectInterval()
/*      */   {
/*  636 */     if (this.uconfig.containsKey(RPC_RECONNECT_INTERVAL))
/*      */     {
/*  638 */       return this.uconfig.getInt(RPC_RECONNECT_INTERVAL);
/*      */     }
/*  640 */     if ((this.hasDsfAdapter) && (this.uconfig.containsKey(DSF_RPC_RECONNECT_INTERVAL)))
/*      */     {
/*      */ 
/*  643 */       return this.uconfig.getInt(DSF_RPC_RECONNECT_INTERVAL);
/*      */     }
/*      */     
/*  646 */     return 10;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getRPCChannelNum()
/*      */   {
/*  652 */     if (this.uconfig.containsKey(RPC_CHANNEL_NUM))
/*      */     {
/*  654 */       return this.uconfig.getInt(RPC_CHANNEL_NUM);
/*      */     }
/*  656 */     if ((this.hasDsfAdapter) && (this.uconfig.containsKey(DSF_RPC_CHANNEL_NUM)))
/*      */     {
/*  658 */       return this.uconfig.getInt(DSF_RPC_CHANNEL_NUM);
/*      */     }
/*      */     
/*  661 */     return 10;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getRPCClientIOWorkers()
/*      */   {
/*  667 */     if (this.uconfig.containsKey(RPC_CLIENT_IO_WORKERS))
/*      */     {
/*  669 */       return this.uconfig.getInt(RPC_CLIENT_IO_WORKERS);
/*      */     }
/*  671 */     if ((this.hasDsfAdapter) && (this.uconfig.containsKey(DSF_RPC_CLIENT_IO_WORKERS)))
/*      */     {
/*      */ 
/*  674 */       return this.uconfig.getInt(DSF_RPC_CLIENT_IO_WORKERS);
/*      */     }
/*      */     
/*  677 */     return 10;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getRPCClientIOQueueSize()
/*      */   {
/*  683 */     if (this.uconfig.containsKey(RPC_CLIENT_IO_QueueSize))
/*      */     {
/*  685 */       return this.uconfig.getInt(RPC_CLIENT_IO_QueueSize);
/*      */     }
/*  687 */     if ((this.hasDsfAdapter) && (this.uconfig.containsKey(DSF_RPC_CLIENT_IO_QueueSize)))
/*      */     {
/*      */ 
/*  690 */       return this.uconfig.getInt(DSF_RPC_CLIENT_IO_QueueSize);
/*      */     }
/*      */     
/*  693 */     return 100000;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getWorkerCoreSize()
/*      */   {
/*  699 */     if (this.uconfig.containsKey(WORKER_CORE_SIZE))
/*      */     {
/*  701 */       return this.uconfig.getInt(WORKER_CORE_SIZE);
/*      */     }
/*  703 */     if ((this.hasDsfAdapter) && (this.uconfig.containsKey(DSF_WORKER_CORE_SIZE)))
/*      */     {
/*      */ 
/*  706 */       return this.uconfig.getInt(DSF_WORKER_CORE_SIZE);
/*      */     }
/*  708 */     if ((this.hasEbusAdapter) && (this.uconfig.containsKey(EBUS_WORKER_CORE_SIZE)))
/*      */     {
/*      */ 
/*  711 */       return this.uconfig.getInt(EBUS_WORKER_CORE_SIZE);
/*      */     }
/*  713 */     return 8;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getWorkerGroupSize()
/*      */   {
/*  719 */     if (this.uconfig.containsKey(WORKER_GROUP_SIZE))
/*      */     {
/*  721 */       return this.uconfig.getInt(WORKER_GROUP_SIZE);
/*      */     }
/*  723 */     if ((this.hasDsfAdapter) && (this.uconfig.containsKey(DSF_WORKER_GROUP_SIZE)))
/*      */     {
/*      */ 
/*  726 */       return this.uconfig.getInt(DSF_WORKER_GROUP_SIZE);
/*      */     }
/*  728 */     return 4;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getWorkerMaxSize()
/*      */   {
/*  734 */     if (this.uconfig.containsKey(WORKER_MAX_SIZE))
/*      */     {
/*  736 */       return this.uconfig.getInt(WORKER_MAX_SIZE);
/*      */     }
/*  738 */     if ((this.hasDsfAdapter) && (this.uconfig.containsKey(DSF_WORKER_MAX_SIZE)))
/*      */     {
/*  740 */       return this.uconfig.getInt(DSF_WORKER_MAX_SIZE);
/*      */     }
/*  742 */     if ((this.hasEbusAdapter) && (this.uconfig.containsKey(EBUS_WORKER_MAX_SIZE)))
/*      */     {
/*      */ 
/*  745 */       return this.uconfig.getInt(EBUS_WORKER_MAX_SIZE);
/*      */     }
/*  747 */     return 15;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getWorkerQueues()
/*      */   {
/*  753 */     if (this.uconfig.containsKey(WORKER_QUEUES))
/*      */     {
/*  755 */       return this.uconfig.getInt(WORKER_QUEUES);
/*      */     }
/*  757 */     if ((this.hasDsfAdapter) && (this.uconfig.containsKey(DSF_WORKER_QUEUES)))
/*      */     {
/*  759 */       return this.uconfig.getInt(DSF_WORKER_QUEUES);
/*      */     }
/*  761 */     return 100000;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getResponseCoreSize()
/*      */   {
/*  767 */     if (this.uconfig.containsKey(RESPONSE_CORE_SIZE))
/*      */     {
/*  769 */       return this.uconfig.getInt(RESPONSE_CORE_SIZE);
/*      */     }
/*  771 */     if ((this.hasEbusAdapter) && (this.uconfig.containsKey(EBUS_RESPONSE_CORE_SIZE)))
/*      */     {
/*      */ 
/*  774 */       return this.uconfig.getInt(EBUS_RESPONSE_CORE_SIZE);
/*      */     }
/*  776 */     return 6;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getResponseGroupSize()
/*      */   {
/*  782 */     if (this.uconfig.containsKey(RESPONSE_GROUP_SIZE))
/*      */     {
/*  784 */       return this.uconfig.getInt(RESPONSE_GROUP_SIZE);
/*      */     }
/*  786 */     return 1;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getResponseMaxSize()
/*      */   {
/*  792 */     if (this.uconfig.containsKey(RESPONSE_MAX_SIZE))
/*      */     {
/*  794 */       return this.uconfig.getInt(RESPONSE_MAX_SIZE);
/*      */     }
/*  796 */     if ((this.hasEbusAdapter) && (this.uconfig.containsKey(EBUS_RESPONSE_MAX_SIZE)))
/*      */     {
/*      */ 
/*  799 */       return this.uconfig.getInt(EBUS_RESPONSE_MAX_SIZE);
/*      */     }
/*  801 */     return 15;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getResponseQueues()
/*      */   {
/*  807 */     if (this.uconfig.containsKey(RESPONSE_QUEUES))
/*      */     {
/*  809 */       return this.uconfig.getInt(RESPONSE_QUEUES);
/*      */     }
/*  811 */     return 100000;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getWhiteList()
/*      */   {
/*  817 */     if (!StringUtils.isEmpty(this.validIpWhiteList))
/*      */     {
/*  819 */       return this.validIpWhiteList;
/*      */     }
/*  821 */     String ipWhiteList = "";
/*  822 */     if (this.uconfig.containsKey(IP_WHITE_LIST))
/*      */     {
/*  824 */       ipWhiteList = this.uconfig.getString(IP_WHITE_LIST);
/*  825 */       this.validIpWhiteList = Utils.parseIpList(ipWhiteList);
/*      */ 
/*      */     }
/*  828 */     else if ((this.hasDsfAdapter) && (this.uconfig.containsKey(DSF_IP_WHITE_LIST)))
/*      */     {
/*  830 */       ipWhiteList = this.uconfig.getString(DSF_IP_WHITE_LIST);
/*      */       
/*  832 */       this.validIpWhiteList = Utils.parseIpList(ipWhiteList);
/*      */ 
/*      */     }
/*  835 */     else if ((this.hasEbusAdapter) && (this.uconfig.containsKey(EBUS_IP_WHITE_LIST)))
/*      */     {
/*  837 */       ipWhiteList = this.uconfig.getString(EBUS_IP_WHITE_LIST);
/*  838 */       this.validIpWhiteList = Utils.parseEbusIpList(ipWhiteList);
/*      */     }
/*      */     
/*      */ 
/*  842 */     return this.validIpWhiteList;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getBlackList()
/*      */   {
/*  848 */     if (!StringUtils.isEmpty(this.validIpBlackList))
/*      */     {
/*  850 */       return this.validIpBlackList;
/*      */     }
/*  852 */     String ipBlackList = "";
/*  853 */     if (this.uconfig.containsKey(IP_BLACK_LIST))
/*      */     {
/*  855 */       ipBlackList = this.uconfig.getString(IP_BLACK_LIST);
/*  856 */       this.validIpBlackList = Utils.parseIpList(ipBlackList);
/*      */     }
/*  858 */     else if ((this.hasDsfAdapter) && (this.uconfig.containsKey(DSF_IP_BLACK_LIST)))
/*      */     {
/*  860 */       ipBlackList = this.uconfig.getString(DSF_IP_BLACK_LIST);
/*  861 */       this.validIpBlackList = Utils.parseIpList(ipBlackList);
/*      */     }
/*  863 */     else if ((this.hasEbusAdapter) && (this.uconfig.containsKey(EBUS_IP_BLACK_LIST)))
/*      */     {
/*  865 */       ipBlackList = this.uconfig.getString(EBUS_IP_BLACK_LIST);
/*  866 */       this.validIpBlackList = Utils.parseEbusIpList(ipBlackList);
/*      */     }
/*      */     
/*  869 */     return this.validIpBlackList;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTimeout()
/*      */   {
/*  875 */     return this.timeout;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getBigNumberMaxLength()
/*      */   {
/*  881 */     if (this.uconfig.containsKey(EBUS_CORE_BIGNUMBER_MAX_DIGITS_SUPPORTED))
/*  882 */       return this.uconfig.getString(EBUS_CORE_BIGNUMBER_MAX_DIGITS_SUPPORTED);
/*  883 */     if (this.uconfig.containsKey(DSF_BIGNUMBER_MAX_DIGITS_SUPPORTED))
/*  884 */       return this.uconfig.getString(DSF_BIGNUMBER_MAX_DIGITS_SUPPORTED);
/*  885 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getTransformerGlobalRef()
/*      */   {
/*  891 */     if (this.uconfig.containsKey(EBUS_TRANSFORMATION_CONTEXT_USE_GLOBAL_REFERENCE))
/*      */     {
/*  893 */       return this.uconfig.getBoolean(EBUS_TRANSFORMATION_CONTEXT_USE_GLOBAL_REFERENCE);
/*      */     }
/*  895 */     if (this.uconfig.containsKey(DSF_TRANSFORMATION_USE_GLOBAL_REFERENCE))
/*  896 */       return this.uconfig.getBoolean(DSF_TRANSFORMATION_USE_GLOBAL_REFERENCE);
/*  897 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getEbusBusConnectorVersion()
/*      */   {
/*  903 */     return this.uconfig.getString(EBUS_BUSCONNECTOR_VERSION, "2.0");
/*      */   }
/*      */   
/*      */ 
/*      */   public String getRestAddress()
/*      */   {
/*  909 */     return this.uconfig.getString(HTTP_ADDRESS, "0:8080");
/*      */   }
/*      */   
/*      */ 
/*      */   public String getDsfRpcSerialization()
/*      */   {
/*  915 */     return this.uconfig.getString(DSF_RPC_SERIALIZATION, "protostuff");
/*      */   }
/*      */   
/*      */ 
/*      */   public String getClientLogginPassword()
/*      */   {
/*  921 */     return this.uconfig.getString(CLIENT_LOGGIN_PASSWORD, "");
/*      */   }
/*      */   
/*      */ 
/*      */   public String getServerLogginPassword()
/*      */   {
/*  927 */     return this.uconfig.getString(SERVER_LOGGIN_PASSWORD, "");
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getThreadPoolMonitorSwitch()
/*      */   {
/*  933 */     return this.uconfig.getBoolean(THREAD_POOL_MONITOR_SWITCH, true);
/*      */   }
/*      */   
/*      */ 
/*      */   public long getThreadPoolMonitorInterval()
/*      */   {
/*  939 */     return this.uconfig.getLong(THREAD_POOL_MONITOR_INTERVAL, 60L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getSlowServiceClientTime()
/*      */   {
/*  948 */     return this.uconfig.getLong(SLOW_SERVICE_CLIENT_TIME, 2000L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isSlowServiceOpen()
/*      */   {
/*  955 */     return this.uconfig.getBoolean(SLOW_SERVICE_OPEN, true);
/*      */   }
/*      */   
/*      */ 
/*      */   public long getSlowServiceServerTime()
/*      */   {
/*  961 */     return this.uconfig.getLong(SLOW_SERVICE_SERVER_TIME, 1000L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public boolean isAuthenticationEnable()
/*      */   {
/*  969 */     if (this.uconfig.containsKey(DSF_AUTHENTICATION_ENABLE))
/*      */     {
/*  971 */       return this.uconfig.getBoolean(DSF_AUTHENTICATION_ENABLE);
/*      */     }
/*      */     
/*      */ 
/*  975 */     return this.uconfig.getBoolean(AUTHENTICATION_ENABLE, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long getGracefulDestroyTimeout()
/*      */   {
/*  982 */     return this.uconfig.getLong(GRACEFUL_DESTROY_TIMEOUT, 60000L);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getDelayTimeInterval()
/*      */   {
/*  988 */     return this.uconfig.getInt(MONITOR_TIME_INTERVAL, 10000);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getIoStatisticsInterval()
/*      */   {
/*  994 */     return this.uconfig.getInt(IO_STATISTICS_INTERVAL, 0);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getDefaultRouter()
/*      */   {
/* 1000 */     if (this.uconfig.containsKey(DEFAULT_ROUTER))
/*      */     {
/* 1002 */       return this.uconfig.getString(DEFAULT_ROUTER, "poll");
/*      */     }
/* 1004 */     if ((this.hasDsfAdapter) && (this.uconfig.containsKey(DSF_DEFAULT_ROUTER)))
/*      */     {
/* 1006 */       return this.uconfig.getString(DSF_DEFAULT_ROUTER, "poll");
/*      */     }
/* 1008 */     return "poll";
/*      */   }
/*      */   
/*      */ 
/*      */   public int getServerWeight()
/*      */   {
/* 1014 */     int weight = this.uconfig.getInt(SERVER_WEIGHT, 10);
/* 1015 */     return weight < 1 ? 10 : weight;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getServerFailPolicy()
/*      */   {
/* 1021 */     return this.failPolicy;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getResendTimes()
/*      */   {
/* 1027 */     if (this.uconfig.containsKey(USF_RESEND_TIMES))
/*      */     {
/* 1029 */       return this.uconfig.getInt(USF_RESEND_TIMES);
/*      */     }
/* 1031 */     if (this.uconfig.containsKey(DSF_RESEND_TIMES))
/*      */     {
/* 1033 */       return this.uconfig.getInt(DSF_RESEND_TIMES);
/*      */     }
/* 1035 */     return 2;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean saveProperty(String key, Object value)
/*      */   {
/*      */     try
/*      */     {
/* 1045 */       if (this.dynamiConfiguration.containsKey(key))
/*      */       {
/* 1047 */         this.dynamiConfiguration.setProperty(key, value);
/*      */       }
/*      */       else
/*      */       {
/* 1051 */         this.dynamiConfiguration.addProperty(key, value);
/*      */       }
/* 1053 */       this.dynamiConfiguration.save();
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/* 1057 */       DEBUGGER.error("Dynamic configuration save failed.", e);
/* 1058 */       return false;
/*      */     }
/* 1060 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isKeyExist(String key)
/*      */   {
/* 1072 */     return (this.dynamiConfiguration.containsKey(key)) || (this.uconfig.containsKey(key));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getConnectionTimeout()
/*      */   {
/* 1083 */     if (this.uconfig.containsKey(CONNECTION_TIME_OUT))
/*      */     {
/* 1085 */       return this.uconfig.getInt(CONNECTION_TIME_OUT);
/*      */     }
/* 1087 */     return 300;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getDsfApplication()
/*      */   {
/* 1093 */     return this.dsfApplication;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isSslEnable()
/*      */   {
/* 1099 */     return this.uconfig.getBoolean(SSL_ENABLE, false);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getTrustStorePath()
/*      */   {
/* 1105 */     return this.uconfig.getString(TRUST_STORE_PATH, "");
/*      */   }
/*      */   
/*      */ 
/*      */   public String getKeyStorePath()
/*      */   {
/* 1111 */     return this.uconfig.getString(KEY_STORE_PATH, "");
/*      */   }
/*      */   
/*      */ 
/*      */   public String getKeyStorePassword()
/*      */   {
/* 1117 */     return this.uconfig.getString(KEY_STORE_PASSWORD, "");
/*      */   }
/*      */   
/*      */ 
/*      */   public String getRpcLoginIdentity()
/*      */   {
/* 1123 */     return this.uconfig.getString(DSF_RPC_LOGIN_IDENTITY, "");
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTransactionTimeout()
/*      */   {
/* 1129 */     return this.uconfig.getInt(DSF_TRANSACTION_TIMEOUT, 10);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getDimensionKeyFormat(String key)
/*      */   {
/* 1135 */     return this.uconfig.getString(key);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getUsfTimeDistribution()
/*      */   {
/* 1141 */     return this.uconfig.getString(USF_STATISTIC_TIME_DISTRIBUTION, DEFAULT_TIME_DISTRIBUTION);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean getEnableStatisic()
/*      */   {
/* 1148 */     return this.uconfig.getBoolean(USF_STATISTIC_ENABLE, Boolean.TRUE).booleanValue();
/*      */   }
/*      */   
/*      */   public boolean getIsDelayRegister()
/*      */   {
/* 1153 */     return this.uconfig.getBoolean(SUPPORT_DELAY_REGISTER, Boolean.FALSE).booleanValue();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getTimeStampEnabled()
/*      */   {
/* 1159 */     return this.timeStampEnabled;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getConsumerGroupNames()
/*      */   {
/* 1165 */     return this.consumerGroupNames;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getProviderGroupName()
/*      */   {
/* 1171 */     return this.providerGroupName;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getServicePrefixName()
/*      */   {
/* 1177 */     return this.servicePrefixName;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getDsfTpsThreshold()
/*      */   {
/* 1183 */     return this.uconfig.getString(DSF_TPS_THRESHOLD, null);
/*      */   }
/*      */   
/*      */ 
/*      */   private String getDefaultAuthSwitchKey()
/*      */   {
/* 1189 */     String key = "dsf.zk.auth.switch";
/* 1190 */     if (this.uconfig.containsKey(key))
/*      */     {
/* 1192 */       return this.uconfig.getString(key, "off");
/*      */     }
/*      */     
/*      */ 
/* 1196 */     key = "zk.auth.switch";
/* 1197 */     if (this.uconfig.containsKey(key))
/*      */     {
/* 1199 */       return this.uconfig.getString(key, "off");
/*      */     }
/*      */     
/* 1202 */     return "off";
/*      */   }
/*      */   
/*      */ 
/*      */   public String getAuthSwitchKey(String regId)
/*      */   {
/* 1208 */     if (regId.equals(this.DEFAULT_REGID))
/*      */     {
/* 1210 */       return getDefaultAuthSwitchKey();
/*      */     }
/* 1212 */     String key = "dsf.zk." + regId + "auth.switch";
/* 1213 */     String authSwitch = this.uconfig.getString(key, "off");
/* 1214 */     return authSwitch;
/*      */   }
/*      */   
/*      */ 
/*      */   public List<String> getZkUrlKeys()
/*      */   {
/* 1220 */     List<String> zkUrlKeys = new ArrayList();
/* 1221 */     Iterator<?> iter = this.uconfig.getKeys();
/* 1222 */     while (iter.hasNext())
/*      */     {
/* 1224 */       String key = (String)iter.next();
/* 1225 */       if ((key.contains("server.url")) && (key.contains("zk.")))
/*      */       {
/* 1227 */         zkUrlKeys.add(key);
/*      */       }
/*      */     }
/* 1230 */     if ((!zkUrlKeys.contains("dsf.zk.server.url")) && (!zkUrlKeys.contains("zk.server.url")))
/*      */     {
/*      */ 
/*      */ 
/* 1234 */       zkUrlKeys.add("dsf.zk.server.url");
/*      */     }
/* 1236 */     else if (zkUrlKeys.contains("dsf.zk.server.url"))
/*      */     {
/* 1238 */       zkUrlKeys.remove("zk.server.url");
/*      */     }
/* 1240 */     return zkUrlKeys;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isZkOff(String regId)
/*      */   {
/*      */     String zkOff;
/*      */     
/* 1248 */     if ((!Utils.isEmpty(regId)) && (!regId.equals(this.DEFAULT_REGID)))
/*      */     {
/* 1250 */       String key = "dsf.zk." + regId + ".off";
/* 1251 */       String zkOff; if (this.uconfig.containsKey(key))
/*      */       {
/* 1253 */         zkOff = this.uconfig.getString(key).trim();
/*      */       }
/*      */       else
/*      */       {
/* 1257 */         return false;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1262 */       String key = "dsf.zk.off";
/* 1263 */       String defaultKey = "zk.off";
/* 1264 */       String zkOff; if (this.uconfig.containsKey(key))
/*      */       {
/* 1266 */         zkOff = this.uconfig.getString(key).trim();
/*      */       } else { String zkOff;
/* 1268 */         if (this.uconfig.containsKey(defaultKey))
/*      */         {
/* 1270 */           zkOff = this.uconfig.getString(defaultKey).trim();
/*      */         } else { String zkOff;
/* 1272 */           if (this.uconfig.containsKey(EBUS_ZK_OFF))
/*      */           {
/* 1274 */             zkOff = this.uconfig.getString(EBUS_ZK_OFF).trim();
/*      */           }
/*      */           else
/*      */           {
/* 1278 */             return false; }
/*      */         } } }
/*      */     String zkOff;
/* 1281 */     if (Utils.isEmpty(zkOff))
/*      */     {
/* 1283 */       return false;
/*      */     }
/*      */     
/*      */ 
/* 1287 */     return Boolean.parseBoolean(zkOff);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getXmlConfig(String xmlAttribute)
/*      */   {
/* 1293 */     URL[] urls = ResourceResolverUtil.getResources("classpath*:openas.cluster.xml");
/*      */     
/* 1295 */     if ((urls == null) || (urls.length == 0))
/*      */     {
/* 1297 */       DEBUGGER.warn("can't find openas.cluster.xml file.");
/* 1298 */       return null;
/*      */     }
/* 1300 */     String zkName = getDsfZkName();
/* 1301 */     InputStream is = null;
/*      */     try
/*      */     {
/* 1304 */       is = urls[0].openStream();
/*      */       
/*      */ 
/* 1307 */       DocumentBuilder dombuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
/*      */       
/* 1309 */       Document doc = dombuilder.parse(is);
/* 1310 */       NodeList connectionNodeLst = doc.getElementsByTagName("connector");
/* 1311 */       for (int i = 0; i < connectionNodeLst.getLength(); i++)
/*      */       {
/* 1313 */         Node node = connectionNodeLst.item(i);
/* 1314 */         if (node.getNodeType() == 1)
/*      */         {
/* 1316 */           Element connectionElement = (Element)node;
/* 1317 */           String Name = connectionElement.getAttribute("name");
/* 1318 */           if ((!StringUtils.isEmpty(Name)) && (Name.equals(zkName)))
/*      */           {
/* 1320 */             String value = connectionElement.getAttribute(xmlAttribute).trim();
/*      */             
/* 1322 */             return value;
/*      */           }
/*      */         }
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
/* 1347 */       return null;
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/* 1330 */       DEBUGGER.warn("load config fail", e);
/*      */     }
/*      */     finally
/*      */     {
/* 1334 */       if (is != null)
/*      */       {
/*      */         try
/*      */         {
/* 1338 */           is.close();
/*      */         }
/*      */         catch (IOException e)
/*      */         {
/* 1342 */           DEBUGGER.warn("close IOStream failed", e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getDefaultZkUrl()
/*      */   {
/* 1354 */     if (this.defaultZkUrl == null)
/*      */     {
/* 1356 */       if (this.uconfig.containsKey(DSF_ZK_URL))
/*      */       {
/* 1358 */         this.defaultZkUrl = Utils.parseZkUrl(this.uconfig, DSF_ZK_URL);
/*      */       }
/* 1360 */       else if (this.uconfig.containsKey(ZK_URL))
/*      */       {
/* 1362 */         this.defaultZkUrl = Utils.parseZkUrl(this.uconfig, ZK_URL);
/*      */       }
/* 1364 */       else if (this.uconfig.containsKey(EBUS_ZK_URL))
/*      */       {
/* 1366 */         this.defaultZkUrl = Utils.parseZkUrl(this.uconfig, EBUS_ZK_URL);
/*      */       }
/*      */       else
/*      */       {
/* 1370 */         this.defaultZkUrl = getXmlConfig("url");
/* 1371 */         if (this.defaultZkUrl == null)
/*      */         {
/* 1373 */           this.defaultZkUrl = "127.0.0.1:2181";
/*      */         }
/*      */       }
/*      */     }
/* 1377 */     return this.defaultZkUrl;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getZkUrl(String regId)
/*      */   {
/* 1384 */     if (regId.equals(this.DEFAULT_REGID))
/*      */     {
/* 1386 */       return getDefaultZkUrl();
/*      */     }
/* 1388 */     String key = "dsf.zk." + regId + ".server.url";
/* 1389 */     if (this.uconfig.containsKey(key))
/*      */     {
/* 1391 */       return Utils.parseZkUrl(this.uconfig, key);
/*      */     }
/* 1393 */     return "0:2181";
/*      */   }
/*      */   
/*      */ 
/*      */   public String getDsfZkName()
/*      */   {
/* 1399 */     return this.uconfig.getString("dsf.registry.zk.connector");
/*      */   }
/*      */   
/*      */ 
/*      */   public Integer getDefaultZkSessionTimeout()
/*      */   {
/* 1405 */     if (this.defaultZkSessionTimeout == null)
/*      */     {
/* 1407 */       String key = "dsf.zk.session.timeout";
/* 1408 */       if (this.uconfig.containsKey(key))
/*      */       {
/* 1410 */         this.defaultZkSessionTimeout = Integer.valueOf(this.uconfig.getInt(key));
/*      */       }
/*      */       else
/*      */       {
/* 1414 */         key = "zk.session.timeout";
/* 1415 */         if (this.uconfig.containsKey(key))
/*      */         {
/* 1417 */           this.defaultZkSessionTimeout = Integer.valueOf(this.uconfig.getInt(key));
/*      */         }
/*      */         else
/*      */         {
/* 1421 */           String zkSessionTimeout = getXmlConfig("session-timeout-millisecond");
/* 1422 */           if (null == zkSessionTimeout)
/*      */           {
/* 1424 */             this.defaultZkSessionTimeout = Integer.valueOf(15000);
/*      */           }
/*      */           else
/*      */           {
/* 1428 */             this.defaultZkSessionTimeout = Integer.valueOf(zkSessionTimeout);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1434 */     return this.defaultZkSessionTimeout;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getDefaultZkRootDir()
/*      */   {
/* 1440 */     String key = "dsf.zk.root.dir";
/* 1441 */     if (this.uconfig.containsKey(key))
/*      */     {
/* 1443 */       return this.uconfig.getString(key);
/*      */     }
/*      */     
/*      */ 
/* 1447 */     key = "dsf.root.dir";
/* 1448 */     if (this.uconfig.containsKey(key))
/*      */     {
/* 1450 */       return this.uconfig.getString(key);
/*      */     }
/*      */     
/* 1453 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Integer getZkSessionTimeout(String regId)
/*      */   {
/* 1460 */     if (regId.equals(this.DEFAULT_REGID))
/*      */     {
/* 1462 */       return getDefaultZkSessionTimeout();
/*      */     }
/* 1464 */     String key = "dsf.zk." + regId + ".session.timeout";
/* 1465 */     if (this.uconfig.containsKey(key))
/*      */     {
/* 1467 */       return Integer.valueOf(this.uconfig.getInt(key));
/*      */     }
/*      */     
/* 1470 */     return Integer.valueOf(15000);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getZkRootDir(String regId)
/*      */   {
/* 1477 */     if (regId.equals(this.DEFAULT_REGID))
/*      */     {
/* 1479 */       return getDefaultZkRootDir();
/*      */     }
/* 1481 */     String key = "dsf.zk." + regId + ".root.dir";
/* 1482 */     if (this.uconfig.containsKey(key))
/*      */     {
/* 1484 */       return this.uconfig.getString(key);
/*      */     }
/*      */     
/* 1487 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */   public int getBulkheadTimeInMilliseconds()
/*      */   {
/* 1493 */     return this.uconfig.getInt("dsf.bulkhead.timeInMilliseconds", 1000);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getBulkheadNumberOfBuckets()
/*      */   {
/* 1500 */     return this.uconfig.getInt("dsf.bulkhead.numberOfBuckets", 10);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getBulkheadMaxThreadNum()
/*      */   {
/* 1506 */     return this.uconfig.getInt("dsf.bulkhead.maxQueueSize", 100);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getDsfApplicationDirect()
/*      */   {
/* 1512 */     return this.uconfig.getString(DSF_APPLICATION, Utils.getHostIp());
/*      */   }
/*      */   
/*      */ 
/*      */   public String getRestUrl()
/*      */   {
/* 1518 */     return this.uconfig.getString(DSF_REST_URL, "");
/*      */   }
/*      */   
/*      */ 
/*      */   public int getHttpClientMaxThreads()
/*      */   {
/* 1524 */     return this.uconfig.getInt(HTTP_CLIENT_MAX_THREADS, 500);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getHttpClientIP()
/*      */   {
/* 1530 */     return this.uconfig.getString(HTTP_CLIENT_IP, null);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getHttpClientSSLEnable()
/*      */   {
/* 1536 */     return this.uconfig.getBoolean(HTTP_CLIENT_SSL_ENABLE, Boolean.FALSE).booleanValue();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getHttpClientSSLNeedClientAuth()
/*      */   {
/* 1542 */     return this.uconfig.getBoolean(HTTP_CLIENT_SSL_CLIENT_AUTH, Boolean.FALSE).booleanValue();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getHttpClientSSLTruststorefile()
/*      */   {
/* 1548 */     return this.uconfig.getString(HTTP_CLIENT_SSL_TRUSTSTORE_FILE, null);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getHttpClientSSLTrustStorePwd()
/*      */   {
/* 1554 */     return this.uconfig.getString(HTTP_CLIENT_SSL_TRUSTSTORE_PWD, null);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getHttpClientSSLKeyStoreFile()
/*      */   {
/* 1560 */     return this.uconfig.getString(HTTP_CLIENT_SSL_KEYSTORE_FILE, null);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getHttpClientSSLKeyStorePwd()
/*      */   {
/* 1566 */     return this.uconfig.getString(HTTP_CLIENT_SSL_KEYSTORE_PWD, null);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasEbusAdapter()
/*      */   {
/* 1572 */     return this.hasEbusAdapter;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getIpEnabledField()
/*      */   {
/* 1578 */     if ((this.hasEbusAdapter) && (this.uconfig.containsKey(IP_ENABLED)))
/*      */     {
/* 1580 */       return this.uconfig.getBoolean(IP_ENABLED);
/*      */     }
/*      */     
/* 1583 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getSyncToOldEbus()
/*      */   {
/* 1589 */     if (this.syncToOldEbus != null)
/*      */     {
/* 1591 */       return this.syncToOldEbus.booleanValue();
/*      */     }
/* 1593 */     Map<String, BusConnector> connectorMap = ContextRegistry.getContextHolder().getBeansOfType(BusConnector.class);
/*      */     
/* 1595 */     boolean hasEbusBusConnector = false;
/* 1596 */     for (BusConnector connector : connectorMap.values())
/*      */     {
/* 1598 */       if (connector.getServiceType().equals(ServiceType.EBUS))
/*      */       {
/* 1600 */         hasEbusBusConnector = true;
/* 1601 */         break;
/*      */       }
/*      */     }
/* 1604 */     if ((this.hasEbusAdapter) && (hasEbusBusConnector) && (this.uconfig.containsKey(SYNCTOOLDEBUS)))
/*      */     {
/*      */ 
/* 1607 */       this.syncToOldEbus = Boolean.valueOf(this.uconfig.getBoolean(SYNCTOOLDEBUS));
/*      */     }
/*      */     else
/*      */     {
/* 1611 */       this.syncToOldEbus = Boolean.valueOf(false);
/*      */     }
/* 1613 */     return this.syncToOldEbus.booleanValue();
/*      */   }
/*      */   
/*      */   private String getDsfFailPolicy()
/*      */   {
/* 1618 */     if (this.uconfig.containsKey(USF_SERVICE_FAILPOLICY))
/*      */     {
/* 1620 */       return this.uconfig.getString(USF_SERVICE_FAILPOLICY);
/*      */     }
/* 1622 */     if (this.uconfig.containsKey(DSF_SERVICE_FAILPOLICY))
/*      */     {
/* 1624 */       return this.uconfig.getString(DSF_SERVICE_FAILPOLICY);
/*      */     }
/* 1626 */     return "failFast";
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isClientAuthenticationEnable()
/*      */   {
/* 1632 */     if (this.uconfig.containsKey(DSF_CLIENT_AUTHENTICATION_ENABLE))
/*      */     {
/* 1634 */       return this.uconfig.getBoolean(DSF_CLIENT_AUTHENTICATION_ENABLE);
/*      */     }
/* 1636 */     if (this.uconfig.containsKey(DSF_AUTHENTICATION_ENABLE))
/*      */     {
/* 1638 */       return this.uconfig.getBoolean(DSF_AUTHENTICATION_ENABLE);
/*      */     }
/*      */     
/*      */ 
/* 1642 */     return this.uconfig.getBoolean(AUTHENTICATION_ENABLE, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isServerAuthenticationEnable()
/*      */   {
/* 1649 */     if (this.uconfig.containsKey(DSF_SERVER_AUTHENTICATION_ENABLE))
/*      */     {
/* 1651 */       return this.uconfig.getBoolean(DSF_SERVER_AUTHENTICATION_ENABLE);
/*      */     }
/* 1653 */     if (this.uconfig.containsKey(DSF_AUTHENTICATION_ENABLE))
/*      */     {
/* 1655 */       return this.uconfig.getBoolean(DSF_AUTHENTICATION_ENABLE);
/*      */     }
/*      */     
/*      */ 
/* 1659 */     return this.uconfig.getBoolean(AUTHENTICATION_ENABLE, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long getClientMessageSizeThreshold()
/*      */   {
/* 1666 */     long threshold = this.uconfig.getLong(CLIENT_MESSAGE_THRESHOLD, 10240L);
/* 1667 */     return threshold * 1024L;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getServerMessageSizeThreshold()
/*      */   {
/* 1673 */     long threshold = this.uconfig.getLong(SERVER_MESSAGE_THRESHOLD, 10240L);
/* 1674 */     return threshold * 1024L;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isBigMessageOpen()
/*      */   {
/* 1680 */     return this.uconfig.getBoolean(BIG_MESSAGE_OPEN, false);
/*      */   }
/*      */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\config\DefaultSystemConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */