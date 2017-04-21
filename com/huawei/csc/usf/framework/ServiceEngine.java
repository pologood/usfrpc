/*      */ package com.huawei.csc.usf.framework;
/*      */ 
/*      */ import com.huawei.csc.container.api.ContextRegistry;
/*      */ import com.huawei.csc.container.api.IContextHolder;
/*      */ import com.huawei.csc.kernel.api.log.LogFactory;
/*      */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*      */ import com.huawei.csc.usf.framework.bus.BusConnector;
/*      */ import com.huawei.csc.usf.framework.bus.MessageObjectCoder;
/*      */ import com.huawei.csc.usf.framework.bus.MsgSerializerFactory;
/*      */ import com.huawei.csc.usf.framework.circuitbreaker.DSFFaultTolerancePropertiesCache;
/*      */ import com.huawei.csc.usf.framework.circuitbreaker.TryableSemaphoreCache;
/*      */ import com.huawei.csc.usf.framework.config.DefaultSystemConfig;
/*      */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*      */ import com.huawei.csc.usf.framework.exception.USFException;
/*      */ import com.huawei.csc.usf.framework.executor.ExecutePoolManager;
/*      */ import com.huawei.csc.usf.framework.interceptor.CustomFaultInterceptor;
/*      */ import com.huawei.csc.usf.framework.interceptor.CustomInInterceptor;
/*      */ import com.huawei.csc.usf.framework.interceptor.CustomOutInterceptor;
/*      */ import com.huawei.csc.usf.framework.interceptor.FaultInterceptor;
/*      */ import com.huawei.csc.usf.framework.interceptor.InInterceptor;
/*      */ import com.huawei.csc.usf.framework.interceptor.InterceptorManager;
/*      */ import com.huawei.csc.usf.framework.interceptor.OutInterceptor;
/*      */ import com.huawei.csc.usf.framework.interceptor.impl.EncodeInterceptor;
/*      */ import com.huawei.csc.usf.framework.monitor.threadpool.ThreadPoolMonitorCenter;
/*      */ import com.huawei.csc.usf.framework.pojo.MessageObjectException;
/*      */ import com.huawei.csc.usf.framework.pojo.MessageObjectInvoke;
/*      */ import com.huawei.csc.usf.framework.pojo.PojoConnector;
/*      */ import com.huawei.csc.usf.framework.pojo.SdlParse;
/*      */ import com.huawei.csc.usf.framework.routing.RoutingInterceptor;
/*      */ import com.huawei.csc.usf.framework.service.ClassLoaderManager;
/*      */ import com.huawei.csc.usf.framework.service.ServiceRegistrySupport;
/*      */ import com.huawei.csc.usf.framework.sr.SRAgentFactory;
/*      */ import com.huawei.csc.usf.framework.sr.ServiceInner;
/*      */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*      */ import com.huawei.csc.usf.framework.statistic.BigMessageStatisticCenter;
/*      */ import com.huawei.csc.usf.framework.statistic.ProcessDelayTracker;
/*      */ import com.huawei.csc.usf.framework.trace.TraceLinkUtilHelper;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import org.springframework.beans.factory.NoSuchBeanDefinitionException;
/*      */ import org.springframework.context.ApplicationContext;
/*      */ import org.springframework.context.support.ClassPathXmlApplicationContext;
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
/*      */ public class ServiceEngine
/*      */ {
/*   75 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(ServiceEngine.class);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   81 */   protected Object serviceLocker = new Object();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   86 */   protected Map<String, Connector> connectorMapping = new HashMap();
/*      */   
/*   88 */   protected InterceptorManager interceptorManager = new InterceptorManager();
/*      */   
/*   90 */   protected SystemConfig config = new DefaultSystemConfig();
/*      */   
/*   92 */   protected MessageFactoryHolder messageFactoryHolder = new MessageFactoryHolder();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   97 */   protected List<ServiceEngineInitCallback> initCallbacks = new ArrayList();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ApplicationContext applicationContext;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  107 */   protected ExecutePoolManager poolManager = ExecutePoolManager.getInstance();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  112 */   protected ThreadPoolMonitorCenter threadPoolMonitorCenter = ThreadPoolMonitorCenter.getInstance();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  120 */   protected BusConnector[] busConnectorArray = new BusConnector[ServiceType.size()];
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  126 */   protected List<Connector> connectorList = new ArrayList();
/*      */   
/*  128 */   protected ReplyInterceptor replyInterceptor = new ReplyInterceptor();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  133 */   protected Map<String, ServiceDefinition> serverServiceDefinition = new ConcurrentHashMap();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  138 */   protected Map<String, ServiceDefinition> clientServiceDefinition = new ConcurrentHashMap();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  143 */   protected volatile boolean destroyed = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  148 */   protected List<String> startBusConnectorFlag = new ArrayList();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  153 */   protected MsgSerializerFactory serializerFactory = new MsgSerializerFactory();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  158 */   private SRAgentFactory srAgentFactory = null;
/*      */   
/*  160 */   private MessageObjectCoder moCoder = null;
/*      */   
/*  162 */   private MessageObjectInvoke moInvoker = null;
/*      */   
/*  164 */   private SdlParse sdlParser = null;
/*      */   
/*  166 */   private MessageObjectException messageObjectException = null;
/*      */   
/*  168 */   private BigMessageStatisticCenter bigMessageStatistisCenter = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ServiceEngine()
/*      */   {
/*  177 */     this(new DefaultSystemConfig());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ServiceEngine(SystemConfig config)
/*      */   {
/*  188 */     if (null != config)
/*      */     {
/*  190 */       this.config = config;
/*  191 */       this.config.init();
/*      */     }
/*      */   }
/*      */   
/*      */   public void init() throws Exception
/*      */   {
/*  197 */     ApplicationContext app = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/*.service.xml");
/*      */     
/*      */ 
/*  200 */     init(app);
/*      */   }
/*      */   
/*      */   public void init(ApplicationContext app) throws Exception
/*      */   {
/*  205 */     DEBUGGER.info("start init service engine");
/*      */     
/*  207 */     ContextRegistry.getContextHolder().setContext(app);
/*  208 */     this.applicationContext = app;
/*      */     
/*  210 */     initInitCallbacks(app);
/*      */     
/*  212 */     doBeforeInitCallbacks();
/*      */     
/*  214 */     ProcessDelayTracker.setTrackerOpen(this.config.isSlowServiceOpen());
/*  215 */     ProcessDelayTracker.setClientTrackThreshold(this.config.getSlowServiceClientTime());
/*      */     
/*  217 */     ProcessDelayTracker.setServerTrackThreshold(this.config.getSlowServiceServerTime());
/*      */     
/*      */ 
/*  220 */     TraceLinkUtilHelper.init();
/*      */     
/*  222 */     ExceptionUtilsHolder.init(this);
/*  223 */     this.messageFactoryHolder.init(this);
/*      */     
/*  225 */     initServiceEngineAware(app);
/*      */     
/*  227 */     initSrAgentFactory(app);
/*      */     
/*  229 */     dynamicInitInterceptors(app);
/*      */     
/*  231 */     initCircuitBreakerProperties(this);
/*      */     
/*  233 */     this.poolManager.initExecutorPool(this.config);
/*      */     
/*  235 */     this.serializerFactory.init(this);
/*  236 */     multiProtocolAbilityInit(app);
/*  237 */     initConnectors(app);
/*  238 */     GetAllServiceInfo.init(this);
/*  239 */     this.srAgentFactory.start();
/*      */     
/*  241 */     this.threadPoolMonitorCenter.init(this.config, app);
/*      */     
/*  243 */     initMessageStatisticCenter();
/*      */     
/*  245 */     doAfterInitCallbacks();
/*      */     
/*  247 */     boolean bPojoConnectorFound = false;
/*  248 */     Map<String, AbstractConnector> mapConnectors = app.getBeansOfType(AbstractConnector.class);
/*      */     
/*  250 */     List<Connector> conns = new ArrayList(mapConnectors.values());
/*  251 */     for (Connector conn : conns)
/*      */     {
/*  253 */       if ((conn instanceof PojoConnector))
/*      */       {
/*  255 */         bPojoConnectorFound = true;
/*  256 */         break;
/*      */       }
/*      */     }
/*  259 */     if (!bPojoConnectorFound)
/*      */     {
/*  261 */       String strTips = "PojoConnector was not found after startup, please check whether the file classpath*:META-INF/spring/usf.framework.service.xml loaded in ApplicationContext or not.";
/*  262 */       DEBUGGER.error(strTips);
/*  263 */       throw new USFException(strTips);
/*      */     }
/*  265 */     DEBUGGER.info("finish init service engine");
/*      */   }
/*      */   
/*      */ 
/*      */   private void multiProtocolAbilityInit(ApplicationContext app)
/*      */   {
/*  271 */     Map<String, MessageObjectCoder> moCoders = app.getBeansOfType(MessageObjectCoder.class);
/*      */     
/*  273 */     if ((null != moCoders) && (null != moCoders.get("moCoderImpl")))
/*      */     {
/*  275 */       this.moCoder = ((MessageObjectCoder)moCoders.get("moCoderImpl"));
/*      */     }
/*  277 */     Map<String, MessageObjectInvoke> moInvokers = app.getBeansOfType(MessageObjectInvoke.class);
/*      */     
/*  279 */     if ((null != moInvokers) && (null != moInvokers.get("moInvokeImpl")))
/*      */     {
/*  281 */       this.moInvoker = ((MessageObjectInvoke)moInvokers.get("moInvokeImpl"));
/*      */     }
/*  283 */     Map<String, SdlParse> sdlParsers = app.getBeansOfType(SdlParse.class);
/*  284 */     if ((null != sdlParsers) && (null != sdlParsers.get("sdlParser")))
/*      */     {
/*  286 */       this.sdlParser = ((SdlParse)sdlParsers.get("sdlParser"));
/*      */     }
/*      */     
/*  289 */     if (app.containsBean("dsfMoExceptionImpl"))
/*      */     {
/*  291 */       this.messageObjectException = ((MessageObjectException)app.getBean("dsfMoExceptionImpl"));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void initInitCallbacks(ApplicationContext app)
/*      */   {
/*  298 */     Map<String, ServiceEngineInitCallback> initCallbackBeans = app.getBeansOfType(ServiceEngineInitCallback.class);
/*      */     
/*  300 */     for (ServiceEngineInitCallback initCallback : initCallbackBeans.values())
/*      */     {
/*      */ 
/*  303 */       this.initCallbacks.add(initCallback);
/*      */     }
/*      */   }
/*      */   
/*      */   private void doBeforeInitCallbacks() throws Exception
/*      */   {
/*  309 */     for (ServiceEngineInitCallback callback : this.initCallbacks)
/*      */     {
/*  311 */       DEBUGGER.info("callback(before init): " + callback.getClass());
/*  312 */       callback.beforeInitCallback(this);
/*      */     }
/*      */   }
/*      */   
/*      */   private void doAfterInitCallbacks() throws Exception
/*      */   {
/*  318 */     for (ServiceEngineInitCallback callback : this.initCallbacks)
/*      */     {
/*  320 */       DEBUGGER.info("callback(after init): " + callback.getClass());
/*  321 */       callback.afterInitCallback(this);
/*      */     }
/*      */   }
/*      */   
/*      */   private void initServiceEngineAware(ApplicationContext app)
/*      */   {
/*  327 */     Map<String, ServiceEngineAware> serviceEngineAwareMap = app.getBeansOfType(ServiceEngineAware.class);
/*      */     
/*  329 */     for (Map.Entry<String, ServiceEngineAware> entry : serviceEngineAwareMap.entrySet())
/*      */     {
/*      */ 
/*  332 */       ((ServiceEngineAware)entry.getValue()).setServiceEngine(this);
/*      */     }
/*      */   }
/*      */   
/*      */   private void dynamicInitInterceptors(ApplicationContext app)
/*      */   {
/*  338 */     this.replyInterceptor.setServiceEngine(this);
/*  339 */     this.replyInterceptor.init();
/*      */     
/*  341 */     Map<String, InInterceptor> inMap = app.getBeansOfType(InInterceptor.class);
/*      */     
/*      */ 
/*  344 */     for (Map.Entry<String, InInterceptor> entry : inMap.entrySet())
/*      */     {
/*  346 */       InInterceptor inInterceptor = (InInterceptor)entry.getValue();
/*      */       
/*  348 */       if (!(inInterceptor instanceof CustomInInterceptor))
/*      */       {
/*  350 */         if ((inInterceptor instanceof RoutingInterceptor))
/*      */         {
/*  352 */           RoutingInterceptor routingInterceptor = (RoutingInterceptor)inInterceptor;
/*  353 */           routingInterceptor.setSrAgentFactory(this.srAgentFactory);
/*  354 */           routingInterceptor.setServiceEngine(this);
/*      */         }
/*  356 */         this.interceptorManager.addInInterceptor(inInterceptor);
/*  357 */         DEBUGGER.info(inInterceptor + " has been added");
/*      */       }
/*  359 */       inInterceptor.init();
/*      */     }
/*      */     
/*  362 */     Collections.sort(this.interceptorManager.getInInterceptors(), new Comparator()
/*      */     {
/*      */ 
/*      */       public int compare(InInterceptor o1, InInterceptor o2)
/*      */       {
/*      */ 
/*  368 */         return Integer.valueOf(o1.getInWeight()).compareTo(Integer.valueOf(o2.getInWeight()));
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  374 */     });
/*  375 */     initCustomInInterceptors(app);
/*      */     
/*  377 */     Map<String, OutInterceptor> outMap = app.getBeansOfType(OutInterceptor.class);
/*      */     
/*  379 */     for (Map.Entry<String, OutInterceptor> entry : outMap.entrySet())
/*      */     {
/*  381 */       OutInterceptor outInterceptor = (OutInterceptor)entry.getValue();
/*      */       
/*  383 */       if (!(outInterceptor instanceof CustomOutInterceptor))
/*      */       {
/*  385 */         if ((outInterceptor instanceof EncodeInterceptor))
/*      */         {
/*  387 */           EncodeInterceptor encodeInterceptor = (EncodeInterceptor)outInterceptor;
/*  388 */           encodeInterceptor.setServiceEngine(this);
/*      */         }
/*  390 */         this.interceptorManager.addOutInterceptor(outInterceptor);
/*  391 */         DEBUGGER.info(outInterceptor + " has been added");
/*      */       }
/*  393 */       outInterceptor.init();
/*      */     }
/*      */     
/*  396 */     Collections.sort(this.interceptorManager.getOutInterceptors(), new Comparator()
/*      */     {
/*      */ 
/*      */ 
/*      */       public int compare(OutInterceptor o1, OutInterceptor o2)
/*      */       {
/*      */ 
/*  403 */         return Integer.valueOf(o1.getOutWeight()).compareTo(Integer.valueOf(o2.getOutWeight()));
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  409 */     });
/*  410 */     initCustomOutInterceptors(app);
/*      */     
/*      */ 
/*  413 */     Map<String, FaultInterceptor> faultMap = app.getBeansOfType(FaultInterceptor.class);
/*      */     
/*  415 */     for (Map.Entry<String, FaultInterceptor> entry : faultMap.entrySet())
/*      */     {
/*  417 */       FaultInterceptor faultInterceptor = (FaultInterceptor)entry.getValue();
/*      */       
/*  419 */       if (!(faultInterceptor instanceof CustomFaultInterceptor))
/*      */       {
/*  421 */         this.interceptorManager.addFaultInterceptor(faultInterceptor);
/*  422 */         DEBUGGER.info(faultInterceptor + " has been added");
/*      */       }
/*  424 */       faultInterceptor.init();
/*      */     }
/*  426 */     Collections.sort(this.interceptorManager.getFaultInterceptors(), new Comparator()
/*      */     {
/*      */ 
/*      */ 
/*      */       public int compare(FaultInterceptor o1, FaultInterceptor o2)
/*      */       {
/*      */ 
/*  433 */         return Integer.valueOf(o1.getFaultWeight()).compareTo(Integer.valueOf(o2.getFaultWeight()));
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  438 */     });
/*  439 */     initCustomFaultInterceptors(app);
/*      */     
/*  441 */     DEBUGGER.info("The InInterceptors are " + this.interceptorManager.getInInterceptors() + "\n" + "The OutInterceptors are " + this.interceptorManager.getOutInterceptors() + "\n" + "The FaultInterceptors are " + this.interceptorManager.getFaultInterceptors());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  449 */     if ((!ContextRegistry.getContextHolder().containsBean("intercepter-handler")) && (!ContextRegistry.getContextHolder().containsBean("partner-statistic-handler")))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  454 */       removeDSFHandlerIntercepter();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void removeDSFHandlerIntercepter()
/*      */   {
/*  464 */     if (!ContextRegistry.getContextHolder().containsBean("dsfHanderInInterceptor"))
/*      */     {
/*      */ 
/*  467 */       return;
/*      */     }
/*  469 */     InInterceptor dsfHanderInInterceptor = (InInterceptor)ContextRegistry.getContextHolder().getBean("dsfHanderInInterceptor");
/*      */     
/*  471 */     InInterceptor dsfHanderClientInInterceptor = (InInterceptor)ContextRegistry.getContextHolder().getBean("dsfHanderClientInInterceptor");
/*      */     
/*  473 */     OutInterceptor dsfHanderOutInterceptor = (OutInterceptor)ContextRegistry.getContextHolder().getBean("dsfHanderOutInterceptor");
/*      */     
/*  475 */     OutInterceptor dsfHanderClientOutInterceptor = (OutInterceptor)ContextRegistry.getContextHolder().getBean("dsfHanderClientOutInterceptor");
/*      */     
/*  477 */     FaultInterceptor dsfExceptionInterceptor = (FaultInterceptor)ContextRegistry.getContextHolder().getBean("dsfExceptionInterceptor");
/*      */     
/*  479 */     this.interceptorManager.getInInterceptors().remove(dsfHanderInInterceptor);
/*  480 */     this.interceptorManager.getInInterceptors().remove(dsfHanderClientInInterceptor);
/*      */     
/*  482 */     this.interceptorManager.getFaultInterceptors().remove(dsfExceptionInterceptor);
/*      */     
/*  484 */     this.interceptorManager.getOutInterceptors().remove(dsfHanderOutInterceptor);
/*  485 */     this.interceptorManager.getOutInterceptors().remove(dsfHanderClientOutInterceptor);
/*      */   }
/*      */   
/*      */   private void initConnectors(ApplicationContext app)
/*      */     throws Exception
/*      */   {
/*  491 */     Map<String, Connector> connectorMap = app.getBeansOfType(Connector.class);
/*      */     
/*      */ 
/*  494 */     this.connectorList = new ArrayList(connectorMap.values());
/*  495 */     for (Connector connector : this.connectorList)
/*      */     {
/*  497 */       if (connector.getConnectorType().equals("BUS"))
/*      */       {
/*  499 */         BusConnector busConnector = (BusConnector)connector;
/*      */         
/*      */ 
/*  502 */         this.busConnectorArray[busConnector.getServiceType().toNumber()] = busConnector;
/*      */       }
/*      */       
/*  505 */       load(connector);
/*      */       
/*  507 */       if (DEBUGGER.isInfoEnable())
/*      */       {
/*  509 */         DEBUGGER.info("finish init connector, connector type: " + connector.getConnectorType() + ", class: " + connector);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  515 */     for (Connector connector : this.connectorList)
/*      */     {
/*  517 */       if (connector.getConnectorType().equals("BUS"))
/*      */       {
/*  519 */         BusConnector busConnector = (BusConnector)connector;
/*  520 */         busConnector.start();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void initSrAgentFactory(ApplicationContext app) throws Exception
/*      */   {
/*  527 */     if (this.srAgentFactory == null)
/*      */     {
/*      */       try
/*      */       {
/*  531 */         this.srAgentFactory = ((SRAgentFactory)app.getBean("usfSrAgentFactory"));
/*      */ 
/*      */       }
/*      */       catch (NoSuchBeanDefinitionException e)
/*      */       {
/*  536 */         this.srAgentFactory = new SRAgentFactory();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  541 */     this.srAgentFactory.init(app, this.config);
/*      */     
/*      */ 
/*  544 */     initSrListenAddress();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void initSrListenAddress()
/*      */   {
/*  550 */     String usfAddrss = this.config.getRPCAddress(ServiceType.USF);
/*  551 */     if (usfAddrss != null)
/*      */     {
/*  553 */       this.srAgentFactory.setListenAddressOnSRs(ServiceType.USF, usfAddrss);
/*  554 */       if (DEBUGGER.isInfoEnable())
/*      */       {
/*  556 */         DEBUGGER.info("Init Service Engine Listen USFAddress: " + usfAddrss);
/*      */       }
/*      */     }
/*      */     
/*  560 */     String ebusAddrss = this.config.getRPCAddress(ServiceType.EBUS);
/*  561 */     if (ebusAddrss != null)
/*      */     {
/*  563 */       this.srAgentFactory.setListenAddressOnSRs(ServiceType.EBUS, ebusAddrss);
/*  564 */       if (DEBUGGER.isInfoEnable())
/*      */       {
/*  566 */         DEBUGGER.info("Init Service Engine Listen EBUSAddress: " + ebusAddrss);
/*      */       }
/*      */     }
/*      */     
/*  570 */     String dsfAddrss = this.config.getRPCAddress(ServiceType.DSF);
/*  571 */     if (dsfAddrss != null)
/*      */     {
/*  573 */       this.srAgentFactory.setListenAddressOnSRs(ServiceType.DSF, dsfAddrss);
/*  574 */       if (DEBUGGER.isInfoEnable())
/*      */       {
/*  576 */         DEBUGGER.info("Init Service Engine Listen DSFAddress: " + dsfAddrss);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void load(Connector connector)
/*      */     throws Exception
/*      */   {
/*  585 */     if ((connector instanceof ServiceEngineAware))
/*      */     {
/*  587 */       connector.setServiceEngine(this);
/*      */     }
/*      */     
/*  590 */     connector.init();
/*      */     
/*  592 */     if ((connector instanceof ServiceExporter))
/*      */     {
/*  594 */       ServiceExporter serviceExporter = (ServiceExporter)connector;
/*      */       
/*  596 */       serviceExporter.loadService(new Object[0]);
/*      */       
/*  598 */       addServiceConnectorMapping(serviceExporter.getExports(), connector);
/*      */       
/*  600 */       List<ServiceDefinition> dList = serviceExporter.getServiceDefinitions();
/*      */       
/*  602 */       if ((null != dList) && (!dList.isEmpty()))
/*      */       {
/*  604 */         for (ServiceDefinition sd : dList)
/*      */         {
/*  606 */           if (sd.isServer())
/*      */           {
/*  608 */             this.serverServiceDefinition.put(sd.getServiceName(), sd);
/*      */           }
/*      */           else
/*      */           {
/*  612 */             this.clientServiceDefinition.put(sd.getServiceName(), sd);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  617 */       registryService(serviceExporter);
/*      */     }
/*      */   }
/*      */   
/*      */   private void registryService(ServiceExporter connector) throws Exception
/*      */   {
/*  623 */     List<ServiceInner> ebusServices = new ArrayList();
/*      */     
/*  625 */     List<ServiceInner> proxyServices = new ArrayList();
/*      */     
/*  627 */     List<ServiceInner> allServices = connector.getServiceList();
/*      */     
/*  629 */     if (allServices == null)
/*      */     {
/*  631 */       return;
/*      */     }
/*      */     
/*  634 */     for (ServiceInner service : allServices)
/*      */     {
/*  636 */       if (null != service)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  641 */         if (service.isImporter())
/*      */         {
/*  643 */           proxyServices.add(service);
/*      */         }
/*      */         else
/*      */         {
/*  647 */           ebusServices.add(service);
/*  648 */           if (!this.startBusConnectorFlag.contains(service.getServiceType()))
/*      */           {
/*  650 */             this.startBusConnectorFlag.add(service.getServiceType()); }
/*      */         }
/*      */       }
/*      */     }
/*  654 */     if (this.config.getIsDelayRegister())
/*      */     {
/*  656 */       this.srAgentFactory.registerServicesOnDefaultSR(ebusServices, true);
/*      */     }
/*      */     else
/*      */     {
/*  660 */       this.srAgentFactory.registerServicesOnDefaultSR(ebusServices, false);
/*      */     }
/*  662 */     this.srAgentFactory.addServiceWatcherOnDefaultSR(ebusServices);
/*  663 */     this.srAgentFactory.registerConsumersOnSRs(proxyServices);
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
/*      */ 
/*      */   public void addServiceConnectorMapping(Collection<String> services, Connector connector)
/*      */   {
/*  678 */     if ((null == services) || (services.isEmpty()))
/*      */     {
/*  680 */       return;
/*      */     }
/*      */     
/*  683 */     synchronized (this.serviceLocker)
/*      */     {
/*  685 */       Map<String, Connector> newMapping = new HashMap(this.connectorMapping);
/*      */       
/*      */ 
/*  688 */       for (String name : services)
/*      */       {
/*  690 */         if (!newMapping.containsKey(name))
/*      */         {
/*      */ 
/*      */ 
/*  694 */           newMapping.put(name, connector);
/*      */           
/*  696 */           if (DEBUGGER.isInfoEnable())
/*      */           {
/*  698 */             DEBUGGER.info("add connector mapping, serviceName : [" + name + "], connector : " + connector);
/*      */           }
/*      */         }
/*      */       }
/*  702 */       this.connectorMapping = newMapping;
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeServiceConnectorMapping(Collection<String> services)
/*      */   {
/*  708 */     if ((null == services) || (services.isEmpty()))
/*      */     {
/*  710 */       return;
/*      */     }
/*      */     
/*  713 */     synchronized (this.serviceLocker)
/*      */     {
/*  715 */       Map<String, Connector> newMapping = new HashMap(this.connectorMapping);
/*      */       
/*      */ 
/*  718 */       for (String name : services)
/*      */       {
/*  720 */         if (newMapping.containsKey(name))
/*      */         {
/*      */ 
/*      */ 
/*  724 */           newMapping.remove(name);
/*      */           
/*  726 */           if (DEBUGGER.isInfoEnable())
/*      */           {
/*  728 */             DEBUGGER.info("remove connector mapping, serviceName : [" + name + "]");
/*      */           }
/*      */         }
/*      */       }
/*  732 */       this.connectorMapping = newMapping;
/*      */     }
/*      */   }
/*      */   
/*      */   public void onReceive(Context context) throws Exception
/*      */   {
/*  738 */     this.interceptorManager.dispatch(context);
/*      */   }
/*      */   
/*      */   public SystemConfig getSystemConfig()
/*      */   {
/*  743 */     return this.config;
/*      */   }
/*      */   
/*      */   public Connector findConnectorByContext(Context context) throws Exception
/*      */   {
/*  748 */     IMessage receivedMessage = context.getReceivedMessage();
/*  749 */     String serviceName = receivedMessage.getHeaders().getServiceName();
/*  750 */     BusConnector busConnector = null;
/*  751 */     ServiceType serviceType = context.getDestServiceType();
/*  752 */     if (null != serviceType)
/*      */     {
/*  754 */       busConnector = this.busConnectorArray[serviceType.toNumber()];
/*      */     }
/*      */     else
/*      */     {
/*  758 */       busConnector = this.busConnectorArray[context.getServiceType().toNumber()];
/*      */     }
/*      */     
/*  761 */     boolean local = false;
/*      */     
/*  763 */     if (context.isServer())
/*      */     {
/*  765 */       local = true;
/*      */ 
/*      */ 
/*      */     }
/*  769 */     else if ((busConnector != null) && (receivedMessage.getHeaders().getDestAddr().equals(busConnector.getListenAddr())))
/*      */     {
/*      */ 
/*      */ 
/*  773 */       local = true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  778 */     Connector connector = null;
/*      */     
/*  780 */     if (("rest".equalsIgnoreCase(receivedMessage.getHeaders().getPolicyProtocol())) && ("out".equalsIgnoreCase(context.getRestProtocolDirection())))
/*      */     {
/*      */ 
/*      */ 
/*  784 */       connector = getConnector(serviceName);
/*  785 */       context.setLocal(true);
/*      */ 
/*      */     }
/*  788 */     else if ((local) || (busConnector == null))
/*      */     {
/*  790 */       connector = getConnector(serviceName);
/*  791 */       context.setLocal(true);
/*      */     }
/*      */     else
/*      */     {
/*  795 */       connector = busConnector;
/*      */     }
/*      */     
/*  798 */     if (connector == null)
/*      */     {
/*  800 */       throw ExceptionUtilsHolder.getExceptionUtils(context.getServiceType()).canNotFindConnector(serviceName);
/*      */     }
/*      */     
/*      */ 
/*  804 */     if (DEBUGGER.isDebugEnable())
/*      */     {
/*  806 */       String strName = connector.getClass().getSimpleName();
/*  807 */       StringBuilder sb = new StringBuilder();
/*  808 */       sb.append("[target_connector:").append(strName).append("][passby][info:find the target connector for service ").append(serviceName).append("]");
/*      */       
/*      */ 
/*      */ 
/*  812 */       DEBUGGER.debug(sb.toString());
/*      */     }
/*      */     
/*  815 */     return connector;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public MsgSerializerFactory getSerializerFactory()
/*      */   {
/*  826 */     return this.serializerFactory;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SRAgentFactory getSrAgentFactory()
/*      */   {
/*  836 */     return this.srAgentFactory;
/*      */   }
/*      */   
/*      */   public void setSrAgentFactory(SRAgentFactory srAgentFactory)
/*      */   {
/*  841 */     this.srAgentFactory = srAgentFactory;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public InterceptorManager getInterceptorManager()
/*      */   {
/*  851 */     return this.interceptorManager;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setInterceptorManager(InterceptorManager interceptorManager)
/*      */   {
/*  862 */     this.interceptorManager = interceptorManager;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Connector getConnector(String serviceName)
/*      */   {
/*  874 */     Connector connector = null;
/*      */     
/*  876 */     connector = (Connector)this.connectorMapping.get(serviceName);
/*      */     
/*  878 */     return connector;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public List<String> getStartBusConnectorFlag()
/*      */   {
/*  889 */     return this.startBusConnectorFlag;
/*      */   }
/*      */   
/*      */   public IMessageFactory getMessageFactory(ServiceType serviceType)
/*      */   {
/*  894 */     return this.messageFactoryHolder.getMessageFactory(serviceType);
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public ExceptionUtils getExceptionUtils(ServiceType serviceType)
/*      */   {
/*  900 */     return ExceptionUtilsHolder.getExceptionUtils(serviceType);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void destroy()
/*      */   {
/*  911 */     gracefulDestroy();
/*  912 */     destroyEngine();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void destroyNow()
/*      */   {
/*  920 */     destroyEngine();
/*      */   }
/*      */   
/*      */   private void destroyEngine()
/*      */   {
/*  925 */     this.initCallbacks.clear();
/*      */     
/*  927 */     this.connectorMapping.clear();
/*      */     
/*  929 */     ClassLoaderManager.getInstance().destroy();
/*  930 */     ServiceRegistrySupport.getInstance().cleanServiceResitryMap();
/*      */     
/*  932 */     ProcessDelayTracker.reset();
/*      */     
/*  934 */     for (Connector connector : this.connectorList)
/*      */     {
/*      */       try
/*      */       {
/*  938 */         connector.destroy();
/*      */       }
/*      */       catch (Exception ex)
/*      */       {
/*  942 */         DEBUGGER.warn("Connector[" + connector + "] destory fialed.", ex);
/*      */       }
/*      */     }
/*      */     
/*  946 */     this.connectorList.clear();
/*      */     
/*  948 */     this.threadPoolMonitorCenter.destroy();
/*  949 */     this.poolManager.destory();
/*      */     
/*  951 */     if (this.srAgentFactory != null)
/*      */     {
/*  953 */       this.srAgentFactory.uninit();
/*      */     }
/*      */     
/*  956 */     if (this.bigMessageStatistisCenter != null)
/*      */     {
/*  958 */       this.bigMessageStatistisCenter.close();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void gracefulDestroy()
/*      */   {
/*  967 */     DEBUGGER.info("gracefulDestroy: start");
/*      */     
/*      */ 
/*  970 */     if (this.srAgentFactory != null)
/*      */     {
/*  972 */       this.srAgentFactory.unregisterAllLocalInstanceOnSRs();
/*  973 */       this.srAgentFactory.stop();
/*      */     }
/*      */     
/*      */ 
/*  977 */     doSimpleWait(5000L);
/*      */     
/*      */ 
/*  980 */     DEBUGGER.info("gracefulDestroy: set state of service engine to destroyed.");
/*  981 */     this.destroyed = true;
/*      */     
/*      */ 
/*  984 */     long timeout = this.config.getGracefulDestroyTimeout();
/*  985 */     DEBUGGER.info("gracefulDestroy: config timeout(ms): " + timeout);
/*      */     
/*      */ 
/*  988 */     long ioWait = timeout < 10000L ? timeout : 10000L;
/*  989 */     long maxDynamicWait = timeout - ioWait > 0L ? timeout - ioWait : 0L;
/*      */     
/*  991 */     long start = System.currentTimeMillis();
/*      */     
/*      */ 
/*      */ 
/*  995 */     while ((this.srAgentFactory != null) && (!isReadyToDestroy()) && (System.currentTimeMillis() - start < maxDynamicWait))
/*      */     {
/*  997 */       doSimpleWait(10L);
/*      */     }
/*      */     
/*      */ 
/* 1001 */     DEBUGGER.info("gracefulDestroy: start to do iowait(ms): " + ioWait);
/* 1002 */     doSimpleWait(ioWait);
/*      */     
/* 1004 */     DEBUGGER.info("gracefulDestroy: stop");
/*      */   }
/*      */   
/*      */   private void doSimpleWait(long millis)
/*      */   {
/*      */     try
/*      */     {
/* 1011 */       Thread.sleep(millis);
/*      */     }
/*      */     catch (InterruptedException e) {}
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
/*      */   protected boolean isReadyToDestroy()
/*      */   {
/* 1027 */     if (this.replyInterceptor.getRequestMap().size() > 0)
/*      */     {
/* 1029 */       return false;
/*      */     }
/*      */     
/* 1032 */     for (Connector connector : this.connectorList)
/*      */     {
/* 1034 */       if (!connector.isReadyToDestroy())
/*      */       {
/* 1036 */         return false;
/*      */       }
/*      */     }
/*      */     
/* 1040 */     if (!this.srAgentFactory.isReadyToDestroy())
/*      */     {
/* 1042 */       return false;
/*      */     }
/*      */     
/* 1045 */     return true;
/*      */   }
/*      */   
/*      */   public ReplyInterceptor getReplyInterceptor()
/*      */   {
/* 1050 */     return this.replyInterceptor;
/*      */   }
/*      */   
/*      */   public boolean isDestroyed()
/*      */   {
/* 1055 */     return this.destroyed;
/*      */   }
/*      */   
/*      */   public void setDestroyed(boolean destroyed)
/*      */   {
/* 1060 */     this.destroyed = destroyed;
/*      */   }
/*      */   
/*      */ 
/*      */   public BusConnector getBusConnector(ServiceType serviceType)
/*      */   {
/* 1066 */     return this.busConnectorArray[serviceType.toNumber()];
/*      */   }
/*      */   
/*      */ 
/*      */   public Object getServiceLocker()
/*      */   {
/* 1072 */     return this.serviceLocker;
/*      */   }
/*      */   
/*      */ 
/*      */   public Map<String, ServiceDefinition> getServerServiceDefinition()
/*      */   {
/* 1078 */     return this.serverServiceDefinition;
/*      */   }
/*      */   
/*      */ 
/*      */   public Map<String, ServiceDefinition> getClientServiceDefinition()
/*      */   {
/* 1084 */     return this.clientServiceDefinition;
/*      */   }
/*      */   
/*      */   public ApplicationContext getApplicationContext()
/*      */   {
/* 1089 */     return this.applicationContext;
/*      */   }
/*      */   
/*      */   public void setApplicationContext(ApplicationContext applicationContext)
/*      */   {
/* 1094 */     this.applicationContext = applicationContext;
/*      */   }
/*      */   
/*      */   public MessageObjectCoder getMoCoder()
/*      */   {
/* 1099 */     return this.moCoder;
/*      */   }
/*      */   
/*      */   public MessageObjectInvoke getMoInvoker()
/*      */   {
/* 1104 */     return this.moInvoker;
/*      */   }
/*      */   
/*      */   public SdlParse getSdlParser()
/*      */   {
/* 1109 */     return this.sdlParser;
/*      */   }
/*      */   
/*      */   public MessageObjectException getMessageObjectException()
/*      */   {
/* 1114 */     return this.messageObjectException;
/*      */   }
/*      */   
/*      */ 
/*      */   private void initCustomInInterceptors(ApplicationContext app)
/*      */   {
/* 1120 */     Map<String, CustomInInterceptor> inCustomMap = app.getBeansOfType(CustomInInterceptor.class);
/*      */     
/*      */ 
/* 1123 */     List<CustomInInterceptor> cusIn = new ArrayList();
/*      */     
/* 1125 */     for (Map.Entry<String, CustomInInterceptor> entry : inCustomMap.entrySet())
/*      */     {
/* 1127 */       cusIn.add(entry.getValue());
/*      */     }
/*      */     
/* 1130 */     List<InInterceptor> in = this.interceptorManager.getInInterceptors();
/* 1131 */     int size = cusIn.size();
/* 1132 */     for (int k = 0; k < size; k++)
/*      */     {
/* 1134 */       for (int i = 0; i < cusIn.size(); i++)
/*      */       {
/* 1136 */         CustomInInterceptor customInInterceptor = (CustomInInterceptor)cusIn.get(i);
/* 1137 */         String before = customInInterceptor.getBefore();
/* 1138 */         if (before != null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1143 */           for (int j = 0; j < in.size(); j++)
/*      */           {
/* 1145 */             if (before.equals(((InInterceptor)in.get(j)).getName()))
/*      */             {
/* 1147 */               in.add(j, customInInterceptor);
/* 1148 */               cusIn.remove(customInInterceptor);
/* 1149 */               j++;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void initCustomOutInterceptors(ApplicationContext app)
/*      */   {
/* 1160 */     Map<String, CustomOutInterceptor> outCustomMap = app.getBeansOfType(CustomOutInterceptor.class);
/*      */     
/* 1162 */     List<CustomOutInterceptor> ll = new ArrayList();
/*      */     
/* 1164 */     for (Map.Entry<String, CustomOutInterceptor> entry : outCustomMap.entrySet())
/*      */     {
/*      */ 
/* 1167 */       ll.add(entry.getValue());
/*      */     }
/*      */     
/* 1170 */     List<OutInterceptor> out = this.interceptorManager.getOutInterceptors();
/* 1171 */     int size = ll.size();
/* 1172 */     for (int k = 0; k < size; k++)
/*      */     {
/* 1174 */       for (int i = 0; i < ll.size(); i++)
/*      */       {
/* 1176 */         CustomOutInterceptor customOutInterceptor = (CustomOutInterceptor)ll.get(i);
/* 1177 */         String before = customOutInterceptor.getBefore();
/* 1178 */         if (before == null)
/*      */         {
/* 1180 */           out.add(customOutInterceptor);
/* 1181 */           ll.remove(customOutInterceptor);
/*      */         }
/*      */         else
/*      */         {
/* 1185 */           for (int j = 0; j < out.size(); j++)
/*      */           {
/* 1187 */             if (before.equals(((OutInterceptor)out.get(j)).getName()))
/*      */             {
/* 1189 */               out.add(j, customOutInterceptor);
/* 1190 */               ll.remove(customOutInterceptor);
/* 1191 */               j++;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void initCustomFaultInterceptors(ApplicationContext app)
/*      */   {
/* 1203 */     Map<String, CustomFaultInterceptor> faultCustomMap = app.getBeansOfType(CustomFaultInterceptor.class);
/*      */     
/* 1205 */     List<CustomFaultInterceptor> cusFault = new ArrayList();
/*      */     
/* 1207 */     for (Map.Entry<String, CustomFaultInterceptor> entry : faultCustomMap.entrySet())
/*      */     {
/*      */ 
/* 1210 */       cusFault.add(entry.getValue());
/*      */     }
/*      */     
/* 1213 */     List<FaultInterceptor> fault = this.interceptorManager.getFaultInterceptors();
/*      */     
/* 1215 */     int size = cusFault.size();
/* 1216 */     for (int k = 0; k < size; k++)
/*      */     {
/* 1218 */       for (int i = 0; i < cusFault.size(); i++)
/*      */       {
/* 1220 */         CustomFaultInterceptor customFaultInterceptor = (CustomFaultInterceptor)cusFault.get(i);
/* 1221 */         String before = customFaultInterceptor.getBefore();
/* 1222 */         if (before == null)
/*      */         {
/* 1224 */           fault.add(customFaultInterceptor);
/* 1225 */           cusFault.remove(customFaultInterceptor);
/*      */         }
/*      */         else
/*      */         {
/* 1229 */           for (int j = 0; j < fault.size(); j++)
/*      */           {
/* 1231 */             if (before.equals(((FaultInterceptor)fault.get(j)).getName()))
/*      */             {
/* 1233 */               fault.add(j, customFaultInterceptor);
/* 1234 */               cusFault.remove(customFaultInterceptor);
/* 1235 */               j++;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void initCircuitBreakerProperties(ServiceEngine engine)
/*      */   {
/* 1245 */     new DSFFaultTolerancePropertiesCache().init(engine);
/*      */     
/* 1247 */     new TryableSemaphoreCache().init(engine);
/*      */   }
/*      */   
/*      */   private void initMessageStatisticCenter()
/*      */   {
/* 1252 */     this.bigMessageStatistisCenter = new BigMessageStatisticCenter();
/* 1253 */     this.bigMessageStatistisCenter.init(this);
/*      */   }
/*      */   
/*      */   public BigMessageStatisticCenter getBigMessageStatistisCenter()
/*      */   {
/* 1258 */     return this.bigMessageStatistisCenter;
/*      */   }
/*      */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\ServiceEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */