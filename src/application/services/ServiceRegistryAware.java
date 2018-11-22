package application.services;

/**
 * Simple interface for classes to indicate that they would require the service
 * registry. Should be used by central loaders / managers to inject the service
 * registry
 *
 * @author students
 */
public interface ServiceRegistryAware {
	public void setServiceRegistry(ServiceRegistry serviceRegistry);
}
