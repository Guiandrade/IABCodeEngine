<%@ page import="org.guiandrade.TesteResource,java.util.*"%>


<jsp:useBean id="teste" class="org.guiandrade.TesteResource">
	<jsp:setProperty name="teste" property="name"
		value="Primeiro teste com o jsp:setProperty!" />
</jsp:useBean>



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

		<form action="index.jsp" method="POST">
			<textarea id="pastedCode" class="code">Paste your code here!</textarea>
			<button name="data" type="button" onclick="doTranslation()">
				Submit</button>
		</form>

	</div>



	<div class="box">
		<h2>OpenIAB Code</h2>
		<p>After pasting your Google API code and clicked the submit
			button, you will see the corresponding IAB code here.</p>

		<textarea id="newCode" class="code">Your OpenIAB will be shown here! </textarea>
	</div>


</body>

<script>

function doTranslation(){ 
	var textBox = document.getElementById("pastedCode").value;
	var xhr = new XMLHttpRequest();
	xhr.open('GET', 'http://localhost:8080/messenger/index.jsp?key=' + encodeURIComponent(textBox), true);
	xhr.send();
	<%
	String texto = request.getParameter("key") ;
	%>
	alert(<%=texto%>);
	
<%--    	var response=<%=resposta%>; --%>
// 		document.getElementById("newCode").value = response;
	
	return true;
}
</script>

</html>
