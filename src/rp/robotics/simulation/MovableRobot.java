package rp.robotics.simulation;

import lejos.robotics.navigation.Pose;
import rp.config.MobileRobotConfiguration;
import rp.robotics.MobileRobot;

/**
 * A robot that can be controlled by the use of {@link Movable} objects. This
 * robot does not use simulated motors, but can still collide, uses sensors etc.
 * 
 * @author Nick Hawes
 *
 */
public class MovableRobot extends MobileRobot {

	private final MovablePilot m_pilot;

	public MovableRobot(MobileRobotConfiguration _config, MovablePilot _pilot) {
		super(_config, _pilot);
		m_pilot = _pilot;
	}

	public MovablePilot getPilot() {
		return m_pilot;
	}

	@Override
	public boolean isMoving() {
		return m_pilot.isMoving();
	}

	@Override
	public void setPose(Pose _pose) {
	
		m_pilot.setPose(_pose);
	}
	
}
