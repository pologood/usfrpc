/*     */ package com.huawei.csc.usf.framework.bus;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.remoting.common.EncodeInfo;
/*     */ import com.huawei.csc.remoting.common.FrameworkMessage;
/*     */ import com.huawei.csc.remoting.common.MessageType;
/*     */ import com.huawei.csc.remoting.common.buf.ProtoBuf;
/*     */ import com.huawei.csc.remoting.common.buf.ProtoBufAllocator;
/*     */ import com.huawei.csc.remoting.common.decode.DecodeInfo;
/*     */ import com.huawei.csc.remoting.common.decode.MessageDecoder;
/*     */ import com.huawei.csc.remoting.common.encode.MessageEncoder;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.IMessageFactory;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import com.huawei.csc.usf.framework.message.MessageFactoryImpl;
/*     */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*     */ import com.huawei.csc.usf.framework.statistic.BigMessageStatisticCenter;
/*     */ import com.huawei.csc.usf.framework.statistic.BigMessageStatisticInfo;
/*     */ import com.huawei.csc.usf.framework.util.USFCtxObject;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class MsgCoder
/*     */   implements MessageEncoder, MessageDecoder
/*     */ {
/*  27 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(MsgCoder.class);
/*     */   
/*     */ 
/*     */   protected IMessageFactory messageFactory;
/*     */   
/*     */ 
/*     */   protected MsgSerializerFactory serializerFactory;
/*     */   
/*     */   private static final int MAGIC = -15;
/*     */   
/*  37 */   protected MsgSerializer protoStuffSerializer = new ProtoStuffMsgSerializerImpl();
/*     */   
/*     */   protected ServiceEngine serviceEngine;
/*     */   
/*     */   protected ServiceType serviceType;
/*     */   
/*     */   public static final int HEART_BEAT = 2;
/*     */   
/*     */   public static final int BIND_REQ = 3;
/*     */   
/*     */   public static final int BIND_RSP = 4;
/*     */   
/*     */   public MsgCoder(ServiceEngine serviceEngine, ServiceType serviceType)
/*     */   {
/*  51 */     this.serviceType = serviceType;
/*  52 */     this.serviceEngine = serviceEngine;
/*  53 */     this.messageFactory = serviceEngine.getMessageFactory(serviceType);
/*  54 */     this.serializerFactory = serviceEngine.getSerializerFactory();
/*     */   }
/*     */   
/*     */ 
/*     */   public MsgCoder(MessageFactoryImpl messageFactoryImpl, MsgSerializerFactory msgSerializerFactory)
/*     */   {
/*  60 */     this.messageFactory = messageFactoryImpl;
/*  61 */     this.serializerFactory = msgSerializerFactory;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public EncodeInfo encode(FrameworkMessage frameworkMessage)
/*     */     throws Exception
/*     */   {
/*  70 */     EncodeInfo encodeInfo = new EncodeInfo();
/*  71 */     Object message = frameworkMessage.getMessage();
/*  72 */     if ((message instanceof ProtoBuf))
/*     */     {
/*  74 */       encodeInfo.setEncodedBuf((ProtoBuf)message);
/*  75 */       return encodeInfo;
/*     */     }
/*     */     
/*  78 */     IMessage msg = (IMessage)frameworkMessage.getMessage();
/*     */     
/*  80 */     if ((msg.getHeaders().isType("request")) || (msg.getHeaders().isType("reply")))
/*     */     {
/*     */ 
/*  83 */       int requestId = (int)msg.getHeaders().getRequestId();
/*  84 */       String service = msg.getHeaders().getServiceName();
/*  85 */       String operation = msg.getHeaders().getOperation();
/*  86 */       String protocol = msg.getHeaders().getPolicyProtocol();
/*  87 */       String srcAddr = msg.getHeaders().getSrcAddr();
/*  88 */       String dstAddr = msg.getHeaders().getDestAddr();
/*  89 */       Map<String, USFCtxObject> attach = msg.getHeaders().getAttachment();
/*  90 */       ProtoBuf buf = ProtoBufAllocator.buffer();
/*  91 */       buf.writeInt(-15);
/*     */       
/*  93 */       buf.writerIndex(8);
/*     */       
/*  95 */       buf.writeInt(msg.getHeaders().isType("request") ? 0 : 1);
/*     */       
/*  97 */       buf.writeInt(requestId);
/*     */       
/*  99 */       MsgCoderUtils.writeString(buf, service);
/* 100 */       MsgCoderUtils.writeString(buf, operation);
/* 101 */       MsgCoderUtils.writeString(buf, protocol);
/* 102 */       MsgCoderUtils.writeString(buf, dstAddr);
/* 103 */       MsgCoderUtils.writeString(buf, srcAddr);
/* 104 */       MsgCoderUtils.writeCtxObjectMap(buf, attach, this.protoStuffSerializer);
/*     */       
/* 106 */       boolean isServer = true;
/* 107 */       if (msg.getHeaders().isType("request"))
/*     */       {
/* 109 */         MsgCoderUtils.writeObjectArray(buf, (Object[])msg.getPayload(), this.protoStuffSerializer);
/*     */         
/* 111 */         msg.getHeaders().getAttachment().put("reqSize", new USFCtxObject(Integer.valueOf(buf.writerIndex())));
/*     */         
/*     */ 
/*     */ 
/* 115 */         isServer = false;
/*     */       }
/*     */       else
/*     */       {
/* 119 */         MsgCoderUtils.writeObj(buf, msg.getPayload(), this.protoStuffSerializer);
/*     */       }
/*     */       
/* 122 */       putBigMessageStatistic(requestId, service, operation, msg.getPayload(), buf.writerIndex(), isServer);
/*     */       
/*     */ 
/* 125 */       int len = buf.readableBytes();
/*     */       
/* 127 */       buf.writerIndex(4);
/* 128 */       buf.writeInt(len - 4);
/* 129 */       buf.writerIndex(len);
/*     */       
/* 131 */       encodeInfo.setEncodedBuf(buf);
/* 132 */       return encodeInfo;
/*     */     }
/* 134 */     if (msg.getHeaders().isType("heartBeat"))
/*     */     {
/* 136 */       ProtoBuf buffer = ProtoBufAllocator.buffer(12);
/* 137 */       buffer.writeInt(-15);
/* 138 */       buffer.writeInt(8);
/* 139 */       buffer.writeInt(2);
/*     */       
/* 141 */       encodeInfo.setEncodedBuf(buffer);
/* 142 */       return encodeInfo;
/*     */     }
/* 144 */     if (msg.getHeaders().isType("bindRequest"))
/*     */     {
/* 146 */       ProtoBuf buffer = ProtoBufAllocator.buffer();
/* 147 */       String payload = (String)msg.getPayload();
/* 148 */       String version = msg.getHeaders().getAttachValue("bindVersion");
/*     */       
/* 150 */       buffer.writeInt(-15);
/* 151 */       buffer.writeInt(8);
/* 152 */       buffer.writeInt(3);
/* 153 */       buffer.writeInt(Integer.parseInt(frameworkMessage.getSequenceID()));
/* 154 */       MsgCoderUtils.writeString(buffer, payload);
/* 155 */       MsgCoderUtils.writeString(buffer, version);
/* 156 */       int len = buffer.readableBytes();
/* 157 */       buffer.writerIndex(4);
/* 158 */       buffer.writeInt(len - 4);
/* 159 */       buffer.writerIndex(len);
/* 160 */       encodeInfo.setEncodedBuf(buffer);
/* 161 */       return encodeInfo;
/*     */     }
/*     */     
/* 164 */     if (msg.getHeaders().isType("bindResponse"))
/*     */     {
/* 166 */       ProtoBuf buffer = ProtoBufAllocator.buffer();
/* 167 */       Object payload = msg.getPayload();
/* 168 */       buffer.writeInt(-15);
/* 169 */       buffer.writeInt(6);
/* 170 */       buffer.writeInt(4);
/* 171 */       buffer.writeInt(Integer.parseInt(frameworkMessage.getSequenceID()));
/* 172 */       if (msg.getHeaders().getAttachValue("bindVersion").equals("bindVersionV1"))
/*     */       {
/*     */ 
/* 175 */         MsgCoderUtils.writeString(buffer, (String)payload);
/*     */       }
/*     */       else
/*     */       {
/* 179 */         MsgCoderUtils.writeObj(buffer, payload, this.protoStuffSerializer);
/*     */       }
/* 181 */       int len = buffer.readableBytes();
/* 182 */       buffer.writerIndex(4);
/* 183 */       buffer.writeInt(len - 4);
/* 184 */       buffer.writerIndex(len);
/* 185 */       encodeInfo.setEncodedBuf(buffer);
/* 186 */       return encodeInfo;
/*     */     }
/* 188 */     return null;
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
/*     */ 
/*     */ 
/*     */   protected <T> void putBigMessageStatistic(long requestId, String service, String operation, T params, int messageSize, boolean isServer)
/*     */   {
/* 206 */     BigMessageStatisticInfo bigMessageStatistic = new BigMessageStatisticInfo(requestId, service, operation, messageSize);
/*     */     
/* 208 */     bigMessageStatistic.setParams(params);
/* 209 */     bigMessageStatistic.setServer(isServer);
/* 210 */     bigMessageStatistic.setServiceType(this.serviceType);
/* 211 */     this.serviceEngine.getBigMessageStatistisCenter().logBigMessageStatisticInfo(bigMessageStatistic);
/*     */   }
/*     */   
/*     */ 
/*     */   public DecodeInfo decode(ProtoBuf buf)
/*     */     throws Exception
/*     */   {
/* 218 */     IMessage msg = this.messageFactory.createMessage();
/* 219 */     DecodeInfo decodeInfo = new DecodeInfo();
/* 220 */     decodeInfo.setMessageType(MessageType.HEARTBEAT_REQUEST);
/* 221 */     decodeInfo.setMessage(msg);
/*     */     
/* 223 */     int messageSize = buf.readableBytes();
/* 224 */     int magic = buf.readInt();
/* 225 */     if (-15 != magic)
/*     */     {
/* 227 */       throw new RuntimeException("the message is invalid");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 232 */     buf.readInt();
/* 233 */     int type = buf.readInt();
/* 234 */     if (type == 0)
/*     */     {
/* 236 */       msg.getHeaders().setType("request");
/* 237 */       decodeInfo.setMessageType(MessageType.REQUEST);
/*     */     }
/* 239 */     else if (type == 1)
/*     */     {
/* 241 */       msg.getHeaders().setType("reply");
/* 242 */       decodeInfo.setMessageType(MessageType.RESPONSE);
/*     */     }
/*     */     else {
/* 245 */       if (type == 3)
/*     */       {
/* 247 */         msg.getHeaders().setType("bindRequest");
/* 248 */         int seqId = buf.readInt();
/* 249 */         decodeInfo.setSequenceID(String.valueOf(seqId));
/* 250 */         decodeInfo.setMessageType(MessageType.BIND_REQUEST);
/* 251 */         msg.getHeaders().setRequestId(seqId);
/* 252 */         String payload = MsgCoderUtils.readString(buf);
/* 253 */         msg.setPayload(payload);
/* 254 */         if (buf.readableBytes() > 0)
/*     */         {
/* 256 */           String version = MsgCoderUtils.readString(buf);
/* 257 */           msg.getHeaders().setAttachValue("bindVersion", version);
/*     */         }
/*     */         
/* 260 */         decodeInfo.setMessage(msg);
/* 261 */         return decodeInfo;
/*     */       }
/* 263 */       if (type == 4)
/*     */       {
/* 265 */         msg.getHeaders().setType("bindResponse");
/* 266 */         int seqId = buf.readInt();
/* 267 */         decodeInfo.setSequenceID(String.valueOf(seqId));
/* 268 */         msg.getHeaders().setRequestId(seqId);
/* 269 */         Object payload = readBindResponsePayload(buf);
/* 270 */         msg.setPayload(payload);
/* 271 */         decodeInfo.setMessageType(MessageType.BIND_RESPONSE);
/* 272 */         return decodeInfo;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 277 */       return decodeInfo;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 287 */     msg.getHeaders().setRequestId(buf.readInt());
/*     */     
/* 289 */     String service = MsgCoderUtils.readString(buf);
/*     */     
/* 291 */     msg.getHeaders().setServiceName(service);
/*     */     
/* 293 */     msg.getHeaders().setPolicyServiceName(service);
/*     */     
/* 295 */     String operation = MsgCoderUtils.readString(buf);
/*     */     
/* 297 */     msg.getHeaders().setOperation(operation);
/*     */     
/* 299 */     msg.getHeaders().setPolicyOperation(operation);
/*     */     
/* 301 */     String protocol = MsgCoderUtils.readString(buf);
/*     */     
/* 303 */     msg.getHeaders().setPolicyProtocol(protocol);
/*     */     
/* 305 */     String dstAddr = MsgCoderUtils.readString(buf);
/*     */     
/* 307 */     msg.getHeaders().setDestAddr(dstAddr);
/*     */     
/* 309 */     String srcAddr = MsgCoderUtils.readString(buf);
/*     */     
/* 311 */     msg.getHeaders().setSrcAddr(srcAddr);
/*     */     
/* 313 */     Map<String, USFCtxObject> attachment = MsgCoderUtils.readCtxObjectMap(buf, this.protoStuffSerializer);
/*     */     
/*     */ 
/* 316 */     if (attachment != null)
/*     */     {
/* 318 */       msg.getHeaders().setAttachment(attachment);
/*     */     }
/*     */     
/*     */ 
/* 322 */     if (msg.getHeaders().isType("request"))
/*     */     {
/* 324 */       msg.setPayload(MsgCoderUtils.readObjectArray(buf, this.protoStuffSerializer));
/*     */       
/*     */ 
/* 327 */       msg.getHeaders().getAttachment().put("reqSize", new USFCtxObject(Integer.valueOf(messageSize)));
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 332 */       msg.setPayload(MsgCoderUtils.readObj(buf, this.protoStuffSerializer));
/*     */       
/* 334 */       msg.getHeaders().getAttachment().put("rspSize", new USFCtxObject(Integer.valueOf(messageSize)));
/*     */     }
/*     */     
/*     */ 
/* 338 */     return decodeInfo;
/*     */   }
/*     */   
/*     */   protected Object readBindResponsePayload(ProtoBuf buf)
/*     */   {
/* 343 */     int id = buf.readInt();
/* 344 */     if ((id != -1) && (id != 1) && (id != 4))
/*     */     {
/*     */ 
/* 347 */       byte[] contentBytes = new byte[id];
/* 348 */       buf.readBytes(contentBytes);
/* 349 */       return new String(contentBytes, MsgCoderUtils.UTF8Charset);
/*     */     }
/*     */     
/*     */ 
/* 353 */     if (id != -1)
/*     */     {
/* 355 */       if (id == 1)
/*     */       {
/* 357 */         return MsgCoderUtils.readString(buf);
/*     */       }
/*     */       
/* 360 */       if (id == 4)
/*     */       {
/* 362 */         return MsgCoderUtils.readBytes(buf);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 367 */     return null;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\bus\MsgCoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */