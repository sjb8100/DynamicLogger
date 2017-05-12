package com.dc.logger.dynamic.controller;

import com.dc.logger.dynamic.logger.IDynamicLogger;

public interface IDynamicLoggerController {
	
	/**
	 * 建议将logger交由 Controller管理，所以不建议调用这个方法（建议直接调用logger方法）
	 * 除非该Logger不会被关闭
	 * 
	 * @param targetName
	 * @return
	 */
	IDynamicLogger getLogger(String targetName);
	
	void flush(String targetName);
	
	void reLoggerWhenClose( String msg, 
			
			String basePath, String targetName, String filenameExtension,
			
			boolean useBuffer, int bufferSize,
			
			boolean useMultilayerTargetNamePath,
			String multilayerTargetNamePathPrefix,
			String multilayerTargetNamePathSuffix, int eachLayerLength );

	void close(String targetName);

	void close(String targetName, IDynamicLogger dynamicLogger);

}
