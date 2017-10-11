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
	private static double sensorOffset = 19.5; 
	private static double tile = 30.48; 
	private Odometer odometer; 
	private Navigation navigator; 
	
	
	private float colorValue = 0; 
	private static float colorBlack =100; 
	//the threshold for the light density of black line 
	private static float threshold = 10; 
	private static boolean crossed; 
	private static int numCrossed = 0; 
	//record the number of lines crossed 
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
	      //drive along the diagonal 
	      navigator.turnTo(-142);
	      boolean rolling = true; 
	      navigator.roll(1.7);
	      //when crossing a black line, stop the robot 
	      while(rolling) {
	    	  	if(colorValue <= (colorBlack + threshold)) {
	    	  		crossed = true; 
	    	  		Sound.beep(); 
	    	  		numCrossed++; 
	    	  		LocalizationLab.leftMotor.stop(); 
	    	  		LocalizationLab.rightMotor.stop(); 
	    	  		rolling = false; 
	    	  	}
	    	  	
	      }
	      //the sensor is in the back, backwards for the sensor distance 
	      LocalizationLab.leftMotor.setSpeed(FWD_SPEED);
	  	  LocalizationLab.rightMotor.setSpeed(FWD_SPEED);
	  	  navigator.roll(-(sensorOffset*1.2)/tile);
	  	  //sleep for 10 second for the measurement 
	  	  try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
		
			e1.printStackTrace();
		}
	   
	      //light localization starts 
	     
	      double [] angle = new double[4]; 
	      //when rotating 360 degrees (4 lines detected) 
	      while(lineCounter < 4) {
	    	  	//when detecting a line
	    	  	navigator.turnTo(90);
	    	  	if(colorValue <= (colorBlack + threshold)) {
	    	  		//sound the beep and increment line counter
	    	  		Sound.beep();
	    	  		lineCounter++; 
	    	  		//stop the motors 
	    	  		LocalizationLab.rightMotor.stop();
	    		    LocalizationLab.leftMotor.stop();
	    	  		//record the angle when detecting the line 
	    	  		angle[(lineCounter-1)] = odometer.getTheta(); 
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
	      double x = -sensorOffset*Math.cos(Math.toRadians(thetaY/2)); 
	      double y = -sensorOffset*Math.cos(Math.toRadians(thetaX/2)); 
	      
	      //calculate current heading 
	      double theta = 276 - (3*thetaY)/2; 
	      theta = theta + odometer.getTheta(); 
	      
	      //update the odometer
	      odometer.setX(x);
	      odometer.setY(y);
	      odometer.setTheta(theta);
	      
	      
	      double heading = odometer.getTheta(); 
	      //adjust the heading to 0 degree 
	      navigator.turnTo(-heading);
	  }
	  
	  
	  
	  
	  
	  
}
