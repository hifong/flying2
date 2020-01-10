package com.flying.framework.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

import com.flying.common.log.Logger;
import com.flying.common.util.Codes;
import com.flying.common.util.ServiceHelper;
import com.flying.common.util.Utils;
import com.flying.framework.application.Application;
import com.flying.framework.data.Data;
import com.flying.framework.security.Principal;

public abstract class LogUtil {
	private final static Logger invokerSimple = Logger.getLogger("invokerSimple");
	private final static Logger invokerDetail = Logger.getLogger("invokerDetail");
	/**
	 * 
	 * @param p
	 * @param type
	 * @param toModule
	 * @param serviceId
	 * @param time
	 * @param request
	 * @param response
	 */
	public static void invokeLog(Principal p, String type, String moduleId, 
			String serviceId, String methodName, long tid, long time, Data request, Data response, Throwable t) {
		
		if("log".equals(moduleId)) return;
		
		final StringBuilder msg = new StringBuilder();
		msg.append("Module:").append(moduleId).append(" | ");
		msg.append("Principal:").append(p == null?"":p.getId()).append(" | ");
		msg.append("Service:").append(serviceId).append(" | ");
		msg.append("Method:").append(methodName).append(" | ");
		msg.append("Time:").append(time).append(" | ");
		msg.append(Codes.CODE + ":").append(response == null?"":(""+response.get(Codes.CODE)));
		
		final String input = request.toString();
		final String output;
		final String error;
		if(response == null) {
			output = null;
		} else if(response.contains(Codes.ROWS)) {
			Map<String, Object> res = Utils.newHashMap();
			res.putAll(response.getValues());
			res.put(Codes.ROWS, "...");
			output = res.toString();
		} else {
			output = response.toString();
		}

		if(t != null){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			error = sw.toString();
		} else {
			error = "";
		}
		//
		if(invokerSimple.isInfoEnabled()) {
			invokerSimple.info(msg.toString());
		}
		
		if(invokerDetail.isInfoEnabled()) {
			invokerDetail.info(msg + "\n\t Input: " + input + "\n\t Output: " + output + "\n\t Error: " + error);
		}
		//
		if(Application.getInstance().getModules().exists("log")) {
			try {
				ServiceHelper.invokeAsync("log", "log:invokeLog", new Data(
						"logDate", 	new Date(),
						"appId", 	Application.getInstance().getId(),
						"moduleId", moduleId,
						"service", 	serviceId,
						"method", 	methodName,
						"time", 	time,
						"tid", 		tid, 
						"returnCode", 	response == null?"":(""+response.get(Codes.CODE)), 
						"input", 		input,
						"output", 		output,
						"error", 		error
					));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
