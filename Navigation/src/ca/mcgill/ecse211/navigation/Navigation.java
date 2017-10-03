package ca.mcgill.ecse211.navigation;


import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Navigation extends Thread {
	private static final int FORWARD_SPEED = 250;
	  private static final int ROTATE_SPEED = 150;
	  private static final double tile = 30.48; 
	  private static final double leftRadius = 2.2; 
	  private static final double rightRadius = 2.2; 
	  private static final double width = 13.5; 
	  private static int[] p1 = {0, 2}; 
	  private static int[] p2 = {1, 1}; 
	  private static int[] p3 = {2, 2}; 
	  private static int[] p4 = {2, 1}; 
	  private static int[] p5 = {1, 0}; 
	  private static int[][] path = {p1, p2, p3, p4, p5}; 
	  private Odometer odometer; 
	  public boolean isNavigating = false; 
	  private static final double error = 0.164; 
	  private static double[] position = new double [3]; 
	  private static boolean[] update = {true, true, true}; 
	  private static final Port usPort = LocalEV3.get().getPort("S1");
	  SensorModes usSensor = new EV3UltrasonicSensor(usPort); // usSensor is the instance
	  SampleProvider usDistance = usSensor.getMode("Distance"); // usDistance provides samples from                                                            // this instance
	  float[] usData = new float[usDistance.sampleSize()]; 
	  private sensorPoller sensorpoller = new sensorPoller(usDistance, usData); 
	  private static final int MOTOR_SPEED = 300;
	  private static final int S_SPEED = 50; 
	  private static final int FILTER_OUT = 30;
	  private static final double PROPCONST = 10.0; 
	  private static int bandCenter = 30;
	  private static int bandWidth = 5;
	  private int filterControl; 
	 
	  
	  
	  public Navigation(Odometer odometer) {
		  this.odometer = odometer; 
	  }

	  public void drive() {
	    // reset the motors
	    for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] {NavigationLab.leftMotor, NavigationLab.rightMotor}) {
	      motor.stop();
	      motor.setAcceleration(3000);
	    }

	    // wait 5 seconds
	    try {
	      Thread.sleep(2000);
	    } catch (InterruptedException e) {
	      // there is nothing to be done here because it is not expected that
	      // the odometer will be interrupted by another thread
	    }
	    
	    		sensorpoller.start(); 
	    		for(int i = 0; i < 5; i++) {
	    			driveAvoidance(path[i][0], path[i][1]); 
	    			//travelTo(path[i][0], path[i][1]); 
	    		
	    		}
	    
	    
	    
	    

	     
	       
	     
	    }
	  
	  
	  
	  

	  private static int convertDistance(double radius, double distance) {
	    return (int) ((180.0 * distance) / (Math.PI * radius));
	  }

	  private static int convertAngle(double radius, double width, double angle) {
	    return convertDistance(radius, Math.PI * width * angle / 360.0);
	  }
	  
	  
	  
	  private static void turnTo(double theta) {
		  NavigationLab.leftMotor.setSpeed(ROTATE_SPEED);
	      NavigationLab.rightMotor.setSpeed(ROTATE_SPEED);

	      NavigationLab.leftMotor.rotate(convertAngle(leftRadius, width, theta), true);
	      NavigationLab.rightMotor.rotate(-convertAngle(rightRadius, width, theta), false);
	  }
	  
	  private static void roll(double distance) {
		  NavigationLab.leftMotor.setSpeed(FORWARD_SPEED);
	      NavigationLab.rightMotor.setSpeed(FORWARD_SPEED);

	      NavigationLab.leftMotor.rotate(convertDistance(leftRadius, (tile*distance)), true);
	      NavigationLab.rightMotor.rotate(convertDistance(rightRadius, (tile*distance)), false);
	  }
	  
	  private void travelTo(double x, double y) {
		  
		  isNavigating = true; 
		  //get the current location 
		  odometer.getPosition(position, update);
		  double startX = odometer.getX()/tile ; 
		  double startY = odometer.getY()/tile; 
		  double theta = position[2]; 
		  //calculate the distance and angle 
		  double distX = x - startX; 
		  double distY = y - startY; 
		  double angle = calcAngle(theta, distX, distY); 
		  //turn to the min angle requried 
		  turnTo(angle); 
		  //start moving
		  //if going along the diagonal 
		  if(Math.abs(distX) > error && (Math.abs(distY) > error)) {
			  double distT = calcDist(distY, distX); 
			  roll(distT); 
			 
		  }else {
			  
			  roll(distX); 
			  roll(distY); 
		  }
		  
		  isNavigating = false; 
		  
		  
	  }
	  
	  public boolean isNavigating() {

		  return isNavigating; 
	  }
	  
	  private double calcAngle(double theta, double distX, double distY) {
		  
		  //calculate the absolute turn required to turn 
		  double turn = Math.toDegrees(Math.atan2(distX, distY)); 
		 //calculate the relative angle required to perform 
		 double angle = 0; 
		  //find the min angle 
		  try {
			  if(turn < 0) {
				  turn += 360; 
			  }
			  
			  angle = turn - theta; 
			  if(Math.abs(angle) > 180) {
				  if(distY > 0) {
					  angle += 360; 
				  }
				  if(distY < 0) {
					  theta -= 360; 
				  }
			  }
		  }
		  
		  catch(Exception e) {
			  
		  }
		  
		  return angle; 
	  }
	  
	  private double calcDist(double x, double y) {
		  double displacement = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)); 
		  return displacement; 
	  }
	  
	  public static void processUSData(int distance) {
		  double error = distance - bandCenter; 
		  int adjust = 0; 
		  if(Math.abs(error) <= bandWidth) {
	    		//when getting close to an obstacle, turn the sensor to 45 degree
	    		//prepare for performing wall following 
	    		NavigationLab.sMotor.setSpeed(ROTATE_SPEED);
	    		NavigationLab.sMotor.rotate(-45); 
	    }
	    //the brick is too close to the wall, turn left
	     if(error < 0) {
	    		adjust = calcProp(error); 
	    		NavigationLab.leftMotor.setSpeed(MOTOR_SPEED + adjust); 
	    		NavigationLab.rightMotor.setSpeed(MOTOR_SPEED - adjust);
	    		NavigationLab.leftMotor.forward();
	    		NavigationLab.rightMotor.forward();
	    	
	    } 
	  }
	  
	  private static int calcProp(double diff) {
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
	  
	  public void driveAvoidance(double x, double y) {
		  isNavigating = true; 
		  odometer.getPosition(position, update);
		  double startX = odometer.getX()/tile ; 
		  double startY = odometer.getY()/tile; 
		  double theta = position[2]; 
		  //calculate the distance and angle 
		  double distX = x - startX; 
		  double distY = y - startY; 
		  double angle = calcAngle(theta, distX, distY); 
		  //turn to the min angle requried 
		  turnTo(angle); 
		  //start moving
		  //if going along the diagonal 
		  if(Math.abs(distX) > error && (Math.abs(distY) > error)) {
			  double distT = calcDist(distY, distX); 
			  roll(distT); 
			 
		  }else {
			  
			  roll(distX); 
			  roll(distY); 
		  }
		  
		  while(true) {
			  if(sensorpoller.getDistance() < bandCenter) {
				  do {
					  int distance = filter(); 
					  processUSData(distance); 
				  }while(sensorpoller.getDistance() < bandCenter); 
				  
				  NavigationLab.leftMotor.setSpeed(FORWARD_SPEED);
				  NavigationLab.rightMotor.setSpeed(FORWARD_SPEED);
				  NavigationLab.sMotor.setSpeed(S_SPEED);
				  
				  NavigationLab.leftMotor.forward();
				  NavigationLab.rightMotor.forward();
				  NavigationLab.sMotor.rotateTo(0);
				  
				  try {
					sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					
				} 
				  
				  travelTo(x, y); 
				  isNavigating = false; 
			  }
		  }
	  }
	  
	  private int filter() {
		  int distance = sensorpoller.getDistance(); 
		  if (distance >= 255 && filterControl < FILTER_OUT) {
		      // bad value, do not set the distance var, however do increment the
		      // filter value
		      filterControl++;
		    } else if (distance >= 255) {
		      // We have repeated large values, so there must actually be nothing
		      // there: leave the distance alone
		      return distance;
		    } else {
		      // distance went below 255: reset filter and leave
		      // distance alone.
		      filterControl = 0;
		      return distance;
		    }

		  return distance; 
	  }
	  
	  
	  
}
