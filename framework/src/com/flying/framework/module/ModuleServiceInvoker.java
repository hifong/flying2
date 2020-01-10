package com.flying.framework.module;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.flying.common.log.Logger;
import com.flying.common.util.Codes;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.Param;
import com.flying.framework.context.ServiceContext;
import com.flying.framework.data.Data;
import com.flying.framework.exception.AppException;
import com.flying.framework.exception.ServiceException;
import com.flying.framework.exception.ServiceNotFoundException;
import com.flying.framework.util.ServiceMethodUtils;
import com.flying.framework.util.ServiceMethodUtils.MethodInfo;

@SuppressWarnings("rawtypes")
class ModuleServiceInvoker {
	private final static Logger logger = Logger.getLogger(ModuleServiceInvoker.class);

	private final static String[] 	EMPTY_STRING_ARRAY 	= new String[0];
	private final static long[] 	EMPTY_LONG_ARRAY 	= new long[0];
	private final static int[] 		EMPTY_INT_ARRAY 	= new int[0];
	private final static float[] 	EMPTY_FLOAT_ARRAY 	= new float[0];
	private final static double[] 	EMPTY_DOUBLE_ARRAY 	= new double[0];
	private final static boolean[] 	EMPTY_BOOLEAN_ARRAY = new boolean[0];
	private final static Date[] 	EMPTY_DATE_ARRAY 	= new Date[0];
	private final static Map[] 		EMPTY_MAP_ARRAY 	= new Map[0];

	private final static Long[] 	EMPTY_LONG_ARRAY2 	= new Long[0];
	private final static Integer[] 		EMPTY_INT_ARRAY2 	= new Integer[0];
	private final static Float[] 	EMPTY_FLOAT_ARRAY2 	= new Float[0];
	private final static Double[] 	EMPTY_DOUBLE_ARRAY2 	= new Double[0];
	
	public static Data invoke(LocalModule module, Data request, String service) {
		String[] tmps = StringUtils.split(service, ":");
		if(tmps.length != 2) {
			throw new ServiceException(String.valueOf(Codes.INVALID_PARAM), "Service must be like 'serviceId:methodName', but it is " + service);
		}
		return invoke(module, request, tmps[0], tmps[1]);
	}
	
	/**
	 * 调用本地服务
	 * @param module
	 * @param request
	 * @param serviceId
	 * @return
	 */
	public static Data invoke(LocalModule module, Data request, String serviceId, String methodName) {
		Throwable cause = null;
		if(StringUtils.isEmpty(serviceId)) return new Data();
		
		ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(module.getClassLoader());
		try {
			Object service = module.getService(serviceId);
			
			//
			MethodInfo methodInfo = ServiceMethodUtils.getMethodInfo(module, serviceId, methodName);
			final Method method = methodInfo.method;
			final Parameter[] parameters = methodInfo.parameters;
			final Object[] args = new Object[parameters.length];
			for(int i=0; request != null && i < parameters.length; i++) {
				Parameter parameter = parameters[i];
				Param param = methodInfo.params[i];
				
				Class type = parameter.getType();
				if(param == null && type == Data.class) {
					args[i] = request;
					continue;
				}
				//
				String key = param == null?parameter.getName():param.value();
				if("int".equals(type.toString())) {
					args[i] = request.getInt(key, 0);
				} else if(type == Integer.class) {
					args[i] = request.getInteger(key);
				} else if( "long".equals(type.toString()) ) {
					args[i] = request.getLong(key, 0);
				} else if(type == Long.class) {
					args[i] = request.getLong(key);
				}  else if("float".equals(type.toString())) {
					args[i] = request.getFloat(key, 0);
				} else if(type == Float.class) {
					args[i] = request.getFloat(key);
				} else if("double".equals(type.toString())) {
					args[i] = request.getDouble(key, 0);
				} else if(type == Double.class) {
					args[i] = request.getDouble(key);
				} else if(type == Date.class) {
					args[i] = request.getDate(key);
				} else if(type == Boolean.class || "boolean".equals(type.toString())) {
					args[i] = request.getBoolean(key, false);
				} else if(type == String.class) {
					args[i] = request.getString(key);
				} else if(type == Map.class) {
					args[i] = request.getMap(key);
				} else if(type == EMPTY_STRING_ARRAY.getClass()) {
					args[i] = request.getStrings(key);
				} else if(type == EMPTY_LONG_ARRAY.getClass()) {
					args[i] = request.getLongs(key, 0);
				} else if(type == EMPTY_INT_ARRAY.getClass()) {
					args[i] = request.getInts(key, 0);
				} else if(type == EMPTY_FLOAT_ARRAY.getClass()) {
					args[i] = request.getFloats(key, 0);
				} else if(type == EMPTY_DOUBLE_ARRAY.getClass()) {
					args[i] = request.getDoubles(key, 0);
				} else if(type == EMPTY_LONG_ARRAY2.getClass()) {
					args[i] = request.getLongs(key);
				} else if(type == EMPTY_INT_ARRAY2.getClass()) {
					args[i] = request.getInts(key);
				} else if(type == EMPTY_FLOAT_ARRAY2.getClass()) {
					args[i] = request.getFloats(key);
				} else if(type == EMPTY_DOUBLE_ARRAY2.getClass()) {
					args[i] = request.getDoubles(key);
				} else if(type == EMPTY_DATE_ARRAY.getClass()) {
					args[i] = request.getDates(key);
				} else if(type == EMPTY_BOOLEAN_ARRAY.getClass()) {
					args[i] = request.getBooleans(key);
				} else if(type == EMPTY_MAP_ARRAY.getClass()) {
					args[i] = request.getMaps(key);
				} else if(type == HttpServletRequest.class) {
					args[i] = ServiceContext.getContext().getRequest();
				} else if(type == HttpServletResponse.class ) {
					args[i] = ServiceContext.getContext().getResponse();
				} else {
					args[i] = request.get(key, type);
				}
			}
			return (Data)method.invoke(service, args);
			
			//
		} catch (IllegalArgumentException|IllegalAccessException|SecurityException e) {
			logger.error(serviceId+":"+methodName + " 调用失败："+e.getMessage() +";\ndetail:", e);
			throw new AppException(String.valueOf(Codes.INVALID_PARAM), serviceId+":"+methodName +" 调用失败： "+e.getMessage());
		} catch (InvocationTargetException e) {
			cause = Utils.getBusinessCause(e);
			logger.error(serviceId+":"+methodName + " 调用失败："+e.getMessage() +";\ndetail:", cause);
			if(cause instanceof AppException) {
				throw (AppException)cause;
			} else if(cause instanceof NoSuchMethodException) {
				throw new ServiceNotFoundException(serviceId);
			} else {
				throw new ServiceException(cause);
			}
		} catch (NoSuchMethodException e) {
			logger.error(serviceId+":"+methodName + " 调用失败："+e.getMessage() +";\ndetail:", e);
			throw new ServiceNotFoundException(serviceId);
		}finally{
			Thread.currentThread().setContextClassLoader(threadClassLoader);
		}
	}
	
}
