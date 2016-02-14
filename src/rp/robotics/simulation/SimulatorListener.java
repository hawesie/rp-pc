package rp.robotics.simulation;

import rp.robotics.MobileRobot;

/**
 * 
 * A listener which can receive events from the simulator.
 * 
 * @author Nick Hawes
 *
 */
public interface SimulatorListener {

	/**
	 * Called when the touch sensor in the simulator is pressed.
	 * 
	 * @param _responseTime
	 */
	public void touchSensorPressed(MobileRobot _robot,
			long _responseTime);

	public void controllerStopped(MobileRobot _robot,
			long _responseTime);

}
