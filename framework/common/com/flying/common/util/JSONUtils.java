package com.flying.common.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.flying.framework.data.Data;

public abstract class JSONUtils {
	static {
		JSONObject.DEFFAULT_DATE_FORMAT="yyyy-MM-dd HH:mm:ss";
	}
	
	public static JSONObject toJSONObject(String js) {
		JSONObject jo = JSONObject.parseObject(js);
		return jo;
	}
	
	public static JSONArray toJSONArray(String js) {
		JSONArray ja = JSONArray.parseArray(js);
		return ja;
	}
	
	public static Map<String, Object> toMap(String js) {
		JSONObject jo = JSONObject.parseObject(js);
		return toMap(jo);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object>[] toArray(String js) {
		JSONArray ja = JSONArray.parseArray(js);
		Map<String, Object>[] res = new Map[ja.size()];
		for(int i=0; i< res.length; i++) {
			res[i] = toMap(ja.getJSONObject(i));
		}
		return res;
	}

	public static List<Object> toList(JSONArray array) {
		List<Object> list = Utils.newArrayList();
		for(int i=0; i< array.size(); i++) {
			Object o = array.get(i);
			if(o instanceof JSONObject) {
				list.add(toMap(array.getJSONObject(i)));
			} else {
				list.add(o);
			}
		}
		return list;
	}
	
	public static Map<String, Object> toMap(JSONObject jo) {
		Map<String, Object> m = Utils.newHashMap();
		for(@SuppressWarnings("rawtypes") Iterator it = jo.keySet().iterator(); it.hasNext();) {
			String key = it.next().toString();
			Object obj = jo.get(key);
			if(obj instanceof String) {
				m.put(key, jo.getString(key));
			} else if(obj instanceof JSONObject){
				m.put(key, toMap(jo.getJSONObject(key)));
			} else if(obj instanceof JSONArray) {
				m.put(key, toList(jo.getJSONArray(key)));
			}
		}
		return m;
	}
	
	public static Object[] toArray(JSONArray ja) {
		if(ja == null) return null;
		Object[] res = new Object[ja.size()];
		for(int i=0; i< ja.size(); i ++) {
			Object obj = ja.get(i);
			if(obj instanceof JSONObject)
				res[i] = toMap(ja.getJSONObject(i));
			else if(obj instanceof JSONArray) 
				res[i] = toArray((JSONArray)obj);
			else
				res[i] = obj;
		}
		return res;
	}
	
	public static String toJSONString(Object result) {
		
		if(result == null) return null;
		if(result instanceof Collection || result.getClass().isArray()) {
			return JSONArray.toJSONString(result, SerializerFeature.WriteMapNullValue,SerializerFeature.DisableCircularReferenceDetect,
					SerializerFeature.WriteDateUseDateFormat);
		} else {
			return JSONObject.toJSONString(result, SerializerFeature.WriteMapNullValue,SerializerFeature.DisableCircularReferenceDetect,
					SerializerFeature.WriteDateUseDateFormat);
		}
	}
}
