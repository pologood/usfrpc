/*     */ package com.huawei.csc.usf.framework.common;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.URLDecoder;
/*     */ import java.net.URLEncoder;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class URL
/*     */ {
/*     */   private final String protocol;
/*     */   private final String host;
/*     */   private final int port;
/*     */   private final String path;
/*     */   private final Map<String, String> parameters;
/*     */   private volatile transient String ip;
/*     */   private volatile transient Map<String, Number> numbers;
/*     */   private volatile transient String string;
/*     */   
/*     */   public URL(String protocol, String host, int port, String path, Map<String, String> parameters)
/*     */   {
/*  78 */     this.protocol = protocol;
/*  79 */     this.host = host;
/*  80 */     this.port = (port < 0 ? 0 : port);
/*     */     
/*  82 */     while ((path != null) && (path.startsWith("/")))
/*     */     {
/*  84 */       path = path.substring(1);
/*     */     }
/*  86 */     this.path = path;
/*  87 */     if (parameters == null)
/*     */     {
/*  89 */       parameters = new HashMap();
/*     */     }
/*     */     else
/*     */     {
/*  93 */       parameters = new HashMap(parameters);
/*     */     }
/*  95 */     this.parameters = Collections.unmodifiableMap(parameters);
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
/*     */   public static URL valueOf(String url)
/*     */   {
/* 108 */     if ((url == null) || ((url = url.trim()).length() == 0))
/*     */     {
/* 110 */       throw new IllegalArgumentException("url == null");
/*     */     }
/* 112 */     String protocol = null;
/*     */     
/*     */ 
/* 115 */     String host = null;
/* 116 */     int port = 0;
/* 117 */     String path = null;
/* 118 */     Map<String, String> parameters = null;
/* 119 */     int i = url.indexOf("?");
/* 120 */     if (i >= 0)
/*     */     {
/* 122 */       String[] parts = url.substring(i + 1).split("\\&");
/* 123 */       parameters = new HashMap();
/* 124 */       for (String part : parts)
/*     */       {
/* 126 */         part = part.trim();
/* 127 */         if (part.length() > 0)
/*     */         {
/* 129 */           int j = part.indexOf('=');
/* 130 */           if (j >= 0)
/*     */           {
/* 132 */             parameters.put(part.substring(0, j), part.substring(j + 1));
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/* 137 */             parameters.put(part, part);
/*     */           }
/*     */         }
/*     */       }
/* 141 */       url = url.substring(0, i);
/*     */     }
/* 143 */     i = url.indexOf("://");
/* 144 */     if (i >= 0)
/*     */     {
/* 146 */       if (i == 0)
/*     */       {
/* 148 */         throw new IllegalStateException("url missing protocol: \"" + url + "\"");
/*     */       }
/*     */       
/* 151 */       protocol = url.substring(0, i);
/* 152 */       url = url.substring(i + 3);
/*     */     }
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
/* 168 */     i = url.indexOf("/");
/* 169 */     if (i >= 0)
/*     */     {
/* 171 */       path = url.substring(i + 1);
/* 172 */       url = url.substring(0, i);
/*     */     }
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
/* 186 */     i = url.indexOf(":");
/* 187 */     if ((i >= 0) && (i < url.length() - 1))
/*     */     {
/* 189 */       port = Integer.parseInt(url.substring(i + 1));
/* 190 */       url = url.substring(0, i);
/*     */     }
/* 192 */     if (url.length() > 0)
/*     */     {
/* 194 */       host = url;
/*     */     }
/* 196 */     return new URL(protocol, host, port, path, parameters);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getProtocol()
/*     */   {
/* 204 */     return this.protocol;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getPath()
/*     */   {
/* 212 */     return this.path;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getHost()
/*     */   {
/* 220 */     return this.host;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 228 */     return this.port;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<String, String> getParameters()
/*     */   {
/* 236 */     return this.parameters;
/*     */   }
/*     */   
/*     */   public String getIp()
/*     */   {
/* 241 */     if (this.ip == null)
/*     */     {
/* 243 */       this.ip = getIpByHost(this.host);
/*     */     }
/* 245 */     return this.ip;
/*     */   }
/*     */   
/*     */   private String getIpByHost(String hostName)
/*     */   {
/*     */     try
/*     */     {
/* 252 */       return InetAddress.getByName(hostName).getHostAddress();
/*     */     }
/*     */     catch (UnknownHostException e) {}
/*     */     
/* 256 */     return hostName;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getAddress()
/*     */   {
/* 262 */     return this.host + ":" + this.port;
/*     */   }
/*     */   
/*     */   public String getParameterAndDecoded(String key)
/*     */   {
/* 267 */     return getParameterAndDecoded(key, null);
/*     */   }
/*     */   
/*     */   public String getParameterAndDecoded(String key, String defaultValue)
/*     */   {
/* 272 */     return decode(getParameter(key, defaultValue));
/*     */   }
/*     */   
/*     */   public String getParameter(String key)
/*     */   {
/* 277 */     String value = (String)this.parameters.get(key);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 282 */     return value;
/*     */   }
/*     */   
/*     */   public String getParameter(String key, String defaultValue)
/*     */   {
/* 287 */     String value = getParameter(key);
/* 288 */     if ((value == null) || (value.length() == 0))
/*     */     {
/* 290 */       return defaultValue;
/*     */     }
/* 292 */     return value;
/*     */   }
/*     */   
/*     */   public int getParameter(String key, int defaultValue)
/*     */   {
/* 297 */     Number n = (Number)getNumbers().get(key);
/* 298 */     if (n != null)
/*     */     {
/* 300 */       return n.intValue();
/*     */     }
/* 302 */     String value = getParameter(key);
/* 303 */     if ((value == null) || (value.length() == 0))
/*     */     {
/* 305 */       return defaultValue;
/*     */     }
/* 307 */     int i = Integer.parseInt(value);
/* 308 */     getNumbers().put(key, Integer.valueOf(i));
/* 309 */     return i;
/*     */   }
/*     */   
/*     */   public boolean getParameter(String key, boolean defaultValue)
/*     */   {
/* 314 */     String value = getParameter(key);
/* 315 */     if ((value == null) || (value.length() == 0))
/*     */     {
/* 317 */       return defaultValue;
/*     */     }
/* 319 */     return Boolean.parseBoolean(value);
/*     */   }
/*     */   
/*     */   private Map<String, Number> getNumbers()
/*     */   {
/* 324 */     if (this.numbers == null)
/*     */     {
/*     */ 
/* 327 */       this.numbers = new ConcurrentHashMap();
/*     */     }
/* 329 */     return this.numbers;
/*     */   }
/*     */   
/*     */   public static String encode(String value)
/*     */   {
/* 334 */     if ((value == null) || (value.length() == 0))
/*     */     {
/* 336 */       return "";
/*     */     }
/*     */     try
/*     */     {
/* 340 */       return URLEncoder.encode(value, "UTF-8");
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/* 344 */       throw new RuntimeException(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */   
/*     */   public static String decode(String value)
/*     */   {
/* 350 */     if ((value == null) || (value.length() == 0))
/*     */     {
/* 352 */       return "";
/*     */     }
/*     */     try
/*     */     {
/* 356 */       return URLDecoder.decode(value, "UTF-8");
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/* 360 */       throw new RuntimeException(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 366 */     if (this.string != null)
/*     */     {
/* 368 */       return this.string;
/*     */     }
/* 370 */     return this.string = buildString();
/*     */   }
/*     */   
/*     */   private String buildString()
/*     */   {
/* 375 */     StringBuilder buf = new StringBuilder();
/* 376 */     if ((this.protocol != null) && (this.protocol.length() > 0))
/*     */     {
/* 378 */       buf.append(this.protocol);
/* 379 */       buf.append("://");
/*     */     }
/*     */     
/* 382 */     String host = getHost();
/*     */     
/* 384 */     if ((host != null) && (host.length() > 0))
/*     */     {
/* 386 */       buf.append(host);
/* 387 */       if (this.port > 0)
/*     */       {
/* 389 */         buf.append(":");
/* 390 */         buf.append(this.port);
/*     */       }
/*     */     }
/* 393 */     String path = getPath();
/* 394 */     if ((path != null) && (path.length() > 0))
/*     */     {
/* 396 */       buf.append("/");
/* 397 */       buf.append(path);
/*     */     }
/*     */     
/* 400 */     buildParameters(buf);
/*     */     
/* 402 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private void buildParameters(StringBuilder buf)
/*     */   {
/* 407 */     Map<String, String> parameters = getParameters();
/* 408 */     boolean first; if ((parameters != null) && (parameters.size() > 0))
/*     */     {
/* 410 */       first = true;
/* 411 */       for (Map.Entry<String, String> entry : parameters.entrySet())
/*     */       {
/* 413 */         if (first)
/*     */         {
/* 415 */           buf.append("?");
/* 416 */           first = false;
/*     */         }
/*     */         else
/*     */         {
/* 420 */           buf.append("&");
/*     */         }
/* 422 */         buf.append((String)entry.getKey());
/* 423 */         buf.append("=");
/* 424 */         buf.append(entry.getValue() == null ? "" : ((String)entry.getValue()).trim());
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\common\URL.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */