<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script type="text/javascript">
	function postDbXML() {
		var username = "<%=(String) session.getAttribute("username")%>";
		var url = "/HW5/DbXML";
		var type = $("input[type='radio']:checked").val();
		var posting = $.get(url, {
			username : username,
			type : type
		});

		posting.done(function(data) {
			$("#result_div").empty().text(data);
		});
	}
</script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<div class="col-md-6">
	<label class="radio-inline"><input type="radio" name="type"
		value="1" checked />All users </label>
	<label class="radio-inline"> <input type="radio" name="type"
		value="2" />Friends
	</label>
	<label class="radio-inline"> <input type="radio" name="type"
		value="3" />Friends with offers
	</label>
	<br>
	<br>
	<button class="btn btn-default" onclick='postDbXML()'>Generate</button>
	<div id="result_div"></div>
</div>
</body>
</html>