/*    */ package com.huawei.csc.usf.framework.bus;
/*    */ 
/*    */ import com.dyuproject.protostuff.Schema;
/*    */ import com.dyuproject.protostuff.runtime.Delegate;
/*    */ import com.huawei.csc.kernel.api.log.LogFactory;
/*    */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
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
/*    */ public class ProtoStuffMsgSerializerUtils
/*    */ {
/* 33 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(ProtoStuffMsgSerializerUtils.class);
/*    */   
/*    */ 
/* 36 */   static Map<Class<?>, Schema> schemasMap = new HashMap(1024);
/*    */   
/* 38 */   static List<Delegate<?>> delegates = new ArrayList(1024);
/*    */   
/*    */   public static void registerRuntimeSchemas(Map<Class<?>, Schema> schemas)
/*    */   {
/* 42 */     synchronized (schemasMap)
/*    */     {
/* 44 */       for (Map.Entry<Class<?>, Schema> entry : schemas.entrySet())
/*    */       {
/* 46 */         schemasMap.put(entry.getKey(), entry.getValue());
/*    */       }
/*    */       
/* 49 */       if (DEBUGGER.isDebugEnable())
/*    */       {
/* 51 */         StringBuilder sb = new StringBuilder();
/*    */         
/* 53 */         sb.append("register schemas: ");
/* 54 */         for (Map.Entry<Class<?>, Schema> entry : schemas.entrySet())
/*    */         {
/* 56 */           sb.append(" class : " + ((Class)entry.getKey()).getName());
/*    */         }
/*    */         
/* 59 */         DEBUGGER.debug(sb.toString());
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public static void registerRuntimeDelegate(Delegate<?> delegate)
/*    */   {
/* 66 */     synchronized (delegates)
/*    */     {
/* 68 */       delegates.add(delegate);
/*    */       
/* 70 */       if (DEBUGGER.isDebugEnable())
/*    */       {
/* 72 */         DEBUGGER.debug("register delegate for class : " + delegate.typeClass().getName());
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\bus\ProtoStuffMsgSerializerUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */