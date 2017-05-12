package com.dc.logger.dynamic.logger.rename.daily;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

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
public class DailyRollingDynamicLogger extends NameChangeDynamicLogger {

	protected long nameNeedChangeTime;
	
	protected final TimeZone timeZone;
	
	/**
	 * 注意：如果是重要的日志，请不要使用缓存 或者 该日志不允许关闭，
	 * 因为BufferedOutputStream即使被关闭也可以write数据，所以有几率部分日志没有写到文件中
	 * （后面会对此加以改善）
	 */
	public DailyRollingDynamicLogger(IDynamicLoggerController controller,
			String basePath, String targetName, String filenameExtension,
			boolean useBuffer, int bufferSize, boolean canClose,
			int maxIdleTime, boolean useMultilayerTargetNamePath,
			String multilayerTargetNamePathPrefix,
			String multilayerTargetNamePathSuffix, int eachLayerLength,
			TimeZone timeZone) {
		
		super(controller, basePath, targetName, filenameExtension, useBuffer,
				bufferSize, canClose, maxIdleTime, useMultilayerTargetNamePath,
				multilayerTargetNamePathPrefix, multilayerTargetNamePathSuffix,
				eachLayerLength);
		
		this.timeZone = timeZone;
		
		resetNameNeedChangeTime();
	}
	
	protected void resetNameNeedChangeTime() {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(timeZone);
		calendar.setTimeInMillis(System.currentTimeMillis()+5000);
		
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		nameNeedChangeTime = calendar.getTimeInMillis() + 86400000l;
	}
	
	protected String formatTime(long time) {
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		format.setTimeZone(timeZone);
		return format.format(time);
	}
	
	@Override
	protected String getNameSuffix(String targetName) {
		
		return "." + formatTime(System.currentTimeMillis());
	}
	
	@Override
	protected void beforeWrite(String msg, byte[] datas) {
		
		super.beforeWrite(msg, datas);
		
		if( System.currentTimeMillis() >= nameNeedChangeTime ) {
			
			changeName(msg, datas);
		}
		
	}
	
	@Override
	public void changeName(String msg, byte[] datas) {
		
		nameChangeLock.lock();
		try {
			
			if( System.currentTimeMillis() >= nameNeedChangeTime ) {
				
				OutputStream oldOut = this.out;
				
				resetNameNeedChangeTime();
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
