/*      */ package com.huawei.csc.usf.framework.util;
/*      */ 
/*      */ import com.huawei.csc.kernel.api.log.LogFactory;
/*      */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*      */ import com.huawei.csc.kernel.api.uconfig.UConfiguration;
/*      */ import com.huawei.csc.usf.framework.Connector;
/*      */ import com.huawei.csc.usf.framework.Context;
/*      */ import com.huawei.csc.usf.framework.ServiceEngine;
/*      */ import com.huawei.csc.usf.framework.config.SystemConfig;
/*      */ import com.huawei.csc.usf.framework.sr.ConfigurationInstanceInner;
/*      */ import com.huawei.csc.usf.framework.sr.DsfZookeeperDataManager;
/*      */ import com.huawei.csc.usf.framework.sr.SRAgentFactory;
/*      */ import com.huawei.csc.usf.framework.sr.ServiceGroup;
/*      */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
/*      */ import com.huawei.csc.usf.framework.sr.ServiceRegistryAgent;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.URI;
/*      */ import java.net.URISyntaxException;
/*      */ import java.net.UnknownHostException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.apache.commons.lang.StringUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Utils
/*      */ {
/*   59 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(Utils.class);
/*      */   
/*      */ 
/*      */   private static final String CONFIGURATION_INSTANCE_INNER_SCHEME = "override";
/*      */   
/*   64 */   private static String ip = null;
/*      */   
/*      */   static
/*      */   {
/*      */     try
/*      */     {
/*   70 */       ip = InetAddress.getLocalHost().getHostAddress();
/*      */     }
/*      */     catch (UnknownHostException e)
/*      */     {
/*   74 */       ip = "127.0.0.1";
/*   75 */       DEBUGGER.error("get machine ip failed, set dsf application is 127.0.0.1");
/*      */     }
/*      */   }
/*      */   
/*      */   public static InetSocketAddress parseIpPortToAddress(String address)
/*      */   {
/*      */     try
/*      */     {
/*   83 */       int location = address.indexOf("]:");
/*   84 */       int pos = address.lastIndexOf(':');
/*      */       
/*   86 */       if (-1 != location)
/*      */       {
/*   88 */         pos = location + 1;
/*      */       }
/*   90 */       if ((-1 == pos) || (pos + 1 >= address.length()))
/*      */       {
/*   92 */         IllegalArgumentException exception = new IllegalArgumentException("The IPPort must not be empty.Address:" + address);
/*      */         
/*   94 */         if (DEBUGGER.isErrorEnable())
/*      */         {
/*   96 */           DEBUGGER.error("The IPPort must not be empty.Address:" + address, exception);
/*      */         }
/*      */         
/*   99 */         throw exception;
/*      */       }
/*  101 */       String host = address.substring(0, pos);
/*      */       
/*  103 */       InetAddress inetAddress = InetAddress.getByName(host);
/*  104 */       if (isLocalAddress(inetAddress))
/*      */       {
/*  106 */         inetAddress = InetAddress.getLocalHost();
/*  107 */         if (isLocalAddress(inetAddress))
/*      */         {
/*      */ 
/*  110 */           String exceptionInfo = "The ip:port must not be local.Plz configue ebus.core.busListenAddress manaully.";
/*  111 */           throw new RuntimeException(exceptionInfo);
/*      */         }
/*      */       }
/*      */       
/*  115 */       int port = Integer.parseInt(address.substring(pos + 1));
/*  116 */       return new InetSocketAddress(inetAddress, port);
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*  120 */       if (DEBUGGER.isErrorEnable())
/*      */       {
/*  122 */         DEBUGGER.error("Failed to parse address[" + address + "].", e);
/*      */       }
/*  124 */       throw new IllegalArgumentException("Failed to parse address[" + address + "].", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static boolean isLocalAddress(InetAddress inetAddress)
/*      */   {
/*  131 */     return (inetAddress.isLoopbackAddress()) || ("0.0.0.0".equals(inetAddress.getHostAddress()));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String parseEbusIpList(String ipList)
/*      */   {
/*  138 */     String[] ipstr = ipList.split("\\;");
/*  139 */     boolean isIP = false;
/*  140 */     StringBuffer buffer = new StringBuffer("");
/*  141 */     for (int i = 0; i < ipstr.length; i++)
/*      */     {
/*      */ 
/*  144 */       if (ipstr[i].equals("*"))
/*      */       {
/*  146 */         isIP = true;
/*      */ 
/*      */       }
/*  149 */       else if (((ipstr[i].split("\\.").length > 1) && (ipstr[i].split("\\.").length < 4) && (ipstr[i].contains("*"))) || (ipstr[i].split("\\.").length == 4))
/*      */       {
/*      */ 
/*      */ 
/*  153 */         isIP = isEbusIPV4(ipstr[i]);
/*      */       }
/*  155 */       else if (((ipstr[i].split("\\:").length > 1) && (ipstr[i].split("\\:").length < 8) && (ipstr[i].contains("*"))) || (ipstr[i].split("\\:").length == 8))
/*      */       {
/*      */ 
/*      */ 
/*  159 */         isIP = isEbusIPV6(ipstr[i]);
/*      */       }
/*  161 */       if (isIP)
/*      */       {
/*  163 */         buffer.append(ipstr[i]).append(";");
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*  168 */       else if (DEBUGGER.isErrorEnable())
/*      */       {
/*  170 */         DEBUGGER.error("parse ip error, invalid ip: " + ipstr[i]);
/*      */       }
/*      */     }
/*      */     
/*  174 */     String resultIPList = buffer.toString();
/*  175 */     if (!StringUtils.isEmpty(resultIPList))
/*      */     {
/*  177 */       resultIPList = resultIPList.substring(0, resultIPList.length() - 1);
/*      */     }
/*  179 */     return resultIPList;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isEbusIPV4(String addr)
/*      */   {
/*  188 */     String rexp = "^(?:(?:[01]?\\d{1,2}|2[0-4]\\d|25[0-5]))";
/*  189 */     Pattern pat = Pattern.compile(rexp);
/*  190 */     String tmp = addr.replace("*", "255");
/*  191 */     String[] segment = tmp.split("\\.");
/*      */     
/*  193 */     for (int i = 0; i < segment.length; i++)
/*      */     {
/*  195 */       if (!pat.matcher(segment[i]).find())
/*      */       {
/*  197 */         return false;
/*      */       }
/*      */     }
/*      */     
/*  201 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isEbusIPV6(String addr)
/*      */   {
/*  210 */     Pattern IPV6_STD_PATTERN = Pattern.compile("^(?:[0-9A-Fa-f]{1,4})");
/*  211 */     String tmp = addr.replace("*", "FFFF");
/*  212 */     String[] segment = tmp.split("\\:");
/*      */     
/*  214 */     for (int i = 0; i < segment.length; i++)
/*      */     {
/*  216 */       if (!IPV6_STD_PATTERN.matcher(segment[i]).find())
/*      */       {
/*  218 */         return false;
/*      */       }
/*      */     }
/*      */     
/*  222 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public static String parseIpList(String ipList)
/*      */   {
/*  228 */     String[] ipstr = ipList.split("\\,");
/*  229 */     boolean isIP = false;
/*  230 */     StringBuffer buffer = new StringBuffer("");
/*  231 */     for (int i = 0; i < ipstr.length; i++)
/*      */     {
/*  233 */       if (ipstr[i].contains("."))
/*      */       {
/*  235 */         isIP = isIPV4(ipstr[i]);
/*      */       }
/*      */       else
/*      */       {
/*  239 */         isIP = isIPV6(ipstr[i]);
/*      */       }
/*  241 */       if (isIP)
/*      */       {
/*  243 */         buffer.append(ipstr[i]).append(";");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*  249 */       else if (DEBUGGER.isErrorEnable())
/*      */       {
/*  251 */         DEBUGGER.error("parse ip error, invalid ip: " + ipstr[i]);
/*      */       }
/*      */     }
/*      */     
/*  255 */     String resultIPList = buffer.toString();
/*  256 */     if (!StringUtils.isEmpty(resultIPList))
/*      */     {
/*  258 */       resultIPList = resultIPList.substring(0, resultIPList.length() - 1);
/*      */     }
/*  260 */     return resultIPList;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isIPV4(String addr)
/*      */   {
/*  277 */     String rexp = "^(?:(?:[01]?\\d{1,2}|2[0-4]\\d|25[0-5])\\.){3}(?:[01]?\\d{1,2}|2[0-4]\\d|25[0-5])\\b";
/*  278 */     Pattern pat = Pattern.compile(rexp);
/*  279 */     boolean isPattern = (addr.contains("*")) && (addr.endsWith("*"));
/*  280 */     if (isPattern)
/*      */     {
/*      */ 
/*  283 */       String[] segment = addr.split("\\.");
/*      */       
/*  285 */       StringBuffer buffer = new StringBuffer("255");
/*  286 */       for (int i = segment.length; i < 4; i++)
/*      */       {
/*      */ 
/*  289 */         buffer.append(".255");
/*      */       }
/*      */       
/*  292 */       String addstr = buffer.toString();
/*  293 */       String tmp = addr.replace("*", addstr);
/*  294 */       return pat.matcher(tmp).find();
/*      */     }
/*  296 */     if ((addr.length() < 7) || (addr.length() > 15) || ("".equals(addr)))
/*      */     {
/*  298 */       return false;
/*      */     }
/*      */     
/*  301 */     Matcher mat = pat.matcher(addr);
/*      */     
/*  303 */     boolean ipAddress = mat.find();
/*      */     
/*  305 */     return ipAddress;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isIPV6(String addr)
/*      */   {
/*  313 */     Pattern IPV6_STD_PATTERN = Pattern.compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");
/*      */     
/*      */ 
/*  316 */     Pattern IPV6_HEX_COMPRESSED_PATTERN = Pattern.compile("^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  339 */     boolean ipAddress = (IPV6_STD_PATTERN.matcher(addr).find()) || (IPV6_HEX_COMPRESSED_PATTERN.matcher(addr).find());
/*      */     
/*      */ 
/*  342 */     return ipAddress;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static List<String> resolveGroups(String groupStr)
/*      */   {
/*  362 */     List<String> groupList = new ArrayList();
/*  363 */     if (StringUtils.isBlank(groupStr))
/*      */     {
/*  365 */       groupList.add("default");
/*  366 */       return groupList;
/*      */     }
/*      */     
/*      */ 
/*  370 */     Set<String> groupSet = new HashSet();
/*  371 */     String[] groupArray = StringUtils.split(groupStr, ",");
/*  372 */     for (String group : groupArray)
/*      */     {
/*  374 */       String nomalizedGroup = group.trim();
/*  375 */       if (!StringUtils.isEmpty(nomalizedGroup))
/*      */       {
/*  377 */         groupSet.add(nomalizedGroup);
/*      */       }
/*      */     }
/*  380 */     groupList.addAll(groupSet);
/*  381 */     if (groupList.isEmpty())
/*      */     {
/*  383 */       groupList.add("default");
/*      */     }
/*  385 */     return groupList;
/*      */   }
/*      */   
/*      */   public static List<String> resolveRefGroups(String groupStr, Context context)
/*      */   {
/*  390 */     if (StringUtils.isBlank(groupStr))
/*      */     {
/*  392 */       groupStr = "default";
/*      */     }
/*  394 */     DsfZookeeperDataManager zookeeperDataManager = context.getSrcConnector().getServiceEngine().getSrAgentFactory().getSRAgent(context.getRegistry()).getZookeeperDataManager();
/*      */     
/*      */ 
/*      */ 
/*  398 */     Map<String, ServiceGroup> serviceGroup = zookeeperDataManager.getServiceGroup();
/*      */     
/*  400 */     List<String> groupList = new ArrayList();
/*      */     
/*  402 */     SystemConfig systemConfig = context.getSrcConnector().getServiceEngine().getSystemConfig();
/*      */     
/*  404 */     String rpcAddress = systemConfig.getRPCAddress(context.getServiceType());
/*      */     
/*  406 */     String dsfApplication = systemConfig.getDsfApplication();
/*  407 */     String consumerGroup = (String)zookeeperDataManager.getConfigFromConfiguration("ALL", "_CONSUMER_", rpcAddress, dsfApplication, "group", "provider");
/*      */     
/*      */ 
/*  410 */     String consumerconfigGroup = systemConfig.getProviderGroupName();
/*      */     
/*  412 */     if (StringUtils.isBlank(consumerGroup))
/*      */     {
/*  414 */       consumerGroup = consumerconfigGroup;
/*  415 */       if (StringUtils.isBlank(consumerGroup))
/*      */       {
/*  417 */         consumerGroup = "default";
/*      */       }
/*      */     }
/*  420 */     consumerGroup = consumerGroup.trim();
/*  421 */     ServiceGroup sGroup = (ServiceGroup)serviceGroup.get(consumerGroup);
/*      */     
/*  423 */     Set<String> groupSet = new HashSet();
/*      */     
/*  425 */     if (null == sGroup)
/*      */     {
/*  427 */       String[] consumerGroups = StringUtils.split(groupStr.trim(), ",");
/*  428 */       for (String cGroup : consumerGroups)
/*      */       {
/*  430 */         String nomalizedGroup = cGroup.trim();
/*  431 */         if (StringUtils.isNotBlank(nomalizedGroup))
/*      */         {
/*  433 */           groupSet.add(nomalizedGroup);
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  439 */       String refGroup = sGroup.getRefGroup();
/*  440 */       if (StringUtils.isNotBlank(refGroup))
/*      */       {
/*  442 */         String[] refGroupArray = StringUtils.split(refGroup, ",");
/*  443 */         for (String refG : refGroupArray)
/*      */         {
/*  445 */           String nomalizedRefGroup = refG.trim();
/*      */           
/*  447 */           if (!StringUtils.isEmpty(nomalizedRefGroup))
/*      */           {
/*  449 */             groupSet.add(nomalizedRefGroup);
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  455 */         groupSet.add("default");
/*      */       }
/*      */     }
/*  458 */     groupList.addAll(groupSet);
/*  459 */     if (groupList.isEmpty())
/*      */     {
/*  461 */       groupList.add("default");
/*      */     }
/*  463 */     if (DEBUGGER.isDebugEnable())
/*      */     {
/*  465 */       StringBuilder sb = new StringBuilder();
/*  466 */       sb.append("before resolve client group , group name is : ");
/*  467 */       if (StringUtils.isNotBlank(consumerconfigGroup))
/*      */       {
/*  469 */         sb.append(consumerconfigGroup);
/*      */       }
/*      */       else
/*      */       {
/*  473 */         sb.append("default");
/*      */       }
/*  475 */       sb.append(" , and ref group is " + groupStr);
/*  476 */       sb.append(" . after resolve client group , group name is : ");
/*  477 */       sb.append(consumerGroup);
/*  478 */       sb.append(" , and ref group is ");
/*  479 */       for (String gp : groupList)
/*      */       {
/*  481 */         sb.append(gp + " ");
/*      */       }
/*  483 */       DEBUGGER.debug(sb.toString());
/*      */     }
/*  485 */     return groupList;
/*      */   }
/*      */   
/*      */   public static List<String> resolveProtocols(String protocolStr)
/*      */   {
/*  490 */     List<String> protocolList = new ArrayList();
/*  491 */     if (StringUtils.isEmpty(protocolStr))
/*      */     {
/*  493 */       return protocolList;
/*      */     }
/*  495 */     Set<String> protocolSet = new HashSet();
/*  496 */     String[] protocolArray = StringUtils.split(protocolStr, ",");
/*  497 */     for (String protocol : protocolArray)
/*      */     {
/*  499 */       String nomalizedProtocol = protocol.trim();
/*  500 */       if (!StringUtils.isEmpty(nomalizedProtocol))
/*      */       {
/*  502 */         protocolSet.add(nomalizedProtocol);
/*      */       }
/*      */     }
/*  505 */     protocolList.addAll(protocolSet);
/*  506 */     return protocolList;
/*      */   }
/*      */   
/*      */   public static String conventRouterType(String routerId)
/*      */   {
/*  511 */     String routerType = null;
/*  512 */     if ("serviceDelayTimeRouter".equalsIgnoreCase(routerId))
/*      */     {
/*  514 */       routerType = "serviceDelayTimeRouter";
/*      */     }
/*  516 */     else if ("poll".equalsIgnoreCase(routerId))
/*      */     {
/*  518 */       routerType = "poll";
/*      */     }
/*  520 */     else if ("serverWeightRouter".equalsIgnoreCase(routerId))
/*      */     {
/*  522 */       routerType = "serverWeightRouter";
/*      */     }
/*      */     else
/*      */     {
/*  526 */       routerType = routerId;
/*      */     }
/*  528 */     return routerType;
/*      */   }
/*      */   
/*      */   public static String getHostIp()
/*      */   {
/*  533 */     return ip;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static ConfigurationInstanceInner toConfigurationInstanceInner(String confMessage)
/*      */   {
/*  546 */     ConfigurationInstanceInner instance = new ConfigurationInstanceInner();
/*      */     
/*  548 */     if (StringUtils.isBlank(confMessage))
/*      */     {
/*  550 */       DEBUGGER.error("Illegal url, url is empty.");
/*  551 */       return instance;
/*      */     }
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  557 */       URI uri = new URI(confMessage);
/*      */       
/*      */ 
/*  560 */       checkConfigurationInstanceInnerScheme(uri, confMessage);
/*      */       
/*      */ 
/*  563 */       setConfigurationInstanceInnerAddress(uri, instance, confMessage);
/*      */       
/*      */ 
/*  566 */       setConfigurationInstanceInnerServiceName(uri, instance, confMessage);
/*      */       
/*      */ 
/*  569 */       setConfigurationInstanceInnerAttributes(uri, instance, confMessage);
/*      */     }
/*      */     catch (URISyntaxException e)
/*      */     {
/*  573 */       if (DEBUGGER.isErrorEnable())
/*      */       {
/*  575 */         DEBUGGER.error("Illegal url : (" + confMessage + ").");
/*      */       }
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*  580 */       if (DEBUGGER.isErrorEnable())
/*      */       {
/*  582 */         DEBUGGER.error(e.getMessage());
/*      */       }
/*      */     }
/*      */     
/*  586 */     return instance;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void checkConfigurationInstanceInnerScheme(URI uri, String confMessage)
/*      */   {
/*  652 */     String scheme = uri.getScheme();
/*  653 */     if (StringUtils.isBlank(scheme))
/*      */     {
/*  655 */       throw new IllegalArgumentException("Illegal url : (" + confMessage + "), scheme is empty.");
/*      */     }
/*      */     
/*      */ 
/*  659 */     scheme = scheme.trim();
/*  660 */     if (!"override".equals(scheme))
/*      */     {
/*  662 */       StringBuilder msg = new StringBuilder();
/*  663 */       msg.append("Illegal url : (");
/*  664 */       msg.append(confMessage);
/*  665 */       msg.append("), scheme is not (");
/*  666 */       msg.append("override");
/*  667 */       msg.append(").");
/*  668 */       throw new IllegalArgumentException(msg.toString());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static void setConfigurationInstanceInnerAddress(URI uri, ConfigurationInstanceInner instance, String confMessage)
/*      */   {
/*  675 */     String address = uri.getAuthority();
/*  676 */     if (StringUtils.isBlank(address))
/*      */     {
/*  678 */       throw new IllegalArgumentException("Illegal url : (" + confMessage + "), address is empty.");
/*      */     }
/*      */     
/*      */ 
/*  682 */     address = address.trim();
/*  683 */     instance.setAddress(address);
/*      */   }
/*      */   
/*      */ 
/*      */   private static void setConfigurationInstanceInnerServiceName(URI uri, ConfigurationInstanceInner instance, String confMessage)
/*      */   {
/*  689 */     String serviceName = uri.getPath();
/*  690 */     if (StringUtils.isBlank(serviceName))
/*      */     {
/*  692 */       throw new IllegalArgumentException("Illegal url : (" + confMessage + "), service name is empty.");
/*      */     }
/*      */     
/*      */ 
/*  696 */     serviceName = serviceName.trim();
/*  697 */     if (!serviceName.startsWith("/"))
/*      */     {
/*  699 */       throw new IllegalArgumentException("Illegal url : (" + confMessage + "), service name is illegal.");
/*      */     }
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  705 */       serviceName = serviceName.substring(1, serviceName.length());
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*  709 */       throw new IllegalArgumentException("Illegal url : (" + confMessage + "), service name is illegal.");
/*      */     }
/*      */     
/*  712 */     if (StringUtils.isBlank(serviceName))
/*      */     {
/*  714 */       throw new IllegalArgumentException("Illegal url : (" + confMessage + "), service name is empty.");
/*      */     }
/*      */     
/*      */ 
/*  718 */     serviceName = serviceName.trim();
/*  719 */     instance.setServiceName(serviceName);
/*      */   }
/*      */   
/*      */ 
/*      */   private static void setConfigurationInstanceInnerAttributes(URI uri, ConfigurationInstanceInner instance, String confMessage)
/*      */   {
/*  725 */     String keyValuePairsStr = uri.getQuery();
/*  726 */     if (StringUtils.isBlank(keyValuePairsStr))
/*      */     {
/*  728 */       throw new IllegalArgumentException("Illegal url : (" + confMessage + "), attributes is empty.");
/*      */     }
/*      */     
/*      */ 
/*  732 */     String[] keyValuePairs = keyValuePairsStr.split("&");
/*  733 */     if ((null == keyValuePairs) || (0 >= keyValuePairs.length))
/*      */     {
/*  735 */       throw new IllegalArgumentException("Illegal url : (" + confMessage + "), attributes is empty.");
/*      */     }
/*      */     
/*      */ 
/*  739 */     Map<String, String> attributes = new HashMap();
/*  740 */     instance.setAttributes(attributes);
/*      */     
/*  742 */     for (String keyValuePair : keyValuePairs)
/*      */     {
/*  744 */       parseConfigurationInstanceInnerAttributesKeyValuePair(instance, confMessage, keyValuePair);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static ServiceInstanceInner parseServiceByConfiguration(ServiceInstanceInner instance, Context context)
/*      */   {
/*  752 */     ServiceEngine engin = context.getSrcConnector().getServiceEngine();
/*  753 */     DsfZookeeperDataManager zookeeperDataManager = engin.getSrAgentFactory().getSRAgent(context.getRegistry()).getZookeeperDataManager();
/*      */     
/*      */ 
/*  756 */     String group = (String)zookeeperDataManager.getConfigFromConfiguration("ALL", "_PROVIDER_", instance.getAddress(), instance.getApplication(), "group", "provider");
/*      */     
/*      */ 
/*      */ 
/*  760 */     if (null != group)
/*      */     {
/*  762 */       instance.setGroup(group);
/*  763 */       if (DEBUGGER.isDebugEnable())
/*      */       {
/*  765 */         DEBUGGER.debug("current instance change the group name , service name : " + instance.getServiceName() + " , before change group name is : " + instance.getGroup() + " , after change group name is : " + group);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  772 */     return instance;
/*      */   }
/*      */   
/*      */   public static ServiceGroup parseServiceGroup(ServiceGroup serviceGroup)
/*      */   {
/*  777 */     String refGroup = serviceGroup.getRefGroup();
/*  778 */     String[] refValue = StringUtils.split(refGroup, "=");
/*      */     
/*  780 */     if ((null == refValue) || (2 != refValue.length))
/*      */     {
/*  782 */       throw new IllegalArgumentException("Illegal refGroup Name :(" + refGroup + ") ,and group name is :(" + serviceGroup.getGroupName() + ")");
/*      */     }
/*      */     
/*      */ 
/*  786 */     serviceGroup.setRegGroup(refValue[1]);
/*  787 */     return serviceGroup;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static void parseConfigurationInstanceInnerAttributesKeyValuePair(ConfigurationInstanceInner instance, String confMessage, String keyValuePair)
/*      */   {
/*  794 */     if ((null == keyValuePair) || (0 >= keyValuePair.length()))
/*      */     {
/*  796 */       throw new IllegalArgumentException("Illegal url : (" + confMessage + "), there is empty attribute.");
/*      */     }
/*      */     
/*      */ 
/*  800 */     String[] keyValue = keyValuePair.split("=");
/*  801 */     if ((null == keyValue) || (2 != keyValue.length))
/*      */     {
/*  803 */       throw new IllegalArgumentException("Illegal url : (" + confMessage + "), attribute(" + keyValuePair + ") is illegal.");
/*      */     }
/*      */     
/*      */ 
/*  807 */     String key = keyValue[0];
/*  808 */     if (StringUtils.isBlank(key))
/*      */     {
/*  810 */       throw new IllegalArgumentException("Illegal url : (" + confMessage + "), the key of attribute(" + keyValuePair + ") is empty.");
/*      */     }
/*      */     
/*  813 */     key = key.trim();
/*      */     
/*  815 */     if (("router".equals(key)) || (".router".contains(key)))
/*      */     {
/*      */ 
/*  818 */       instance.setHasRouterType(true);
/*      */     }
/*      */     
/*  821 */     String value = keyValue[1];
/*  822 */     if (StringUtils.isBlank(key))
/*      */     {
/*  824 */       throw new IllegalArgumentException("Illegal url : (" + confMessage + "), the value of attribute(" + keyValuePair + ") is empty.");
/*      */     }
/*      */     
/*      */ 
/*  828 */     value = value.trim();
/*      */     
/*  830 */     instance.putAttribute(key, value);
/*      */   }
/*      */   
/*      */ 
/*      */   public static ServiceInstanceInner toInstanceInner(String url)
/*      */   {
/*  836 */     ServiceInstanceInner instanceInner = new ServiceInstanceInner();
/*  837 */     String protocol = null;
/*  838 */     String address = null;
/*  839 */     String serviceName = null;
/*  840 */     Map<String, String> parameters = null;
/*  841 */     int i = url.indexOf("?");
/*  842 */     if (i > 0)
/*      */     {
/*  844 */       String[] parts = url.substring(i + 1).split("\\&");
/*  845 */       parameters = new HashMap();
/*  846 */       for (String part : parts)
/*      */       {
/*  848 */         part = part.trim();
/*  849 */         if (part.length() > 0)
/*      */         {
/*  851 */           int j = part.indexOf("=");
/*  852 */           if (j >= 0)
/*      */           {
/*  854 */             parameters.put(part.substring(0, j), part.substring(j + 1));
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*  859 */             DEBUGGER.error("url register this part" + part + "has no value");
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  865 */     i = url.indexOf("://");
/*  866 */     if (i >= 0)
/*      */     {
/*  868 */       if (i == 0)
/*  869 */         DEBUGGER.error("url missing protocol: \"" + url + "\"");
/*  870 */       protocol = url.substring(0, i);
/*  871 */       url = url.substring(i + 3);
/*      */     }
/*      */     else
/*      */     {
/*  875 */       DEBUGGER.error("Illegal url : \"" + url + "\"");
/*      */     }
/*  877 */     i = url.indexOf("/");
/*  878 */     if (i >= 0)
/*      */     {
/*  880 */       address = url.substring(0, i);
/*      */     }
/*  882 */     i = url.indexOf("?");
/*  883 */     url = url.substring(0, i);
/*  884 */     i = url.indexOf("/");
/*  885 */     if (i > 0)
/*      */     {
/*  887 */       serviceName = url.substring(i + 1);
/*      */     }
/*      */     else
/*      */     {
/*  891 */       DEBUGGER.error("Illegal url , no service Name: \"" + url + "\"");
/*      */     }
/*  893 */     instanceInner.setType(protocol);
/*  894 */     instanceInner.setAddress(address);
/*  895 */     instanceInner.setInstanceName(serviceName);
/*  896 */     if ((parameters != null) && (!parameters.isEmpty()))
/*      */     {
/*  898 */       if (parameters.containsKey("version"))
/*      */       {
/*  900 */         instanceInner.setVersion((String)parameters.get("version"));
/*      */       }
/*  902 */       if (parameters.containsKey("group"))
/*      */       {
/*  904 */         instanceInner.setGroup((String)parameters.get("group"));
/*      */       }
/*  906 */       if (parameters.containsKey("serviceGroup"))
/*      */       {
/*  908 */         instanceInner.setServiceGroup((String)parameters.get("serviceGroup"));
/*      */       }
/*  910 */       if (parameters.containsKey("weight"))
/*      */       {
/*  912 */         instanceInner.setWeight(Integer.valueOf((String)parameters.get("weight")).intValue());
/*      */       }
/*      */       
/*  915 */       if (parameters.containsKey("executes"))
/*      */       {
/*      */ 
/*      */ 
/*  919 */         String excutes = (String)parameters.get("executes");
/*  920 */         int executes = 0;
/*  921 */         if ((StringUtils.isNotBlank(excutes)) && (!"null".equals(excutes)))
/*      */         {
/*  923 */           instanceInner.setExecutes(Integer.parseInt(excutes));
/*      */         }
/*  925 */         instanceInner.setExecutes(executes);
/*      */       }
/*  927 */       if (parameters.containsKey("methods"))
/*      */       {
/*  929 */         String methods = (String)parameters.get("methods");
/*  930 */         String[] methodsArr = methods.split(",");
/*  931 */         if (methodsArr.length > 0)
/*      */         {
/*  933 */           List<String> methodList = new ArrayList();
/*  934 */           for (String mathod : methodsArr)
/*      */           {
/*  936 */             methodList.add(mathod);
/*      */           }
/*  938 */           instanceInner.setMethods(methodList);
/*      */         }
/*      */       }
/*  941 */       if (parameters.containsKey("interface"))
/*      */       {
/*  943 */         instanceInner.setServiceName((String)parameters.get("interface"));
/*      */       }
/*  945 */       if (parameters.containsKey("type"))
/*      */       {
/*  947 */         instanceInner.setType((String)parameters.get("type"));
/*      */       }
/*  949 */       if (parameters.containsKey("serviceType"))
/*      */       {
/*  951 */         instanceInner.setServiceType((String)parameters.get("serviceType"));
/*      */       }
/*  953 */       if (parameters.containsKey("pid"))
/*      */       {
/*  955 */         instanceInner.setPid((String)parameters.get("pid"));
/*      */       }
/*  957 */       if (parameters.containsKey("application"))
/*      */       {
/*  959 */         instanceInner.setApplication((String)parameters.get("application"));
/*      */       }
/*  961 */       if (parameters.containsKey("rest.exportUrl"))
/*      */       {
/*  963 */         instanceInner.setRestfulUrl((String)parameters.get("rest.exportUrl"));
/*      */       }
/*  965 */       if (parameters.containsKey("timeout"))
/*      */       {
/*  967 */         instanceInner.setTimeout(Integer.parseInt((String)parameters.get("timeout")));
/*      */       }
/*      */       
/*  970 */       if (parameters.containsKey("router"))
/*      */       {
/*  972 */         instanceInner.setRouter((String)parameters.get("router"));
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  977 */       DEBUGGER.error("Illegal url , no any attrs: \"" + url + "\"");
/*      */     }
/*  979 */     return instanceInner;
/*      */   }
/*      */   
/*      */   public static boolean isEmpty(Collection<?> coll)
/*      */   {
/*  984 */     return (coll == null) || (coll.isEmpty());
/*      */   }
/*      */   
/*      */   public static boolean isNotEmpty(Collection<?> coll)
/*      */   {
/*  989 */     return !isEmpty(coll);
/*      */   }
/*      */   
/*      */   public static boolean isEmpty(Map<?, ?> map)
/*      */   {
/*  994 */     return (map == null) || (map.isEmpty());
/*      */   }
/*      */   
/*      */   public static boolean isNotEmpty(Map<?, ?> map)
/*      */   {
/*  999 */     return !isEmpty(map);
/*      */   }
/*      */   
/*      */   public static boolean isEmpty(String str)
/*      */   {
/* 1004 */     return (str == null) || (0 >= str.length());
/*      */   }
/*      */   
/*      */   public static boolean isNotEmpty(String str)
/*      */   {
/* 1009 */     return !isEmpty(str);
/*      */   }
/*      */   
/*      */   public static String parseZkUrl(UConfiguration uconfig, String key)
/*      */   {
/* 1014 */     String zkUrl = null;
/* 1015 */     Object urls = uconfig.getProperty(key);
/* 1016 */     if ((urls instanceof String))
/*      */     {
/* 1018 */       zkUrl = uconfig.getString(key);
/*      */     }
/* 1020 */     else if ((urls instanceof List))
/*      */     {
/* 1022 */       StringBuilder sb = new StringBuilder();
/* 1023 */       List list = (List)urls;
/* 1024 */       for (Object object : list)
/*      */       {
/* 1026 */         sb.append(object.toString());
/* 1027 */         sb.append(",");
/*      */       }
/* 1029 */       if (!list.isEmpty())
/*      */       {
/* 1031 */         zkUrl = sb.substring(0, sb.length() - 1);
/*      */       }
/*      */     }
/* 1034 */     return zkUrl;
/*      */   }
/*      */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\util\Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */