/*     */ package com.huawei.csc.usf.framework.pojo;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.kernel.commons.util.ClassUtils;
/*     */ import com.huawei.csc.kernel.commons.util.UConfigDynamicString;
/*     */ import com.huawei.csc.usf.framework.DefaultMethodDefinition;
/*     */ import com.huawei.csc.usf.framework.DefaultServiceDefinition;
/*     */ import com.huawei.csc.usf.framework.MethodDefinition;
/*     */ import com.huawei.csc.usf.framework.ServiceDefinition;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*     */ import com.huawei.csc.usf.framework.util.XmlUtil;
/*     */ import org.apache.commons.lang.StringUtils;
/*     */ import org.springframework.beans.factory.config.RuntimeBeanReference;
/*     */ import org.springframework.beans.factory.support.BeanDefinitionBuilder;
/*     */ import org.springframework.beans.factory.xml.ParserContext;
/*     */ import org.springframework.beans.factory.xml.XmlReaderContext;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public class ServiceBeanDefinitionParser extends AbstractBeanDefinitonParser
/*     */ {
/*  23 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(ServiceBeanDefinitionParser.class);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final String SERVICE_NAMESPACE = "http://www.huawei.com/schema/service";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  38 */   private UConfigDynamicString globalUconfig = new UConfigDynamicString("globalProperties");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Class<?> getBeanClass(Element element)
/*     */   {
/*  51 */     return PojoServerInner.class;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder)
/*     */   {
/*  67 */     ServiceDefinition sDefinition = new DefaultServiceDefinition();
/*  68 */     sDefinition.setIsServer(true);
/*     */     
/*  70 */     String name = element.getAttribute("name").trim();
/*  71 */     String intf = element.getAttribute("interface").trim();
/*     */     
/*     */ 
/*  74 */     String className = element.getAttribute("class").trim();
/*  75 */     String refName = element.getAttribute("ref").trim();
/*     */     
/*  77 */     String executesStr = getDynamicExecutes(element, true);
/*  78 */     int executes = 0;
/*  79 */     if (!StringUtils.isEmpty(executesStr))
/*     */     {
/*  81 */       executes = Integer.parseInt(executesStr);
/*     */     }
/*     */     
/*     */ 
/*  85 */     String tpsThresholdStr = element.getAttribute("tpsThreshold").trim();
/*     */     
/*  87 */     if (StringUtils.isNotEmpty(refName))
/*     */     {
/*  89 */       RuntimeBeanReference ref = new RuntimeBeanReference(refName);
/*  90 */       ref.setSource(parserContext.getReaderContext().extractSource(element));
/*     */       
/*  92 */       builder.addPropertyValue("ref", ref);
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*  97 */     else if (StringUtils.isNotEmpty(className))
/*     */     {
/*     */       try
/*     */       {
/* 101 */         Class<?> clazz = ClassUtils.getClass(className);
/* 102 */         Object ref = clazz.newInstance();
/* 103 */         builder.addPropertyValue("ref", ref);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 107 */         if (DEBUGGER.isErrorEnable())
/*     */         {
/* 109 */           DEBUGGER.error("fail to create instance of class:" + className, e);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 116 */     String executePool = element.getAttribute("executePool").trim();
/* 117 */     String protocol = element.getAttribute("protocol").trim();
/*     */     
/* 119 */     sDefinition.setServiceName(name);
/* 120 */     sDefinition.setServiceInterface(intf);
/* 121 */     sDefinition.setThreadPool(executePool);
/* 122 */     sDefinition.setProtocolType(protocol);
/* 123 */     sDefinition.setExecutes(executes);
/*     */     
/* 125 */     if (!StringUtils.isEmpty(tpsThresholdStr))
/*     */     {
/* 127 */       sDefinition.setThreshold(tpsThresholdStr);
/*     */     }
/*     */     
/* 130 */     String group = XmlUtil.getAttribute(element, "group", "default");
/* 131 */     sDefinition.setGroup(group);
/*     */     
/* 133 */     String version = XmlUtil.getAttribute(element, "version", "0.0.0");
/* 134 */     sDefinition.setVersion(version);
/*     */     
/* 136 */     NodeList methodControlElements = element.getElementsByTagNameNS("http://www.huawei.com/schema/service", "method");
/*     */     
/*     */ 
/* 139 */     if ((null != methodControlElements) && (methodControlElements.getLength() > 0))
/*     */     {
/*     */ 
/* 142 */       buildDefaultMethodDefinition(methodControlElements, sDefinition);
/*     */     }
/*     */     
/* 145 */     builder.addPropertyValue("intf", intf);
/* 146 */     builder.addPropertyValue("serviceDefinition", sDefinition);
/* 147 */     builder.addPropertyValue("serviceType", ServiceType.USF);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void buildDefaultMethodDefinition(NodeList methodControlElements, ServiceDefinition serviceDefinition)
/*     */   {
/* 159 */     if (null != methodControlElements)
/*     */     {
/* 161 */       for (int index = 0; index < methodControlElements.getLength(); index++)
/*     */       {
/* 163 */         Element element = (Element)methodControlElements.item(index);
/* 164 */         String methodName = element.getAttribute("name");
/*     */         
/* 166 */         if (!StringUtils.isBlank(methodName))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 171 */           MethodDefinition methodDefinition = new DefaultMethodDefinition();
/* 172 */           methodDefinition.setMethodName(methodName);
/* 173 */           serviceDefinition.addMethodDefinition(methodName, methodDefinition);
/*     */           
/*     */ 
/* 176 */           String executes = getDynamicExecutes(element, false);
/* 177 */           if (StringUtils.isNotBlank(executes))
/*     */           {
/* 179 */             methodDefinition.setExecutes(executes);
/*     */           }
/* 181 */           String tpsThreshold = element.getAttribute("tpsThreshold");
/*     */           
/* 183 */           if (StringUtils.isNotBlank(tpsThreshold))
/*     */           {
/* 185 */             methodDefinition.setThreshold(tpsThreshold);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String getDynamicExecutes(Element element, boolean isServer)
/*     */   {
/* 202 */     String defaultResult = "0";
/* 203 */     String key = getAttribute(element, "executes", null);
/* 204 */     if (null == key)
/*     */     {
/* 206 */       if (isServer)
/*     */       {
/* 208 */         return defaultResult;
/*     */       }
/*     */       
/* 211 */       return null;
/*     */     }
/*     */     
/* 214 */     if (!key.startsWith("${"))
/*     */     {
/* 216 */       return key;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 221 */       String globalString = this.globalUconfig.resolveStringValue(key);
/* 222 */       if (!globalString.equals(key))
/*     */       {
/* 224 */         return globalString;
/*     */       }
/*     */       
/* 227 */       if (isServer)
/*     */       {
/* 229 */         return defaultResult;
/*     */       }
/*     */       
/* 232 */       return null;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 236 */       if (DEBUGGER.isWarnEnable())
/*     */       {
/* 238 */         DEBUGGER.warn("get executes configation failed, key: " + key, e); }
/*     */     }
/* 240 */     return defaultResult;
/*     */   }
/*     */   
/*     */ 
/*     */   private String getAttribute(Element element, String key, String defaulValue)
/*     */   {
/* 246 */     String value = element.getAttribute(key).trim();
/* 247 */     if (StringUtils.isBlank(value))
/*     */     {
/* 249 */       return defaulValue;
/*     */     }
/* 251 */     return value;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\pojo\ServiceBeanDefinitionParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */