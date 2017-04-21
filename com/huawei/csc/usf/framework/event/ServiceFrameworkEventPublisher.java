/*    */ package com.huawei.csc.usf.framework.event;
/*    */ 
/*    */ import com.huawei.csc.kernel.api.log.LogFactory;
/*    */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*    */ import com.huawei.csc.usf.framework.util.Utils;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ServiceFrameworkEventPublisher
/*    */ {
/* 37 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(ServiceFrameworkEventPublisher.class);
/*    */   
/*    */ 
/* 40 */   public static final Map<String, List<IServiceFrameworkEventHandler>> handlerMap = new HashMap();
/*    */   
/*    */   public static final boolean isEmpty()
/*    */   {
/* 44 */     return handlerMap.isEmpty();
/*    */   }
/*    */   
/*    */   public static void publish(String eventName, ServiceFrameworkEvent event)
/*    */     throws Exception
/*    */   {
/* 50 */     List<IServiceFrameworkEventHandler> handlers = (List)handlerMap.get(eventName);
/*    */     
/*    */ 
/* 53 */     int index = 0;
/* 54 */     StringBuffer stringBuffer = new StringBuffer();
/* 55 */     if (Utils.isNotEmpty(handlers))
/*    */     {
/*    */       try
/*    */       {
/* 59 */         for (IServiceFrameworkEventHandler handler : handlers)
/*    */         {
/* 61 */           handler.handle(event, eventName);
/* 62 */           index++;
/*    */         }
/*    */         
/*    */       }
/*    */       catch (Exception e)
/*    */       {
/* 68 */         for (IServiceFrameworkEventHandler handler : handlers)
/*    */         {
/*    */ 
/* 71 */           if (index >= 0)
/*    */           {
/* 73 */             index--;
/*    */           }
/*    */           else {
/* 76 */             stringBuffer.append(handler);
/* 77 */             stringBuffer.append(";");
/*    */           }
/*    */         }
/* 80 */         if (stringBuffer.length() != 0)
/*    */         {
/* 82 */           stringBuffer.deleteCharAt(stringBuffer.length() - 1);
/* 83 */           if (DEBUGGER.isWarnEnable())
/*    */           {
/* 85 */             DEBUGGER.warn("Because of the high Priority ServiceFrameworkEventHandler has throwed Exception, the low Priority ServiceFrameworkEventHandlers[" + stringBuffer.toString() + "] will not execute");
/*    */           }
/*    */         }
/*    */         
/*    */ 
/* 90 */         throw e;
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\event\ServiceFrameworkEventPublisher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */