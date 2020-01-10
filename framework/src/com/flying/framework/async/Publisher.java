package com.flying.framework.async;

import com.flying.common.log.Logger;
import com.flying.framework.application.Application;
import com.flying.framework.async.simple.NoResultInThreadPool;
import com.flying.framework.data.Data;

public class Publisher {
	private static Logger logger = Logger.getLogger(Publisher.class);
	private static String producer = Application.getInstance().getAsyncProducer();

	private static Processor processor;
	
	static {
		try {
			processor = (Processor)Class.forName(producer).newInstance();
			logger.info("EventProducer init by " + producer);
		} catch (Exception e) {
			logger.warn("EventProducer fail to init by " + producer + " using default " + NoResultInThreadPool.class.getName());
			processor = new NoResultInThreadPool();
		}
	}

	public static Data publish(String moduleId, String serviceId, Data data) {
		return processor.process(moduleId, serviceId, data);
	}
	
	public static Processor getEventProducer() {
		return processor;
	}
}
