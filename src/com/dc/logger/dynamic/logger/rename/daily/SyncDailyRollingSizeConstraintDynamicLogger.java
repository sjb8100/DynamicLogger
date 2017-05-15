package com.dc.logger.dynamic.logger.rename.daily;

import java.util.TimeZone;

/**
 * 当logger被关闭时，再调用log方法将会直接写入（不管useBuffer取值）到文件中（从新打开文件 写入 并关闭）
 * 
 * @author Daemon
 *
 */
public class SyncDailyRollingSizeConstraintDynamicLogger extends DailyRollingSizeConstraintDynamicLogger {

	/**
	 * 当logger被关闭时，再调用log方法将会直接写入（不管useBuffer取值）到文件中（从新打开文件 写入 并关闭）
	 * 
	 * @param basePath
	 * @param targetName
	 * @param filenameExtension
	 * @param useBuffer
	 * @param bufferSize
	 * @param canClose
	 * @param maxIdleTime
	 * @param useMultilayerTargetNamePath
	 * @param multilayerTargetNamePathPrefix
	 * @param multilayerTargetNamePathSuffix
	 * @param eachLayerLength
	 * @param timeZone
	 * @param maxSize
	 */
	public SyncDailyRollingSizeConstraintDynamicLogger(
			String basePath,
			String targetName, String filenameExtension, boolean useBuffer,
			int bufferSize, boolean canClose, int maxIdleTime,
			boolean useMultilayerTargetNamePath,
			String multilayerTargetNamePathPrefix,
			String multilayerTargetNamePathSuffix, int eachLayerLength,
			TimeZone timeZone, int maxSize) {

		super(basePath, targetName, filenameExtension, useBuffer,
				bufferSize, canClose, maxIdleTime, useMultilayerTargetNamePath,
				multilayerTargetNamePathPrefix, multilayerTargetNamePathSuffix,
				eachLayerLength, timeZone, maxSize);
	}
	
	@Override
	public synchronized void close() {
		
		super.close();
	}
	
	@Override
	public synchronized void log(String msg) {
		
		super.log(msg);
	}
	
}
