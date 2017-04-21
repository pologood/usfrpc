/*     */ package com.huawei.csc.usf.framework.event;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.util.Utils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.lang.StringUtils;
/*     */ import org.springframework.beans.factory.InitializingBean;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractFrameworkEventHandler
/*     */   extends AbstractFrameworkEventHandlerSort
/*     */   implements IServiceFrameworkEventHandler, InitializingBean
/*     */ {
/*  20 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(AbstractFrameworkEventHandler.class);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  26 */   private List<String> eventNames = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  31 */   private String eventName = null;
/*     */   
/*     */   public List<String> getEventNames()
/*     */   {
/*  35 */     return this.eventNames;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setEventNames(List<String> eventNames)
/*     */   {
/*  41 */     this.eventNames = eventNames;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getEventName()
/*     */   {
/*  47 */     return this.eventName;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setEventName(String eventName)
/*     */   {
/*  53 */     this.eventName = eventName;
/*     */   }
/*     */   
/*     */   public void afterPropertiesSet()
/*     */     throws Exception
/*     */   {
/*  59 */     if (((null == this.eventNames) || (this.eventNames.isEmpty())) && (StringUtils.isEmpty(this.eventName)))
/*     */     {
/*     */ 
/*  62 */       if (DEBUGGER.isWarnEnable())
/*     */       {
/*  64 */         DEBUGGER.warn("The handler is [" + this + "] is not assign the subscribe eventName, this handler may unavailable.");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*  69 */       return;
/*     */     }
/*     */     
/*  72 */     if (!StringUtils.isEmpty(this.eventName))
/*     */     {
/*  74 */       setHandlerByEventName(this.eventName);
/*  75 */       return;
/*     */     }
/*     */     
/*  78 */     for (String eventName : this.eventNames)
/*     */     {
/*  80 */       setHandlerByEventName(eventName);
/*     */     }
/*     */   }
/*     */   
/*     */   private void setHandlerByEventName(String eventName)
/*     */   {
/*  86 */     List<IServiceFrameworkEventHandler> handlers = (List)ServiceFrameworkEventPublisher.handlerMap.get(eventName);
/*  87 */     if (Utils.isEmpty(handlers))
/*     */     {
/*  89 */       handlers = new ArrayList();
/*  90 */       handlers.add(this);
/*     */ 
/*     */ 
/*     */     }
/*  94 */     else if (!handlers.contains(this))
/*     */     {
/*  96 */       handlers.add(this);
/*     */     }
/*     */     
/*  99 */     Collections.sort(handlers, AbstractFrameworkEventHandlerSort.comparator);
/* 100 */     ServiceFrameworkEventPublisher.handlerMap.put(eventName, handlers);
/*     */   }
/*     */   
/*     */   public void destroy() {}
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\event\AbstractFrameworkEventHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */