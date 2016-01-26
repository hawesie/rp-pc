package rp.robotics.simulation;

import rp.robotics.DifferentialDriveRobotPC;

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
	public void touchSensorPressed(DifferentialDriveRobotPC _robot,
			long _responseTime);

	public void controllerStopped(DifferentialDriveRobotPC _robot,
			long _responseTime);

}
