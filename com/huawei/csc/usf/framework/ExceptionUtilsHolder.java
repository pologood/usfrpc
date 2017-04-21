/*    */ package com.huawei.csc.usf.framework;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*    */ import java.util.Map;
/*    */ import org.springframework.context.ApplicationContext;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ExceptionUtilsHolder
/*    */ {
/* 17 */   private static ExceptionUtils[] exceptionUtilsArray = new ExceptionUtils[ServiceType.size()];
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static ExceptionUtils getExceptionUtils(ServiceType serviceType)
/*    */   {
/* 24 */     return exceptionUtilsArray[serviceType.toNumber()];
/*    */   }
/*    */   
/*    */ 
/*    */   public static void init(ServiceEngine engine)
/*    */   {
/* 30 */     Map<String, ExceptionUtils> exceptionUtilsBeans = engine.getApplicationContext().getBeansOfType(ExceptionUtils.class);
/*    */     
/* 32 */     for (ExceptionUtils exceptionUtils : exceptionUtilsBeans.values())
/*    */     {
/*    */ 
/*    */ 
/* 36 */       exceptionUtilsArray[exceptionUtils.getServiceType().toNumber()] = exceptionUtils;
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\ExceptionUtilsHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */