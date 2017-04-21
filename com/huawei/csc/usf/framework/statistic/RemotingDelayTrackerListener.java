/*     */ package com.huawei.csc.usf.framework.statistic;
/*     */ 
/*     */ import com.huawei.csc.remoting.common.EncodeInfo;
/*     */ import com.huawei.csc.remoting.common.buf.ProtoBuf;
/*     */ import com.huawei.csc.remoting.common.decode.DecodeInfo;
/*     */ import com.huawei.csc.remoting.common.impl.ChannelInfo;
/*     */ import com.huawei.csc.remoting.event.RemotingEvent;
/*     */ import com.huawei.csc.remoting.event.RemotingEventListener;
/*     */ import com.huawei.csc.remoting.event.RemotingEventType;
/*     */ import com.huawei.csc.usf.framework.IMessage;
/*     */ import com.huawei.csc.usf.framework.MessageHeaders;
/*     */ import com.huawei.csc.usf.framework.ReplyInterceptor;
/*     */ import com.huawei.csc.usf.framework.ServiceEngine;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RemotingDelayTrackerListener
/*     */   implements RemotingEventListener
/*     */ {
/*  42 */   private ServiceEngine serviceEngine = null;
/*     */   
/*  44 */   private Map<RemotingEventType, String> eventName = new HashMap();
/*     */   
/*  46 */   private Map<RemotingEventType, Boolean> eventBegine = new HashMap();
/*     */   
/*     */   public RemotingDelayTrackerListener(ServiceEngine serviceEngine)
/*     */   {
/*  50 */     this.serviceEngine = serviceEngine;
/*     */     
/*  52 */     this.eventName.put(RemotingEventType.ENCODING_BEGIN, "io.encode");
/*  53 */     this.eventName.put(RemotingEventType.ENCODING_END, "io.encode");
/*  54 */     this.eventName.put(RemotingEventType.ENCODE_PUT_QUEUE_BEGIN, "io.encode.inQueue");
/*     */     
/*  56 */     this.eventName.put(RemotingEventType.ENCODE_EXECUTE_BEGIN, "io.encode.inQueue");
/*     */     
/*  58 */     this.eventBegine.put(RemotingEventType.ENCODE_PUT_QUEUE_BEGIN, Boolean.valueOf(true));
/*  59 */     this.eventName.put(RemotingEventType.DECODE_PUT_QUEUE_BEGIN, "io.decode.inQueue");
/*     */     
/*  61 */     this.eventName.put(RemotingEventType.DECODE_EXECUTE_BEGIN, "io.decode.inQueue");
/*     */     
/*  63 */     this.eventBegine.put(RemotingEventType.DECODE_PUT_QUEUE_BEGIN, Boolean.valueOf(true));
/*  64 */     this.eventBegine.put(RemotingEventType.ENCODING_BEGIN, Boolean.valueOf(true));
/*  65 */     this.eventName.put(RemotingEventType.CHANNEL_WRITE_BEGIN, "io.write");
/*  66 */     this.eventName.put(RemotingEventType.CHANNEL_WRITE_END, "io.write");
/*  67 */     this.eventBegine.put(RemotingEventType.CHANNEL_WRITE_BEGIN, Boolean.valueOf(true));
/*  68 */     this.eventName.put(RemotingEventType.CHANNEL_READ_BEGIN, "io.read");
/*  69 */     this.eventName.put(RemotingEventType.CHANNEL_READ_END, "io.read");
/*  70 */     this.eventBegine.put(RemotingEventType.CHANNEL_READ_BEGIN, Boolean.valueOf(true));
/*  71 */     this.eventName.put(RemotingEventType.DECODE_BEGIN, "io.decode");
/*  72 */     this.eventName.put(RemotingEventType.DECODE_END, "io.decode");
/*  73 */     this.eventBegine.put(RemotingEventType.DECODE_BEGIN, Boolean.valueOf(true));
/*     */   }
/*     */   
/*     */ 
/*     */   public void onEvent(RemotingEvent event)
/*     */   {
/*  79 */     if (!ProcessDelayTracker.isTrackerOpen())
/*     */     {
/*  81 */       return;
/*     */     }
/*     */     
/*  84 */     RemotingEventType evnetType = event.getEventType();
/*     */     
/*  86 */     if ((evnetType == RemotingEventType.INVOKE_MESSAGE_PROCESSOR_BEGIN) || (evnetType == RemotingEventType.INVOKE_MESSAGE_PROCESSOR_END))
/*     */     {
/*     */ 
/*     */ 
/*  90 */       return;
/*     */     }
/*     */     
/*  93 */     com.huawei.csc.remoting.common.Context remotingContext = event.getContext();
/*     */     
/*     */ 
/*  96 */     ProcessDelayTracker tracker = (ProcessDelayTracker)remotingContext.getContext("usf.tracker");
/*     */     
/*     */ 
/*  99 */     if (null == tracker)
/*     */     {
/* 101 */       tracker = (ProcessDelayTracker)remotingContext.getContext("newResponseTracker");
/*     */       
/* 103 */       if (null == tracker)
/*     */       {
/* 105 */         tracker = ProcessDelayTracker.createTracker("io.receive");
/* 106 */         remotingContext.addContext("newResponseTracker", tracker);
/*     */       }
/*     */     }
/*     */     
/* 110 */     ProcessDelayTracker newTracker = null;
/* 111 */     String name = (String)this.eventName.get(evnetType);
/*     */     
/* 113 */     com.huawei.csc.remoting.common.Context context = event.getContext();
/* 114 */     if (Boolean.TRUE.equals(this.eventBegine.get(evnetType)))
/*     */     {
/* 116 */       newTracker = ProcessDelayTracker.next(tracker, name);
/* 117 */       if (null == newTracker)
/*     */       {
/* 119 */         return;
/*     */       }
/* 121 */       context.addContext(name, newTracker);
/* 122 */       if ((evnetType == RemotingEventType.DECODE_BEGIN) || (evnetType == RemotingEventType.CHANNEL_WRITE_BEGIN))
/*     */       {
/*     */ 
/* 125 */         ChannelInfo cInfo = event.getChannelInfo();
/* 126 */         newTracker.putAppendInfo("io.chnnelInfo", cInfo);
/* 127 */         Object message = event.getMessage();
/*     */         
/* 129 */         if ((message instanceof EncodeInfo))
/*     */         {
/* 131 */           message = ((EncodeInfo)message).getEncodedBuf();
/*     */         }
/*     */         
/* 134 */         if ((message instanceof ProtoBuf))
/*     */         {
/*     */ 
/* 137 */           newTracker.putAppendInfo("io.size(byte)", Integer.valueOf(((ProtoBuf)message).readableBytes()));
/*     */         }
/*     */         
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 144 */       newTracker = (ProcessDelayTracker)context.removeContext(name);
/* 145 */       if (null != newTracker)
/*     */       {
/* 147 */         ProcessDelayTracker.done(newTracker);
/*     */       }
/*     */       
/*     */ 
/* 151 */       if (evnetType == RemotingEventType.DECODE_END)
/*     */       {
/* 153 */         ProcessDelayTracker.done(tracker);
/*     */         
/* 155 */         Object message = event.getMessage();
/*     */         
/* 157 */         if ((message instanceof DecodeInfo))
/*     */         {
/* 159 */           message = ((DecodeInfo)message).getMessage();
/*     */         }
/*     */         
/* 162 */         if (!(message instanceof IMessage))
/*     */         {
/* 164 */           return;
/*     */         }
/*     */         
/* 167 */         com.huawei.csc.usf.framework.Context usfContext = this.serviceEngine.getReplyInterceptor().getContext(((IMessage)message).getHeaders().getRequestId());
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 172 */         if (null == usfContext)
/*     */         {
/* 174 */           return;
/*     */         }
/*     */         
/* 177 */         ProcessDelayTracker mainTracker = usfContext.getProcessDelayTracker();
/*     */         
/*     */ 
/* 180 */         ProcessDelayTracker respTracker = (ProcessDelayTracker)remotingContext.removeContext("newResponseTracker");
/*     */         
/* 182 */         if (null == respTracker)
/*     */         {
/* 184 */           return;
/*     */         }
/*     */         
/* 187 */         ProcessDelayTracker cTrack = mainTracker.getCurrentTracker();
/*     */         
/* 189 */         ProcessDelayTracker ioWaitReply = ProcessDelayTracker.next(mainTracker, "io.waitReply");
/*     */         
/* 191 */         ioWaitReply.setBeginTime(cTrack.getEndTime());
/* 192 */         ioWaitReply.setEndTime(respTracker.getBeginTime());
/* 193 */         mainTracker.next(respTracker);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\statistic\RemotingDelayTrackerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */