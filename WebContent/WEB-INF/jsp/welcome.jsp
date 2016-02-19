<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>Reverse Image Search</title>
<style type="text/css">
body {
	background-image: url('http://crunchify.com/bg.png');
}
</style>
</head>
<body>
	<ul>
		<c:forEach var="userName" items="${message}">
			<li>${userName}</li>
		</c:forEach>
	</ul>
	<br>
	<br>
	<div
		style="font-family: verdana; padding: 10px; border-radius: 10px; font-size: 12px; text-align: center;">
		Reverse Image Search</div>
</body>
</html>