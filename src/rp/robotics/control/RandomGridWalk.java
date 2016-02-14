package rp.robotics.control;

import java.util.Random;

import lejos.robotics.RangeFinder;
import rp.robotics.mapping.GridMap;
import rp.robotics.navigation.GridPose;
import rp.robotics.simulation.Drive;
import rp.robotics.simulation.MovableRobot;
import rp.robotics.simulation.Rotate;
import rp.systems.StoppableRunnable;
import rp.util.Rate;

public class RandomGridWalk implements StoppableRunnable {

	private final GridMap m_map;
	private final MovableRobot m_robot;
	private GridPose m_pose;
	private boolean m_running = true;
	private float m_turnSpeed = 30f;
	private float m_driveSpeed = 0.2f;
	private final RangeFinder m_ranger;

	public RandomGridWalk(MovableRobot _robot, GridMap _map, GridPose _start,
			RangeFinder _ranger) {
		m_map = _map;
		m_robot = _robot;
		m_pose = _start;
		m_ranger = _ranger;
	}

	private boolean enoughSpace() {
		return m_ranger.getRange() > m_map.getCellSize()
				+ m_robot.getRobotLength() / 2f;
	}

	private boolean moveAheadClear() {
		GridPose moved = m_pose.clone();
		moved.moveUpdate();
		return m_map.isValidTransition(m_pose.getX(), m_pose.getY(),
				moved.getX(), moved.getY())
				&& enoughSpace();
	}

	@Override
	public void run() {

		Random rand = new Random();
		Rate rate = new Rate(10);

		while (m_running) {

			int choice = rand.nextInt(3);

			int amount = 0;

			if (choice == 1) {
				amount = 90;
			} else if (choice == 2) {
				amount = -90;
			}

			if (amount != 0) {
				m_robot.getPilot().executeMove(new Rotate(m_turnSpeed, amount));
			}

			while (m_running && m_robot.getPilot().isMoving()) {
				rate.sleep();
			}

			m_pose.rotateUpdate(amount);

//			System.out.println(m_pose);

			// check on target before making move

			if (moveAheadClear()) {

				m_robot.getPilot().executeMove(
						new Drive(m_driveSpeed, m_map.getCellSize()));

				while (m_running && m_robot.getPilot().isMoving()) {
					rate.sleep();
				}

				m_pose.moveUpdate();
			}

			//			System.out.println(m_pose);

			// update continuous pose using grid
			m_robot.setPose(m_map.toPose(m_pose));
		}
	}

	@Override
	public void stop() {
		m_running = false;
	}

}
