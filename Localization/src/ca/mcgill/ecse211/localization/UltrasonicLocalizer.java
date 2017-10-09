package ca.mcgill.ecse211.localization;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class UltrasonicLocalizer {
	//true for rising edge, false for falling edge 
	public boolean routineType; 
	private static int R_SPEED = 50; 
	private static int A_SPEED = 100; 
	private static int FILTER_OUT = 5; 
	private static int MAX = 62; 
	//d value determined by testing 
	private static int dWall = 35; 
	private static int noiseMargin = 3; 
	private static int wallOffset = 42; 
	private int filterControl = 0; 
	private static final double leftRadius = 2.2; 
	private static final double rightRadius = 2.2; 
	private static final double width = 13.8;
	private static final int ROTATE_SPEED = 150;
	
	private Odometer odometer; 
	private SampleProvider us; 
	private float usData[]; 
	//constructor 
	public UltrasonicLocalizer(Odometer odometer, 
			boolean routineType, SampleProvider us, float usData[]) {
		
		this.odometer = odometer; 
		this.routineType = routineType; 
		this.us = us; 
		this.usData = usData; 
		
		
	}
	
	public void localize() {
		
		//pre-set up for motor 
		LocalizationLab.leftMotor.setAcceleration(A_SPEED); 
		LocalizationLab.rightMotor.setAcceleration(A_SPEED);
		//falling edge is selected 
		if(routineType == false) {
			//keep turning clockwise until passing the wall 
			while(usFilter() < dWall + noiseMargin) {
				
				rotateCW(); 
				
			} 
			//keep turning clockwise until encountering the wall 
			while(usFilter() > dWall) {
				
				rotateCW(); 
			}
			//stop the robot and beep 
			Sound.beep();
			LocalizationLab.leftMotor.stop();
			LocalizationLab.rightMotor.stop(); 
			//record the current heading 
			double alpha = odometer.getTheta(); 
			//keep turning counterclockwise until passing the wall 
			while(usFilter() < dWall + noiseMargin) {
				rotateCCW();  
			}
			//keep turning counterclockwise until encountering the wall 
			while(usFilter() > dWall) {
				rotateCCW(); 
			}
			//stop the robot and sound the beep 
			Sound.beep(); 
			LocalizationLab.leftMotor.stop();
			LocalizationLab.rightMotor.stop();
			//record the current heading 
			double beta = odometer.getTheta(); 
			double cTheta = odometer.getTheta(); 
			double thetaR = calcAngle(alpha, beta) + cTheta; 
			odometer.setTheta(thetaR); 
			turnTo(-thetaR); 
			
		} 
		//rising  edge is selected 
		else {
			//keep turning clockwise until passing the wall 
			while(usFilter() > dWall - noiseMargin) {
				
				rotateCCW(); 
				
			} 
			//keep turning clockwise until encountering the wall 
			while(usFilter() < dWall) {
				
				rotateCCW(); 
			}
			//stop the robot and beep 
			Sound.beep();
			LocalizationLab.leftMotor.stop();
			LocalizationLab.rightMotor.stop(); 
			//record the current heading 
			double alpha = odometer.getTheta(); 
			//keep turning counterclockwise until passing the wall 
			while(usFilter() > dWall - noiseMargin) {
				rotateCW();  
			}
			//keep turning counterclockwise until encountering the wall 
			while(usFilter() < dWall) {
				rotateCW(); 
			}
			//stop the robot and sound the beep 
			Sound.beep(); 
			LocalizationLab.leftMotor.stop();
			LocalizationLab.rightMotor.stop();
			//record the current heading 
			double beta = odometer.getTheta(); 
			double cTheta = odometer.getTheta(); 
			double thetaR = calcAngle(alpha, beta) + cTheta; 
			odometer.setTheta(thetaR); 
			turnTo(-thetaR); 
		}
	}
	
	private void rotateCW() {
		//turn clockwise
		LocalizationLab.leftMotor.setSpeed(R_SPEED);
		LocalizationLab.rightMotor.setSpeed(R_SPEED);
		
		LocalizationLab.leftMotor.forward(); 
		LocalizationLab.rightMotor.backward();
	}
	
	private void rotateCCW() {
		//turn counterclockwise 
		LocalizationLab.leftMotor.setSpeed(R_SPEED);
		LocalizationLab.rightMotor.setSpeed(R_SPEED);
		
		LocalizationLab.leftMotor.backward(); 
		LocalizationLab.rightMotor.forward();
	}
	
	private float usFilter() {
		//get and store ultrasonic data from the sensor 
		us.fetchSample(usData, 0);
		int distance = (int)(usData[0] * 100); 
		int distanceF = 0; 
		//an alternative distance when the data is invalid 
		int distanceP = MAX; 
		if(distance > MAX && filterControl < FILTER_OUT) {
			//the data is not good, increment the filter control 
			//and return the alternative distance 
			filterControl++; 
			distanceF = distanceP; 

		} else if(distance > MAX) {
			//if the distance is actually large, return the value
			distanceF = distance; 
		}else {
			//everything is normal, return the distance 
			filterControl = 0; 
			distanceF = distance; 
		}
		//update the alternative distance to the distance got from the sensor 
		distanceP = distanceF; 
		
		return distanceF; 
	}
	
	public static void turnTo(double theta) {
		
		  LocalizationLab.leftMotor.setAcceleration(A_SPEED);
		  LocalizationLab.rightMotor.setAcceleration(A_SPEED);
		  
		  
		  LocalizationLab.leftMotor.setSpeed(ROTATE_SPEED);
	      LocalizationLab.rightMotor.setSpeed(ROTATE_SPEED);

	      LocalizationLab.leftMotor.rotate(convertAngle(leftRadius, width, theta), true);
	      LocalizationLab.rightMotor.rotate(-convertAngle(rightRadius, width, theta), false);
	      //when the wheels stopped, set isNavigating back to false 
	      
	  }
	
	private static int convertDistance(double radius, double distance) {
	    return (int) ((180.0 * distance) / (Math.PI * radius));
	  }

	private static int convertAngle(double radius, double width, double angle) {
	    return convertDistance(radius, Math.PI * width * angle / 360.0);
	  }
	  
	private static double calcAngle(double alpha, double beta) {
		double theta; 
		double aveHeading = (alpha + beta)/2; 
		if(alpha < beta) {
			theta = 45 - aveHeading; 
		} else {
			theta = 225 - aveHeading; 
		}
		
		return theta; 
	}
	  


}
