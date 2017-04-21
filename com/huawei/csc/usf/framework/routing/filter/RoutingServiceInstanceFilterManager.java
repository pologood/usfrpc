/*     */ package com.huawei.csc.usf.framework.routing.filter;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
/*     */ import com.huawei.csc.usf.framework.util.Utils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class RoutingServiceInstanceFilterManager
/*     */ {
/*  30 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(RoutingServiceInstanceFilterManager.class);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  36 */   private List<RoutingServiceInstanceFilter> filters = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addFilter(RoutingServiceInstanceFilter filter)
/*     */   {
/*  47 */     this.filters.add(filter);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void clearFilters()
/*     */   {
/*  57 */     this.filters.clear();
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
/*     */   public void doFilter(List<ServiceInstanceInner> instances, RoutingServiceInstanceFilterContext routingContext, Context context)
/*     */   {
/*  75 */     if ((Utils.isEmpty(instances)) || (null == routingContext) || (null == context))
/*     */     {
/*     */ 
/*  78 */       return;
/*     */     }
/*     */     
/*     */ 
/*  82 */     for (RoutingServiceInstanceFilter filter : this.filters)
/*     */     {
/*     */ 
/*     */ 
/*  86 */       int beforeSize = instances.size();
/*  87 */       filter.filter(instances, context);
/*  88 */       int afterSize = instances.size();
/*     */       
/*     */ 
/*  91 */       if ((afterSize != beforeSize) && (LOGGER.isDebugEnable()))
/*     */       {
/*  93 */         StringBuilder msg = new StringBuilder();
/*  94 */         msg.append("Before filter[");
/*  95 */         msg.append(filter.getFilterName());
/*  96 */         msg.append("], the num of service instances is [");
/*  97 */         msg.append(beforeSize);
/*  98 */         msg.append("], but after the filter, the num is [");
/*  99 */         msg.append(afterSize);
/* 100 */         msg.append("].");
/* 101 */         LOGGER.debug(msg.toString());
/*     */       }
/* 103 */       routingContext.setAfterFilterServiceInstanceSize(filter, afterSize);
/*     */       
/*     */ 
/* 106 */       if (afterSize <= 0) {
/*     */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getFilterInfo(RoutingServiceInstanceFilterContext routingContext)
/*     */   {
/* 116 */     StringBuilder info = new StringBuilder();
/* 117 */     for (RoutingServiceInstanceFilter filter : this.filters)
/*     */     {
/* 119 */       int serviceSize = routingContext.getAfterFilterServiceInstanceSize(filter.getClass());
/*     */       
/* 121 */       if (serviceSize >= 0)
/*     */       {
/*     */ 
/* 124 */         info.append("Num of services after [" + filter.getFilterName() + "]:");
/* 125 */         info.append(serviceSize);
/* 126 */         info.append(".");
/*     */       }
/*     */     }
/* 129 */     return info.toString();
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\filter\RoutingServiceInstanceFilterManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */