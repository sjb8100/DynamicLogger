package com.dc.logger.dynamic.controller.factory;

import com.dc.logger.dynamic.controller.IDynamicLoggerController;
import com.dc.logger.dynamic.logger.IDynamicLogger;

/**
 * 
 * 注意：如果是重要的日志，请不要使用缓存 或者 该日志不允许关闭，
 * 因为BufferedOutputStream即使被关闭也可以write数据，所以有几率部分日志没有写到文件中
 * （后面会对此加以改善）
 * 
 * @author Daemon
 *
 */
public interface ILoggerFactory {

	/**
	 * 注意：如果是重要的日志，请不要使用缓存 或者 该日志不允许关闭，
	 * 因为BufferedOutputStream即使被关闭也可以write数据，所以有几率部分日志没有写到文件中
	 * （后面会对此加以改善）
	 * 
	 * @param controller
	 * @param targetName
	 * @return
	 */
	IDynamicLogger getNewLogger(IDynamicLoggerController controller, String targetName);
}
