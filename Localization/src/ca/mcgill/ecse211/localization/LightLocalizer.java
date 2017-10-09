package ca.mcgill.ecse211.localization;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class LightLocalizer {
	
	private static int R_SPEED = 100; 
	private static int FWD_SPEED = 100; 
	//the distance between light sensor and the center of rotation 
	private static double sensorOffset = 10; 
	private static double tile = 30.48; 
	private Odometer odometer; 
	private Navigation navigator; 
	
	
	private float colorValue = 0; 
	private static float colorBlack =100; 
	//the threshold for the light density of black line 
	private static float threshold = 10; 
	private static boolean crossed; 
	private static int numCrossed = 0; 
	private static int lineCounter = 0; 
	
	//allocate ports for the light sensor
	  private static final Port lsPort = LocalEV3.get().getPort("S2");
	  //attach instance of light sensor to the port 
	  private static SensorModes lsSensor = new EV3ColorSensor(lsPort); 
	  //get an instance of a sample provider for each sensor
	  private static SampleProvider lsSample = lsSensor.getMode("Red"); 
	  //allocate buffers for the sensor 
	  private static float[] sampleColor = new float[lsSample.sampleSize()];
	  
	  
	  public LightLocalizer(Odometer odometer, Navigation navigator) {
		  this.odometer = odometer; 
		  this.navigator = navigator; 
		  
	  }
	  
	  public void localizer() {
		  
		  lsSample.fetchSample(sampleColor, 0);
	      colorValue = sampleColor[0] * 1000; 
	      
		  //Go to the origin first 
	      navigator.turnTo(90);
	      
	      while (numCrossed < 1) {
	    	  	crossed = false; 
	    	  	drive(); 
	      }
	      //go back when detecting a black line
	      navigator.roll(-sensorOffset/tile); 
	      //turn 90 degrees 
	      navigator.turnTo(90);
	      
	      while(numCrossed < 2) {
	    	  	crossed = false; 
	    	  	drive(); 
	      }
	      
	      navigator.roll(-sensorOffset/tile); 
	      try {
			LocalizationLab.leftMotor.wait(1000);
		} catch (InterruptedException e1) {
			
		}
	      try {
			LocalizationLab.rightMotor.wait(1000);
		} catch (InterruptedException e1) {
			
		}
	      
	      //light localization starts 
	     
	      double [] angle = new double[4]; 
	      //when rotating 360 degrees (4 lines detected) 
	      while(lineCounter < 4) {
	    	  	//when detecting a line
	    	  	navigator.turnTo(100);
	    	  	if(colorValue <= (colorBlack + threshold)) {
	    	  		//sound the beep and increment line counter
	    	  		Sound.beep();
	    	  		lineCounter++; 
	    	  		//stop the motors 
	    	  		LocalizationLab.rightMotor.stop();
	    		    LocalizationLab.leftMotor.stop();
	    	  		//record the angle when detecting the line 
	    	  		angle[lineCounter] = odometer.getTheta(); 
	    	  		//sleep for 1 seconds 
	    	  		try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						
					} 
	    	  	}
	      }
	      
	      //stop the motors when finished rotating 
	      LocalizationLab.rightMotor.stop();
	      LocalizationLab.leftMotor.stop(); 
	      
	      //calculate thetaX and thetaY
	      double thetaX = angle[2] - angle[0]; 
	      double thetaY = angle[3] - angle[1]; 
	      
	      //calculate x and y value 
	      double x = -sensorOffset*Math.cos(Math.toRadians(thetaY)); 
	      double y = -sensorOffset*Math.cos(Math.toRadians(thetaX)); 
	      
	      //calculate current heading 
	      double theta = 180 - angle[0]; 
	      theta = theta + odometer.getTheta(); 
	      
	      //update the odometer
	      odometer.setX(x);
	      odometer.setY(y);
	      odometer.setTheta(theta);
	      
	      //travel to the origin
	      navigator.travelTo(0, 0);
	      double heading = odometer.getTheta(); 
	      //adjust the heading to 0 degree 
	      navigator.turnTo(-heading);
	  }
	  
	  private void drive() {
		  if(colorValue <= (colorBlack + threshold)) {
	          crossed = true; 
	          Sound.beep(); 
	          numCrossed++; 
	      		}
	      
	      	if(crossed = false) {
	    	  	  	LocalizationLab.leftMotor.setSpeed(FWD_SPEED);
	    	  	  	LocalizationLab.rightMotor.setSpeed(FWD_SPEED);
		      
	    	  	  	LocalizationLab.leftMotor.forward();
	    	  	  	LocalizationLab.rightMotor.forward();
	      	}
	      
	      	if(crossed = true) {
	    	  	 	LocalizationLab.leftMotor.stop();
	    	  	 	LocalizationLab.rightMotor.stop(); 
	      	}
	  }
	  
	  
	  
	  
}
