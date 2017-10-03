package ca.mcgill.ecse211.navigation;


import lejos.hardware.motor.EV3LargeRegulatedMotor;
public class Odometer extends Thread{
	
	 // robot position
	  private double x;
	  private double y;
	  private double theta;
	  private int leftMotorTachoCount;
	  private int rightMotorTachoCount;
	  private EV3LargeRegulatedMotor leftMotor;
	  private EV3LargeRegulatedMotor rightMotor;
	  private double tile = 30.48; 

	  private static final long ODOMETER_PERIOD = 25; /*odometer update period, in ms*/

	  private Object lock; /*lock object for mutual exclusion*/
	  
	  //My constants:
	  private final double wheelRadius = 2.2; //measured in cm
	  private final double wheelBandRadius = 14.3;
	  private int updatedLeftMotorTachoCount;
	  private int updatedRightMotorTachoCount;
	  
	  // default constructor
	  public Odometer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
	    this.leftMotor = leftMotor;
	    this.rightMotor = rightMotor;
	    this.x = -tile/2;
	    this.y = -tile/2;
	    this.theta = 0.0;
	    this.leftMotorTachoCount = 0;
	    this.rightMotorTachoCount = 0;
	    lock = new Object();
	  }

	  // run method (required for Thread)
	  public void run() {
	    long updateStart, updateEnd;

	    
	    //my variables
	    double distL; //distance traveled by left wheel
	    double distR; //distance traveled by right wheel
	    double deltaD; //vehicle displacement
	    double deltaT; //change in heading
	    double dx; //X component of displacement
	    double dy; //Y component of displacement
	    
	    while (true) {
	      updateStart = System.currentTimeMillis();
	      // TODO put (some of) your odometer code here
	      //current values of left&right wheels' rotation angles:
	      updatedRightMotorTachoCount = rightMotor.getTachoCount();
	      updatedLeftMotorTachoCount = leftMotor.getTachoCount();
	      
	      //To get the distance traveled by each wheel:
	      //distance = 2pi * wheel radius * wheel rotation angle / 360
	      //wheel rotation angle =  new angle - old angle 
	      distR = Math.PI * wheelRadius * (updatedRightMotorTachoCount-rightMotorTachoCount) / 180;
	      distL = Math.PI * wheelRadius * (updatedLeftMotorTachoCount-leftMotorTachoCount) / 180;
	      rightMotorTachoCount = updatedRightMotorTachoCount; //save the rotation angles for the next iteration
	      leftMotorTachoCount = updatedLeftMotorTachoCount;
	      //To get the vehicle's displacement: d = (dL+dR)/2:
	      deltaD = (distR+distL)/2.0;
	      //To get the change in heading: thetaH = (dL-dR)/Wheel Band Radius
	      
	      /*This will be the assumed robot's course --> x & y are updated accordingly
	      
	      y-axis
	      x-axis                            
	      */
	      deltaT = Math.toDegrees((distL-distR)/wheelBandRadius); 
	      
	      
	      //To get the x & y component of dislacement: 
	      
	      //Now we can update the x, y, theta:
	      synchronized (lock) {
	        /**
	         * Don't use the variables x, y, or theta anywhere but here! Only update the values of x, y,
	         * and theta in this block. Do not perform complex math
	         * 
	         */
	    	  	dx = deltaD*Math.sin(Math.toRadians(theta));
	    	  	dy = deltaD*Math.cos(Math.toRadians(theta));
	    	  //x += dx/100;
	    	  //y += dy/100;
	    	  	x = x + dx; 
	    	  	y = y + dy; 
	    	  	theta = theta + deltaT; // TODO replace example value
	    	  	 
	    	  	if(theta > 360) {
	    	  		theta = theta - 360; 
	    	  	}
	    	  
	        
	      }

	      // this ensures that the odometer only runs once every period
	      updateEnd = System.currentTimeMillis();
	      if (updateEnd - updateStart < ODOMETER_PERIOD) {
	        try {
	          Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
	        } catch (InterruptedException e) {
	          // there is nothing to be done here because it is not
	          // expected that the odometer will be interrupted by
	          // another thread
	        }
	      }
	    }
	  }
	  
	  //My method: 
	  public double radianToDegree (double radian){
		  double degree = radian * 180 / Math.PI;
		  return degree;
	  }
	  
	  
	  public void getPosition(double[] position, boolean[] update) {
	    // ensure that the values don't change while the odometer is running
	    synchronized (lock) {
	      if (update[0])
	        position[0] = x;
	      if (update[1])
	        position[1] = y;
	      if (update[2])
	        position[2] = theta;
	    }
	  }

	  public double getX() {
	    double result;

	    synchronized (lock) {
	      result = x;
	    }

	    return result;
	  }

	  public double getY() {
	    double result;

	    synchronized (lock) {
	      result = y;
	    }

	    return result;
	  }

	  public double getTheta() {
	    double result;

	    synchronized (lock) {
	      result = theta;
	    }

	    return result;
	  }

	  // mutators
	  public void setPosition(double[] position, boolean[] update) {
	    // ensure that the values don't change while the odometer is running
	    synchronized (lock) {
	      if (update[0])
	        x = position[0];
	      if (update[1])
	        y = position[1];
	      if (update[2])
	        theta = position[2];
	    }
	  }

	  public void setX(double x) {
	    synchronized (lock) {
	      this.x = x;
	    }
	  }

	  public void setY(double y) {
	    synchronized (lock) {
	      this.y = y;
	    }
	  }

	  public void setTheta(double theta) {
	    synchronized (lock) {
	      this.theta = theta;
	    }
	  }

	  /**
	   * @return the leftMotorTachoCount
	   */
	  public int getLeftMotorTachoCount() {
	    return leftMotorTachoCount;
	  }

	  /**
	   * @param leftMotorTachoCount the leftMotorTachoCount to set
	   */
	  public void setLeftMotorTachoCount(int leftMotorTachoCount) {
	    synchronized (lock) {
	      this.leftMotorTachoCount = leftMotorTachoCount;
	    }
	  }

	  /**
	   * @return the rightMotorTachoCount
	   */
	  public int getRightMotorTachoCount() {
	    return rightMotorTachoCount;
	  }

	  /**
	   * @param rightMotorTachoCount the rightMotorTachoCount to set
	   */
	  public void setRightMotorTachoCount(int rightMotorTachoCount) {
	    synchronized (lock) {
	      this.rightMotorTachoCount = rightMotorTachoCount;
	    }
	  }
}
