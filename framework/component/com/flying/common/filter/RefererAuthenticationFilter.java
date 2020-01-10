package com.flying.common.filter;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.flying.common.util.Codes;
import com.flying.common.util.ServiceHelper;
import com.flying.common.util.StringUtils;
import com.flying.framework.config.ServiceConfig;
import com.flying.framework.context.ServiceContext;
import com.flying.framework.data.Data;
import com.flying.framework.exception.AppException;
import com.flying.framework.module.LocalModule;
import com.flying.framework.request.AbstractRequestFilter;
import com.flying.framework.request.RequestFilter;
import com.flying.framework.request.RequestFilterChain;
import com.flying.framework.security.Principal;

public class RefererAuthenticationFilter extends AbstractRequestFilter implements RequestFilter {
	private Logger logger = Logger.getLogger(RefererAuthenticationFilter.class);

	private Pattern filterPattern;
	private Pattern ignorePattern;
	private Object lock = new Object();

	public Pattern getFilterPattern(ServiceConfig serviceConfig) {
		if (filterPattern == null) {
			synchronized (lock) {
				if (filterPattern == null) {
					filterPattern = Pattern.compile(".+(\\.do|\\.xdo|\\.action)", Pattern.CASE_INSENSITIVE);
				}
			}
		}
		return filterPattern;
	}

	public Pattern getIgnorePattern(ServiceConfig serviceConfig) {
		if (ignorePattern == null) {
			synchronized (lock) {
				if (ignorePattern == null) {
					String patternStr = serviceConfig.getConfig("ignorePattern");
					try {
						ignorePattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
					} catch (Exception e) {
						logger.error("URIAuthenticationFilter:getPattern", e);
						ignorePattern = Pattern.compile(".+(\\.page|\\.shtml|\\.widget)", Pattern.CASE_INSENSITIVE);
					}
				}
			}
		}
		return ignorePattern;
	}
	
	@Override
	public void doFilter(LocalModule module, HttpServletRequest req, HttpServletResponse resp, RequestFilterChain chain) throws Exception {
		final Principal principal = req.getSession() == null?null:(Principal)req.getSession().getAttribute(Principal.PRINCIPAL);
		final String referer = req.getHeader("Referer");
		final String uri = req.getRequestURI();
		
		final Pattern filterPattern = this.getFilterPattern(this.serviceConfig);
		final Pattern ignorePattern = this.getIgnorePattern(this.serviceConfig);

		//非保护资源，直接忽略
		if(!filterPattern.matcher(uri).find()) {	//not protected resource
			chain.doFilter(req, resp);
			return;
		}
		
		if(referer != null && ignorePattern.matcher(referer).find()) {	//ignore urls
			chain.doFilter(req, resp);
			return;
		}
		
		if(StringUtils.isEmpty(referer)) {
			ServiceContext context = ServiceContext.getContext();
			throw new com.flying.framework.exception.SecurityException(context.getPrincipal(), "URIAuthenticate auth error to uri[" + uri +"] for referer is empty", Codes.FAIL + "" );
		}
		//
		if(principal == null) {	//没有登陆，对于登陆页面则过，否则跳转
			if(referer.indexOf(this.serviceConfig.getConfig("login")) >= 0) {	//login page
				chain.doFilter(req, resp);
				return;
			}
			resp.sendRedirect(this.serviceConfig.getConfig("login"));
		} else {		//已经登陆，判定角色
			//auth uris
			if(!this.auth(principal, referer)) {
				logger.error("URIAuthenticate auth error to principal[" + principal +"] for uri[" + referer +"]");
				resp.sendError(403);
			} else { //
				chain.doFilter(req, resp);
			}
		}
	}

	private boolean auth(Principal principal, String uri) throws URIAuthenticationException {
		final String authModuleId = this.serviceConfig.getConfig("authModuleId");
		final String authService = this.serviceConfig.getConfig("authService");
		if(StringUtils.isEmpty(authModuleId) || StringUtils.isEmpty(authService)) return true;
		try {
			Data data = ServiceHelper.invoke(authModuleId, authService, new Data("principal", principal, "url", uri));
			boolean authResult = data.getBoolean("authResult", false);
			return authResult;
		} catch (Exception e) {
			throw new AppException(e, "URIAuthenticationController error occurs, because of :" + e);
		}
	}
}
