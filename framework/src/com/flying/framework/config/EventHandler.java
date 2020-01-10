package com.flying.framework.config;

import java.io.Serializable;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.flying.common.util.Utils;
import com.flying.framework.annotation.event.Condition;
import com.flying.framework.data.Data;

/**
 * @author wanghaifeng
 *
 */
public class EventHandler implements Serializable {
	private static final long serialVersionUID = 847818937027917201L;
	private String moduleId;
	private String serviceId;
	private final Map<String, Pattern> conditionPatterns = Utils.newHashMap();
	private final Map<String, String> conditions = Utils.newHashMap();

	public EventHandler(String moduleId, String serviceId) {
		this.setModuleId(moduleId);
		this.setServiceId(serviceId);
	}

	public EventHandler(com.flying.framework.annotation.event.Handler h) {
		this.setModuleId(h.moduleId());
		this.setServiceId(h.serviceId());
		for(Condition c: h.conditions()) {
			this.conditions.put(c.field(), c.value());
		}
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceId() {
		return serviceId;
	}
	
	public void addCondition(String name, String value) {
		assert name != null && value != null: " condition name and value is not null!";
		this.conditionPatterns.put(name, Pattern.compile(value));
		this.conditions.put(name, value);
	}
	
	public Map<String, String> getConditions() {
		return conditions;
	}
	
	public boolean canHandle(Data data) {
		if(conditionPatterns.isEmpty()) return true;
		for(String key: conditionPatterns.keySet()) {
			String value = data.contains(key)?data.get(key).toString(): null;
			if(value == null) return false;
			Pattern p = conditionPatterns.get(key);
			if(!p.matcher(value).matches()) return false;
		}
		return true;
	}
	
	public int hashCode() {
		return (moduleId +":"+serviceId).hashCode();
	}
	
	public boolean equals(Object o) {
		if(o instanceof EventHandler) {
			EventHandler h = (EventHandler)o;
			return StringUtils.equals(moduleId, h.moduleId) && StringUtils.equals(h.serviceId, h.serviceId);
		}
		return false;
	}
	
	public String toString() {
		return serviceId + "@" + moduleId;
	}
}
