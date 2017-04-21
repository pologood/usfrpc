/*    */ package com.huawei.csc.usf.framework.circuitbreaker;
/*    */ 
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ 
/*    */ 
/*    */ public class DSFServiceCallCommandFactory
/*    */ {
/*  8 */   protected static final ConcurrentHashMap<String, DSFServiceCallCommand> command = new ConcurrentHashMap();
/*    */   
/*    */   public static DSFServiceCallCommand getDsfServiceCallCommand(String serviceName)
/*    */   {
/* 12 */     DSFServiceCallCommand callCommand = (DSFServiceCallCommand)command.get(serviceName);
/*    */     
/* 14 */     if (null != callCommand)
/*    */     {
/* 16 */       return callCommand;
/*    */     }
/*    */     
/* 19 */     callCommand = (DSFServiceCallCommand)command.get(serviceName);
/* 20 */     if (null == callCommand)
/*    */     {
/* 22 */       callCommand = new DSFServiceCallCommand(serviceName);
/*    */       
/* 24 */       command.putIfAbsent(serviceName, callCommand);
/*    */     }
/*    */     
/*    */ 
/* 28 */     return (DSFServiceCallCommand)command.get(serviceName);
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\circuitbreaker\DSFServiceCallCommandFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */