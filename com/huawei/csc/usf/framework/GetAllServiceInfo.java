/*    */ package com.huawei.csc.usf.framework;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*    */ import com.huawei.csc.usf.framework.pojo.PojoConnector;
/*    */ import com.huawei.csc.usf.framework.pojo.PojoServerInner;
/*    */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ import org.apache.commons.lang.StringUtils;
/*    */ 
/*    */ public class GetAllServiceInfo
/*    */ {
/* 16 */   private static GetAllServiceInfo MANAGER = new GetAllServiceInfo();
/*    */   
/* 18 */   private ServiceEngine engine = null;
/*    */   
/*    */   public static void init(ServiceEngine engine)
/*    */   {
/* 22 */     MANAGER.engine = engine;
/*    */   }
/*    */   
/*    */   public static List<ServiceInfo> getServicesInfo()
/*    */   {
/* 27 */     if (null == MANAGER.engine)
/*    */     {
/* 29 */       return new ArrayList();
/*    */     }
/* 31 */     Map<String, ServiceInfo> services = new HashMap();
/* 32 */     List<Connector> connectorList = MANAGER.engine.connectorList;
/* 33 */     for (Connector connector : connectorList)
/*    */     {
/* 35 */       if (connector.getClass().getName().equals(PojoConnector.class.getName()))
/*    */       {
/*    */ 
/* 38 */         Map<String, PojoServerInner> serverInners = ((PojoConnector)connector).getServerMapper();
/*    */         
/* 40 */         for (Map.Entry<String, PojoServerInner> serverInner : serverInners.entrySet())
/*    */         {
/*    */ 
/* 43 */           ServiceDefinition definition = ((PojoServerInner)serverInner.getValue()).getServiceDefinition();
/*    */           
/* 45 */           ServiceInfo serviceInfo = new ServiceInfo();
/* 46 */           serviceInfo.setServiceName((String)serverInner.getKey());
/* 47 */           serviceInfo.setIntf(((PojoServerInner)serverInner.getValue()).getIntf().getName());
/*    */           
/* 49 */           serviceInfo.setGroup(definition.getGroup());
/* 50 */           if (StringUtils.isBlank(definition.getVersion()))
/*    */           {
/* 52 */             serviceInfo.setVersion("0.0.0");
/*    */           }
/*    */           else
/*    */           {
/* 56 */             serviceInfo.setVersion(definition.getVersion());
/*    */           }
/* 58 */           serviceInfo.setAddress(MANAGER.engine.getSystemConfig().getRPCAddress(((PojoServerInner)serverInner.getValue()).getServiceType()));
/*    */           
/*    */ 
/* 61 */           if (StringUtils.isNotBlank(definition.getProtocolType()))
/*    */           {
/* 63 */             serviceInfo.setProtocal(definition.getProtocolType());
/*    */           }
/*    */           else
/*    */           {
/* 67 */             serviceInfo.setProtocal("pojo");
/*    */           }
/* 69 */           serviceInfo.setExecutes(definition.getExecutes());
/* 70 */           serviceInfo.setServiceType(((PojoServerInner)serverInner.getValue()).getServiceType().toString());
/*    */           
/* 72 */           services.put(serverInner.getKey(), serviceInfo);
/*    */         }
/*    */       }
/*    */     }
/* 76 */     return new ArrayList(services.values());
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\GetAllServiceInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */