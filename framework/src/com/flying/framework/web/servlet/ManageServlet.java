package com.flying.framework.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.flying.common.util.Codes;
import com.flying.framework.application.Application;
import com.flying.framework.data.Data;
import com.flying.framework.data.JSONData;

@WebServlet(value="/manage")
public class ManageServlet  extends HttpServlet {
	private static final long serialVersionUID = 124178848285193113L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json;charset=UTF-8");
		
		Data data;
		try {
			data = this.handle(req);
		} catch (Exception e) {
			data = new Data(Codes.CODE, Codes.FAIL, Codes.MSG, e.getMessage());
		}
		
		JSONData result = new JSONData(data);
    	resp.getWriter().write(result.toJSONString());
	}

	private Data handle(HttpServletRequest req) throws Exception {
		String action = req.getParameter("action");
		Data data = new Data("action", action);
		if("install".equals(action)) {
			String moduleId = req.getParameter("moduleId");
			String locate = req.getParameter("locate");
			String path = req.getParameter("path");
			Application.getInstance().getModules().loadModule(moduleId, locate, path, null);
			//
			data.put("moduleId", moduleId);
			data.put("locate", locate);
			data.put("path", path);
			data.put(Codes.CODE, Codes.SUCCESS);
		} else if("uninstall".equals(action)) {
			String moduleId = req.getParameter("moduleId");
			Application.getInstance().getModules().unloadModule(moduleId);
			data.put("moduleId", moduleId);
			data.put(Codes.CODE, Codes.SUCCESS);
		} else {
			data.put(Codes.CODE, Codes.FAIL);
		}
		return data;
	}
}
