<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html>
<head>
<link href="style.css" rel="stylesheet" type="text/css" media="all">
<meta charset="ISO-8859-1">
<title>Project 3</title>
</head>
<body>
<div class="main"><div class="text">
<%
	String userinput = (String) session.getAttribute("sqlinput");
	String message = (String) session.getAttribute("message");
%>

<h2>Enter SQL Command Here</h2>

<form action="businesslogic" method="post">
<textarea name="sqlinput" rows="20" style="width:100%"><%=userinput %></textarea>
<br><br>
<input type="submit" value="Submit">
</form>
<form action="businesslogic" method="post">
<input type="submit" value="Clear">
</form>

<%=message %>

</div></div>
</body>
</html>