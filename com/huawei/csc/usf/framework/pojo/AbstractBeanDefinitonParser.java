/*    */ package com.huawei.csc.usf.framework.pojo;
/*    */ 
/*    */ import com.huawei.csc.kernel.api.log.LogFactory;
/*    */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*    */ import org.apache.commons.lang.StringUtils;
/*    */ import org.springframework.beans.factory.BeanDefinitionStoreException;
/*    */ import org.springframework.beans.factory.support.AbstractBeanDefinition;
/*    */ import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
/*    */ import org.springframework.beans.factory.xml.ParserContext;
/*    */ import org.w3c.dom.Element;
/*    */ 
/*    */ 
/*    */ public class AbstractBeanDefinitonParser
/*    */   extends AbstractSingleBeanDefinitionParser
/*    */ {
/* 16 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(AbstractBeanDefinitonParser.class);
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext context)
/*    */     throws BeanDefinitionStoreException
/*    */   {
/* 24 */     String name = element.getAttribute("name").trim();
/* 25 */     String intf = element.getAttribute("interface").trim();
/* 26 */     if (StringUtils.isEmpty(name))
/*    */     {
/* 28 */       DEBUGGER.error("The importer or exporter name is missing,please check the xml file which export or import the service,the interface is [ " + intf + "].");
/*    */     }
/*    */     
/* 31 */     return name;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\pojo\AbstractBeanDefinitonParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */