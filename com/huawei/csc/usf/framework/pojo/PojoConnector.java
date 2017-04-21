/*     */ package com.huawei.csc.usf.framework.pojo;
/*     */ 
/*     */ import com.huawei.csc.container.api.ContextRegistry;
/*     */ import com.huawei.csc.container.api.IContextHolder;
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.remoting.common.util.CastUtil;
/*     */ import com.huawei.csc.usf.framework.AbstractConnector;
/*     */ import com.huawei.csc.usf.framework.Context;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.ServiceDefinition;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.ServiceExporter;
/*     */ import com.huawei.csc.usf.framework.message.MessageImpl;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceInner;
/*     */ import com.huawei.csc.usf.framework.trace.MessageType;
/*     */ import com.huawei.csc.usf.framework.trace.TraceLinkUtilHelper;
/*     */ import com.huawei.csc.usf.framework.util.CopyOnWriteHashMap;
/*     */ import com.huawei.csc.usf.framework.util.LogTraceUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.aopalliance.intercept.MethodInvocation;
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
/*     */ 
/*     */ 
/*     */ public class PojoConnector
/*     */   extends AbstractConnector
/*     */   implements ServiceExporter
/*     */ {
/*  55 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(PojoConnector.class);
/*     */   
/*     */ 
/*     */   private static ProxyHandler proxyHandler;
/*     */   
/*     */ 
/*  61 */   protected Map<String, PojoServerInner> serverMapper = new CopyOnWriteHashMap();
/*     */   
/*     */ 
/*  64 */   protected Map<String, PojoClientInner> clientMapper = new CopyOnWriteHashMap();
/*     */   
/*     */ 
/*     */   public Collection<String> getExports()
/*     */   {
/*  69 */     return this.serverMapper.keySet();
/*     */   }
/*     */   
/*     */ 
/*     */   public List<ServiceInner> getServiceList()
/*     */   {
/*  75 */     List<ServiceInner> list = new ArrayList();
/*     */     
/*     */ 
/*  78 */     for (Map.Entry<String, PojoServerInner> entry : this.serverMapper.entrySet())
/*     */     {
/*  80 */       PojoServerInner pojoServer = (PojoServerInner)entry.getValue();
/*  81 */       ServiceInner service = pojoServer.parseService(this);
/*  82 */       list.add(service);
/*     */     }
/*     */     
/*     */ 
/*  86 */     for (Map.Entry<String, PojoClientInner> entry : this.clientMapper.entrySet())
/*     */     {
/*  88 */       PojoClientInner pojoClient = (PojoClientInner)entry.getValue();
/*  89 */       ServiceInner service = pojoClient.parseService(this);
/*  90 */       list.add(service);
/*     */     }
/*     */     
/*  93 */     return list;
/*     */   }
/*     */   
/*     */ 
/*     */   public void init()
/*     */     throws Exception
/*     */   {}
/*     */   
/*     */ 
/*     */   public void loadService(Object... objects)
/*     */   {
/* 104 */     setClientMapper(ContextRegistry.getContextHolder().getBeansOfType(PojoClientInner.class));
/*     */     
/* 106 */     setServerMapper(ContextRegistry.getContextHolder().getBeansOfType(PojoServerInner.class));
/*     */   }
/*     */   
/*     */ 
/*     */   public <T> T doOnReceive(Context context)
/*     */     throws Exception
/*     */   {
/* 113 */     context.setSrcConnector(this);
/* 114 */     context.setSysnFirstNode(true);
/* 115 */     this.serviceEngine.onReceive(context);
/* 116 */     Throwable th = context.getException();
/*     */     
/* 118 */     if ((null != th) && ((th instanceof Exception)))
/*     */     {
/* 120 */       throw ((Exception)context.getException());
/*     */     }
/* 122 */     if (null != th)
/*     */     {
/* 124 */       throw new Exception("Unexpected exception happend.", context.getException());
/*     */     }
/*     */     
/*     */ 
/* 128 */     return (T)CastUtil.cast(context.getReplyObject());
/*     */   }
/*     */   
/*     */ 
/*     */   public IMessage decode(Context context)
/*     */     throws Exception
/*     */   {
/* 135 */     IMessage request = null;
/*     */     
/* 137 */     Object[] nativeMessage = context.getNativeInputs();
/* 138 */     PojoClientInner client = (PojoClientInner)nativeMessage[0];
/* 139 */     MethodInvocation invocation = (MethodInvocation)nativeMessage[1];
/* 140 */     if (3 == nativeMessage.length)
/*     */     {
/* 142 */       request = (IMessage)nativeMessage[2];
/*     */     }
/*     */     else
/*     */     {
/* 146 */       request = client.invocationToMessage(invocation, getConnectorType());
/*     */     }
/*     */     
/*     */ 
/* 150 */     return request;
/*     */   }
/*     */   
/*     */   public <T> T encode(Context context, IMessage message)
/*     */     throws Exception
/*     */   {
/* 156 */     return (T)CastUtil.cast(message.getPayload());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public IMessage doHandle(Context context, IMessage requestMessage)
/*     */     throws Exception
/*     */   {
/* 164 */     IMessage request = context.getReceivedMessage();
/*     */     
/* 166 */     String service = request.getHeaders().getServiceName();
/*     */     
/* 168 */     PojoServerInner server = (PojoServerInner)this.serverMapper.get(service);
/*     */     
/* 170 */     IMessage reply = server.invoke(request);
/*     */     
/*     */ 
/* 173 */     if (!(requestMessage instanceof MessageImpl))
/*     */     {
/* 175 */       MessageObjectException messageObjectException = this.serviceEngine.getMessageObjectException();
/*     */       
/* 177 */       if (null != messageObjectException)
/*     */       {
/* 179 */         boolean hasException = messageObjectException.containsException(reply);
/*     */         
/* 181 */         context.setEbusException(hasException);
/*     */       }
/*     */     }
/* 184 */     return reply;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void destroy()
/*     */     throws Exception
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void setClientMapper(Map<String, PojoClientInner> clientMapper)
/*     */   {
/* 197 */     for (Map.Entry<String, PojoClientInner> entry : clientMapper.entrySet())
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 207 */       if (DEBUGGER.isInfoEnable())
/*     */       {
/* 209 */         DEBUGGER.info("import service,service name " + (String)entry.getKey());
/*     */       }
/*     */       
/* 212 */       this.clientMapper.put(entry.getKey(), entry.getValue());
/* 213 */       ((PojoClientInner)entry.getValue()).setConnector(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void setServerMapper(Map<String, PojoServerInner> serverMapper)
/*     */   {
/* 220 */     for (Map.Entry<String, PojoServerInner> entry : serverMapper.entrySet())
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 229 */       if (DEBUGGER.isInfoEnable())
/*     */       {
/* 231 */         DEBUGGER.info("export service,service name " + (String)entry.getKey());
/*     */       }
/* 233 */       this.serverMapper.put(entry.getKey(), entry.getValue());
/* 234 */       ((PojoServerInner)entry.getValue()).setConnector(this);
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
/*     */   public PojoServerInner getPojoServer(String serviceName)
/*     */   {
/* 262 */     return (PojoServerInner)this.serverMapper.get(serviceName);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getConnectorType()
/*     */   {
/* 268 */     return "POJO";
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isAsync()
/*     */   {
/* 274 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public List<ServiceInner> getExportServiceList()
/*     */   {
/* 280 */     List<ServiceInner> list = new ArrayList();
/*     */     
/*     */ 
/* 283 */     for (Map.Entry<String, PojoServerInner> entry : this.serverMapper.entrySet())
/*     */     {
/* 285 */       PojoServerInner pojoServer = (PojoServerInner)entry.getValue();
/* 286 */       ServiceInner service = pojoServer.parseService(this);
/* 287 */       list.add(service);
/*     */     }
/*     */     
/* 290 */     return list;
/*     */   }
/*     */   
/*     */ 
/*     */   public List<ServiceDefinition> getServiceDefinitions()
/*     */   {
/* 296 */     List<ServiceDefinition> list = new ArrayList();
/*     */     
/*     */ 
/* 299 */     for (Map.Entry<String, PojoServerInner> entry : this.serverMapper.entrySet())
/*     */     {
/* 301 */       list.add(((PojoServerInner)entry.getValue()).getServiceDefinition());
/*     */     }
/*     */     
/*     */ 
/* 305 */     for (Map.Entry<String, PojoClientInner> entry : this.clientMapper.entrySet())
/*     */     {
/* 307 */       list.add(((PojoClientInner)entry.getValue()).getServiceDefinition());
/*     */     }
/*     */     
/* 310 */     return list;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<String, PojoServerInner> getServerMapper()
/*     */   {
/* 318 */     return this.serverMapper;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<String, PojoClientInner> getClientMapper()
/*     */   {
/* 326 */     return this.clientMapper;
/*     */   }
/*     */   
/*     */   public <T> T findClient(String name)
/*     */   {
/* 331 */     PojoClientInner client = (PojoClientInner)this.clientMapper.get(name);
/* 332 */     if (null == client)
/*     */     {
/* 334 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 338 */     return (T)CastUtil.cast(client.getProxy());
/*     */   }
/*     */   
/*     */ 
/*     */   public void addServer(PojoServerInner pojoServer)
/*     */   {
/* 344 */     if (null == pojoServer)
/*     */     {
/* 346 */       return;
/*     */     }
/* 348 */     pojoServer.setConnector(this);
/* 349 */     this.serverMapper.put(pojoServer.getServiceName(), pojoServer);
/*     */   }
/*     */   
/*     */   public void addClient(String localName, PojoClientInner pojoClient)
/*     */   {
/* 354 */     pojoClient.setConnector(this);
/* 355 */     this.clientMapper.put(localName, pojoClient);
/*     */   }
/*     */   
/*     */ 
/*     */   public void startTraceLog(IMessage message)
/*     */   {
/* 361 */     LogTraceUtil.doStartTrace(message);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void asyncEndTraceLog(IMessage message) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public static ProxyHandler getProxyHandler()
/*     */   {
/* 372 */     if (null == proxyHandler)
/*     */     {
/* 374 */       proxyHandler = new DefaultProxyHandler();
/*     */     }
/* 376 */     return proxyHandler;
/*     */   }
/*     */   
/*     */   public static void setProxyHandler(ProxyHandler proxyHandler)
/*     */   {
/* 381 */     proxyHandler = proxyHandler;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void doClientBegin(Context context)
/*     */   {
/* 387 */     TraceLinkUtilHelper.clientLinkTraceBegin(context);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void doServerBegin(Context context)
/*     */   {
/* 393 */     TraceLinkUtilHelper.serverLinkTraceBegin(context);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void doServerEnd(Context context)
/*     */   {
/* 399 */     TraceLinkUtilHelper.serverlinkTraceEnd(context);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void doClientEnd(Context context)
/*     */   {
/* 405 */     TraceLinkUtilHelper.clientlinkTraceEnd(context, MessageType.CLIENTRECV);
/*     */   }
/*     */   
/*     */ 
/*     */   public void doHeadBegin(Context context)
/*     */   {
/* 411 */     if (!isPojo())
/*     */     {
/* 413 */       super.doHeadBegin(context);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void doHeadEnd(Context context)
/*     */   {
/* 420 */     if (!isPojo())
/*     */     {
/* 422 */       super.doHeadEnd(context);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean isPojo()
/*     */   {
/* 434 */     boolean isPojo = false;
/* 435 */     String connectorType = getConnectorType();
/* 436 */     if (("COMMONASYNC".equals(connectorType)) || ("COMMONASYNCEBUS".equals(connectorType)) || ("COMMONSYNC".equals(connectorType)) || ("COMMONSYNCEBUS".equals(connectorType)) || ("POJO".equals(connectorType)))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 444 */       isPojo = true;
/*     */     }
/* 446 */     return isPojo;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\pojo\PojoConnector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */