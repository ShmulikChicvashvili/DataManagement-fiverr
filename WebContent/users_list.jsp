<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
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
		UsersDatabase userDB =
			(UsersDatabase) getServletContext().getAttribute("userDB");
		List<DBUser> users = userDB.getUsers(0, 20);
		for (DBUser u : users)
		{
			String username = u.getUsername();
			String name = u.getName();
			int balance = u.getBalance();
	%>

	<form id="friendshipForm">

		Username:<input type="text" name="username" value="<%=username%>"
			readonly /><br /> <br /> name:<input type="text" name="name"
			value="<%=name%>" readonly /><br /> <br /> balance:<input
			type="text" name="balance" value="<%=balance%>" /> <br /> <br /> <input
			type="submit" value="add friend" readonly>


	</form>

	<%
		}
	%>
</body>
</html>