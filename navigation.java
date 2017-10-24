package ca.mcgill.ecse211.zipline;


import lejos.hardware.motor.EV3LargeRegulatedMotor;

/*
 * @author: Bill Zhang 
 * @version: 1.3 
 * @since: 2017/10/15
 * 
 */

public class navigation {
	  //Constants 
	  private static final int FORWARD_SPEED = 250;
	  private static final int ROTATE_SPEED = 50;
	  private static final double tile = 30.48; 
	  private static final double leftRadius = 2.2; 
	  private static final double rightRadius = 2.2; 
	  private static final double width = 16.2; 
	  private static final double error = 0.3; 
	  private Odometer odometer; 
	  private static boolean isNavigating = false; 
	  private static double[] position = new double [3]; 
	  private static boolean[] update = {true, true, true}; 
	 
	  
	  //construct 
	  public navigation(Odometer odometer) {
		  this.odometer = odometer; 
	  }

	  public void drive(int x, int y) {
	    
	   
	    	travelTo(x, y); 
	    		
	     
	    
	   
	    }
	  
	  /*
	   * this method convert the distance to a degree that can be passed to rotate()
	   * @param radius is the radius of the wheel 
	   * @param distance is the distance that is desired distance to travel 
	   * @return is the degree that is required in rotate() for the distance 
	   */
	  private static int convertDistance(double radius, double distance) {
	    return (int) ((180.0 * distance) / (Math.PI * radius));
	  }
	  
	  /*
	   * this method convert the angle required to turn to the form of rotate() in EV3
	   * @param radius is the radius of the wheel 
	   * @param width is the distance between two wheels 
	   * @param angle is the angle required to turn 
	   * @return is the degree that can be passed to rotate() in EV3 to make the required turn 
	   */
	  private static int convertAngle(double radius, double width, double angle) {
	    return convertDistance(radius, Math.PI * width * angle / 360.0);
	  }
	  /*
	   * the method makes the robot turn a specific angle 
	   * @param theta is the angle required to turn 
	   * @return is none 
	   */
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
	  /*
	   * the method makes the robot travel a specific distance 
	   * @param distace is the distance required to travel 
	   * @return is none 
	   */
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
	  /*
	   * 
	   * the method navigate the robot between two points using line following 
	   * @param x is the value of x coordinate 
	   * @param y is the value of y coordinate 
	   * @return none 
	   */
	  public void travelTo(double x, double y) {
		  
		  double turn0 = odometer.getTheta(); 
		  turnTo(90-turn0); 
		  
		  //get the current location 
		  odometer.getPosition(position, update);
		  double startX = odometer.getX()/tile ; 
		  double startY = odometer.getY()/tile; 
		  
		  //calculate the difference between the source and destination x and y
		  double distX = x - startX; 
		  double distY = y - startY; 
		  //move forward or backward to get to y value 
		  roll(distX); 
		  //if the x coordinate is different 
		  if(Math.abs(distY) > error) {
			double angle = odometer.getTheta(); 
			//point the robot to 90 degrees 
			turnTo(90 - angle); 
			//move forward or backward to x value 
			roll(distY); 
		   }
		  
		  //turn to the true 0 after reaching each point 
		  double turn = odometer.getTheta(); 
		  turnTo(90-turn); 
		  
		 
		  
	  }
	  /*
	   * This method tells whether the navigation is enable 
	   * @param:none
	   * @return is a boolean value to indicate if the navigation is enable 
	   */
	  public boolean isNavigating() {

		  return isNavigating; 
	  }
	  
	  
	 /*
	  * This method rotates the top motor for the indefinite period of time 
	  * @param: none
	  * @return: none 
	  */
	  public void rotating() {
		  ziplineLab.topMotor.setAcceleration(FORWARD_SPEED);
		  //the top motor always rotates 
		  while(true) {
			  ziplineLab.topMotor.backward();
		  }
	  }
}
