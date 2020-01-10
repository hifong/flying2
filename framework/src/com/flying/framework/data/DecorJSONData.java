package com.flying.framework.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.flying.common.util.Codes;
import com.flying.common.util.JSONUtils;
import com.flying.common.util.Utils;
import com.flying.framework.context.ServiceContext;
import com.flying.framework.exception.AppException;
import com.flying.framework.module.LocalModule;

public class DecorJSONData extends JSONData {

	public DecorJSONData(AppException e) {
		super(e);
	}

	public DecorJSONData(String errorCode, String message) {
		super(errorCode, message);
	}

	public DecorJSONData(Data d) {
		super(d);
	}

	public String toJSONString() {
		Map<String, Object> head = Utils.newHashMap();
		Map<String, Object> body = Utils.newHashMap();
		//
		int status = data.getInt(Codes.CODE, 0);
		final int old_status = status;
		data.remove(Codes.CODE);
		if(status == 0) 
			status = 1;
		else if(status == 1)
			status = 0;
		head.put("status", status);
		//
		if(data.contains(Codes.TID))
			head.put(Codes.TID, data.remove(Codes.TID));
		//
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
		body.putAll(data.getValues());

		Map<String, Object> result = Utils.newHashMap();
		result.put("head", head);
		result.put("data", body);
		this.handleNull(body);

		return JSONUtils.toJSONString(result);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void handleNull(Map map) {
		for(Iterator<Entry> entries = map.entrySet().iterator(); entries.hasNext(); ) {
			Entry entry = entries.next();
			Object k = entry.getKey();
			Object v = entry.getValue();
			if(v == null) {
				entry.setValue("");
				continue;
			}
			if(v instanceof Map) {
				this.handleNull((Map)v);
			} else if(v instanceof Collection) {
				Collection coll = (Collection)v;
				for(Object obj: coll) {
					if(obj instanceof Map) {
						this.handleNull((Map)obj);
					}
				}
			}
		}
	}
}
