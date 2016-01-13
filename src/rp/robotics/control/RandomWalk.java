package rp.robotics.control;

import lejos.robotics.navigation.DifferentialPilot;
import rp.robotics.DifferentialDriveRobot;
import rp.systems.StoppableRunnable;

/**
 * A controller that randomly moves the robot around with no care for its
 * safety.
 * 
 * @author Nick Hawes
 *
 */
public class RandomWalk implements StoppableRunnable {

	private final DifferentialDriveRobot m_robot;
	private boolean m_running = false;

	public RandomWalk(DifferentialDriveRobot _robot) {
		m_robot = _robot;
	}

	@Override
	public void run() {
		m_running = true;
		DifferentialPilot pilot = m_robot.getDifferentialPilot();
		float moveMin = 0.2f, moveMax = 1.0f;
		float turnMin = 20, turnMax = 135f;
		float moveDiff = moveMax - moveMin;
		float turnDiff = turnMax - turnMin;
		while (m_running) {
			pilot.travel(moveMin + (Math.random() * moveDiff));
			if (m_running) {
				pilot.rotate(turnMin + (Math.random() * turnDiff));
			}
		}
	}

	@Override
	public void stop() {
		m_running = false;
	}

}
