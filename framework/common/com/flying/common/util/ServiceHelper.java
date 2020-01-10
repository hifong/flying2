package com.flying.common.util;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

import com.flying.framework.annotation.MethodInfo;
import com.flying.framework.annotation.Param;
import com.flying.framework.application.Application;
import com.flying.framework.async.Publisher;
import com.flying.framework.data.Data;
import com.flying.framework.data.DataSerializable;
import com.flying.framework.data.DataUtils;
import com.flying.framework.module.LocalModule;

public abstract class ServiceHelper {
	private static Map<String, Map<String, Map<String, Object>>> cache = Utils.newHashMap();

	/**
	 * 同步服务调用
	 * @param moduleId：		模块ID
	 * @param serviceId：	服务ID
	 * @param bean：		请求POJO对象
	 * @throws Exception
	 */
	public static Data invoke(String moduleId, String serviceId, Object bean) throws Exception {
		Data data = null;
		if(bean instanceof DataSerializable) {
			data = ((DataSerializable)bean).serialize();
		} else {
			data = DataUtils.serialize(bean);
		}
		return invoke(moduleId, serviceId, data);
	}

	/**
	 * 同步服务调用
	 * @param moduleId：		模块ID
	 * @param serviceId：	服务ID
	 * @param request：		请求数据
	 * @throws Exception
	 */
	public static Data invoke(String moduleId, String serviceId, Data request) throws Exception {
		return Application.getInstance().getModules().getModule(moduleId).invoke(serviceId, request);
	}
	
	/**
	 * 异步服务调用
	 * @param moduleId：		模块ID
	 * @param serviceId：	服务ID
	 * @param bean：		请求POJO对象
	 * @throws Exception
	 */
	public static void invokeAsync(String moduleId, String serviceId, Object bean) throws Exception {
		Data data = null;
		if(bean instanceof DataSerializable) {
			data = ((DataSerializable)bean).serialize();
		} else {
			data = DataUtils.serialize(bean);
		}
		invokeAsync(moduleId, serviceId, data);
	}
	
	/**
	 * 异步服务调用
	 * @param moduleId：		模块ID
	 * @param serviceId：	服务ID
	 * @param request：		请求数据
	 * @throws Exception
	 */
	public static void invokeAsync(String moduleId, String serviceId, Data request) throws Exception {
		Publisher.publish(moduleId, serviceId, request);
	}
	
	/**
	 * @param moduleId
	 * @param serviceId
	 * @return <methodName:<methodProperty:methodPropertyValue>>
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, Map<String, Object>> getServiceMethods(String moduleId, String serviceId) {
		final String cacheKey = moduleId + "." + serviceId;
		if(cache.containsKey(cacheKey)) return cache.get(cacheKey);
		
		synchronized(ServiceHelper.class) {
			if(cache.containsKey(cacheKey)) return cache.get(cacheKey);
			//
			final LocalModule module = Application.getInstance().getModules().getLocalModule(moduleId);
			final Object service = module.getService(serviceId);
			Class serviceClass = service.getClass();
			if(serviceClass.getName().indexOf("CGLIB") >= 0) {
				serviceClass = serviceClass.getSuperclass();
			}
			//
			Map<String, Map<String, Object>> methods = Utils.newHashMap();
			Method[] ms = serviceClass.getMethods();
			for(Method m: ms ) {
				String name = m.getName();
				if("equals".equals(name) || "hashCode".equals(name) || "getClass".equals(name) || "toString".equals(name) || 
						"notify".equals(name) || "notifyAll".equals(name) || "wait".equals(name)) continue;
				
				if(!Data.class.isAssignableFrom(m.getReturnType())) continue;
				
				Map<String, Object> methodInfo = Utils.newHashMap();
				methodInfo.put("name", m.getName());
				
				if(m.getAnnotation(MethodInfo.class) != null) {
					methodInfo.put("MethodInfo", m.getAnnotation(MethodInfo.class));
				}
				methodInfo.put("annotations", m.getAnnotations());
				methodInfo.put("returnType", m.getReturnType());
				//
				List<Map<String, Object>> params = Utils.newArrayList();
				Parameter[] ps = m.getParameters();
				for(Parameter p: ps) {
					Map<String, Object> param = Utils.newHashMap();
					param.put("type", p.getType().getName());
					
					Param anno = p.getAnnotation(Param.class);
					param.put("param", anno);
					param.put("name", anno == null? p.getName(): anno.value());
					if(anno == null && p.getType() == Data.class) 
						param.put("name", "request");
					params.add(param);
				}
				methodInfo.put("params", params);
				//
				methods.put(m.getName(), methodInfo);
			}
			cache.put(cacheKey, methods);
			//
			return methods;
		}
	}
}
