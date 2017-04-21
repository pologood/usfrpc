/*    */ package com.huawei.csc.usf.framework;
/*    */ 
/*    */ import com.huawei.csc.usf.framework.sr.ServiceType;
/*    */ import java.util.Map;
/*    */ import org.springframework.context.ApplicationContext;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MessageFactoryHolder
/*    */ {
/* 17 */   private IMessageFactory[] messageFactoryArray = new IMessageFactory[ServiceType.size()];
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public IMessageFactory getMessageFactory(ServiceType serviceType)
/*    */   {
/* 24 */     return this.messageFactoryArray[serviceType.toNumber()];
/*    */   }
/*    */   
/*    */ 
/*    */   public void init(ServiceEngine engine)
/*    */   {
/* 30 */     Map<String, IMessageFactory> messageFactoryBeans = engine.getApplicationContext().getBeansOfType(IMessageFactory.class);
/*    */     
/* 32 */     for (IMessageFactory messageFactory : messageFactoryBeans.values())
/*    */     {
/* 34 */       messageFactory.setSystemConfig(engine.getSystemConfig());
/*    */       
/*    */ 
/* 37 */       this.messageFactoryArray[messageFactory.getServiceType().toNumber()] = messageFactory;
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\MessageFactoryHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */