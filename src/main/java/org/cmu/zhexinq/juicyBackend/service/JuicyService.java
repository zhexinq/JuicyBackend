package org.cmu.zhexinq.juicyBackend.service;

import org.cmu.zhexinq.juicyBackend.db.JDBCAdapter;
import org.cmu.zhexinq.juicyBackend.db.JuicyDBConstants;
import org.cmu.zhexinq.juicyBackend.util.EventCreate;
import org.cmu.zhexinq.juicyBackend.util.EventExplore;
import org.cmu.zhexinq.juicyBackend.util.UserLogin;
import org.cmu.zhexinq.juicyBackend.util.UserRegister;
import org.cmu.zhexinq.juicyBackend.util.Utility;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import javax.servlet.ServletContext;

/**
 * Service class to serve android app request
 * Created by qiuzhexin on 11/27/15.
 */
@SuppressWarnings("unchecked")
public class JuicyService extends JuicyServiceProxy 
					      implements EventCreate, EventExplore, UserLogin, UserRegister {
    private static JuicyService juicyService = null;
    
    public static JuicyService getJuicyService() {
    	if (juicyService == null)
    		juicyService = new JuicyService();
    	return juicyService;
    }
    
    private JuicyService() {
    	
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
                "description", 2, 1, 2);
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
        // test decode image and write to web content folder
//        String imgStr = adapter.readImage(4);
    }

}
