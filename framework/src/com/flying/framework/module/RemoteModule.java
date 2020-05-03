package com.flying.framework.module;

import com.flying.common.log.Logger;
import com.flying.framework.application.Application;
import com.flying.framework.data.Data;
import com.flying.framework.remote.RemoteServiceInvokerHelper;

/**
 * 远程模块
 * @author wanghaifeng
 *
 */
public class RemoteModule extends Module {
	private final static Logger logger = Logger.getLogger(RemoteModule.class);
	private final String remoteServiceInvoker;
	
	public static RemoteModule newInstance(String id, String path, Data configs) {
		RemoteModule m = new RemoteModule(id, path, configs);
		return m;
	}

	private RemoteModule(String id, String path, Data configs) {
		super(id, path, configs);
		remoteServiceInvoker = Application.getInstance().getConfigValue("remoting", "invoker");
	}

	public ModuleLocation getLocation() {
		return ModuleLocation.REMOTE;
	}

	public Data invoke(String serviceId, Data request) {
		return RemoteServiceInvokerHelper.invoke(remoteServiceInvoker, id, serviceId, request);
	}

	public String getRemoteServiceInvoker() {
		return remoteServiceInvoker;
	}

	@Override
	public void fireEvents(String eventType) {
		logger.debug("Module " + this.id + "("+this.path+")" + " fireEvents unimplements!");
	}

}
