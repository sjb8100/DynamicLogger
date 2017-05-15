package com.dc.logger.dynamic.logger.rename;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import com.dc.logger.dynamic.logger.BaseDynamicLogger;

/**
 * 当logger被关闭时，再调用log方法将会直接写入（不管useBuffer取值）到文件中（从新打开文件 写入 并关闭）
 * 
 * @author Daemon
 *
 */
public class NameChangeDynamicLogger extends BaseDynamicLogger {
	
	protected ReentrantLock nameChangeLock = new ReentrantLock();
	
	/**
	 * 当logger被关闭时，再调用log方法将会直接写入（不管useBuffer取值）到文件中（从新打开文件 写入 并关闭）
	 */
	public NameChangeDynamicLogger(
			String basePath, String targetName, String filenameExtension,
			boolean useBuffer, int bufferSize, boolean canClose,
			int maxIdleTime, boolean useMultilayerTargetNamePath,
			String multilayerTargetNamePathPrefix,
			String multilayerTargetNamePathSuffix, int eachLayerLength) {
		
		super(basePath, targetName, filenameExtension, useBuffer,
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
