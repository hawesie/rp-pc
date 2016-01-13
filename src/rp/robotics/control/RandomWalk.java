package rp.robotics.control;

import lejos.robotics.RangeFinder;
import lejos.robotics.navigation.DifferentialPilot;
import rp.robotics.DifferentialDriveRobot;
import rp.robotics.TouchSensorEvent;
import rp.robotics.TouchSensorListener;
import rp.robotics.simulation.Rate;
import rp.systems.StoppableRunnable;

/**
 * A controller that randomly moves the robot around with some care for its
 * safety. This is not the best solution to this problem. Can you make something
 * more responsive and/or elegant?
 * 
 * @author Nick Hawes
 *
 */
public class RandomWalk implements StoppableRunnable, TouchSensorListener {

	private final DifferentialDriveRobot m_robot;
	private boolean m_running = false;
	private final DifferentialPilot m_pilot;
	private boolean m_bumped = false;
	private RangeFinder m_ranger;

	public RandomWalk(DifferentialDriveRobot _robot) {
		m_robot = _robot;
		m_pilot = m_robot.getDifferentialPilot();
	}

	@Override
	public void run() {
		m_running = true;
		float moveMin = 0.2f, moveMax = 1.0f;
		float turnMin = 20, turnMax = 135f;
		float moveDiff = moveMax - moveMin;
		float turnDiff = turnMax - turnMin;

		m_pilot.setTravelSpeed(0.10f);
		m_pilot.setRotateSpeed(20);

		Rate r;
		while (m_running) {
			float move = (float) (moveMin + (Math.random() * moveDiff));
			m_pilot.travel(move, true);

			r = new Rate(40);
			while (m_running && m_pilot.isMoving() && !m_bumped) {
				if (m_ranger != null) {
					if (m_ranger.getRange() < m_robot.getRobotLength()) {
						System.out.println("Watch out of that wall!");
					}

				}
				r.sleep();

			}

			if (m_bumped) {
				m_pilot.stop();
				m_pilot.travel(-move / 2);

				m_bumped = false;
			}

			if (m_running) {
				m_pilot.rotate(turnMin + (Math.random() * turnDiff));
			}
		}
	}

	@Override
	public void stop() {
		m_running = false;
	}

	@Override
	public void sensorPressed(TouchSensorEvent _e) {
		m_bumped = true;
	}

	@Override
	public void sensorReleased(TouchSensorEvent _e) {
		// Doesn't work in simulation
	}

	@Override
	public void sensorBumoed(TouchSensorEvent _e) {
		// Doesn't work in simulation

	}

	public void setRangeScanner(RangeFinder _ranger) {
		m_ranger = _ranger;
	}

}
