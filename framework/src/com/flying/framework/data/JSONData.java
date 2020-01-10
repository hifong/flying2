package com.flying.framework.data;

import java.util.Collection;
import java.util.HashMap;

import com.flying.common.util.Codes;
import com.flying.common.util.JSONUtils;
import com.flying.framework.exception.AppException;

public class JSONData {
	protected final Data data;
	
	public JSONData(Data d) {
		this.data = d;
	}
	
	public JSONData(String errorCode, String message) {
		this.data = new Data(Codes.CODE, errorCode, Codes.MSG, message);
	}
	
	public JSONData(AppException e) {
		this.data = new Data(Codes.CODE, e.getErrorCode(), Codes.MSG, e.getMessage());
	}
	
	public String toString() {
		try {
			return toJSONString();
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	public String toJSONString() {
		Object result = null;
		if (data != null) {
			result = data.contains("$value")?data.get(data.getString("$value")):data.getValues();
		}else{
			result = new HashMap<String, String>();
		}
		if(result instanceof Collection) {
			return JSONUtils.toJSONString(DataUtils.toList((Collection)result));
		} else {
			return JSONUtils.toJSONString(DataUtils.toMap(result));
		}
	}
	
}
