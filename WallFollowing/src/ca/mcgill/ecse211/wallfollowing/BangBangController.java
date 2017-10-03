package ca.mcgill.ecse211.wallfollowing;

import lejos.hardware.motor.*;



public class BangBangController implements UltrasonicController {
	
	  private final int bandCenter;
	  private final int bandwidth;
	  private final int motorLow;
	  private final int motorHigh;
	  private int filterControl;
	  private int distance;
	  private static final int FILTER_OUT = 30;

	  public BangBangController(int bandCenter, int bandwidth, int motorLow, int motorHigh) {
	    // Default Constructor
	    this.bandCenter = bandCenter;
	    this.bandwidth = bandwidth; //the error threshold
	    this.motorLow = motorLow;
	    this.motorHigh = motorHigh;
	    this.filterControl = 0;
	    WallFollowingLab.leftMotor.setSpeed(motorHigh); // Start robot moving forward
	    WallFollowingLab.rightMotor.setSpeed(motorHigh);
	    WallFollowingLab.leftMotor.forward();
	    WallFollowingLab.rightMotor.forward();
	  }

	  @Override
	  public void processUSData(int distance) {
		  if (distance >= 255 && filterControl < FILTER_OUT) {
		      // bad value, do not set the distance var, however do increment the
		      // filter value
		      filterControl++;
		    } else if (distance >= 255) {
		      // We have repeated large values, so there must actually be nothing
		      // there: leave the distance alone
		      this.distance = distance;
		    } else {
		      // distance went below 255: reset filterand leave
		      // distance alone.
		      filterControl = 0;
		      this.distance = distance;
		    }
	   
	    // TODO: process a movement based on the us distance passed in (BANG-BANG style)
	    //If the distance between the brick and wall is acceptable, keep straight 
	    //else make adjustments, either left or right (bang-bang style)
	    int diff = this.distance - bandCenter; 
//	    if(this.distance < bandwidth) {
//
//	    	WallFollowingLab.leftMotor.setSpeed(motorHigh+500); 
//	    	WallFollowingLab.rightMotor.setSpeed(motorHigh+500);
//	    WallFollowingLab.leftMotor.backward(); 
//	    WallFollowingLab.rightMotor.backward(); 
//	    	}    
	    if(Math.abs(diff) < bandwidth) {
	    	//If the difference is within acceptance, keep straight 
	    		WallFollowingLab.leftMotor.setSpeed(motorHigh); 
	    		WallFollowingLab.rightMotor.setSpeed(motorHigh);
	    		WallFollowingLab.leftMotor.forward(); 
	    		WallFollowingLab.rightMotor.forward(); 
	    		
	    }else {
	    		if(diff < 0) {
	    			//If the brick is too close to the wall, turn right
	    			WallFollowingLab.leftMotor.setSpeed(motorHigh);
	    			WallFollowingLab.rightMotor.setSpeed(motorLow + 50); 
	    			WallFollowingLab.rightMotor.forward(); 
	    			WallFollowingLab.leftMotor.forward(); 
	    		} 
	    		if(diff > 0) {
	    			//If the brick is too far from the wall, turn left
	    			WallFollowingLab.leftMotor.setSpeed(motorLow + 50);
	    			WallFollowingLab.rightMotor.setSpeed(motorHigh);
	    			WallFollowingLab.leftMotor.forward(); 
	    			WallFollowingLab.rightMotor.forward(); 
	    			
	    		}
	    }
	    
	    
	  }

	  @Override
	  public int readUSDistance() {
	    return this.distance;
	  }


}
