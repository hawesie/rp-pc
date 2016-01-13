package rp.robotics.control;

import lejos.robotics.navigation.DifferentialPilot;
import rp.robotics.DifferentialDriveRobot;
import rp.systems.StoppableRunnable;

/**
 * A controller that moves forwards then backwards
 * 
 * @author Nick Hawes
 *
 */
public class ForwardBackwards implements StoppableRunnable {

	private boolean m_running = false;
	private final DifferentialPilot m_pilot;

	public ForwardBackwards(DifferentialDriveRobot _robot) {
		m_pilot = _robot.getDifferentialPilot();
	}

	@Override
	public void run() {
		m_running = true;
		float move = 0.4f;
		while (m_running) {

			m_pilot.travel(move);
			if (m_running) {
				m_pilot.travel(-move);
			}

		}
	}

	@Override
	public void stop() {
		m_running = false;
	}

}
