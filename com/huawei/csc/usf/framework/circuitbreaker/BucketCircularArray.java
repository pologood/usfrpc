/*     */ package com.huawei.csc.usf.framework.circuitbreaker;
/*     */ 
/*     */ import com.huawei.csc.usf.framework.circuitbreaker.util.Bucket;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.concurrent.atomic.AtomicReferenceArray;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BucketCircularArray
/*     */   implements Iterable<Bucket>
/*     */ {
/*     */   private final AtomicReference<ListState> state;
/*     */   private final int dataLength;
/*     */   private final int numBuckets;
/*     */   
/*     */   public BucketCircularArray(int size)
/*     */   {
/*  39 */     AtomicReferenceArray<Bucket> buckets = new AtomicReferenceArray(size + 1);
/*     */     
/*  41 */     this.state = new AtomicReference(new ListState(buckets, 0, 0, null));
/*  42 */     this.dataLength = buckets.length();
/*  43 */     this.numBuckets = size;
/*     */   }
/*     */   
/*     */   public void clear()
/*     */   {
/*     */     for (;;)
/*     */     {
/*  50 */       ListState current = (ListState)this.state.get();
/*  51 */       ListState newState = current.clear();
/*  52 */       if (this.state.compareAndSet(current, newState))
/*     */       {
/*  54 */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private class ListState
/*     */   {
/*     */     private final AtomicReferenceArray<Bucket> data;
/*     */     
/*     */     private final int size;
/*     */     
/*     */     private final int tail;
/*     */     
/*     */     private final int head;
/*     */     
/*     */ 
/*     */     private ListState(int data, int head)
/*     */     {
/*  73 */       this.head = head;
/*  74 */       this.tail = tail;
/*  75 */       if ((head == 0) && (tail == 0))
/*     */       {
/*  77 */         this.size = 0;
/*     */       }
/*     */       else
/*     */       {
/*  81 */         this.size = ((tail + BucketCircularArray.this.dataLength - head) % BucketCircularArray.this.dataLength);
/*     */       }
/*  83 */       this.data = data;
/*     */     }
/*     */     
/*     */     public Bucket getTail()
/*     */     {
/*  88 */       if (this.size == 0)
/*     */       {
/*  90 */         return null;
/*     */       }
/*     */       
/*     */ 
/*  94 */       return (Bucket)this.data.get(convert(this.size - 1));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private Bucket[] getArray()
/*     */     {
/* 102 */       ArrayList<Bucket> array = new ArrayList();
/* 103 */       for (int i = 0; i < this.size; i++)
/*     */       {
/* 105 */         array.add(this.data.get(convert(i)));
/*     */       }
/* 107 */       return (Bucket[])array.toArray(new Bucket[array.size()]);
/*     */     }
/*     */     
/*     */ 
/*     */     private ListState incrementTail()
/*     */     {
/* 113 */       if (this.size == BucketCircularArray.this.numBuckets)
/*     */       {
/* 115 */         return new ListState(BucketCircularArray.this, this.data, (this.head + 1) % BucketCircularArray.this.dataLength, (this.tail + 1) % BucketCircularArray.this.dataLength);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 120 */       return new ListState(BucketCircularArray.this, this.data, this.head, (this.tail + 1) % BucketCircularArray.this.dataLength);
/*     */     }
/*     */     
/*     */ 
/*     */     public ListState clear()
/*     */     {
/* 126 */       return new ListState(BucketCircularArray.this, new AtomicReferenceArray(BucketCircularArray.this.dataLength), 0, 0);
/*     */     }
/*     */     
/*     */ 
/*     */     public ListState addBucket(Bucket b)
/*     */     {
/* 132 */       this.data.set(this.tail, b);
/* 133 */       return incrementTail();
/*     */     }
/*     */     
/*     */     private int convert(int index)
/*     */     {
/* 138 */       return (index + this.head) % BucketCircularArray.this.dataLength;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Iterator<Bucket> iterator()
/*     */   {
/* 148 */     return Collections.unmodifiableList(Arrays.asList(getArray())).iterator();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addLast(Bucket o)
/*     */   {
/* 157 */     ListState currentState = (ListState)this.state.get();
/* 158 */     ListState newState = currentState.addBucket(o);
/*     */     
/*     */ 
/* 161 */     if (this.state.compareAndSet(currentState, newState)) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Bucket getLast()
/*     */   {
/* 173 */     return peekLast();
/*     */   }
/*     */   
/*     */ 
/*     */   public int size()
/*     */   {
/* 179 */     return ((ListState)this.state.get()).size;
/*     */   }
/*     */   
/*     */   public Bucket peekLast()
/*     */   {
/* 184 */     return ((ListState)this.state.get()).getTail();
/*     */   }
/*     */   
/*     */   public Bucket[] getArray()
/*     */   {
/* 189 */     return ((ListState)this.state.get()).getArray();
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\circuitbreaker\BucketCircularArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */