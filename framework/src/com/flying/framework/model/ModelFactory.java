package com.flying.framework.model;

import java.util.Map;

import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;

public class ModelFactory {
	
	public static <T extends Data> T createModelInstance(Class<T> clazz) {
		return createModelInstance(null, clazz);
	}
	
	public static <T extends Data> T createModelInstance(Class<T> clazz, Map<String, Object> data) {
		return createModelInstance(null, clazz, data);
	}
	
	public static <T extends Data> T createModelInstance(Class<T> clazz, Object ... params) {
		return createModelInstance(null, clazz, params);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Data> T createModelInstance(LocalModule module, Class<T> clazz) {
		if(clazz == Data.class) {
			return (T) new Data();
		} else {
			return new ModelProxy<T>(module, clazz).getInstance();
		}
	}
	
	public static <T extends Data> T createModelInstance(LocalModule module, Class<T> clazz, Map<String, Object> data) {
		T result = createModelInstance(module, clazz);
		if(data != null) {
			data.forEach((k, v) -> result.put(k, v));
		}
		return result;
	}
	
	public static <T extends Data> T createModelInstance(LocalModule module, Class<T> clazz, Object ... params) {
		T result = createModelInstance(module, clazz);
		if(params != null) {
			for(int i=0; i < params.length / 2; i++) {
				if(2 * i  + 1 >= params.length) break;
				String k = (String)params[2 * i];
				Object v = params[ 2* i + 1];
				result.put(k, v);
			}
		}
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(ModelFactory.createModelInstance(Data.class));
	}
}
