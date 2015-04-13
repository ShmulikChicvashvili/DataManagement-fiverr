<%@page import="com.servicebook.database.primitives.DBPaidTask"%>
<%@page import="com.servicebook.database.primitives.DBPaidService"%>
<%@page import="com.servicebook.database.primitives.DBPaidActivity"%>
<%@page import="java.util.List"%>
<%@page import="com.servicebook.database.PaidActivitiesDatabase"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script type="text/javascript">
	function removeActivity(id, index, element) {
		var url = '/HW5/RemoveActivity';
		var posting = $.post(url, {
			id : id
		});

		posting.done(function(data) {
			if (data == "") {
				var elementToRemove = "#" + element;
				$(elementToRemove).remove();
			} else {
				alert(data);
			}
		});
	}
	function removeAllActivities(username) {
		var url = '/HW5/RemoveAllActivities';
		var posting = $.post(url, {
			username : username
		});

		posting.done(function(data) {
			window.location.href = "/HW5/my_activities.jsp";
		});
	}
</script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<div class="col-md-12">
		<!--    Striped Rows Table  -->
		<div class="panel panel-default">
			<div class="panel-heading">My Activities</div>
			<div class="panel-body">
				<%
					PaidActivitiesDatabase activitiesDB =
						(PaidActivitiesDatabase) getServletContext().getAttribute(
							"activitiesDB");
					int totalCount = 0;
					int perPage = 10;
					int pageStart = 0;
					String start = request.getParameter("start");
					if (start != null) pageStart = Integer.parseInt(start);
					if (pageStart < 0) pageStart = 0;
					totalCount =
						activitiesDB.getActivitiesOfferedByUserCount((String) session
							.getAttribute("username"));
					if (pageStart > totalCount) pageStart = pageStart - perPage;
				%>
				<div style="width: 30%; margin: 0px auto;">
					<a href="<%=request.getRequestURL()%>?start=<%=pageStart - 10%>"><i
						class="fa fa-arrow-circle-left"></i></a>
					<%=pageStart + 1%>
					-
					<%=pageStart + 20%>
					<a href="<%=request.getRequestURL()%>?start=<%=pageStart + 10%>"><i
						class="fa fa-arrow-circle-right"></i></a>
				</div>
				<br />

				<div class="table-responsive">
					<table class="table table-striped">
						<thead>
							<tr>
								<th>#</th>
								<th>Type</th>
								<th>Username</th>
								<th>Title</th>
								<th>Capacity</th>
								<th>Number Registered</th>
								<th>Delete Activity</th>
							</tr>
						</thead>
						<tbody>
							<%
								List<DBPaidService> services =
									activitiesDB.getServicesOfferedByUser(
										(String) session.getAttribute("username"),
										pageStart,
										perPage);
								List<DBPaidTask> tasks =
									activitiesDB.getTasksOfferedByUser(
										(String) session.getAttribute("username"),
										pageStart,
										perPage);
								int count = 0;
								for (int i = 0; i < Math.max(tasks.size(), services.size()); i++)
								{
									if (i < services.size())
									{
							%>
							<tr id='Service<%=i%>'>
								<td><%=count%></td>
								<td>Service</td>
								<td><%=services.get(i).getUsername()%></td>
								<td><%=services.get(i).getTitle()%></td>
								<td><%=services.get(i).getCapacity()%></td>
								<td><%=services.get(i).getNumRegistered()%></td>
								<td><button class="btn btn-default"
										onclick='removeActivity("<%=services.get(i).getId()%>","<%=i%>", "Service<%=i%>")'>Delete
									</button></td>
							</tr>

							<%
								count++;
									}
									if (i < tasks.size())
									{
							%>
							<tr id='Task<%=i%>'>
								<td><%=count%></td>
								<td>Task</td>
								<td><%=tasks.get(i).getUsername()%></td>
								<td><%=tasks.get(i).getTitle()%></td>
								<td><%=tasks.get(i).getCapacity()%></td>
								<td><%=tasks.get(i).getNumRegistered()%></td>
								<td><button class="btn btn-default"
										onclick='removeActivity("<%=tasks.get(i).getId()%>","<%=i%>", "Task<%=i%>")'>Delete
									</button></td>
							</tr>
							<%
								count++;
									}
							%>

							<%
								}
							%>
							<Button class="btn btn-default"
								onclick='removeAllActivities("<%=(String) session.getAttribute("username")%>")'>Delete
								All</Button>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<!--  End  Striped Rows Table  -->
	</div>
</body>
</html>