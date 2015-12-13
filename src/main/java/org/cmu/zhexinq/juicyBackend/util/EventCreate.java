package org.cmu.zhexinq.juicyBackend.util;

import javax.servlet.ServletContext;

public interface EventCreate {
	public String createEventFromJSON(String jsonStr, ServletContext context);
}
