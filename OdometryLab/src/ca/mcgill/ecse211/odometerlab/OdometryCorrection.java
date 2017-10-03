package ca.mcgill.ecse211.odometerlab;
import lejos.hardware.sensor.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.robotics.SampleProvider;
import lejos.hardware.Button;
import lejos.hardware.Sound;

public class OdometryCorrection extends Thread {
  private static final long CORRECTION_PERIOD = 10;
  private Odometer odometer;
  
  //added constants 
  
  //allocate ports for the light sensor
  private static final Port lsPort = LocalEV3.get().getPort("S2");
  //attach instance of light sensor to the port 
  private static SensorModes lsSensor = new EV3ColorSensor(lsPort); 
  //get an instance of a sample provider for each sensor
  private static SampleProvider lsSample = lsSensor.getMode("Red"); 
  //allocate buffers for the sensor 
  private static float[] sampleColor = new float[lsSample.sampleSize()]; 
  //reading from light sensor 
  private static float colorValue = 0; 
  //the reflection density of black line 
  private static float colorBlack =100; 
  //the threshold for the light density of black line 
  private static float threshold = 10; 
  //the length of the tile 
  private static double tile = 30.48; 
  //the distance between light sensor and wheel 
  private static double sensorOffset = 1.7; 
  //the threshold of theta error 
  private static double angularOffset = 7; 
  //the number of lines crossed 
  private static int numCrossed = 0; 
  //the number of lines crossed heading north 
  private static int numCrossedN = 0; 
  //the number of lines crossed heading south 
  private static int numCrossedS = 0; 
  //the number of lines crossed heading east 
  private static int numCrossedE = 0; 
  //the number of lines crossed heading west 
  private static int numCrossedW = 0; 
  //if the cart crossed the line 
  private static boolean cross; 
  private static double theta = 0; 
  //the max x or y value 
  private static double MAX = tile/2 + tile * 2; 
  
  private static double MIN = -tile/2; 
  
  private static double actualX = 0; 
  
  private static double actualY = 0; 
  
  private static double distanceOffset = 0; 
  

  
  // constructor
  public OdometryCorrection(Odometer odometer) {
    this.odometer = odometer;
  }

  // run method (required for Thread)
  public void run() {
    long correctionStart, correctionEnd;
    

    while (true) {
      correctionStart = System.currentTimeMillis();
      
      //TODO Place correction implementation here
      lsSample.fetchSample(sampleColor, 0);
      colorValue = sampleColor[0] * 1000; 
      
      cross = false;
      //if the brick crossed a black line, update the status and line number 
      if(colorValue <= (colorBlack + threshold)) {
        cross = true; 
        numCrossed++; 
        Sound.beep(); 
      }
      
      
      
      if(cross == true){
    	  
    	    
    	  	  theta = odometer.getTheta(); 
          actualX = odometer.getX();
          actualY = odometer.getY();
    	  
    	  		if(Math.abs(theta) < angularOffset){
              //the brick is moving north
    	  			//int numCrossedN = 0; 
    	  			numCrossedN++;
              //increment the line counter in the nouthern direction
    	  			double expectedY = tile * (numCrossedN - 1) - sensorOffset;
              //calculate the expected y-value for each line crossed
    	  			odometer.setY(expectedY);
              //update y-value 
    	  			/*if(Math.abs(actualY - expectedY) > distanceOffset){
                  //the y error is above the threshold, correct the theta
    	  				double DthetaRad = Math.asin(Math.abs(actualX)/(actualY - sensorOffset));
    	  				double DthetaDeg = Math.toDegrees(DthetaRad);
    	  				if(actualX > MIN){
                      //the brick is leaning inwards
    	  					odometer.setTheta(theta + DthetaDeg);
    	  				} else {
                	  	//the brick is leaning outwards 
    	  					odometer.setTheta(theta + DthetaDeg);
    	  				}
                  
    	  			}*/
    	  		}
    	  	//the brick is moving east
    	  		if(Math.abs(theta - 90) < angularOffset){
              
    	  			//int numCrossedE = 0; 
    	  			numCrossedE++;
              //increment the line counter in the eastern direction
    	  			double expectedX = (numCrossedE - 1) * tile - sensorOffset;
              //calculate the expected x-valye for each line crossed
    	  			odometer.setX(expectedX);
    	  			/*if(Math.abs(actualX - expectedX) > distanceOffset) {
                  //calculate delta theta
    	  				double DthetaRad = Math.asin(Math.abs(actualY-MAX)/(actualX - sensorOffset));
    	  				double DthetaDeg = Math.toDegrees(DthetaRad);
    	  				if((actualY - MAX) > 0){
                      //the brick is leaning outwards
    	  					odometer.setTheta(theta - DthetaDeg);
    	  				} else {
                      //the brick is leaning inwards
    	  					odometer.setTheta(theta + DthetaDeg);
    	  				}
                  
              }*/
              
          }
    	  	//the brick is moving south
    	  		if(Math.abs(theta - 180) < angularOffset){
    	  			
    	  			
         
    	  			numCrossedS++;
    	  			//increment the line counter in the southern direction
    	  			double expectedY = MAX - (tile/2 + (numCrossedS - 1)* tile) + sensorOffset;
    	  			odometer.setY(expectedY);
    	  			//calculate the expected y-value for each line
    	  			/*if(Math.abs(actualY - expectedY) > distanceOffset) {
    	  				double DthetaRad = Math.asin((Math.abs(actualX - MAX))/(MAX - actualY + sensorOffset));
    	  				double DthetaDeg = Math.toDegrees(DthetaRad);
    	  				if((actualX - MAX) > 0){
    	  					//the brick is leaning outwards
    	  					odometer.setTheta(theta - DthetaDeg);
    	  				} else {
    	  					//the brick is leaning inwards
    	  					odometer.setTheta(theta + DthetaDeg);
    	  				}
             
    	  			}*/
    	  		}
     
    	  		//the brick is moving west
    	  		if(Math.abs(theta - 270) < angularOffset){
    	  			
    	  			//int numCrossedW = 0; 
    	  			numCrossedW++;
    	  			//increment the line counter in the western direction
    	  			double expectedX = MAX - (tile/2 + (numCrossedW - 1) * tile) + sensorOffset;
    	  			//calculate the expected x-value for each line
    	  			odometer.setX(expectedX);
    	  			/*if(Math.abs(expectedX-actualX) > distanceOffset){
    	  				double DthetaRad = Math.asin(Math.abs(actualY)/(MAX - actualX + sensorOffset));
    	  				double DthetaDeg = Math.toDegrees(DthetaRad);
    	  				if(actualY > MIN){
    	  					//the brick is leaning inwards
    	  					odometer.setTheta(theta + DthetaDeg);
    	  				} else {
    	  					//the brick is leaning outwards
    	  					odometer.setTheta(theta - DthetaDeg);
    	  				}
            
    	  			}*/
    	  		}
     
     
    	  

    	  
      }
      
      
   

      // this ensure the odometry correction occurs only once every period
      correctionEnd = System.currentTimeMillis();
      if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
        try {
          Thread.sleep(CORRECTION_PERIOD - (correctionEnd - correctionStart));
        } catch (InterruptedException e) {
          // there is nothing to be done here because it is not
          // expected that the odometry correction will be
          // interrupted by another thread
        }
      }
    }
  }
}








