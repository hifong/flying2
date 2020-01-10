package com.flying.common.annotation.handler;

import java.util.Date;

import com.flying.common.util.Codes;
import com.flying.common.util.ServiceHelper;
import com.flying.framework.annotation.service.DefaultServiceInvokeAnnotation;
import com.flying.framework.application.Application;
import com.flying.framework.context.ServiceContext;
import com.flying.framework.data.Data;
import com.flying.framework.security.Principal;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;
import com.flying.framework.util.LogUtil;

public class DefaultServiceInvokeHandler implements ServiceHandler<DefaultServiceInvokeAnnotation> {
	
	private boolean loggerAllowed(String moduleId, String serviceId, String methodName) {
		if("admin".equals(moduleId)) return false;
		if(Codes.SERVICE_NOTICE.equals(serviceId)) return false;
		return true;
	}
	
	@Override
	public Data handle(DefaultServiceInvokeAnnotation annotation, Data request, ServiceHandlerContext context) throws Exception {
		ServiceContext currentContext = ServiceContext.getContext();
		currentContext.push(context.getModule().getId(), context.getServiceId()+":"+context.getMethodName());
		
		Principal p = currentContext.getPrincipal();
		long start = System.currentTimeMillis();
		Data result = null;
		Exception ex = null;
		try {
			result = context.doChain(request);
			//
			if(result != null)
				result.put(Codes.TID, currentContext.getTransactionId());
			//
			return result;
		} catch (Exception e) {
			ex = e;
			throw e;
		} finally {
			if(this.loggerAllowed(context.getModule().getId(), context.getServiceId(), context.getMethodName())) {
				long end = System.currentTimeMillis();
				LogUtil.invokeLog(p, "Local", context.getModule().getId(), context.getServiceId(), context.getMethodName(),
								currentContext.getTransactionId(), (end - start), request, result, ex);
				this.invokeNotice(context, currentContext, end - start, request, result, ex);
			}
			//
			currentContext.pop();
		}
	}

	private void invokeNotice(ServiceHandlerContext context, ServiceContext currentContext, 
			long consume, Data request, Data result, Throwable ex) {
		try {
			ServiceHelper.invokeAsync(context.getModule().getId(), Codes.SERVICE_NOTICE+":invokeNotice", new Data(
					"event_date",	new Date(),
					"app_id", 		Application.getInstance().getId(),
					"module_id",	context.getModule().getId(),
					"service_id",	context.getServiceId(), 
					"service_method",	context.getMethodName(), 
					"principle",		currentContext.getPrincipal(),
					"tid",				currentContext.getTransactionId(),
					"consume",			consume,
					
					"input",			request,
					"output",			result,
					"error",			ex));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
