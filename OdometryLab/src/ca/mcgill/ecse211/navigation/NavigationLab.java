package ca.mcgill.ecse211.navigation;


import ca.mcgill.ecse211.odometerlab.Odometer;
import ca.mcgill.ecse211.odometerlab.OdometryCorrection;
import ca.mcgill.ecse211.odometerlab.OdometryDisplay;
import ca.mcgill.ecse211.odometerlab.SquareDriver;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;

public class NavigationLab {

	private static final EV3LargeRegulatedMotor leftMotor =
		      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
		  
		  private static final EV3LargeRegulatedMotor rightMotor =
		      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

		  public static final double WHEEL_RADIUS = 2.2;
		  public static final double TRACK = 14.3;

		  public static void main(String[] args) {
		    int buttonChoice;

		    final TextLCD t = LocalEV3.get().getTextLCD();
		    Odometer odometer = new Odometer(leftMotor, rightMotor);
		    OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, t);
		    OdometryCorrection odometryCorrection = new OdometryCorrection(odometer);

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
		      
		      
		      
		      if(buttonChoice == Button.ID_RIGHT){
		        //obstackleAvoidance.start();
		        
		      }
		      
		      // spawn a new Thread to avoid SquareDriver.drive() from blocking
		      (new Thread() {
		        public void run() {
		          //navigation.drive(leftMotor, rightMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK);
		        }
		      }).start();
		    }

		    while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		    System.exit(0);
		  }
}
