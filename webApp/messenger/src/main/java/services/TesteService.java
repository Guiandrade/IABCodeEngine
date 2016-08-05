package services;

import org.guiandrade.TesteResource;

public class TesteService {
	
	public String getTranslation(String text){
		// Main function, will receive Google API code and translate to OpenIAB.
		boolean success = true;
		String errorMessage = "*ERROR -> Please review the code you've submitted.*";
		
		TesteResource resource = new TesteResource();
		boolean checkValue = resource.checkContent(text);
		
		if (success == checkValue){
			System.out.println("Success on translation!");
			return resource.changeIAB(text);
		}
		else{
			System.out.println("Error!");
			return errorMessage;
		}
	}
}
