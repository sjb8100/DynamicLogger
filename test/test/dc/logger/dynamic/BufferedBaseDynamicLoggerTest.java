package test.dc.logger.dynamic;

import com.dc.logger.dynamic.logger.BaseDynamicLogger;

public class BufferedBaseDynamicLoggerTest {

	public static void main(String[] args) throws Exception {
		
		
		String basePath = ClassLoader.getSystemClassLoader().getResource("").getFile() + "../mlogs" ;
		BaseDynamicLogger logger = new BaseDynamicLogger(basePath, "qqaazz00", "log", true, 1024, true, 0, true, "~", "", 2);
		logger.start();
		
		logger.log("before close\n");
		
		logger.close();
		
		logger.log("after close\n");
	}
}
