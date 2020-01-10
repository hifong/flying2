package com.flying.framework.context;

import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.flying.common.util.Utils;
import com.flying.framework.module.LocalModule;
import com.flying.framework.security.Principal;

/**
 * @author wanghaifeng
 *
 */
public class ServiceContext {
	private final static ThreadLocal<ServiceContext> local = new ThreadLocal<ServiceContext>();
	private final static AtomicLong COUNTER = new AtomicLong(1000000);
	private final Principal principal;
	private final LocalModule module;
	private final String uuid;
	private String remoteModuleId;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private Stack<ServiceInfo> services = new Stack<ServiceInfo>();
	private InvokeType invokeType = InvokeType.Local;
	private final Map<String, Object> tempVariables = Utils.newHashMap();
	
	private final long startTime;
	
	public static ServiceContext getContext() {
		ServiceContext c = local.get();
		if(c == null) {
			return createLocalInvokingContext(null, null, null, null);
		}
		return c;
	}
	
	public static ServiceContext createLocalInvokingContext(LocalModule module, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		ServiceContext ctx = new ServiceContext(module, principal, UUID.randomUUID().toString().replaceAll("-", ""));
		ctx.invokeType = InvokeType.Local;
		ctx.request = request;
		ctx.response = response;
		local.set(ctx);
		return ctx;
	}
	
	public static ServiceContext createRemoteInvokingContext(LocalModule module, String remoteModuleId, Principal principal, String uuid) {
		ServiceContext ctx = new ServiceContext(module, principal, uuid);
		ctx.invokeType = InvokeType.Remote;
		ctx.remoteModuleId = remoteModuleId;
		local.set(ctx);
		return ctx;
	}
	
	private ServiceContext(LocalModule module, Principal principal, String uuid) {
		this.module = module;
		this.principal = principal;
		this.uuid = uuid;
		this.startTime = System.currentTimeMillis();
	}
	
	public LocalModule getModule() {
		return module;
	}

	public String getRemoteModuleId() {
		return remoteModuleId;
	}

	public long getStartTime() {
		return startTime;
	}

	public String getUuid() {
		return uuid;
	}

	public Principal getPrincipal() {
		return principal;
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public InvokeType getInvokeType() {
		return invokeType;
	}

	public enum InvokeType {
		Local, Remote
	}

	public ServiceInfo getCurrentServiceInfo() {
		if(this.services.isEmpty())
			return null;
		else
			return services.peek();
	}
	
	public long getTransactionId() {
		ServiceInfo s = this.getCurrentServiceInfo();
		return s == null?0:s.tid;
	}
	
	public void push(String moduleId, String serviceId) {
		this.services.push(new ServiceInfo(moduleId, serviceId));
	}
	
	public ServiceInfo pop() {
		return this.services.pop();
	}
	
	public Stack<ServiceInfo> getServices() {
		return this.services;
	}
	
	public Map<String, Object> getTempVariables() {
		return tempVariables;
	}

	public class ServiceInfo {
		public final String moduleId;
		public final String serviceId;
		public final long tid = COUNTER.getAndIncrement();
		public final long startTime = System.currentTimeMillis();
		
		ServiceInfo(String moduleId, String serviceId) {
			this.moduleId = moduleId;
			this.serviceId = serviceId;
		}
	}
}
