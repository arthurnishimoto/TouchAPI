package TouchEvents;

import java.util.ArrayList;

import processing.core.*;

import tacTile.net.*;

public class TouchEventManager {
	PApplet p;
	float radius;
	float lifetime = 100;
	int residuelifetime = 150;
	int maxListSize = 200;
	boolean deleteoldtouches = false;
	  
	TouchAPI tacTile;
	  
	String tacTileMachine = "tactile.evl.uic.edu";
	  
	//Port for data transfer
	int dataPort = 7020;
	int msgPort = 7340;
	
	boolean touchMovedDuplicates = false;	 	  
	  
	ArrayList allTouches = new ArrayList();
	ArrayList touchesDown = new ArrayList();
	ArrayList touchesUp = new ArrayList();
	ArrayList touchesMoved = new ArrayList();
	  
	ArrayList residue = new ArrayList();
	
	public TouchEventManager(PApplet o){
		p = o;
		radius = (float) (.08 * o.width);
		tacTile = new TouchAPI(o, dataPort, msgPort, tacTileMachine);
		tacTile.setTouchLifeTime(5);
	}// CTOR
	
	public TouchEventManager(PApplet o, TouchAPI t){
		p = o;
		radius = (float) (.08 * o.width);
		tacTile = t;
		tacTile.setTouchLifeTime(5);
		
	}// CTOR
	
	public void  setMaxListSize(int listsize){
		maxListSize = listsize;
	}
	
	public void process(){
          int i = 0;
          int j = 0;
          int offset = 0; 
          
          ArrayList managedList = tacTile.getOldManagedList();
          InsertionSort(managedList);
          ArrayList newDown = new ArrayList();
          ArrayList newUp = new ArrayList();
          ArrayList newMoved = new ArrayList();
          ArrayList newAllTouches = new ArrayList();
          
          if(managedList.isEmpty()){
             allUp(0); 
             deleteOldResidue();
          }else{
            
            for(i = 0; i < allTouches.size(); i++){
               //getting smallest (by finger ID) unsearched element from both lists
                 TouchShell oldShell = (TouchShell)allTouches.get(i);
                 Touches oldTouch = oldShell.getWrongTouch(); //updated
               
               if(j == managedList.size() ){
                 allUp(i);
                 break;
               }
               
               Touches newTouch = (Touches) managedList.get(j);               
               
               if( newTouch.getFinger() == oldTouch.getFinger() ){
                  //if they are the same, move up one in the old list, and check to see if it has moved.
                  j++;
                  if( checkMoved(oldTouch, newTouch) ){
                    //if it has moved, add it to the moved list. 
                     oldShell.updateTouch(newTouch);
                     newMoved.add(oldShell.getRightTouch());
                     newAllTouches.add(oldShell);
                  }else{//check moved
                     newAllTouches.add(oldShell); 
                  }//check moved 
               }else{// new = old
                 residue.add(new ResidueCirc( oldShell.getRightTouch() ));
               }// new = old               
               
            }//end for loop
            
            //Now, add extra elements to the "down" list.
            deleteOldResidue(); 
            dealWithResidue( j, managedList, newMoved, newAllTouches, newDown );
            
            addNewTouches( newUp, newDown, newMoved, newAllTouches );            
            
          } //end empty managed list check
          
          if( tacTile.hasTouchListener() ){
        	  TouchListener touchListener = tacTile.getTouchListener();
	          ArrayList downList = getTouchesDown();
	          ArrayList moveList = getTouchesMoved();
	          ArrayList upList = getTouchesUp();
	          ArrayList holdList = tacTile.getOldManagedList();
	          
	          for( i = 0; i < downList.size(); i++ ){
	        	  Touches t = (Touches)downList.get(i);
	        	  t.setGesture(TouchAPI.GESTURE_DOWN);
	        	  touchListener.onInput(t);
	          }// for
	          for( i = 0; i < holdList.size(); i++ ){
	        	  Touches t = (Touches)holdList.get(i);
	        	  t.setGesture(TouchAPI.GESTURE_HOLD);
	        	  touchListener.onInput(t);
	          }// for
	          for( i = 0; i < moveList.size(); i++ ){
	        	  Touches t = (Touches)moveList.get(i);
	        	  t.setGesture(TouchAPI.GESTURE_MOVE);
	        	  touchListener.onInput(t);
	          }// for
	          for( i = 0; i < upList.size(); i++ ){
	        	  Touches t = (Touches)upList.get(i);
	        	  t.setGesture(TouchAPI.GESTURE_UP);
	        	  touchListener.onInput(t);
	          }// for
          }
	   }//process
	
	   public void lostTouch( Touches touch ){
		   for(int i = 0; i < allTouches.size(); i++){
			   TouchShell oldShell = (TouchShell)allTouches.get(i);
               Touches oldTouch = oldShell.getWrongTouch(); 
               
			   if(touch.getFinger() == (oldTouch.getFinger())){				   
				   residue.add(new ResidueCirc( oldShell.getRightTouch() ));
				   allTouches.remove(i);
			   }
		   }
	   }	
	   
	   //a touch comes in, potentially new, potentially a current touch. 
	   //check to see if it's moved or if it's new or neither.
	   public void checkTouch( Touches touch ){
		   
		   for(int i = 0; i < allTouches.size(); i++){
			   
			   TouchShell oldShell = (TouchShell)allTouches.get(i);
               Touches oldTouch = oldShell.getWrongTouch(); 
               
               if(touch.getFinger() == (oldTouch.getFinger())){
                   //if they are the same, move up one in the old list, and check to see if it has moved.
                   if( checkMoved(oldTouch, touch) ){
                     //if it has moved, add it to the moved list. 
                      oldShell.updateTouch(touch);
                      
                      if(!touchMovedDuplicates){     	  
		
		            	  int touchid = oldShell.getRightTouch().getFinger();
		            	  
		            	  for(int j = 0; j < touchesMoved.size(); j++){
		            		  
		            		  int curID = ((Touches) touchesMoved.get(j)).getFinger();
		            		  
		            		  if(curID == touchid){
		            			  touchesMoved.remove(j);
		            			  j--;
		            		  }//endif curID == touchid
		            		  
		            	  }//end for j
                      }//end if !touchMovedDuplicates
                      
                      touchesMoved.add(oldShell.getRightTouch());
                      return;
                   
               }
             }
		   }
		   
		   //need to check residue here
		   checkResidueSingleTouch(touch);
		   
	   }
	   
	   public void checkResidueSingleTouch(Touches touch){
		   deleteOldResidue();
		   float x = touch.getXPos() * p.width;
	       float y = p.height - touch.getYPos() * p.height;
	       
		   for(int i = 0; i < residue.size(); i++){
			   ResidueCirc currentResidue = ( (ResidueCirc) residue.get(i) );	
			   
	           if( currentResidue.isInside(x, y) ){
	        	   if( !currentResidue.isDone() ){
	       	        
	       	        TouchShell newShell = new TouchShell( new Touches(touch) );
	       	        newShell.setRightId( (currentResidue.getTouch()).getFinger() );
	       	        allTouches.add( newShell );
	       	            
	       	            //touch.setFinger((current.getTouch()).getFinger());
	       	            //removeTouch( current.getTouch(), newAllTouches);
	       	        touchesMoved.add( newShell.getRightTouch() );
	       	        removeResidue(currentResidue);
	       	        currentResidue.gotUsed();
	       	        
	        	   return;
	        	   }
	           }
		   }
		   //not viable for residue, add new down touch
		   touchesDown.add(touch);
           
           TouchShell newShell = new TouchShell( new Touches( ( (Touches) touch ) ));

           allTouches.add(newShell);
	   }
	   
	   public void setResidueRadius( float newradius ){
	      radius = newradius; 
	   }
	   
	   //similar to tacTile's touch lifetime 
	   public void setTouchLifetime( float newlifetime ){
	     lifetime = newlifetime;
	   }
	   
	   public void setResidueLifetime( int newlifetimer ){
	    residuelifetime = newlifetimer; 
	   }
	   
	   void deleteOldTouches(){
	     
	     Touches current;
	      
	    for(int i = 0; i < touchesDown.size(); i++){
	        current = (Touches) touchesDown.get(i);
	        if(p.millis() - current.getTimeStamp() > lifetime ){
	           touchesDown.remove(i);
	           i--; 
	        }
	    }
	    
	    for(int i = 0; i < touchesUp.size(); i++){
	        current = (Touches) touchesUp.get(i);
	        if(p.millis() - current.getTimeStamp() > lifetime ){
	           touchesUp.remove(i);
	           i--; 
	        }
	    }
	    
	    for(int i = 0; i < touchesMoved.size(); i++){
	        current = (Touches) touchesMoved.get(i);
	        if(p.millis() - current.getTimeStamp() > lifetime ){
	           touchesMoved.remove(i);
	           i--; 
	        }
	    }
	     
	   }//delete old touches from up, down, moved
	   
	   public ArrayList getAllTouches(){
	     
	     ArrayList touchlist = new ArrayList();
	     Touches current;
	     
	     for(int i = 0; i < allTouches.size(); i++){
	       current = ((TouchShell) allTouches.get(i) ).getRightTouch();
	       touchlist.add(current);
	     }
	     
	     return touchlist;
	     
	   } //get all touches

	   
	   void dealWithResidue(int j, ArrayList managedList, ArrayList movelist, ArrayList newAllTouches, ArrayList newDown ){

	     
	     ArrayList sortedByDistance = new ArrayList();
	     
	       for(j = j; j < managedList.size(); j++){
	         
	         Touches currentTouch = ( (Touches) managedList.get(j) );
	         float x = currentTouch.getXPos() * p.height;
	         float y = p.width - currentTouch.getYPos() * p.width;
	         boolean used = false;
	         
	         for(int i = 0; i < residue.size(); i++){
	           ResidueCirc currentResidue = ( (ResidueCirc) residue.get(i) );

	           
	           if( currentResidue.isInside(x, y) ){
	             //println("residueinside");
	            
	            ResidueTouchNode node = new ResidueTouchNode( currentTouch, currentResidue, currentResidue.getDiff(x, y) ); 
	            
	            addSorted(sortedByDistance, node);
	            used = true;
	             
	           }//end if, residue.isInside
	         }//end for loop residue 
	             
	           if(!used){
	                        //println("new down");
	                newDown.add(managedList.get(j));
	                                
	               TouchShell newShell = new TouchShell( new Touches( ( (Touches) managedList.get(j)) ) );
	                                //NEW
	                 newAllTouches.add(newShell);
	           }        
	       } //end for loop touches
	    
	    for(int i = 0; i < sortedByDistance.size(); i++){
	      
	      ResidueTouchNode currentRTnode = ( (ResidueTouchNode) sortedByDistance.get(i) );
	      ResidueCirc currentResidue = currentRTnode.getResidueCirc();
	      Touches touch = currentRTnode.getTouch();
	      
	      if( !currentResidue.isDone() ){
	        
	        TouchShell newShell = new TouchShell( new Touches(touch) );
	        newShell.setRightId( (currentResidue.getTouch()).getFinger() );
	        newAllTouches.add( newShell );
	            
	            //touch.setFinger((current.getTouch()).getFinger());
	            //removeTouch( current.getTouch(), newAllTouches);
	        movelist.add( newShell.getRightTouch() );
	        removeResidue(currentResidue);
	        currentResidue.gotUsed();
	        //removeDuplicateTouches(sortedByDistance, touch);
	        
	      }else{
	        
	         TouchShell newShell = new TouchShell( new Touches(touch) );
	         newAllTouches.add( newShell );
	         newDown.add( touch ); 
	        
	      }
	       
	      
	    }
	    
	     
	   }
	   
	   void removeDuplicateTouches(ArrayList sortedByDistance, Touches t){
	     for(int i = 0; i < sortedByDistance.size(); i ++){
	        ResidueTouchNode node = ( (ResidueTouchNode) sortedByDistance.get(i) );
	        if( node.getTouch().getFinger() == t.getFinger() ){
	           node.getResidueCirc().gotUsed(); 
	        }
	     }
	   }
	   
	   void removeResidue( ResidueCirc rc ){
	     
	    for(int i = 0; i < residue.size(); i++){
	     
	      if( ( (ResidueCirc) residue.get(i) ).getFinger()  == rc.getFinger() ){
	         residue.remove(i);
	        break; 
	      }
	     
	    }   
	   }
	   
	   void addSorted(ArrayList sortedByDistance, ResidueTouchNode node){
	     
	     int i = 0;
	     float diff = node.getDiff();
	     Touches t = node.getTouch();
	     
	     while( !sortedByDistance.isEmpty() && i < sortedByDistance.size() &&diff > ( (ResidueTouchNode) sortedByDistance.get(i) ).getDiff() ){
	       //println("checking");
	       //println(( (ResidueTouchNode) sortedByDistance.get(i) ).getDiff());
	       Touches current = ( (ResidueTouchNode) sortedByDistance.get(i) ).getTouch();
	       if(current.getFinger() == t.getFinger() ){
	       //  println("hi");
	          return; 
	       }
	        i++; 
	     }
	     
	     sortedByDistance.add(i, node);
	     i++;
	     

	     
	     while( !sortedByDistance.isEmpty() && i < sortedByDistance.size() ){
	       Touches current = ( (ResidueTouchNode) sortedByDistance.get(i) ).getTouch();
	       //       println(( (ResidueTouchNode) sortedByDistance.get(i) ).getDiff());
	       if(current.getFinger() == t.getFinger() ){
	       //  println("removing");
	          sortedByDistance.remove(i); 
	       }else{
	          i++; 
	       }
	     }
	     
	     
	   }
	   
	   void printList(ArrayList printlist){
	     p.println("printing list..");
	     for(int i = 0; i < printlist.size(); i++){
	        p.println(( (Touches) printlist.get(i) ).getFinger());
	     } 
	     p.println("sorted..");
	     InsertionSort(printlist);
	          for(int i = 0; i < printlist.size(); i++){
	        p.println(( (Touches) printlist.get(i) ).getFinger());
	     } 
	   }
	   
	   void removeTouch( Touches t, ArrayList newAllTouches){
	      for(int i = 0; i < newAllTouches.size(); i++){
	         Touches current = ((Touches)newAllTouches.get(i));
	         
	         if(current.getFinger() == t.getFinger() ){
	            newAllTouches.remove(i);
	            break; 
	         }
	      } 
	   }
	   
	   void deleteOldResidue(){
	     for(int i = 0; i < residue.size(); i++){
	         if( ((ResidueCirc)residue.get(i)).isDone() ){
	           Touches t = ((ResidueCirc)residue.get(i)).getTouch();
	           //println("deleting old residue");
	           touchesUp.add(t);
	            residue.remove(i);
	            i--; 
	         }
	     }
	     
	   }
	   
	   void InsertionSort(ArrayList sortlist){
	     Touches TouchLocation;
	     
	     for(int i = 1; i < sortlist.size(); i++ ){
	       Touches two = ( (Touches) sortlist.get(i));
	       Touches one = ( (Touches) sortlist.get(i - 1) );
	       
	       if( one.getFinger() > two.getFinger() ){
	         
	         
	         int loc = i - 1;
	         boolean found = false;
	         
	         while( !found && loc > 0){
	           Touches locfinger = ( (Touches) sortlist.get(loc) );
	           
	           if(locfinger.getFinger() < two.getFinger() ){
	              found = true; 
	              loc++;
	           }else{
	              loc --; 
	           }
	         
	         }
	         if(!found){
	           Touches locfinger = ( (Touches) sortlist.get(loc) );
	            if(locfinger.getFinger() < two.getFinger() ){
	                loc++;
	            }
	         }
	         
	         sortlist.remove(i);         
	         sortlist.add(loc, two);
	       }
	      }
	   }
	   
	   void autoDelete(boolean input){
	      deleteoldtouches = input;  
	   }
	   
	   void addNewTouches( ArrayList newUp, ArrayList newDown, ArrayList newMoved, ArrayList newAllTouches){
	     ArrayList up = new ArrayList();
	     ArrayList down = new ArrayList();
	     ArrayList moved = new ArrayList();
	     TouchListener touchListener = tacTile.getTouchListener();;
	     
	     //merging up
	     for(int i = 0; i < touchesUp.size(); i++){
	        up.add(touchesUp.get(i));
	     }
	     
	     for(int i = 0; i < newUp.size(); i++ ){
	        up.add(newUp.get(i));
	     }
	     
	     //merging down
	     for(int i = 0; i < touchesDown.size(); i++){
	        down.add(touchesDown.get(i));
	     }
	     
	     for(int i = 0; i < newDown.size(); i++ ){
	        down.add(newDown.get(i));
	     }

	     //merging moved
	     for(int i = 0; i < touchesMoved.size(); i++){
	        moved.add(touchesMoved.get(i));
	     }
	     
	     for(int i = 0; i < newMoved.size(); i++ ){
	        moved.add(newMoved.get(i));
	     }
	   
	    touchesUp = up;
	    touchesDown = down;
	    touchesMoved = moved;
	    allTouches = newAllTouches;  
	  
	     
	   }
	   
	   boolean checkMoved( Touches oldTouch, Touches newTouch ){
	       float oldx = oldTouch.getXPos() * p.height;
	       float oldy = p.width - oldTouch.getYPos() * p.width;
	       float newx = newTouch.getXPos() * p.height;
	       float newy = p.width - newTouch.getYPos() * p.width;
	       
	       float xdif = oldx - newx;
	       xdif = xdif * xdif;
	       
	       float ydif = oldy - newy;
	       ydif = ydif * ydif;
	       
	       float diff = xdif + ydif;
	       //println(oldx + " " + newx);
	       //println(diff);
	       if( diff > 190 ){
	        // println("true");
	         return true; 
	       }else{
	         return false; 
	       }
	   }
	   
	void allUp(int i){
	      for(i = i; i < allTouches.size(); i++){
	         //touchesUp.add(allTouches.get(i));
	         //println("adding residue in allup");
	         TouchShell shell = ((TouchShell)allTouches.get(i));
	         residue.add(new ResidueCirc( shell.getRightTouch() ));
	      } 
	      
	      if(!allTouches.isEmpty()){
	        allTouches = new ArrayList();
	      }
	   }
	   
	   public ArrayList getTouchesDown(){
		  if(deleteoldtouches || touchesDown.size() > maxListSize){
			deleteOldTouches();
		  }
		  deleteOldResidue();
		  
	      ArrayList temp = touchesDown;
	      touchesDown = new ArrayList();
	      return temp; 
	   }
	   
	  public ArrayList getTouchesUp(){
		  if(deleteoldtouches || touchesUp.size() > maxListSize){
			deleteOldTouches();
		  }
		  
	      ArrayList temp = touchesUp;
	      touchesUp = new ArrayList();
	      return temp; 
	   }
	   
	  public ArrayList getTouchesMoved(){
		  if(deleteoldtouches || touchesMoved.size() > maxListSize){
			deleteOldTouches();
		  }
		  
	      ArrayList temp = touchesMoved;
	      touchesMoved = new ArrayList();
	      return temp; 
	   }
	   
	  /**
	   * Adds a touch to one of the event lists.
	   * 
	   * @param gesture Indicates which list to add to ( 0 = down, 1 = move, 2 = up )
	   * @param touch Touch to be added.
	   */
	   public void addTouch( int gesture, Touches touch ){
		   switch(gesture){
		   case(0): // Down
			   touchesDown.add(touch); break;
		   case(1): // Move
			   touchesMoved.add(touch); break;
		   case(2): // Up
			   touchesUp.add(touch); break;
		   }// switch
	   }// addTouch
	   
	   void setTouchMovedDuplicates( boolean input ){
	     touchMovedDuplicates = input; 
	   }

	/****************************************RESIDUE CIRC CLASS**************************************/
	   
	class ResidueCirc{

	  Touches t;
	  int createdAt;
	  int lifetimer = residuelifetime;
	  float xpos;
	  float ypos;
	  boolean used = false;
	  //float radius;

	  ResidueCirc(Touches touch){
	    createdAt = p.millis();
	    t = touch; 
	    xpos = t.getXPos() * p.height;
	    ypos = p.width - t.getYPos() * p.width; 
	  } 

	  boolean isInside(float inputx, float inputy){
	    //println("inx: " + inputx + ", iny: " + inputy + ", xpos: " + xpos + ", ypos: " + ypos);
	    
	    float xdif = (xpos - inputx);
	    xdif = xdif * xdif;
	    float ydif = (ypos - inputy);
	    ydif = ydif * ydif;
	    float diff = xdif + ydif;    
	    //float radius = .08 * screen.width; 
	    float rad = radius * radius;
	    //println(diff);

	    if( diff < rad ){
	      //println("diff: " + diff);
	      return true && !isDone(); 
	    }
	    else{    
	      return false;
	    }
	  }
	  
	  float getDiff(float inputx, float inputy){

	    float xdif = (xpos - inputx);
	    xdif = xdif * xdif;
	    float ydif = (ypos - inputy);
	    ydif = ydif * ydif;
	    float diff = xdif + ydif;        
	    
	    return diff;
	    
	  }

	  boolean isDone(){
	    return ( p.millis() - createdAt > lifetimer ) || used; 
	  }
	  
	  void gotUsed(){
	     used = true; 
	  }

	  Touches getTouch(){
	    return t; 
	  }
	  
	  int getFinger(){
	     return t.getFinger(); 
	  }
	  
	  void setRadius( float rad ){
	    radius = rad;
	  }
	}


	/****************************************RESIDUE TOUCH NODE CLASS**************************************/



	class ResidueTouchNode{
	  
	   Touches touch;
	   ResidueCirc residue;
	   float diff;
	  
	   ResidueTouchNode(Touches t, ResidueCirc rc, float d){
	    touch = t;
	    residue = rc;  
	    diff = d;
	   } 
	   
	   
	   Touches getTouch(){
	      return touch; 
	   }
	   
	   ResidueCirc getResidueCirc(){
	      return residue; 
	   }
	   
	   float getDiff(){
	      return diff; 
	   } 
	  
	}

	/****************************************TOUCH SHELL CLASS**************************************/


	class TouchShell{
	  
	  int myId;
	  Touches t;
	  
	  TouchShell(Touches touch){
	    t = new Touches(touch);
	    myId = touch.getFinger();
	  }
	  
	  Touches getWrongTouch(){
	     return t; 
	  }
	  
	  Touches getRightTouch(){
	     Touches newt = new Touches(t);
	     newt.setFingerID(myId);
	     return newt; 
	  }
	  
	  void updateTouch(Touches touch){
	     t = new Touches(touch); 
	  }
	  
	  void setRightId(int id){
	     myId = id; 
	  }
	  
	  int getRightId(){
	     return myId; 
	  }
	  
	  int getWrongId(){
	     return t.getFinger(); 
	  }
	}
}// class TouchEventManager
