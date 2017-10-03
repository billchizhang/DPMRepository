package ca.mcgill.ecse211.navigation;

import lejos.robotics.SampleProvider;

public class sensorPoller extends Thread {
	
	  private SampleProvider us;
	  private usController cont;
	  private float[] usData;
	  public int distance; 

	  public sensorPoller(SampleProvider us, float[] usData, usController cont) {
	    this.us = us;
	    this.cont = cont;
	    this.usData = usData;
	  }

	  /*
	   * Sensors now return floats using a uniform protocol. Need to convert US result to an integer
	   * [0,255] (non-Javadoc)
	   * 
	   * @see java.lang.Thread#run()
	   */
	  public void run() {
	    
	    while (true) {
	      us.fetchSample(usData, 0); // acquire data
	      distance = (int) (usData[0] * 100.0); // extract from buffer, cast to int
	      cont.processUSData((int)(distance/Math.sqrt(2))); // now take action depending on value
	      try {
	        Thread.sleep(50);
	      } catch (Exception e) {
	      } // Poor man's timed sampling
	    }
	  }
	  
	  public void exit() {
		  System.exit(0);
	  }

}
