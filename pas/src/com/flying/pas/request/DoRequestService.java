package com.flying.pas.request;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.flying.common.log.Logger;
import com.flying.common.util.Codes;
import com.flying.common.util.StringUtils;
import com.flying.framework.annotation.Service;
import com.flying.framework.context.ServiceContext;
import com.flying.framework.data.Data;
import com.flying.framework.data.DataUtils;
import com.flying.framework.data.JSONData;
import com.flying.framework.module.LocalModule;
import com.flying.framework.request.AbstractRequestService;
import com.flying.framework.security.Principal;
import com.flying.pas.User;

@Service("com.flying.pas.request.DoRequestService")
public class DoRequestService extends AbstractRequestService{
	private final static Logger log = Logger.getLogger(DoRequestService.class);

	@Override
	public void service(LocalModule module, String[] uris, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		final String s = uris[uris.length - 2];
		final String service = this.serviceConfig.getConfig(s) == null? s : this.serviceConfig.getConfig(s); 
		final String method = uris[uris.length - 1];
		final String $value = req.getParameter("$value");
		Data request = DataUtils.convert(module, req);

		if(ServiceContext.getContext() != null && ServiceContext.getContext().getPrincipal() != null) {
			request.put("creator", ServiceContext.getContext().getPrincipal().getName());
			request.put("updator", ServiceContext.getContext().getPrincipal().getName());
		}
		
		//resp.setContentType("text/json;charset=UTF-8");
		if(req.getRequestURI().indexOf("/pas/") >= 0) {
			request.put("rows", Math.max(15, request.getInt("rows", 15)));
		}
		//
		Data data;
		try {
			data = module.invoke(service+":"+method.trim(), request);
			if(data.getInt(Codes.CODE, 0) == Codes.SUCCESS) {
				data.put("success", Boolean.TRUE);
				if("findByUsernamePassword".equals(method)) {
					Principal p = new User(data);
					req.getSession().setAttribute(Principal.PRINCIPAL, p);
				}
			}
		} catch (Exception e) {
			data = new Data(Codes.CODE, Codes.FAIL, Codes.MSG, "操作失败，原因："+e.getMessage());
			log.error("DoRequestSerice error", e);
		}
		if(!StringUtils.isEmpty($value)) data.put("$value", $value);
		JSONData result = new JSONData(data);
    	resp.getWriter().write(result.toJSONString());
	}
}