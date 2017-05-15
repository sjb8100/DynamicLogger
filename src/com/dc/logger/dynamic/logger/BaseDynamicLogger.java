package com.dc.logger.dynamic.logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * 当logger被关闭时，再调用log方法将会直接写入（不管useBuffer取值）到文件中（从新打开文件 写入 并关闭）
 * 
 * @author Daemon
 *
 */
public class BaseDynamicLogger implements IDynamicLogger {
	
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
	 * 当logger被关闭时，再调用log方法将会直接写入（不管useBuffer取值）到文件中（从新打开文件 写入 并关闭）
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
	public BaseDynamicLogger(
			
			String basePath, String targetName, String filenameExtension,
			
			boolean useBuffer, int bufferSize,
			
			boolean canClose, int maxIdleTime,
			
			boolean useMultilayerTargetNamePath,
			String multilayerTargetNamePathPrefix,
			String multilayerTargetNamePathSuffix, int eachLayerLength) {

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
				
				out = new OpenBufferedOutputStream( new FileOutputStream(logFile, true), bufferSize );
				
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
	
	public void startAndLogAndClose(byte[] datas) throws IOException {
		
		if(datas.length == 0)
			return;
		
		String filePathNow = getFilePath(targetName);
		File logFile = createFile(filePathNow);
		
		synchronized(this) {
			
			FileOutputStream out = new FileOutputStream(logFile, true);
			out.write(datas);
			out.close();
		}
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
			
			out.write(datas);
			
		} catch (IOException e) {
			
			if( isClose ) {
				
				try {
					startAndLogAndClose(datas);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
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
		
//		if( ! isClose ) {
			
			isClose = true;
			
			try {
				out.close();
			} catch (IOException e) {
				
				if( e instanceof BufferedOutputStreamCloseException ) {
					try {
						startAndLogAndClose( ((OpenBufferedOutputStream)out).cleanBuffer() );
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					e.printStackTrace();
				}
			}
//		}
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
				
				if( e instanceof BufferedOutputStreamCloseException ) {
					try {
						startAndLogAndClose( ((OpenBufferedOutputStream)out).cleanBuffer() );
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					e.printStackTrace();
				}
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

	private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	class OpenBufferedOutputStream extends BufferedOutputStream {
		
		boolean isClose;

		public OpenBufferedOutputStream(OutputStream out) {
			super(out);
			isClose = false;
		}
		
		public OpenBufferedOutputStream(OutputStream out, int size) {
			super(out, size);
		}
		
		@Override
		public synchronized void write(int b) throws IOException {
			
			if (isClose) {
				throw new BufferedOutputStreamCloseException();
			}
			
			super.write(b);
		}

		@Override
		public synchronized void write(byte[] b, int off, int len) throws IOException {
			
			if (isClose) {
				throw new BufferedOutputStreamCloseException();
			}
			
			super.write(b, off, len);
		}

		public synchronized byte[] cleanBuffer() {
			
			if (count > 0) {
				
				byte[] cleanBuf = new byte[count];
				System.arraycopy(buf, 0, cleanBuf, 0, count);
	            count = 0;
	            
	            return cleanBuf;
	            
	        } else {
	        	return EMPTY_BYTE_ARRAY;
	        }
		}

		@Override
		public void close() throws IOException {
			
			synchronized(this) {
				isClose = true;
				super.close();
			}
		}
		
		
	}
	
	class BufferedOutputStreamCloseException extends IOException {
		private static final long serialVersionUID = 1L;
	}

}
