package ca.mcgill.ecse211.localization;


import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class LocalizationLab {


	  public static final EV3LargeRegulatedMotor leftMotor =
	      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	  
	  public static final EV3LargeRegulatedMotor rightMotor =
	      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	  
	  
	  private static final Port usPort = LocalEV3.get().getPort("S1");

	  public static final double WHEEL_RADIUS = 2.2;
	  public static final double TRACK = 14.3;
	  
	  
	 

	  public static void main(String[] args) {
	    int buttonChoice;

	    final TextLCD t = LocalEV3.get().getTextLCD();
	    Odometer odometer = new Odometer(leftMotor, rightMotor);
	    OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, t);
	    final Navigation navigation = new Navigation(odometer); 
	    @SuppressWarnings("resource")
		SensorModes usSensor = new EV3UltrasonicSensor(usPort); // usSensor is the instance
	    SampleProvider usDistance = usSensor.getMode("Distance"); // usDistance provides samples from
	                                                              // this instance
	    float[] usData = new float[usDistance.sampleSize()]; 
	    UltrasonicLocalizer uslocalizer = new UltrasonicLocalizer(odometer, false, usDistance, usData); 
	    UltrasonicLocalizer uslocalizerR = new UltrasonicLocalizer(odometer, true, usDistance, usData); 
	    
	     
	    

	    do {
	      // clear the display
	      t.clear();

	      // ask the user whether the motors should drive in a square or float
	      t.drawString("< Left | Right >", 0, 0);
	      t.drawString("       |        ", 0, 1);
	      t.drawString(" Ultra | light  ", 0, 2);
	      t.drawString(" sonic | sensor   ", 0, 3);
	      t.drawString("       |       ", 0, 4);

	      buttonChoice = Button.waitForAnyPress();
	    } while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);

	    if (buttonChoice == Button.ID_RIGHT) {


	      odometer.start();
	      odometryDisplay.start();

	    } else {
	      // clear the display
	      t.clear();

	      // ask the user whether the motors should drive in a square or float
	      t.drawString("< Left | Right >", 0, 0);
	      t.drawString(" risi  | fall   ", 0, 1);
	      t.drawString(" ng-ed | ing-ed  ", 0, 2);
	      t.drawString(" ge    | ge ", 0, 3);
	      t.drawString("       |        ", 0, 4);
	      
	      buttonChoice = Button.waitForAnyPress();
	      
	      odometer.start();
	      odometryDisplay.start();
	     
	      if(buttonChoice == Button.ID_RIGHT) {
	    	  	uslocalizer.localize();
	      }
	     
	      
	      if(buttonChoice == Button.ID_LEFT){
	    	  	 
	    	  	uslocalizerR.localize();
	    	  	
	    	   	
	       
	      }
	     
	    }

	    while (Button.waitForAnyPress() != Button.ID_ESCAPE);
	    System.exit(0);
	  }
}
