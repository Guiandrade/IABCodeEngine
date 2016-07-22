<!DOCTYPE html>
<html lang="en-us">
<head>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css" href="style.css" />
</head>
<body>

<h1> IAB Code Migration Engine</h1>


<div class="box">
  
  <h2>Google API Code</h2>
  <p>Insert here your Android code with Google In-App Billing calls and press the Submit button to translate to OpenIab code.</p>
  
  <form id="inputForm">
    <textarea id="codePasted" class="code" form="inputForm">Paste your code here!</textarea>
  </form> 
 

  <div class="button">
    <input type="button" onclick="translate()"; class="button" value="Submit!">
  </div>

</div>


<div class="box">
  <h2>OpenIAB Code</h2>
  <p>After pasting your Google API code and clicked the submit button, you will see the corresponding IAB code here.</p> 

  <textarea id="newCode" class="code">Your OpenIAB will be shown here! </textarea>
</div>

<script type="text/javascript">

function translate() {
// Main function of the site, will receive Google API code and translate to OpenIAB.
  var success= 1;
  var checkValue= initialVerification();
  
  if (checkValue == success) {
    document.getElementById('newCode').value=changeIAB();
    alert("Sucess!");
  }
  
  else{
    alert("Error: Please paste your code before submission.");
  }
}

</script>

<script type="text/javascript">

function initialVerification(){
// Function that makes simple initial verifications. Returns 0 for error and 1 for success.
  var textInput = document.getElementById('codePasted').value;
  var exampleTextSize = 21;
  
  if (textInput == '' || textInput.length == exampleTextSize){
      return 0;
  }
  else{
      return 1;
  }
}

</script>

<script type="text/javascript">

function changeIAB(){
// function that will translate the basic classes   
  var textInput = document.getElementById('codePasted').value;
  var googleHelper = /IabHelper /g; // For searching for all instances to be replaced
  var openIabHelper= "OpenIabHelper ";
  var mandatoryImport = "import org.onepf.oms.OpenIabHelper;";
  var textOutput="";
  var newImport="";
  var mandatoryCreation=" new OpenIabHelper.Options.Builder()";
  var setStoreSearchStrategy=".setStoreSearchStrategy(OpenIabHelper.Options.SEARCH_STRATEGY_INSTALLER)\n\t";
  var setVerifyMode=".setVerifyMode(OpenIabHelper.Options.VERIFY_ONLY_KNOWN)\n";
  var helperAssign="mHelper = new OpenIabHelper(this, builder.build());\n"
  var options = "\n// Please put this constructor before starting setup. \n\nOpenIabHelper.Options.Builder builder ="+mandatoryCreation+"\n\t"+setStoreSearchStrategy+setVerifyMode+ "\n// You can also specify .addStoreKeys(storeKeys map)\n\n"+helperAssign;
  
  textOutput = textOutput.concat(textInput);
  textOutput = textOutput.replace(googleHelper,openIabHelper) // How to know when to replace?

  if (!textInput.includes(mandatoryImport) && !textInput.includes(mandatoryCreation)){ //check OpenIabHelper import
    newImport= mandatoryImport.concat("\n\n"); 
    textOutput=newImport.concat(textOutput);
    textOutput= textOutput.concat(options); 
  }

  else if (!textInput.includes(mandatoryImport)){
    newImport= mandatoryImport.concat("\n\n");
    textOutput=newImport.concat(textOutput);
  }

  else{
    textOutput=options.concat(textOutput);
  }
  
  return textOutput;
}  

</script>

</body>
</html>

