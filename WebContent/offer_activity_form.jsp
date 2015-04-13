<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script type="text/javascript">
	function postActivity(username) {
		var url = '/HW5/OfferActivity';
		var type = $("input[type='radio']:checked").val();
		var title = jQuery(document.getElementsByName("title")).val();
		var capacity = jQuery(document.getElementsByName("capacity")).val();
		var posting = $.post(url, {username: username, type:type, title:title,capacity:capacity});
		
		posting.done(function(data) {
			if(data=="") {
				window.location.href = "/HW5/my_activities.jsp";
			}
			else {
				$("#result_div").empty().append(data);
			}
		});
	}
</script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<div class="col-lg-6">
		<label>Offer Activity</label> <br>
		<br>
		<label class="radio-inline"><input type="radio"
			name="activity" value="Task" checked />Task</label> <label
			class="radio-inline"><input type="radio" name="activity"
			value="Service" />Service</label><br><br> <input class="form-control"
			placeholder="Title" type="text" name="title" /> <br> <input
			type="text" class="form-control" placeholder="Capacity"
			name="capacity" /><br>
		<button class="btn btn-default"
			onclick='postActivity("<%=(String) session.getAttribute("username")%>")'>
			Offer the activity</button>
		<div id="result_div"></div>
	</div>
</body>
</html>