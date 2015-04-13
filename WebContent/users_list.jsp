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
	function postFriendships(user1, user2, index) {
		var url = '/HW5/AddFriend';
		var posting = $.post(url, {
			first_username : user1,
			second_username : user2
		});

		posting.done(function(data) {
			alert(data);
		});
	}
</script>
</head>
<body>
	<div class="col-md-12">
		<!--    Striped Rows Table  -->
		<div class="panel panel-default">
			<div class="panel-heading">Users</div>
			<div class="panel-body">
				<%
					UsersDatabase userDB =
						(UsersDatabase) getServletContext().getAttribute("userDB");
					int totalCount = 0;
					int perPage = 10;
					int pageStart = 0;
					String start = request.getParameter("start");
					if (start != null) pageStart = Integer.parseInt(start);
					if (pageStart < 0) pageStart = 0;
					totalCount = userDB.getUsersCount();
					if (pageStart > totalCount) pageStart = pageStart - perPage;
				%>
				<div style="width: 30%; margin: 0px auto;">
				<a href="<%=request.getRequestURL()%>?start=<%=pageStart - 10%>"><i class="fa fa-arrow-circle-left"></i></a>
				<%=pageStart + 1%>
				-
				<%=pageStart + 10%>
				<a href="<%=request.getRequestURL()%>?start=<%=pageStart + 10%>"><i class="fa fa-arrow-circle-right"></i></a>
				</div>
				<br />
				
				<div class="table-responsive">
					<table class="table table-striped">
						<thead>
							<tr>
								<th>#</th>
								<th>Username</th>
								<th>Name</th>
								<th>Balance</th>
								<th>Add Friend</th>
							</tr>
						</thead>
						<tbody>
							<%
								List<DBUser> users = userDB.getUsers(pageStart, perPage);
								int index = 0;
								for (DBUser u : users)
								{
									String username = u.getUsername();
									String name = u.getName();
									int balance = u.getBalance();
							%>
							<tr>
								<td><%=index%></td>
								<td><%=username%></td>
								<td><%=name%></td>
								<td><%=balance%></td>
								<td><button class="btn btn-default"
										id='add_friend<%=index%>'
										onclick='postFriendships("<%=(String) session.getAttribute("username")%>", "<%=username%>","<%=index%>")'>Add
										Friend</button></td>
							</tr>
							<%
								index++;
								}
							%>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<!--  End  Striped Rows Table  -->
	</div>
</body>
</html>