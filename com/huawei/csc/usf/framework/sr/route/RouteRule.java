/*     */ package com.huawei.csc.usf.framework.sr.route;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
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
/*     */ public class RouteRule
/*     */ {
/*     */   private final Map<String, MatchPair> whenCondition;
/*     */   private final Map<String, MatchPair> thenCondition;
/*  38 */   private volatile String tostring = null;
/*     */   
/*  40 */   static final RouteRule EMPTY = new RouteRule(Collections.emptyMap(), Collections.emptyMap());
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public RouteRule(Map<String, MatchPair> when, Map<String, MatchPair> then)
/*     */   {
/*  47 */     for (Map.Entry<String, MatchPair> entry : when.entrySet())
/*     */     {
/*  49 */       ((MatchPair)entry.getValue()).freeze();
/*     */     }
/*  51 */     for (Map.Entry<String, MatchPair> entry : then.entrySet())
/*     */     {
/*  53 */       ((MatchPair)entry.getValue()).freeze();
/*     */     }
/*     */     
/*     */ 
/*  57 */     this.whenCondition = when;
/*  58 */     this.thenCondition = then;
/*     */   }
/*     */   
/*     */   public Map<String, MatchPair> getWhenCondition()
/*     */   {
/*  63 */     return this.whenCondition;
/*     */   }
/*     */   
/*     */   public Map<String, MatchPair> getThenCondition()
/*     */   {
/*  68 */     return this.thenCondition;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/*  74 */     if (this.tostring != null)
/*  75 */       return this.tostring;
/*  76 */     StringBuilder sb = new StringBuilder(512);
/*  77 */     contidionToString(sb, this.whenCondition);
/*  78 */     sb.append(" => ");
/*  79 */     contidionToString(sb, this.thenCondition);
/*  80 */     return this.tostring = sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   private void contidionToString(StringBuilder sb, Map<String, MatchPair> condition)
/*     */   {
/*  86 */     boolean isFirst = true;
/*  87 */     for (Map.Entry<String, MatchPair> entry : condition.entrySet())
/*     */     {
/*  89 */       String keyName = (String)entry.getKey();
/*  90 */       MatchPair p = (MatchPair)entry.getValue();
/*     */       
/*  92 */       List<Set<String>> setList = new ArrayList();
/*  93 */       setList.add(p.getMatches());
/*  94 */       setList.add(p.getUnmatches());
/*  95 */       String[] opArray = { " = ", " != " };
/*     */       
/*  97 */       for (int i = 0; i < setList.size(); i++)
/*     */       {
/*  99 */         if (!((Set)setList.get(i)).isEmpty())
/*     */         {
/*     */ 
/*     */ 
/* 103 */           if (isFirst)
/*     */           {
/* 105 */             isFirst = false;
/*     */           }
/*     */           else
/*     */           {
/* 109 */             sb.append(" & ");
/*     */           }
/*     */           
/* 112 */           sb.append(keyName);
/* 113 */           sb.append(opArray[i]);
/* 114 */           join(sb, (Set)setList.get(i));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void join(StringBuilder sb, Set<String> valueSet) {
/* 121 */     boolean isFirst = true;
/* 122 */     for (String s : valueSet)
/*     */     {
/* 124 */       if (isFirst)
/*     */       {
/* 126 */         isFirst = false;
/*     */       }
/*     */       else
/*     */       {
/* 130 */         sb.append(",");
/*     */       }
/* 132 */       sb.append(s);
/*     */     }
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\route\RouteRule.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */