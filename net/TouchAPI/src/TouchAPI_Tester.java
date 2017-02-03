import java.util.ArrayList;

import processing.core.PApplet;
import tacTile.net.*;

public class TouchAPI_Tester extends PApplet implements TouchListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//InputManager inputManager;
	TouchAPI tacTile;
	
	boolean classicMethod = true;
	public void setup(){
		size( 640, 480 );
		
		//inputManager = new InputManager(this);
		tacTile = new TouchAPI(this, 7002, 28000, "nishimoto.evl.uic.edu");
		if( !classicMethod )
			tacTile.setTouchListener(this);
		background(0);
	}// setup
	
	public void draw(){
		background(0);
		
		if( classicMethod )
		{
			ArrayList<Touches> touchList = tacTile.getOldManagedList();
			
			for( int i = 0; i < touchList.size(); i++ )
			{
				Touches t = touchList.get(i);
				DrawTouch(t);
			}
			/*
			for( int i = 0; i < touchDownList.size(); i++ )
			{
				Touches t = touchDownList.get(i);
				DrawTouch(t, 0);
			}
			for( int i = 0; i < touchMoveList.size(); i++ )
			{
				Touches t = touchMoveList.get(i);
				DrawTouch(t, 1);
			}
			for( int i = 0; i < touchUpList.size(); i++ )
			{
				Touches t = touchUpList.get(i);
				DrawTouch(t, 2);
			}
			*/
		}
		else
		{
			tacTile.process();
		}
	}// draw
	
	private void DrawTouch(Touches t) {
		DrawTouch(t, t.getGesture());
	}
	
	private void DrawTouch(Touches t, int gesture) {
		float x = t.getXPos() * width;
		float y = height - t.getYPos() * height;
		int id = t.getFinger();
		float w = t.getXWidth() * width;
		float h = t.getYWidth() * height;
		float intensity = t.getIntensity();
		
		switch(gesture) {
		case(0): fill(255, 0, 0); break; // Down
		case(1): fill(0, 255, 0); break; // Move
		case(2): fill(0, 0, 255); break; // Up
		default:
			fill(255); break;
		}
		noStroke();
		ellipse( x, y, w, h);
		// println("onInput: touch at ("+ x + "," + y +") " + intensity);
	}
	
	public static void main(String[] args){
		PApplet.main(new String[] { TouchAPI_Tester.class.getName() });
	}// main

	public void onInput(Touches t) {
		DrawTouch(t);
	}
}// class TouchAPI_Tester