package com.dc.logger.dynamic.controller.factory;

import com.dc.logger.dynamic.controller.IDynamicLoggerController;
import com.dc.logger.dynamic.logger.BaseDynamicLogger;
import com.dc.logger.dynamic.logger.IDynamicLogger;

public class BaseLoggerFactory implements ILoggerFactory {

	protected final String basePath;
	protected final String filenameExtension;
	
	protected final boolean useBuffer;
	protected final int bufferSize;
	
	protected final boolean canClose;
	protected final int maxIdleTime;
	protected volatile long lastLoggerTime;
	
	protected final boolean useMultilayerTargetNamePath;
	protected final String multilayerTargetNamePathPrefix;
	protected final String multilayerTargetNamePathSuffix;
	protected final int eachLayerLength;
	
	public BaseLoggerFactory(String basePath) {
		
		this( basePath, "log", false, 0, true, 300000, true, "~", "", 2 );
	}
	
	public BaseLoggerFactory(String basePath, boolean useBuffer, int bufferSize ) {
		
		this( basePath, "log", useBuffer, bufferSize, true, 300000, true, "~", "", 2 );
	}
	
	public BaseLoggerFactory(
			
			String basePath, String filenameExtension,
			
			boolean useBuffer, int bufferSize,
			
			boolean canClose, int maxIdleTime,
			
			boolean useMultilayerTargetNamePath,
			String multilayerTargetNamePathPrefix,
			String multilayerTargetNamePathSuffix, int eachLayerLength) {

		this.basePath = basePath;
		this.filenameExtension = filenameExtension;
		
		this.useBuffer = useBuffer;
		this.bufferSize = bufferSize;
		
		this.canClose = canClose;
		this.maxIdleTime = maxIdleTime;
		
		this.useMultilayerTargetNamePath = useMultilayerTargetNamePath;
		this.multilayerTargetNamePathPrefix = multilayerTargetNamePathPrefix;
		this.multilayerTargetNamePathSuffix = multilayerTargetNamePathSuffix;
		this.eachLayerLength = eachLayerLength;
		
	}

	public IDynamicLogger getNewLogger(IDynamicLoggerController controller, String targetName) {
		
		return new BaseDynamicLogger(controller, basePath, targetName, filenameExtension, 
				useBuffer, bufferSize, canClose, maxIdleTime, useMultilayerTargetNamePath, multilayerTargetNamePathPrefix, multilayerTargetNamePathSuffix, eachLayerLength);
	}
}
