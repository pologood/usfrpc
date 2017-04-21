/*     */ package com.huawei.csc.usf.framework.bus;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.remoting.common.buf.ProtoBuf;
/*     */ import com.huawei.csc.usf.framework.util.ImplicitArgsType;
/*     */ import com.huawei.csc.usf.framework.util.USFCtxObject;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ public class MsgCoderUtils
/*     */ {
/*  15 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(MsgCoderUtils.class);
/*     */   
/*     */ 
/*     */   protected static final int NULL_ID = -1;
/*     */   
/*     */   protected static final int STRING_ID = 1;
/*     */   
/*     */   protected static final int OBJECT_ID = 2;
/*     */   
/*     */   protected static final int IMPLICIT_OBJECT_ID = 3;
/*     */   
/*     */   protected static final int BYTE_ID = 4;
/*     */   
/*  28 */   public static final Charset UTF8Charset = Charset.forName("UTF-8");
/*     */   
/*     */   public static void writeString(ProtoBuf buf, String content)
/*     */   {
/*  32 */     if (content == null)
/*     */     {
/*  34 */       buf.writeInt(-1);
/*  35 */       return;
/*     */     }
/*  37 */     byte[] contentBytes = content.getBytes(UTF8Charset);
/*  38 */     buf.writeInt(contentBytes.length);
/*     */     
/*  40 */     buf.writeBytes(contentBytes);
/*     */   }
/*     */   
/*     */   public static String readString(ProtoBuf buf)
/*     */   {
/*  45 */     int length = buf.readInt();
/*  46 */     if (length == -1)
/*     */     {
/*  48 */       return null;
/*     */     }
/*  50 */     byte[] contentBytes = new byte[length];
/*  51 */     buf.readBytes(contentBytes);
/*  52 */     return new String(contentBytes, UTF8Charset);
/*     */   }
/*     */   
/*     */   public static void writeBytes(ProtoBuf buf, byte[] bytes)
/*     */   {
/*  57 */     if (bytes == null)
/*     */     {
/*  59 */       buf.writeInt(-1);
/*  60 */       return;
/*     */     }
/*  62 */     buf.writeInt(bytes.length);
/*  63 */     buf.writeBytes(bytes);
/*     */   }
/*     */   
/*     */   public static byte[] readBytes(ProtoBuf buf)
/*     */   {
/*  68 */     int length = buf.readInt();
/*  69 */     if (length == -1)
/*     */     {
/*  71 */       return null;
/*     */     }
/*  73 */     byte[] result = new byte[length];
/*  74 */     buf.readBytes(result);
/*  75 */     return result;
/*     */   }
/*     */   
/*     */   public static void writeObj(ProtoBuf buf, Object obj, MsgSerializer serializer)
/*     */     throws Exception
/*     */   {
/*  81 */     if (obj == null)
/*     */     {
/*  83 */       buf.writeInt(-1);
/*  84 */       return;
/*     */     }
/*  86 */     if ((obj instanceof String))
/*     */     {
/*  88 */       buf.writeInt(1);
/*  89 */       writeString(buf, (String)obj);
/*  90 */       return;
/*     */     }
/*     */     
/*  93 */     if ((obj instanceof byte[]))
/*     */     {
/*  95 */       buf.writeInt(4);
/*  96 */       writeBytes(buf, (byte[])obj);
/*  97 */       return;
/*     */     }
/*     */     
/* 100 */     if ((obj instanceof USFCtxObject))
/*     */     {
/* 102 */       buf.writeInt(3);
/* 103 */       writeImplicitObject(buf, (USFCtxObject)obj, serializer);
/* 104 */       return;
/*     */     }
/* 106 */     buf.writeInt(2);
/* 107 */     byte[] objBytes = serializer.serialize(obj);
/* 108 */     buf.writeInt(objBytes.length);
/* 109 */     buf.writeBytes(objBytes);
/*     */   }
/*     */   
/*     */   public static Object readObj(ProtoBuf buf, MsgSerializer serializer)
/*     */   {
/* 114 */     int id = buf.readInt();
/* 115 */     if (id == -1)
/*     */     {
/* 117 */       return null;
/*     */     }
/* 119 */     if (id == 1)
/*     */     {
/* 121 */       return readString(buf);
/*     */     }
/* 123 */     if (id == 4)
/*     */     {
/* 125 */       return readBytes(buf);
/*     */     }
/* 127 */     if (id == 3)
/*     */     {
/* 129 */       return readImplicitObject(buf, serializer);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 134 */     int length = buf.readInt();
/* 135 */     byte[] objBytes = new byte[length];
/* 136 */     buf.readBytes(objBytes);
/* 137 */     Object obj = null;
/*     */     try
/*     */     {
/* 140 */       obj = serializer.deserialize(objBytes);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 144 */       if (LOGGER.isErrorEnable())
/*     */       {
/* 146 */         LOGGER.error("deserialize exception: " + e.getMessage());
/*     */       }
/*     */     }
/* 149 */     return obj;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void writeCtxObjectMap(ProtoBuf buf, Map<String, USFCtxObject> map, MsgSerializer serializer)
/*     */     throws Exception
/*     */   {
/* 157 */     if (map == null)
/*     */     {
/* 159 */       buf.writeInt(-1);
/* 160 */       return;
/*     */     }
/* 162 */     int size = map.size();
/* 163 */     buf.writeInt(size);
/*     */     
/* 165 */     for (Map.Entry<String, USFCtxObject> entry : map.entrySet())
/*     */     {
/* 167 */       writeString(buf, (String)entry.getKey());
/* 168 */       writeObj(buf, entry.getValue(), serializer);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void writeObjectMap(ProtoBuf buf, Map<String, USFCtxObject> map, MsgSerializer serializer)
/*     */     throws Exception
/*     */   {
/* 176 */     if (map == null)
/*     */     {
/* 178 */       buf.writeInt(-1);
/* 179 */       return;
/*     */     }
/* 181 */     int size = map.size();
/* 182 */     buf.writeInt(size);
/*     */     
/* 184 */     for (Map.Entry<String, USFCtxObject> entry : map.entrySet())
/*     */     {
/* 186 */       writeString(buf, (String)entry.getKey());
/* 187 */       if (entry.getValue() != null)
/*     */       {
/* 189 */         writeObj(buf, ((USFCtxObject)entry.getValue()).getValue(), serializer);
/*     */       }
/*     */       else
/*     */       {
/* 193 */         writeObj(buf, null, serializer);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static Map<String, USFCtxObject> readCtxObjectMap(ProtoBuf buf, MsgSerializer serializer)
/*     */   {
/* 201 */     int size = buf.readInt();
/* 202 */     if (size == -1)
/*     */     {
/* 204 */       return null;
/*     */     }
/* 206 */     Map<String, USFCtxObject> map = new HashMap(size);
/* 207 */     for (int i = 1; i <= size; i++)
/*     */     {
/* 209 */       String key = readString(buf);
/* 210 */       USFCtxObject value = (USFCtxObject)readObj(buf, serializer);
/* 211 */       map.put(key, value);
/*     */     }
/* 213 */     return map;
/*     */   }
/*     */   
/*     */ 
/*     */   public static Map<String, USFCtxObject> readObjectMap(ProtoBuf buf, MsgSerializer serializer)
/*     */   {
/* 219 */     int size = buf.readInt();
/* 220 */     if (size == -1)
/*     */     {
/* 222 */       return null;
/*     */     }
/* 224 */     Map<String, USFCtxObject> map = new HashMap(size);
/* 225 */     for (int i = 1; i <= size; i++)
/*     */     {
/* 227 */       String key = readString(buf);
/* 228 */       USFCtxObject value = readCtxObject(buf, serializer);
/* 229 */       map.put(key, value);
/*     */     }
/* 231 */     return map;
/*     */   }
/*     */   
/*     */ 
/*     */   private static USFCtxObject readCtxObject(ProtoBuf buf, MsgSerializer serializer)
/*     */   {
/* 237 */     String value = (String)readObj(buf, serializer);
/* 238 */     return new USFCtxObject(value);
/*     */   }
/*     */   
/*     */   private static void writeImplicitObject(ProtoBuf buf, USFCtxObject usfCtxobj, MsgSerializer serializer)
/*     */     throws Exception
/*     */   {
/* 244 */     int type = usfCtxobj.getAttType().ordinal();
/* 245 */     buf.writeInt(type);
/* 246 */     Object obj = usfCtxobj.getValue();
/* 247 */     writeObj(buf, obj, serializer);
/*     */   }
/*     */   
/*     */ 
/*     */   private static USFCtxObject readImplicitObject(ProtoBuf buf, MsgSerializer serializer)
/*     */   {
/* 253 */     int type = buf.readInt();
/* 254 */     ImplicitArgsType typeEm = ImplicitArgsType.values()[type];
/* 255 */     Object obj = readObj(buf, serializer);
/* 256 */     return new USFCtxObject(obj, typeEm);
/*     */   }
/*     */   
/*     */   public static void writeObjectArray(ProtoBuf buf, Object[] objArray, MsgSerializer serializer)
/*     */     throws Exception
/*     */   {
/* 262 */     if (objArray == null)
/*     */     {
/* 264 */       buf.writeInt(-1);
/* 265 */       return;
/*     */     }
/*     */     
/* 268 */     buf.writeInt(objArray.length);
/* 269 */     for (Object obj : objArray)
/*     */     {
/* 271 */       writeObj(buf, obj, serializer);
/*     */     }
/*     */   }
/*     */   
/*     */   public static Object readObjectArray(ProtoBuf buf, MsgSerializer serializer)
/*     */   {
/* 277 */     int size = buf.readInt();
/* 278 */     if (size == -1)
/*     */     {
/* 280 */       return null;
/*     */     }
/* 282 */     Object[] objArray = new Object[size];
/* 283 */     for (int i = 1; i <= size; i++)
/*     */     {
/* 285 */       objArray[(i - 1)] = readObj(buf, serializer);
/*     */     }
/* 287 */     return objArray;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\bus\MsgCoderUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */