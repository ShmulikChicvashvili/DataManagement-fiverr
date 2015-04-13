<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="windows-1255"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>JSP Page.</title>
</head>
<body>
	<div class="col-md-12">
		<p>
			Welcome, <%=(String)session.getAttribute("username") %>. We hope you will
			enjoy your staying in the website. In this website you are welcomed to 
			make new connection, offer activities and register to ones by exchanging
			time.
		</p>
		
	</div>

</body>
</html>