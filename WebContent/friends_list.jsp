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
	<div class="col-md-12">
		<!--    Striped Rows Table  -->
		<div class="panel panel-default">
			<div class="panel-heading">My Friends</div>
			<div class="panel-body">
				<div class="table-responsive">
					<table class="table table-striped">
						<thead>
							<tr>
								<th>#</th>
								<th>Username</th>
								<th>Name</th>
								<th>Balance</th>
							</tr>
						</thead>
						<tbody>
							<%
								FriendshipsDatabase friendshipDB =
									(FriendshipsDatabase) getServletContext().getAttribute(
										"friendshipDB");
								
								List<DBUser> friends =
									friendshipDB.getFriends((String) session
										.getAttribute("username"));
								int index = 0;
								for (DBUser u : friends)
								{
									String username = u.getUsername();
									String name = u.getName();
									int balance = u.getBalance();
							%>
							<tr>
								<td><%=index %></td>
								<td><%=username %></td>
								<td><%=name %></td>
								<td><%=balance %></td>
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
		<!--End Advanced Tables -->
	</div>
	</div>
	</div>
</body>
</html>