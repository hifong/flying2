package com.flying.common.annotation.handler;

import com.flying.common.annotation.ServiceResultDecorate;
import com.flying.common.util.Codes;
import com.flying.common.util.ServiceHelper;
import com.flying.common.util.StringUtils;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;

public class ServiceResultDecorateHandler implements ServiceHandler<ServiceResultDecorate> {
	
	@Override
	public Data handle(ServiceResultDecorate annotation, Data request, ServiceHandlerContext context) throws Exception {
		final LocalModule module = context.getModule();
		final String serviceId = context.getServiceId();
		final String methodName = context.getMethodName();
		
		Data result = context.doChain(request);
		//
		final String mid 	= StringUtils.isEmpty(annotation.moduleId())?module.getId(): annotation.moduleId();
		final String sid 	= StringUtils.isEmpty(annotation.serviceId())?serviceId: annotation.serviceId();
		final String mn 	= StringUtils.isEmpty(annotation.methodName())?methodName: annotation.methodName();
		Data result2 = ServiceHelper.invoke(mid, sid + ":" + mn, result);
		if(result.contains(Codes.CODE)) 
			result2.put(Codes.CODE, result.get(Codes.CODE));
		if(result.contains(Codes.MSG)) 
			result2.put(Codes.MSG, result.get(Codes.MSG));
		//
		return result2;
	}

}
