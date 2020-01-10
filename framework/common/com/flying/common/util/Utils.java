package com.flying.common.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.flying.common.log.Logger;
import com.flying.framework.application.Application;
import com.flying.framework.module.LocalModule;

public class Utils {
    private static Logger logger = Logger.getLogger(Utils.class);

    public static String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer(obj.getClass().getName()).append("@").append(obj.hashCode());
        if (obj.getClass().isArray()) {
            int len = Array.getLength(obj);
            sb.append("[");
            for (int i = 0; i < len; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(toString(Array.get(obj, i)));
            }
            sb.append("]");
        } else {
            return obj.toString();
        }
        return sb.toString();
    }

    public static String toString2(Object obj) {
        if (obj == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        if (obj.getClass().isArray()) {
            int len = Array.getLength(obj);
            sb.append("[");
            for (int i = 0; i < len; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(toString(Array.get(obj, i)));
            }
            sb.append("]");
        } else {
            return obj.toString();
        }
        return sb.toString();
    }

    public static <K, V> Map<K, V> newHashMap(int size) {
        return new HashMap<K, V>(size);
    }

    public static <K, V> Map<K, V> newHashMap() {
        return new HashMap<K, V>(64);
    }

    public static <K, V> Map<K, V> newHashMap(K[] keys, V[] values) {
    	Map<K, V> m = new HashMap<K, V>(64);
    	int i =0;
    	for(K k: keys) {
    		m.put(k, values[i]);
    		i++;
    	}
    	return m;
    }

    public static <T> List<T> newArrayList(int size) {
        return new ArrayList<T>(size);
    }

    public static <T> List<T> newArrayList() {
        return new ArrayList<T>(64);
    }

    public static <T> Set<T> newHashSet(int size) {
        return new HashSet<T>(size);
    }

    public static <T> Set<T> newHashSet() {
        return new HashSet<T>(64);
    }

    public static <T> Collection<T> addAll(Collection<T> coll, T[] arr) {
        if ((arr == null) || (coll == null)) {
            return coll;
        }
        for (T e : arr) {
            coll.add(e);
        }
        return coll;
    }
    
    @SuppressWarnings("unchecked")
	public static <K> Map<K, List<Map<String, Object>>> group(List<Map<String, Object>> rows, String group_field) {
		Map<K, List<Map<String, Object>>> result = Utils.newHashMap();
		for(Map<String, Object> item: rows) {
			K itemKey = (K)item.get(group_field);
			if(result.containsKey(itemKey)) {
				List<Map<String, Object>> list = result.get(itemKey);
				list.add(item);
			} else {
				List<Map<String, Object>> list = Utils.newArrayList();
				list.add(item);
				result.put(itemKey, list);
			}
		}
		return result;
    }

	public static <K> List<Map<String, Object>> groupAsList(List<Map<String, Object>> rows, String group_field, String list_field) {
		Map<K, List<Map<String, Object>>> grouped = group(rows, group_field);

		List<Map<String, Object>> result = Utils.newArrayList();
		for(Entry<K, List<Map<String, Object>>> e: grouped.entrySet()) {
			K key = e.getKey();
			List<Map<String, Object>> value = e.getValue();
			
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(group_field, key);
			item.put(list_field, value);
			result.add(item);
		}
		return result;
	}

    public static Throwable getBusinessCause(final Throwable t) {
        Throwable cause = t;

        while ((cause != null)
                && (cause.getCause() != null)
                && (cause.getClass().getName().startsWith("java.lang.") || cause.getCause().getClass().getName()
                        .startsWith("java.lang."))) {
            cause = cause.getCause();
        }

        return cause;
    }

    public static String getLocalString(String fileName, String key) {
        return getLocalString(null, fileName, key, "zh");
    }

    public static String getLocalString(String moduleId, String fileName, String key, String locale) {
        LocalModule module = null;
        try {
            module = Application.getInstance().getModules().getLocalModule(moduleId);
        } catch (Exception e) {
            // TODO: handle exception
        }

        try {
            ClassLoader classLoader = null;
            if (module == null) {
                classLoader = ClassUtils.getDefaultClassLoader();
            } else {
                classLoader = module.getClassLoader();
            }
            ResourceBundle resourceBundle = ResourceBundle.getBundle(fileName, new Locale(locale), classLoader);
            return resourceBundle.getString(key);
        } catch (Exception e) {
            logger.error("Utils.getLocalString failed!", e);
            return key;
        }
    }
    
    public static String toDataBaseField(String name){
    	 if(null == name)
         {
             return null;
         }
         char c, pc = (char) 0;
         StringBuilder sb = new StringBuilder();
         for(int i = 0; i < name.length(); i++)
         {
             c = name.charAt(i);
             if(Character.isLowerCase(pc) && Character.isUpperCase(c))
             {
                 sb.append('_');
             }
             pc = c;
             sb.append(Character.toUpperCase(c));
         }
         return sb.toString();
    } 
    
    public static String escapeSecurity(String src,String escapePattern){
    	if(StringUtils.isBlank(src)){
    		return src;
    	}
    	String dest = null;
    	

    	if(StringUtils.isNotBlank(escapePattern)){
    		dest = src.replaceAll(escapePattern, "");
    	}else{
    		dest = src.replaceAll("\\(|\\)|\\{|\\}|\\[|\\]|;|\"|\'|\r|\n|<|>", "");
    	}
    	
    	if(logger.isDebugEnabled()){
    		logger.debug("Utils:escapeSecurity(" + src + "," + escapePattern + ")");
    		logger.debug("Utils:escapeSecurity result " + dest);
    	}
    	
    	return dest;
    }
}
