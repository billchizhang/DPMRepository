package ca.mcgill.ecse211.navigation;


//problem yet to solve, how to exit and return back 
public class usController {

	/* Constants */
	  private static final int MOTOR_SPEED = 300;
	  private static final int ROTATE_SPEED = 50; 
	  private static final int FILTER_OUT = 30;
	  private static final double PROPCONST = 10.0; 

	  private int bandCenter = 30;
	  private int bandWidth = 5;
	  private int distance;
	  private int filterControl;

	  public usController(int bandCenter, int bandwidth) {
	    this.bandCenter = bandCenter;
	    this.bandWidth = bandwidth;
	    this.filterControl = 0;
	    //reset the sensor motor to turn to facing straight 
	    NavigationLab.sMotor.rotateTo(0);
	    
	  }

	  public void processUSData(int distance) {

	    // rudimentary filter - toss out invalid samples corresponding to null
	    // signal.
	    // (n.b. this was not included in the Bang-bang controller, but easily
	    // could have).
	    //
	    if (distance >= 255 && filterControl < FILTER_OUT) {
	      // bad value, do not set the distance var, however do increment the
	      // filter value
	      filterControl++;
	    } else if (distance >= 255) {
	      // We have repeated large values, so there must actually be nothing
	      // there: leave the distance alone
	      this.distance = distance;
	    } else {
	      // distance went below 255: reset filter and leave
	      // distance alone.
	      filterControl = 0;
	      this.distance = distance;
	    }

	    // TODO: process a movement based on the us distance passed in (P style)
	    double error = this.distance - bandCenter; 
	    int adjust = 0; 
	    
	    
	    if(Math.abs(error) <= bandWidth) {
	    		//when getting close to an obstacle, turn the sensor to 45 degree
	    		//prepare for performing wall following 
	    		NavigationLab.sMotor.setSpeed(ROTATE_SPEED);
	    		NavigationLab.sMotor.rotate(-45); 
	    }
	    //the brick is too close to the wall, turn left
	    else if(error < 0) {
	    		adjust = calcProp(error); 
	    		NavigationLab.leftMotor.setSpeed(MOTOR_SPEED + adjust); 
	    		NavigationLab.rightMotor.setSpeed(MOTOR_SPEED - adjust);
	    		NavigationLab.leftMotor.forward();
	    		NavigationLab.rightMotor.forward();
	    	
	    } 
	    //the brick is too far away from the wall 
	   /* else if (error > 0) {
	    		adjust = calcProp(error); 
	    		NavigationLab.leftMotor.setSpeed(MOTOR_SPEED - adjust);
	    		NavigationLab.rightMotor.setSpeed(MOTOR_SPEED + adjust);
	    		NavigationLab.leftMotor.forward();
	    		NavigationLab.rightMotor.forward();
	    }*/
	    if(error > 20) {
	    		System.exit(0);
	    }
	    
	  }
	  
	  public int calcProp(double diff) {
		  int correction = 0; 
		  if(diff < 0) {
			  diff = -diff; 
		  } 
		  correction = (int)(PROPCONST * (double)diff); 
		  if(correction >= MOTOR_SPEED) {
			  correction = 50; 
		  }
		  return correction; 
	  }


	  public int readUSDistance() {
	    return this.distance;
	  }

}
