package com.dc.logger.dynamic.controller.checker;

import com.dc.logger.dynamic.controller.IDynamicLoggerController;
import com.dc.logger.dynamic.logger.IDynamicLogger;

public class CloseChecker implements IChecker {
	
	protected final IDynamicLoggerController dynamicLoggerController;
	
	public CloseChecker(IDynamicLoggerController dynamicLoggerController) {
		
		this.dynamicLoggerController = dynamicLoggerController;
	}

	public void check(IDynamicLogger dynamicLogger) {
		
		dynamicLoggerController.close(dynamicLogger.getTargetName(), dynamicLogger);
		
	}

}
