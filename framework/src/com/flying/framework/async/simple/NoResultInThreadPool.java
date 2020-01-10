package com.flying.framework.async.simple;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.flying.common.log.Logger;
import com.flying.common.util.ServiceHelper;
import com.flying.framework.async.Processor;
import com.flying.framework.data.Data;

public class NoResultInThreadPool implements Processor{
	private final static int THREAD_POOL_SIZE = 50;
	private static Logger logger = Logger.getLogger(NoResultInThreadPool.class);
	private ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	private ConcurrentLinkedQueue<ServiceParam> queue = new ConcurrentLinkedQueue<ServiceParam>();
	
	public NoResultInThreadPool() {
		for(int i=0; i< THREAD_POOL_SIZE; i++) {
			threadPool.execute(new AsyncThread());
		}
	}
	
	@Override
	public Data process(String moduleId, String serviceId, Data data) {
		queue.add(new ServiceParam(moduleId, serviceId, data));
		return null;
	}
	
	class AsyncThread extends Thread {
		public void run() {
			while(true) {
				ServiceParam param = queue.poll();
				if(param != null) {
					try {
						ServiceHelper.invoke(param.moduleId, param.serviceId, param.data);
					} catch (Exception e) {
						logger.error("EventHandler fail, Module[" + param.moduleId +"], Service[" + param.serviceId +"], Req[" + param.data + "]", e);
					}
				} else {
					try {
						Thread.sleep(100);
					} catch(Exception e){}
				}
			}
		}
	}
	
	class ServiceParam {
		private String moduleId;
		private String serviceId;
		private Data data;
		
		ServiceParam(String moduleId, String serviceId, Data data) {
			this.moduleId = moduleId;
			this.serviceId = serviceId;
			this.data = data;
		}
	}
}
