package com.flying.project.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.flying.common.annotation.handler.DaoException;
import com.flying.common.log.Logger;
import com.flying.common.util.Codes;
import com.flying.common.util.JSONUtils;
import com.flying.common.util.ServiceHelper;
import com.flying.common.util.StringUtils;
import com.flying.framework.annotation.MethodInfo;
import com.flying.framework.annotation.Service;
import com.flying.framework.data.Data;
import com.flying.framework.data.DataUtils;
import com.flying.framework.data.JSONData;
import com.flying.framework.exception.AppException;
import com.flying.framework.module.LocalModule;
import com.flying.framework.request.AbstractRequestService;

@Service("com.flying.project.request.DoRequestService")
public class DoRequestService extends AbstractRequestService{
	private final static Logger log = Logger.getLogger(DoRequestService.class);

	@Override
	public void service(LocalModule module, String[] uris, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final String s = uris[uris.length - 2];
		final String service = this.serviceConfig.getConfig(s) == null? s : this.serviceConfig.getConfig(s); 
		final String method = uris[uris.length - 1];
		final String $value = req.getParameter("$value");
		resp.setContentType("application/json;charset=UTF-8");
		Data request = DataUtils.convert(module, req);
		if(request.contains("page")) request.put("page", request.getInt("page", 1) - 1);
		//
		JSONData result = null;
		try {
			//
			final InputStream is = req.getInputStream();
			MessageBody messageBody = null;
			if(is != null && "POST".equalsIgnoreCase(req.getMethod())) {
				messageBody = new MessageBody(is);
			}
			if(messageBody != null) request.putAll(messageBody.jsonData);
			//
			Data data = module.invoke(service+":"+method, request);
			if(!StringUtils.isEmpty($value)) data.put("$value", $value);
			if(!data.contains(Codes.CODE)) data.put(Codes.CODE, Codes.SUCCESS);
			result = new JSONData(data);
		} catch (DaoException e) {
			Map<String, Object> methodInfo = ServiceHelper.getServiceMethods(module.getId(), service).get(method);
			MethodInfo mi = (MethodInfo)methodInfo.get("MethodInfo");
			
			String msg = e.getMessage();
			if(mi != null) {
				msg = mi.value() + "失败，请检查参数是否正确！";
			}
			Data data = new Data(Codes.CODE, Codes.FAIL, Codes.MSG, msg);
			result = new JSONData(data);
			log.error("DoRequestSerice error", e);
		} catch (AppException e) {
			log.error("DoRequestSerice error", e);
			result = new JSONData(e);
		} catch (Exception e) {
			Data data = new Data(Codes.CODE, Codes.FAIL, Codes.MSG, e.getMessage());
			result = new JSONData(data);
			log.error("DoRequestSerice error", e);
		}
    	resp.getWriter().write(result.toJSONString());
	}

	class MessageBody {
		String messageBody = "";
		Data jsonData = new Data();
		
		public MessageBody(InputStream is) {
			try {
				messageBody = IOUtils.toString(is, "UTF-8");
				if(messageBody == null || "".equals(messageBody.trim())) return ;
				//
				final int start = messageBody.indexOf("{");
				messageBody = messageBody.substring(start);
				//
				Map<String, Object> map = JSONUtils.toMap(messageBody);
				this.jsonData = new Data(map);
			} catch (Exception e) {
				throw new AppException(e,"参数错误，JSON解析错误！", String.valueOf(Codes.INVALID_PARAM));
			}
		}
	}
}