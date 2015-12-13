package org.cmu.zhexinq.juicyBackend.service;

import java.util.ArrayList;

import javax.servlet.ServletContext;

import org.cmu.zhexinq.juicyBackend.db.JDBCAdapter;
import org.cmu.zhexinq.juicyBackend.db.JuicyDBConstants;
import org.cmu.zhexinq.juicyBackend.util.Utility;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public abstract class JuicyServiceProxy {
	private static JDBCAdapter adapter = new JDBCAdapter(JuicyDBConstants.url, JuicyDBConstants.driverClass,
			JuicyDBConstants.user, JuicyDBConstants.passwd);
	private final static String WRONG_INPUT_RESP = "only support valid json string format";
	private final static String WRONG_INPUT_SPEC_RESP = "JSON specification not correct";
	private final static String FAIL_TO_CREATE_RESP = "fail to persist data according to request";
	private static JuicyService juicyService = null;

	// return upcoming events
	// JSON -> {id, eventDateTime, imgId, creatorEmail, name, description, lon,
	// lat, followers, ImgStr}
	public String getUpcomingEventsByEmail(String email) {
		ArrayList<Long> eventIds = adapter.readEventListByEmailOrderByTime(email);
		JSONArray eventList = new JSONArray();
		JSONObject eventFollowerUser;
		JSONObject creator;
		long count;

		for (Long i : eventIds) {
			// add follower count to the event list json response
			eventFollowerUser = adapter.readEvent(i);
			count = adapter.readEventFollowers(i);
			eventFollowerUser.put("followers", count);
			// add createtor info into event list
			String creatorEmail = (String) eventFollowerUser.get("creatorEmail");
			creator = adapter.readUser(creatorEmail);
			eventFollowerUser.put("creator", creator);
			eventList.add(eventFollowerUser);
		}
		return eventList.toString();
	}

	// create a event in db, if success return event in json format, otherwise
	// return "fail"
	public String createEventFromJSON(String jsonStr, ServletContext context) {
		// get attribute values out of json string
		JSONObject event = (JSONObject) JSONValue.parse(jsonStr);
		if (event == null)
			return WRONG_INPUT_RESP;
		try {
			String creatorEmail = (String) event.get("creatorEmail");
			String name = (String) event.get("name");
			Double lat = (Double) event.get("lat");
			Double lon = (Double) event.get("lon");
			String eventDateTime = (String) event.get("eventDateTime");
			String description = (String) event.get("description");
			// add context colors
			Long imageContextColor = (Long) event.get("imageContextColor");
			Long titleContextColor = (Long) event.get("titleContextColor");
			// get the image string, write the image to disk, and store its url
			String imgStr = (String) event.get("imgStr");
			String imgFormat = (String) event.get("imgFormat");
			long imgId = adapter.nextImgId();
			String url = Utility.writeImageToWebContent(imgStr, context, imgId, imgFormat);
			// persist image data into image table
			imgId = adapter.insertImage(url);
			// persist event data into event table
			long eventId = adapter.insertEvent(creatorEmail, name, lat, lon, eventDateTime, description, imgId,
					imageContextColor, titleContextColor);
			if (eventId > 0)
				return adapter.readEvent(eventId).toJSONString();
		} catch (NullPointerException e) {
			e.printStackTrace();
			return WRONG_INPUT_SPEC_RESP;
		}
		return FAIL_TO_CREATE_RESP;
	}

	// associate a user with an event
	public String setUserJoinEventFromJSON(String jsonStr) {
		// get attribute values out of json string
		JSONObject join = (JSONObject) JSONValue.parse(jsonStr);
		if (join == null)
			return WRONG_INPUT_RESP;
		try {
			String userEmail = (String) join.get("userEmail");
			Long eventId = (Long) join.get("eventId");
			// persist event-user relation data into eventUser table
			if (adapter.readUser(userEmail) != null && adapter.readEvent(eventId) != null) {
				if (adapter.insertEventUserRelation(eventId, userEmail))
					return adapter.readEvent(eventId).toJSONString();
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			return WRONG_INPUT_SPEC_RESP;
		}
		return FAIL_TO_CREATE_RESP;
	}

	// disjoin a user from an event
	public String setUserDisjoinEventFromJSON(String jsonStr) {
		// get attribute values out of json string
		JSONObject disjoin = (JSONObject) JSONValue.parse(jsonStr);
		if (disjoin == null)
			return WRONG_INPUT_RESP;
		try {
			String userEmail = (String) disjoin.get("userEmail");
			Long eventId = (Long) disjoin.get("eventId");
			String resp = adapter.readEvent(eventId).toJSONString();
			// persist event-user relation data into eventUser table
			if (adapter.deleteEventUserRelation(eventId, userEmail))
				return resp;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return WRONG_INPUT_SPEC_RESP;
		}
		return FAIL_TO_CREATE_RESP;
	}

	// return a list of events nearby specified location
	public String exploreEventsFromJSON(String jsonStr) {
		JSONObject geo = (JSONObject) JSONValue.parse(jsonStr);
		if (geo == null)
			return WRONG_INPUT_RESP;
		try {
			// get attribute values out of json string
			Double lon = (Double) geo.get("lon");
			Double lat = (Double) geo.get("lat");
			Double dist = ((Long) geo.get("distance")).doubleValue();
			// search for events within specified distance
			ArrayList<Long> eventIds = adapter.readEventListByGeoOrderByTime(lat, lon, dist);
			JSONArray eventList = new JSONArray();
			JSONObject eventFollowerImage;
			long count;
			JSONObject creator;
			
			for (Long id : eventIds) {
				// add follower count to the event list json response
				eventFollowerImage = adapter.readEvent(id);
				count = adapter.readEventFollowers(id);
				eventFollowerImage.put("followers", count);
				eventList.add(eventFollowerImage);
				// add createtor info into event list
				String creatorEmail = (String) eventFollowerImage.get("creatorEmail");
				creator = adapter.readUser(creatorEmail);
				eventFollowerImage.put("creator", creator);
				eventList.add(eventFollowerImage);
			}
			return eventList.toString();
		} catch (NullPointerException e) {
			e.printStackTrace();
			return WRONG_INPUT_SPEC_RESP;
		}
	}

	// return response for user login request
	// verified -> "status": true
	// not verified -> "status": false
	public String gerUserRegisterResponse(String jsonStr) {
		// get attribute values out of json string
		JSONObject user = (JSONObject) JSONValue.parse(jsonStr);
		JSONObject status = new JSONObject();
		if (user == null)
			return WRONG_INPUT_RESP;
		try {
			// retrieve user information =
			String email = (String) user.get("email");
			String passwd = (String) user.get("password");
			String name = (String) user.get("name");
			// determine whether the email is already exist
			JSONObject dbUser = adapter.readUser(email);
			if (dbUser == null) {
				// insert the user info into db
				adapter.insertUser(email, name, passwd, 1);
				status.put("status", true);
				return status.toString();
			} else {
				status.put("status", false);
				return status.toString();
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			return WRONG_INPUT_SPEC_RESP;
		}
	}

	// return response for user register response
	// verified -> "status": true
	// not verified -> "status": "false
	public String getUserLoginResponse(String jsonStr) {
		// get attribute values out of json string
		JSONObject user = (JSONObject) JSONValue.parse(jsonStr);
		JSONObject status = new JSONObject();
		if (user == null)
			return WRONG_INPUT_RESP;
		try {
			// retrieve user information =
			String email = (String) user.get("email");
			String passwd = (String) user.get("password");
			// determine whether user password & name is correct
			JSONObject dbUser = adapter.readUser(email);
			String dbUsrPasswd = (String) dbUser.get("passwd");
			if (dbUsrPasswd.equals(passwd)) {
				status.put("status", true);
				return status.toString();
			} else
				status.put("status", false);
			return status.toString();
		} catch (NullPointerException e) {
			e.printStackTrace();
			return WRONG_INPUT_SPEC_RESP;
		}

	}
}
