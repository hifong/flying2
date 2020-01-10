package com.flying.framework.async.simple;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.flying.common.log.Logger;
import com.flying.common.util.ServiceHelper;
import com.flying.framework.async.Processor;
import com.flying.framework.data.Data;
import com.flying.framework.exception.AppException;

public class WithResultInThreadPool implements Processor{
	private static Logger logger = Logger.getLogger(WithResultInThreadPool.class);
	private static ExecutorService threadPool = Executors.newFixedThreadPool(50);

	@Override
	public Data process(String moduleId, String serviceId, Data data) {
		Future<Data> future = threadPool.submit(new EventCallable(moduleId, serviceId, data));
		try {
			return future.get();
		} catch (Exception e) {
			throw new AppException(e);
		}
	}
	
	class EventCallable implements Callable<Data> {
		private String moduleId;
		private String serviceId;
		private Data data;
		
		EventCallable(String moduleId, String serviceId, Data data) {
			this.moduleId = moduleId;
			this.serviceId = serviceId;
			this.data = data;
		}
		
		@Override
		public Data call() throws Exception {
			return ServiceHelper.invoke(moduleId, serviceId, data);
		}
		
	}
}
