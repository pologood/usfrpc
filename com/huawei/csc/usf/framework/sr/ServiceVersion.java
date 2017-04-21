/*     */ package com.huawei.csc.usf.framework.sr;
/*     */ 
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServiceVersion
/*     */   implements Comparable<Object>
/*     */ {
/*     */   private final int major;
/*     */   private final int minor;
/*     */   private final int micro;
/*     */   private static final String SEPARATOR = ".";
/*  16 */   public static final ServiceVersion emptyVersion = new ServiceVersion(0, 0, 0);
/*     */   
/*     */   public ServiceVersion(int major, int minor, int micro)
/*     */   {
/*  20 */     this.major = major;
/*  21 */     this.minor = minor;
/*  22 */     this.micro = micro;
/*  23 */     validate();
/*     */   }
/*     */   
/*     */   public ServiceVersion(String version)
/*     */   {
/*  28 */     int major = 0;
/*  29 */     int minor = 0;
/*  30 */     int micro = 0;
/*     */     try
/*     */     {
/*  33 */       StringTokenizer st = new StringTokenizer(version, ".", true);
/*  34 */       major = Integer.parseInt(st.nextToken());
/*     */       
/*  36 */       if (st.hasMoreTokens())
/*     */       {
/*  38 */         st.nextToken();
/*  39 */         minor = Integer.parseInt(st.nextToken());
/*     */         
/*  41 */         if (st.hasMoreTokens())
/*     */         {
/*  43 */           st.nextToken();
/*  44 */           micro = Integer.parseInt(st.nextToken());
/*     */           
/*  46 */           if (st.hasMoreTokens()) {
/*  47 */             throw new IllegalArgumentException("invalid format");
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (NoSuchElementException e) {
/*  53 */       throw new IllegalArgumentException("invalid format");
/*     */     }
/*     */     catch (NullPointerException e)
/*     */     {
/*  57 */       throw new NullPointerException("version is " + version);
/*     */     }
/*  59 */     this.major = major;
/*  60 */     this.minor = minor;
/*  61 */     this.micro = micro;
/*  62 */     validate();
/*     */   }
/*     */   
/*     */   private void validate()
/*     */   {
/*  67 */     if (this.major < 0) {
/*  68 */       throw new IllegalArgumentException("negative major");
/*     */     }
/*  70 */     if (this.minor < 0) {
/*  71 */       throw new IllegalArgumentException("negative minor");
/*     */     }
/*  73 */     if (this.micro < 0) {
/*  74 */       throw new IllegalArgumentException("negative micro");
/*     */     }
/*     */   }
/*     */   
/*     */   public static ServiceVersion parseVersion(String version) {
/*  79 */     if (version == null)
/*     */     {
/*  81 */       return emptyVersion;
/*     */     }
/*     */     
/*  84 */     version = version.trim();
/*  85 */     if (version.length() == 0)
/*     */     {
/*  87 */       return emptyVersion;
/*     */     }
/*     */     
/*  90 */     return new ServiceVersion(version);
/*     */   }
/*     */   
/*     */   public int getMajor()
/*     */   {
/*  95 */     return this.major;
/*     */   }
/*     */   
/*     */   public int getMinor()
/*     */   {
/* 100 */     return this.minor;
/*     */   }
/*     */   
/*     */   public int getMicro()
/*     */   {
/* 105 */     return this.micro;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 110 */     return this.major + "." + this.minor + "." + this.micro;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 115 */     return (this.major << 24) + (this.minor << 16) + (this.micro << 8);
/*     */   }
/*     */   
/*     */   public boolean equals(Object object)
/*     */   {
/* 120 */     if (object == this)
/*     */     {
/* 122 */       return true;
/*     */     }
/*     */     
/* 125 */     if (!(object instanceof ServiceVersion))
/*     */     {
/* 127 */       return false;
/*     */     }
/*     */     
/* 130 */     ServiceVersion other = (ServiceVersion)object;
/* 131 */     return (this.major == other.major) && (this.minor == other.minor) && (this.micro == other.micro);
/*     */   }
/*     */   
/*     */ 
/*     */   public int compareTo(Object object)
/*     */   {
/* 137 */     if (object == this)
/*     */     {
/* 139 */       return 0;
/*     */     }
/*     */     
/* 142 */     ServiceVersion other = (ServiceVersion)object;
/*     */     
/* 144 */     int result = this.major - other.major;
/* 145 */     if (result != 0)
/*     */     {
/* 147 */       return result;
/*     */     }
/*     */     
/* 150 */     result = this.minor - other.minor;
/* 151 */     if (result != 0)
/*     */     {
/* 153 */       return result;
/*     */     }
/*     */     
/* 156 */     result = this.micro - other.micro;
/* 157 */     if (result != 0)
/*     */     {
/* 159 */       return result;
/*     */     }
/* 161 */     return 0;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\ServiceVersion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */