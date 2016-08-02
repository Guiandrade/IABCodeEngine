<%@ page import="org.guiandrade.TesteResource,java.util.*"%>
<jsp:useBean id="teste" class="org.guiandrade.TesteResource">
	<jsp:setProperty name="teste" property="name"
		value="Primeiro teste com o jsp:setProperty!" />
</jsp:useBean>
<jsp:useBean id="date" class="java.util.Date" />

<p>
	The date/time is
	<%=date%>
</p>
<p>
	The name of the resource object is:
	<jsp:getProperty name="teste" property="name" />
</p>
<p>
	<%= teste.getResponse() %>
</p>

<!DOCTYPE html>
<html lang="en-us">
<head>

<meta charset="UTF-8">
<link rel="stylesheet" type="text/css" href="style.css" />

</head>
<body>

	<h1>IAB Code Migration Engine</h1>

	<div class="box">

		<h2>Google API Code</h2>
		<p>Insert here your Android code with Google In-App Billing calls
			and press the Submit button to translate to OpenIab code.</p>

		<form action="index.jsp" method="GET">
			<input type="text" name="pastedCode" class="code">Paste your code here!<br />
			<input type="submit" value="Submit" />
		</form>

	</div>


	<div class="box">
		<h2>OpenIAB Code</h2>
		<p>After pasting your Google API code and clicked the submit
			button, you will see the corresponding IAB code here.</p>

		<textarea id="newCode" class="code">Your OpenIAB will be shown here! </textarea>
	</div>

</body>
</html>

