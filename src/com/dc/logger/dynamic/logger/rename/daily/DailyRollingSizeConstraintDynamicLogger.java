package com.dc.logger.dynamic.logger.rename.daily;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 当logger被关闭时，再调用log方法将会直接写入（不管useBuffer取值）到文件中（从新打开文件 写入 并关闭）
 * 
 * @author Daemon
 *
 */
public class DailyRollingSizeConstraintDynamicLogger extends DailyRollingDynamicLogger {

	protected final int maxSize;
	
	protected final AtomicLong sizeCountter = new AtomicLong(0);
	protected int index = 0;
	
	/**
	 * 当logger被关闭时，再调用log方法将会直接写入（不管useBuffer取值）到文件中（从新打开文件 写入 并关闭）
	 */
	public DailyRollingSizeConstraintDynamicLogger(
			String basePath, String targetName, String filenameExtension,
			boolean useBuffer, int bufferSize, boolean canClose,
			int maxIdleTime, boolean useMultilayerTargetNamePath,
			String multilayerTargetNamePathPrefix,
			String multilayerTargetNamePathSuffix, int eachLayerLength,
			TimeZone timeZone, int maxSize) {
		
		super(basePath, targetName, filenameExtension, useBuffer,
				bufferSize, canClose, maxIdleTime, useMultilayerTargetNamePath,
				multilayerTargetNamePathPrefix, multilayerTargetNamePathSuffix,
				eachLayerLength, timeZone);
		
		this.maxSize = maxSize;
		
		sizeCountter.set( resetIndex(0) );
	}
	
	protected long resetIndex(int lengthToWriteNow) {
		
		int oldIndex = index;
		
		for( index=oldIndex; ; index++ ) {
			
			String filePath = getFilePath(targetName);
			File f = new File(filePath);
			
			if( ! f.exists() || f.length() + lengthToWriteNow <= maxSize ) {
				
				return f.length();
				
			}
			
		}
		
	}
	
	@Override
	protected String getNameSuffix(String targetName) {
		
		return super.getNameSuffix(targetName) + "." + index;
	}
	
	@Override
	protected void beforeWrite(String msg, byte[] datas) {
		
		super.beforeWrite(msg, datas);
		
		if( sizeCountter.addAndGet(msg.length()) > maxSize ) {
			
			this.changeName(msg, datas);
		}
		
	}
	
	@Override
	public void changeName(String msg, byte[] datas) {
		
		nameChangeLock.lock();
		try {
			
			super.changeName(msg, datas);
			
			if( sizeCountter.get() > maxSize ) {
				
				OutputStream oldOut = this.out;
				
				this.start();
				
				long fileLenth = resetIndex(datas.length);
				sizeCountter.set(fileLenth + datas.length);
				
				try {
					oldOut.flush();
					oldOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		} finally {
			
			nameChangeLock.unlock();
		}
		
	}
}
