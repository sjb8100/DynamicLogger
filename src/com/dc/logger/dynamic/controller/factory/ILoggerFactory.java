package com.dc.logger.dynamic.controller.factory;

import com.dc.logger.dynamic.logger.IDynamicLogger;

/**
 * 当logger被关闭时，再调用log方法将会直接写入（不管useBuffer取值）到文件中（从新打开文件 写入 并关闭）
 * 
 * @author Daemon
 *
 */
public interface ILoggerFactory {

	/**
	 * 当logger被关闭时，再调用log方法将会直接写入（不管useBuffer取值）到文件中（从新打开文件 写入 并关闭）
	 * 
	 * @param controller
	 * @param targetName
	 * @return
	 */
	IDynamicLogger getNewLogger(String targetName);
}
