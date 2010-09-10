import processing.core.PApplet;
import tacTile.net.*;

public class TouchAPI_Tester extends PApplet implements TouchListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//InputManager inputManager;
	TouchAPI tacTile;
	
	public void setup(){
		size( 640, 480 );
		
		//inputManager = new InputManager(this);
		tacTile = new TouchAPI(this, 7000, 7340, "tactile.evl.uic.edu");
		tacTile.setTouchListener(this);
		background(0);
	}// setup
	
	public void draw(){
		background(0);
		tacTile.process();
	}// draw
	
	public static void main(String[] args){
		PApplet.main(new String[] { TouchAPI_Tester.class.getName() });
	}// main

	@Override
	public void onInput(Touches t) {
		// TODO Auto-generated method stub
		float x = t.getXPos() * width;
		float y = height - t.getYPos() * height;
		//println("onInput: touch at ("+ x + "," + y +")");
	}
}// class TouchAPI_Tester