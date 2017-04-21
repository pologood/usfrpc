/*     */ package com.huawei.csc.usf.framework.util;
/*     */ 
/*     */ import com.huawei.csc.kernel.api.log.LogFactory;
/*     */ import com.huawei.csc.kernel.api.log.debug.DebugLog;
/*     */ import java.lang.management.ClassLoadingMXBean;
/*     */ import java.lang.management.CompilationMXBean;
/*     */ import java.lang.management.GarbageCollectorMXBean;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.MemoryMXBean;
/*     */ import java.lang.management.MemoryUsage;
/*     */ import java.lang.management.OperatingSystemMXBean;
/*     */ import java.lang.management.RuntimeMXBean;
/*     */ import java.lang.management.ThreadMXBean;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.NumberFormat;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.ObjectName;
/*     */ import javax.management.StandardMBean;
/*     */ import org.apache.commons.lang.time.FastDateFormat;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JMXUtils
/*     */ {
/*  36 */   private static final DebugLog LOGGER = LogFactory.getDebugLog(JMXUtils.class);
/*     */   
/*     */ 
/*  39 */   private static final NumberFormat formatter = new DecimalFormat("###,###,###,###,###,###");
/*     */   
/*     */ 
/*  42 */   private static final FastDateFormat utcDfm = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss'Z'");
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
/*     */   public static <T> void registerMBean(T mbeanInstance, Class<T> mbeanInterface, String objectName)
/*     */   {
/*  57 */     doRegister(mbeanInstance, mbeanInterface, objectName);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static <T> void doRegister(T mbeanInstance, Class<T> mbeanInterface, String objectName)
/*     */   {
/*     */     try
/*     */     {
/*  66 */       ObjectName name = new ObjectName(objectName);
/*  67 */       StandardMBean mxBean = new StandardMBean(mbeanInstance, mbeanInterface, true);
/*     */       
/*  69 */       MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
/*  70 */       Set<ObjectName> set = mbs.queryNames(name, null);
/*  71 */       if ((set != null) && (set.isEmpty()))
/*     */       {
/*  73 */         mbs.registerMBean(mxBean, name);
/*     */       }
/*     */       else
/*     */       {
/*  77 */         mbs.unregisterMBean(name);
/*  78 */         mbs.registerMBean(mxBean, name);
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  83 */       LOGGER.warn("Error registering a MBean with objectname ' " + objectName + " ' for JMX management", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void unregisterMBean(String objectName)
/*     */   {
/*     */     try
/*     */     {
/*  92 */       ObjectName name = new ObjectName(objectName);
/*  93 */       ManagementFactory.getPlatformMBeanServer().unregisterMBean(name);
/*     */     }
/*     */     catch (Exception ignore)
/*     */     {
/*  97 */       LOGGER.error("Error unregister a mbean with objectname '" + objectName + "' for JMX management", ignore);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Map<String, String> getSystemInformation()
/*     */   {
/* 105 */     Map<String, String> map = new TreeMap();
/*     */     
/*     */ 
/* 108 */     OperatingSystemMXBean osMBean = ManagementFactory.getOperatingSystemMXBean();
/*     */     
/* 110 */     map.put("os-name", osMBean.getName());
/* 111 */     map.put("os-arch", osMBean.getArch());
/* 112 */     map.put("os-version", osMBean.getVersion());
/* 113 */     map.put("os-procs", Integer.toString(osMBean.getAvailableProcessors()));
/*     */     
/*     */     try
/*     */     {
/* 117 */       map.put("os-open-file-descriptor-count", formatNumberWithThousandSeparator(((Long)ManagementFactory.getPlatformMBeanServer().getAttribute(new ObjectName("java.lang:type=OperatingSystem"), "OpenFileDescriptorCount")).longValue()));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 123 */       map.put("os-max-file-descriptor-count", formatNumberWithThousandSeparator(((Long)ManagementFactory.getPlatformMBeanServer().getAttribute(new ObjectName("java.lang:type=OperatingSystem"), "MaxFileDescriptorCount")).longValue()));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 129 */       map.put("os-load-average", formatNumberWithThousandSeparator(((Long)ManagementFactory.getPlatformMBeanServer().getAttribute(new ObjectName("java.lang:type=OperatingSystem"), "SystemLoadAverage")).longValue()));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 135 */       String ulimit = System.getProperty("ultra.ulimit");
/* 136 */       map.put("os-ulimit", ulimit == null ? "Unknown" : formatNumberWithThousandSeparator(Long.parseLong(ulimit)));
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (Exception ignore)
/*     */     {
/*     */ 
/* 143 */       LOGGER.error("Error get system information", ignore);
/*     */     }
/*     */     
/*     */ 
/* 147 */     MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
/* 148 */     MemoryUsage u = memoryBean.getHeapMemoryUsage();
/* 149 */     map.put("mem-heap-size", formatKBytes(u.getUsed()));
/* 150 */     map.put("mem-committed", formatKBytes(u.getCommitted()));
/* 151 */     map.put("mem-max-heap-size", formatKBytes(u.getMax()));
/* 152 */     map.put("mem-heap-objects-pending-finalization", formatNumberWithThousandSeparator(memoryBean.getObjectPendingFinalizationCount()));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 157 */     Collection<GarbageCollectorMXBean> garbageCollectors = ManagementFactory.getGarbageCollectorMXBeans();
/*     */     
/* 159 */     for (GarbageCollectorMXBean garbageCollectorMBean : garbageCollectors)
/*     */     {
/* 161 */       String gcName = garbageCollectorMBean.getName();
/* 162 */       long gcCount = garbageCollectorMBean.getCollectionCount();
/* 163 */       long gcTime = garbageCollectorMBean.getCollectionTime();
/* 164 */       map.put("gc-" + gcName + "-collections", formatNumberWithThousandSeparator(gcCount));
/*     */       
/* 166 */       map.put("gc-" + gcName + "-gc-time", gcTime < 0L ? "Unavailable" : formatNumberWithThousandSeparator(gcTime));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 171 */     RuntimeMXBean rmBean = ManagementFactory.getRuntimeMXBean();
/* 172 */     map.put("vm-instance-name", rmBean.getName());
/* 173 */     map.put("vm-name", rmBean.getVmName());
/* 174 */     map.put("vm-vendor", rmBean.getVmVendor());
/* 175 */     map.put("vm-version", rmBean.getVmVersion());
/* 176 */     map.put("vm-uptime", getMillisecondsAsTime(rmBean.getUptime()));
/* 177 */     map.put("vm-starttime", getAsUTCString(rmBean.getStartTime()));
/*     */     
/*     */ 
/* 180 */     CompilationMXBean cmpMBean = ManagementFactory.getCompilationMXBean();
/* 181 */     map.put("cmp-jit-compiler", cmpMBean.getName());
/* 182 */     map.put("cmp-compilation-time", cmpMBean.isCompilationTimeMonitoringSupported() ? formatNumberWithThousandSeparator(cmpMBean.getTotalCompilationTime()) : "Unavailable");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 190 */     ThreadMXBean tmBean = ManagementFactory.getThreadMXBean();
/* 191 */     map.put("thread-count", Integer.toString(tmBean.getThreadCount()));
/* 192 */     map.put("thread-daemon-count", Integer.toString(tmBean.getDaemonThreadCount()));
/*     */     
/* 194 */     map.put("thread-peak-count", Integer.toString(tmBean.getPeakThreadCount()));
/*     */     
/* 196 */     map.put("thread-total-started-count", formatNumberWithThousandSeparator(tmBean.getTotalStartedThreadCount()));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 201 */     ClassLoadingMXBean clMBean = ManagementFactory.getClassLoadingMXBean();
/* 202 */     map.put("cl-loaded-count", formatNumberWithThousandSeparator(clMBean.getLoadedClassCount()));
/*     */     
/* 204 */     map.put("cl-unloaded-count", formatNumberWithThousandSeparator(clMBean.getUnloadedClassCount()));
/*     */     
/* 206 */     map.put("cl-total-loaded-count", formatNumberWithThousandSeparator(clMBean.getTotalLoadedClassCount()));
/*     */     
/*     */ 
/*     */ 
/* 210 */     return map;
/*     */   }
/*     */   
/*     */   public static String formatNumberWithThousandSeparator(long l)
/*     */   {
/* 215 */     return formatter.format(l);
/*     */   }
/*     */   
/*     */   public static String getMillisecondsAsTime(long millis)
/*     */   {
/* 220 */     long days = TimeUnit.MILLISECONDS.toDays(millis);
/* 221 */     millis -= days * 60000L * 60L * 24L;
/* 222 */     long hours = TimeUnit.MILLISECONDS.toHours(millis);
/* 223 */     millis -= hours * 60000L * 60L;
/* 224 */     long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
/* 225 */     millis -= minutes * 60000L;
/* 226 */     long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
/*     */     
/* 228 */     return String.format("%d Days, %d H: %d M: %d S", new Object[] { Long.valueOf(days), Long.valueOf(hours), Long.valueOf(minutes), Long.valueOf(seconds) });
/*     */   }
/*     */   
/*     */ 
/*     */   public static String getAsUTCString(long time)
/*     */   {
/*     */     try
/*     */     {
/* 236 */       return utcDfm.format(new Date(time));
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 240 */       throw new IllegalArgumentException("Invalid time : in milliseconds " + time);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static String formatKBytes(long bytes)
/*     */   {
/* 247 */     if (bytes == -1L)
/*     */     {
/* 249 */       return "-1K";
/*     */     }
/* 251 */     long kb = bytes / 1024L;
/* 252 */     return formatNumberWithThousandSeparator(kb) + "K";
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\util\JMXUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */