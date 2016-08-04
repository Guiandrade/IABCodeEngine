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
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
        <script>
            $(document).on("click", "#somebutton", function() { // When HTML DOM "click" event is invoked on element with ID "somebutton", execute the following function...
                $.get("someservlet", function(responseText) {   // Execute Ajax GET request on URL of "someservlet" and execute the following function with Ajax response text...
                    $("#newCode").text(responseText);           // Locate HTML DOM element with ID "newCode" and set its text content with the response text.
                });
            });
        </script>
</head>


<body>
        <button id="somebutton">press here</button>
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
<%--  
<script>

function doTranslation(){ 
	var textBox = document.getElementById("pastedCode").value;
	var xhr = new XMLHttpRequest();
	xhr.open('POST', 'http://localhost:8080/messenger?key=' + encodeURIComponent(textBox), true);
	xhr.send();
	<%
	String texto = request.getParameter("key") ;
	%>
	alert(<%=texto%>);
	
  	var response=<%=resposta%>; 
	document.getElementById("newCode").value = response;
	
	return true;
}
</script>
--%>
</body>



</html>
