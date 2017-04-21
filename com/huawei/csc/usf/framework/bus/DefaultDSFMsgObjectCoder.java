/*    */ package com.huawei.csc.usf.framework.bus;
/*    */ 
/*    */ import com.huawei.csc.remoting.common.buf.ProtoBuf;
/*    */ import com.huawei.csc.usf.framework.util.USFCtxObject;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DefaultDSFMsgObjectCoder
/*    */   implements DSFObjectCoder
/*    */ {
/*    */   public void encodeCtxObject(ProtoBuf buf, Map<String, USFCtxObject> attachment, MsgSerializer serializer)
/*    */     throws Exception
/*    */   {
/* 21 */     MsgCoderUtils.writeCtxObjectMap(buf, attachment, serializer);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Map<String, USFCtxObject> decodeCtxObject(ProtoBuf buf, MsgSerializer serializer)
/*    */   {
/* 28 */     return MsgCoderUtils.readCtxObjectMap(buf, serializer);
/*    */   }
/*    */   
/*    */ 
/*    */   public void encodeException(ProtoBuf buf, Object exception, MsgSerializer serializer)
/*    */     throws Exception
/*    */   {
/* 35 */     MsgCoderUtils.writeObj(buf, exception, serializer);
/*    */   }
/*    */   
/*    */ 
/*    */   public Object decodeException(ProtoBuf buf, MsgSerializer serializer)
/*    */   {
/* 41 */     return MsgCoderUtils.readObj(buf, serializer);
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\bus\DefaultDSFMsgObjectCoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */