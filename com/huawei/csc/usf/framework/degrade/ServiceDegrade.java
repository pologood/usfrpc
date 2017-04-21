/*     */ package com.huawei.csc.usf.framework.degrade;
/*     */ 
/*     */ 
/*     */ public class ServiceDegrade
/*     */ {
/*     */   private String mockServiceName;
/*     */   
/*     */   private String exceptionCode;
/*   9 */   private boolean methodForceReturnNull = false;
/*     */   
/*  11 */   private boolean methodForceReturnException = false;
/*     */   
/*  13 */   private boolean methodForceReturnMockService = false;
/*     */   
/*  15 */   private boolean methodFailReturnNull = false;
/*     */   
/*  17 */   private boolean forceReturnNull = false;
/*     */   
/*  19 */   private boolean forceReturnException = false;
/*     */   
/*  21 */   private boolean forceReturnMockService = false;
/*     */   
/*  23 */   private boolean failReturnNull = false;
/*     */   
/*  25 */   private boolean falseReturn = false;
/*     */   
/*  27 */   private boolean methodFalseReturn = false;
/*     */   
/*     */   public String getMockServiceName()
/*     */   {
/*  31 */     return this.mockServiceName;
/*     */   }
/*     */   
/*     */   public void setMockServiceName(String mockServiceName)
/*     */   {
/*  36 */     this.mockServiceName = mockServiceName;
/*     */   }
/*     */   
/*     */   public String getExceptionCode()
/*     */   {
/*  41 */     return this.exceptionCode;
/*     */   }
/*     */   
/*     */   public void setExceptionCode(String exceptionCode)
/*     */   {
/*  46 */     this.exceptionCode = exceptionCode;
/*     */   }
/*     */   
/*     */   public boolean isMethodForceReturnNull()
/*     */   {
/*  51 */     return this.methodForceReturnNull;
/*     */   }
/*     */   
/*     */   public void setMethodForceReturnNull(boolean methodForceReturnNull)
/*     */   {
/*  56 */     this.methodForceReturnNull = methodForceReturnNull;
/*     */   }
/*     */   
/*     */   public boolean isMethodForceReturnException()
/*     */   {
/*  61 */     return this.methodForceReturnException;
/*     */   }
/*     */   
/*     */   public void setMethodForceReturnException(boolean methodForceReturnException)
/*     */   {
/*  66 */     this.methodForceReturnException = methodForceReturnException;
/*     */   }
/*     */   
/*     */   public boolean isMethodForceReturnMockService()
/*     */   {
/*  71 */     return this.methodForceReturnMockService;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setMethodForceReturnMockService(boolean methodForceReturnMockService)
/*     */   {
/*  77 */     this.methodForceReturnMockService = methodForceReturnMockService;
/*     */   }
/*     */   
/*     */   public boolean isMethodFailReturnNull()
/*     */   {
/*  82 */     return this.methodFailReturnNull;
/*     */   }
/*     */   
/*     */   public void setMethodFailReturnNull(boolean methodFailReturnNull)
/*     */   {
/*  87 */     this.methodFailReturnNull = methodFailReturnNull;
/*     */   }
/*     */   
/*     */   public boolean isForceReturnNull()
/*     */   {
/*  92 */     return this.forceReturnNull;
/*     */   }
/*     */   
/*     */   public void setForceReturnNull(boolean forceReturnNull)
/*     */   {
/*  97 */     this.forceReturnNull = forceReturnNull;
/*     */   }
/*     */   
/*     */   public boolean isForceReturnException()
/*     */   {
/* 102 */     return this.forceReturnException;
/*     */   }
/*     */   
/*     */   public void setForceReturnException(boolean forceReturnException)
/*     */   {
/* 107 */     this.forceReturnException = forceReturnException;
/*     */   }
/*     */   
/*     */   public boolean isForceReturnMockService()
/*     */   {
/* 112 */     return this.forceReturnMockService;
/*     */   }
/*     */   
/*     */   public void setForceReturnMockService(boolean forceReturnMockService)
/*     */   {
/* 117 */     this.forceReturnMockService = forceReturnMockService;
/*     */   }
/*     */   
/*     */   public boolean isFailReturnNull()
/*     */   {
/* 122 */     return this.failReturnNull;
/*     */   }
/*     */   
/*     */   public void setFailReturnNull(boolean failReturnNull)
/*     */   {
/* 127 */     this.failReturnNull = failReturnNull;
/*     */   }
/*     */   
/*     */   public boolean isFalseReturn()
/*     */   {
/* 132 */     return this.falseReturn;
/*     */   }
/*     */   
/*     */   public void setFalseReturn(boolean falseReturn)
/*     */   {
/* 137 */     this.falseReturn = falseReturn;
/*     */   }
/*     */   
/*     */   public boolean isMethodFalseReturn()
/*     */   {
/* 142 */     return this.methodFalseReturn;
/*     */   }
/*     */   
/*     */   public void setMethodFalseReturn(boolean methodFalseReturn)
/*     */   {
/* 147 */     this.methodFalseReturn = methodFalseReturn;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\degrade\ServiceDegrade.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */