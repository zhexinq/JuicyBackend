package org.cmu.zhexinq.juicyBackend.util;

public interface EventExplore {
	public String getUpcomingEventsByEmail(String email);
	public String setUserJoinEventFromJSON(String jsonStr);
	public String setUserDisjoinEventFromJSON(String jsonStr);
	public String exploreEventsFromJSON(String jsonStr);
}
