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
		String username = null;
		if (session == null || session.getAttribute("username") == null)
		{
			if(session.getAttribute("username") == null) {
				System.out.println(":(((()))");
			}
			response.sendRedirect("/HW5/registration.html");
		} else
		{
			username = (String)session.getAttribute("username");
		}
	%>
	<h1>Welcome: ${username } </h1>

</body>
</html>