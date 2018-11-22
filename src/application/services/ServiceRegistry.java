package application.services;

import java.util.HashMap;

/**
 * This is a simple service registry implementation. It can be used to store
 * central services or dependencies to be shared across classes, so they do not
 * need to be passed actively. Instead these can be retrieved from the service
 * registry if they were set before.
 * 
 * @author students
 */
public class ServiceRegistry {
	/**
	 * The map of services for classes
	 */
	HashMap<Class<?>, Object> services = new HashMap<Class<?>, Object>();

	/**
	 * Whether there is a service loaded for a class
	 * 
	 * @param key The class of the service to check
	 * @return If the service is loaded
	 */
	public boolean has(Class<?> key) {
		return services.containsKey(key);
	}

	/**
	 * Retrieve the service of a given class
	 * 
	 * @param key The class of the service to load
	 * @return The service of the class
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> key) {
		Object service = services.get(key);
		if (key.isInstance(service)) {
			return (T) service;
		}
		return null;
	}

	/**
	 * Sets a new service for a class
	 * 
	 * @param key   The class to set a new service for
	 * @param value The actual service
	 */
	public <T> void set(Class<T> key, T value) {
		services.put(key, value);
	}
}
