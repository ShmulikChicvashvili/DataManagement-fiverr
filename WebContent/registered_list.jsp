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
	<ul>
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
		<a href="<%=request.getRequestURL()%>?start=<%=pageStart - 10%>">Previous</a>
		<%=pageStart + 1%>
		-
		<%=pageStart + 10%>
		<a href="<%=request.getRequestURL()%>?start=<%=pageStart + 10%>">Next</a>
		<br />
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
			for (int i = 0; i < Math.max(
				registeredServices.size(),
				registeredTasks.size()); i++)
			{
		%>
		<%
			if (i < registeredServices.size())
				{
		%>
		<li id='Service<%=i%>'><h5>Service-</h5>Username:<%=registeredServices.get(i).getUsername()%>
			&emsp; Title:<%=registeredServices.get(i).getTitle()%> &emsp;
			Capacity:<%=registeredServices.get(i).getCapacity()%> &emsp; Number
			registered:<%=registeredServices.get(i).getNumRegistered()%> &emsp;
			<button
				onclick='unregisterActivity("<%=registeredServices.get(i).getId()%>","<%=(String) session.getAttribute("username")%>","<%=i%>", "Service<%=i%>")'>Unregister
				to service</button></li>
		<%
			}
				if (i < registeredTasks.size())
				{
		%>
		<li id='Task<%=i%>'><h5>Task-</h5>Username:<%=registeredTasks.get(i).getUsername()%>
			&emsp; Title:<%=registeredTasks.get(i).getTitle()%> &emsp; Capacity:<%=registeredTasks.get(i).getCapacity()%>
			&emsp; Number registered:<%=registeredTasks.get(i).getNumRegistered()%>
			&emsp;
			<button
				onclick='unregisterActivity("<%=registeredTasks.get(i).getId()%>","<%=(String) session.getAttribute("username")%>","<%=i%>", "Task<%=i%>")'>Unregister
				to task</button></li>
		<%
			}
		%>
		<div id="result_div<%=i%>"></div>
		<%
			}
		%>
	</ul>
</body>
</html>