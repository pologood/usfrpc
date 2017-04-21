/*     */ package com.huawei.csc.usf.framework.config;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.uconfig.UConfigHelper;
/*     */ import com.huawei.csc.kernel.api.uconfig.UConfiguration;
/*     */ 
/*     */ 
/*     */ public class USFConfig
/*     */ {
/*     */   private static UConfiguration configuration;
/*  10 */   private static final USFConfig CONFIG = new USFConfig();
/*     */   
/*     */ 
/*     */   private static final String POLICY_ID = "globalProperties";
/*     */   
/*     */ 
/*     */   private static final String KEY_DSF_TRACE_SWITCH = "dsf.trace.switch";
/*     */   
/*     */   private static final String KEY_USF_TRACE_SWITCH = "usf.trace.switch";
/*     */   
/*     */   private static final String KEY_DSF_SAMPLE_SWITCH = "dsf.sample.switch";
/*     */   
/*     */   private static final String KEY_USF_SAMPLE_SWITCH = "usf.sample.switch";
/*     */   
/*     */   private static final String KEY_DSF_SAMPLE_RATE = "dsf.sampleRate";
/*     */   
/*     */   private static final String KEY_USF_SAMPLE_RATE = "usf.sampleRate";
/*     */   
/*     */   private static final String KEY_DSF_LOG_SWITCH = "dsf.logTraceSwitch";
/*     */   
/*     */   private static final String KEY_USF_LOG_SWITCH = "usf.logTraceSwitch";
/*     */   
/*     */   private static final String KEY_EBUS_TRACE_FLAG = "tracelink.sample.rate";
/*     */   
/*     */   private static final String RPC_ADDRESS = "rpc.address";
/*     */   
/*     */   private static final String TRACELINK_SEQ_NUM_DEPTH = "tracelink.seqNum.depth";
/*     */   
/*  38 */   private boolean traceSwitch = false;
/*     */   
/*     */ 
/*  41 */   private long sampleRate = 1000L;
/*     */   
/*     */ 
/*  44 */   private boolean sampleSwitch = true;
/*     */   
/*     */ 
/*  47 */   private boolean logTraceSwitch = false;
/*     */   
/*  49 */   private String rpcAddress = null;
/*     */   
/*  51 */   private int traceLinkSeqNumDepth = 0;
/*     */   
/*     */   private USFConfig()
/*     */   {
/*  55 */     configuration = UConfigHelper.getDefaultPropertyConfiguration("globalProperties");
/*     */     
/*     */ 
/*  58 */     if (null != configuration.getString("usf.trace.switch"))
/*     */     {
/*  60 */       this.traceSwitch = configuration.getBoolean("usf.trace.switch", false);
/*     */     }
/*  62 */     else if (null != configuration.getString("dsf.trace.switch"))
/*     */     {
/*  64 */       this.traceSwitch = configuration.getBoolean("dsf.trace.switch", false);
/*     */     }
/*     */     else
/*     */     {
/*  68 */       int sampleRate = configuration.getInt("tracelink.sample.rate", 0);
/*  69 */       this.traceSwitch = (sampleRate != 0);
/*     */     }
/*     */     
/*  72 */     if (null != configuration.getString("usf.sample.switch"))
/*     */     {
/*  74 */       this.sampleSwitch = configuration.getBoolean("usf.sample.switch", true);
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*  79 */       this.sampleSwitch = configuration.getBoolean("dsf.sample.switch", true);
/*     */     }
/*     */     
/*     */ 
/*  83 */     if (null != configuration.getString("usf.sampleRate"))
/*     */     {
/*  85 */       this.sampleRate = configuration.getLong("usf.sampleRate", 1000L);
/*     */     }
/*  87 */     else if (null != configuration.getString("dsf.trace.switch"))
/*     */     {
/*  89 */       this.sampleRate = configuration.getLong("dsf.sampleRate", 1000L);
/*     */     }
/*     */     else
/*     */     {
/*  93 */       this.sampleRate = configuration.getLong("tracelink.sample.rate", 1000L);
/*     */     }
/*     */     
/*  96 */     if (null != configuration.getString("usf.logTraceSwitch"))
/*     */     {
/*  98 */       this.logTraceSwitch = configuration.getBoolean("usf.logTraceSwitch", false);
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 103 */       this.logTraceSwitch = configuration.getBoolean("dsf.logTraceSwitch", false);
/*     */     }
/*     */     
/* 106 */     this.rpcAddress = configuration.getString("rpc.address", "0:2618");
/*     */     
/* 108 */     this.traceLinkSeqNumDepth = configuration.getInt("tracelink.seqNum.depth", 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean getLogTraceSwitch()
/*     */   {
/* 116 */     return CONFIG.logTraceSwitch;
/*     */   }
/*     */   
/*     */   public static long getSampleRate()
/*     */   {
/* 121 */     return CONFIG.sampleRate;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean getSampleSwitch()
/*     */   {
/* 127 */     return CONFIG.sampleSwitch;
/*     */   }
/*     */   
/*     */   public String getString(String key, String defaultVal)
/*     */   {
/* 132 */     return configuration.getString(key, defaultVal);
/*     */   }
/*     */   
/*     */   public static boolean isTraceSwitch()
/*     */   {
/* 137 */     return CONFIG.traceSwitch;
/*     */   }
/*     */   
/*     */   public static String getRpcAddress()
/*     */   {
/* 142 */     return CONFIG.rpcAddress;
/*     */   }
/*     */   
/*     */ 
/*     */   public static int getTraceLinkSeqNumDepth()
/*     */   {
/* 148 */     return CONFIG.traceLinkSeqNumDepth;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\config\USFConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */