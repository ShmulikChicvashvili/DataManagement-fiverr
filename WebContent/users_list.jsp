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
<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script type="text/javascript">
	function postFriendships(user1, user2,index) {
		var url = '/HW5/AddFriend';
		var posting = $.post(url, {
			first_username : user1,
			second_username : user2
		});
		
		posting.done(function(data) {
			$("#result_div"+index).empty().append(data);
			if(data == "Friend Added") {
				document.getElementById("add_friend"+index).disabled = true;
			}
		});
	}
</script>
</head>
<body>
	<ul>
		<%
			UsersDatabase userDB =
				(UsersDatabase) getServletContext().getAttribute("userDB");
			List<DBUser> users = userDB.getUsers(0, 20);
			int index = 0;
			for (DBUser u : users)
			{
				String username = u.getUsername();
				String name = u.getName();
				int balance = u.getBalance();
		%>

		<li>Username: <%=username%> &emsp; Name: <%=name%> &emsp;
			Balance: <%=balance%> &emsp;
			<button id='add_friend<%=index%>'
				onclick='postFriendships("<%=(String) session.getAttribute("username")%>", "<%=username%>","<%=index%>")'>Add
				Friend</button>
			<div id="result_div<%=index%>"></div>
		</li>

		<%
			index++;
			}
		%>

	</ul>
</body>
</html>