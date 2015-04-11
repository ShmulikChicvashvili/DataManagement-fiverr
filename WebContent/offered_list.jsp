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
	function registerActivity(id, username, index, element) {
		var url = '/HW5/RegisterActivity';
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
				activitiesDB.getActivitiesOfferedToUserCount((String) session
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
			System.out.println(pageStart + " " + totalCount);
			List<DBPaidService> offeredServices =
				activitiesDB.getServicesOfferedToUser(
					(String) session.getAttribute("username"),
					pageStart,
					perPage);
			List<DBPaidTask> offeredTasks =
				activitiesDB.getTasksOfferedToUser(
					(String) session.getAttribute("username"),
					pageStart,
					perPage);
			for (int i = 0; i < Math.max(
				offeredServices.size(),
				offeredTasks.size()); i++)
			{
		%>
		<%
			if (i < offeredServices.size())
				{
		%>
		<li id='Service<%=i%>'><h5>Service-</h5>Username:<%=offeredServices.get(i).getUsername()%>
			&emsp; Title:<%=offeredServices.get(i).getTitle()%> &emsp; Capacity:<%=offeredServices.get(i).getCapacity()%>
			&emsp; Number registered:<%=offeredServices.get(i).getNumRegistered()%>
			&emsp;
			<button
				onclick='registerActivity("<%=offeredServices.get(i).getId()%>","<%=(String) session.getAttribute("username")%>","<%=i%>", "Service<%=i%>")'>Register
				to service</button></li>
		<%
			}
				if (i < offeredTasks.size())
				{
		%>
		<li id='Task<%=i%>'><h5>Task-</h5>Username:<%=offeredTasks.get(i).getUsername()%>
			&emsp; Title:<%=offeredTasks.get(i).getTitle()%> &emsp; Capacity:<%=offeredTasks.get(i).getCapacity()%>
			&emsp; Number registered:<%=offeredTasks.get(i).getNumRegistered()%>
			&emsp;
			<button
				onclick='registerActivity("<%=offeredTasks.get(i).getId()%>","<%=(String) session.getAttribute("username")%>","<%=i%>", "Task<%=i%>")'>Register
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