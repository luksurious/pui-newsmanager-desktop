package application;

/**
 * This interface enables controllers to listen on certain lifecycle event of a scene.
 * 
 * For our purposes only a method which is called when a scene is shown is implemented.
 */
public interface ControllerEvents {
	
	/**
	 * This method is called just before a scene is shown.
	 * It can be used to update the UI before it is actually visible.
	 */
	public void onBeforeShow();
}
