package com.dc.logger.dynamic.logger.rename.size;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;

import com.dc.logger.dynamic.controller.IDynamicLoggerController;
import com.dc.logger.dynamic.logger.rename.NameChangeDynamicLogger;

/**
 * 注意：如果是重要的日志，请不要使用缓存 或者 该日志不允许关闭，
 * 因为BufferedOutputStream即使被关闭也可以write数据，所以有几率部分日志没有写到文件中
 * （后面会对此加以改善）
 * 
 * @author Daemon
 *
 */
public class SizeConstraintDynamicLogger extends NameChangeDynamicLogger {

	protected final int maxSize;
	
	protected final AtomicLong sizeCountter = new AtomicLong(0);
	protected int index = 0;
	
	/**
	 * 注意：如果是重要的日志，请不要使用缓存 或者 该日志不允许关闭，
	 * 因为BufferedOutputStream即使被关闭也可以write数据，所以有几率部分日志没有写到文件中
	 * （后面会对此加以改善）
	 */
	public SizeConstraintDynamicLogger(IDynamicLoggerController controller,
			String basePath, String targetName, String filenameExtension,
			boolean useBuffer, int bufferSize, boolean canClose,
			int maxIdleTime, boolean useMultilayerTargetNamePath,
			String multilayerTargetNamePathPrefix,
			String multilayerTargetNamePathSuffix, int eachLayerLength,
			int maxSize) {
		
		super(controller, basePath, targetName, filenameExtension, useBuffer,
				bufferSize, canClose, maxIdleTime, useMultilayerTargetNamePath,
				multilayerTargetNamePathPrefix, multilayerTargetNamePathSuffix,
				eachLayerLength);
		
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
		
		return "." + index;
	}
	
	@Override
	protected void beforeWrite(String msg, byte[] datas) {
		
		super.beforeWrite(msg, datas);
		
		if( sizeCountter.addAndGet(msg.length()) > maxSize ) {
			
			changeName(msg, datas);
		}
		
	}
	
	@Override
	public void changeName(String msg, byte[] datas) {
		
		nameChangeLock.lock();
		try {
			
			if( sizeCountter.get() > maxSize ) {
				
				OutputStream oldOut = this.out;
				
				long fileLenth = resetIndex(datas.length);
				
				sizeCountter.set(fileLenth + datas.length);
				
				this.start();
				
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
