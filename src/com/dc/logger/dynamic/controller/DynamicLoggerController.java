package com.dc.logger.dynamic.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.dc.logger.dynamic.controller.checker.CloseChecker;
import com.dc.logger.dynamic.controller.checker.FlushChecker;
import com.dc.logger.dynamic.controller.checker.IChecker;
import com.dc.logger.dynamic.controller.checker.ICheckerThread;
import com.dc.logger.dynamic.controller.factory.ILoggerFactory;
import com.dc.logger.dynamic.logger.IDynamicLogger;


public class DynamicLoggerController implements IDynamicLoggerController, ICheckerThread {
	
	protected final ILoggerFactory loggerFactory;

	protected final ConcurrentHashMap<String, IDynamicLogger> targetNameMapLogger 
		= new ConcurrentHashMap<String, IDynamicLogger>();
	
	protected final ArrayList<IChecker> chekerList = new ArrayList<IChecker>();
	
	public DynamicLoggerController(ILoggerFactory loggerFactory) {

		this.loggerFactory = loggerFactory;
	}

	public void startCheckerThread(int cycleTime) {
		
		setCycleTime(cycleTime);
		
		Thread dynamicLoggerCheckerThread = new Thread(this);
		dynamicLoggerCheckerThread.setName("dynamicLoggerCheckerThread");
		dynamicLoggerCheckerThread.setDaemon(true);
		
		dynamicLoggerCheckerThread.start();
		
	}
	
	public void addCloseAbleChecker() {
		
		addChecker( new CloseChecker(this) );
	}
	
	public void addFlushAbleChecker() {
		
		addChecker( new FlushChecker() );
	}
	
	public void addChecker(IChecker checker) {
		
		chekerList.add(checker);
	}
	
	private long cycleTime = 60000;
	private void setCycleTime(int cycleTime) {
		
		this.cycleTime = cycleTime;
	}
	
	public void run() {
		
		int i;
		int length;
		long beginTime, sleepTime;
		
		long cycleTime = this.cycleTime;
		ArrayList<IChecker> chekerList = this.chekerList;
		ConcurrentHashMap<String, IDynamicLogger> targetNameMapLogger = this.targetNameMapLogger;
		
		for(;;) {
			
			beginTime = System.currentTimeMillis();
			
			try {
				
				length = chekerList.size();
				for( IDynamicLogger dynamicLogger : targetNameMapLogger.values() ) {
					
					for( i=0; i<length; i++ ) {
						
						try {
							chekerList.get(i).check(dynamicLogger);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				}
				
			} catch (Exception e) {

				e.printStackTrace();
			}
			
			sleepTime = cycleTime + beginTime - System.currentTimeMillis(); // cycleTime - ( System.currentTimeMillis() - beginTime )
			
			if( sleepTime > 0 ) {
				
				try {
					TimeUnit.MILLISECONDS.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	public void log(String targetName, String msg) {
		
		IDynamicLogger logger = getLogger(targetName);
		
		logger.log(msg);
	}
	
	public void log( String targetName, StringBuilder beforeNInfo ) {
		
		beforeNInfo.append("\n");
		log(targetName, beforeNInfo.toString());
	}
	
	public final void log( String targetName, Exception e ) {
		
		StringWriter sw=new StringWriter();  
		PrintWriter pw=new PrintWriter(sw);   
		e.printStackTrace(pw);
		pw.println();
		log( targetName, sw.toString() );
	}
	
	public IDynamicLogger getLogger(String targetName) {

		IDynamicLogger logger = targetNameMapLogger.get(targetName);
		if( logger == null ) {
			
			logger = loggerFactory.getNewLogger(this, targetName);
			
			logger.start();
			
			IDynamicLogger oldLogger = targetNameMapLogger.putIfAbsent(targetName, logger);
			
			if( oldLogger != null ) {
				
				logger.close();
				
				logger = oldLogger;
			}
			
		}
		
		return logger;
	}
	
	public void close(String targetName, IDynamicLogger dynamicLogger) {
		
		if( dynamicLogger.closeAble() ) {
			
			targetNameMapLogger.remove(targetName, dynamicLogger);
			
			if( dynamicLogger.flushAble() )
				dynamicLogger.flush();
			
			dynamicLogger.close();
			
		}
	}
	
	public void close(String targetName) {
		
		IDynamicLogger dynamicLogger = targetNameMapLogger.get(targetName);
		if( dynamicLogger != null && dynamicLogger.closeAble() ) {
			
			targetNameMapLogger.remove(targetName, dynamicLogger);
			
			if( dynamicLogger.flushAble() )
				dynamicLogger.flush();
			
			dynamicLogger.close();
			
		}
	}
	
	public void flush(String targetName) {
		
		IDynamicLogger logger = targetNameMapLogger.get(targetName);
		if( logger != null ) 
			logger.flush();
	}
	
	public void reLoggerWhenClose(String msg, String basePath, String targetName,
			String filenameExtension, boolean useBuffer, int bufferSize,
			boolean useMultilayerTargetNamePath,
			String multilayerTargetNamePathPrefix,
			String multilayerTargetNamePathSuffix, int eachLayerLength) {
		
		this.log(targetName, msg);
	}

	public int getActiveCount() {
		
		return targetNameMapLogger.size();
	}
	
	public HashSet<String> getActiveTarget() {
		
		HashSet<String> targetSet = new HashSet<String>( targetNameMapLogger.size() << 1 );
		
		for( IDynamicLogger dynamicLogger : targetNameMapLogger.values() ) {
			
			targetSet.add(dynamicLogger.getTargetName());
		}
		
		return targetSet;
	}
	

}
