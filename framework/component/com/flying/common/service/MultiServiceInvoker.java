package com.flying.common.service;

import java.util.Map;
import java.util.Set;

import com.flying.common.annotation.Transaction;
import com.flying.common.log.Logger;
import com.flying.common.util.Codes;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.MethodInfo;
import com.flying.framework.context.ServiceContext;
import com.flying.framework.data.Data;
import com.flying.framework.exception.AppException;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.AbstractService;

@SuppressWarnings("unchecked")
public class MultiServiceInvoker extends AbstractService {
	private final static Logger log = Logger.getLogger(MultiServiceInvoker.class);

	@MethodInfo("调用复合接口")
	public Data invoke(Data data) {
		Data authData = data.remove("MultiServiceInvoker.authData");
		int count = data.getInt("count", 0);
		boolean transaction = data.getBoolean("transaction", false);
		Map<String, Object>[] services = data.getMaps("services");
		if(count ==0 || services == null || count != services.length)
			return new Data(Codes.CODE, Codes.FAIL);
		//
		Map<String, Object>[] results;
		if(transaction)
			results = this.service(services, authData);
		else
			results = this.query(services, authData);
		return new Data("count", count, "results", results);
	}

	protected Map<String, Object>[] query(Map<String, Object>[] services, Data authData) {
		Map<String, Object>[] results = new Map[services.length];
		for(int i=0; i< services.length; i++) {
			Map<String, Object> service = services[i];
			String serviceId 			= (String)service.get("serviceId");
			Map<String, Object> params 	= (Map<String, Object>)service.get("params");
			
			Data input = new Data(params);
			Map<String, Object> result = Utils.newHashMap();
			result.put("result", formatData(invokeQuery(serviceId, input, authData)));
			result.put("tid", service.get("tid"));
			result.put("serviceId", serviceId);
			results[i] = result;
		}
		return results;
	}

	@Transaction
	protected Map<String, Object>[] service(Map<String, Object>[] services, Data authData) {
		Map<String, Object>[] results = new Map[services.length];
		for(int i=0; i< services.length; i++) {
			Map<String, Object> service = services[i];
			String serviceId 			= (String)service.get("serviceId");
			String tid 					= (String)service.get("tid");
			Map<String, Object> params 	= (Map<String, Object>)service.get("params");
			
			Data input = new Data(params);
			Map<String, Object> result = Utils.newHashMap();
			result.put("result", formatData(invokeService(serviceId, input, authData)));
			result.put("tid", tid);
			result.put("serviceId", serviceId);
			results[i] = result;
		}
		return results;
	}
	
	private Data invokeQuery(String serviceId, Data input, Data authData) {
		try {
			this.auth(serviceId, authData);
			Data data = module.invoke(serviceId, input);
			if(!data.contains(Codes.CODE)) data.put(Codes.CODE, Codes.SUCCESS);
			return data;
		} catch (AppException e) {
			log.error("MultiServiceInvoker.invoke(" + serviceId +")", e);
			return new Data(Codes.CODE, e.getErrorCode(), Codes.MSG, e.getMessage());
		} catch (Exception e) {
			log.error("MultiServiceInvoker.invoke(" + serviceId +")", e);
			Data data = new Data(Codes.CODE, Codes.FAIL, Codes.MSG, e.getMessage());
			return data;
		}
	}
	
	private Data invokeService(String serviceId, Data input, Data authData) {
		this.auth(serviceId, authData);
		Data data = module.invoke(serviceId, input);
		if(!data.contains(Codes.CODE)) data.put(Codes.CODE, Codes.SUCCESS);
		return data;
	}
	
	private void auth(String serviceId, Data authData) {
		if(authData == null) return;
		boolean authTokenResult = authData.getBoolean("authTokenResult", false);
		Set<String> directAccessServices = authData.get("directAccessServices");
		if(!directAccessServices.contains(serviceId) && !authTokenResult)
			throw new AppException("Token鉴权失败", String.valueOf(Codes.AUTH_FAIL));
	}

	protected Data formatData(Data data) {
		Map<String, Object> head = Utils.newHashMap();
		Map<String, Object> body = Utils.newHashMap();
		int status = data.getInt(Codes.CODE, 0);
		final int old_status = status;
		data.remove(Codes.CODE);
		if(status == 0) 
			status = 1;
		else if(status == 1)
			status = 0;
		head.put("status", status);
		if(data.contains(Codes.MSG)) {
			head.put("message", data.remove(Codes.MSG));
		} else {
			LocalModule m = ServiceContext.getContext() == null?null:ServiceContext.getContext().getModule();
			if(m != null && m.getModuleConfig().getConfigs().containsKey("msg." + old_status)) {
				head.put("message", m.getModuleConfig().getConfig("msg." + old_status));
			}
		}
		if(data.contains(Codes.EFFECT_ROWS))
			head.put(Codes.EFFECT_ROWS, data.remove(Codes.EFFECT_ROWS));
		if(data.contains("metadata"))
			head.put("metadata", data.remove("metadata"));
		body.putAll(data.getValues());

		Map<String, Object> result = Utils.newHashMap();
		result.put("head", head);
		result.put("data", body);
		return new Data(result);
	}
}
