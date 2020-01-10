package com.flying.common.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.flying.common.util.Codes;
import com.flying.common.util.JSONUtils;
import com.flying.common.util.Pair;
import com.flying.framework.exception.AppException;

public class JsonHttpUtils {

	public static JSONObject post(String url) throws Exception {
		return post(url, null);
	}
	

	public static JSONObject post(final String url, List<Pair<String, String>> params) throws Exception {
		return (JSONObject)post(url, null, params);
	}

	public static Object post(final String url, List<Pair<String, String>> headers, List<Pair<String, String>> params) throws Exception {
		return HttpUtils.post(url, headers, params, new ResponseHandler<Object>(){

			@Override
			public Object convert(InputStream is) throws Exception {
				BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			    StringBuilder responseStrBuilder = new StringBuilder();
			    try {
				    String inputStr;
				    while ((inputStr = streamReader.readLine()) != null)
				        responseStrBuilder.append(inputStr);
				    
				    String json = responseStrBuilder.toString().trim();
				    if(json.startsWith("{"))
				    	return JSONUtils.toJSONObject(json);
				    else {
				    	return JSONUtils.toJSONArray(json);
				    }
			    } finally {
			    	streamReader.close();
			    }
			}
			
		});
	}

	public static JSONObject get(final String url) throws Exception {
		return HttpUtils.get(url, new ResponseHandler<JSONObject>(){

			@Override
			public JSONObject convert(InputStream is) throws Exception {
				BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			    StringBuilder responseStrBuilder = new StringBuilder();

			    String inputStr;
			    while ((inputStr = streamReader.readLine()) != null)
			        responseStrBuilder.append(inputStr);

			    return JSONUtils.toJSONObject(responseStrBuilder.toString());
			}
			
		});
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(get("http://120.24.154.150:28080/questions/getLastUpdateTime.do"));
		System.out.println(get("http://admin.epaylinks.cn/index.htm"));
		System.out.println(get("https://admin.epaylinks.cn/newpay/SHstore/store/html/index.html"));
	}
}