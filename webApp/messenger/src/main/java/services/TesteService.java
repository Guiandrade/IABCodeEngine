package services;

import org.guiandrade.TesteResource;

public class TesteService {
	
	public String getTranslation(String text){
		// Main function, will receive Google API code and translate to OpenIAB.
		String success = "success";
		
		TesteResource resource = new TesteResource();
		String checkValue = resource.checkContent(text);
		
		if (success.equals(checkValue)){
			System.out.println("Success on translation!");
			return resource.changeIAB(text);
		}
		else{
			System.out.println("Error!");
			return checkValue;
		}
	}
}
