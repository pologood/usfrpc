/*      */ package com.huawei.csc.usf.framework.trace;
/*      */ 
/*      */ import com.huawei.csc.kernel.api.log.LogFactory;
/*      */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*      */ import com.huawei.csc.kernel.api.trace.ITraceLinkMessage;
/*      */ import com.huawei.csc.kernel.api.trace.TraceFactory;
/*      */ import com.huawei.csc.kernel.api.trace.TraceManager;
/*      */ import com.huawei.csc.kernel.api.trace.context.TraceContext;
/*      */ import com.huawei.csc.kernel.api.trace.context.TraceContextHolder;
/*      */ import com.huawei.csc.usf.framework.Connector;
/*      */ import com.huawei.csc.usf.framework.Context;
/*      */ import com.huawei.csc.usf.framework.IMessage;
/*      */ import com.huawei.csc.usf.framework.MessageHeaders;
/*      */ import com.huawei.csc.usf.framework.config.USFConfig;
/*      */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*      */ import com.huawei.csc.usf.framework.statistic.ProcessDelayTracker;
/*      */ import com.huawei.csc.usf.framework.util.LogTraceUtil;
/*      */ import com.huawei.csc.usf.framework.util.USFContext;
/*      */ import com.huawei.csc.usf.framework.util.USFContextUtil;
/*      */ import com.huawei.csc.usf.framework.util.USFCtxObject;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import java.util.concurrent.atomic.AtomicLong;
/*      */ import org.apache.commons.lang.StringUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TraceLinkUtilHelper
/*      */ {
/*   31 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(TraceLinkUtilHelper.class);
/*      */   
/*      */ 
/*   34 */   private static AtomicLong sendReqSize = new AtomicLong();
/*      */   
/*   36 */   private static HashMap<String, String> callingNodeMapper = new HashMap();
/*      */   
/*   38 */   private static final Object CALLING_NODE_LOCK = new Object();
/*      */   
/*   40 */   private static final Object CALLED_NODE_LOCK = new Object();
/*      */   
/*   42 */   private static final Object CALLED_CLUSTER_LOCK = new Object();
/*      */   
/*   44 */   private static HashMap<String, String> calledNodeMapper = new HashMap();
/*      */   
/*   46 */   private static HashMap<String, String> calledClusterMapper = new HashMap();
/*      */   
/*      */   private static String dsfNodeId;
/*      */   
/*      */   private static String dsfClusterId;
/*      */   
/*   52 */   private static volatile boolean isInited = false;
/*      */   
/*   54 */   private static final Object LOCK = new Object();
/*      */   
/*      */   public static void init()
/*      */   {
/*   58 */     if (!isInited)
/*      */     {
/*   60 */       synchronized (LOCK)
/*      */       {
/*   62 */         if (!isInited)
/*      */         {
/*   64 */           dsfNodeId = System.getProperty("PAAS_HOST_ID");
/*   65 */           if (null == dsfNodeId)
/*      */           {
/*   67 */             dsfNodeId = System.getenv("PAAS_HOST_ID");
/*   68 */             if (null == dsfNodeId)
/*      */             {
/*   70 */               dsfNodeId = USFConfig.getRpcAddress();
/*      */             }
/*      */           }
/*      */           
/*   74 */           dsfClusterId = System.getProperty("PAAS_CLUSTER_ID");
/*      */           
/*      */ 
/*   77 */           if (null == dsfClusterId)
/*      */           {
/*   79 */             dsfClusterId = System.getenv("PAAS_CLUSTER_ID");
/*   80 */             if (null == dsfClusterId)
/*      */             {
/*   82 */               dsfClusterId = "";
/*      */             }
/*      */           }
/*      */           
/*   86 */           initializeTraceLink();
/*   87 */           isInited = true;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static void initializeTraceLink()
/*      */   {
/*   96 */     int traceLinkSeqNumDepth = USFConfig.getTraceLinkSeqNumDepth();
/*   97 */     TraceManager.setServerHeader(String.valueOf(traceLinkSeqNumDepth));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void clientLinkTraceBegin(Context context)
/*      */   {
/*  109 */     if (!USFConfig.isTraceSwitch())
/*      */     {
/*  111 */       LOGGER.debug("client trace switch is false.");
/*  112 */       return;
/*      */     }
/*  114 */     TraceContext traceContext = TraceContextHolder.getDataContext();
/*  115 */     boolean isHeadFlag = false;
/*      */     
/*  117 */     Object traceFlagObj = traceContext.getContext("TraceFlag");
/*      */     
/*  119 */     String traceId = traceContext.getTraceID();
/*  120 */     String hasComparedTrace = getHasComparedTrace(traceContext);
/*  121 */     if (null == traceFlagObj)
/*      */     {
/*  123 */       if (StringUtils.isEmpty(traceId))
/*      */       {
/*      */ 
/*  126 */         isHeadFlag = true;
/*  127 */         context.addAttachment("headFlag", "true");
/*  128 */         if (sampleTrace())
/*      */         {
/*  130 */           if ("true".equals(hasComparedTrace))
/*      */           {
/*  132 */             TraceManager.setTraceFlag(6, traceContext);
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*  137 */             TraceManager.setTraceFlag(2, traceContext);
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*  143 */         else if (LogTraceUtil.isTraceLogEnable())
/*      */         {
/*  145 */           LOGGER.debug("client trace log is true,it will print tracelink log.");
/*  146 */           if ("true".equals(hasComparedTrace))
/*      */           {
/*  148 */             TraceManager.setTraceFlag(6, traceContext);
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*  153 */             TraceManager.setTraceFlag(2, traceContext);
/*      */           }
/*      */           
/*      */         }
/*      */         else
/*      */         {
/*  159 */           TraceManager.setTraceFlag(0, traceContext);
/*      */           
/*  161 */           TraceManager.setTraceID("0");
/*      */         }
/*      */         
/*      */       }
/*  165 */       else if ("0".equals(traceId))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  170 */         TraceManager.setTraceFlag(0, traceContext);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*  177 */       else if ("true".equals(hasComparedTrace))
/*      */       {
/*  179 */         TraceManager.setTraceFlag(6, traceContext);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  184 */         TraceManager.setTraceFlag(2, traceContext);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  190 */     boolean isAsync = context.getSrcConnector().isAsync();
/*  191 */     String serviceName = context.getReceivedMessage().getHeaders().getServiceName();
/*      */     
/*  193 */     MessageType msgType = isAsync ? MessageType.MSGSEND : MessageType.CLIENTSEND;
/*      */     
/*  195 */     String callingNodeID = getCallingNodeIDFromCache(traceContext, serviceName, context);
/*      */     
/*  197 */     context.addAttachment("CallingNodeID", callingNodeID);
/*  198 */     ITraceLinkMessage linkMessage = createTraceLinkMessage(context, TraceConst.TerminalType.CLIENT, msgType, traceContext);
/*      */     
/*  200 */     if (null != linkMessage)
/*      */     {
/*  202 */       linkMessage.setProperty("callingNodeID", callingNodeID);
/*      */       
/*  204 */       linkMessage.setServerFlag(false);
/*  205 */       linkMessage.setUpdatedFlag(true);
/*  206 */       linkMessage.setTsTraceHead(isHeadFlag);
/*  207 */       if (isAsync)
/*      */       {
/*  209 */         linkMessage.commitMsg(Boolean.valueOf(true), false);
/*  210 */         context.addAttachment("traceId", traceContext.getTraceID());
/*      */         
/*  212 */         context.addAttachment("seqNo", linkMessage.getSeqNo());
/*  213 */         setInfo2Req(traceContext.getTraceID(), linkMessage.getSeqNo(), traceContext.getTraceFlag(), context, traceContext);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  218 */         if (isHeadFlag)
/*      */         {
/*  220 */           linkMessage.begin(true);
/*      */         }
/*      */         else
/*      */         {
/*  224 */           linkMessage.begin(false);
/*      */         }
/*  226 */         setInfo2Req(traceContext.getTraceID(), traceContext.getSeqNo(), traceContext.getTraceFlag(), context, traceContext);
/*      */         
/*  228 */         context.addAttachment("clientLinkMessage", linkMessage);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  233 */     ProcessDelayTracker tracker = context.getProcessDelayTracker();
/*  234 */     if (null != tracker)
/*      */     {
/*  236 */       tracker.setTraceId(TraceManager.getTraceID(traceContext));
/*      */     }
/*      */     
/*  239 */     if ((isAsync) && (isHeadFlag))
/*      */     {
/*  241 */       traceContext.clearTraceLinkInfo();
/*      */     }
/*  243 */     clearTraceInfoInDSFContent();
/*      */   }
/*      */   
/*      */ 
/*      */   private static String getCallingNodeIDFromCache(TraceContext traceContext, String serviceName, Context context)
/*      */   {
/*  249 */     String callingNodeID = null;
/*  250 */     if (traceContext.getContexts().containsKey("CallingNodeID"))
/*      */     {
/*  252 */       callingNodeID = (String)traceContext.getContext("CallingNodeID");
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  257 */       callingNodeID = (String)callingNodeMapper.get(serviceName);
/*  258 */       if (null == callingNodeID)
/*      */       {
/*  260 */         synchronized (CALLING_NODE_LOCK)
/*      */         {
/*  262 */           callingNodeID = (String)callingNodeMapper.get(serviceName);
/*  263 */           if (null == callingNodeID)
/*      */           {
/*  265 */             callingNodeMapper.put(serviceName, generateID("PAAS_HOST_ID", "UnsetCallingNodeId"));
/*      */             
/*      */ 
/*      */ 
/*  269 */             callingNodeID = (String)callingNodeMapper.get(serviceName);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  275 */     return callingNodeID;
/*      */   }
/*      */   
/*      */   private static String getCallingService(TraceContext traceContext)
/*      */   {
/*  280 */     String callingService = "";
/*  281 */     if (traceContext.getContexts().containsKey("callingService"))
/*      */     {
/*  283 */       callingService = (String)traceContext.getContext("callingService");
/*      */     }
/*      */     
/*  286 */     return callingService;
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
/*      */   public static void clientlinkTraceEnd(Context context, MessageType msgType)
/*      */   {
/*  300 */     if (!USFConfig.isTraceSwitch())
/*      */     {
/*  302 */       LOGGER.debug("client trace switch is false.");
/*  303 */       return;
/*      */     }
/*      */     
/*  306 */     IMessage replyMessage = context.getReplyMessage();
/*  307 */     String traceFlagStr = null;
/*  308 */     if (null != replyMessage)
/*      */     {
/*  310 */       traceFlagStr = replyMessage.getHeaders().getAttachValue("TraceFlag");
/*      */     }
/*      */     
/*  313 */     TraceContext traceContext = TraceContextHolder.getDataContext();
/*  314 */     if (!StringUtils.isEmpty(traceFlagStr))
/*      */     {
/*      */       try
/*      */       {
/*      */ 
/*  319 */         int traceFlag = Integer.parseInt(traceFlagStr);
/*  320 */         traceContext.setTraceFlag(traceFlag);
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*  324 */         LOGGER.error("Parse the traceFlag failed.The traceFlag is " + traceFlagStr, e);
/*      */       }
/*      */     }
/*      */     
/*  328 */     if (context.getSrcConnector().isAsync())
/*      */     {
/*  330 */       asyncClientLinkTraceEnd(context);
/*  331 */       return;
/*      */     }
/*      */     
/*  334 */     ITraceLinkMessage linkMessage = (ITraceLinkMessage)context.getAttachment("clientLinkMessage");
/*      */     
/*  336 */     if (null != linkMessage)
/*      */     {
/*  338 */       clientEndSetProperties(context, linkMessage, msgType, traceContext);
/*  339 */       Object headLink = context.getAttachment("headFlag");
/*  340 */       if (null != headLink)
/*      */       {
/*  342 */         linkMessage.end(true);
/*      */       }
/*      */       else
/*      */       {
/*  346 */         linkMessage.end(false);
/*      */       }
/*      */     }
/*  349 */     clearTraceInfoInDSFContent();
/*      */   }
/*      */   
/*      */   private static void asyncClientLinkTraceEnd(Context context)
/*      */   {
/*  354 */     TraceContext traceContext = TraceContextHolder.getDataContext();
/*  355 */     Object traceId = context.getAttachment("traceId");
/*  356 */     if (null != traceId)
/*      */     {
/*  358 */       traceContext.setTraceID(String.valueOf(traceId));
/*      */     }
/*  360 */     Object seqNo = context.getAttachment("seqNo");
/*  361 */     if (null != seqNo)
/*      */     {
/*  363 */       traceContext.setBaseSeqNo(String.valueOf(seqNo));
/*      */     }
/*  365 */     ITraceLinkMessage linkMessage = createTraceLinkMessage(context, TraceConst.TerminalType.CLIENT, MessageType.MSGRECV, traceContext);
/*      */     
/*      */ 
/*  368 */     if (null != linkMessage)
/*      */     {
/*  370 */       clientEndSetProperties(context, linkMessage, MessageType.MSGRECV, traceContext);
/*      */       
/*  372 */       linkMessage.commitMsg(Boolean.valueOf(true), true);
/*      */     }
/*  374 */     clearTraceInfoInDSFContent();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void serverLinkTraceBegin(Context context)
/*      */   {
/*  386 */     if (!USFConfig.isTraceSwitch())
/*      */     {
/*  388 */       LOGGER.debug("server trace switch is false.");
/*  389 */       return;
/*      */     }
/*  391 */     TraceContext traceContext = TraceContextHolder.getDataContext();
/*      */     
/*  393 */     if (context.isServer())
/*      */     {
/*  395 */       traceContext.clearTraceLinkInfo();
/*      */     }
/*  397 */     IMessage reqMsg = context.getReceivedMessage();
/*  398 */     String traceId = getMsgAttrValue(reqMsg, "traceId", "");
/*  399 */     String traceFlagStr = getMsgAttrValue(reqMsg, "TraceFlag", "");
/*      */     
/*  401 */     String hasComparedTrace = getMsgAttrValue(reqMsg, "hasComparedTrace", "false");
/*      */     
/*  403 */     if ((!StringUtils.isEmpty(traceFlagStr)) && (StringUtils.isEmpty(traceId)))
/*      */     {
/*  405 */       traceId = "0";
/*      */     }
/*  407 */     int traceFlag = parseInt(traceFlagStr, traceId, hasComparedTrace, 0);
/*  408 */     traceContext.setTraceFlag(traceFlag);
/*  409 */     traceContext.setTraceID(traceId);
/*  410 */     if (context.isServer())
/*      */     {
/*  412 */       traceContext.setBaseSeqNo(getMsgAttrValue(reqMsg, "seqNo", ""));
/*      */     }
/*      */     
/*      */ 
/*  416 */     String callNumberStr = getMsgAttrValue(reqMsg, "callNumber", "");
/*      */     
/*  418 */     int callNumber = 0;
/*  419 */     if (!StringUtils.isEmpty(callNumberStr))
/*      */     {
/*      */       try
/*      */       {
/*  423 */         callNumber = Integer.parseInt(callNumberStr);
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*  427 */         LOGGER.error("Parse the callNumber failed.The callNumber is " + callNumber, e);
/*      */       }
/*      */     }
/*      */     
/*  431 */     traceContext.setCallNumber(callNumber);
/*  432 */     USFContext.getContext().removeAttribute("ExtendInfo");
/*      */     try
/*      */     {
/*  435 */       ITraceLinkMessage linkMessage = createTraceLinkMessage(context, TraceConst.TerminalType.SERVER, MessageType.SERVERRECV, traceContext);
/*      */       
/*      */ 
/*  438 */       if (null != linkMessage)
/*      */       {
/*  440 */         linkMessage.setServerFlag(true);
/*  441 */         linkMessage.setUpdatedFlag(true);
/*  442 */         linkMessage.begin(false);
/*  443 */         context.addAttachment("serverLinkMessage", linkMessage);
/*      */       }
/*      */       
/*  446 */       if (LOGGER.isDebugEnable())
/*      */       {
/*  448 */         LOGGER.debug("server trace link begin,seqNo:" + traceContext.getSeqNo() + " traceid:" + traceContext.getTraceID() + " traceFlag:" + traceContext.getTraceFlag());
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*      */ 
/*  456 */       LOGGER.error("link trace end failed. ", e);
/*      */     }
/*      */     
/*  459 */     ProcessDelayTracker tracker = context.getProcessDelayTracker();
/*  460 */     if (null != tracker)
/*      */     {
/*  462 */       tracker.setTraceId(traceId);
/*      */     }
/*  464 */     clearTraceInfoInDSFContent();
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
/*      */   public static void serverlinkTraceEnd(Context context)
/*      */   {
/*  477 */     if (!USFConfig.isTraceSwitch())
/*      */     {
/*  479 */       LOGGER.debug("server trace switch is false.");
/*  480 */       return;
/*      */     }
/*  482 */     ITraceLinkMessage linkMessage = (ITraceLinkMessage)context.getAttachment("serverLinkMessage");
/*      */     
/*  484 */     TraceContext traceContext = TraceContextHolder.getDataContext();
/*  485 */     setInfoToRsp(context, traceContext);
/*  486 */     if (null != linkMessage)
/*      */     {
/*  488 */       serverEndSetLinkProperties(context, linkMessage, MessageType.SERVERSEND, traceContext);
/*      */       
/*  490 */       linkMessage.end(false);
/*      */     }
/*  492 */     clearTraceInfoInDSFContent();
/*      */   }
/*      */   
/*      */   public static void linkTraceEnd(Context context)
/*      */   {
/*  497 */     if (context.isServer())
/*      */     {
/*  499 */       serverlinkTraceEnd(context);
/*      */ 
/*      */ 
/*      */     }
/*  503 */     else if (!context.getSrcConnector().isAsync())
/*      */     {
/*  505 */       clientlinkTraceEnd(context, MessageType.CLIENTRECV);
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
/*      */ 
/*      */ 
/*      */   private static boolean isComparedTrace(int traceFlag, String retCode)
/*      */   {
/*  522 */     boolean flag = false;
/*  523 */     if ((traceFlag & 0x4) == 4)
/*      */     {
/*  525 */       flag = true;
/*      */     }
/*  527 */     else if (!"0".equals(retCode))
/*      */     {
/*  529 */       flag = true;
/*  530 */       TraceManager.setTraceFlag(6);
/*      */     }
/*  532 */     return flag;
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
/*      */   private static void setCodeStream2LinkMessage(Context ctx, MessageType msgType, ITraceLinkMessage linkMessage)
/*      */   {
/*  546 */     if (null == linkMessage)
/*      */     {
/*  548 */       return;
/*      */     }
/*  550 */     if ((msgType == MessageType.CLIENTSEND) || (msgType == MessageType.SERVERRECV) || (msgType == MessageType.MSGSEND))
/*      */     {
/*      */ 
/*      */ 
/*  554 */       setRequestCodeStream2LinkMessage(ctx, linkMessage);
/*      */     }
/*      */     else
/*      */     {
/*  558 */       setReponseCodeStream2LinkMessage(ctx, linkMessage);
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
/*      */   private static void setRequestCodeStream2LinkMessage(Context ctx, ITraceLinkMessage linkMessage)
/*      */   {
/*  572 */     StringBuilder builder = new StringBuilder();
/*  573 */     IMessage reqMessage = ctx.getReceivedMessage();
/*  574 */     builder.append(reqMessage.getHeaders().toString());
/*  575 */     builder.append(",parameterObjects=");
/*  576 */     Object payload = reqMessage.getPayload();
/*  577 */     if ((payload instanceof Object[]))
/*      */     {
/*  579 */       builder.append(Arrays.toString((Object[])reqMessage.getPayload()));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  585 */     linkMessage.setProperty("CodeStream", builder.toString());
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
/*      */   private static void setReponseCodeStream2LinkMessage(Context ctx, ITraceLinkMessage linkMessage)
/*      */   {
/*  599 */     StringBuilder builder = new StringBuilder();
/*  600 */     IMessage rspMessage = ctx.getReplyMessage();
/*      */     
/*  602 */     if (rspMessage != null)
/*      */     {
/*  604 */       builder.append(rspMessage.getHeaders().toString());
/*  605 */       builder.append(",reply object: ");
/*  606 */       builder.append(rspMessage.getPayload());
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  611 */       IMessage reqMessage = ctx.getReceivedMessage();
/*  612 */       builder.append(reqMessage.getHeaders().toString());
/*  613 */       builder.append(",parameterObjects=");
/*  614 */       Object payload = reqMessage.getPayload();
/*  615 */       if ((payload instanceof Object[]))
/*      */       {
/*  617 */         builder.append(Arrays.toString((Object[])reqMessage.getPayload()));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  624 */       if (ctx.getException() != null)
/*      */       {
/*  626 */         builder.append(",exception=");
/*  627 */         builder.append(ctx.getException());
/*      */       }
/*      */     }
/*  630 */     linkMessage.setProperty("CodeStream", builder.toString());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static ITraceLinkMessage createTraceLinkMessage(Context ctx, TraceConst.TerminalType type, MessageType msgType, TraceContext traceContext)
/*      */   {
/*  638 */     ITraceLinkMessage linkMessage = null;
/*      */     
/*      */     try
/*      */     {
/*  642 */       linkMessage = TraceFactory.createTraceLinkMessage("", "", msgType.toString(), "", "", ServiceType.USF.toString(), 6);
/*      */       
/*      */ 
/*      */ 
/*  646 */       if (null != linkMessage)
/*      */       {
/*      */ 
/*  649 */         linkMessage.setIsNeedPreStatistic(true);
/*  650 */         linkMessage.setIsSendToPreStatistic(true);
/*  651 */         if (TraceConst.TerminalType.CLIENT == type)
/*      */         {
/*  653 */           clientBeginSetLinkProperties(ctx, linkMessage, msgType, traceContext);
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  658 */           serverBeginSetLinkProperties(ctx, linkMessage, msgType, traceContext);
/*      */         }
/*      */         
/*      */       }
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*  665 */       LOGGER.error("link trace end failed. ", e);
/*      */     }
/*  667 */     return linkMessage;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static ITraceLinkMessage createTraceLinkMessage(TraceConst.TerminalType type, MessageType msgType, TraceContext traceContext, ContextHolder contextHolder)
/*      */   {
/*  674 */     ITraceLinkMessage linkMessage = null;
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  679 */       linkMessage = TraceFactory.createTraceLinkMessage("", "", msgType.toString(), "", "", contextHolder.getProxyProtocol(), 6);
/*      */       
/*      */ 
/*      */ 
/*  683 */       if (null != linkMessage)
/*      */       {
/*      */ 
/*  686 */         linkMessage.setIsNeedPreStatistic(true);
/*  687 */         linkMessage.setIsSendToPreStatistic(true);
/*  688 */         linkMessage.setProperty("appName", contextHolder.getProxyOperation());
/*      */         
/*  690 */         linkMessage.setProperty("callingNodeID", generateID("PAAS_HOST_ID", "UnsetCallingNodeId"));
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  695 */         linkMessage.setProperty("calledService", contextHolder.getProxyService());
/*      */       }
/*      */       
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*  701 */       LOGGER.error("link trace end failed. ", e);
/*      */     }
/*  703 */     return linkMessage;
/*      */   }
/*      */   
/*      */   public static TraceLinkInfo headServerRec(ContextHolder contextHolder)
/*      */   {
/*  708 */     TraceLinkInfo traceLinkInfo = new TraceLinkInfo();
/*  709 */     TraceContext traceContext = TraceContextHolder.getDataContext();
/*  710 */     boolean isHeadFlag = checkHead();
/*  711 */     ITraceLinkMessage linkMessage = createTraceLinkMessage(TraceConst.TerminalType.SERVER, MessageType.SERVERRECV, traceContext, contextHolder);
/*      */     
/*      */ 
/*  714 */     traceLinkInfo.setHeadFlag(isHeadFlag);
/*  715 */     traceLinkInfo.setLinkMessage(linkMessage);
/*  716 */     traceLinkInfo.setContextHolder(contextHolder);
/*  717 */     if (null != linkMessage)
/*      */     {
/*  719 */       linkMessage.setTsTraceHead(isHeadFlag);
/*  720 */       linkMessage.setUpdatedFlag(true);
/*  721 */       linkMessage.setServerFlag(true);
/*  722 */       if (isHeadFlag)
/*      */       {
/*  724 */         linkMessage.begin(true);
/*      */       }
/*      */       else
/*      */       {
/*  728 */         linkMessage.begin(false);
/*      */       }
/*      */     }
/*      */     
/*  732 */     return traceLinkInfo;
/*      */   }
/*      */   
/*      */ 
/*      */   public static void headServerSend(TraceLinkInfo traceLinkInfo, Context context)
/*      */   {
/*  738 */     if (null == traceLinkInfo)
/*      */     {
/*  740 */       return;
/*      */     }
/*  742 */     ITraceLinkMessage linkMessage = traceLinkInfo.getLinkMessage();
/*  743 */     linkMessage.setProperty("calledNodeID", generateID("CalledNodeId", traceLinkInfo.getContextHolder().getProxyService()));
/*      */     
/*      */ 
/*      */ 
/*  747 */     linkMessage.setProperty("calledClusterId", generateID("PAAS_CLUSTER_ID", traceLinkInfo.getContextHolder().getProxyService()));
/*      */     
/*      */ 
/*      */ 
/*  751 */     String retCode = getResultCodeInEnd(context);
/*  752 */     if (StringUtils.isNotEmpty(retCode))
/*      */     {
/*  754 */       linkMessage.setProperty("resultCode", retCode);
/*      */     }
/*  756 */     if (traceLinkInfo.isHeadFlag())
/*      */     {
/*  758 */       linkMessage.end(true);
/*      */     }
/*      */     else
/*      */     {
/*  762 */       linkMessage.end(false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void clientBeginSetLinkProperties(Context ctx, ITraceLinkMessage linkMessage, MessageType msgType, TraceContext traceContext)
/*      */   {
/*  771 */     IMessage reqMsg = ctx.getReceivedMessage();
/*  772 */     if (null == reqMsg)
/*      */     {
/*  774 */       return;
/*      */     }
/*  776 */     linkMessage.setProperty("callingNodeID", getMsgAttrValue(reqMsg, "CallingNodeID", ""));
/*      */     
/*  778 */     if (isComparedTrace(traceContext.getTraceFlag(), "0"))
/*      */     {
/*      */ 
/*  781 */       setCodeStream2LinkMessage(ctx, msgType, linkMessage);
/*      */     }
/*  783 */     String extendInfo = getAndRemoveExtendInfo();
/*  784 */     if (!StringUtils.isEmpty(extendInfo))
/*      */     {
/*  786 */       linkMessage.setProperty("extendInfo", extendInfo);
/*      */       
/*  788 */       ctx.addAttachment("ExtendInfo", extendInfo);
/*      */     }
/*  790 */     linkMessage.setProperty("appName", reqMsg.getHeaders().getOperation());
/*      */     
/*      */ 
/*  793 */     String callingService = getCallingService(traceContext);
/*  794 */     linkMessage.setProperty("callingService", callingService);
/*  795 */     linkMessage.setProperty("calledService", reqMsg.getHeaders().getServiceName());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void clientEndSetProperties(Context ctx, ITraceLinkMessage linkMessage, MessageType msgType, TraceContext traceContext)
/*      */   {
/*  803 */     IMessage reqMsg = ctx.getReceivedMessage();
/*  804 */     if (null == reqMsg)
/*      */     {
/*  806 */       return;
/*      */     }
/*  808 */     String retCode = getResultCodeInEnd(ctx);
/*  809 */     String resultInfo = getResultInfoInEnd(ctx);
/*  810 */     if (StringUtils.isNotEmpty(retCode))
/*      */     {
/*  812 */       linkMessage.setProperty("resultCode", retCode);
/*      */     }
/*  814 */     if (StringUtils.isNotEmpty(resultInfo))
/*      */     {
/*  816 */       linkMessage.setProperty("resultInfo", resultInfo);
/*      */     }
/*      */     
/*      */ 
/*  820 */     if (isComparedTrace(traceContext.getTraceFlag(), retCode))
/*      */     {
/*  822 */       setCodeStream2LinkMessage(ctx, msgType, linkMessage);
/*      */     }
/*      */     
/*  825 */     linkMessage.setProperty("reqMsgSize", getMsgAttrValue(reqMsg, "reqSize", ""));
/*      */     
/*  827 */     IMessage replyMessage = ctx.getReplyMessage();
/*  828 */     String calledClusterID = null;
/*  829 */     String calledNodeID = null;
/*  830 */     if (null != replyMessage)
/*      */     {
/*  832 */       calledClusterID = getMsgAttrValue(replyMessage, "CalledClusterId", "");
/*      */       
/*  834 */       calledNodeID = getMsgAttrValue(replyMessage, "CalledNodeId", "");
/*      */       
/*  836 */       linkMessage.setProperty("rspMsgSize", getMsgAttrValue(replyMessage, "rspSize", ""));
/*      */     }
/*      */     
/*      */ 
/*  840 */     Object extendInfo = ctx.getAttachment("ExtendInfo");
/*  841 */     if (null != extendInfo)
/*      */     {
/*  843 */       linkMessage.setProperty("extendInfo", (String)extendInfo);
/*      */     }
/*      */     
/*  846 */     linkMessage.setProperty("calledClusterId", calledClusterID);
/*      */     
/*  848 */     linkMessage.setProperty("calledNodeID", calledNodeID);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void serverBeginSetLinkProperties(Context ctx, ITraceLinkMessage linkMessage, MessageType msgType, TraceContext traceContext)
/*      */   {
/*  856 */     IMessage reqMsg = ctx.getReceivedMessage();
/*  857 */     if (null == reqMsg)
/*      */     {
/*  859 */       return;
/*      */     }
/*  861 */     linkMessage.setProperty("callingNodeID", getMsgAttrValue(reqMsg, "CallingNodeID", ""));
/*      */     
/*      */ 
/*  864 */     linkMessage.setProperty("reqMsgSize", getMsgAttrValue(reqMsg, "reqSize", ""));
/*      */     
/*  866 */     if (isComparedTrace(traceContext.getTraceFlag(), "0"))
/*      */     {
/*      */ 
/*  869 */       setCodeStream2LinkMessage(ctx, msgType, linkMessage);
/*      */     }
/*  871 */     String serviceName = reqMsg.getHeaders().getServiceName();
/*  872 */     String calledNodeID = getCalledNodeIDFromCache(serviceName, ctx);
/*      */     
/*  874 */     traceContext.setContext("CallingNodeID", calledNodeID);
/*  875 */     ctx.addAttachment("PAAS_HOST_ID", calledNodeID);
/*  876 */     String calledClusterID = getCalledClusterIDFromCache(serviceName, ctx);
/*  877 */     ctx.addAttachment("PAAS_CLUSTER_ID", calledClusterID);
/*  878 */     linkMessage.setProperty("extendInfo", getAndRemoveExtendInfo());
/*      */     
/*  880 */     linkMessage.setProperty("appName", reqMsg.getHeaders().getOperation());
/*      */     
/*  882 */     linkMessage.setProperty("calledClusterId", calledClusterID);
/*      */     
/*  884 */     linkMessage.setProperty("calledNodeID", calledNodeID);
/*      */     
/*      */ 
/*  887 */     String callingService = getMsgAttrValue(reqMsg, "callingService", "");
/*      */     
/*  889 */     if (!StringUtils.isEmpty(callingService))
/*      */     {
/*  891 */       linkMessage.setProperty("callingService", callingService);
/*      */     }
/*  893 */     linkMessage.setProperty("calledService", serviceName);
/*      */     
/*  895 */     traceContext.setContext("callingService", serviceName);
/*      */   }
/*      */   
/*      */ 
/*      */   private static String getCalledNodeIDFromCache(String serviceName, Context ctx)
/*      */   {
/*  901 */     String calledNodeID = (String)calledNodeMapper.get(serviceName);
/*  902 */     if (null == calledNodeID)
/*      */     {
/*  904 */       synchronized (CALLED_NODE_LOCK)
/*      */       {
/*  906 */         calledNodeID = (String)calledNodeMapper.get(serviceName);
/*  907 */         if (null == calledNodeID)
/*      */         {
/*  909 */           calledNodeMapper.put(serviceName, generateID("PAAS_HOST_ID", serviceName));
/*      */           
/*  911 */           calledNodeID = (String)calledNodeMapper.get(serviceName);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  916 */     return calledNodeID;
/*      */   }
/*      */   
/*      */ 
/*      */   private static String getCalledClusterIDFromCache(String serviceName, Context ctx)
/*      */   {
/*  922 */     String calledClusterID = (String)calledClusterMapper.get(serviceName);
/*  923 */     if (null == calledClusterID)
/*      */     {
/*  925 */       synchronized (CALLED_CLUSTER_LOCK)
/*      */       {
/*  927 */         calledClusterID = (String)calledClusterMapper.get(serviceName);
/*  928 */         if (null == calledClusterID)
/*      */         {
/*  930 */           calledClusterMapper.put(serviceName, generateID("PAAS_CLUSTER_ID", serviceName));
/*      */           
/*  932 */           calledClusterID = (String)calledClusterMapper.get(serviceName);
/*      */         }
/*      */       }
/*      */     }
/*  936 */     return calledClusterID;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static void serverEndSetLinkProperties(Context ctx, ITraceLinkMessage linkMessage, MessageType msgType, TraceContext traceContext)
/*      */   {
/*  943 */     IMessage reqMsg = ctx.getReceivedMessage();
/*  944 */     if (null == reqMsg)
/*      */     {
/*  946 */       return;
/*      */     }
/*  948 */     String retCode = getResultCodeInEnd(ctx);
/*  949 */     String resultInfo = getResultInfoInEnd(ctx);
/*  950 */     if (StringUtils.isNotEmpty(retCode))
/*      */     {
/*  952 */       linkMessage.setProperty("resultCode", retCode);
/*      */     }
/*  954 */     if (StringUtils.isNotEmpty(resultInfo))
/*      */     {
/*  956 */       linkMessage.setProperty("resultInfo", resultInfo);
/*      */     }
/*      */     
/*  959 */     if (isComparedTrace(traceContext.getTraceFlag(), retCode))
/*      */     {
/*  961 */       setCodeStream2LinkMessage(ctx, msgType, linkMessage);
/*      */     }
/*  963 */     linkMessage.setProperty("extendInfo", getAndRemoveExtendInfo());
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
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void setInfo2Req(String traceId, String seqNo, int traceFlag, Context context, TraceContext traceContext)
/*      */   {
/*  981 */     IMessage reqMsg = context.getReceivedMessage();
/*  982 */     if (null == reqMsg)
/*      */     {
/*  984 */       return;
/*      */     }
/*  986 */     MessageHeaders msgHeaders = reqMsg.getHeaders();
/*      */     
/*  988 */     msgHeaders.setAttachValue("TraceFlag", String.valueOf(traceFlag));
/*      */     
/*  990 */     if (!"0".equals(traceId))
/*      */     {
/*  992 */       msgHeaders.setAttachValue("traceId", traceId);
/*      */     }
/*  994 */     msgHeaders.setAttachValue("seqNo", seqNo);
/*  995 */     String callingNodeID = String.valueOf(context.getAttachment("CallingNodeID"));
/*      */     
/*  997 */     msgHeaders.setAttachValue("CallingNodeID", callingNodeID);
/*  998 */     String hasComparedTrace = "false";
/*  999 */     if ((traceFlag & 0x4) == 4)
/*      */     {
/* 1001 */       hasComparedTrace = "true";
/*      */     }
/* 1003 */     msgHeaders.setAttachValue("hasComparedTrace", hasComparedTrace);
/*      */     
/*      */ 
/* 1006 */     msgHeaders.setAttachValue("callNumber", String.valueOf(traceContext.getCallNumber()));
/*      */     
/*      */ 
/* 1009 */     String callingService = getCallingService(traceContext);
/* 1010 */     msgHeaders.setAttachValue("callingService", callingService);
/* 1011 */     if (LOGGER.isDebugEnable())
/*      */     {
/* 1013 */       LOGGER.debug("client trace link begin,seq:" + seqNo + " traceid:" + traceId + " traceFlag:" + traceFlag + " set hasCompareTrace is " + hasComparedTrace);
/*      */       
/*      */ 
/* 1016 */       LOGGER.debug("get user set callingNodeId is " + callingNodeID);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void setInfoToRsp(Context context, TraceContext traceContext)
/*      */   {
/* 1028 */     IMessage rspMsg = context.getReplyMessage();
/* 1029 */     if (null == rspMsg)
/*      */     {
/* 1031 */       return;
/*      */     }
/* 1033 */     int traceFlag = traceContext.getTraceFlag();
/* 1034 */     MessageHeaders msgHeader = rspMsg.getHeaders();
/*      */     
/* 1036 */     Object nodeId = context.getAttachment("PAAS_HOST_ID");
/* 1037 */     if (null != nodeId)
/*      */     {
/* 1039 */       msgHeader.setAttachValue("CalledNodeId", String.valueOf(nodeId));
/*      */     }
/*      */     
/* 1042 */     Object clusterId = context.getAttachment("PAAS_CLUSTER_ID");
/* 1043 */     if (null != clusterId)
/*      */     {
/* 1045 */       msgHeader.setAttachValue("CalledClusterId", String.valueOf(clusterId));
/*      */     }
/*      */     
/* 1048 */     msgHeader.setAttachValue("TraceFlag", String.valueOf(traceFlag));
/*      */   }
/*      */   
/*      */ 
/*      */   private static String getResultInfoInEnd(Context ctx)
/*      */   {
/* 1054 */     String restultInfo = null;
/*      */     
/* 1056 */     restultInfo = getRetValue(ctx.getReplyMessage(), "ResultInfo", null);
/*      */     
/* 1058 */     if ((ctx.isServer()) && (ctx.getReplyMessage() != null))
/*      */     {
/*      */ 
/* 1061 */       ctx.getReplyMessage().getHeaders().setAttachValue("ResultInfo", restultInfo);
/*      */     }
/*      */     
/* 1064 */     return restultInfo;
/*      */   }
/*      */   
/*      */   private static boolean checkHead()
/*      */   {
/* 1069 */     TraceContext traceContext = TraceContextHolder.getDataContext();
/* 1070 */     boolean isHeadFlag = false;
/* 1071 */     Object traceFlagObj = traceContext.getContext("TraceFlag");
/*      */     
/* 1073 */     if (null == traceFlagObj)
/*      */     {
/* 1075 */       if (LogTraceUtil.isTraceLogEnable())
/*      */       {
/* 1077 */         LOGGER.debug("Trace log is true,it will print tracelink log.");
/* 1078 */         TraceManager.setTraceFlag(2, traceContext);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1083 */         TraceManager.setTraceFlag(0, traceContext);
/*      */       }
/*      */       
/* 1086 */       isHeadFlag = true;
/*      */     }
/* 1088 */     return isHeadFlag;
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
/*      */   private static String getResultCodeInEnd(Context ctx)
/*      */   {
/* 1102 */     String retCode = null;
/*      */     
/* 1104 */     IMessage replyMessage = ctx.getReplyMessage();
/* 1105 */     retCode = getRetValue(replyMessage, "retCode", null);
/* 1106 */     if ((ctx.isServer()) && (replyMessage != null))
/*      */     {
/*      */ 
/* 1109 */       replyMessage.getHeaders().setAttachValue("retCode", retCode);
/*      */     }
/*      */     
/*      */ 
/* 1113 */     if (null == retCode)
/*      */     {
/* 1115 */       if (((replyMessage != null) && ((replyMessage.getPayload() instanceof Throwable))) || (ctx.getException() != null) || (ctx.isEbusException()))
/*      */       {
/*      */ 
/* 1118 */         retCode = "-1";
/*      */       }
/*      */       else
/*      */       {
/* 1122 */         retCode = "0";
/*      */       }
/*      */     }
/* 1125 */     return retCode;
/*      */   }
/*      */   
/*      */ 
/*      */   private static String getMsgAttrValue(IMessage msg, String key, String defaultValue)
/*      */   {
/* 1131 */     if (null == msg)
/*      */     {
/* 1133 */       return defaultValue;
/*      */     }
/*      */     
/* 1136 */     Object obj = msg.getHeaders().getAttachment().get(key);
/* 1137 */     return obj == null ? defaultValue : obj.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String getExtendInfo()
/*      */   {
/* 1149 */     String extendInfo = "";
/* 1150 */     if (!USFContext.getContext().getAttributes().containsKey("ExtendInfo"))
/*      */     {
/*      */ 
/* 1153 */       return extendInfo;
/*      */     }
/* 1155 */     USFCtxObject usfctxObject = USFContext.getContext().getAttribute("ExtendInfo");
/*      */     
/* 1157 */     if (null == usfctxObject)
/*      */     {
/* 1159 */       return extendInfo;
/*      */     }
/* 1161 */     Object value = usfctxObject.getValue();
/* 1162 */     if (null != value)
/*      */     {
/* 1164 */       extendInfo = (String)value;
/*      */     }
/*      */     else
/*      */     {
/* 1168 */       return extendInfo;
/*      */     }
/* 1170 */     return extendInfo;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String getAndRemoveExtendInfo()
/*      */   {
/* 1181 */     String extendInfo = getExtendInfo();
/* 1182 */     if (StringUtils.isNotEmpty(extendInfo))
/*      */     {
/* 1184 */       USFContext.getContext().removeAttribute("ExtendInfo");
/*      */     }
/* 1186 */     return extendInfo;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void clearTraceInfoInDSFContent()
/*      */   {
/* 1197 */     USFContextUtil.removeAttribute("ExtendInfo");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static boolean sampleTrace()
/*      */   {
/* 1209 */     boolean isDebugEnable = LOGGER.isDebugEnable();
/*      */     
/* 1211 */     if (!USFConfig.getSampleSwitch())
/*      */     {
/* 1213 */       LOGGER.debug("sample trace flag is false");
/* 1214 */       return true;
/*      */     }
/*      */     
/*      */ 
/* 1218 */     long sampleRate = USFConfig.getSampleRate();
/* 1219 */     if (1L == sampleRate)
/*      */     {
/* 1221 */       LOGGER.debug("sample rate is " + sampleRate);
/* 1222 */       return true;
/*      */     }
/*      */     
/*      */ 
/* 1226 */     sendReqSize.getAndIncrement();
/* 1227 */     if (sendReqSize.longValue() == 9223372036854775806L)
/*      */     {
/* 1229 */       if (isDebugEnable)
/*      */       {
/* 1231 */         LOGGER.debug("sendReqSize count to max num：" + sendReqSize.longValue() + " now reset to 0");
/*      */       }
/*      */       
/* 1234 */       sendReqSize.set(0L);
/*      */     }
/* 1236 */     long total = sendReqSize.get();
/* 1237 */     long count = total % sampleRate;
/*      */     
/*      */ 
/* 1240 */     boolean sampledFlag = 1L == count;
/*      */     
/* 1242 */     if (isDebugEnable)
/*      */     {
/* 1244 */       LOGGER.debug("sampled flag:" + sampledFlag + ",call times:" + total + ",sample rate:" + sampleRate);
/*      */     }
/*      */     
/*      */ 
/* 1248 */     return sampledFlag;
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
/*      */   private static String generateID(String idType, String service)
/*      */   {
/* 1262 */     if ("PAAS_HOST_ID".equals(idType))
/*      */     {
/* 1264 */       return join(dsfNodeId, service);
/*      */     }
/*      */     
/*      */ 
/* 1268 */     return join(dsfClusterId, service);
/*      */   }
/*      */   
/*      */ 
/*      */   private static String join(String id, String service)
/*      */   {
/* 1274 */     return id + "." + service;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getHasComparedTrace(TraceContext tc)
/*      */   {
/* 1284 */     String hasComparedTrace = "false";
/* 1285 */     if (USFContext.getContext().getAttributes().containsKey("hasComparedTrace"))
/*      */     {
/*      */ 
/* 1288 */       hasComparedTrace = (String)USFContext.getContext().getAttribute("hasComparedTrace").getValue();
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1293 */       boolean comparedTrace = TraceManager.gethasComparedTrace(tc);
/* 1294 */       hasComparedTrace = comparedTrace ? "true" : "false";
/*      */     }
/*      */     
/*      */ 
/* 1298 */     return hasComparedTrace;
/*      */   }
/*      */   
/*      */ 
/*      */   private static int parseInt(String traceFlagStr, String traceId, String hasComparedTrace, int defaultValue)
/*      */   {
/* 1304 */     int traceFlag = defaultValue;
/* 1305 */     if (!StringUtils.isEmpty(traceFlagStr))
/*      */     {
/*      */       try
/*      */       {
/*      */ 
/* 1310 */         traceFlag = Integer.parseInt(traceFlagStr);
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/* 1314 */         LOGGER.error("Parse the traceFlag failed.The traceFlag is " + traceFlagStr, e);
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */     }
/* 1321 */     else if ((StringUtils.isEmpty(traceId)) || ("0".equals(traceId)))
/*      */     {
/*      */ 
/* 1324 */       traceFlag = 0;
/*      */ 
/*      */ 
/*      */     }
/* 1328 */     else if ("true".equals(hasComparedTrace))
/*      */     {
/* 1330 */       traceFlag = 6;
/*      */     }
/*      */     else
/*      */     {
/* 1334 */       traceFlag = 2;
/*      */     }
/*      */     
/*      */ 
/* 1338 */     return traceFlag;
/*      */   }
/*      */   
/*      */ 
/*      */   private static String getRetValue(IMessage msg, String key, String defaultValue)
/*      */   {
/* 1344 */     if (null == msg)
/*      */     {
/* 1346 */       return defaultValue;
/*      */     }
/*      */     
/* 1349 */     MessageHeaders headers = msg.getHeaders();
/*      */     
/* 1351 */     Object obj = headers.getAttachment().get(key);
/*      */     
/* 1353 */     if (null == obj)
/*      */     {
/* 1355 */       USFCtxObject attrs = (USFCtxObject)headers.getAttachment().get("attrs_map");
/*      */       
/* 1357 */       if (null != attrs)
/*      */       {
/* 1359 */         Map<String, Object> attrMap = (Map)attrs.getValue();
/*      */         
/* 1361 */         obj = attrMap.get(key);
/*      */       }
/*      */     }
/* 1364 */     return obj == null ? defaultValue : obj.toString();
/*      */   }
/*      */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\trace\TraceLinkUtilHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */