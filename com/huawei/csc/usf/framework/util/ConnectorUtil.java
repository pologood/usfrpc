/*    */ package com.huawei.csc.usf.framework.util;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.Connector;
/*    */ import com.huawei.csc.usf.framework.Context;
/*    */ 
/*    */ 
/*    */ public class ConnectorUtil
/*    */ {
/*    */   public static boolean isBusConnector(Context context, boolean isSrcConnector)
/*    */   {
/* 11 */     if (null == context)
/*    */     {
/* 13 */       return false;
/*    */     }
/*    */     
/* 16 */     Connector connector = isSrcConnector ? context.getSrcConnector() : context.getDestConnector();
/*    */     
/*    */ 
/* 19 */     if (null == connector)
/*    */     {
/* 21 */       return false;
/*    */     }
/*    */     
/* 24 */     return "BUS".equals(connector.getConnectorType());
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\util\ConnectorUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */