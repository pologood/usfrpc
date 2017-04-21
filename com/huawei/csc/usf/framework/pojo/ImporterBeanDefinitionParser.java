/*     */ package com.huawei.csc.usf.framework.pojo;
/*     */ 
/*     */ import com.huawei.csc.usf.framework.DefaultMethodDefinition;
/*     */ import com.huawei.csc.usf.framework.DefaultServiceDefinition;
/*     */ import com.huawei.csc.usf.framework.MethodDefinition;
/*     */ import com.huawei.csc.usf.framework.ServiceDefinition;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*     */ import com.huawei.csc.usf.framework.util.Utils;
/*     */ import com.huawei.csc.usf.framework.util.XmlUtil;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import org.apache.commons.lang.StringUtils;
/*     */ import org.springframework.beans.factory.support.BeanDefinitionBuilder;
/*     */ import org.springframework.beans.factory.xml.ParserContext;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NodeList;
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
/*     */ public class ImporterBeanDefinitionParser
/*     */   extends AbstractBeanDefinitonParser
/*     */ {
/*     */   private static final String SERVICE_NAMESPACE = "http://www.huawei.com/schema/service";
/*     */   private static final String DEFAUL_REGID = "dsf_default";
/*     */   
/*     */   protected Class<?> getBeanClass(Element element)
/*     */   {
/*  38 */     return PojoClientInner.class;
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
/*  54 */     String serviceName = element.getAttribute("service-name").trim();
/*  55 */     String beanName = element.getAttribute("name").trim();
/*  56 */     String intf = element.getAttribute("interface").trim();
/*  57 */     String serviceAddress = element.getAttribute("service-address").trim();
/*  58 */     String protocol = element.getAttribute("protocol").trim();
/*  59 */     String registry = element.getAttribute("registry").trim();
/*  60 */     String timeout = element.getAttribute("timeout").trim();
/*  61 */     String router = Utils.conventRouterType(element.getAttribute("router").trim());
/*     */     
/*  63 */     String failPolicy = element.getAttribute("fail-policy").trim();
/*  64 */     String restUrl = element.getAttribute("url").trim();
/*  65 */     String restDirection = element.getAttribute("direction").trim();
/*  66 */     ServiceDefinition sDefinition = new DefaultServiceDefinition();
/*  67 */     sDefinition.setIsServer(false);
/*     */     
/*     */ 
/*  70 */     NodeList methodControlElements = element.getElementsByTagNameNS("http://www.huawei.com/schema/service", "method");
/*     */     
/*     */ 
/*  73 */     ConcurrentMap<String, Map<String, String>> methodControlMap = new ConcurrentHashMap();
/*  74 */     if ((null != methodControlElements) && (methodControlElements.getLength() > 0))
/*     */     {
/*     */ 
/*  77 */       buildMethodsControl(methodControlMap, methodControlElements, sDefinition);
/*     */     }
/*     */     
/*     */ 
/*  81 */     if (serviceAddress.isEmpty())
/*     */     {
/*  83 */       serviceAddress = null;
/*     */     }
/*  85 */     if (protocol.isEmpty())
/*     */     {
/*  87 */       protocol = null;
/*     */     }
/*  89 */     if (StringUtils.isEmpty(timeout))
/*     */     {
/*  91 */       timeout = String.valueOf(0);
/*     */     }
/*  93 */     if (restUrl.isEmpty())
/*     */     {
/*  95 */       restUrl = null;
/*     */     }
/*  97 */     if (restDirection.isEmpty())
/*     */     {
/*  99 */       restDirection = null;
/*     */     }
/* 101 */     sDefinition.setProtocolType(protocol);
/* 102 */     String group = XmlUtil.getAttribute(element, "group", "default");
/* 103 */     String version = element.getAttribute("version").trim();
/* 104 */     sDefinition.setVersion(version);
/* 105 */     sDefinition.setServiceName(serviceName);
/* 106 */     sDefinition.setServiceInterface(intf);
/* 107 */     sDefinition.setServiceAddress(serviceAddress);
/* 108 */     sDefinition.setGroup(group);
/* 109 */     sDefinition.setTimeout(Integer.parseInt(timeout));
/* 110 */     sDefinition.setRouterId(router);
/* 111 */     sDefinition.setFailPolicy(failPolicy);
/* 112 */     if (Utils.isEmpty(registry))
/*     */     {
/* 114 */       sDefinition.setRegistry("dsf_default");
/*     */     }
/*     */     else
/*     */     {
/* 118 */       sDefinition.setRegistry(registry);
/*     */     }
/* 120 */     sDefinition.setBeanName(beanName);
/* 121 */     sDefinition.setResturl(restUrl);
/* 122 */     sDefinition.setRestProtocolDirection(restDirection);
/*     */     
/* 124 */     builder.addPropertyValue("serviceInterface", intf);
/* 125 */     builder.addPropertyValue("serviceDefinition", sDefinition);
/* 126 */     builder.addPropertyValue("serviceType", ServiceType.USF);
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
/*     */   private void buildMethodsControl(ConcurrentMap<String, Map<String, String>> methodControlMap, NodeList methodControlElements, ServiceDefinition serviceDefinition)
/*     */   {
/* 139 */     if (null != methodControlElements)
/*     */     {
/* 141 */       for (int index = 0; index < methodControlElements.getLength(); index++)
/*     */       {
/* 143 */         Element element = (Element)methodControlElements.item(index);
/* 144 */         String methodName = element.getAttribute("name");
/*     */         
/* 146 */         if (!StringUtils.isBlank(methodName))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 151 */           MethodDefinition methodDefinition = new DefaultMethodDefinition();
/* 152 */           methodDefinition.setMethodName(methodName);
/* 153 */           serviceDefinition.addMethodDefinition(methodName, methodDefinition);
/*     */           
/*     */ 
/* 156 */           String methodTimeout = element.getAttribute("timeout");
/* 157 */           Map<String, String> methodControl = new HashMap();
/* 158 */           if (StringUtils.isNotBlank(methodTimeout))
/*     */           {
/* 160 */             methodControl.put("timeout", methodTimeout);
/* 161 */             methodDefinition.setTimeout(methodTimeout);
/*     */           }
/* 163 */           methodControlMap.put(methodName, methodControl);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\pojo\ImporterBeanDefinitionParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */