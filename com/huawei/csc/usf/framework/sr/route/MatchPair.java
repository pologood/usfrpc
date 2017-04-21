/*     */ package com.huawei.csc.usf.framework.sr.route;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
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
/*     */ public class MatchPair
/*     */ {
/*  24 */   protected Set<String> matches = new HashSet();
/*     */   
/*  26 */   protected Set<String> unmatches = new HashSet();
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
/*     */   public void freeze()
/*     */   {
/*  45 */     this.matches = Collections.unmodifiableSet(this.matches);
/*  46 */     this.unmatches = Collections.unmodifiableSet(this.unmatches);
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
/*     */   public boolean pass(String sample)
/*     */   {
/*  66 */     if (this.unmatches.contains(sample))
/*     */     {
/*  68 */       return false;
/*     */     }
/*  70 */     if (this.matches.isEmpty())
/*     */     {
/*  72 */       return true;
/*     */     }
/*  74 */     return this.matches.contains(sample);
/*     */   }
/*     */   
/*     */   public Set<String> getMatches()
/*     */   {
/*  79 */     return this.matches;
/*     */   }
/*     */   
/*     */   public Set<String> getUnmatches()
/*     */   {
/*  84 */     return this.unmatches;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setMatches(Set<String> matches)
/*     */   {
/*  93 */     this.matches = matches;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setUnmatches(Set<String> unmatches)
/*     */   {
/* 102 */     this.unmatches = unmatches;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\route\MatchPair.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */