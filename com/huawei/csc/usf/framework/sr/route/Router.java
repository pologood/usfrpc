/*     */ package com.huawei.csc.usf.framework.sr.route;
/*     */ 
/*     */ import com.huawei.csc.usf.framework.common.URL;
/*     */ import java.text.ParseException;
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
/*     */ public class Router
/*     */ {
/*     */   private final String name;
/*     */   private final String service;
/*     */   private final boolean enabled;
/*     */   private final URL url;
/*     */   private final int priority;
/*     */   private final RouteRule routeRule;
/*     */   
/*     */   public Router(URL url)
/*     */   {
/*  51 */     this.url = url;
/*  52 */     this.service = url.getPath();
/*     */     
/*  54 */     String name = url.getParameter("name");
/*  55 */     if ((name == null) || (name.trim().length() == 0))
/*     */     {
/*  57 */       throw new IllegalArgumentException("Illegal route name!");
/*     */     }
/*  59 */     this.name = name;
/*  60 */     this.enabled = url.getParameter("enabled", true);
/*  61 */     this.priority = url.getParameter("priority", 0);
/*     */     try
/*     */     {
/*  64 */       String rule = url.getParameterAndDecoded("rule");
/*  65 */       if ((rule == null) || (rule.trim().length() == 0))
/*     */       {
/*  67 */         throw new IllegalArgumentException("Illegal route rule!");
/*     */       }
/*  69 */       rule = rule.replace("consumer.", "").replace("provider.", "");
/*     */       
/*  71 */       this.routeRule = RouteRuleUtils.parse(rule);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (ParseException e)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  88 */       throw new IllegalStateException(e.getMessage(), e);
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
/*     */   public String getName()
/*     */   {
/* 104 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getService()
/*     */   {
/* 112 */     return this.service;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPriority()
/*     */   {
/* 120 */     return this.priority;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/* 128 */     return this.enabled;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public URL getUrl()
/*     */   {
/* 136 */     return this.url;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public RouteRule getRouteRule()
/*     */   {
/* 144 */     return this.routeRule;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\route\Router.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */