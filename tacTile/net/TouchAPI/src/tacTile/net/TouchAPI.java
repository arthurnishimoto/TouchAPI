/**
 * (./) TouchAPI.java v0.1 01/01/09
 * (by) Dennis Chau
 * (cc) some right reserved
 *
 * An addition to the Processing Libraries.  Written primarily to 
 * communicate with TacTile at the Electronic Visualization Lab 
 * at the University of Illinois at Chicago.
 * 
 * http://www.evl.uic.edu/core.php?mod=4&type=4&indi=602
 *      
 * The TouchAPI library utilizes the networking capabilities provided by 
 *    Processing as well as the UDP library provided by 
 *    Douglas Edric Stanley & Cousot Stéphane.  The UDP class was slightly
 *    modified for this purpose.  See UDP Class for details.
 *    
 *  * THIS LIBRARY IS RELEASED UNDER A CREATIVE COMMONS LICENSE BY.
 * -> http://creativecommons.org/licenses/by/2.5/
 */

package tacTile.net;

import java.util.*;
import java.text.SimpleDateFormat;
import java.net.InetAddress;
import java.net.UnknownHostException;

import processing.core.*;
import processing.net.*;
import hypermedia.net.*;

/**
 * TouchAPI is an addition to the Processing Libraries written so Processing applications could communicate with the TacTile.<br>
 * Tac Tile is a Multi-touch table begin researched at the Electronic Visualization Lab at the University of Illinois at Chicago.
 * 
 * <br>
 * <br>
 * The TouchAPI constructor creates and manages communications between a <u>touch server</u> and a <u>Processing application</u>.<br>
 * <br>
 * Connections are facilitated by simply calling the TouchApi constructors.  However, there are two constructors that can be called depending on the type of touch server.
 * <ul>
 *		<li><b>TacTile Touch Server:</b> Processing applications that are loaded onto TacTile for testing can access  
 * 			touch data from the TacTile touch server.  This is done by simply calling this constructor:<br> 
 *			{@link TouchAPI#TouchAPI(Object owner)}
 *		<br><br>
 * 		<li><b>Local Touch Server:</b> Often times during development a Multi-Touch Pad is used.  Construction and drivers for such a device can be found <a href="http://www.evl.uic.edu/spiff/class/cs426/index.html"> here.</a>
 * 			Installing the drivers allows input on the Multi-Touch Pad to be sent to your personal computer. 
 * 			Your machine is now a local touch server! 
 * 			In conjunction with TouchAPI, both the local touch server and Processing application can run on the same machine.
 * 		    This is done by simply calling this constructor:<br> 
 *			{@link TouchAPI#TouchAPI(Object owner, int data_port)}
 * </ul>
 * <br> 
 * Both these connection will tell TouachAPI to collect touch data from the touch server.
 * The data is then processed and stored as an ArrayList inside the TouchAPI.  Various
 * functions will allow users to access the touch data:<br>
 * <ul>
 * 	<b>Retrieval</b> 
 * 		<ul>
 * 		<li>{@link TouchAPI#getTouch( int )}
 * 		<li>{@link TouchAPI#getLeastRecentTouch()}
 * 		<li>{@link TouchAPI#getMostRecentTouch()}
 * 		</ul>
 * 	<br>
 * 	<b>Clearing</b>
 * 		<ul>
 * 		<li>{@link TouchAPI#clearAllButEarliestTouch()}
 *  	<li>{@link TouchAPI#clearAllTouches()}
 * 		</ul>
 * 	<br>
 * 	<b>List Attributes</b>
 * 		<ul> 
 * 		<li>{@link TouchAPI#listIsEmpty()}
 * 		<li>{@link TouchAPI#listSize()}
 * 		</ul>
 * </ul>
 * 
 * @version 0.1
 * @author Dennis Chau - Koracas@gmail.com<br>
 */
public class TouchAPI {

	int port_tcp; 		// port to send the TCP msg in order to start sending datagrams
	int port_udp; 		// port open where to recieve UDP msgs.
	String serverName; 	// machine where the datagrams will come from

	Client clientForServer; // The Client obj that will comm with Tac Tile
	UDP socketForData;		// The udp socket that will recieve the data 
	
	//the Array List that will hold all the processed touches
	ArrayList<Touches> touchList = new ArrayList<Touches>();

	boolean debug = false; 			// true if the debug method should print debug msgs
	boolean connected = false; 		// true if there is a server connection
	boolean dataOn = false; 		// true if server as told to send data
	boolean server = false; 		// true if there is server info
	boolean log = false; 			// enable/disable output log
	boolean listen = false; 		// true, if the socket waits for packets

	// the parent object (could be an application, a componant, etc...)
	Object owner = null;

	// The "receive handler" methods name.  This is the method name
	//   that the UDP will call when the socket recieves data. 
	String modHandler = "__tacTile_Polling__";

	// the log "header" to be set for debugging. 
	String header = "";

	//Currently intensity is not part of the info sent in, but a hardcoded value
	float intensity = (float) .5;
	
	// /////////////////////////////// fields ///////////////////////////////
	/**
	 * The default length of the TCP msg
	 */
	private static final int TCP_LENGTH = 99;
	/**
	 * The TCP msg to initiate data transfer
	 */
	private static final String MSG_TCP_SEND_DATA = "data_on,";

	/**
	 * The default number of touches list can hold
	 */
	private static final int LIST_SIZE = 200;
	
	/**
	 * The IP for TacTile Touch Server
	 */
	private static final String TACTILE_IP = "tactile.evl.uic.edu";

	/**
	 * The port on TacTile to send msgs to
	 */
	private static final int TCP_PORT = 7340;
	
	/**
	 * The socket where data from TacTile should be sent
	 */
	private static final int UDP_PORT = 7075;

	// //////////////////////////// constructors ////////////////////////////
	/**
	 * Default constructor that will create a connection between the TacTile touch server and a Processing Application.
	 * 
	 * @param owner
	 *            the parent object so the class knows how to get back to the Processing Application
	 */
	public TouchAPI(Object owner) {
		this( owner, UDP_PORT, TCP_PORT, TACTILE_IP);
	}

	/**
	 * Constructor that will create a connection between a local touch server and a Processing Application. 
	 * Consequently both these applications will be running on the same machine.
	 * In order to establish this connection, all that is needed is a port where the data should be stored.
	 * 
	 * @param owner
	 *            the parent object so the class knows how to get back to the Processing Application
	 * @param touchPort
	 *            the port where the touch server should send the data.  A suggested port number is 7000. 
	 */
	public TouchAPI(Object owner, int touchPort) {
		this( owner, touchPort, TCP_PORT, null);
	}
	
	/**
	 * Constructor that creates a new connection between a touch server and a Processing application.
	 *     
	 * @param owner
	 *            the parent object so the class knows how to get back to the Processing Application
	 * @param udp_port
	 *            port where touch server will send data to 
	 * @param tcp_port
	 *            port on the touch server to initiate message passing
	 * @param serverIP
	 *            address for the touch server ( your machine's IP address)
	 */
	private TouchAPI(Object owner, int udp_port, int tcp_port, String serverIP) {
		this.owner = owner;

		// Register this object to the PApplet
		try {
			if (owner instanceof PApplet)
				((PApplet) owner).registerDispose(this);
		} catch (NoClassDefFoundError e) {
			;
		}

		// Open UDP Socket
		if (udp_port != 0) {
			this.port_udp = udp_port;
			socketForData = new UDP(this, udp_port);
			socketForData.setReceiveHandler(modHandler);
			debug( "Opened socket:" + udp_port() );
		}

		// Open connection to server
		if (tcp_port != 0 && serverIP == null) {
			try {
				InetAddress address = InetAddress.getLocalHost();

				byte[] ip = address.getAddress();

				int i = 4;
				String ipAddress = "";
				for (byte b : ip) {
					ipAddress += (b & 0xFF);
					if (--i > 0) {
						ipAddress += ".";
					}
				}
				this.serverName = ipAddress;
				this.port_tcp = tcp_port;
				this.server = true;
				// Initialize connection with server
				clientForServer = new Client((PApplet) owner, serverName, port_tcp);
				this.connected = true;
				debug( "Opened connection to server:" + serverAddy() + ", port: " + tcp_port());

			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		} else if (tcp_port != 0 && serverIP != null) {
			this.serverName = serverIP;
			this.port_tcp = tcp_port;
			this.server = true;
			// Initialize connection with server
			clientForServer = new Client((PApplet) owner, serverName, port_tcp);
			this.connected = true;
			debug( "Opened connection to server:" + serverAddy() + ", port: " + tcp_port());
		} else { 
			this.server = false;
			this.connected = false;
		}
		
		//Initiate data transfer
		this.dataOn = false;
		initHandShake();
		debug( "Data Ready to be tranfered.");

		//Tell port to start polling
		udpListen(true);
		debug( "Socket is listening.");
		
	}

	// ///////////////////////////// methods ///////////////////////////////

	/**
	 * Close the connections that are made to touch server and the Processing Application.
	 * This method is automatically called by Processing when the PApplet shuts down.<br>
	 * You <i>DO NOT</i> need to call this method. 
	 */
	public void dispose() {
		udpClose();
		tcpClose();
	}

	/**
	 * Closes the server connection.
	 */
	private void tcpClose() {
		if (connected()) {
			int port = tcp_port();
			String ip = serverAddy();

			// close the connection
			clientForServer.stop();
			clientForServer = null;
			log("close socket < port:" + port + ", address:" + ip + " >\n");
		} else {
			error("Tried to close a connection to a server that was not there!");
		}
	}

	/**
	 * Close the socket and all associate resources.
	 */
	private void udpClose() {
		socketForData.close();
	}

	/**
	 * Return true if socket is closed
	 * 
	 * @return boolean
	 */
	private boolean socketIsClosed() {
		return socketForData.isClosed();
	}
	
	/**
	 * Return true if connected to server
	 * 
	 * @return boolean
	 */
	private boolean connected() {
		return connected;
	}

	/**
	 * Return the socket on the server where msgs are passed, or -1 if not
	 * connected to a server.
	 * 
	 * @return int
	 */
	protected int tcp_port() {
		if (server == false)
			return -1;
		return port_tcp;
	}

	/**
	 * Return the actual socket's local port, or -1 if the socket is closed.
	 * 
	 * @return int
	 */
	protected int udp_port() {
		return socketForData.port();
	}

	/**
	 * Return the actual socket's local address
	 * 
	 * @return String
	 */
	protected String localAddy() {
		if (socketIsClosed())
			return null;
		return socketForData.address();
	}

	/**
	 * Return the server's address, or <code>null</code> if not connected to a
	 * server
	 * 
	 * @return String
	 */
	protected String serverAddy() {
		if (server == false)
			return null;
		return serverName;
	}

	/**
	 * Returns whether the socket wait for incoming data or not.
	 * 
	 * @return boolean
	 */
	private boolean socketListening() {
		return socketForData.isListening();
	}

	/**
	 * Tell UDP socket to Start/stop waiting constantly for incoming data.
	 * 
	 * @param on
	 *            the required listening status.
	 * 
	 */
	private void udpListen(boolean on) {
		socketForData.listen(on);
	}

	/**
	 * Tells the server to start sending msgs to the UDP socket
	 */
	private void initHandShake() {
		if (server == true) {
			if (connected == true) {
				String MsgTCPInit = MSG_TCP_SEND_DATA;
				MsgTCPInit = MsgTCPInit + port_udp;
				MsgTCPInit = padMsg(MsgTCPInit);
				clientForServer.write(MsgTCPInit);
				this.dataOn = true;
			} else {
				// TODO:: insert exception here
				error("no connection to server");
				System.exit(1);
			}
		} else {
			// TODO:: insert exception here
			error("no server info");
			System.exit(1);
		}
	}

	/**
	 * Returns whether the server was told to send data
	 * 
	 * @return boolean
	 */
	private boolean dataOnSent() {
		return dataOn;
	}

	/**
	 * Pads the msgs bc they need to be 99 char long.  Then returns the complete string
	 * 
	 * @param msg
	 *           The message that will be padded 
	 * @return boolean
	 */
	private String padMsg(String msg) {
		int length = msg.length();

		if (length > TCP_LENGTH) {
			System.out.println("ERROR : TCP Msg");
			System.out.println("   Tried to pad the UDP msg: " + msg);
			System.out.println("   However it was longer than: " + TCP_LENGTH);
			System.exit(1);
		}

		String pad = "";
		for (int padLoc = 0; padLoc < (TCP_LENGTH - length); padLoc++) {
			pad = pad + " ";
		}
		msg = msg + pad;
		return msg;
	}

	/**
	 * Parses the messages that are sent to the socket.  The messages are passes 
	 * in as string in the following format:<br>
	 * <ul> 
	 * timestamp:flag:finger_ID, xPos, yPos<br>
	 * <small>
	 * *Note: that the xpos and yPos are ratios between 0 to 1
	 * </small>
	 * </ul>
	 * They are then parsed out with the data types:
	 * <ul>
	 *   String : String : int, float, float
	 * </ul>
	 * These values are then passed to the Touch constructor where they are stored:
	 * The Touch that is returned from the constructor is then stored inside an ArrayList
	 * of touches.  These touches can then be accessed by various methods  
	 * 
	 * @param dGram
	 *           The datagram that was sent to the  socket 
	 * @return Touches
	 * @see Touches#Touches(String flag, String timeStamp, int finger, float xPos, float yPos)
	 */
	private Touches parseDGram(String dGram) {
		int firstSpace = dGram.indexOf(" ", 0);
		dGram = dGram.substring( 0, firstSpace );
		
		String flag, timeStamp;
		int finger;
		float xPos, yPos;

		int start, last, sub_last;
		int maxLen = dGram.length() - 1;

		// flag //
		start = dGram.indexOf(":", 0);
		sub_last = start;
		flag = dGram.substring(start + 1, start + 2);

		// Only message a d flag are useful. //
		if (flag.compareTo("d") == 0) {
			log("MSG received : <"+dGram+">");
			
			debug("  Flag: -" + flag + "-" );
			
			// time stamp //
			timeStamp = dGram.substring(0, sub_last);
			last = sub_last;
			debug("  Time: -" + timeStamp + "-" );
			
			// finger //
			start = dGram.indexOf(":", last + 1);
			sub_last = start;
			last = sub_last;
			
			start = dGram.indexOf(",", last + 1);
			sub_last = start;
			finger = Integer.parseInt(dGram.substring(last + 1, sub_last));
			last = sub_last;
			debug( " Finger: -" + finger + "-" );
			
			// x pos //
			start = dGram.indexOf(",", last + 1);
			sub_last = start;
			xPos = Float.valueOf(dGram.substring(last + 1, sub_last)).floatValue();
			last = sub_last;
			debug( " XPos: -" + xPos + "-" );
			
			// y pos //
			yPos = Float.valueOf(dGram.substring(last + 1, maxLen)).floatValue();
			debug( " YPos: -" + yPos + "-" );

			Touches curTouch = new Touches(timeStamp, finger, xPos, yPos, intensity);
			return curTouch;
		} else {
			// System.out.println( " Msg Dropped." );
			return null;
		}
	}

	/**
	 * Process data that is transfered into the socket. Creates a touch out of
	 * it and stores it in the touchList variable.
	 * 
	 * @param data
	 *            the data byte array of data
	 */
	private void processData(byte[] data) {
		String dGram = new String(data);
		Touches newTouch = parseDGram(dGram);
		if (newTouch != null) {
			addTouch(newTouch);
		}
	}

	/**
	 * Adds a new touch to the touchList
	 * 
	 * @param newTouch
	 *            A Touches object to be added to touchList 
	 */
	private void addTouch(Touches newTouch) {
		if (listSize() > LIST_SIZE) {
			removeTouch(0);
		}
		touchList.add(newTouch);
	}

	/**
	 * Returns a specified Touches in the touchList.  If the list is empty, a Null is returned.<br>  
	 * Once the touch is retrieved, the Touches are <b> removed </b>.
	 * 
	 * @param index
	 *            The index of the desired touch
	 * @return 	Touches if touchList is not empty<br>
	 * 			OR<br> 
	 * 			Null if touchList is empty 
	 */
	public Touches getTouch(int index) {
		if ( !listIsEmpty() ) {
			Touches curTouch = touchList.get(index);
			removeTouch(index);
			return curTouch;
		} else {
			return null;
		}
	}

	/**
	 * Returns a the oldest touch on the touchList.   If the list is empty, a Null is returned.<br>  
	 * Once the touch is retrieved, the Touches are <b> removed </b>.
	 * 
	 * @return 	Touches if touchList is not empty -OR- Null if touchList is empty 
	 */
	public Touches getLeastRecentTouch() {
		Touches curTouch = getTouch(0);
		return curTouch;
	}

	/**
	 * Returns a the newest touch on the touchList.   If the list is empty, a Null is returned.  <br>
	 * Once the touch is retrieved, the Touches are <b> removed </b>.
	 * 
	 * @return 	Touches if touchList is not empty -OR- Null if touchList is empty
	 */
	public Touches getMostRecentTouch() {
		int latest = listSize() - 1;
		Touches curTouch = getTouch(latest);
		return curTouch;
	}

	/**
	 * Removes a specified element from the touchList
	 * 
	 * @param index
	 *            The index of the desired touch
	 */
	private void removeTouch(int index) {
		touchList.remove(index);
	}

	/**
	 * Returns the size of the touchList
	 * @return int
	 */
	public int listSize() {
		return touchList.size();
	}

	/**
	 * Returns true if touchList is empty
	 * @return boolean
	 */
	public boolean listIsEmpty() {
		return touchList.isEmpty();
	}

	/**
	 * Clears all the Touches in the touchList
	 */
	public void clearAllTouches() {
		touchList.clear();
	}

	/**
	 * Clears all the Touches in the touchList except for the newest one
	 */
	public void clearAllButEarliestTouch() {
		int size = listSize();
		if (size > 1 ){
			int last = size - 1;
			Touches temp = new Touches();
			temp = touchList.get(last);
			clearAllTouches();
			addTouch(temp);
		} else {
			return;
		}
	}

	/**
	 * Enable or disable log of the touches being sent in.
	 */
	private void log(boolean on) {
		log = on;
	}

	/**
	 * Output message to the standard output stream.
	 * 
	 * @param out
	 *            the output message
	 */
	private void log(String out) {
		Date date = new Date();
		// define the "header" to retrieve at least the principal socket
		// informations : the host/port where the socket is bound.
		if (log && header.equals("")){
			header = "-- Tac Tile API session: " + date;
			System.out.println( header );
		} 
		
		if (log && !header.equals("")){
			String pattern = "yy-MM-dd HH:mm:ss.S Z";
			String sdf = new SimpleDateFormat(pattern).format(date);
			System.out.println("  [" + sdf + "] " + out);
		}
	}

	/**
	 * Output error messages to the standard error stream.
	 * 
	 * @param err
	 *            the error string
	 */
	private void error(String err) {
		System.err.println(err);
	}

	/**
	 * Output debug messages 
	 * 
	 * @param out
	 *            the debug string
	 */
	private void debug(String out) {
		Date date = new Date();
		if (debug && header.equals("")){
			header = "-- Tac Tile API session: " + date;
			System.out.println( header);
		}
		if (debug) {
			System.out.println( out );
		}
	}

	/**
	 * In order to perform parsing of the incoming touch data, this method is needed.<br>
	 * You <i>DO NOT</i> need to implement/use this method.
	 * 
	 * @param data
	 *            the data that is transmitted 
	 * @param IP
	 *            the IP of the data's origin 
	 * @param port
	 *            the port the data was sent from
	 * @see TouchAPI#processData(byte[] data) 
	 */
	 // This implemention of the handler is needed in your code.  This method will be 
 	 // automatically called by the UDP object each time he receive a nonnull 
	 // message. By default, this method have just one argument (the received message
	 // as byte[ ] array), but in addition, two arguments (representing in order
	 // the sender IP address and his port can be set like below.<br>
	 // Current implementation is: once data is passed in, it hands it over to processData() for parsing
 	 // void __tacTile_Polling__( byte[] data ) { // <-- default handler
	public void __tacTile_Polling__(byte[] data, String ip, int port) { // <-- extended handler
		processData(data);
	}

}
