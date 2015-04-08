<%@page import="com.servicebook.database.primitives.DBUser"%>
<%@page import="com.servicebook.database.UsersDatabase"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<%
		String username = (String) session.getAttribute("username");
		UsersDatabase userDB =
			(UsersDatabase) getServletContext().getAttribute("userDB");
		DBUser res = userDB.getUser(username);
		String name = null;
		int balance = -1;
		if (res != null)
		{
			name = res.getName();
			balance = res.getBalance();
		}
	%>
	Username:
	<%=username%>
	<br>
	<br> Name:
	<%=name%>
	<br>
	<br> Balance:
	<%=balance%>
	<br>
	<br>
	<form action="/HW5/DeleteProfile" method="post">
		<input type="submit" name="deleteButton" value="Delete Profile" />
	</form>
</body>
</html>