/*    */ package com.huawei.csc.usf.framework.routing.filter;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.Context;
/*    */ import com.huawei.csc.usf.framework.IMessage;
/*    */ import com.huawei.csc.usf.framework.MessageHeaders;
/*    */ import com.huawei.csc.usf.framework.sr.ServiceInstanceInner;
/*    */ import com.huawei.csc.usf.framework.util.Utils;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ResendAddressFilter
/*    */   implements RoutingServiceInstanceFilter
/*    */ {
/*    */   public void filter(List<ServiceInstanceInner> instances, Context context)
/*    */   {
/* 31 */     IMessage receivedMessage = context.getReceivedMessage();
/* 32 */     MessageHeaders headers = receivedMessage.getHeaders();
/* 33 */     String lastRequestDestAddress = headers.getDestAddr();
/* 34 */     if ((!context.getIsReSend()) || (Utils.isEmpty(lastRequestDestAddress)) || (1 >= instances.size()))
/*    */     {
/*    */ 
/* 37 */       return;
/*    */     }
/*    */     
/*    */ 
/* 41 */     Iterator<ServiceInstanceInner> iterator = instances.iterator();
/* 42 */     while (iterator.hasNext())
/*    */     {
/* 44 */       ServiceInstanceInner instance = (ServiceInstanceInner)iterator.next();
/* 45 */       if (lastRequestDestAddress.equals(instance.getAddress()))
/*    */       {
/* 47 */         iterator.remove();
/* 48 */         break;
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public String getFilterName()
/*    */   {
/* 56 */     return "resend address filter";
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\filter\ResendAddressFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */