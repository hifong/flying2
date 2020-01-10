<%@page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page session="false" %>

<%@page import="com.flying.framework.application.*"%>
<%@page import="com.flying.framework.module.*"%>
<%@page import="com.flying.framework.config.*"%>
<%@page import="com.flying.framework.data.*"%>
<%@page import="com.flying.common.util.*"%>
<%@page import="com.flying.framework.context.ServiceContext"%>
<%@page import="java.util.*"%>
<%@page import="java.io.*"%>
<%@page import="java.lang.annotation.*"%>
<%!
String asString(Throwable t) {
	StringWriter sw = new StringWriter();
	t.printStackTrace(new PrintWriter(sw));
	return sw.toString();
}
%>
<%
String moduleId = request.getParameter("module");
String serviceId = request.getParameter("service");
String method = request.getParameter("method");

Modules modules = Application.getInstance().getModules();
LocalModule module = modules.getLocalModule(moduleId);
				
Data data = module.getDataConverter(HttpServletRequest.class.getName()).convert(request);

ServiceContext ctx = ServiceContext.createLocalInvokingContext(module, null, request, response);

JSONData result = null;
Exception ex = null;

try {
	Data resdata = module.invoke(serviceId+":"+method, data);
	if(resdata == null) resdata = new Data(Codes.CODE, Codes.SUCCESS);
	if(!resdata.contains(Codes.CODE)) resdata.put(Codes.CODE, Codes.SUCCESS);
	result = new JSONData(resdata);
} catch (com.flying.framework.exception.AppException e) {
	result = new JSONData(e);
	ex = e;
} catch (Exception e) {
	ex = e;
	result = new JSONData("1", e.getMessage() == null? e.toString():e.getMessage());
}
%>
<html>
<head>
</head>
<body>

<strong>Request</strong>
<%
data.remove("module");data.remove("service");data.remove("method");
%>
<pre><%= new JSONData(data).toJSONString() %></pre>
<hr>

<strong>Response</strong>
<pre><%= result.toJSONString() %></pre>
<hr>

<%if(ex != null) { %>
<strong>Exception</strong>
<font size=-1><%= ex %></font><br>
<pre>
<%= asString(ex) %>
</pre>
<hr>
<% } %>

</body>
</html>