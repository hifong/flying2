package com.flying.common.remote.hessian;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.caucho.hessian.server.HessianServlet;
import com.flying.common.log.Logger;
import com.flying.framework.application.Application;
import com.flying.framework.context.ServiceContext;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;
import com.flying.framework.security.Principal;

@SuppressWarnings("serial")
@WebServlet(value="/remoting")
public class HessianRemoteService extends HessianServlet implements RemoteService{
	private final static Logger logger = Logger.getLogger(HessianRemoteService.class);
	
	
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
	}
	
	public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)servletRequest;
		long start = System.currentTimeMillis();
		final String requestURI = req.getRequestURI() + (req.getQueryString() == null?"":"?" + req.getQueryString());
		
		try {
			super.service(servletRequest, servletResponse);
		} finally {
			logger.info("Access(" + (System.currentTimeMillis() - start) + ")\tURI:" + requestURI);
		}
	}

	public RemoteRequest invoke(Principal principal, String moduleId, String serviceId, RemoteRequest remoteData) throws Exception {
		long start = System.currentTimeMillis();
		try {
			Data req = remoteData.getRequest();
			LocalModule module = Application.getInstance().getModules().getLocalModule(moduleId);
			ServiceContext.createRemoteInvokingContext(module, remoteData.getFromModuleId(), principal, remoteData.getUuid());
			
			Data result = module.invoke(serviceId, req);
			remoteData.setResponse(result);
		} catch (Exception e){
			remoteData.setException(e);
		} finally {
			logger.info("RemoteInvoker(" + (System.currentTimeMillis() - start) + ")\tModuleId:" + moduleId+";ServiceId:" + serviceId);
		}
		return remoteData;
	}
}
