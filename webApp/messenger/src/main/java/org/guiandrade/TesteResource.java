package org.guiandrade;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import services.TesteService;

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


	TesteService testeService = new TesteService();

	/*	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getResponse(String txt){
		return testeService.getTranslation(txt);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String add(){
		return "Post works!";
	}*/

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
		String options = "\n// Please call this constructor before starting setup. \n\nOpenIabHelper.Options.Builder builder ="
				+ mandatoryCreation
				+ "\n\t"
				+ setStoreSearchStrategy
				+ setVerifyMode
				+ "\n// You can also specify .addStoreKeys(storeKeys map)\n\n"
				+ helperAssign;

		javaClass.addMethod().setPublic().setName("setupIAB").setReturnTypeVoid().setBody(options);

		System.out.println("Devia estar aqui o body -> "+javaClass.getMethod("setupIAB").getBody());
		
		if (!text.contains(mandatoryImport)
				&& !text.contains(mandatoryCreation)) { //check OpenIabHelper import

			textOutput= addIabImport(javaClass);
			//	textOutput = textOutput.concat(options);
		}
		else if (!text.contains(mandatoryImport)) {

			textOutput = addIabImport(javaClass);
			textOutput = textOutput.concat(textOutput);
		}
		else {
			textOutput = options.concat(textOutput);
		}
		return textOutput;

	}

	public String addIabImport(JavaClassSource javaClass){
		//Add mandatory import to use OpenIAB
		javaClass.addImport(mandatoryImport);
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
