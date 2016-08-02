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

		<form action="index.jsp" method="POST" accept-charset="utf-8">
			<textarea name="pastedCode" class="code">Paste your code here!</textarea>
			<input type="submit" value="Submit" onclick="translate()" />
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

function translate(){ 
	<%String texto = request.getParameter("pastedCode");
			String resposta = teste.getResponse(texto);%>	
   var response="<%=resposta%>";
		document.getElementById("newCode").value = response;
	}
</script>
</html>
