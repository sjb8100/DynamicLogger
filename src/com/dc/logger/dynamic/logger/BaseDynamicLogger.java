package com.dc.logger.dynamic.logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.dc.logger.dynamic.controller.IDynamicLoggerController;

/**
 * 注意：如果是重要的日志，请不要使用缓存 或者 该日志不允许关闭，
 * 因为BufferedOutputStream即使被关闭也可以write数据，所以有几率部分日志没有写到文件中
 * （后面会对此加以改善）
 * 
 * @author Daemon
 *
 */
public class BaseDynamicLogger implements IDynamicLogger {
	
	protected final IDynamicLoggerController controller;
	
	protected final String basePath;
	protected final String targetName;
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

	protected volatile boolean isClose = true;
	
	protected OutputStream out;
	protected String filePathNow;
	
	/**
	 * 
	 * 
	 * 注意：如果是重要的日志，请不要使用缓存 或者 该日志不允许关闭，
	 * 因为BufferedOutputStream即使被关闭也可以write数据，所以有几率部分日志没有写到文件中
	 * （后面会对此加以改善）
	 * 
	 * @param controller
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
	 */
	public BaseDynamicLogger(IDynamicLoggerController controller,
			
			String basePath, String targetName, String filenameExtension,
			
			boolean useBuffer, int bufferSize,
			
			boolean canClose, int maxIdleTime,
			
			boolean useMultilayerTargetNamePath,
			String multilayerTargetNamePathPrefix,
			String multilayerTargetNamePathSuffix, int eachLayerLength) {

		this.controller = controller;
		
		this.basePath = basePath;
		this.targetName = targetName;
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
	
	public void start() {
		
		filePathNow = getFilePath(targetName);
		
		File logFile = createFile(filePathNow);
		
		if( useBuffer ) {
			
			try {
				
				out = new BufferedOutputStream( new FileOutputStream(logFile, true), bufferSize );
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		} else {
			
			try {
				
				out = new FileOutputStream(logFile, true);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		isClose = false;
	}
	
	protected File createFile(String filePath) {
		
		File file = new File(filePath);
		
		if( ! file.exists() ) {
			
			file.getParentFile().mkdirs();
			
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return file;
	}
	
	public void log(String msg) {
		
		byte[] datas = msg.getBytes();
		
		beforeWrite(msg, datas);
		write(msg, datas);
		
		lastLoggerTime = System.currentTimeMillis();
	}
	
	protected void beforeWrite(String msg, byte[] datas) {
	}
	
	protected void write(String msg, byte[] datas) {
		
		try {
			
			if( useBuffer && isClose ) {
				
				controller.reLoggerWhenClose(msg, 
						basePath, targetName, filenameExtension, 
						useBuffer, bufferSize, 
						useMultilayerTargetNamePath, multilayerTargetNamePathPrefix, multilayerTargetNamePathSuffix, eachLayerLength);
				
			} else {
				
				out.write(datas);
			}
			
		} catch (IOException e) {
			
			if( isClose ) {
				
				controller.reLoggerWhenClose(msg, 
						basePath, targetName, filenameExtension, 
						useBuffer, bufferSize, 
						useMultilayerTargetNamePath, multilayerTargetNamePathPrefix, multilayerTargetNamePathSuffix, eachLayerLength);
				
			} else {
				
				e.printStackTrace();
			}
			
		}
	}

	public boolean closeAble() {

		return canClose && System.currentTimeMillis() - lastLoggerTime > maxIdleTime;
	}

	public boolean isClose() {

		return isClose;
	}

	public void close() {
		
		if( ! isClose ) {
			
			isClose = true;
			
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean flushAble() {

		return useBuffer;
	}

	public void flush() {
		
		OutputStream out = this.out;
		if( out != null ) {
			
			try {
				out.flush();
			} catch (IOException e) {
				
				if( ! isClose )
					e.printStackTrace();
			}
		}
		
	}
	
	protected String getFilePath(String targetName) {
		
		if( useMultilayerTargetNamePath ) {
			
			return basePath + "/" + getMultilayerTargetNamePath(targetName) 
					+ "/" + getName(targetName) + "." + getFilenameExtension(targetName);
			
		} else {
			
			return basePath + "/" + getName(targetName) + "." + getFilenameExtension(targetName);
		}
		
	}
	
	protected String getMultilayerTargetNamePath(String targetName) {
		
		StringBuilder path = new StringBuilder();
		
		int size = targetName.length();
		if( size >= eachLayerLength ) {
			
			for( int i=eachLayerLength; i<=size; i=i+eachLayerLength ) {
				
				path.append("/").append(multilayerTargetNamePathPrefix)
					.append( targetName.substring(i-2, i) ).append(multilayerTargetNamePathSuffix);
			}
			
		}
		
		return path.append("/").append(targetName).toString();
	}

	protected String getName(String targetName) {
		
		return targetName;
	}

	protected String getFilenameExtension(String targetName) {
		
		return filenameExtension;
	}

	public String getTargetName() {
		
		return targetName;
	}

}
