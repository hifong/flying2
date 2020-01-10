package com.flying.common.annotation.handler;

import com.flying.common.annotation.MethodAuthentication;
import com.flying.framework.context.ServiceContext;
import com.flying.framework.data.Data;
import com.flying.framework.security.Principal;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;

public class MethodAuthenticationHandler implements ServiceHandler<MethodAuthentication>{
	
	@Override
	public Data handle(MethodAuthentication annotation, Data request, ServiceHandlerContext context)
					throws Exception {
		final Principal p = ServiceContext.getContext().getPrincipal();
		final String serviceId = context.getServiceId()+":"+context.getMethodName();
		
		if(p == null) 
			throw new AuthenticationException(p, "Principal is null!", context.getModule().getId(), serviceId);
		
		if(annotation.loginOnly() || p.isRole(annotation.roles()) || p.hasPermission(annotation.permissions()))
			context.doChain(request);
		
		throw new AuthenticationException(p, "Principal not allow!", context.getModule().getId(), serviceId, annotation.roles(), annotation.permissions());
		
	}
}
