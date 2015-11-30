package org.cmu.zhexinq.juicyBackend.db;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Service class to serve android app request
 * Created by qiuzhexin on 11/27/15.
 */
@SuppressWarnings("unchecked")
public class JuicyService {
    private static JDBCAdapter adapter = new JDBCAdapter(JuicyDBConstants.url, JuicyDBConstants.driverClass, 
    		JuicyDBConstants.user, JuicyDBConstants.passwd);
    private final static String WRONG_INPUT_RESP = "only support valid json string format";
    private final static String WRONG_INPUT_SPEC_RESP = "JSON specification not correct";
    private final static String FAIL_TO_CREATE_RESP = "fail to persist data according to request";
    
    // return upcoming events 
    // JSON -> {id, eventDateTime, imgId, creatorEmail, name, description, lon, lat, followers, ImgStr}
    public synchronized String getUpcomingEventsByEmail(String email) {
    	ArrayList<Long> eventIds = adapter.readEventListByEmailOrderByTime(email);
    	JSONArray eventList = new JSONArray();
    	JSONObject eventFollowerImage;
    	long count;
    	
		for (Long i : eventIds) {
			// add follower count to the event list json response
			eventFollowerImage = adapter.readEvent(i);
			count = adapter.readEventFollowers(i);
			eventFollowerImage.put("followers", count);
			// add image to the event list json 
			long imgId = (Long) eventFollowerImage.get("imgId");
			String imgStr = adapter.readImage(imgId);
			eventFollowerImage.put("imgStr", imgStr);
			eventList.add(eventFollowerImage);
		}
		return eventList.toString();
    }
    
    // create a event in db, if success return event in json format, otherwise return "fail"
    public synchronized String createEventFromJSON (String jsonStr) {
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
	    	String imgStr = (String) event.get("imgStr");
	    	// persist image data into image table
	    	long imgId = adapter.insertImage(imgStr);
	    	// persist event data into event table
	    	long eventId = adapter.insertEvent(creatorEmail, name, lat, lon, eventDateTime, description, imgId);
	    	if (eventId > 0)
	    		return adapter.readEvent(eventId).toJSONString();
    	} catch (NullPointerException e) {
    		e.printStackTrace();
    		return WRONG_INPUT_SPEC_RESP;
    	}
    	return FAIL_TO_CREATE_RESP;
    }
    
    // associate a user with an event
    public synchronized String setUserJoinEventFromJSON(String jsonStr) {
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
    public synchronized String setUserDisjoinEventFromJSON(String jsonStr) {
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
    public synchronized String exploreEventsFromJSON(String jsonStr) {
    	JSONObject geo = (JSONObject) JSONValue.parse(jsonStr);
    	if (geo == null)
    		return WRONG_INPUT_RESP;
    	try {
    		// get attribute values out of json string
	    	Double lon = (Double) geo.get("lon");
	    	Double lat = (Double) geo.get("lat");
	    	Double dist = ((Long)geo.get("distance")).doubleValue();
	    	// search for events within specified distance
	    	ArrayList<Long> eventIds = adapter.readEventListByGeoOrderByTime(lat, lon, dist);
	    	JSONArray eventList = new JSONArray();
	    	JSONObject eventFollowerImage;
	    	long count;
	    	for (Long id : eventIds) {
	    		// add follower count to the event list json response
				eventFollowerImage = adapter.readEvent(id);
				count = adapter.readEventFollowers(id);
				eventFollowerImage.put("followers", count);
				// add image to the event list json 
				long imgId = (Long) eventFollowerImage.get("imgId");
				String imgStr = adapter.readImage(imgId);
				eventFollowerImage.put("imgStr", imgStr);
				eventList.add(eventFollowerImage);
	    	}
	    	return eventList.toString();	
    	} catch (NullPointerException e) {
    		e.printStackTrace();
    		return WRONG_INPUT_SPEC_RESP;
    	}
    }

    // unit tests of DB functions
    public static void main(String[] args) {
        JDBCAdapter jdbcAdapter = new JDBCAdapter(JuicyDBConstants.url, JuicyDBConstants.driverClass, 
        		JuicyDBConstants.user, JuicyDBConstants.passwd);
        jdbcAdapter.initDB(JuicyDBConstants.initFile);
        // insert a few images to the database
        String img;
        img = Utility.convertImgToStr("/Users/qiuzhexin/Documents/Ethan docs/Photo/watchmen.jpg");
        jdbcAdapter.insertImage(img);
        jdbcAdapter.insertImage(img);
        jdbcAdapter.insertImage(img);

        // inser/update user test
        jdbcAdapter.insertUser("test@test.com", "tester", "123456", 1);
        jdbcAdapter.updateUser("test@test.com", "updater", "upupup", 2);
        // insert duplicate user test
        jdbcAdapter.insertUser("test@test.com", "tester", "12312", 1);
        // read user test
        String jsonStr = jdbcAdapter.readUser("test@test.com").toJSONString();
        System.out.println(jsonStr);
        // insert event test
        jdbcAdapter.insertEvent("eventC@cmu.edu", "sponsor", 12.5, 95.2, "2015-5-27 07:25:51",
                "description", 2);
        // read a event test
        jsonStr = jdbcAdapter.readEvent(2).toJSONString();
        System.out.println(jsonStr);
        // test encoding json array
        JSONArray eventList = new JSONArray();
        JSONObject event1 = jdbcAdapter.readEvent(1);
        JSONObject event2 = jdbcAdapter.readEvent(2);
        eventList.add(event1);
        eventList.add(event2);
        System.out.println(eventList.toJSONString());
        // insert eventUser test
        jdbcAdapter.insertEventUserRelation(2, "lqc@cmu.edu");
        // event list of a user email test
        ArrayList<Long> eventIds = jdbcAdapter.readEventListByEmailOrderByTime("lqc@cmu.edu");
        for (Long i : eventIds) {
            event1 = jdbcAdapter.readEvent(i);
            System.out.println("by email list: " + event1.toJSONString());
        }
        // event population count test
        long event1Count = jdbcAdapter.readEventFollowers(1);
        System.out.println("event 1 has " + event1Count + " followers");
        // unfollow event test
        jdbcAdapter.deleteEventUserRelation(2, "lqc@cmu.edu");
        // find events by geo specification test
        eventIds = jdbcAdapter.readEventListByGeoOrderByTime(23.1, -23.5, 100);
        for (Long i : eventIds) {
            event1 = jdbcAdapter.readEvent(i);
            System.out.println("by geo list: " + event1.toJSONString());
        }
        // test decode image
        String imgStr = adapter.readImage(1);
        Utility.convertStrToImg("/Users/qiuzhexin/Documents/workspace/juicyBackend/new.jpg", imgStr);
    }

}
