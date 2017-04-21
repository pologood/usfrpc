/*    */ package com.huawei.csc.usf.framework.routing;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.Context;
/*    */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
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
/*    */ public class RoutingProcessorManager
/*    */ {
/* 26 */   private static final List<RoutingProcessor> processors = new CopyOnWriteArrayList();
/*    */   
/*    */   public static void addRoutingProcessor(RoutingProcessor processor)
/*    */   {
/* 30 */     processors.add(processor);
/*    */   }
/*    */   
/*    */   public static void removeRoutingProcessor(RoutingProcessor processor)
/*    */   {
/* 35 */     if (processors.contains(processor))
/*    */     {
/* 37 */       processors.remove(processor);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public static void process(Object router, Context context, List<ServiceInstanceInner> instances)
/*    */   {
/* 44 */     for (RoutingProcessor processor : processors)
/*    */     {
/* 46 */       processor.process(router, context, instances);
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\RoutingProcessorManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */