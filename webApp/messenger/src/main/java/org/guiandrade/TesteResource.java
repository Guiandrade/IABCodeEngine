package org.guiandrade;

import java.util.Date;

import javax.ws.rs.Path;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;


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

	public boolean checkContent(String text){
		// Function that makes simple initial verifications.
		String exampleText ="Paste your code here!";
		if (text.equals("") || text.equals(exampleText)){
			return false;
		}
		else{
			return true;
		}
	}

	public String changeIAB(String text){
		// Function that will do the translation

		JavaClassSource javaClass = Roaster.parse(JavaClassSource.class, text);

		String textOutput = "";
		String mandatoryCreation = " new OpenIabHelper.Options.Builder()";
		String setStoreSearchStrategy = ".setStoreSearchStrategy(OpenIabHelper.Options.SEARCH_STRATEGY_INSTALLER)\n\t";
		String setVerifyMode = ".setVerifyMode(OpenIabHelper.Options.VERIFY_ONLY_KNOWN)\n";
		String helperAssign = "mHelper = new OpenIabHelper(this, builder.build());\n";
		String options = "\n // Please call this constructor before starting setup. \n\n OpenIabHelper.Options.Builder builder ="
				+ mandatoryCreation
				+ "\n\t"
				+ setStoreSearchStrategy
				+ setVerifyMode
				+ "\n// You can also specify .addStoreKeys(storeKeys map)\n\n"
				+ helperAssign;


		javaClass.addMethod()
		.setPublic()
		.setStatic(false)
		.setName("setupIAB")
		.setReturnTypeVoid()
		.setBody("OpenIabHelper.Options.Builder builder = new OpenIabHelper.Options.Builder(); \n\t builder.setStoreSearchStrategy(OpenIabHelper.Options.SEARCH_STRATEGY_INSTALLER);");

		System.out.println("Body here -> "+javaClass.getMethod("setupIAB").getBody());
		
		if (!text.contains(mandatoryImport)
				&& !text.contains(mandatoryCreation)) { //check OpenIabHelper import

			textOutput= addIabImports(javaClass);

		}
		else if (!text.contains(mandatoryImport)) {

			textOutput = addIabImports(javaClass);
			textOutput = textOutput.concat(textOutput);
		}
		else {
			textOutput = options.concat(textOutput);
		}
		return changeToIab(textOutput,options);

	}

	private String changeToIab(String textOutput,String newConstructor) {

		String helper= "IabHelper mHelper;";
		String newHelper= "OpenIabHelper mHelper;";
		String constructor = "mHelper = new IabHelper(this, base64EncodedPublicKey);";




		if (textOutput.contains(helper) ){
			textOutput = textOutput.replace(helper,newHelper);
		}
		if (textOutput.contains(constructor)){
			textOutput = textOutput.replace(constructor, newConstructor);
		}

		return textOutput;
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
