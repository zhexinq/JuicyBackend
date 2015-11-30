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
    
    // return upcoming events 
    // JSON -> {id, eventDateTime, imgUri, creatorEmail, name, description, lon, lat, followers}
    public synchronized String getUpcomingEventsByEmail(String email) {
    	ArrayList<Integer> eventIds = adapter.readEventListByEmailOrderByTime(email);
    	JSONArray eventList = new JSONArray();
    	JSONObject eventAndFollower;
    	int count;
    	
		for (Integer i : eventIds) {
			eventAndFollower = adapter.readEvent(i);
			count = adapter.readEventFollowers(i);
			eventAndFollower.put("followers", count);
			eventList.add(eventAndFollower);
		}
		return eventList.toString();
    }
    
    // create a event in db, if success return event in json format, otherwise return "fail"
    public synchronized String createEventFromJSON (String jsonStr) {
    	// get attribute values out of json string
    	JSONObject event = (JSONObject) JSONValue.parse(jsonStr);
    	if (event != null) {
	    	String creatorEmail = (String) event.get("creatorEmail");
	    	String name = (String) event.get("name");
	    	Double lat = (Double) event.get("lat");
	    	Double lon = (Double) event.get("lon");
	    	String eventDateTime = (String) event.get("eventDateTime");
	    	String description = (String) event.get("description");
	    	String imgStr = (String) event.get("imgStr");
	    	// persist image data into image table
	    	int imgId = adapter.insertImage(imgStr);
	    	// persist event data into event table
	    	int eventId = adapter.insertEvent(creatorEmail, name, lat, lon, eventDateTime, description, imgId);
	    	if (eventId > 0)
	    		return adapter.readEvent(eventId).toJSONString();
    	}
    	return "fail to create the event";
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
        ArrayList<Integer> eventIds = jdbcAdapter.readEventListByEmailOrderByTime("lqc@cmu.edu");
        for (Integer i : eventIds) {
            event1 = jdbcAdapter.readEvent(i);
            System.out.println("by email list: " + event1.toJSONString());
        }
        // event population count test
        int event1Count = jdbcAdapter.readEventFollowers(1);
        System.out.println("event 1 has " + event1Count + " followers");
        // unfollow event test
        jdbcAdapter.deleteEventUserRelation(2, "lqc@cmu.edu");
        // find events by geo specification test
        eventIds = jdbcAdapter.readEventListByGeoOrderByTime(23.1, -23.5, 100);
        for (Integer i : eventIds) {
            event1 = jdbcAdapter.readEvent(i);
            System.out.println("by geo list: " + event1.toJSONString());
        }
        // test decode image
        String imgStr = adapter.readImage(1);
        Utility.convertStrToImg("/Users/qiuzhexin/Documents/workspace/juicyBackend/new.jpg", imgStr);
    }

}
