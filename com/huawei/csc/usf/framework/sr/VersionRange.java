/*     */ package com.huawei.csc.usf.framework.sr;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.util.Utils;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ public class VersionRange
/*     */ {
/*  12 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(VersionRange.class);
/*     */   
/*     */ 
/*  15 */   String[] rangTypeArray = { "\\[.+[^\\+]\\]", "\\[.+[^\\+]\\)", "\\(.+[^\\+]\\]", "\\(.+[^\\+]\\)", "\\[.+\\+{2}", "\\(.+\\+{2}" };
/*     */   
/*     */ 
/*  18 */   String regExOfVersion = "((\\d+\\.){0,2}\\d+)";
/*     */   
/*     */ 
/*     */   private ServiceVersion startVersion;
/*     */   
/*     */ 
/*     */   private ServiceVersion endVersion;
/*     */   
/*     */ 
/*     */   private RangType rangType;
/*     */   
/*     */ 
/*     */   public RangType getRangType()
/*     */   {
/*  32 */     return this.rangType;
/*     */   }
/*     */   
/*     */ 
/*     */   public VersionRange(String versionRange)
/*     */   {
/*  38 */     if (null == versionRange)
/*     */     {
/*  40 */       versionRange = "";
/*     */     }
/*  42 */     this.rangType = getTheRangeType(versionRange);
/*  43 */     if (null == this.rangType)
/*     */     {
/*  45 */       throw new IllegalArgumentException();
/*     */     }
/*  47 */     init(versionRange);
/*     */     
/*  49 */     if (LOGGER.isDebugEnable())
/*     */     {
/*  51 */       LOGGER.debug("the version is: " + versionRange);
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
/*     */   private void init(String versionRange)
/*     */   {
/*  64 */     Pattern pat = Pattern.compile(this.regExOfVersion);
/*  65 */     Matcher mat = pat.matcher(versionRange);
/*     */     boolean startVer;
/*  67 */     switch (this.rangType)
/*     */     {
/*     */     case ANY_VERSION: 
/*     */       break;
/*     */     
/*     */     case PRECISE: 
/*  73 */       this.endVersion = new ServiceVersion(versionRange);
/*  74 */       break;
/*     */     case START_END_BOTH_INCLUDE: 
/*     */     case START_INCLUDE: 
/*     */     case END_INCLUDE: 
/*     */     case START_END_NEITHER_INCLUDE: 
/*  79 */       startVer = true;
/*  80 */     case START_INCLUDE_TO_MAX: case START_NOT_INCLUDE_TO_MAX:  while (mat.find())
/*     */       {
/*     */ 
/*  83 */         if (startVer)
/*     */         {
/*  85 */           this.startVersion = new ServiceVersion(mat.group(1));
/*  86 */           startVer = false;
/*     */         }
/*     */         else
/*     */         {
/*  90 */           this.endVersion = new ServiceVersion(mat.group(1)); continue;
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  96 */           while (mat.find())
/*     */           {
/*  98 */             this.startVersion = new ServiceVersion(mat.group(1));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
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
/*     */   private RangType getTheRangeType(String versionRange)
/*     */   {
/* 116 */     Pattern pat = null;
/*     */     
/* 118 */     if (Utils.isEmpty(versionRange))
/*     */     {
/* 120 */       return RangType.ANY_VERSION;
/*     */     }
/*     */     
/* 123 */     Pattern patVersion = Pattern.compile(this.regExOfVersion);
/* 124 */     Matcher matVersion = patVersion.matcher(versionRange);
/* 125 */     if (matVersion.matches())
/*     */     {
/* 127 */       return RangType.PRECISE;
/*     */     }
/*     */     
/* 130 */     for (int i = 0; i < this.rangTypeArray.length; i++)
/*     */     {
/* 132 */       pat = Pattern.compile(this.rangTypeArray[i]);
/* 133 */       Matcher mat = pat.matcher(versionRange);
/* 134 */       if (mat.find())
/*     */       {
/* 136 */         return RangType.values()[i];
/*     */       }
/*     */     }
/* 139 */     return null;
/*     */   }
/*     */   
/*     */   public ServiceVersion getStartVersion()
/*     */   {
/* 144 */     return this.startVersion;
/*     */   }
/*     */   
/*     */   public ServiceVersion getEndVersion()
/*     */   {
/* 149 */     return this.endVersion;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isInTheRange(ServiceVersion temp)
/*     */   {
/* 155 */     boolean result = false;
/* 156 */     switch (this.rangType)
/*     */     {
/*     */ 
/*     */     case ANY_VERSION: 
/* 160 */       result = true;
/* 161 */       break;
/*     */     
/*     */     case PRECISE: 
/* 164 */       result = temp.compareTo(this.endVersion) == 0;
/* 165 */       break;
/*     */     
/*     */     case START_END_BOTH_INCLUDE: 
/* 168 */       result = (temp.compareTo(this.startVersion) >= 0) && (temp.compareTo(this.endVersion) <= 0);
/*     */       
/* 170 */       break;
/*     */     
/*     */     case START_INCLUDE: 
/* 173 */       result = (temp.compareTo(this.startVersion) >= 0) && (temp.compareTo(this.endVersion) < 0);
/*     */       
/* 175 */       break;
/*     */     
/*     */     case END_INCLUDE: 
/* 178 */       result = (temp.compareTo(this.startVersion) > 0) && (temp.compareTo(this.endVersion) <= 0);
/*     */       
/* 180 */       break;
/*     */     
/*     */     case START_END_NEITHER_INCLUDE: 
/* 183 */       result = (temp.compareTo(this.startVersion) > 0) && (temp.compareTo(this.endVersion) < 0);
/*     */       
/* 185 */       break;
/*     */     
/*     */ 
/*     */     case START_INCLUDE_TO_MAX: 
/* 189 */       result = temp.compareTo(this.startVersion) >= 0;
/* 190 */       break;
/*     */     
/*     */     case START_NOT_INCLUDE_TO_MAX: 
/* 193 */       result = temp.compareTo(this.startVersion) > 0;
/* 194 */       break;
/*     */     default: 
/* 196 */       result = false;
/*     */     }
/*     */     
/* 199 */     return result;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\VersionRange.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */