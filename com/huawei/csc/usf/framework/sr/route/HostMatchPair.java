/*     */ package com.huawei.csc.usf.framework.sr.route;
/*     */ 
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
/*     */ public class HostMatchPair
/*     */   extends MatchPair
/*     */ {
/*     */   public boolean pass(String sample)
/*     */   {
/*  33 */     if ((!this.unmatches.isEmpty()) && ((this.unmatches.contains(sample)) || (isInRange(sample, this.unmatches))))
/*     */     {
/*     */ 
/*  36 */       return false;
/*     */     }
/*  38 */     if (this.matches.isEmpty())
/*     */     {
/*  40 */       return true;
/*     */     }
/*  42 */     return (this.matches.contains(sample)) || (isInRange(sample, this.matches));
/*     */   }
/*     */   
/*     */   private boolean isInRange(String host, Set<String> unmatches)
/*     */   {
/*  47 */     String[] ips = host.split("\\.");
/*     */     
/*     */ 
/*  50 */     if (ips.length != 4)
/*     */     {
/*  52 */       return false;
/*     */     }
/*     */     
/*  55 */     boolean inRange = false;
/*  56 */     for (String ip : unmatches)
/*     */     {
/*     */ 
/*  59 */       if (ip.contains("/"))
/*     */       {
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/*  65 */           inRange = isInRange(ips, ip);
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/*  69 */           inRange = false;
/*     */         }
/*  71 */         if (inRange) {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*  77 */     return inRange;
/*     */   }
/*     */   
/*     */ 
/*     */   private static boolean isInRange(String[] ips, String cidr)
/*     */     throws Exception
/*     */   {
/*  84 */     int ipAddr = Integer.parseInt(ips[0]) << 24 | Integer.parseInt(ips[1]) << 16 | Integer.parseInt(ips[2]) << 8 | Integer.parseInt(ips[3]);
/*     */     
/*     */ 
/*     */ 
/*  88 */     int type = Integer.parseInt(cidr.replaceAll(".*/", ""));
/*     */     
/*     */ 
/*  91 */     int mask = -1 << 32 - type;
/*     */     
/*  93 */     String cidrIp = cidr.replaceAll("/.*", "");
/*  94 */     String[] cidrIps = cidrIp.split("\\.");
/*     */     
/*     */ 
/*  97 */     int cidrIpAddr = Integer.parseInt(cidrIps[0]) << 24 | Integer.parseInt(cidrIps[1]) << 16 | Integer.parseInt(cidrIps[2]) << 8 | Integer.parseInt(cidrIps[3]);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 103 */     return (ipAddr & mask) == (cidrIpAddr & mask);
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\route\HostMatchPair.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */