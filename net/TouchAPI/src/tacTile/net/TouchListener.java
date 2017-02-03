package tacTile.net;

/**
 * Interface used to set event call-back functions. To receive events,
 * implement this class and pass to TouchAPI via setTouchListener().
 * 
 * @author Arthur Nishimoto - anishimoto42@gmail.com
 *
 */
public interface TouchListener {
	/**
	 * Called on any touch event.
	 * @param t A touch 
	 */
	public void onInput(Touches t);
}// interface