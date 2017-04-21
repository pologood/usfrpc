/*    */ package com.huawei.csc.usf.framework.sr;
/*    */ 
/*    */ public enum ServiceType
/*    */ {
/*  5 */   USF(0), 
/*    */   
/*  7 */   DSF(1), 
/*    */   
/*  9 */   EBUS(2);
/*    */   
/* 11 */   private int value = 0;
/*    */   
/*    */   private ServiceType(int value)
/*    */   {
/* 15 */     this.value = value;
/*    */   }
/*    */   
/*    */   public final int toNumber()
/*    */   {
/* 20 */     return this.value;
/*    */   }
/*    */   
/*    */   public static int size()
/*    */   {
/* 25 */     return 4;
/*    */   }
/*    */   
/*    */   public static ServiceType valueOf(int value)
/*    */   {
/* 30 */     switch (value)
/*    */     {
/*    */     case 0: 
/* 33 */       return USF;
/*    */     case 1: 
/* 35 */       return DSF;
/*    */     case 2: 
/* 37 */       return EBUS;
/*    */     }
/* 39 */     return null;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\ServiceType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */