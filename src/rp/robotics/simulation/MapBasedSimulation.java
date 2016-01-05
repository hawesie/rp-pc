package rp.robotics.simulation;

import java.util.ArrayList;

import lejos.robotics.mapping.LineMap;
import rp.config.WheeledRobotConfiguration;
import rp.systems.StoppableRunnable;

/**
 * This combines a simulated robot with a map to create a simple simulation of
 * the robot in a 2D world. This class provides collision detection between a
 * single robot and the map and also supports limited sensing of the world.
 * 
 * 
 * @author Nick Hawes
 *
 */
public class MapBasedSimulation implements StoppableRunnable {

	private final LineMap m_map;
	private final ArrayList<DifferentialDriveRobot> m_robots = new ArrayList<DifferentialDriveRobot>();
	private float m_simulationRateHz = 30;
	private boolean m_running = false;

	public MapBasedSimulation(LineMap _map) {
		m_map = _map;
	}

	@Override
	public void run() {
		Rate r = new Rate(m_simulationRateHz);
		m_running = true;
		while (m_running) {

			synchronized (m_robots) {
				for (DifferentialDriveRobot robot : m_robots) {

				}

			}

			r.sleep();
		}
	}

	@Override
	public void stop() {
		m_running = false;
	}

	/**
	 * Add a robot to the simulation.
	 * 
	 * @param _robot
	 */
	public void addRobot(WheeledRobotConfiguration _robot) {
		
		synchronized (m_robots) {
			if (_robot != null && !m_robots.contains(_robot)) {
				m_robots.add(new DifferentialDriveRobot(_robot));
			}
		}
	}

}
