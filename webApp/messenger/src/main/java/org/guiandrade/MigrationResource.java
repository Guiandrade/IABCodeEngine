package org.guiandrade;

import java.util.List;
import java.util.regex.Pattern;

import javax.ws.rs.Path;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;


@Path("resources")
public class MigrationResource {

	private final String oldBillingImport  = "com.android.vending.billing.IInAppBillingService";
	private final String newBillingImport= "org.onepf.oms.IOpenInAppBillingService";
	private final String oldPermission = "<uses-permission android:name=\"com.android.vending.BILLING\" />";
	private final String newPermission = "<uses-permission android:name=\"com.android.vending.BILLING\" />";

	public String checkContent(String text){
		// Check if there is an error with the code and the it's type (XML or Java)
		String exampleText ="Paste your code here!";
		String errorNull="*Error : You didn't paste any code.*";
		String errorText="*Error : Please paste your code.*";
		String errorImport="*Error : Your class must have the"+newBillingImport+".*";
		String success = "success";
		String successXml = "XML";

		if (text.equals("")){
			return errorNull ;
		}

		if(text.equals(exampleText)){
			return errorText;
		}

		if(text.contains(oldPermission) || text.contains(newPermission)){ // XML
			return successXml;

		}

		JavaClassSource javaClass = Roaster.parse(JavaClassSource.class, text);
		if(!javaClass.hasImport(oldBillingImport) && !javaClass.hasImport(newBillingImport) ){ // JAVA, considered default type

			return errorImport;

		}



		else{
			return success;
		}
	}

	public String changeJava(String text) {
		// Changes Import and Intent if needed 
		JavaClassSource javaClass = Roaster.parse(JavaClassSource.class, text);
		List<MethodSource<JavaClassSource>> methods = javaClass.getMethods();
		String oldIntent="Intent serviceIntent=new Intent(\"com.android.vending.billing.InAppBillingService.BIND\");";
		String oldSetPackage="serviceIntent.setPackage(\"com.android.vending\");";
		String newIntent="Intent serviceIntent = new Intent(\"org.onepf.oms.billing.BIND\");";
		String newSetPackage="\r\nserviceIntent.setPackage(\"cm.aptoide.pt\");";
		String fieldName= "mService";
		String fieldDeclaration="IOpenInAppBillingService mService;";
		if (javaClass.hasImport(oldBillingImport) && !javaClass.hasImport(newBillingImport)){
			javaClass.removeImport(oldBillingImport);
			javaClass.addImport(newBillingImport);
			if (javaClass.hasField(fieldName)){
				@SuppressWarnings("rawtypes")
				Field field = javaClass.getField(fieldName);
				javaClass.removeField(field);
			}
			javaClass.addField(fieldDeclaration);
		}
		
		
		for (@SuppressWarnings("rawtypes") MethodSource m : methods){

			if (m.getBody().contains(newIntent) && m.getBody().contains(newSetPackage) ){
				//new intent already present
				break;
			}

			if (m.getBody().contains(oldIntent)){
				//replace intent
				String body = m.getBody();
				body=body.replace(oldIntent,newIntent);
				body=body.replace(oldSetPackage,newSetPackage);
				m.setBody(body);
				break;
			}

		}

		return changeToString(javaClass);

	}

	public String changeXml(String text) {
		if(text.contains(oldPermission)){
			text=text.replace(oldPermission,newPermission);
		}
		return text;
	}
	
	public String changeToString(JavaClassSource javaClass){
		String unformattedText = javaClass.toUnformattedString();
		String formattedText = Roaster.format(unformattedText);
		return formattedText;
	}


}



