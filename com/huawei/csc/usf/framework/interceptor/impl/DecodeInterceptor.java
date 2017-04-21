/*     */ package com.huawei.csc.usf.framework.interceptor.impl;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.Connector;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*     */ import com.huawei.csc.usf.framework.degrade.ServiceDegrade;
/*     */ import com.huawei.csc.usf.framework.event.FrameworkEvent;
/*     */ import com.huawei.csc.usf.framework.event.ServiceFrameworkEvent;
/*     */ import com.huawei.csc.usf.framework.event.ServiceFrameworkEventPublisher;
/*     */ import com.huawei.csc.usf.framework.interceptor.InInterceptor;
/*     */ import com.huawei.csc.usf.framework.mockutil.MockUtil;
/*     */ import com.huawei.csc.usf.framework.sr.DsfZookeeperDataManager;
/*     */ import com.huawei.csc.usf.framework.sr.SRAgentFactory;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceRegistryAgent;
/*     */ import com.huawei.csc.usf.framework.util.USFContext;
/*     */ import org.apache.commons.lang.StringUtils;
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
/*     */ public class DecodeInterceptor
/*     */   extends AbstractInterceptor
/*     */   implements InInterceptor
/*     */ {
/*  48 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(DecodeInterceptor.class);
/*     */   
/*     */ 
/*     */   public static final String NMAE = "decode";
/*     */   
/*  53 */   private int inWeight = 100;
/*     */   
/*     */   public void doInvoke(Context context)
/*     */     throws Exception
/*     */   {
/*  58 */     Connector connector = context.getSrcConnector();
/*  59 */     IMessage message = context.getReceivedMessage();
/*  60 */     IMessage reply = null;
/*  61 */     if (!context.getIsReSend())
/*     */     {
/*     */ 
/*  64 */       if (!context.isServer())
/*     */       {
/*  66 */         if (!context.getIsReSend())
/*     */         {
/*  68 */           if (message != null)
/*     */           {
/*  70 */             String address = connector.getServiceEngine().getSystemConfig().getRPCAddress(context.getServiceType());
/*     */             
/*     */ 
/*  73 */             handleServiceDegrade(message, context, address);
/*  74 */             MockUtil.mock(context);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/*     */       else {
/*  80 */         USFContext.getContext().rpcCtx2UsfCtx(message);
/*     */       }
/*     */     }
/*     */     
/*  84 */     if ((!context.isServer()) && (!ServiceFrameworkEventPublisher.isEmpty()))
/*     */     {
/*  86 */       ServiceFrameworkEvent event = FrameworkEvent.creatEvent(context.getReceivedMessage(), context.getSrcConnector().getConnectorType(), context, Boolean.valueOf(true), Boolean.valueOf(!context.getSrcConnector().isAsync()), null, null, null);
/*     */       
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/*  92 */         ServiceFrameworkEventPublisher.publish("usf.framework.afterDecode", event);
/*     */ 
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*  97 */         if (DEBUGGER.isErrorEnable())
/*     */         {
/*  99 */           DEBUGGER.error("Excpetion occured while processing afterDecode event.");
/*     */         }
/* 101 */         publishRejectEvent(context, context.getReceivedMessage(), context.getStartTime(), event, e);
/*     */         
/* 103 */         throw e;
/*     */       }
/* 105 */       reply = FrameworkEvent.getReplyMessage(event);
/* 106 */       if (null != reply)
/*     */       {
/* 108 */         publishRejectEvent(context, context.getReceivedMessage(), context.getStartTime(), event, null);
/*     */         
/* 110 */         context.setReplyMessage(reply);
/* 111 */         connector.encode(context, reply);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void publishRejectEvent(Context context, IMessage request, long startTime, ServiceFrameworkEvent event, Exception ex)
/*     */     throws Exception
/*     */   {
/* 131 */     ServiceFrameworkEvent exEvent = FrameworkEvent.creatEvent(request, context.getSrcConnector().getConnectorType(), context, Boolean.valueOf(true), Boolean.valueOf(!context.getSrcConnector().isAsync()), null, Long.valueOf(System.nanoTime() - startTime), ex);
/*     */     
/*     */ 
/*     */ 
/* 135 */     ServiceFrameworkEventPublisher.publish("usf.framework.rejectAfterDecode", exEvent);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void handleServiceDegrade(IMessage message, Context context, String address)
/*     */   {
/* 142 */     if (context.getSrcConnector().isAsync())
/*     */     {
/* 144 */       return;
/*     */     }
/*     */     
/* 147 */     MessageHeaders headers = message.getHeaders();
/* 148 */     String serviceName = headers.getServiceName();
/* 149 */     String operation = headers.getOperation();
/* 150 */     Connector contector = context.getSrcConnector();
/* 151 */     String dsfApplication = contector.getServiceEngine().getSystemConfig().getDsfApplication();
/*     */     
/* 153 */     ServiceDegrade serviceDegrade = (ServiceDegrade)contector.getServiceEngine().getSrAgentFactory().getSRAgent(context.getRegistry()).getZookeeperDataManager().getConfigFromConfiguration(serviceName, operation, address, dsfApplication, "serviceDegrade", "consumer");
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
/* 166 */     if (serviceDegrade == null)
/*     */     {
/* 168 */       return;
/*     */     }
/*     */     
/* 171 */     if ((serviceDegrade.isForceReturnException()) || (serviceDegrade.isMethodForceReturnException()))
/*     */     {
/*     */ 
/* 174 */       context.addAttachment("ServiceDegrade", "FalseReturnException");
/*     */       
/* 176 */       context.addAttachment("serviceDegradeExceptionCode", serviceDegrade.getExceptionCode());
/*     */ 
/*     */     }
/* 179 */     else if ((serviceDegrade.isForceReturnNull()) || (serviceDegrade.isMethodForceReturnNull()))
/*     */     {
/*     */ 
/* 182 */       context.addAttachment("ServiceDegrade", "FalseReturnNull");
/*     */ 
/*     */     }
/* 185 */     else if ((serviceDegrade.isForceReturnMockService()) || (serviceDegrade.isMethodForceReturnMockService()))
/*     */     {
/*     */ 
/* 188 */       if (null != serviceDegrade.getMockServiceName())
/*     */       {
/* 190 */         String alternateServiceName = serviceDegrade.getMockServiceName();
/*     */         
/*     */ 
/* 193 */         if (StringUtils.isBlank(alternateServiceName))
/*     */         {
/* 195 */           alternateServiceName = getMockServiceName(message, serviceName);
/*     */           
/* 197 */           message = updateServiceName(message, serviceName, alternateServiceName);
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 202 */           message = updateServiceName(message, serviceName, alternateServiceName);
/*     */         }
/*     */         
/*     */ 
/* 206 */         context.setReceivedMessage(message);
/*     */       }
/*     */     }
/* 209 */     else if ((serviceDegrade.isFailReturnNull()) || (serviceDegrade.isMethodFailReturnNull()))
/*     */     {
/*     */ 
/* 212 */       context.addAttachment("ServiceDegrade", "FailReturn");
/*     */       
/*     */ 
/* 215 */       if (DEBUGGER.isDebugEnable())
/*     */       {
/* 217 */         DEBUGGER.debug("Service [" + serviceName + "] " + "with operation [" + operation + "] " + " degrade to fail:return null.");
/*     */       }
/*     */       
/*     */ 
/*     */     }
/* 222 */     else if ((serviceDegrade.isFalseReturn()) || (serviceDegrade.isMethodFalseReturn()))
/*     */     {
/*     */ 
/* 225 */       if (DEBUGGER.isDebugEnable())
/*     */       {
/* 227 */         DEBUGGER.debug("Service [" + serviceName + "] " + "with operation [" + operation + "] " + " degrade to false.");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String getMockServiceName(IMessage message, String serviceName)
/*     */   {
/* 236 */     String mockServiceName = message.getHeaders().getServiceName() + "MOCK";
/*     */     
/* 238 */     if (DEBUGGER.isInfoEnable())
/*     */     {
/* 240 */       DEBUGGER.info("Invalid mock service for the serviceName [" + serviceName + "] ,hence using default mock service [" + mockServiceName + ']');
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 245 */     return mockServiceName;
/*     */   }
/*     */   
/*     */ 
/*     */   private IMessage updateServiceName(IMessage message, String policyServiceName, String alternateServiceName)
/*     */   {
/* 251 */     if (DEBUGGER.isInfoEnable())
/*     */     {
/* 253 */       DEBUGGER.info("Alternate service configured for the [" + policyServiceName + "] service," + "hence request message redirected to alternate service [" + alternateServiceName + ']');
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 259 */     DEBUGGER.info("The message :requestID[" + message.getHeaders().getRequestId() + "] service name updated to [" + alternateServiceName + ']');
/*     */     
/*     */ 
/*     */ 
/* 263 */     message.getHeaders().setServiceName(alternateServiceName);
/* 264 */     return message;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 270 */     return "decode";
/*     */   }
/*     */   
/*     */ 
/*     */   public int getInWeight()
/*     */   {
/* 276 */     return this.inWeight;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\interceptor\impl\DecodeInterceptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */