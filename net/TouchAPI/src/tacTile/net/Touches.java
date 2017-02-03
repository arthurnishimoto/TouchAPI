/**
 * (./) TouchAPI.java v0.1 01/01/09
 * (by) Dennis Chau 
 * 
 * The Touches Class is essentially a data structure that holds information
 *    about touches that have been recieved over the network connection and 
 *    processed through the TouchAPI.
 */

package tacTile.net;

/**
 * Touches is essentially a data structure that stores information about a touch:<br>
 * <ul>
 * 		<li> <b>Time Stamp:</b><br>
 * 				<ul>
 * 				The time that the touch Occurred. 
 * 				</ul> 			
 * 		<li> <b>FingerID: </b><br>
 * 				<ul>
 * 				The ID give to the finger.
 * 				</ul>
 * 		<li> <b>X Position: </b><br>
 * 				<ul>
 * 				The relative X position of the touch.  
 * 				This is a value between 0 and 1.
 * 				</ul>
 * 		<li> <b>Y Position: </b><br>
 * 				<ul>
 * 				The relative Y position of the touch.  
 * 				This is a value between 0 and 1.
 * 				</ul>
 * 		<li> <b>Intensity: </b><br>
 * 				<ul>
 * 				The relative intensity of the touch.  
 * 				This intensity is an area of the respective touch.
 * 				It will range from one to infinity.
 * 				</ul>
 * </ul> 
 * 
 * @version 0.1
 * @author Dennis Chau - Koracas@gmail.com<br>
 */
public class Touches {

	int finger, gesture;
	long timeStamp;
	float xPos, yPos, intensity;
	float xWidth, yWidth;

	/**
	 * Create a new Touches by passing in another Touch
	 *    
	 * @param srcTouch
	 *            the original touch to be copied 
	 *                    
	 */
	public Touches( Touches srcTouch) {
		this.timeStamp = srcTouch.timeStamp;
		this.finger = srcTouch.finger;
		this.xPos = srcTouch.xPos;
		this.yPos = srcTouch.yPos;
		this.intensity = srcTouch.intensity;
		this.gesture = srcTouch.gesture;
	}
	
	/**
	 * Create a new Touches by passing in 
	 *    
	 * @param timeStamp
	 *            the time the touch was intiated 
	 * @param finger
	 *            the id number for the finger
	 * @param xPos
	 *            the relative x position.  Range btn 0 and 1
	 * @param yPos
	 *            the relative y position.  Range btn 0 and 1
	 * @param intensity 
	 *            the intensity of the touch Range btn 0 and 1        
	 */
	public Touches( long timeStamp, int finger, float xPos, float yPos, float intensity) {
		this.timeStamp = timeStamp;
		this.finger = finger;
		this.xPos = xPos;
		this.yPos = yPos;
		this.intensity = intensity;
		this.gesture = -1;
	}

	/**
	 * Default construtor  
	 *    
	 * @param timeStamp
	 *            the time the touch was intiated 
	 * @param finger
	 *            the id number for the finger
	 * @param xPos
	 *            the relative x position.  Range btn 0 and 1
	 * @param yPos
	 *            the relative y position.  Range btn 0 and 1
	 * @param intensity 
	 *            the intensity of the touch Range btn 0 and 1        
	 */
	public Touches( ) {
		this.timeStamp = 0;
		this.finger = 0;
		this.xPos = 0;
		this.yPos = 0;
		this.intensity = 0;
		this.gesture = -1;
	}
	
	/**
	 * Returns the timeStamp for the touch
	 * @return string
	 */
	public long getTimeStamp() {
		return timeStamp;
	}
	
	/**
	 * Returns the finger id for the touch
	 * @return int
	 */
	public int getFinger() {
		return finger;
	}

	/**
	 * Returns the relative x position for the touch
	 * @return float
	 */
	public float getXPos() {
		return xPos;
	}

	/**
	 * Returns the relative y position for the touch
	 * @return float
	 */
	public float getYPos() {
		return yPos;
	}
	
	/**
	 * Returns the intensity for the touch
	 * @return float
	 */
	public float getIntensity() {
		return intensity;
	}

	/**
	 * Returns the relative x width of the touch
	 * @return float
	 */
	public float getXWidth() {
		return xWidth;
	}

	/**
	 * Returns the relative y width of the touch
	 * @return float
	 */
	public float getYWidth() {
		return yWidth;
	}
	
	/**
	 * Returns the type of gesture the touch is
	 * @return int
	 */
	public int getGesture(){
		return gesture;
	}
	
	/**
	 * Sets a timestamp for a touch
	 * 
	 * @param time 
	 *            the new time stamp
	 */
	public void setFingerID(int id) {
		finger = id;
	}
	
	/**
	 * Sets a timestamp for a touch
	 * 
	 * @param time 
	 *            the new time stamp
	 */
	public void setTimeStamp(long time) {
		timeStamp = time;
	}

	/**
	 * Sets a xPosition for a touch
	 * 
	 * @param xPosition
	 *            the new xPosition
	 */
	public void setXPos( float xPosition) {
		xPos = xPosition;
	}

	/**
	 * Sets a yPosition for a touch
	 *
	 * @param yPosition
	 *            the new yPosition
	 */
	public void setYPos( float yPosition ) {
		yPos = yPosition;
	}

	/**
	 * Sets a intensity for a touch
	 *
	 * @param newIntensity
	 *            the new Intensity
	 */
	public void setIntensity( float newIntensity) {
		intensity = newIntensity;
	}
	
	/**
	 * Sets xWidth for a touch
	 *
	 * @param xW
	 *            the new x width
	 */
	public void setXWidth( float xW ) {
		xWidth = xW;
	}
	
	/**
	 * Sets yWidth for a touch
	 *
	 * @param yW
	 *            the new y width
	 */
	public void setYWidth( float yW ) {
		yWidth = yW;
	}
	
	/**
	 * Sets the gesture type of the touch
	 * 
	 * @param g
	 *            the new gesture
	 */
	public void setGesture( int g ){
		gesture = g;
	}
}
