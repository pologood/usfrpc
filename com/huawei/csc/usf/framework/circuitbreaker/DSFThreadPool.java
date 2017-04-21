/*    */ package com.huawei.csc.usf.framework.circuitbreaker;
/*    */ 
/*    */ import java.util.concurrent.BlockingQueue;
/*    */ import java.util.concurrent.Callable;
/*    */ import java.util.concurrent.Future;
/*    */ import java.util.concurrent.LinkedBlockingQueue;
/*    */ import java.util.concurrent.SynchronousQueue;
/*    */ import java.util.concurrent.ThreadPoolExecutor;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ public class DSFThreadPool
/*    */ {
/*    */   private BlockingQueue<Runnable> queue;
/*    */   private ThreadPoolExecutor threadPoolExecutor;
/*    */   private Integer queueSize;
/*    */   private Integer maxQueueSize;
/*    */   
/*    */   public DSFThreadPool(Integer threadNum, Integer queueSize, Integer maxQueueSize)
/*    */   {
/* 20 */     this.queueSize = queueSize;
/* 21 */     this.maxQueueSize = maxQueueSize;
/* 22 */     this.queue = getBlockingQueue(this.maxQueueSize.intValue());
/* 23 */     this.threadPoolExecutor = new ThreadPoolExecutor(threadNum.intValue(), threadNum.intValue(), 0L, TimeUnit.SECONDS, this.queue);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Future<?> submit(Callable<Void> callable)
/*    */   {
/* 30 */     return this.threadPoolExecutor.submit(callable);
/*    */   }
/*    */   
/*    */ 
/*    */   public ThreadPoolExecutor getThreadPoolExecutor()
/*    */   {
/* 36 */     return this.threadPoolExecutor;
/*    */   }
/*    */   
/*    */   public synchronized void shutdown(long timeout, TimeUnit unit)
/*    */   {
/*    */     try
/*    */     {
/* 43 */       this.threadPoolExecutor.awaitTermination(timeout, unit);
/*    */     }
/*    */     catch (InterruptedException e)
/*    */     {
/* 47 */       throw new RuntimeException("Interrupted while waiting for thread-pools to terminate. Pools may not be correctly shutdown or cleared.", e);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize)
/*    */   {
/* 56 */     if (maxQueueSize <= 0)
/*    */     {
/* 58 */       return new SynchronousQueue();
/*    */     }
/*    */     
/*    */ 
/* 62 */     return new LinkedBlockingQueue(maxQueueSize);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public boolean isQueueSpaceAvailable()
/*    */   {
/* 69 */     if (this.queueSize.intValue() <= 0)
/*    */     {
/* 71 */       return true;
/*    */     }
/*    */     
/*    */ 
/* 75 */     return this.threadPoolExecutor.getQueue().size() < this.queueSize.intValue();
/*    */   }
/*    */   
/*    */ 
/*    */   public void resizeThreadPool(DSFFaultToleranceProperties zkThreadPoolProperties)
/*    */   {
/* 81 */     if (null != zkThreadPoolProperties)
/*    */     {
/* 83 */       if (null != zkThreadPoolProperties.getBulkheadThreadNum())
/*    */       {
/* 85 */         this.threadPoolExecutor.setCorePoolSize(zkThreadPoolProperties.getBulkheadThreadNum().intValue());
/* 86 */         this.threadPoolExecutor.setMaximumPoolSize(zkThreadPoolProperties.getBulkheadThreadNum().intValue());
/*    */       }
/*    */       
/* 89 */       if (null != zkThreadPoolProperties.getBulkheadQueueSize())
/*    */       {
/* 91 */         this.queueSize = zkThreadPoolProperties.getBulkheadQueueSize();
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\circuitbreaker\DSFThreadPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */