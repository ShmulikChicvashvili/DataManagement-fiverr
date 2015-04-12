<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script type="text/javascript">
	function getFriends() {
		var username = $("#username").val();
		var distance = $("input[type='radio']:checked").val();
		var url = "/HW5/search/u/" + username + "?maxDist=" + distance;
		var posting = $.get(url);
		$("#result_div").empty();
		posting.done(function(data) {
			if (data == "maxDist must be 1 or 2") {
				$("#result_div").empty().append(data);
			} else {
				var ul = $('<ul>').appendTo("#result_div");
				for ( var i in data) {
					ul.append($(document.createElement('li')).text(
							"Username: " + data[i].username + " " + "Name: "
									+ data[i].name + " " + "Balance: "
									+ data[i].balance));
				}
			}
		});
	}
</script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	Username:
	<input type="text" id="username" />
	<br>
	<br> Maximum Distance:
	<input type="radio" name="distance" value="1" checked />1
	<input type="radio" name="distance" value="2" />2
	<br>
	<br>
	<div id="result_div"></div>
	<button onclick="getFriends()">Search</button>
</body>
</html>