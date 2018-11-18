package application.services;

import java.util.HashMap;

public class ServiceRegistry {
	HashMap<String, Object> services = new HashMap<String, Object>();
	
	public boolean has(String key) {
		return services.containsKey(key);
	}
	
	public Object get(String key) {
		return services.get(key);
	}
	
	public void set(String key, Object value) {
		services.put(key, value);
	}
}
