/*    */ package com.huawei.csc.usf.framework.util;
/*    */ 
/*    */ import org.apache.commons.lang.StringUtils;
/*    */ import org.w3c.dom.Element;
/*    */ 
/*    */ 
/*    */ public class XmlUtil
/*    */ {
/*    */   public static String getAttribute(Element element, String key, String defaulValue)
/*    */   {
/* 11 */     String value = null;
/* 12 */     if (element != null)
/*    */     {
/* 14 */       value = element.getAttribute(key).trim();
/*    */     }
/* 16 */     if (StringUtils.isBlank(value))
/*    */     {
/* 18 */       return defaulValue;
/*    */     }
/* 20 */     return value;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\util\XmlUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */