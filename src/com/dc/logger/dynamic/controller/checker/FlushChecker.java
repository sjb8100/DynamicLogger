package com.dc.logger.dynamic.controller.checker;

import com.dc.logger.dynamic.logger.IDynamicLogger;

public class FlushChecker implements IChecker {

	public void check(IDynamicLogger dynamicLogger) {
		
		if( dynamicLogger.flushAble() )
			dynamicLogger.flush();
		
	}

}
