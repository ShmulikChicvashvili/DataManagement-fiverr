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
				var elementToRemove = "#"+element;
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
			List<DBPaidService> services =
				activitiesDB.getServicesOfferedByUser(
					(String) session.getAttribute("username"),
					0,
					10);
			List<DBPaidTask> tasks =
				activitiesDB.getTasksOfferedByUser(
					(String) session.getAttribute("username"),
					0,
					10);
			for (int i = 0; i < Math.max(tasks.size(), services.size()); i++)
			{
				if (i < services.size())
				{
		%>
		<li id='Service<%=i%>'><h5>Service-</h5>Username:<%=services.get(i).getUsername()%>
			&emsp; Title:<%=services.get(i).getTitle()%> &emsp; Capacity:<%=services.get(i).getCapacity()%>
			&emsp; Number registered:<%=services.get(i).getNumRegistered()%>
			&emsp;
			<button
				onclick='removeActivity("<%=services.get(i).getId()%>","<%=i%>", "Service<%=i%>")'>Remove
				Service</button></li>

		<%
			}
				if (i < tasks.size())
				{
		%>
		<li id='Task<%=i%>'><h5>Task-</h5>Username:<%=tasks.get(i).getUsername()%>
			&emsp; Title:<%=tasks.get(i).getTitle()%> &emsp; Capacity:<%=tasks.get(i).getCapacity()%>
			&emsp; Number registered:<%=tasks.get(i).getNumRegistered()%> &emsp;
			<button
				onclick='removeActivity("<%=tasks.get(i).getId()%>","<%=i%>", "Task<%=i%>")'>Remove
				Task</button></li>
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