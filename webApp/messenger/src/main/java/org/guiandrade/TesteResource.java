package org.guiandrade;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Path;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;


@Path("resources")
public class TesteResource implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name = null;
	private Date date = null;
	private String content = null;
	private final String mandatoryImport = "org.onepf.oms.OpenIabHelper";
	private final String billingImport = "com.android.vending.billing.IInAppBillingService";

	public boolean checkContent(String text){
		// Function that makes simple initial verifications.
		JavaClassSource javaClass = Roaster.parse(JavaClassSource.class, text);
		String exampleText ="Paste your code here!";
		if (text.equals("") || text.equals(exampleText) || !javaClass.hasImport(billingImport)){
			return false;
		}
		else{
			return true;
		}
	}

	public String changeIAB(String text){
		// Function that will do the translation

		JavaClassSource javaClass = Roaster.parse(JavaClassSource.class, text);
		String mandatoryCreation = " new OpenIabHelper.Options.Builder();";
		String setStoreSearchStrategy = "builder.setStoreSearchStrategy(OpenIabHelper.Options.SEARCH_STRATEGY_INSTALLER);\n\t";
		String setVerifyMode = "builder.setVerifyMode(OpenIabHelper.Options.VERIFY_ONLY_KNOWN);\n";
		String helperAssign = "mHelper = new OpenIabHelper(this, builder.build());\n";
		String options = "\n OpenIabHelper.Options.Builder builder ="
				+ mandatoryCreation
				+ setStoreSearchStrategy
				+ setVerifyMode
				+ helperAssign;

		/*
		javaClass.addMethod()
		.setPublic()
		.setStatic(false)
		.setName("setupIAB")
		.setReturnTypeVoid()
		.setBody("OpenIabHelper.Options.Builder builder = new OpenIabHelper.Options.Builder(); \n\t builder.setStoreSearchStrategy(OpenIabHelper.Options.SEARCH_STRATEGY_INSTALLER);");
		 */

		addIabImports(javaClass);

		return changeToIab(javaClass,options);

	}

	private String changeToIab(JavaClassSource javaClass,String newConstructor) {
		// Change constructor and onIabSetupFinished(labResult result) and verify Intent
		String method= "onIabSetupFinished";
		String intent = "Intent(\"com.android.vending.billing.InAppBillingService.BIND\")" ;
		String methodBody="if (result.isSuccess()) { \n\t onServiceConnected(); \n } \n else{ \n\t onServiceDisconnected(); \n }";
		List<MethodSource<JavaClassSource>> methods = javaClass.getMethods();
		int constructorSuccess=0;
		int intentSuccess=0;
		int methodSuccess=0;

		for (MethodSource m : methods){
			if (m.isConstructor()){
				String newBody = newConstructor.concat(m.getBody());
				m.setBody(newBody);
				constructorSuccess=1;
				if (intentSuccess==1 && methodSuccess==1){break;}	
			}
			if (m.getBody().contains(intent)){
				// Talvez necessario parsear o body para verificar que BindService usa este intent.
				intentSuccess=1;
				if (constructorSuccess==1 && methodSuccess==1){break;}	
			}
			System.out.println("name of method -> "+m.getName());
			if (m.getName().equals(method)){
				// Metodo esta dentro do startSetup...e necessario uma alternativa
				m.setBody(methodBody);
				methodSuccess=1;
				if (constructorSuccess==1 && intentSuccess==1){break;}
			}

		}
		if ( constructorSuccess==0 || intentSuccess==0 || methodSuccess==0){
			System.out.println("constructorSuccess -> "+constructorSuccess);
			System.out.println("intentSuccess -> "+intentSuccess);
			System.out.println("methodSuccess -> "+methodSuccess);
			return "Error.";
		}
		else{
			return changeToString(javaClass);
		}
	}

	public String addIabImports(JavaClassSource javaClass){
		//Add mandatory imports to use OpenIAB
		String importHelper= "teste.example.android.trivialdrivesample.util.IabHelper";
		String importResult= "teste.example.android.trivialdrivesample.util.IabResult";
		String importInventory= "teste.example.android.trivialdrivesample.util.Inventory";
		String importPurchase= "teste.example.android.trivialdrivesample.util.Purchase";
		String importIabHelper= "org.onepf.oms.appstore.googleUtils.IabHelper";
		String importIabResult= "org.onepf.oms.appstore.googleUtils.IabResult";
		String importIabInventory= "org.onepf.oms.appstore.googleUtils.Inventory";
		String importIabPurchase= "org.onepf.oms.appstore.googleUtils.Purchase";
		String[] oldImports ={importHelper,importResult,importInventory, importPurchase};
		String[] newImports = {importIabHelper,importIabResult,importIabInventory,importIabPurchase};
		int i=0;

		javaClass.addImport(mandatoryImport);

		while (i < newImports.length){

			if(!javaClass.hasImport(newImports[i])){
				if(javaClass.hasImport(oldImports[i])){
					javaClass.removeImport(oldImports[i]);
				}
				javaClass.addImport(newImports[i]);
			}

			i++;
		}

		return changeToString(javaClass);
	}

	public String changeToString(JavaClassSource javaClass){
		String unformattedText = javaClass.toUnformattedString();
		String formattedText = Roaster.format(unformattedText);
		return formattedText;
	}

	public Date getDate() {
		return date;
	}



	public void setDate(Date date) {
		this.date = date;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getContent() {
		return content;
	}



	public void setContent(String content) {
		this.content = content;
	}



}
