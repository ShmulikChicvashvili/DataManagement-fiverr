<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<table border="1" height="100%" width="100%" cellspacing="0" cellpadding="0">
		<tr valign='top'>
			<td width="20%"><%@include file='sidebar.html'%></td>
			<td width="80%"><table border="1" height="100%" width="100%" cellspacing="0"
					cellpadding="0">
					<tr>
						<td height="20%"><%@include file='header.html'%></td>
					</tr>
					<tr>
						<td style="background: #FFFFFF" height="80%"><%@include
								file='users_list.jsp'%></td>
					</tr>
				</table></td>
		</tr>
	</table>
</body>
</html>