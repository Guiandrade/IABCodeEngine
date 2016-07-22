package org.guiandrade;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("test")
public class TesteResource {

	   @GET
	   @Produces(MediaType.TEXT_PLAIN)
	    public String getMessage() {
	        return "Hello!";
	    }
}
