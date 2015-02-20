package rp.robotics.localisation;

import lejos.robotics.RangeReadings;

/**
 * An example of an interface a sensor model might provide.
 * 
 * Note: you do not have to use this if you don't want to.
 * 
 * @author Nick Hawes
 * 
 */
public interface SensorModel {

	/***
	 * Update the given distribution
	 * 
	 * @param _dist
	 *            The input distribution
	 * @param _readings
	 *            The range readings to use for the update
	 * @return
	 */
	public GridPositionDistribution updateAfterSensing(
			GridPositionDistribution _dist, RangeReadings _readings);

}
