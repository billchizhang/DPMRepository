package ca.mcgill.ecse211.navigation;


import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation {
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
	  private static boolean isNavigating = false; 
	  private static final double error = 0.164; 
	  private static double[] position = new double [3]; 
	  private static boolean[] update = {true, true, true}; 
	 
	  
	  
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
	    
	    
	    for(int i = 0; i < 5; i++) {
	    		travelTo(path[i][0], path[i][1]); 
	    		
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
	  
}
