/*     */ package com.huawei.csc.usf.framework.bus;
/*     */ 
/*     */ import com.huawei.csc.container.api.ContextRegistry;
/*     */ import com.huawei.csc.container.api.IContextHolder;
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.ExceptionUtils;
/*     */ import com.huawei.csc.usf.framework.ExceptionUtilsHolder;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.exception.USFException;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*     */ import com.huawei.csc.usf.framework.util.CopyOnWriteHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class MsgSerializerFactory
/*     */ {
/*  19 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(MsgSerializerFactory.class);
/*     */   
/*     */ 
/*     */ 
/*  23 */   private Map<String, MsgSerializer> serializerMap = new CopyOnWriteHashMap();
/*     */   
/*     */   private MsgSerializer defaultSerializer;
/*     */   
/*     */   private DSFObjectCoder dsfObjectCoder;
/*     */   
/*  29 */   private String configSerializeType = "protostuff";
/*     */   
/*     */   public static final String PROTOSTUFF = "protostuff";
/*     */   
/*     */   public static final String BSON = "bson";
/*     */   
/*     */   public static final String SERIALIZATION = "serialization";
/*     */   
/*     */   public static final byte PROTOSTUFF_TAG = 0;
/*     */   
/*     */   public static final byte BSON_TAG = 1;
/*     */   
/*     */   public MsgSerializerFactory()
/*     */   {
/*  43 */     this.defaultSerializer = new ProtoStuffMsgSerializerImpl();
/*     */   }
/*     */   
/*     */   public void init(ServiceEngine engine) throws Exception
/*     */   {
/*  48 */     this.configSerializeType = engine.getSystemConfig().getDsfRpcSerialization();
/*  49 */     Set<Byte> tagSet = new java.util.HashSet();
/*     */     
/*  51 */     Map<String, DSFObjectCoder> coderBeanMap = ContextRegistry.getContextHolder().getBeansOfType(DSFObjectCoder.class);
/*     */     
/*  53 */     for (Map.Entry<String, DSFObjectCoder> entry : coderBeanMap.entrySet())
/*     */     {
/*  55 */       DSFObjectCoder value = (DSFObjectCoder)entry.getValue();
/*  56 */       if (value != null)
/*     */       {
/*  58 */         this.dsfObjectCoder = value;
/*  59 */         break;
/*     */       }
/*     */     }
/*     */     
/*  63 */     if (this.dsfObjectCoder == null)
/*     */     {
/*  65 */       this.dsfObjectCoder = new DefaultDSFMsgObjectCoder();
/*     */     }
/*     */     
/*  68 */     if (LOGGER.isDebugEnable())
/*     */     {
/*  70 */       LOGGER.debug("set dsf object coder to : " + this.dsfObjectCoder);
/*     */     }
/*     */     
/*     */ 
/*  74 */     Map<String, MsgSerializer> beanMap = ContextRegistry.getContextHolder().getBeansOfType(MsgSerializer.class);
/*     */     
/*  76 */     for (Map.Entry<String, MsgSerializer> entry : beanMap.entrySet())
/*     */     {
/*  78 */       String serializeName = (String)entry.getKey();
/*  79 */       if (serializeName.equalsIgnoreCase("protostuff"))
/*     */       {
/*  81 */         throw new USFException("The name of serialization should not be \"protostuff\" .");
/*     */       }
/*     */       
/*     */ 
/*  85 */       MsgSerializer serializer = (MsgSerializer)entry.getValue();
/*  86 */       byte tag = serializer.getSerializeTag();
/*  87 */       Byte tagByte = Byte.valueOf(tag);
/*  88 */       if ((0 == tag) || (tagSet.contains(tagByte)))
/*     */       {
/*  90 */         throw new USFException("The tag of serialization should not be repeated, current tag is :" + tag);
/*     */       }
/*     */       
/*     */ 
/*  94 */       this.serializerMap.put(entry.getKey(), serializer);
/*  95 */       tagSet.add(tagByte);
/*     */     }
/*     */   }
/*     */   
/*     */   public MsgSerializer getMsgSerializer(byte tag) throws Exception
/*     */   {
/* 101 */     if (0 == tag)
/*     */     {
/* 103 */       return this.defaultSerializer;
/*     */     }
/*     */     
/*     */ 
/* 107 */     if (null != this.serializerMap)
/*     */     {
/* 109 */       for (Map.Entry<String, MsgSerializer> entry : this.serializerMap.entrySet())
/*     */       {
/*     */ 
/* 112 */         if (((MsgSerializer)entry.getValue()).getSerializeTag() == tag)
/* 113 */           return (MsgSerializer)entry.getValue();
/*     */       }
/*     */     }
/* 116 */     Exception e = new Exception("Cann't find the serialization of tag:" + tag);
/*     */     
/* 118 */     throw ExceptionUtilsHolder.getExceptionUtils(ServiceType.DSF).invalidArgument(e);
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getSerializeTag(String serializeType)
/*     */     throws Exception
/*     */   {
/* 125 */     if (serializeType.equals("protostuff"))
/*     */     {
/* 127 */       return 0;
/*     */     }
/* 129 */     if (serializeType.equals("bson"))
/*     */     {
/* 131 */       return 1;
/*     */     }
/* 133 */     if ((null != this.serializerMap) && (null != this.serializerMap.get(serializeType)))
/*     */     {
/* 135 */       return ((MsgSerializer)this.serializerMap.get(serializeType)).getSerializeTag();
/*     */     }
/* 137 */     Exception e = new Exception("Cann't find the serialization of type:" + serializeType);
/*     */     
/* 139 */     throw ExceptionUtilsHolder.getExceptionUtils(ServiceType.DSF).invalidArgument(e);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getConfigSerializeType()
/*     */   {
/* 145 */     return this.configSerializeType;
/*     */   }
/*     */   
/*     */   public DSFObjectCoder getDSFObjectCoder()
/*     */   {
/* 150 */     return this.dsfObjectCoder;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\bus\MsgSerializerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */