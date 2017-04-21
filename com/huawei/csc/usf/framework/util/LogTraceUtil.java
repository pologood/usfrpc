/*     */ package com.huawei.csc.usf.framework.util;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.kernel.api.log.trace.ITraceInfo;
/*     */ import com.huawei.csc.kernel.api.log.trace.TraceMgr;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.config.USFConfig;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.lang.StringUtils;
/*     */ import org.apache.log4j.MDC;
/*     */ 
/*     */ 
/*     */ public class LogTraceUtil
/*     */ {
/*  17 */   private static final DebugLog logger = LogFactory.getDebugLog(LogTraceUtil.class);
/*     */   
/*     */   public static final String TASK_ID_KEY = "traceTaskID";
/*     */   
/*     */   public static final String HAS_CMP_KEY = "hasComparedLogTrace";
/*     */   
/*     */   public static final String ETRACE_ID_KEY = "eTraceID";
/*     */   
/*     */   public static final String ETRACE_PACKAGE_NAME = "packageName";
/*     */   
/*     */   public static final String TRACE_ORDER_KEY = "traceOrder";
/*     */   
/*     */   public static final String SERVICE_NAME_KEY = "serviceName";
/*     */   
/*     */   public static final String OPERATION_NAME_KEY = "operationName";
/*     */   
/*     */ 
/*     */   public static boolean isTraceLogEnable()
/*     */   {
/*  36 */     return (USFConfig.getLogTraceSwitch()) && (TraceMgr.getTraceInfo().hasComparedLogTasks());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void setTraceInfo(IMessage reqMsg, String eTraceID, String taskIDs, String packageName, boolean hasComparedTask, int traceOrder)
/*     */   {
/*  43 */     reqMsg.getHeaders().getAttachment().put("traceTaskID", new USFCtxObject(taskIDs));
/*     */     
/*     */ 
/*  46 */     reqMsg.getHeaders().getAttachment().put("hasComparedLogTrace", new USFCtxObject(Boolean.valueOf(hasComparedTask)));
/*     */     
/*     */ 
/*  49 */     reqMsg.getHeaders().getAttachment().put("eTraceID", new USFCtxObject(eTraceID));
/*     */     
/*     */ 
/*  52 */     reqMsg.getHeaders().getAttachment().put("traceOrder", new USFCtxObject(Integer.valueOf(traceOrder)));
/*     */     
/*     */ 
/*  55 */     if (null != packageName)
/*     */     {
/*  57 */       reqMsg.getHeaders().getAttachment().put("packageName", new USFCtxObject(packageName));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void doStartTrace(IMessage reqMsg)
/*     */   {
/*  65 */     if ((!USFConfig.getLogTraceSwitch()) || (reqMsg == null))
/*     */     {
/*  67 */       logger.debug("trace log switch is off, or request message is null");
/*  68 */       return;
/*     */     }
/*     */     
/*  71 */     if (TraceMgr.getTraceInfo() == null)
/*     */     {
/*  73 */       logger.debug("trace info is null");
/*  74 */       return;
/*     */     }
/*     */     
/*  77 */     TraceMgr.addParameter("serviceName", reqMsg.getHeaders().getServiceName());
/*     */     
/*     */ 
/*  80 */     TraceMgr.addParameter("operationName", reqMsg.getHeaders().getOperation());
/*     */     
/*     */ 
/*  83 */     TraceMgr.commit();
/*     */     
/*  85 */     ITraceInfo info = TraceMgr.getTraceInfo();
/*     */     
/*  87 */     String eTraceID = "";
/*  88 */     String taskIDs = "";
/*  89 */     int traceOrder = 0;
/*  90 */     boolean hasComparedTrace = false;
/*     */     
/*  92 */     eTraceID = info.getTraceID();
/*  93 */     taskIDs = StringUtils.join(info.getLogTaskIDs(), ",");
/*  94 */     Object obj = MDC.get("traceOrder");
/*  95 */     if (null != obj)
/*     */     {
/*     */       try
/*     */       {
/*  99 */         traceOrder = Integer.parseInt(obj.toString());
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 103 */         logger.error("parse trace order failed.", e);
/*     */       }
/*     */     }
/* 106 */     MDC.put("traceOrder", Integer.valueOf(traceOrder));
/* 107 */     hasComparedTrace = info.hasComparedLogTasks();
/* 108 */     setTraceInfo(reqMsg, eTraceID, taskIDs, TraceMgr.getPackageName(), hasComparedTrace, traceOrder);
/*     */   }
/*     */   
/*     */   public static void doServerStartTraceLog(IMessage reqMsg)
/*     */   {
/* 113 */     if ((!USFConfig.getLogTraceSwitch()) || (reqMsg == null))
/*     */     {
/* 115 */       logger.debug("trace log switch is off, or request message is null");
/* 116 */       return;
/*     */     }
/*     */     
/*     */ 
/* 120 */     updateMessageTraceOrder(reqMsg);
/* 121 */     updateMDCTraceOrder(reqMsg);
/*     */     
/* 123 */     Map<String, USFCtxObject> attach = reqMsg.getHeaders().getAttachment();
/*     */     
/* 125 */     String eTraceID = "";
/* 126 */     String taskIDs = "";
/* 127 */     String packageName = null;
/* 128 */     boolean hasComparedLogTasks = false;
/*     */     
/* 130 */     if (attach.containsKey("eTraceID"))
/*     */     {
/* 132 */       eTraceID = ((USFCtxObject)attach.get("eTraceID")).toString();
/*     */     }
/*     */     
/* 135 */     if (attach.containsKey("traceTaskID"))
/*     */     {
/* 137 */       taskIDs = ((USFCtxObject)attach.get("traceTaskID")).toString();
/*     */     }
/*     */     
/* 140 */     if (attach.containsKey("hasComparedLogTrace"))
/*     */     {
/* 142 */       hasComparedLogTasks = Boolean.valueOf(((USFCtxObject)attach.get("hasComparedLogTrace")).toString()).booleanValue();
/*     */     }
/*     */     
/*     */ 
/* 146 */     if (attach.containsKey("packageName"))
/*     */     {
/* 148 */       packageName = ((USFCtxObject)attach.get("packageName")).toString();
/*     */     }
/*     */     
/*     */ 
/* 152 */     TraceMgr.removeTraceInfo();
/* 153 */     ITraceInfo etraceInfo = TraceMgr.getTraceInfo();
/* 154 */     etraceInfo.setTraceID(eTraceID);
/* 155 */     String[] taskIDArray = StringUtils.split(taskIDs, ",");
/* 156 */     for (String taskID : taskIDArray)
/*     */     {
/* 158 */       etraceInfo.addLogTaskID(taskID);
/*     */     }
/* 160 */     TraceMgr.getTraceInfo().setHasComparedLogTasks(hasComparedLogTasks);
/* 161 */     TraceMgr.setPackageName(packageName);
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
/*     */ 
/*     */   public static void doAsyncEndTraceLog(IMessage reqMsg)
/*     */   {
/* 188 */     if ((!USFConfig.getLogTraceSwitch()) || (reqMsg == null))
/*     */     {
/* 190 */       logger.debug("trace log switch is off, or request message is null");
/* 191 */       return;
/*     */     }
/*     */     
/* 194 */     Map<String, USFCtxObject> attach = reqMsg.getHeaders().getAttachment();
/*     */     
/* 196 */     String eTraceID = "";
/* 197 */     String taskIDs = "";
/* 198 */     String packageName = null;
/* 199 */     boolean hasComparedLogTasks = false;
/*     */     
/* 201 */     if (attach.containsKey("eTraceID"))
/*     */     {
/* 203 */       eTraceID = ((USFCtxObject)attach.get("eTraceID")).toString();
/*     */     }
/*     */     
/* 206 */     if (attach.containsKey("traceTaskID"))
/*     */     {
/* 208 */       taskIDs = ((USFCtxObject)attach.get("traceTaskID")).toString();
/*     */     }
/*     */     
/* 211 */     if (attach.containsKey("hasComparedLogTrace"))
/*     */     {
/* 213 */       hasComparedLogTasks = Boolean.valueOf(((USFCtxObject)attach.get("hasComparedLogTrace")).toString()).booleanValue();
/*     */     }
/*     */     
/*     */ 
/* 217 */     if (attach.containsKey("packageName"))
/*     */     {
/* 219 */       packageName = ((USFCtxObject)attach.get("packageName")).toString();
/*     */     }
/*     */     
/*     */ 
/* 223 */     TraceMgr.removeTraceInfo();
/* 224 */     ITraceInfo etraceInfo = TraceMgr.getTraceInfo();
/* 225 */     etraceInfo.setTraceID(eTraceID);
/* 226 */     String[] taskIDArray = StringUtils.split(taskIDs, ",");
/* 227 */     for (String taskID : taskIDArray)
/*     */     {
/* 229 */       etraceInfo.addLogTaskID(taskID);
/*     */     }
/* 231 */     TraceMgr.getTraceInfo().setHasComparedLogTasks(hasComparedLogTasks);
/* 232 */     TraceMgr.setPackageName(packageName);
/*     */   }
/*     */   
/*     */   public static void updateMessageTraceOrder(IMessage msg)
/*     */   {
/* 237 */     if ((!USFConfig.getLogTraceSwitch()) || (null == msg))
/*     */     {
/* 239 */       logger.debug("log trace switch is off, or request message is null");
/* 240 */       return;
/*     */     }
/*     */     
/* 243 */     String traceOrderStr = "";
/* 244 */     Map<String, USFCtxObject> attach = msg.getHeaders().getAttachment();
/* 245 */     if (attach.containsKey("traceOrder"))
/*     */     {
/* 247 */       traceOrderStr = ((USFCtxObject)attach.get("traceOrder")).toString();
/*     */     }
/*     */     
/* 250 */     if (!StringUtils.isEmpty(traceOrderStr))
/*     */     {
/*     */       try
/*     */       {
/* 254 */         int traceOrder = Integer.parseInt(traceOrderStr) + 1;
/*     */         
/* 256 */         msg.getHeaders().getAttachment().put("traceOrder", new USFCtxObject(Integer.valueOf(traceOrder)));
/*     */ 
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 261 */         logger.error("update message trace order failed.", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void updateMDCTraceOrder(IMessage msg)
/*     */   {
/* 272 */     if ((!USFConfig.getLogTraceSwitch()) || (null == msg))
/*     */     {
/* 274 */       logger.debug("log trace switch is off, or request message is null");
/* 275 */       return;
/*     */     }
/*     */     
/* 278 */     String traceOrderStr = "";
/* 279 */     Map<String, USFCtxObject> attach = msg.getHeaders().getAttachment();
/* 280 */     if (attach.containsKey("traceOrder"))
/*     */     {
/* 282 */       traceOrderStr = ((USFCtxObject)attach.get("traceOrder")).toString();
/*     */     }
/*     */     
/* 285 */     if (!StringUtils.isEmpty(traceOrderStr))
/*     */     {
/*     */       try
/*     */       {
/* 289 */         int traceOrder = Integer.parseInt(traceOrderStr);
/* 290 */         MDC.put("traceOrder", Integer.valueOf(traceOrder));
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 294 */         logger.error("update MDC trace order failed.", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void moveTraceInfo(IMessage from, IMessage to)
/*     */   {
/* 305 */     if (!USFConfig.getLogTraceSwitch())
/*     */     {
/* 307 */       return;
/*     */     }
/*     */     
/* 310 */     if ((null == from) || (null == to))
/*     */     {
/* 312 */       logger.debug("source or dest is empty, trace info move fail");
/* 313 */       return;
/*     */     }
/*     */     
/* 316 */     if (from.getHeaders().getAttachment().containsKey("traceTaskID"))
/*     */     {
/* 318 */       USFCtxObject value = (USFCtxObject)from.getHeaders().getAttachment().get("traceTaskID");
/*     */       
/* 320 */       to.getHeaders().getAttachment().put("traceTaskID", value);
/*     */     }
/*     */     
/* 323 */     if (from.getHeaders().getAttachment().containsKey("hasComparedLogTrace"))
/*     */     {
/* 325 */       USFCtxObject value = (USFCtxObject)from.getHeaders().getAttachment().get("hasComparedLogTrace");
/*     */       
/* 327 */       to.getHeaders().getAttachment().put("hasComparedLogTrace", value);
/*     */     }
/*     */     
/* 330 */     if (from.getHeaders().getAttachment().containsKey("eTraceID"))
/*     */     {
/* 332 */       USFCtxObject value = (USFCtxObject)from.getHeaders().getAttachment().get("eTraceID");
/*     */       
/* 334 */       to.getHeaders().getAttachment().put("eTraceID", value);
/*     */     }
/*     */     
/* 337 */     if (from.getHeaders().getAttachment().containsKey("traceOrder"))
/*     */     {
/* 339 */       USFCtxObject value = (USFCtxObject)from.getHeaders().getAttachment().get("traceOrder");
/*     */       
/* 341 */       to.getHeaders().getAttachment().put("traceOrder", value);
/*     */     }
/*     */     
/* 344 */     if (from.getHeaders().getAttachment().containsKey("packageName"))
/*     */     {
/* 346 */       USFCtxObject value = (USFCtxObject)from.getHeaders().getAttachment().get("packageName");
/*     */       
/* 348 */       to.getHeaders().getAttachment().put("packageName", value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\util\LogTraceUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */