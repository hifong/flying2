<%@page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>

<%@page import="com.flying.framework.application.*"%>
<%@page import="com.flying.framework.module.*"%>
<%@page import="com.flying.framework.config.*"%>
<%@page import="com.flying.framework.data.*"%>
<%@page import="com.flying.common.util.*"%>
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

Modules modules = Application.getInstance().getModules();
LocalModule module = modules.getLocalModule(moduleId);
module.getMetadataRepository().load();
%>
<html>
<head>
</head>
<body>

<h2><%= moduleId %></h2>
<h5>metadata reload success</h5>

<% 
for(String f: module.getMetadataRepository().getRepository().keySet()) {
%>
<hr>
<h5><%= f %></h5>
<pre><%= module.getMetadataRepository().getMetadata(f).getData() %></pre>
<% } %>

</body>
</html>