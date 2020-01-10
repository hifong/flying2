package com.flying.framework.remote;

import com.flying.framework.context.ServiceContext;
import com.flying.framework.data.Data;
import com.flying.framework.exception.AppException;
import com.flying.framework.exception.ServiceException;
import com.flying.framework.module.LocalModule;
import com.flying.framework.security.Principal;
import com.flying.framework.util.LogUtil;

public abstract class RemoteServiceInvokerHelper {
	public static Data invoke(RemoteServiceInvoker invoker, String remoteModuleId, String serviceId, Data request)
					throws Exception {

		long start = System.currentTimeMillis();
		LocalModule localModule = ServiceContext.getContext().getModule();
		Principal principal = ServiceContext.getContext().getPrincipal();

		Data response = null;
		Exception ex = null;
		try {
			return invoker.invoke(principal, localModule, remoteModuleId, serviceId, request);
		} catch (AppException e) {
			ex = e;
			throw e;
		} catch (Exception e) {
			ServiceException se = new ServiceException(e);
			ex = se;
			throw se;
		} finally {
			long end = System.currentTimeMillis();
			LogUtil.invokeLog(principal,"remote", remoteModuleId, serviceId, "RemoteInvoke", 0, (end-start), request, response, ex);
		}
	}

	public static Data invoke(String invoker, String remoteModuleId, String serviceId, Data request)
					throws Exception {
		return invoke(RemoteServiceInvokerFactory.getRemoteServiceInvoker(invoker), remoteModuleId, serviceId, request);
	}
}
