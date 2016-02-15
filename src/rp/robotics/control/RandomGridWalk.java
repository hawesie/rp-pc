package rp.robotics.control;

import java.util.Random;

import lejos.robotics.RangeFinder;
import rp.robotics.mapping.GridMap;
import rp.robotics.navigation.GridPilot;
import rp.robotics.navigation.GridPose;
import rp.robotics.simulation.MovableRobot;
import rp.systems.StoppableRunnable;

/**
 * Performs a random walk on a grid map, avoid things ahead of it within limits.
 * 
 * @author nah
 *
 */
public class RandomGridWalk implements StoppableRunnable {

	private final GridMap m_map;
	private final GridPilot m_pilot;

	private boolean m_running = true;
	private final RangeFinder m_ranger;
	private final MovableRobot m_robot;

	public RandomGridWalk(MovableRobot _robot, GridMap _map, GridPose _start,
			RangeFinder _ranger) {
		m_map = _map;
		m_pilot = new GridPilot(_robot.getPilot(), _map, _start);
		m_ranger = _ranger;
		m_robot = _robot;
	}

	/**
	 * Is there enough space in front of the robot to move into?
	 * 
	 * @return
	 */
	private boolean enoughSpace() {
		return m_ranger.getRange() > m_map.getCellSize()
				+ m_robot.getRobotLength() / 2f;
	}

	/**
	 * Is the grid junction ahead really a junction and clear of obstructions?
	 * 
	 * @return
	 */
	private boolean moveAheadClear() {
		GridPose current = m_pilot.getGridPose();
		GridPose moved = current.clone();
		moved.moveUpdate();
		return m_map.isValidTransition(current.getPosition(),
				moved.getPosition())
				&& enoughSpace();
	}

	@Override
	public void run() {

		Random rand = new Random();

		while (m_running) {

			int choice = rand.nextInt(3);

			// choice of direction. choice == 0 is straight ahead
			if (choice == 1) {
				m_pilot.rotatePositive();
			} else if (choice == 2) {
				m_pilot.rotateNegative();
			}

			// check on target before making move
			if (moveAheadClear()) {
				m_pilot.moveForward();
			}

		}
	}

	@Override
	public void stop() {
		m_running = false;
	}

}
