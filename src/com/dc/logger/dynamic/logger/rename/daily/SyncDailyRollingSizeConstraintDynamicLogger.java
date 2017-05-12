package com.dc.logger.dynamic.logger.rename.daily;

import java.util.TimeZone;

import com.dc.logger.dynamic.controller.IDynamicLoggerController;

/**
 * 
 * 对DailyRollingSizeConstraintDynamicLogger的 close 和  log 方法加了同步块，
 * 保证了其在使用buffer的情况下也能准确的记录logger
 * 
 * @author Daemon
 *
 */
public class SyncDailyRollingSizeConstraintDynamicLogger extends DailyRollingSizeConstraintDynamicLogger {

	public SyncDailyRollingSizeConstraintDynamicLogger(
			IDynamicLoggerController controller, String basePath,
			String targetName, String filenameExtension, boolean useBuffer,
			int bufferSize, boolean canClose, int maxIdleTime,
			boolean useMultilayerTargetNamePath,
			String multilayerTargetNamePathPrefix,
			String multilayerTargetNamePathSuffix, int eachLayerLength,
			TimeZone timeZone, int maxSize) {

		super(controller, basePath, targetName, filenameExtension, useBuffer,
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
