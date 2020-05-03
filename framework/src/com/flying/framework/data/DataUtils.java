package com.flying.framework.data;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;

import com.flying.common.util.BeanUtils;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.Param;
import com.flying.framework.context.ServiceContext;
import com.flying.framework.module.LocalModule;

/**
 * @author wanghaifeng
 * 
 */
public class DataUtils {
	private final static int MAX_DEEPTH = 6;
	public static Data convert(LocalModule module, HttpServletRequest request) throws IOException {
		@SuppressWarnings("unchecked")
		DataConverter<HttpServletRequest> dc = module.getDataConverter(HttpServletRequest.class.getName());
		return dc.convert(request);
	}

	/**
	 * @param data
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static <T> T convert(Data data, Class<T> type) throws Exception {
		T t = type.newInstance();
		return deserialize(data, t);
	}

	@SuppressWarnings("rawtypes")
	public static <T> T deserialize(Data data, T input) {
		if(input == null) return null;
		Class t = input.getClass();
		Field[] fields = t.getFields();
		
		Arrays.stream(fields).forEach(f -> {
			Param p = f.getAnnotation(Param.class);
			String key = p == null? f.getName(): p.value();
			Object value = data.get(key);
			try {
				PropertyUtils.setProperty(input, f.getName(), value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		return input;
	}
	
	public static Data serialize(Object input)  {
		if(input == null) return null;
		Data data = new Data();
		Field[] fields = input.getClass().getDeclaredFields();
		for(Field f: fields) {
			Param p = f.getAnnotation(Param.class);
			if(p == null) continue;
			try {
				data.put(p.value(), PropertyUtils.getProperty(input, f.getName()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object getValue(Object input) {
		if(input == null) {
			return null;
		} 
		Class t = input.getClass();
		if(input instanceof List) {
			List list = (List)input;
			List result = new ArrayList(list.size());
			list.stream().forEach(x ->{
				try {
					result.add(getValue(x));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			return result;
		} else if(input instanceof Set) {
			Set set = (Set)input;
			Set result = new HashSet(set.size());
			set.stream().forEach(x ->{
				try {
					result.add(getValue(x));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			return result;
		} else if(input instanceof Map) {
			Map map = (Map)input;
			Map result = new HashMap(map.size());
			map.entrySet().stream().forEach(x -> {
				Entry e = (Entry)x;
				try {
					Object key = getValue(e.getKey());
					Object value = getValue(e.getValue());
					result.put(key, value);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
			return result;
		} else if(t.getName().startsWith("java")) {
			return input;
		} else {
			Field[] fields = t.getFields();
			Map<String, Object> result = Utils.newHashMap();
			Arrays.stream(fields).forEach(f -> {
				Param p = f.getAnnotation(Param.class);
				String key = p == null? f.getName(): p.value();
				try {
					Object value = f.get(input);
					result.put(key, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			return result;
		}
	}
	

	public static List<Object> toList(@SuppressWarnings("rawtypes") Collection c) {
		return toList(c, 0, MAX_DEEPTH);
	}

	@SuppressWarnings("rawtypes")
	public static List<Object> toList(Collection c, final int deepth, final int maxDeepth) {
		if(c == null || deepth >= maxDeepth) return null;
		List<Object> list = Utils.newArrayList();
		for(Object o: c) {
			if(o instanceof Data || o instanceof Data) {
				list.add(toMap(o, deepth + 1, maxDeepth));
			} else if(o instanceof Collection) {
				list.add(toList((Collection)o, deepth + 1, maxDeepth));
			} else {
				list.add(o);
			}
		}
		return list;
	}

	public static Map<String, Object> toMap(Object o) {
		return toMap(o, 0, MAX_DEEPTH);
	}
	
	@SuppressWarnings({ "rawtypes"})
	public static Map<String, Object> toMap(Object o, final int deepth, final int maxDeepth) {
		if(ServiceContext.getContext() != null)
			ServiceContext.getContext().getTempVariables().put("source.FromToJson", "true");
		if(o == null || deepth >= maxDeepth) return null;
		Map<String, Object> result = Utils.newHashMap();
		if (o instanceof Data) {
			Class dClass = o.getClass();
			if(dClass.getName().indexOf("CGLIB") >= 0) {
				dClass = o.getClass().getSuperclass();
			}
			Field[] fields = dClass.getDeclaredFields();
			PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(dClass);
			for(PropertyDescriptor pd: pds) {
				final String property = pd.getName();
				if("class".equals(property) || "empty".equals(property) || "callbacks".equals(property) || "values".equals(property)) continue;
				Param p = null;
				for(Field f: fields) {
					if(pd.getName().equals(f.getName())) {
						p = f.getAnnotation(Param.class);
						break;
					}
				}
				String key = p == null?pd.getName(): p.value();
				if(pd == null || pd.getReadMethod() == null) continue;
				
				Object value = null;
				try {
					value = pd.getReadMethod().invoke(o, new Object[]{});
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(value instanceof Data || value instanceof Map) {
					result.put(key, toMap(value, deepth + 1, maxDeepth));
				} else if (value instanceof Collection) {
					result.put(key, toList((Collection) value, deepth + 1, maxDeepth));
				} else {
					result.put(key, value);
				}
			}
		} else if (o instanceof Map) {
			Map<String, Object> dm = (Map<String, Object>) o;
			for (String key : dm.keySet()) {
				Object value = dm.get(key);
				if (value instanceof Data || value instanceof Map) {
					result.put(key, toMap(value, deepth + 1, maxDeepth));
				} else if (value instanceof Collection) {
					result.put(key, toList((Collection) value, deepth + 1, maxDeepth));
				} else {
					result.put(key, value);
				}
			}
		}
		return result;
	}
	
}
