<html>
<head>
<link href="assets/css/custom-styles.css" rel="stylesheet">
<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script type="text/javascript">
	function searchUser() {
		var url = '/HW5/SearchUser';
		var user = $("#input_text").val();
		var posting = $.get(url, {
			user : user
		});
		posting.done(function(data) {
			if (data == "No such user") {
				document.getElementById("add_friend").style.display = "none";
			} else {
				document.getElementById("add_friend").style.display = "block";
			}
			$("#result_label").empty().append(data);
		});
	}
	function addFriend(username) {
		var url = '/HW5/AddFriend';
		var user = $("#input_text").val();
		var posting = $.post(url, {
			first_username : user,
			second_username : username
		});
		
		posting.done(function(data) {
			$("#result_div_friend").empty().append(data);
			if(data == "Friend Added") {
				document.getElementById("add_friend").disabled = true;
			}
		});
	}
</script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

	<div class="col-lg-6">
		<label>Search for user</label>
		<div class="form-group input-group">
			<input type="text" id="input_text" class="form-control"
				placeholder="username"> <span class="input-group-btn">
				<button class="btn btn-default" type="button" onclick="searchUser()">
					<i class="fa fa-search"></i>
				</button>
			</span>
		</div>
		<div id="result_div"><label id="result_label"></label></div>
		<button class="btn btn-default"
			onclick='addFriend("<%=(String) session.getAttribute("username")%>")'
			style="display: none;" id="add_friend">Add Friend</button>
		<div id="result_div_friend"></div>
	</div>

</body>
</html>