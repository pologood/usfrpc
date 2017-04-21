/*     */ package com.huawei.csc.usf.framework.util;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class CopyOnWriteHashMap<K, V>
/*     */   implements Map<K, V>
/*     */ {
/*     */   private HashMap<K, V> map;
/*     */   
/*     */   public CopyOnWriteHashMap()
/*     */   {
/*  16 */     this.map = new HashMap();
/*     */   }
/*     */   
/*     */   public CopyOnWriteHashMap(int initialCapacity)
/*     */   {
/*  21 */     this.map = new HashMap(initialCapacity);
/*     */   }
/*     */   
/*     */   public CopyOnWriteHashMap(int initialCapacity, float loadFactor)
/*     */   {
/*  26 */     this.map = new HashMap(initialCapacity, loadFactor);
/*     */   }
/*     */   
/*     */   public CopyOnWriteHashMap(Map<? extends K, ? extends V> m)
/*     */   {
/*  31 */     this.map = new HashMap(m);
/*     */   }
/*     */   
/*     */ 
/*     */   public int size()
/*     */   {
/*  37 */     return this.map.size();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  43 */     return this.map.isEmpty();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean containsKey(Object key)
/*     */   {
/*  49 */     return this.map.containsKey(key);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean containsValue(Object value)
/*     */   {
/*  55 */     return this.map.containsValue(value);
/*     */   }
/*     */   
/*     */ 
/*     */   public V get(Object key)
/*     */   {
/*  61 */     return (V)this.map.get(key);
/*     */   }
/*     */   
/*     */ 
/*     */   public Set<K> keySet()
/*     */   {
/*  67 */     return this.map.keySet();
/*     */   }
/*     */   
/*     */ 
/*     */   public Collection<V> values()
/*     */   {
/*  73 */     return this.map.values();
/*     */   }
/*     */   
/*     */ 
/*     */   public Set<Map.Entry<K, V>> entrySet()
/*     */   {
/*  79 */     return this.map.entrySet();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized V put(K key, V value)
/*     */   {
/*  88 */     HashMap<K, V> newOne = new HashMap(this.map);
/*  89 */     V ret = newOne.put(key, value);
/*  90 */     this.map = newOne;
/*  91 */     return ret;
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void putAll(Map<? extends K, ? extends V> m)
/*     */   {
/*  97 */     HashMap<K, V> newOne = new HashMap(this.map);
/*  98 */     newOne.putAll(m);
/*  99 */     this.map = newOne;
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized V remove(Object key)
/*     */   {
/* 105 */     HashMap<K, V> newOne = new HashMap(this.map);
/* 106 */     V ret = newOne.remove(key);
/* 107 */     this.map = newOne;
/* 108 */     return ret;
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void clear()
/*     */   {
/* 114 */     this.map = new HashMap();
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\util\CopyOnWriteHashMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */