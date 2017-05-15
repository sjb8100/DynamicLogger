package test.dc.logger.dynamic;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.dc.logger.dynamic.controller.DynamicLoggerController;
import com.dc.logger.dynamic.controller.factory.BaseLoggerFactory;
import com.dc.logger.dynamic.logger.IDynamicLogger;
import com.dc.logger.dynamic.logger.rename.daily.DailyRollingSizeConstraintDynamicLogger;

public class DailyRollingSizeConstraintDynamicLoggerTest extends BaseLoggerFactory {


	public DailyRollingSizeConstraintDynamicLoggerTest(String basePath) {
		super( basePath, "log", true, 4096, true, 30000, true, "~", "", 2 );
	}

	public static void main(String[] args) throws Exception {
		
		String basePath = ClassLoader.getSystemClassLoader().getResource("").getFile() + "../mlogs" ;
		DailyRollingSizeConstraintDynamicLoggerTest loggerFactory = new DailyRollingSizeConstraintDynamicLoggerTest( basePath );
		
		final DynamicLoggerController controller = new DynamicLoggerController(loggerFactory);
		
		controller.addCloseAbleChecker();
		controller.addFlushAbleChecker();
		controller.startCheckerThread(3000);
		
		new Thread(new Runnable() {
			
			public void run() {
				
				for( int i=0; i<10000; i++ ) {
					
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					controller.log("ccdd00", i+"\n");
				}
			}
		}).start();
		
		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				
//				for( int i=0; i<10000; i++ ) {
//					
//					try {
//						TimeUnit.SECONDS.sleep(1);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					controller.log("ccdd00", i+"\n");
//				}
//			}
//		}).start();
	}
	
	@Override
	public IDynamicLogger getNewLogger(String targetName) {
		
		return new DailyRollingSizeConstraintDynamicLogger(basePath, targetName, filenameExtension, 
				useBuffer, bufferSize, canClose, maxIdleTime, useMultilayerTargetNamePath, multilayerTargetNamePathPrefix, multilayerTargetNamePathSuffix, eachLayerLength,
				TimeZone.getDefault(), 10240);
	}
}
