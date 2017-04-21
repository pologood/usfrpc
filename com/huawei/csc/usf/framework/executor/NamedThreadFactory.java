/*    */ package com.huawei.csc.usf.framework.executor;
/*    */ 
/*    */ import java.util.concurrent.ThreadFactory;
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ 
/*    */ public class NamedThreadFactory implements ThreadFactory
/*    */ {
/*  8 */   private static final AtomicInteger POOL_SEQ = new AtomicInteger(1);
/*    */   
/* 10 */   private final AtomicInteger threadNum = new AtomicInteger(1);
/*    */   
/*    */   private final String prefixName;
/*    */   
/*    */   private final boolean isDaemon;
/*    */   
/*    */   private final ThreadGroup group;
/*    */   
/*    */   public NamedThreadFactory()
/*    */   {
/* 20 */     this("pool-" + POOL_SEQ.getAndIncrement(), true);
/*    */   }
/*    */   
/*    */   public NamedThreadFactory(String prefix)
/*    */   {
/* 25 */     this(prefix, true);
/*    */   }
/*    */   
/*    */   public NamedThreadFactory(String prefix, boolean daemon)
/*    */   {
/* 30 */     this.isDaemon = daemon;
/* 31 */     this.prefixName = (prefix + "-thread-");
/* 32 */     SecurityManager s = System.getSecurityManager();
/* 33 */     this.group = (s == null ? Thread.currentThread().getThreadGroup() : s.getThreadGroup());
/*    */   }
/*    */   
/*    */ 
/*    */   public ThreadGroup getThreadGroup()
/*    */   {
/* 39 */     return this.group;
/*    */   }
/*    */   
/*    */   public Thread newThread(Runnable runnable)
/*    */   {
/* 44 */     String name = this.prefixName + this.threadNum.getAndIncrement();
/* 45 */     Thread ret = new Thread(this.group, runnable, name, 0L);
/* 46 */     ret.setDaemon(this.isDaemon);
/* 47 */     return ret;
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\executor\NamedThreadFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */