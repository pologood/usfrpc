/*    */ package com.huawei.csc.usf.framework.bus;
/*    */ 
/*    */ import com.dyuproject.protostuff.runtime.Delegate;
/*    */ import com.huawei.soa.foundation.compress.Serializer;
/*    */ import com.huawei.soa.foundation.compress.impl.ProtostuffSerializer;
/*    */ import com.huawei.soa.foundation.compress.impl.SerializerFactoryImpl;
/*    */ 
/*    */ public class ProtoStuffMsgSerializerImpl implements MsgSerializer
/*    */ {
/* 10 */   private Serializer serializer = SerializerFactoryImpl.getInstance().createSerializer();
/*    */   
/*    */ 
/*    */   public ProtoStuffMsgSerializerImpl()
/*    */   {
/*    */     ProtostuffSerializer pbSerializer;
/* 16 */     if ((this.serializer instanceof ProtostuffSerializer))
/*    */     {
/* 18 */       pbSerializer = (ProtostuffSerializer)this.serializer;
/*    */       
/*    */ 
/* 21 */       pbSerializer.registerRuntimeSchemas(ProtoStuffMsgSerializerUtils.schemasMap);
/*    */       
/*    */ 
/* 24 */       for (Delegate<?> delegate : ProtoStuffMsgSerializerUtils.delegates)
/*    */       {
/* 26 */         pbSerializer.registerRuntimeDelegate(delegate);
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public byte[] serialize(Object obj)
/*    */   {
/* 34 */     return this.serializer.serialize(obj);
/*    */   }
/*    */   
/*    */ 
/*    */   public Object deserialize(byte[] bytes)
/*    */   {
/* 40 */     return this.serializer.deserialize(bytes, Object.class);
/*    */   }
/*    */   
/*    */ 
/*    */   public byte getSerializeTag()
/*    */   {
/* 46 */     return 0;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\bus\ProtoStuffMsgSerializerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */