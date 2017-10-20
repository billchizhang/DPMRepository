package ca.mcgill.ecse211.zipline;


import lejos.hardware.motor.EV3LargeRegulatedMotor;



public class navigation {
	  //Constants 
	  private static final int FORWARD_SPEED = 250;
	  private static final int ROTATE_SPEED = 50;
	  private static final double tile = 30.48; 
	  private static final double leftRadius = 2.2; 
	  private static final double rightRadius = 2.2; 
	  private static final double width = 16.2; 
	  private Odometer odometer; 
	  private static boolean isNavigating = false; 
	  private static double[] position = new double [3]; 
	  private static boolean[] update = {true, true, true}; 
	 
	  
	  
	  public navigation(Odometer odometer) {
		  this.odometer = odometer; 
	  }

	  public void drive(int x, int y) {
	    // reset the motors
	    for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] {ziplineLab.leftMotor, ziplineLab.rightMotor}) {
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
	    
	   
	    	travelTo(x, y); 
	    		
	     
	    
	   
	    }
	  

	  private static int convertDistance(double radius, double distance) {
	    return (int) ((180.0 * distance) / (Math.PI * radius));
	  }

	  private static int convertAngle(double radius, double width, double angle) {
	    return convertDistance(radius, Math.PI * width * angle / 360.0);
	  }
	  
	  
	  
	  public void turnTo(double theta) {
		  ziplineLab.leftMotor.setSpeed(ROTATE_SPEED);
	      ziplineLab.rightMotor.setSpeed(ROTATE_SPEED);
	      
	      //when the wheel is about to turn, set isNavigating to true
	      isNavigating = true; 

	      ziplineLab.leftMotor.rotate(convertAngle(leftRadius, width, theta), true);
	      ziplineLab.rightMotor.rotate(-convertAngle(rightRadius, width, theta), false);
	      //when the wheels stopped, set isNavigating back to false 
	      isNavigating = false; 
	  }
	  
	  public void roll(double distance) {
		  ziplineLab.leftMotor.setSpeed(FORWARD_SPEED);
	      ziplineLab.rightMotor.setSpeed(FORWARD_SPEED);
	      
	      //when the wheel is about to turn, set isNavigating to true
	      isNavigating = true; 
	      
	      ziplineLab.leftMotor.rotate(convertDistance(leftRadius, (tile*distance)), true);
	      ziplineLab.rightMotor.rotate(convertDistance(rightRadius, (tile*distance)), false);
	      
	      //when the wheels stopped, set isNavigating back to false 
	      isNavigating = false; 
	  }
	  
	  public void travelTo(double x, double y) {
		  
		  
		  //get the current location 
		  odometer.getPosition(position, update);
		  double startX = odometer.getX()/tile ; 
		  double startY = odometer.getY()/tile; 
		  
		  //calculate the difference between the source and destination x and y
		  double distX = x - startX; 
		  double distY = y - startY; 
		  
		  //roll until reaching y 
		  roll(distY); 
		  turnTo(90); 
		  //roll until reaching x 
		  roll(distX); 
			  
		 
		  
	  }
	  
	  public boolean isNavigating() {

		  return isNavigating; 
	  }
	  
	  private double calcAngle(double theta, double distX, double distY) {
		  
		  //calculate the absolute turn required to turn 
		  double turn = Math.toDegrees(Math.atan2(distX, distY)); 
		 //calculate the relative angle required to perform 
		 double angle = 0; 
		   
		  try {
			  if(turn < 0) {
				  turn += 360; 
			  }
			  //find the angle needs to turn 
			  angle = turn - theta; 
			  //if the angle is greater than 180 which means it is not optimal 
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
		  //find the min distance using the sum of two vectors 
		  double displacement = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)); 
		  return displacement; 
	  }
	  
	  
	  public void rotating() {
		  ziplineLab.topMotor.setAcceleration(FORWARD_SPEED);
		  //the top motor always rotates 
		  while(true) {
			  ziplineLab.topMotor.backward();
		  }
	  }
}
