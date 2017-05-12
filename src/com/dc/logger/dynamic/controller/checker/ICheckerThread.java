package com.dc.logger.dynamic.controller.checker;

public interface ICheckerThread extends Runnable {
	
	void addChecker(IChecker checker);
	
}
