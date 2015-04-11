<%@page import="com.servicebook.database.primitives.DBUser"%>
<%@page import="java.util.List"%>
<%@page import="com.servicebook.database.FriendshipsDatabase"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<ul>
		<%
			FriendshipsDatabase friendshipDB =
				(FriendshipsDatabase) getServletContext().getAttribute(
					"friendshipDB");
			
			List<DBUser> friends =
				friendshipDB.getFriends((String) session
					.getAttribute("username"));
			for (DBUser u : friends)
			{
				String username = u.getUsername();
				String name = u.getName();
				int balance = u.getBalance();
		%>

		<li>Username: <%=username%> &emsp; Name: <%=name%> &emsp;
			Balance: <%=balance%> &emsp;
		</li>

		<%
			}
		%>

	</ul>
</body>
</html>