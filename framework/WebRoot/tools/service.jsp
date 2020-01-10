<%@page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page session="false" %>

<%@page import="com.flying.framework.application.*"%>
<%@page import="com.flying.framework.annotation.*"%>
<%@page import="com.flying.framework.module.*"%>
<%@page import="com.flying.framework.config.*"%>
<%@page import="com.flying.common.util.*"%>
<%@page import="java.util.*"%>
<%@page import="java.lang.annotation.*"%>
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
String asString(Object o) {
	if(o == null)
		return "";
	else if(o instanceof Param) {
		String sb = "";
		Param p = (Param)o;
		if(p.required()) {
			if(sb.length() > 0) sb += ",";
			sb += "required=true";
		}
		if(p.maxlength() >0) {
			if(sb.length() > 0) sb += ",";
			sb += "maxlength="+p.maxlength();
		}
		if(p.max() != null && !"".equals(p.max())) {
			if(sb.length() > 0) sb += ",";
			sb += "max="+p.max();
		}
		if(p.min() != null && !"".equals(p.min())) {
			if(sb.length() > 0) sb += ",";
			sb += "min="+p.min();
		}
		if(p.format() != null && !"".equals(p.format())) {
			if(sb.length() > 0) sb += ",";
			sb += "format="+p.format();
		}
		if(p.tag() != null && !"".equals(p.tag())) {
			if(sb.length() > 0) sb += ",";
			sb += "tag="+p.tag();
		}
		if(p.desc() != null && !"".equals(p.desc())) {
			if(sb.length() > 0) sb += ",";
			sb += p.desc();
		}
		return sb;
	} else if(o instanceof String) {
		String s = (String)o;
		
		int li = s.indexOf("java.lang.");
		if(li >= 0) {
			return s.substring("java.lang.".length());
		} else if("com.flying.framework.data.Data".equals(s)) {
			return "Data";
		} else {
			return s;
		}
	} else
		return o.toString();
}
%>
<%
String moduleId = request.getParameter("module");
String serviceId = request.getParameter("service");

Modules modules = Application.getInstance().getModules();
LocalModule module = modules.getLocalModule(moduleId);
com.flying.framework.config.ServiceConfig serviceConfig = module.getModuleConfig().getServiceConfig(serviceId);

%>
<strong><a href="modules.jsp">modules</a> / <%= moduleId %>:<%= serviceId %></strong>

<table border=1 cellpadding=0 cellspacing=0 bgcolor="#FFFFFF" width="90%" bordercolor='#FEFEFE'>
<tr>
	<td  width="20%">target</td>
	<td  width="80%"><%= serviceConfig.getTarget() %></td>
</tr>
<tr style="display:none">
	<td  width="20%">type</td>
	<td  width="80%"><%= serviceConfig.getType() %></td>
</tr>
<tr style="display:none">
	<td  width="20%">desc</td>
	<td  width="80%"><%= serviceConfig.getDesc() %></td>
</tr>
<tr style="display:none">
	<td  width="20%">configs</td>
	<td  width="80%"><table width='100%' border=1 cellspacing=0 cellpadding=0 bordercolor='#FEFEFE'>
	<% 
	for(String f: sort(serviceConfig.getConfigs().getValues().keySet())) {
	%>
	<tr><td><%= f %></td><td><%= serviceConfig.getConfigs().getValues().get(f) %></td>
	<% } %></table></td>
</tr>
</table>
<hr>
<%
Map<String,Map<String,Object>> methods = ServiceHelper.getServiceMethods(moduleId, serviceId);
for(String methodName: sort(methods.keySet())) {
	Map<String, Object> methodInfo = (Map<String, Object>)methods.get(methodName);
%>
<strong><a href="#<%= methodName %>" name="<%= methodName %>"><%= methodName %></a></strong>
	<%MethodInfo mi = null;
	if(methodInfo.containsKey("MethodInfo")){ mi = (MethodInfo)methodInfo.get("MethodInfo");%>
	(<font size=-1><%= mi.value() %></font>)
	<%}%>
	&nbsp;<font size=-1>/<%= moduleId %>/<%= serviceId %>/<%= methodName %>.do</font>
	&nbsp;<input type='button' onclick="showMore('<%= methodName %>')" value='...'>
<form id="<%= methodName %>FRM" action="invoke.jsp?module=<%= moduleId %>&service=<%= serviceId %>&method=<%= methodName %>" method="post" target="result_<%= methodName %>">
<table width="99%" border=0 style="display:none" id="MORE<%= methodName %>">
<%
	List<Map<String, Object>> params = (List<Map<String, Object>>)methodInfo.get("params");
	int c = 0;
	if(params != null)
	for(Map<String, Object> param : params) {
	c ++;
%>
	<tr valign="middle">
		<td width="20%"><strong><%= param.get("name") %></strong><br>
			<font size=-1><%= asString(param.get("type")) %>, <%= asString(param.get("param")) %></font>
		</td>
		<td width="25%">&nbsp;<input type="text" name="<%= param.get("name") %>" style="width:95%" /></td>
		<% if (c == 1) {%>
		<td width="*" rowspan="<%= params.size() + 1 %>"><iframe width="100%" height="<%= (1+params.size())*40 %>px" name="result_<%= methodName %>"></iframe></td>
		<%}%>
	</tr>
<%
	}
%>
	<tr><td colspan=2 align="center">
		<input type=submit value="Invoke" style="width:100px">
		<input type=button value="GET" style="width:100px;display:none" onclick="get('<%= methodName %>')">
		<input type=button value="POST" style="width:100px;display:none" onclick="post('<%= methodName %>')">
	</td></tr>
</table>
</form>
<hr>
<%}%>
</body>
<script>
function showMore(m) {
	var t = document.getElementById('MORE'+m);
	if(t.style.display == 'none')
		t.style.display=''
	else
		t.style.display='none'
}
function get(m) {
	var frm = document.getElementById(m+"FRM");
	var t = frm.target;
	var a = frm.action;
	frm.target = "_blank";
	frm.action="/decor/<%= serviceId %>/" + m + ".do";
	frm.method="GET";
	frm.submit();
	frm.target=t;
	frm.action=a;
}
function post(m) {
	var frm = document.getElementById(m+"FRM");
	var t = frm.target;
	var a = frm.action;
	frm.target = "_blank";
	frm.action="/decor/<%= serviceId %>/" + m + ".do";
	frm.method="POST";
	frm.submit();
	frm.target=t;
	frm.action=a;
}
</script>
</html>