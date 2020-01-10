package com.flying.common.annotation.handler;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.flying.common.annotation.MethodLogger;
import com.flying.common.log.util.LoggerStringBuffer;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;

public class MethodLoggerHandler implements ServiceHandler<MethodLogger>{
	private final static Logger log = Logger.getLogger(MethodLoggerHandler.class);

	@Override
	public Data handle(MethodLogger annotation, Data request, ServiceHandlerContext context)
					throws Exception {
		final LocalModule module = context.getModule();
		final String serviceId = context.getServiceId();
		final String methodName = context.getMethodName();
		
		long time = System.currentTimeMillis();
		Throwable t = null;
		Data result = null;
		try {
			result = context.doChain(request);
			return result;
		} catch (Exception e) {
			t = e;
			throw e;
		}finally {
			time = System.currentTimeMillis() - time;
			LoggerStringBuffer sb = new LoggerStringBuffer();
			sb.append("--->");
			if(!StringUtils.isEmpty(annotation.tag())) sb.append(annotation.tag());
			sb.append(module.getId());
			if(annotation.logServiceId()) sb.append(serviceId + ":" + methodName);
			if(annotation.logTime())sb.append("TIME:" + String.valueOf(time));
			
			for(int i=0; annotation.requests() != null && i < annotation.requests().length; i++) {
				sb.append(annotation.requests()[i] + ":" + request.getString(annotation.requests()[i]));
			}
			for(int i=0; annotation.responses() != null && i < annotation.responses().length; i++) {
				sb.append(annotation.responses()[i] + ":" + (result == null?"null": result.getString(annotation.responses()[i])));
			}
			
			if(annotation.logErrorSimple()) {
				sb.append("EX:"+(t == null?"":t.getMessage()));
			}
			if(annotation.logErrorDetail()) {
				sb.append("EX:"+(t == null?"": asString(t)));
			}
			log.info(sb.toString());
		}
	}
	
	private String asString(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
