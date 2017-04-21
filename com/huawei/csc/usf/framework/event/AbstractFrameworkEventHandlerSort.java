/*    */ package com.huawei.csc.usf.framework.event;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AbstractFrameworkEventHandlerSort
/*    */ {
/* 11 */   private int index = Integer.MAX_VALUE;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setIndex(int index)
/*    */   {
/* 18 */     this.index = index;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public int getIndex()
/*    */   {
/* 26 */     return this.index;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 32 */   public static final Comparator<IServiceFrameworkEventHandler> comparator = new Comparator()
/*    */   {
/*    */ 
/*    */     public int compare(IServiceFrameworkEventHandler o1, IServiceFrameworkEventHandler o2)
/*    */     {
/*    */ 
/* 38 */       int index1 = Integer.MAX_VALUE;
/* 39 */       int index2 = Integer.MAX_VALUE;
/* 40 */       if ((o1 instanceof AbstractFrameworkEventHandlerSort))
/*    */       {
/* 42 */         index1 = ((AbstractFrameworkEventHandlerSort)o1).getIndex();
/*    */       }
/*    */       
/* 45 */       if ((o2 instanceof AbstractFrameworkEventHandlerSort))
/*    */       {
/* 47 */         index2 = ((AbstractFrameworkEventHandlerSort)o2).getIndex();
/*    */       }
/*    */       
/*    */ 
/* 51 */       return index1 - index2;
/*    */     }
/*    */   };
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\event\AbstractFrameworkEventHandlerSort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */