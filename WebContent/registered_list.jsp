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
	<%
		PaidActivitiesDatabase activitiesDB =
			(PaidActivitiesDatabase) getServletContext().getAttribute(
				"activitiesDB");
		List<DBPaidService> registeredServices =
			activitiesDB.getServicesUserRegistered(
				(String) session.getAttribute("username"),
				0,
				10);
		List<DBPaidTask> registeredTasks =
			activitiesDB.getTasksUserRegistered(
				(String) session.getAttribute("username"),
				0,
				10);
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
</body>
</html>