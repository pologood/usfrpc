/*     */ package com.huawei.csc.usf.framework.event;
/*     */ 
/*     */ import com.huawei.csc.remoting.common.util.CastUtil;
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
/*     */ public class ServiceFrameworkEvent
/*     */ {
/*  29 */   private String name = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  34 */   private String type = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  39 */   private Map<String, Object> payload = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final ServiceFrameworkEvent createEvent()
/*     */   {
/*  48 */     return new ServiceFrameworkEvent();
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
/*     */   public void put(String key, Object object)
/*     */   {
/*  61 */     if (null == object)
/*     */     {
/*  63 */       return;
/*     */     }
/*  65 */     this.payload.put(key, object);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public <T> T get(String key)
/*     */   {
/*  77 */     return (T)CastUtil.cast(this.payload.get(key));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void clear()
/*     */   {
/*  85 */     this.payload.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/*  95 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/* 106 */     this.name = name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getType()
/*     */   {
/* 116 */     return this.type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setType(String type)
/*     */   {
/* 127 */     this.type = type;
/*     */   }
/*     */   
/*     */   public Map<String, Object> getSource()
/*     */   {
/* 132 */     return this.payload;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\event\ServiceFrameworkEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */