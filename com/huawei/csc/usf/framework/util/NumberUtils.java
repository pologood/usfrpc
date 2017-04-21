/*    */ package com.huawei.csc.usf.framework.util;
/*    */ 
/*    */ import com.huawei.csc.kernel.api.log.LogFactory;
/*    */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*    */ import com.huawei.csc.usf.framework.config.DefaultSystemConfig;
/*    */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*    */ import org.apache.commons.lang.StringUtils;
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
/*    */ public class NumberUtils
/*    */ {
/* 20 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(NumberUtils.class);
/*    */   
/*    */ 
/*    */   public static final String EBUS_CORE_BIGNUMBER_MAX_DIGITS_SUPPORTED = "ebus.core.bignumber.max.digits.supported";
/*    */   
/*    */   private static final int DEF_NUMBER_LENGTH = 256;
/*    */   
/*    */   private static final int MAX_NUMBER_LENGTH = 1000;
/*    */   
/*    */   private static final int MIN_NUMBER_LENGTH = 1;
/*    */   
/* 31 */   private static SystemConfig config = new DefaultSystemConfig();
/*    */   
/* 33 */   private static int maxLengthSupported = getMaxLengthSupported();
/*    */   
/*    */ 
/*    */   public static void validateNumberString(String numberString)
/*    */   {
/* 38 */     if (null == numberString)
/*    */     {
/* 40 */       throw new IllegalArgumentException("Number cannot be null");
/*    */     }
/* 42 */     if (numberString.length() > maxLengthSupported)
/*    */     {
/* 44 */       throw new IllegalArgumentException("Number is too big to parse, maximum allowed digits is:" + maxLengthSupported);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   private static int getMaxLengthSupported()
/*    */   {
/* 52 */     String maxLengthSupported = config.getBigNumberMaxLength();
/* 53 */     return verifyStringtoInt("ebus.core.bignumber.max.digits.supported", maxLengthSupported, 1, 1000, 256);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   private static int verifyStringtoInt(String key, String value, int min, int max, int defaultValue)
/*    */   {
/* 61 */     if (StringUtils.isEmpty(value))
/*    */     {
/* 63 */       if (DEBUGGER.isInfoEnable())
/*    */       {
/* 65 */         DEBUGGER.info("Property[" + key + "] is empty, using default value[" + defaultValue + "] instead.");
/*    */       }
/*    */       
/*    */ 
/* 69 */       return defaultValue;
/*    */     }
/*    */     
/* 72 */     if (DEBUGGER.isInfoEnable())
/*    */     {
/* 74 */       DEBUGGER.info("The property " + key + " is: " + value);
/*    */     }
/*    */     
/*    */     try
/*    */     {
/* 79 */       int n = Integer.parseInt(value);
/* 80 */       if ((n >= min) && (n <= max))
/*    */       {
/* 82 */         return n;
/*    */       }
/*    */     }
/*    */     catch (NumberFormatException e)
/*    */     {
/* 87 */       DEBUGGER.error("Number format exception: ", e);
/*    */     }
/*    */     
/* 90 */     if (DEBUGGER.isWarnEnable())
/*    */     {
/* 92 */       DEBUGGER.warn("Property[" + key + "] is [" + value + "], which is invalid.Using default value[" + defaultValue + "] instead.");
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 97 */     return defaultValue;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\util\NumberUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */