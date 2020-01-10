<%@page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page session="false" %>

<%@page import="com.flying.framework.application.*"%>
<%@page import="com.flying.framework.module.*"%>
<%@page import="com.flying.framework.config.*"%>
<%@page import="java.util.*"%>
<html>
	<head>
		<title>模块管理工具</title>
	</head>
<body>
<%!
List<String> sort(Set<String> sets) {
	List<String> moduleIds = new ArrayList<String>();
	moduleIds.addAll(sets);
	Collections.sort(moduleIds);
	return moduleIds;
}
%>
<%
Modules modules = Application.getInstance().getModules();

for(String mid: sort(modules.getLocalModules().keySet())) {
LocalModule m = modules.getLocalModule(mid);
ModuleConfig mc = m.getModuleConfig();
%>
<h2><%= m.getId() %></h2><h4><%= m.getPath() %></h4>

<table border=1 cellpadding=0 cellspacing=0 bgcolor="#FFFFFF" width="90%">
<tr style="display:none">
	<td  width="20%">moduleHome</td>
	<td  width="80%"><%= mc.getModuleHome() %></td>
</tr>
<tr>
	<td  width="20%">Services</td>
	<td  width="80%"><% 
	for(String f: sort(mc.getServiceConfigs().keySet())) {
		if(f.startsWith("com.")) continue;
	%>
	<a href="service.jsp?module=<%= mid %>&service=<%= f %>"><%= f %></a><br>
	<% } %></td>
</tr>
<tr style="display:none">
	<td  width="20%">pageConfigs</td>
	<td  width="80%"><% 
	for(String f: sort(mc.getPageConfigs().keySet())) {
	%>
	<%= f %> || <%= mc.getPageConfigs().get(f) %><br>
	<% } %></td>
</tr>
</table>

<hr>
<%}%>
</body>
</html>