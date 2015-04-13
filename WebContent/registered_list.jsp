<%@page import="com.servicebook.database.primitives.DBPaidTask"%>
<%@page import="com.servicebook.database.primitives.DBPaidService"%>
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
	function unregisterActivity(id, username, index, element) {
		var url = '/HW5/UnregisterActivity';
		var posting = $.post(url, {
			id : id,
			username : username
		});

		posting.done(function(data) {
			if (data == "") {
				var elementToRemove = "#" + element;
				$(elementToRemove).remove();
			} else {
				$("#result_div" + index).empty().append(data);
			}
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
			<div class="panel-heading">Registered Activities</div>
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
						activitiesDB.getActivitiesUserRegisteredCount((String) session
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
								<th>Register</th>
							</tr>
						</thead>
						<tbody>

							<%
								List<DBPaidService> registeredServices =
									activitiesDB.getServicesUserRegistered(
										(String) session.getAttribute("username"),
										pageStart,
										perPage);
								List<DBPaidTask> registeredTasks =
									activitiesDB.getTasksUserRegistered(
										(String) session.getAttribute("username"),
										pageStart,
										perPage);
								int count = 0;
								for (int i = 0; i < Math.max(
									registeredServices.size(),
									registeredTasks.size()); i++)
								{
							%>
							<%
								if (i < registeredServices.size())
									{
							%>
							<tr id='Service<%=i%>'>
								<td><%=count%></td>
								<td>Service</td>
								<td><%=registeredServices.get(i).getUsername()%></td>
								<td><%=registeredServices.get(i).getTitle()%></td>
								<td><%=registeredServices.get(i).getCapacity()%></td>
								<td><%=registeredServices.get(i).getNumRegistered()%></td>
								<td><button class="btn btn-default"
										onclick='unregisterActivity("<%=registeredServices.get(i).getId()%>","<%=(String) session.getAttribute("username")%>","<%=i%>", "Service<%=i%>")'>Unregister
									</button></td>
							</tr>
							<%
								count++;
									}
									if (i < registeredTasks.size())
									{
							%>
							<tr id='Task<%=i%>'>
								<td><%=count%></td>
								<td>Service</td>
								<td><%=registeredTasks.get(i).getUsername()%></td>
								<td><%=registeredTasks.get(i).getTitle()%></td>
								<td><%=registeredTasks.get(i).getCapacity()%></td>
								<td><%=registeredTasks.get(i).getNumRegistered()%></td>
								<td><button class="btn btn-default"
										onclick='unregisterActivity("<%=registeredTasks.get(i).getId()%>","<%=(String) session.getAttribute("username")%>","<%=i%>", "Task<%=i%>")'>Unregister
									</button></td>
							</tr>
							<%
								count++;
									}
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