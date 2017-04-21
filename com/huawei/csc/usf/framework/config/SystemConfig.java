package com.huawei.csc.usf.framework.config;

import com.huawei.csc.usf.framework.sr.ServiceType;
import java.util.List;

public abstract interface SystemConfig
{
  public abstract void init();
  
  @Deprecated
  public abstract String getRPCAddress();
  
  public abstract String getRPCAddress(ServiceType paramServiceType);
  
  public abstract String getRestAddress();
  
  public abstract int getRPCHeartBeatInterval();
  
  public abstract int getRPCHeartBeatMaxLostCount();
  
  public abstract int getRPCServerIOWorkers();
  
  public abstract int getRPCServerIOQueueSize();
  
  public abstract int getRPCClientReconnectInterval();
  
  public abstract int getRPCChannelNum();
  
  public abstract int getRPCClientIOWorkers();
  
  public abstract int getRPCClientIOQueueSize();
  
  public abstract int getWorkerCoreSize();
  
  public abstract int getWorkerGroupSize();
  
  public abstract int getWorkerMaxSize();
  
  public abstract int getWorkerQueues();
  
  public abstract int getResponseCoreSize();
  
  public abstract int getResponseGroupSize();
  
  public abstract int getResponseMaxSize();
  
  public abstract int getResponseQueues();
  
  public abstract String getWhiteList();
  
  public abstract String getBlackList();
  
  public abstract boolean isAuthenticationEnable();
  
  public abstract boolean isClientAuthenticationEnable();
  
  public abstract boolean isServerAuthenticationEnable();
  
  public abstract String getClientLogginPassword();
  
  public abstract String getServerLogginPassword();
  
  public abstract boolean getThreadPoolMonitorSwitch();
  
  public abstract long getThreadPoolMonitorInterval();
  
  public abstract boolean isSlowServiceOpen();
  
  public abstract long getSlowServiceClientTime();
  
  public abstract long getSlowServiceServerTime();
  
  public abstract int getDelayTimeInterval();
  
  public abstract String getDefaultRouter();
  
  public abstract long getGracefulDestroyTimeout();
  
  public abstract int getTimeout();
  
  public abstract int getServerWeight();
  
  public abstract String getServerFailPolicy();
  
  public abstract int getResendTimes();
  
  public abstract int getConnectionTimeout();
  
  public abstract String getDsfApplication();
  
  public abstract boolean isSslEnable();
  
  public abstract String getTrustStorePath();
  
  public abstract String getKeyStorePath();
  
  public abstract String getKeyStorePassword();
  
  public abstract String getRpcLoginIdentity();
  
  public abstract int getTransactionTimeout();
  
  public abstract String getDimensionKeyFormat(String paramString);
  
  public abstract String getUsfTimeDistribution();
  
  public abstract boolean getEnableStatisic();
  
  public abstract boolean getTimeStampEnabled();
  
  public abstract boolean getIsDelayRegister();
  
  public abstract long getRPCAsynWaitQueueSize();
  
  public abstract String getConsumerGroupNames();
  
  public abstract String getProviderGroupName();
  
  public abstract String getDsfRpcSerialization();
  
  public abstract String getServicePrefixName();
  
  public abstract String getDsfTpsThreshold();
  
  public abstract String getAuthSwitchKey(String paramString);
  
  public abstract String getXmlConfig(String paramString);
  
  public abstract List<String> getZkUrlKeys();
  
  public abstract boolean isZkOff(String paramString);
  
  public abstract String getDefaultZkUrl();
  
  public abstract String getZkUrl(String paramString);
  
  public abstract String getDsfZkName();
  
  public abstract Integer getDefaultZkSessionTimeout();
  
  public abstract String getDefaultZkRootDir();
  
  public abstract Integer getZkSessionTimeout(String paramString);
  
  public abstract String getZkRootDir(String paramString);
  
  public abstract int getBulkheadTimeInMilliseconds();
  
  public abstract int getBulkheadNumberOfBuckets();
  
  public abstract int getBulkheadMaxThreadNum();
  
  public abstract String getDsfApplicationDirect();
  
  public abstract int getHttpClientMaxThreads();
  
  public abstract String getHttpClientIP();
  
  public abstract boolean getHttpClientSSLEnable();
  
  public abstract boolean getHttpClientSSLNeedClientAuth();
  
  public abstract String getHttpClientSSLTruststorefile();
  
  public abstract String getHttpClientSSLTrustStorePwd();
  
  public abstract String getHttpClientSSLKeyStoreFile();
  
  public abstract String getHttpClientSSLKeyStorePwd();
  
  public abstract String getRestUrl();
  
  public abstract boolean getTransformerGlobalRef();
  
  public abstract String getBigNumberMaxLength();
  
  public abstract String getEbusBusConnectorVersion();
  
  public abstract int getIoStatisticsInterval();
  
  public abstract boolean hasEbusAdapter();
  
  public abstract boolean getIpEnabledField();
  
  public abstract long getClientMessageSizeThreshold();
  
  public abstract boolean getSyncToOldEbus();
  
  public abstract long getServerMessageSizeThreshold();
  
  public abstract boolean isBigMessageOpen();
}


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\config\SystemConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */