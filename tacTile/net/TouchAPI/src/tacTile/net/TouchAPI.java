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
 * TouchAPI is an addition to the Processing Libraries written so Processing
 * applications could communicate with the TacTile.<br>
 * Tac Tile is a Multi-touch table begin researched at the Electronic
 * Visualization Lab at the University of Illinois at Chicago.
 * 
 * <br>
 * <br>
 * The TouchAPI constructor creates and manages communications between a
 * <u>Touch Server</u> and a <u>Processing application</u>.<br>
 * <br>
 * <b>CONNECTION : Touch Servers</b><br>
 * <ul>
 * 		Connections are facilitated by simply calling the TouchApi constructors.
 * 		However, there are two constructors that can be called depending on the type
 * 		of touch server.
 * 		<ul>
 * 		<li><b>TacTile Touch Server:</b> Processing applications that are loaded
 * 		onto TacTile for testing can access touch data from the TacTile touch server.
 * 		This is done by simply calling this constructor:<br>
 * 		<b>
 * 		{@link TouchAPI#TouchAPI(Object owner)} <br>
 * 		</b>
 * 		<br>
 * 		<li><b>Local Touch Server:</b> Often times during development a Multi-Touch
 * 		Pad is used. Construction and drivers for such a device can be found <a
 * 		href="http://www.evl.uic.edu/spiff/class/cs426/index.html"> here.</a>
 * 		Installing the drivers allows input on the Multi-Touch Pad to be sent to your
 * 		personal computer. Your machine is now a local touch server! In conjunction
 * 		with TouchAPI, both the local touch server and Processing application can run
 * 		on the same machine. This is done by simply calling this constructor:<br>
 * 		<b>
 * 		{@link TouchAPI#TouchAPI(Object owner, int data_port)}
 * 		</b>
 * 		</ul>
 * 		<br>
 * 		<ul>
 * 		<li><b>Specified Touch Server:</b> This is an alternative constructor that will allow 
 * 		the user to specify all the information needed to make a connection with TouchAPI:<br>
 * 		<b>
 * 		{@link TouchAPI#TouchAPI(Object owner, int data_port, int msg_port, String serverIP) }
 *		</b>
 * 		</ul>
 * 		<br>
 * 
 * After a connection is formed by either one of these constructors, TouachAPI is called to collect touch data from the
 * Touch Server.  
 * 
 * </ul>
 * <b>RETRIEVAL : Touch Data</b><br>
 * <ul>
 * The data is then processed and stored within <i>two</i> ArrayLists inside
 * the TouchAPI:
 * <ul>
 * 		<li><b>dataTouchList:</b>
 *		<ul> For more advanced users who want to manage their own touch data.  
 *			 This arrayList of Touches is the parsed data that is sent back from
 *			 the touch server.<br>
 *			 <br>
 *			 The dataTouchList has an initial maximum size of 200.  Any touches
 *			 beyond the maximum will still be added to the list.  However, the oldest
 *			 touch in the list will be deleted.  Essentially only 200 of the most recent touches	
 *			 will be kept in the list.  This list size can be retrieved and altered via:
 *			 <b>
 *			 {@link TouchAPI#getTouchData( int )} , 
 *			 {@link TouchAPI#getLeastRecentTouchData()}, & 
 *			 {@link TouchAPI#getMostRecentTouchData()}.
 *			 </b><br>
 *			 <br>
 * 			 The data within the dataTouchList can be accessed by the following methods:
 * 			 <ul><i>Retrieve data : </i><br> 
 * 			 	<b>
 * 			 	{@link TouchAPI#getTouchData( int )} , 
 * 			 	{@link TouchAPI#getLeastRecentTouchData()} & 
 * 			 	{@link TouchAPI#getMostRecentTouchData()}.<br>
 * 			 	</b>
 * 			 </ul>
 * 			 <br>
 * 			 <ul><i>Clearing the list : </i><br>
 * 			 	<b>
 * 			 	{@link TouchAPI#clearAllButEarliestTouchData()} & 
 * 			 	{@link TouchAPI#clearAllDataTouches()}.<br>
 * 			 </b>
 * 			 </ul>
 * 			 <br>
 * 			 <ul><i>List Attributes : </i><br>
 * 			 	<b>
 * 			 	{@link TouchAPI#dataListIsEmpty()} & 
 * 			 	{@link TouchAPI#dataListSize()}.<br>
 * 			 	</b>
 * 			 </ul>
 * 		</ul>
 * 		<br> 
 * 		<li><b>managedTouchList:</b>
 * 		<ul> This list manages the touch data for you.  The data for each touch is stored
 * 			 within an instance of <b>{@link Touches}</b>.  A new touch's finger ID is first 
 * 			 compared with all the existing touches in the managedTouchList.  If the finger 
 * 			 ID is not present the touch is assumed to be new and added to the managedTouchList.
 * 			 If it is present, then the x Position, y Position, time stamp and intensity are 
 * 			 updated.<br>
 *    		 <br>
 *    		Once the finger ID is checked, the managedTouchList is then refreshed.  Each touch has a life span in
 *    		 milliseconds. During the refresh period, if a touch exceeds its life time it is
 *        	 removed from the list.  The default value for a life time is 80 milliseconds.  This
 *           can be changed via the function: <b>{@link TouchAPI#setTouchLifeTime(long time)}</b>.
 *			 The entire managedTouchList and info about the list can be accessed by the following methods:
 * 			 <ul><i>Retrieve list : </i><br> 
 * 			 	<b>
 * 			 	{@link TouchAPI#getManagedList()}<br>
 * 		     	</b>
 * 			 </ul>
 * 			 <br>
 *   		 <ul><i>managedTouchList info: </i><br> 
 * 			 	<b>
 * 			 	{@link TouchAPI#managedListSize()} & {@link TouchAPI#managedListIsEmpty()}<br>
 * 		     	</b>
 * 			 </ul>
 * 			 <br> 
 *		</ul>
 * </ul>
 * 
 * @version 0.1
 * @author Dennis Chau - Koracas@gmail.com<br>
 */
public class TouchAPI {

	int port_tcp; // port to send the TCP msg in order to start sending
					// datagrams
	int port_udp; // port open where to recieve UDP msgs.
	String serverName; // machine where the datagrams will come from

	Client clientForServer; // The Client obj that will comm with Tac Tile
	UDP socketForData; // The udp socket that will recieve the data

	// the Array List that will hold all raw touches
	ArrayList<Touches> dataTouchList = new ArrayList<Touches>();

	// the Array List that will hold all the managed touches
	ArrayList<Touches> managedTouchList = new ArrayList<Touches>();

	boolean debug = false; // true if the debug method should print debug msgs
	boolean connected = false; // true if there is a server connection
	boolean dataOn = false; // true if server as told to send data
	boolean server = false; // true if there is server info
	boolean log = false; // enable/disable output log
	boolean listen = false; // true, if the socket waits for packets

	// the parent object (could be an application, a componant, etc...)
	Object owner = null;

	// The "receive handler" methods name. This is the method name
	// that the UDP will call when the socket recieves data.
	String modHandler = "__tacTile_Polling__";
	String timeOutHandler = "__tacTile_TimeOut__";

	// the log "header" to be set for debugging.
	String header = "";

	// The life time of a touch in milliseconds
	long touchLifeTime = 80;
	int dataTouchListSize = 200;
	
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
	 * The milliseconds req for the table to realize there are no touches 
	 */
	private static final int TIMEOUT_TIME = 30;

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
	 * Default constructor that will create a connection between the TacTile
	 * touch server and a Processing Application.
	 * 
	 * @param owner
	 *            the parent object so the class knows how to get back to the
	 *            Processing Application
	 */
	public TouchAPI(Object owner) {
		this(owner, UDP_PORT, TCP_PORT, TACTILE_IP);
	}

	/**
	 * Constructor that will create a connection between a local touch server
	 * and a Processing Application. Ideally this would be used along with the
	 * OmegaTracker Server running on the same machine as the Processing
	 * Application. In order to establish this connection, all that is needed is
	 * a port where the data should be stored. The constructor will
	 * automatically connect to the machine that the TouchAPI is instantiated
	 * on.
	 * 
	 * @param owner
	 *            the parent object so the class knows how to get back to the
	 *            Processing Application
	 * @param data_port
	 *            the port where the touch server should send the data. A
	 *            suggested port number is 7000.
	 */
	public TouchAPI(Object owner, int data_port) {
		this(owner, data_port, TCP_PORT, null);
	}

	/**
	 * Constructor that creates a new connection between a touch server and a
	 * Processing application.  In order to form a connection the following information is needed:<br>
	 * <ul>
	 * 	<li>data_port : the port on the Touch Client where the data should be sent 
	 * 	<li>msg_port  : the port on the Touch Server where the message to begin data transfer is sent
	 * 	<li>serverIP  : the IP address of the Touch Server.
	 * </ul>
	 * If this information is correct, this constructor can be used to form a connection between the
	 * Touch Server and the Processing Application.  TouchAPI will send a "being data transfer" message
	 * to the specified port and server.   Once this message is sent, it listens to the data_port
	 * for any incoming data.
	 * 
	 * <b><i>Note:</i></b> If the Touch Server is running on the same computer as the Touch Client, the serverIP should be set to the IP of the localhost.
	 * 
	 * @param owner
	 *            the parent object so the class knows how to get back to the
	 *            Processing Application
	 * @param clientPort
	 *            the port the Touch Client listens on for touch messages from the Touch Server.
	 * @param serverPort
	 *            the IP address of the Touch Server.
	 * @param serverIP
	 *            the port the Touch Server listens on for new clients.
	 */
	public TouchAPI(Object owner, int clientPort, int serverPort, String serverIP) {
		this.owner = owner;

		// Register this object to the PApplet
		try {
			if (owner instanceof PApplet)
				((PApplet) owner).registerDispose(this);
		} catch (NoClassDefFoundError e) {
			;
		}

		// Open UDP Socket
		if (clientPort != 0) {
			this.port_udp = clientPort;
			socketForData = new UDP(this, clientPort);
			socketForData.setReceiveHandler(modHandler);
			socketForData.setTimeoutHandler(timeOutHandler);
			debug( "Opened socket:" + udp_port() + "\n");
		}

		// Open connection to server
		if (serverPort != 0 && serverIP == null) {
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
				this.port_tcp = serverPort;
				this.server = true;
				// Initialize connection with server
				clientForServer = new Client((PApplet) owner, serverName, port_tcp);
				this.connected = true;
				debug( "Opened connection to server:" + serverAddy() + ", port: " + tcp_port()+ "\n");

			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		} else if (serverPort != 0 && serverIP != null) {
			this.serverName = serverIP;
			this.port_tcp = serverPort;
			this.server = true;
			// Initialize connection with server
			clientForServer = new Client((PApplet) owner, serverName, port_tcp);
			this.connected = true;
			debug( "Opened connection to server:" + serverAddy() + ", port: " + tcp_port()+ "\n");
		} else { 
			this.server = false;
			this.connected = false;
		}
		
		// Initiate data transfer
		this.dataOn = false;
		initHandShake();
		debug( "Data Ready to be tranfered."+ "\n");

		// Tell port to start polling
		udpListen(true);
		debug( "Socket is listening."+ "\n");
		
	}

	// ///////////////////////////// methods ///////////////////////////////

	/**
	 * Close the connections that are made to touch server and the Processing
	 * Application. This method is automatically called by Processing when the
	 * PApplet shuts down.<br>
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
		socketForData.listen(TIMEOUT_TIME);
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
				error("no connection to server");
				System.exit(1);
			}
		} else {
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
	 * Pads the msgs bc they need to be 99 char long. Then returns the complete
	 * string
	 * 
	 * @param msg
	 *            The message that will be padded
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
	 * Parses the messages that are sent to the socket. The messages are passes
	 * in as string in the following format:<br>
	 * <ul>
	 * timestamp:flag:finger_ID, xPos, yPos<br>
	 * <small> *Note: that the xpos and yPos are ratios between 0 to 1 </small>
	 * </ul>
	 * They are then parsed out with the data types:
	 * <ul>
	 * String : String : int, float, float
	 * </ul>
	 * These values are then passed to the Touch constructor where they are
	 * stored: The Touch that is returned from the constructor is then stored
	 * inside an ArrayList of touches. These touches can then be accessed by
	 * various methods
	 * 
	 * @param dGram
	 *            The datagram that was sent to the socket
	 * @return Touches
	 * @see Touches#Touches(String flag, String timeStamp, int finger, float
	 *      xPos, float yPos)
	 */
	private Touches parseDGram(String dGram) {
		int firstSpace = dGram.indexOf(" ", 0);
		dGram = dGram.substring(0, firstSpace);

		String flag;
		int finger;
		long timeStamp;
		float xPos, yPos, intensity;

		int start, last, sub_last;
		int maxLen = dGram.length() - 1;

		// flag //
		start = dGram.indexOf(":", 0);
		sub_last = start;
		flag = dGram.substring(start + 1, start + 2);

		// Only message a d flag are useful. //
		if (flag.compareTo("d") == 0 || flag.compareTo("g") == 0) {
			log("MSG received : <" + dGram + ">");

			// time stamp //
			timeStamp = System.currentTimeMillis();

			last = sub_last;

			// finger //
			start = dGram.indexOf(":", last + 1);
			sub_last = start;
			last = sub_last;

			start = dGram.indexOf(",", last + 1);
			sub_last = start;
			finger = Integer.parseInt(dGram.substring(last + 1, sub_last));
			last = sub_last;

			// x pos //
			start = dGram.indexOf(",", last + 1);
			sub_last = start;
			xPos = Float.valueOf(dGram.substring(last + 1, sub_last))
					.floatValue();
			last = sub_last;
			
			// y pos //
			start = dGram.indexOf(",", last + 1);
			if ( start == -1 ){ 	//there is no intensity in the dgram
				intensity = 0;
				yPos = Float.valueOf(dGram.substring(last + 1, maxLen)).floatValue();
			} else {				//there is intensity in the dgram 
				sub_last = start;
				yPos = Float.valueOf(dGram.substring(last + 1, sub_last))
						.floatValue();
				last = sub_last;
				intensity = Float.valueOf(dGram.substring(last + 1, maxLen))
					.floatValue();
			}

			Touches curTouch = new Touches(timeStamp, finger, xPos, yPos,
					intensity);
			
			return curTouch;
		} else {
			return null;
		}
	}

	/**
	 * Process data that is transfered into the socket. Creates a touch out of
	 * it and stores it in the dataTouchList and managedTouchList variable.
	 * 
	 * @param data
	 *            the data byte array of data
	 */
	private void processData(byte[] data) {
		String dGram = new String(data);
		Touches newTouch = parseDGram(dGram);
		if (newTouch != null) {
			addTouches(newTouch);
		}
	}

	/**
	 * Adds a new touch to the dataTouchList and managedTouchList
	 * 
	 * @param newTouch
	 *            A Touches object to be added to dataTouchList and
	 *            managedTouchList
	 */
	private void addTouches(Touches newTouch) {
		// add to dataTouchList

		if (dataListSize() > dataTouchListSize) {
			removeTouchData(0);
		}
		dataTouchList.add(newTouch);

		// add to managedTouchList
		addNewManagedTouch(newTouch);
	}

	/**
	 * Returns true if dataTouchList is empty
	 * 
	 * @return boolean
	 */
	public boolean dataListIsEmpty() {
		return dataTouchList.isEmpty();
	}

	/**
	 * Returns the size of the dataTouchList
	 * 
	 * @return int
	 */
	public int dataListSize() {
		return dataTouchList.size();
	}

	/**
	 * Allows user to set the <b>max</b> size of the dataTouchList
	 */
	public void setDataListMaxSize(int size ) {
		dataTouchListSize = size;
	}
	
	/**
	 * Returns the <b>current maximum</b> size of the dataTouchList
	 * 
	 * @return int
	 */
	public int getDataListMaxSize( ) {
		return dataTouchListSize;
	}
	
	/**
	 * Returns a specified Touches in the dataTouchList. If the list is empty, a
	 * Null is returned.<br>
	 * Once the touch is retrieved, the Touches are <b> removed </b>.
	 * 
	 * @param index
	 *            The index of the desired touch
	 * @return Touches if dataTouchList is not empty<br>
	 *         OR<br>
	 *         Null if dataTouchList is empty
	 */
	public Touches getTouchData(int index) {
		if (!dataListIsEmpty()) {
			Touches curTouch = dataTouchList.get(index);
			removeTouchData(index);
			return curTouch;
		} else {
			return null;
		}
	}

	/**
	 * Returns a the oldest touch on the dataTouchList. If the list is empty, a
	 * Null is returned.<br>
	 * Once the touch is retrieved, the Touches are <b> removed </b>.
	 * 
	 * @return Touches if dataTouchList is not empty -OR- Null if dataTouchList
	 *         is empty
	 */
	public Touches getLeastRecentTouchData() {
		Touches curTouch = getTouchData(0);
		return curTouch;
	}

	/**
	 * Returns a the newest touch on the dataTouchList. If the list is empty, a
	 * Null is returned. <br>
	 * Once the touch is retrieved, the Touches are <b> removed </b>.
	 * 
	 * @return Touches if dataTouchList is not empty -OR- Null if dataTouchList
	 *         is empty
	 */
	public Touches getMostRecentTouchData() {
		int latest = dataListSize() - 1;
		Touches curTouch = getTouchData(latest);
		return curTouch;
	}

	/**
	 * Clears all the Touches in the dataTouchList
	 */
	public void clearAllDataTouches() {
		dataTouchList.clear();
	}

	/**
	 * Clears all the Touches in the dataTouchList except for the newest one
	 */
	public void clearAllButEarliestTouchData() {
		int size = dataListSize();
		if (size > 1) {
			int last = size - 1;
			Touches temp = new Touches();
			temp = dataTouchList.get(last);
			clearAllDataTouches();
			if (dataListSize() > dataTouchListSize) {
				removeTouchData(0);
			}
			dataTouchList.add(temp);
		} else {
			return;
		}
	}

	/**
	 * Removes a specified element from the dataTouchList
	 * 
	 * @param index
	 *            The index of the desired touch
	 */
	private void removeTouchData(int index) {
		dataTouchList.remove(index);
	}

	/**
	 * Passes in a new touch and compares its ID with all current touches. If it
	 * is indeed a new finger, then it is added to the managedTouchList. If it
	 * is an existing finger its: x position, y positions, intensity, and
	 * timestamp are updated. The managedTouchList is then refreshed.
	 * 
	 * @param newTouch
	 *            The new touch
	 */
	private void addNewManagedTouch(Touches newTouch) {
		if (newTouch != null) {
			boolean sameFinger = false;

			for (int tempIndex = 0; tempIndex < managedListSize(); tempIndex++) {
				int newId = newTouch.getFinger();

				Touches curTouch = managedTouchList.get(tempIndex);

				int curTempID = curTouch.getFinger();

				if (curTempID == newId) {
					sameFinger = true;
					curTouch.setTimeStamp(newTouch.getTimeStamp());
					curTouch.setXPos(newTouch.getXPos());
					curTouch.setYPos(newTouch.getYPos());
					curTouch.setIntensity(newTouch.getIntensity());
				}
			}

			if (sameFinger == false) {
				managedTouchList.add(newTouch);
			}
		}
		refreshManagedList();
	}

	/**
	 * Refreshes the managedTouchlist by looking at all the Touches in the list.
	 * The current time is subtracted by each respective timestamp.  This difference 
	 * is compared to the touchLifeTime value.  Any touches that have a lifetime that 
	 * exceeds this value is dropped.
	 */
	private void refreshManagedList() {

		ArrayList<Touches> tempManagedList = new ArrayList<Touches>();

		for (int index = 0; index < managedListSize(); index++) {
			long curTimeStamp = System.currentTimeMillis();
			Touches temp = managedTouchList.get(index);
			long touchTimeStamp = temp.getTimeStamp();

			if (curTimeStamp - touchTimeStamp < touchLifeTime) {
				tempManagedList.add(temp);
			}
		}

		managedTouchList = tempManagedList;
	}

	/**
	 * Returns the entire managedTouchList.
	 * 
	 * @return ArrayList <Touches>
	 */
	public ArrayList<Touches> getManagedList() {
		return managedTouchList;
	}

	/**
	 * Returns the size of the managedTouchList
	 * 
	 * @return int
	 */
	public int managedListSize() {
		return managedTouchList.size();
	}

	/**
	 * Returns true if managedTouchList is empty
	 * 
	 * @return boolean
	 */
	public boolean managedListIsEmpty() {
		return managedTouchList.isEmpty();
	}

	/**
	 * Set the lifetime of a touch within the <b>managedTouchList</b>
	 * 
	 * @param time 
	 *            the lifetime of a touch in milliseconds.  Default value is 80. 
	 */
	public void setTouchLifeTime(long time) {
		touchLifeTime = time;
	}
	
	/**
	 * Clears all the Touches in the managedTouchList
	 */
	private void clearManagedList() {
		managedTouchList.clear();
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
		if (log && header.equals("")) {
			header = "-- Tac Tile API session: " + date;
			System.out.println(header);
		}

		if (log && !header.equals("")) {
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
		if (debug && header.equals("")) {
			header = "-- Tac Tile API session: " + date;
			System.out.println(header);
		}
		if (debug) {
			System.out.print(out);
		}
	}

	/**
	 * In order to perform parsing of the incoming touch data, this method is
	 * needed.<br>
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
	// This implemention of the handler is needed in your code. This method will
	// be
	// automatically called by the UDP object each time he receive a nonnull
	// message. By default, this method have just one argument (the received
	// message
	// as byte[ ] array), but in addition, two arguments (representing in order
	// the sender IP address and his port can be set like below.<br>
	// Current implementation is: once data is passed in, it hands it over to
	// processData() for parsing
	// void __tacTile_Polling__( byte[] data ) { // <-- default handler
	public void __tacTile_Polling__(byte[] data, String ip, int port) { // <--
																		// extended
																		// handler
		processData(data);
	}

	/**
	 * In order to perform proper handling when TacTile is idle, this method is
	 * needed.<br>
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
	// This implemention of the handler is needed in your code. This method will
	// be
	// automatically called by the UDP object each time it times out while
	// polling.
	// This indicates that there has been no activity on tacTile.
	public void __tacTile_TimeOut__() { // <-- extended handler
		// clearManagedList();
		refreshManagedList();
	}

}
