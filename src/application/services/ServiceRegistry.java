package application.services;

import java.util.HashMap;

public class ServiceRegistry {
	HashMap<Class<?>, Object> services = new HashMap<Class<?>, Object>();
	
	public boolean has(Class<?> key) {
		return services.containsKey(key);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> key) {
		Object service = services.get(key);
		if (key.isInstance(service)) {
			return (T) service;
		}
		return null;
	}
	
	public <T> void set(Class<T> key, T value) {
		services.put(key, value);
	}
}
