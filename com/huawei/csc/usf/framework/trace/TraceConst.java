/*    */ package com.huawei.csc.usf.framework.trace;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TraceConst
/*    */ {
/*    */   public static final String EXTEND_INFO = "ExtendInfo";
/*    */   
/*    */ 
/*    */   public static final String HASCOMPAREDTRACE = "hasComparedTrace";
/*    */   
/*    */ 
/*    */   public static final String CALLING_NODE_ID = "CallingNodeID";
/*    */   
/*    */ 
/*    */   public static final String CALLED_NODE_ID = "CalledNodeId";
/*    */   
/*    */ 
/*    */   public static final String CALLED_CLUSTER_ID = "CalledClusterId";
/*    */   
/*    */ 
/*    */   public static final String NODE_ID_ENV = "PAAS_HOST_ID";
/*    */   
/*    */ 
/*    */   public static final String CLUSTER_ID_ENV = "PAAS_CLUSTER_ID";
/*    */   
/*    */ 
/*    */   public static final String CALL_NUMBER = "callNumber";
/*    */   
/*    */ 
/*    */   public static final String SEQ_NO = "seqNo";
/*    */   
/*    */ 
/*    */   public static final String TRACE_ID = "traceId";
/*    */   
/*    */ 
/*    */   public static final String TRACE_FLAG = "TraceFlag";
/*    */   
/*    */ 
/*    */   public static final String REQ_SIZE = "reqSize";
/*    */   
/*    */ 
/*    */   public static final String RSP_SIZE = "rspSize";
/*    */   
/*    */ 
/*    */   public static final String RET_CODE_KEY = "retCode";
/*    */   
/*    */ 
/*    */   public static final String RESULT_INFO = "ResultInfo";
/*    */   
/*    */ 
/*    */   public static final String BEGIN_TIME_KEY = "traceBeginTime";
/*    */   
/*    */ 
/*    */   public static final String CLIENT_LINK_MESSAGE = "clientLinkMessage";
/*    */   
/*    */ 
/*    */   public static final String SERVER_LINK_MESSAGE = "serverLinkMessage";
/*    */   
/*    */ 
/*    */   public static final String CALLING_SERVICE = "callingService";
/*    */   
/*    */ 
/*    */   public static final String CALLED_SERVICE = "calledService";
/*    */   
/*    */ 
/*    */   public static final String HEAD_FLAG = "headFlag";
/*    */   
/*    */ 
/*    */   public static final String FALSE = "false";
/*    */   
/*    */   public static final String TRUE = "true";
/*    */   
/*    */   public static final String UNSAMPLED_VALUE = "0";
/*    */   
/*    */   public static final String SUCCESS_RET_CODE = "0";
/*    */   
/*    */   public static final String ERROR_RET_CODE = "-1";
/*    */   
/*    */   public static final int SAMPLE_TRACE_FLAG = 2;
/*    */   
/*    */   public static final int UNSAMPLE_TRACE_FLAG = 0;
/*    */   
/*    */   public static final int CMP_TRACE_FLAG = 6;
/*    */   
/*    */   public static final String ATTRS_MAP_KEY = "attrs_map";
/*    */   
/*    */ 
/*    */   public static enum TerminalType
/*    */   {
/* 91 */     CLIENT,  SERVER;
/*    */     
/*    */     private TerminalType() {}
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\trace\TraceConst.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */