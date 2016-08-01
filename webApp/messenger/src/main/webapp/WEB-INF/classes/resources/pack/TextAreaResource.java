package resources.pack;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


public class TextAreaResource {
	
	   @GET
	   @Produces(MediaType.TEXT_PLAIN)
	    public String getMessage() {
	        return "Hello!";
	    }
	   
	   @POST
	   @Consumes(MediaType.TEXT_PLAIN)
	   @Produces(MediaType.TEXT_PLAIN)
	    public String sendTranslation(String oldCode){
		   return 
	   }
	   
	   public final String getTest(){
		   return "It works!!!";
	   }
	   

}
