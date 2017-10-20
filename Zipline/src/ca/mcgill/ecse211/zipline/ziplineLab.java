package ca.mcgill.ecse211.zipline;



import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class ziplineLab {
		  public static final EV3LargeRegulatedMotor leftMotor =
		      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
		  
		  public static final EV3LargeRegulatedMotor rightMotor =
		      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
		  public static final EV3LargeRegulatedMotor topMotor = 
			  new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D")); 
		  
		  
		  private static final Port usPort = LocalEV3.get().getPort("S1");
		  private static final Port lsPortL = LocalEV3.get().getPort("S3");
		  private static final Port lsPortR = LocalEV3.get().getPort("S4"); 
		  
		 
		  private static final double tile = 30.48; 
		  private static int X0; 
		  private static int Y0; 
		  private static int XC; 
		  private static int YC; 
		  
		  
		  public static void main(String[] args) {
			  	int buttonChoice;
			  	
			    final TextLCD t = LocalEV3.get().getTextLCD();
			    
			    Odometer odometer = new Odometer(leftMotor, rightMotor);
			    OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, t);
			    
			    @SuppressWarnings("resource")
				SensorModes usSensor = new EV3UltrasonicSensor(usPort); // usSensor is the instance
			    SampleProvider usDistance = usSensor.getMode("Distance"); // usDistance provides samples from
			                                                              // this instance
			    float[] usData = new float[usDistance.sampleSize()]; 
			    
			  
				/*********************************************/
			    //polling from the left light sensor 
				@SuppressWarnings("resource")
				SensorModes lsSensorL = new EV3ColorSensor(lsPortL); 
				//get an instance of a sample provider for each sensor
				SampleProvider lsSampleL = lsSensorL.getMode("Red"); 
				//allocate buffers for the sensor 
				float[] sampleColorL = new float[lsSampleL.sampleSize()];
				
				/********************************************/
				//polling from the right light sensor 
				SensorModes lsSensorR = new EV3ColorSensor(lsPortR); 
				//get an instance of a sample provider for each sensor
				SampleProvider lsSampleR = lsSensorR.getMode("Red"); 
				//allocate buffers for the sensor 
				float[] sampleColorR = new float[lsSampleR.sampleSize()];
				
				
				//navigator for the top motor
			    final navigation top_navigator = new navigation(odometer);
			    //navigator for the driving motors 
			    final navigation navigator = new navigation(odometer); 
			    //instance of ultrasonic localization 
			   //UltrasonicLocalizer uslocalizerR = new UltrasonicLocalizer(odometer, true, usDistance, usData);
			    //instance of light localization 
			   // LightLocalizer ltlocalizer = new LightLocalizer(odometer, navigation); 
			    
			     
			    

			    
			      // clear the display
			     /* t.clear(); 
			      // ask the user to input the value for x0 coordinate 
			      t.drawString("enter the x0 value now", 0, 0);
			      buttonChoice = Button.waitForAnyPress();
			      //initial x = 0; 
			      int x = 0; 
			      //max x = 8
			     
			    	  while(buttonChoice != Button.ID_RIGHT)	{
			    		buttonChoice = Button.waitForAnyPress(); 
			    	  	//increment x0 by pushing the up button 
			    	  	if (buttonChoice == Button.ID_UP && x <= 8) {
			    	  		x++; 
			    	  		t.drawInt(x, 0, 1); 
			    	  	}
			    	  	//decrement x0 by pushing the down button 
			    	  	if(buttonChoice == Button.ID_DOWN && x >= 1) {
			    	  		x--; 
			    	  		t.drawInt(x, 0, 1); 
			    	  	}
			    	  } 
			      
			      
			      confirm the value by pushing the right button 
			       * and clear the screen for the next entry 
			       
			      if(buttonChoice == Button.ID_RIGHT) {
			    	  		X0 = x; 
			    	  		t.clear(); 
			      }
			      
			    
			      //ask the user to enter the value of y0 coodinates 
			      t.drawString("enter the y0 value now", 0, 0);
			      buttonChoice = Button.waitForAnyPress();
			      int y = 0; 
			      //max y = 8
			      while(buttonChoice != Button.ID_RIGHT) {
			    	  	buttonChoice = Button.waitForAnyPress(); 
			    	  	//increment y0 by pushing the up button 
			    	  	if(buttonChoice == Button.ID_UP) {
			    		  y++;
			    		  t.drawInt(y, 0, 1); 
			    	  	} 
			    	  	//decrement y0 by pushing the down button 
			    	  	if(buttonChoice == Button.ID_DOWN) {
			    	  	  y--; 
			    	  	  t.drawInt(y, 0, 1); 
			    	  	}
			      }
			      confirm the entry by pushing the right button 
			       * and clear the screen for the next entry 
			       
			      if(buttonChoice == Button.ID_RIGHT) {
		    	  		Y0 = y; 
		    	  		t.clear(); 
			      }
			      
			      
			      //ask the user to enter the value of Xc
			      t.drawString("enter the Xc value now", 0, 0);
			      buttonChoice = Button.waitForAnyPress(); 
			      int Xc = 0; 
			      //max Xc = 8; 
			      while(buttonChoice != Button.ID_RIGHT) {
			    	  	buttonChoice = Button.waitForAnyPress(); 
			    	  	//increment Xc by pushing the up button 
			    	  	if(buttonChoice == Button.ID_UP) {
			    	  		Xc++; 
			    	  		t.drawInt(Xc, 0, 1);
			    	  	}
			    	  	//decrement Xc by pushing the down button 
			    	  	if(buttonChoice == Button.ID_DOWN) {
			    	  		Xc--; 
			    	  		t.drawInt(Xc, 0, 1); 
			    	  	}
			      }
			      confirm the entry by pushing the right button 
			       * and clear the screen for the next entry 
			       
			      if(buttonChoice == Button.ID_RIGHT) {
		    	  		XC = Xc; 
		    	  		t.clear(); 
			      }
			     
			      //ask the user to enter the value of Yc
			      t.drawString("enter the Yc value now ", 0, 0);
			      buttonChoice = Button.waitForAnyPress(); 
			      int Yc = 0; 
			      while(buttonChoice != Button.ID_RIGHT) {
			    	  	buttonChoice = Button.waitForAnyPress(); 
			    	  	//increment Yc by pushing the up button 
			    	  	if(buttonChoice == Button.ID_UP) {
			    	  		Yc++; 
			    	  		t.drawInt(Yc, 0, 1); 
			    	  	}
			    	  	//decrement Yc by pushing the down button 
			    	  	if(buttonChoice == Button.ID_DOWN) {
			    	  		Yc--; 
			    	  		t.drawInt(Yc, 0, 1); 
			    	  	}
			      }
			      confirm the entry by pushing the right button 
			       * and clear the screen for the next entry 
			       
			      if(buttonChoice == Button.ID_RIGHT) {
		    	  		YC = Yc; 
		    	  		t.clear(); 
			      }*/
			      
	
			      //clear the display for the odometry info 
			      t.clear(); 
			      odometer.start(); 
			      odometryDisplay.start(); 
			      //first perform ultrasonic localization 
			      /*start ultrasonic localization here*/
			      
			      //then perform light localization 
			      /*start light localization here*/
			      
			      //travel to x,y, calling navigation and passing x, y with correction 
			      /*start correction here*/
			      //top motor rotates 
			      (new Thread() {
			    	  		public void run() {
			    	  			top_navigator.rotating();
			    	  		}
			      }).start(); 
			      
			      /*run navigation in a separate thread to ensure concurrency 
			       * navigate to X0, Y0
			       */
			      (new Thread() {
				        public void run() {
				          navigator.drive(1, 6); 
				        }
				      }).start();
			      
			      //stop the robot (do nothing since all threads are finished) at X0, Y0
			      
			      
			      //pause until a button is pressed 
			      Button.waitForAnyPress(); 
			      //navigate to Xc, Yc and mount onto the zipline 
			      navigator.drive(2, 6);
			      //travesing the zipline 
			      double endZip = 6; 
			      double rollDist = endZip - (odometer.getX()/tile); 
			      navigator.roll(rollDist);
			      
			      //relocaliza after the travesal 
			      //push the excape button to exit the system 
			      while (Button.waitForAnyPress() == Button.ID_ESCAPE);
				    System.exit(0);
			     
		  }
		  
		  
}
