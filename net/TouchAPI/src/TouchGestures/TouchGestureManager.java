package TouchGestures;

import java.util.ArrayList;

import processing.core.*;
import tacTile.net.*;

public class TouchGestureManager {
	TouchAPI tacTile;
	PApplet p;
	
	public TouchGestureManager( PApplet owner, TouchAPI tracker ){
		tacTile = tracker;
		p = owner;
	}// CTOR
	
	public void process(){
		ArrayList<Touches> touchDownList = tacTile.getTouchesDown();
		for( int i = 0; i < touchDownList.size(); i++ ){
			Touches t = touchDownList.get(i);
			float x = t.getXPos() * p.width;
			float y = p.height - t.getYPos() * p.height;
			//int gesture = t.getGesture();
			p.println(" Touch down at ("+x+","+y+")");
		}// for
		
		ArrayList<Touches> touchMoveList = tacTile.getTouchesMoved();
		for( int i = 0; i < touchMoveList.size(); i++ ){
			Touches t = touchMoveList.get(i);
			float x = t.getXPos() * p.width;
			float y = p.height - t.getYPos() * p.height;
			//int gesture = t.getGesture();
			p.println(" Touch move at ("+x+","+y+")");
		}// for
		
		ArrayList<Touches> touchUpList = tacTile.getTouchesUp();
		for( int i = 0; i < touchUpList.size(); i++ ){
			Touches t = touchUpList.get(i);
			float x = t.getXPos() * p.width;
			float y = p.height - t.getYPos() * p.height;
			//int gesture = t.getGesture();
			p.println(" Touch up at ("+x+","+y+")");
		}// for
	}// process
	
}// class TouchGestureManager
