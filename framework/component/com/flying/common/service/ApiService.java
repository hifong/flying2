package com.flying.common.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.flying.common.util.Codes;
import com.flying.common.util.StringUtils;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.ConstantEnum;
import com.flying.framework.annotation.MethodInfo;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.application.Application;
import com.flying.framework.data.Data;
import com.flying.framework.service.AbstractService;

@Service("API")
public class ApiService extends AbstractService {
	private Map<String, List<APIMethod>> cache = Utils.newHashMap();

	@MethodInfo("查看服务信息")
	public Data getServiceApis(
			@Param(value="serviceId", required=true, desc="服务ID") String serviceId) {
		
		if(!this.cache.containsKey(serviceId)) {
			this.initServiceMethodInfo(serviceId);
		}
		return new Data(Codes.CODE, Codes.SUCCESS, Codes.ROWS, cache.get(serviceId));
	}

	@MethodInfo("查看服务接口信息")
	public Data getApiInfo(
			@Param(value="serviceId", required=true, desc="服务ID") String serviceId,
			@Param(value="methodName", required=true, desc="接口名称") String methodName) {

		if(!this.cache.containsKey(serviceId)) {
			this.initServiceMethodInfo(serviceId);
		}
		//
		List<APIMethod> apiMethods = cache.get(serviceId);
		Optional<APIMethod> om = apiMethods.stream()
				.filter(m -> methodName.equals(m.getName()))
				.findFirst();
		//
		return new Data(Codes.CODE, Codes.SUCCESS, "Method", om.get());
	}
	
	public Data testApi(
			@Param(value="serviceId", required=true, desc="服务ID") String serviceId,
			@Param(value="methodName", required=true, desc="接口名称") String methodName,
			@Param(value="parameters", required=true, desc="参数") Data parameters) throws Exception {
		return module.invoke(serviceId + ":" + methodName, parameters);
	}
	
	public Data echo(
			@Param(value="msg", required=true, desc="消息") String msg) throws Exception {
		
		return new Data(
				"hostName", 		InetAddress.getLocalHost().getHostName(),
				"hostAddress", 		InetAddress.getLocalHost().getHostAddress(),
				"applicationId", 	Application.getInstance().getId(),
				"moduleId", 		this.module.getId(),
				"serviceId", 		this.serviceConfig.getId(),
				"msg", 				"Hello, it is " + msg
			);
	}
	
	private synchronized void initServiceMethodInfo(String serviceId) {
		if(cache.containsKey(serviceId)) return;
		
		final Object service = module.getService(serviceId);
		Class<?> serviceClass = service.getClass();
		if(serviceClass.getName().indexOf("CGLIB") >= 0) {
			serviceClass = serviceClass.getSuperclass();
		}
		//
		List<APIMethod> apiMethods = Utils.newArrayList();
		Method[] ms = serviceClass.getMethods();
		for(Method m: ms ) {
			if(m.getReturnType() != Data.class) continue;
			
			MethodInfo apiMethodInfo = m.getAnnotation(MethodInfo.class);
			//
			Parameter[] parameters = m.getParameters();
			APIMethodParameter[] apiParams = new APIMethodParameter[parameters.length];
			for(int i=0; i<parameters.length; i++) {
				apiParams[i] = new APIMethodParameter(parameters[i]);
			}
			//
			Annotation[] annotations = m.getAnnotations();
			//
			final String name = m.getName();
			APIMethod apiMethod = new APIMethod(name, apiMethodInfo == null?null: apiMethodInfo.value(), annotations, apiParams);
			apiMethods.add(apiMethod);
		}
		cache.put(serviceId, apiMethods);
	}
	
	public class APIMethod {
		private final String name;
		private final String description;
		private final Annotation[] annotations;
		private final APIMethodParameter[] parameters;

		public APIMethod(String apiName, String apiDesc, Annotation[] annotations,
				APIMethodParameter[] parameters) {
			super();
			this.name = apiName;
			this.description = apiDesc;
			this.annotations = annotations;
			this.parameters = parameters;
		}
		
		public String getName() {
			return this.name;
		}
		
		public String getDescription() {
			return this.description;
		}
		
		public String[] getAnnotations() {
			return Stream.of(this.annotations)
					.filter(a -> module.getAnnotationHandler(a.annotationType().getName()) != null)
					.map(a -> a.annotationType().getName())
					.toArray(String[]::new);
		}
		
		public APIMethodParameter[] getParameters() {
			return this.parameters;
		}
	}
	
	public class APIMethodParameter {
		private final String name;
		private final Param param;
		private final Class<?> type;
		
		APIMethodParameter(Parameter p) {
			this(p.getName(), p.getAnnotation(Param.class), p.getType());
		}
		
		APIMethodParameter(String methodName, Param param, Class<?> type) {
			this.name = methodName;
			this.param = param;
			this.type = type;
		}
		
		public Class<?> getType() {
			return this.type;
		}
		
		public String getTypeName() {
			if(this.isArray())
				return this.type.getComponentType().getSimpleName()+"[]";
			else
				return this.type.getSimpleName();
		}
		
		public boolean isArray() {
			return this.type.isArray();
		}
		
		public boolean isRequired() {
			return param == null? false: param.required();
		}
		
		public String getMax() {
			return param == null? null: param.max();
		}
		
		public String getMin() {
			return param == null? null: param.min();
		}
		
		public ConstantEnum[] getEnums() {
			if(param != null && param.enumClass() != ConstantEnum.class) {
				return param.enumClass().getEnumConstants();
			} else {
				return null;
			}
		}
		
		public int maxLength() {
			return param == null? 0: param.maxlength();
		}
		
		public String getFormat() {
			return param == null? null: param.format();
		}
		
		public String getParamName() {
			if(param == null) return this.name;
			return !StringUtils.isEmpty(param.alias()) ? param.alias() : param.value();
		}
		
		public String getDescription() {
			return param == null?null: param.desc();
		}
		
	}
}
