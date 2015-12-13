package org.cmu.zhexinq.juicyBackend.api;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cmu.zhexinq.juicyBackend.service.JuicyService;

@Path("user")
public class User {
	private JuicyService service = JuicyService.getJuicyService();

	@Path("login")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String authenticateUser(String jsonStr) {
		return service.getUserLoginResponse(jsonStr);
	}
	
	@Path("register")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String registerUser(String jsonStr) {
		return service.gerUserRegisterResponse(jsonStr);
	}
	
	
}
