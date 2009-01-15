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
 * 				This functionality is currently <b>NOT</b> implemented.  
 * 				A default value of .5 is given for each touch.
 * 				</ul>
 * </ul> 
 * 
 * @version 0.1
 * @author Dennis Chau - Koracas@gmail.com<br>
 */
public class Touches {

	String timeStamp;
	int finger;
	float xPos, yPos, intensity;

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
	Touches( String timeStamp, int finger, float xPos, float yPos, float intensity) {
		this.timeStamp = timeStamp;
		this.finger = finger;
		this.xPos = xPos;
		this.yPos = yPos;
		this.intensity = intensity;
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
	Touches( ) {
		this.timeStamp = null;
		this.finger = 0;
		this.xPos = 0;
		this.yPos = 0;
		this.intensity = 0;
	}
	
	/**
	 * Returns the timeStamp for the touch
	 * @return string
	 */
	public String getTimeStamp() {
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
	
}