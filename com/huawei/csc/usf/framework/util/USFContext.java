/*     */ package com.huawei.csc.usf.framework.util;
/*     */ 
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ public class USFContext
/*     */ {
/*  11 */   private Map<String, USFCtxObject> attributes = new HashMap();
/*     */   
/*  13 */   private static ThreadLocal<USFContext> contextLocal = new ThreadLocal()
/*     */   {
/*     */     protected USFContext initialValue()
/*     */     {
/*  17 */       return new USFContext();
/*     */     }
/*     */   };
/*     */   
/*     */   public static USFContext getContext()
/*     */   {
/*  23 */     return (USFContext)contextLocal.get();
/*     */   }
/*     */   
/*     */   public static void remove()
/*     */   {
/*  28 */     contextLocal.remove();
/*     */   }
/*     */   
/*     */   public void setAttribute(String key, String value)
/*     */   {
/*  33 */     this.attributes.put(key, new USFCtxObject(value));
/*     */   }
/*     */   
/*     */   public USFCtxObject getAttribute(String key)
/*     */   {
/*  38 */     return (USFCtxObject)this.attributes.get(key);
/*     */   }
/*     */   
/*     */   public Object getAttributeValue(String key)
/*     */   {
/*  43 */     USFCtxObject usfCtxObject = (USFCtxObject)this.attributes.get(key);
/*  44 */     if (null != usfCtxObject)
/*     */     {
/*  46 */       return usfCtxObject.getValue();
/*     */     }
/*     */     
/*     */ 
/*  50 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void clear()
/*     */   {
/*  56 */     this.attributes.clear();
/*     */   }
/*     */   
/*     */   public Map<String, USFCtxObject> getAttributes()
/*     */   {
/*  61 */     return this.attributes;
/*     */   }
/*     */   
/*     */   public void setAttribute(String key, Object value)
/*     */   {
/*  66 */     if ((value instanceof USFCtxObject))
/*     */     {
/*  68 */       this.attributes.put(key, (USFCtxObject)value);
/*     */     }
/*     */     else
/*     */     {
/*  72 */       this.attributes.put(key, new USFCtxObject(value));
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeAttribute(String key)
/*     */   {
/*  78 */     if (getContext().getAttributes().containsKey(key))
/*     */     {
/*  80 */       getContext().getAttributes().remove(key);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setAttribute(String key, Object value, ImplicitArgsType aatType)
/*     */   {
/*  86 */     if ((value instanceof USFCtxObject))
/*     */     {
/*  88 */       USFCtxObject dsfObject = (USFCtxObject)value;
/*  89 */       this.attributes.put(key, new USFCtxObject(dsfObject.getValue(), aatType));
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*  94 */       this.attributes.put(key, new USFCtxObject(value, aatType));
/*     */     }
/*     */   }
/*     */   
/*     */   public void usfCtx2RpcCtx(IMessage msg)
/*     */   {
/* 100 */     if (this.attributes.isEmpty())
/*     */     {
/* 102 */       return;
/*     */     }
/*     */     
/*     */ 
/* 106 */     Map<String, USFCtxObject> msgAttachment = msg.getHeaders().getAttachment();
/*     */     
/* 108 */     Map<String, USFCtxObject> localManualAttributes = new HashMap();
/*     */     
/* 110 */     for (Map.Entry<String, USFCtxObject> element : this.attributes.entrySet())
/*     */     {
/*     */ 
/* 113 */       if (((USFCtxObject)element.getValue()).getAttType() == ImplicitArgsType.REMOTE)
/*     */       {
/* 115 */         msgAttachment.put(element.getKey(), element.getValue());
/*     */       }
/* 117 */       else if (((USFCtxObject)element.getValue()).getAttType() == ImplicitArgsType.LOCAL_MANUAL)
/*     */       {
/* 119 */         localManualAttributes.put(element.getKey(), element.getValue());
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/* 124 */       else if (msgAttachment.containsKey(element.getKey()))
/*     */       {
/* 126 */         msgAttachment.remove(element.getKey());
/*     */       }
/*     */     }
/*     */     
/* 130 */     this.attributes.clear();
/* 131 */     this.attributes.putAll(localManualAttributes);
/* 132 */     localManualAttributes.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void rpcCtx2UsfCtx(IMessage msg)
/*     */   {
/* 139 */     if (null == msg)
/*     */     {
/* 141 */       return;
/*     */     }
/* 143 */     Map<String, USFCtxObject> attachments = msg.getHeaders().getAttachment();
/*     */     
/* 145 */     if ((attachments == null) || (attachments.isEmpty()))
/*     */     {
/* 147 */       return;
/*     */     }
/*     */     
/* 150 */     for (Map.Entry<String, USFCtxObject> element : attachments.entrySet())
/*     */     {
/* 152 */       if (((USFCtxObject)element.getValue()).getAttType() == ImplicitArgsType.REMOTE)
/*     */       {
/* 154 */         setAttribute((String)element.getKey(), element.getValue(), ((USFCtxObject)element.getValue()).getAttType());
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\util\USFContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */