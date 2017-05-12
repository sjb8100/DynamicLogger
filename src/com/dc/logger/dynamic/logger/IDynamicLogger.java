package com.dc.logger.dynamic.logger;

public interface IDynamicLogger {
	
	String getTargetName();

	void log(String msg);
	
	
	void start();
	
	boolean closeAble();
	
	boolean isClose();
	
	void close();
	
	boolean flushAble();
	
	void flush();
	
	
	
	
}
