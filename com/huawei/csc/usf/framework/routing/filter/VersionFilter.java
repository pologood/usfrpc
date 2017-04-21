/*    */ package com.huawei.csc.usf.framework.routing.filter;
/*    */ 
/*    */ import com.huawei.csc.kernel.api.log.LogFactory;
/*    */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*    */ import com.huawei.csc.usf.framework.Context;
/*    */ import com.huawei.csc.usf.framework.IMessage;
/*    */ import com.huawei.csc.usf.framework.MessageHeaders;
/*    */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
/*    */ import com.huawei.csc.usf.framework.sr.VersionRange;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class VersionFilter
/*    */   implements RoutingServiceInstanceFilter
/*    */ {
/* 22 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(VersionFilter.class);
/*    */   
/*    */ 
/* 25 */   private ConcurrentHashMap<String, VersionRange> versionRangeMap = new ConcurrentHashMap();
/*    */   
/*    */ 
/*    */   public void filter(List<ServiceInstanceInner> instances, Context context)
/*    */   {
/* 30 */     IMessage receivedMessage = context.getReceivedMessage();
/* 31 */     MessageHeaders headers = receivedMessage.getHeaders();
/* 32 */     String version = headers.getVersion();
/* 33 */     if ((version == null) || (version.equals("0.0.0")))
/*    */     {
/*    */ 
/* 36 */       return;
/*    */     }
/*    */     try
/*    */     {
/* 40 */       VersionRange vrRange = (VersionRange)this.versionRangeMap.get(version);
/* 41 */       if (null == vrRange)
/*    */       {
/* 43 */         vrRange = (VersionRange)this.versionRangeMap.putIfAbsent(version, new VersionRange(version));
/*    */         
/* 45 */         vrRange = (VersionRange)this.versionRangeMap.get(version);
/*    */       }
/* 47 */       Iterator<ServiceInstanceInner> iterator = instances.iterator();
/* 48 */       while (iterator.hasNext())
/*    */       {
/* 50 */         ServiceInstanceInner instanceInner = (ServiceInstanceInner)iterator.next();
/*    */         
/* 52 */         if (!vrRange.isInTheRange(instanceInner.getServiceVersion()))
/*    */         {
/* 54 */           iterator.remove();
/*    */         }
/*    */       }
/*    */     }
/*    */     catch (Exception ex)
/*    */     {
/* 60 */       DEBUGGER.error("compare version failed. client version is: " + version);
/*    */       
/* 62 */       instances.clear();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public String getFilterName()
/*    */   {
/* 69 */     return "VersionFilter";
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\filter\VersionFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */