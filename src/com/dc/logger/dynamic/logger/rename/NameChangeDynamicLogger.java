package com.dc.logger.dynamic.logger.rename;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import com.dc.logger.dynamic.controller.IDynamicLoggerController;
import com.dc.logger.dynamic.logger.BaseDynamicLogger;

/**
 * 注意：如果是重要的日志，请不要使用缓存 或者 该日志不允许关闭，
 * 因为BufferedOutputStream即使被关闭也可以write数据，所以有几率部分日志没有写到文件中
 * （后面会对此加以改善）
 * 
 * @author Daemon
 *
 */
public class NameChangeDynamicLogger extends BaseDynamicLogger {
	
	protected ReentrantLock nameChangeLock = new ReentrantLock();
	
	/**
	 * 注意：如果是重要的日志，请不要使用缓存 或者 该日志不允许关闭，
	 * 因为BufferedOutputStream即使被关闭也可以write数据，所以有几率部分日志没有写到文件中
	 * （后面会对此加以改善）
	 */
	public NameChangeDynamicLogger(IDynamicLoggerController controller,
			String basePath, String targetName, String filenameExtension,
			boolean useBuffer, int bufferSize, boolean canClose,
			int maxIdleTime, boolean useMultilayerTargetNamePath,
			String multilayerTargetNamePathPrefix,
			String multilayerTargetNamePathSuffix, int eachLayerLength) {
		
		super(controller, basePath, targetName, filenameExtension, useBuffer,
				bufferSize, canClose, maxIdleTime, useMultilayerTargetNamePath,
				multilayerTargetNamePathPrefix, multilayerTargetNamePathSuffix,
				eachLayerLength);
		
	}
	
	@Override
	public void close() {
		
		nameChangeLock.lock();
		try {
			
			super.close();
			
		} finally {
			
			nameChangeLock.unlock();
		}
		
	}

	protected String getNamePrefix(String targetName) {
		
		return "";
	}
	
	protected String getNameSuffix(String targetName) {
		
		return "";
	}
	
	@Override
	protected String getName(String targetName) {
		
		return getNamePrefix(targetName) + targetName + getNameSuffix(targetName);
	}
	
	public void changeName(String msg, byte[] datas) {
		
		nameChangeLock.lock();
		try {
			
			OutputStream oldOut = this.out;
			
			this.start();
			
			try {
				oldOut.flush();
				oldOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} finally {
			
			nameChangeLock.unlock();
		}
		
	}
	
}
