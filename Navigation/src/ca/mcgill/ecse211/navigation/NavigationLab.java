package ca.mcgill.ecse211.navigation;


import ca.mcgill.ecse211.navigation.Odometer;
import ca.mcgill.ecse211.navigation.OdometryCorrection;
import ca.mcgill.ecse211.navigation.OdometryDisplay;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class NavigationLab {

		  public static final EV3LargeRegulatedMotor leftMotor =
		      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
		  
		  public static final EV3LargeRegulatedMotor rightMotor =
		      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
		  //the motor to rotate the sensor 
		  public static final EV3LargeRegulatedMotor sMotor = 
			  new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C")); 
		  
		  private static final Port usPort = LocalEV3.get().getPort("S1");

		  public static final double WHEEL_RADIUS = 2.2;
		  public static final double TRACK = 14.3;
		  private static final int bandCenter = 30; // Offset from the wall (cm)
		  private static final int bandWidth = 5; // Width of dead band (cm)

		  public static void main(String[] args) {
		    int buttonChoice;

		    final TextLCD t = LocalEV3.get().getTextLCD();
		    Odometer odometer = new Odometer(leftMotor, rightMotor);
		    OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, t);
		    final OdometryCorrection odometryCorrection = new OdometryCorrection(odometer);
		    final Navigation navigation = new Navigation(odometer); 
		    @SuppressWarnings("resource")
			SensorModes usSensor = new EV3UltrasonicSensor(usPort); // usSensor is the instance
		    SampleProvider usDistance = usSensor.getMode("Distance"); // usDistance provides samples from
		                                                              // this instance
		    float[] usData = new float[usDistance.sampleSize()]; 
		    
		    usController usController = new usController(bandCenter, bandWidth);

		    do {
		      // clear the display
		      t.clear();

		      // ask the user whether the motors should drive in a square or float
		      t.drawString("< Left | Right >", 0, 0);
		      t.drawString("       |        ", 0, 1);
		      t.drawString(" Float | Navi  ", 0, 2);
		      t.drawString("motors | gation   ", 0, 3);
		      t.drawString("       |       ", 0, 4);

		      buttonChoice = Button.waitForAnyPress();
		    } while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);

		    if (buttonChoice == Button.ID_LEFT) {

		      leftMotor.forward();
		      leftMotor.flt();
		      rightMotor.forward();
		      rightMotor.flt();

		      odometer.start();
		      odometryDisplay.start();

		    } else {
		      // clear the display
		      t.clear();

		      // ask the user whether the motors should drive in a square or float
		      t.drawString("< Left | Right >", 0, 0);
		      t.drawString("  No   | with   ", 0, 1);
		      t.drawString(" Avoid | Avoid  ", 0, 2);
		      t.drawString(" ance  | ance ", 0, 3);
		      t.drawString("       |        ", 0, 4);
		      
		      buttonChoice = Button.waitForAnyPress();
		      
		      odometer.start();
		      odometryDisplay.start();
		      odometryCorrection.start(); 
                (new Thread() {
                    public void run() {
                        navigation.drive();
                    }
                }).start();
            }
		      
		      
		      if(buttonChoice == Button.ID_RIGHT){
		    	  	 sensorPoller sensorpoller = new sensorPoller(usDistance, usData, usController); 
		    	  	 
		    	  	 
                  
                  while(navigation.isNavigating() == true){
                      if(sensorpoller.distance <= 30){
                          Thread.sleep(5000);
                          sensorpoller.start();
                          navigation.isNavigating = false;
                      }
                      
                  }
                  while(navigation.isNavigating() == false){
                      if(sensorpoller.distance > 50){
                          sensorpoller.exit();
                      }
                  }
		    	   	
		       
		       
		        
		      }
		      
		      // spawn a new Thread to avoid SquareDriver.drive() from blocking
		      

		    while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		    System.exit(0);
		  }
}
