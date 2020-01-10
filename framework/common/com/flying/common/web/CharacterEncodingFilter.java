package com.flying.common.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class CharacterEncodingFilter implements Filter {
	private String encoding = "UTF-8";
	private String contentType = "";
	private String allowHeaders = "timestamp,token,userid,checkCode,messageType,version,apptype,Content-Type,source";

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse httpresponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) httpresponse;
		request.setCharacterEncoding(encoding);
		response.setCharacterEncoding(encoding);
		response.setContentType(contentType);
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", -1);
		// 设置可以跨域
		response.setHeader("Access-Control-Allow-Headers", allowHeaders);
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Credentials", "true");

		response.setHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS");
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		String _encoding = config.getInitParameter("encoding");
		String _contentType = config.getInitParameter("contentType");
		String _allowHeaders = config.getInitParameter("allowHeaders");

		if (_encoding != null)
			encoding = _encoding;
		if (_contentType != null)
			contentType = _contentType;
		if(_allowHeaders != null) 
			allowHeaders = _allowHeaders;
	}

}
