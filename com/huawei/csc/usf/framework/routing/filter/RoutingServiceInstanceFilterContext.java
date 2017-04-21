/*     */ package com.huawei.csc.usf.framework.routing.filter;
/*     */ 
/*     */ import com.huawei.csc.usf.framework.util.Utils;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public class RoutingServiceInstanceFilterContext
/*     */ {
/*     */   public static final int SERVICE_INSTANCE_SIZE_MIN_VALUE = 0;
/*     */   private static final int SERVICE_INSTANCE_SIZE_NULL_VALUE = -1;
/*  36 */   private int cacheServiceInstanceSize = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  41 */   private Map<String, Integer> afterFilterServiceInstanceSizes = new HashMap();
/*     */   
/*     */   public int getCacheServiceInstanceSize()
/*     */   {
/*  45 */     return this.cacheServiceInstanceSize;
/*     */   }
/*     */   
/*     */   public void setCacheServiceInstanceSize(int cacheServiceInstanceSize)
/*     */   {
/*  50 */     this.cacheServiceInstanceSize = cacheServiceInstanceSize;
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
/*     */   public int getAfterFilterServiceInstanceSize(Class<?> filterClass)
/*     */   {
/*  64 */     if (null == filterClass)
/*     */     {
/*  66 */       return -1;
/*     */     }
/*  68 */     String filterClassName = filterClass.getName();
/*  69 */     if (Utils.isEmpty(filterClassName))
/*     */     {
/*  71 */       return -1;
/*     */     }
/*     */     
/*     */ 
/*  75 */     Integer size = (Integer)this.afterFilterServiceInstanceSizes.get(filterClassName);
/*     */     
/*     */ 
/*  78 */     if (null == size)
/*     */     {
/*  80 */       return -1;
/*     */     }
/*     */     
/*  83 */     int sizeIntValue = size.intValue();
/*  84 */     if (sizeIntValue < 0)
/*     */     {
/*  86 */       sizeIntValue = 0;
/*     */     }
/*     */     
/*  89 */     return sizeIntValue;
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
/*     */   public void setAfterFilterServiceInstanceSize(RoutingServiceInstanceFilter filter, int size)
/*     */   {
/* 105 */     if (null == filter)
/*     */     {
/* 107 */       return;
/*     */     }
/* 109 */     String filterClassName = filter.getClass().getName();
/* 110 */     if (Utils.isEmpty(filterClassName)) {
/*     */       return;
/*     */     }
/*     */     
/*     */     int sizeToPut;
/*     */     
/*     */     int sizeToPut;
/* 117 */     if (size < 0)
/*     */     {
/* 119 */       sizeToPut = 0;
/*     */     }
/*     */     else
/*     */     {
/* 123 */       sizeToPut = size;
/*     */     }
/*     */     
/*     */ 
/* 127 */     this.afterFilterServiceInstanceSizes.put(filterClassName, Integer.valueOf(sizeToPut));
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\filter\RoutingServiceInstanceFilterContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */