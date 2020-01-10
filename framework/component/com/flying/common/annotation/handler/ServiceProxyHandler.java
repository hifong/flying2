package com.flying.common.annotation.handler;

import com.flying.common.annotation.ServiceProxy;
import com.flying.common.util.Codes;
import com.flying.common.util.ServiceHelper;
import com.flying.common.util.StringUtils;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;

public class ServiceProxyHandler implements ServiceHandler<ServiceProxy> {
	
	@Override
	public Data handle(ServiceProxy annotation, Data request, ServiceHandlerContext context) throws Exception {
		final LocalModule module = context.getModule();
		final String serviceId = context.getServiceId();
		final String methodName = context.getMethodName();
		
		Data result = null;

		if (annotation.position() == ServiceProxy.Position.after_body) {
			result = context.doChain(request);
			request.merge(result, true);
			if (result != null && result.contains(Codes.CODE))
				return result;
		}
		if(result == null) result = new Data();
		//
		final String mid 	= StringUtils.isEmpty(annotation.moduleId())?module.getId(): annotation.moduleId();
		final String sid 	= StringUtils.isEmpty(annotation.serviceId())?serviceId: annotation.serviceId();
		final String mn 	= StringUtils.isEmpty(annotation.methodName())?methodName: annotation.methodName();
		
		if(annotation.async()) {
			ServiceHelper.invokeAsync(mid, sid + ":" + mn, request);
			result = new Data(Codes.CODE, Codes.SUCCESS);
		} else {
			result = ServiceHelper.invoke(mid, sid + ":" + mn, request);
		}
		//
		if (annotation.position() == ServiceProxy.Position.before_body) {
			result.putAll(context.doChain(request));
			request.merge(result, true);
		}
		
		return result;
	}

}
