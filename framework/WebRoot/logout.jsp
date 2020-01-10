<%
session.invalidate();
response.sendRedirect(request.getParameter("url"));
%>