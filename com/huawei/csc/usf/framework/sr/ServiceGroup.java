/*    */ package com.huawei.csc.usf.framework.sr;
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
/*    */ public class ServiceGroup
/*    */ {
/* 20 */   private String groupName = "default";
/*    */   
/* 22 */   private String refGroup = "";
/*    */   
/*    */   public ServiceGroup(String groupName)
/*    */   {
/* 26 */     this.groupName = groupName;
/*    */   }
/*    */   
/*    */   public String getGroupName()
/*    */   {
/* 31 */     return this.groupName;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getRefGroup()
/*    */   {
/* 37 */     return this.refGroup;
/*    */   }
/*    */   
/*    */   public void setRegGroup(String refGroup)
/*    */   {
/* 42 */     this.refGroup = refGroup;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\ServiceGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */