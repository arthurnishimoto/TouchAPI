package tacTile.net;

/**
 * A wrapper class to store PQLabs gesture information. Not fully supported 9/2/10.
 * 
 * @author Arthur Nishimoto - anishimoto42@gmail.com
 *
 */
public class Gestures {
	int gesture;
	float p1, p2, p3, p4, p5, p6;
	
	Gestures( int type, float p1, float p2, float p3, float p4, float p5, float p6 ){
		gesture = type;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
		this.p5 = p5;
		this.p6 = p6;
	}
}
