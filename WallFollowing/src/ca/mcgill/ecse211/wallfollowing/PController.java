package ca.mcgill.ecse211.wallfollowing;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {
	
	/* Constants */
	  private static final int MOTOR_SPEED = 300;
	  private static final int FILTER_OUT = 30;
	  private static final double PROPCONST = 10.0; 

	  private final int bandCenter;
	  private final int bandWidth;
	  private int distance;
	  private int filterControl;

	  public PController(int bandCenter, int bandwidth) {
	    this.bandCenter = bandCenter;
	    this.bandWidth = bandwidth;
	    this.filterControl = 0;

	    WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED); // Initalize motor rolling forward
	    WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
	    WallFollowingLab.leftMotor.forward();
	    WallFollowingLab.rightMotor.forward();
	  }

	  @Override
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
	    int error = this.distance - bandCenter; 
	    int adjust = 0; 
	    
	    //the error is within the threshold, no need to change 
	    if(Math.abs(error) <= bandWidth) {
	    		WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED);
	    		WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
	    		WallFollowingLab.leftMotor.forward();
	    		WallFollowingLab.rightMotor.forward();
	    }
	    //the brick is too close to the wall, turn left
	    else if(error < 0) {
	    		adjust = calcProp(error); 
	    		WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED + adjust); 
	    		WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED - adjust);
	    		WallFollowingLab.leftMotor.forward();
	    		WallFollowingLab.rightMotor.forward();
	    	
	    } 
	    //the brick is too far away from the wall 
	    else if (error > 0) {
	    		adjust = calcProp(error); 
	    		WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED - adjust);
	    		WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED + adjust);
	    		WallFollowingLab.leftMotor.forward();
	    		WallFollowingLab.rightMotor.forward();
	    }
	  }
	  
	  public int calcProp(int diff) {
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


	  @Override
	  public int readUSDistance() {
	    return this.distance;
	  }

}
