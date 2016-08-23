package services;

import org.guiandrade.MigrationResource;

public class TesteService {
	
	public String getTranslation(String text){
		// Main function, will receive Google API code and translate to OpenIAB.
		String successJava = "success";
		String successXml ="XML";
		
		MigrationResource resource = new MigrationResource();
		String checkValue = resource.checkContent(text);
		
		if (successJava.equals(checkValue)){
			System.out.println("Success on Java Migration!");
			return resource.changeJava(text);
		}
		else if(successXml.equals(checkValue)){
			System.out.println("Success on XML Migration!");
			return resource.changeXml(text);
		}
		else{
			System.out.println("Error!");
			return checkValue;
		}
	}
}
