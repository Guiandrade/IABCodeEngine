package org.guiandrade;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import services.TesteService;

@Path("resources")
public class TesteResource implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name = null;
	private Date date = null;
	private String content = "Default";
	
	TesteService testeService = new TesteService();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getResponse(){
		return testeService.getTranslation();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String add(){
		return "Post works!";
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
