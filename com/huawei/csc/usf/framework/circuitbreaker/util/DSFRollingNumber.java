/*     */ package com.huawei.csc.usf.framework.circuitbreaker.util;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import com.huawei.csc.usf.framework.circuitbreaker.BucketCircularArray;
/*     */ import com.huawei.csc.usf.framework.config.DefaultSystemConfig;
/*     */ import io.netty.util.internal.chmv8.LongAdderV8;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DSFRollingNumber
/*     */ {
/*  15 */   private static final DebugLog DEBUGGER = LogFactory.getDebugLog(DSFRollingNumber.class);
/*     */   
/*     */ 
/*  18 */   private static final Time ACTUAL_TIME = new ActualTime(null);
/*     */   
/*     */   private final Time time;
/*     */   
/*     */   final long timeInMilliseconds;
/*     */   
/*     */   final int numberOfBuckets;
/*     */   
/*     */   final long bucketSizeInMillseconds;
/*     */   
/*  28 */   private ReentrantLock newBucketLock = new ReentrantLock();
/*     */   
/*     */   final BucketCircularArray buckets;
/*     */   
/*     */   private static final long MS_TO_NS = 1000000L;
/*     */   
/*  34 */   Integer bulkheadNum = Integer.valueOf(new DefaultSystemConfig().getBulkheadNumberOfBuckets());
/*     */   
/*     */ 
/*     */ 
/*     */   DSFRollingNumber(Time time, long timeInMilliseconds, int numberOfBuckets)
/*     */   {
/*  40 */     this.time = time;
/*  41 */     this.timeInMilliseconds = timeInMilliseconds;
/*  42 */     this.numberOfBuckets = numberOfBuckets;
/*     */     
/*  44 */     this.bucketSizeInMillseconds = (timeInMilliseconds / numberOfBuckets);
/*     */     
/*  46 */     this.buckets = new BucketCircularArray(numberOfBuckets);
/*     */   }
/*     */   
/*     */   public DSFRollingNumber(long timeInMilliseconds, int numberOfBuckets)
/*     */   {
/*  51 */     this(ACTUAL_TIME, timeInMilliseconds, numberOfBuckets);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void increment(RollingNumberEvent type)
/*     */   {
/*  59 */     getCurrentBucket().getAdder(type).increment();
/*     */   }
/*     */   
/*     */   public void add(RollingNumberEvent type, long value)
/*     */   {
/*  64 */     getCurrentBucket().getAdder(type).add(value);
/*     */   }
/*     */   
/*     */ 
/*     */   public void reset()
/*     */   {
/*  70 */     this.buckets.clear();
/*     */   }
/*     */   
/*     */   private Bucket getCurrentBucket()
/*     */   {
/*  75 */     long currentTime = this.time.getCurrentTimeInNanos() / 1000000L;
/*     */     
/*     */ 
/*  78 */     Bucket currentBucket = this.buckets.peekLast();
/*  79 */     if ((currentBucket != null) && (currentTime < currentBucket.windowStart + this.bucketSizeInMillseconds))
/*     */     {
/*     */ 
/*     */ 
/*  83 */       return currentBucket;
/*     */     }
/*     */     
/*  86 */     if (this.newBucketLock.tryLock())
/*     */     {
/*     */       try
/*     */       {
/*     */ 
/*  91 */         if (this.buckets.peekLast() == null)
/*     */         {
/*  93 */           Bucket newBucket = new Bucket(currentTime);
/*  94 */           this.buckets.addLast(newBucket);
/*  95 */           return newBucket;
/*     */         }
/*     */         
/*     */ 
/*  99 */         for (int i = 0; i < this.numberOfBuckets; i++)
/*     */         {
/* 101 */           Bucket lastBucket = this.buckets.peekLast();
/*     */           Bucket localBucket2;
/* 103 */           if (currentTime < lastBucket.windowStart + this.bucketSizeInMillseconds)
/*     */           {
/*     */ 
/* 106 */             return lastBucket;
/*     */           }
/*     */           
/* 109 */           if (currentTime - (lastBucket.windowStart + this.bucketSizeInMillseconds) > this.timeInMilliseconds)
/*     */           {
/*     */ 
/* 112 */             reset();
/*     */             
/* 114 */             return getCurrentBucket();
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 119 */           this.buckets.addLast(new Bucket(lastBucket.windowStart + this.bucketSizeInMillseconds));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 124 */         return this.buckets.peekLast();
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/* 129 */         this.newBucketLock.unlock();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 134 */     currentBucket = this.buckets.peekLast();
/*     */     
/* 136 */     if (currentBucket != null)
/*     */     {
/* 138 */       return currentBucket;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 145 */       Thread.sleep(5L);
/*     */     }
/*     */     catch (InterruptedException e) {}
/*     */     
/*     */ 
/* 150 */     return getCurrentBucket();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getValueOfLatestBucket(RollingNumberEvent type)
/*     */   {
/* 158 */     Bucket lastBucket = getCurrentBucket();
/* 159 */     if (lastBucket == null)
/*     */     {
/* 161 */       return 0L;
/*     */     }
/* 163 */     return lastBucket.get(type);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getRollingSum(RollingNumberEvent type)
/*     */   {
/* 171 */     Bucket lastBucket = getCurrentBucket();
/* 172 */     if (lastBucket == null)
/*     */     {
/* 174 */       return 0L;
/*     */     }
/*     */     
/* 177 */     long sum = 0L;
/* 178 */     for (Bucket b : this.buckets)
/*     */     {
/* 180 */       sum += b.getAdder(type).sum();
/*     */     }
/*     */     
/*     */ 
/* 184 */     return sum;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long[] getValues(RollingNumberEvent type)
/*     */   {
/* 193 */     Bucket lastBucket = getCurrentBucket();
/* 194 */     if (lastBucket == null)
/*     */     {
/* 196 */       return new long[0];
/*     */     }
/*     */     
/* 199 */     Bucket[] bucketArray = this.buckets.getArray();
/*     */     
/* 201 */     long[] values = new long[bucketArray.length];
/* 202 */     int i = 0;
/* 203 */     for (Bucket bucket : bucketArray)
/*     */     {
/*     */ 
/* 206 */       values[(i++)] = bucket.getAdder(type).sum();
/*     */     }
/*     */     
/* 209 */     return values;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class ActualTime
/*     */     implements DSFRollingNumber.Time
/*     */   {
/*     */     public long getCurrentTimeInNanos()
/*     */     {
/* 223 */       return System.nanoTime();
/*     */     }
/*     */   }
/*     */   
/*     */   static abstract interface Time
/*     */   {
/*     */     public abstract long getCurrentTimeInNanos();
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\circuitbreaker\util\DSFRollingNumber.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */