package org.cmu.zhexinq.juicyBackend;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cmu.zhexinq.juicyBackend.db.JDBCAdapter;
import org.cmu.zhexinq.juicyBackend.db.JuicyDBConstants;
import org.cmu.zhexinq.juicyBackend.db.JuicyService;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

@Path("event")
@SuppressWarnings("unchecked")
public class Event {
	private JuicyService service = new JuicyService();

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
		return service.createEventFromJSON(jsonStr);
	}
	
	
	
	
	
}
