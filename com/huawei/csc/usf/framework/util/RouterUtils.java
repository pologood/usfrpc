/*     */ package com.huawei.csc.usf.framework.util;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.Connector;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.sr.DsfZookeeperDataManager;
/*     */ import com.huawei.csc.usf.framework.sr.SRAgentFactory;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceRegistryAgent;
/*     */ import com.huawei.csc.usf.framework.sr.route.RouteRule;
/*     */ import com.huawei.csc.usf.framework.sr.route.RouteRuleUtils;
/*     */ import com.huawei.csc.usf.framework.sr.route.Router;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
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
/*     */ public class RouterUtils
/*     */ {
/*  42 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(RouterUtils.class);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void filterByRouterGovernance(Context context, String serviceName, String srcAddress, String operaion, String application, List<ServiceInstanceInner> instances)
/*     */   {
/*  49 */     ServiceRegistryAgent srAgent = context.getSrcConnector().getServiceEngine().getSrAgentFactory().getSRAgent(context.getRegistry());
/*     */     
/*     */ 
/*  52 */     List<Router> matchedRouters = srAgent.getZookeeperDataManager().getMatchedRouters(serviceName);
/*     */     
/*  54 */     if (matchedRouters.isEmpty())
/*     */     {
/*  56 */       return;
/*     */     }
/*     */     
/*  59 */     Map<String, String> consumerParams = getConsumerParams(srcAddress, operaion, application);
/*     */     
/*  61 */     Router resultRouter = null;
/*  62 */     for (int i = matchedRouters.size() - 1; i >= 0; i--)
/*     */     {
/*  64 */       Router router = (Router)matchedRouters.get(i);
/*  65 */       RouteRule routeRule = router.getRouteRule();
/*  66 */       if (RouteRuleUtils.matchRuleWhenCondition(consumerParams, routeRule))
/*     */       {
/*     */ 
/*  69 */         resultRouter = router;
/*  70 */         logMatchedRule(serviceName, consumerParams, resultRouter);
/*     */         
/*  72 */         break;
/*     */       }
/*     */       
/*     */ 
/*  76 */       logUnmatchedRule(serviceName, consumerParams, router);
/*     */     }
/*     */     
/*     */ 
/*  80 */     if (null == resultRouter)
/*     */     {
/*  82 */       return;
/*     */     }
/*     */     
/*  85 */     RouteRule routeRule = resultRouter.getRouteRule();
/*  86 */     Iterator<ServiceInstanceInner> iterator = instances.iterator();
/*  87 */     while (iterator.hasNext())
/*     */     {
/*  89 */       ServiceInstanceInner instanceInner = (ServiceInstanceInner)iterator.next();
/*  90 */       Map<String, String> params = getProviderParams(instanceInner);
/*  91 */       if ((params.isEmpty()) || (!RouteRuleUtils.matchRuleThenCondition(params, routeRule)))
/*     */       {
/*     */ 
/*     */ 
/*  95 */         iterator.remove();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static Map<String, String> getConsumerParams(String address, String operaion, String application)
/*     */   {
/* 103 */     Map<String, String> whenParams = new HashMap();
/* 104 */     whenParams.put("method", operaion);
/*     */     
/* 106 */     int indexOf = address.indexOf(":");
/* 107 */     String consumerIp = indexOf == -1 ? address : address.substring(0, indexOf);
/*     */     
/* 109 */     whenParams.put("host", consumerIp);
/*     */     
/* 111 */     whenParams.put("application", application);
/*     */     
/* 113 */     return whenParams;
/*     */   }
/*     */   
/*     */ 
/*     */   private static Map<String, String> getProviderParams(ServiceInstanceInner serviceInstance)
/*     */   {
/* 119 */     Map<String, String> thenParams = new HashMap();
/*     */     
/* 121 */     String address = serviceInstance.getAddress();
/* 122 */     String[] split = address.split(":");
/*     */     
/* 124 */     String host = split[0];
/* 125 */     String port = split[1];
/*     */     
/* 127 */     thenParams.put("host", host);
/* 128 */     thenParams.put("port", port);
/* 129 */     thenParams.put("application", serviceInstance.getApplication());
/*     */     
/* 131 */     return thenParams;
/*     */   }
/*     */   
/*     */ 
/*     */   private static void logMatchedRule(String serviceName, Map<String, String> consumerParams, Router resultRouter)
/*     */   {
/* 137 */     if (DEBUGGER.isInfoEnable())
/*     */     {
/* 139 */       StringBuilder builder = new StringBuilder();
/* 140 */       builder.append("Service ").append("[").append(serviceName).append("] ");
/*     */       
/* 142 */       builder.append("match the router: ").append(resultRouter.getName()).append(". ");
/*     */       
/* 144 */       String consumerInfo = getConsumerStatusLogInfo(consumerParams);
/* 145 */       builder.append(consumerInfo);
/* 146 */       builder.append(". ");
/* 147 */       builder.append("The router rule is {");
/* 148 */       builder.append(resultRouter.getRouteRule().toString()).append("}.");
/*     */       
/* 150 */       DEBUGGER.info(builder.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static void logUnmatchedRule(String serviceName, Map<String, String> consumerParams, Router resultRouter)
/*     */   {
/* 157 */     if (DEBUGGER.isInfoEnable())
/*     */     {
/* 159 */       StringBuilder builder = new StringBuilder();
/* 160 */       builder.append("Service ").append("[").append(serviceName).append("] ");
/*     */       
/* 162 */       builder.append("unmatch the router: ").append(resultRouter.getName()).append(". ");
/*     */       
/* 164 */       String consumerInfo = getConsumerStatusLogInfo(consumerParams);
/* 165 */       builder.append(consumerInfo);
/* 166 */       builder.append(". ");
/* 167 */       builder.append("The router rule is {");
/* 168 */       builder.append(resultRouter.getRouteRule().toString()).append("}.");
/*     */       
/* 170 */       DEBUGGER.info(builder.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static String getConsumerStatusLogInfo(Map<String, String> consumerParams)
/*     */   {
/* 177 */     StringBuilder builder = new StringBuilder();
/* 178 */     builder.append("Consumer status:");
/* 179 */     builder.append("{");
/* 180 */     int i = 0;
/* 181 */     for (Map.Entry<String, String> entry : consumerParams.entrySet())
/*     */     {
/* 183 */       if (i != 0)
/*     */       {
/* 185 */         builder.append(",");
/*     */       }
/* 187 */       String key = (String)entry.getKey();
/* 188 */       String value = (String)entry.getValue();
/* 189 */       builder.append(key).append(":").append(value);
/*     */       
/* 191 */       i++;
/*     */     }
/* 193 */     builder.append("}");
/*     */     
/* 195 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\util\RouterUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */