/*    */ package com.huawei.csc.usf.framework.routing.filter;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.Context;
/*    */ import com.huawei.csc.usf.framework.GroupNameInterpret;
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
/*    */ 
/*    */ 
/*    */ public class GroupNameFilter
/*    */   implements RoutingServiceInstanceFilter
/*    */ {
/*    */   private static final String DEFAULT_GROUP = "default";
/*    */   
/*    */   public void filter(List<ServiceInstanceInner> instances, Context context)
/*    */   {
/* 36 */     IMessage receivedMessage = context.getReceivedMessage();
/*    */     
/* 38 */     GroupNameInterpret interpret = receivedMessage.getGroupNameInterpret();
/* 39 */     if (null == interpret)
/*    */     {
/* 41 */       interpret = GroupNameInterpret.Compare;
/*    */     }
/*    */     
/*    */ 
/* 45 */     if (GroupNameInterpret.NoEffect == interpret)
/*    */     {
/* 47 */       return;
/*    */     }
/*    */     
/*    */ 
/* 51 */     String requestGroupNames = receivedMessage.getHeaders().getGroup();
/*    */     
/*    */ 
/* 54 */     List<String> groups = Utils.resolveRefGroups(requestGroupNames, context);
/*    */     
/*    */ 
/* 57 */     if ((GroupNameInterpret.DefaultConsumeAllGroup == interpret) && (groups.contains("default")))
/*    */     {
/*    */ 
/* 60 */       return;
/*    */     }
/*    */     
/*    */ 
/* 64 */     if ((GroupNameInterpret.DefaultConsumeAllGroup == interpret) || (GroupNameInterpret.Compare == interpret))
/*    */     {
/*    */ 
/* 67 */       Iterator<ServiceInstanceInner> iterator = instances.iterator();
/* 68 */       while (iterator.hasNext())
/*    */       {
/* 70 */         ServiceInstanceInner instance = (ServiceInstanceInner)iterator.next();
/*    */         
/* 72 */         instance = Utils.parseServiceByConfiguration(instance, context);
/*    */         
/* 74 */         if (!groups.contains(instance.getGroup()))
/*    */         {
/* 76 */           iterator.remove();
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public String getFilterName()
/*    */   {
/* 85 */     return "group name filter";
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\routing\filter\GroupNameFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */