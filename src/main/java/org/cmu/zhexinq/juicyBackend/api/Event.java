package org.cmu.zhexinq.juicyBackend.api;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.servlet.ServletContext;

import org.cmu.zhexinq.juicyBackend.db.JDBCAdapter;
import org.cmu.zhexinq.juicyBackend.db.JuicyDBConstants;
import org.cmu.zhexinq.juicyBackend.service.JuicyService;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

@Path("event")
@SuppressWarnings("unchecked")
public class Event {
	@Context 
	private ServletContext context;
	private JuicyService service = JuicyService.getJuicyService();

	@Path("upcoming/{email}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getUpcomingEvents(@PathParam("email") String email) throws SQLException {
		return service.getUpcomingEventsByEmail(email);
	}
	
	@Path("create")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String createEvent(String jsonStr) {
		return service.createEventFromJSON(jsonStr, context);
	}
	
	@Path("join")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String joinEvent(String jsonStr) {
		return service.setUserJoinEventFromJSON(jsonStr);
	}
	
	@Path("disjoin")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String disjoinEvent(String jsonStr) {
		return service.setUserDisjoinEventFromJSON(jsonStr);
	}
	
	@Path("explore")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String exploreEvents(String jsonStr) {
		return service.exploreEventsFromJSON(jsonStr);
	}
	
}
