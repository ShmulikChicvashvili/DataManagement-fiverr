<%@ page language="java" contentType="text/html; charset=windows-1255"
	pageEncoding="windows-1255"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type"
	content="text/html; charset=windows-1255">
<title>JSP Page.</title>
</head>
<body>
	<%
		Cookie[] cookies = request.getCookies();
		for (Cookie c : cookies)
		{
			System.out.println("cookies in jsp : "
				+ c.getName()
				+ " "
				+ c.getValue());
		}
	%>
	<%
		String username = (String) session.getAttribute("username");
	%>
	<h1>Welcome: ${username }</h1>

</body>
</html>