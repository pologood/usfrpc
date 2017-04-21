/*    */ package com.huawei.csc.usf.framework;
/*    */ 
/*    */ import com.huawei.csc.kernel.api.log.LogFactory;
/*    */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.CopyOnWriteArrayList;
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
/*    */ 
/*    */ public class ContextListenerManager
/*    */ {
/* 36 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(ContextListenerManager.class);
/*    */   
/*    */ 
/* 39 */   private static final List<ContextListener> listeners = new CopyOnWriteArrayList();
/*    */   
/*    */   public static void registerListener(ContextListener listener)
/*    */   {
/* 43 */     listeners.add(listener);
/*    */   }
/*    */   
/*    */   public static void unregisterListener(ContextListener listener)
/*    */   {
/* 48 */     listeners.remove(listener);
/*    */   }
/*    */   
/*    */   public static void clear()
/*    */   {
/* 53 */     listeners.clear();
/*    */   }
/*    */   
/*    */   public static void onContextDone(Context context)
/*    */   {
/* 58 */     for (ContextListener listener : listeners)
/*    */     {
/*    */       try
/*    */       {
/* 62 */         listener.onContextDone(context);
/*    */       }
/*    */       catch (Throwable ex)
/*    */       {
/* 66 */         DEBUGGER.error("Invoke context listener[" + listener + "] error.", ex);
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static void onContextCreated(Context context)
/*    */   {
/* 75 */     for (ContextListener listener : listeners)
/*    */     {
/*    */       try
/*    */       {
/* 79 */         listener.onContextCreated(context);
/*    */       }
/*    */       catch (Throwable ex)
/*    */       {
/* 83 */         DEBUGGER.error("Invoke context listener[" + listener + "] error.", ex);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\ContextListenerManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */