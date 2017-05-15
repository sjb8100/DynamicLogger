package com.dc.logger.dynamic.logger;

import java.io.IOException;

public interface IDynamicLogger {
	
	String getTargetName();

	void log(String msg);
	
	
	void start();
	
	void startAndLogAndClose(byte[] datas) throws IOException;
	
	boolean closeAble();
	
	boolean isClose();
	
	void close();
	
	boolean flushAble();
	
	void flush();
	
	
	
	
}
