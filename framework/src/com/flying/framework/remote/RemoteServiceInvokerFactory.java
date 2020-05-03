package com.flying.framework.remote;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.flying.common.util.Utils;
import com.flying.framework.application.Application;
import com.flying.framework.exception.ObjectNotFoundException;

public abstract class RemoteServiceInvokerFactory {
	private static final Map<String, RemoteServiceInvoker> invokers = Utils.newHashMap();
	
	public static RemoteServiceInvoker getRemoteServiceInvoker(String type) {
		if(invokers.containsKey(type))
			return invokers.get(type);
		
		String className = Application.getInstance().getConfigValue("remoting", type);
		if(StringUtils.isEmpty(className))
			className = Application.getInstance().getConfigValue("remoting", "hessian");
		try {
			return (RemoteServiceInvoker)Class.forName(className).newInstance();
		} catch (Exception e) {
			throw new ObjectNotFoundException(e, type, className);
		}
	}
}
