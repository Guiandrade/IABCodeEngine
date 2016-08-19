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
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js">
</script>

<script>
	$(document).on("click", "#data", function() { // When HTML DOM "click" event is invoked on element with ID "data", execute the following function...
		var text = document.getElementById("pastedCode").value;

		$.post("someservlet", {
			pastedCode : text
		}, function(response) {
			$("#newCode").text(response);
		});

	});
</script>
</head>


<body>
<style>
body {
    background-color: lightblue;
}
</style>
	<h1>IAB Code Migration Engine</h1>
	
	<div style="text-align: left">
	<a href="https://github.com/onepf/OpenIAB/wiki/How-To-add-OpenIAB-to-an-app" target="_blank">Click Here to Know How to Add OpenIab to an App.</a>
	</div>
	
	<div class="box">

		<h2>Google API Code</h2>
		<p>Insert here your Android code with Google In-App Billing calls
			and press the Submit button to translate to OpenIab code.</p>

		<form action="index.jsp">
			<textarea id="pastedCode" class="code">Paste your code here!</textarea>
				<button id="data" class="button" type="button" >Submit</button>
			
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
