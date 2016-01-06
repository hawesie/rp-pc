package rp.robotics.simulation;

import java.util.ArrayList;
import java.util.Iterator;

import lejos.geom.Line;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;
import rp.config.WheeledRobotConfiguration;
import rp.geom.GeometryUtils;
import rp.robotics.DifferentialDriveRobotPC;
import rp.robotics.mapping.RPLineMap;
import rp.robotics.visualisation.MapVisualisationComponent;
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
public class MapBasedSimulation implements StoppableRunnable,
		Iterable<DifferentialDriveRobotPC> {

	protected final RPLineMap m_map;
	protected final ArrayList<DifferentialDriveRobotPC> m_robots = new ArrayList<DifferentialDriveRobotPC>();
	private float m_simulationRateHz = 30;
	private boolean m_running = false;
	private Thread m_simThread;
	private Line[] m_footprint = new Line[4];

	public MapBasedSimulation(RPLineMap _map) {
		m_map = _map;
	}

	@Override
	public void run() {
		Rate r = new Rate(m_simulationRateHz);
		m_running = true;
		while (m_running) {

			synchronized (m_robots) {
				for (DifferentialDriveRobotPC robot : m_robots) {
					if (isInCollision(robot)) {
						robot.startCollision();
					}
				}
			}

			r.sleep();
		}
	}

	private boolean isInCollision(DifferentialDriveRobotPC _robot) {
		// transform robot footprint to it's pose location
		GeometryUtils.transform(_robot.getPose(), _robot.getFootprint(),
				m_footprint);
		// check for footprint intersection with map
		return m_map.intersectsWith(m_footprint);
	}

	@Override
	public void stop() {
		m_running = false;
	}

	/**
	 * Add a robot to the simulation with the given configuration at the given
	 * pose.
	 * 
	 * @param _config
	 */
	public DifferentialDriveRobotPC addRobot(WheeledRobotConfiguration _config,
			Pose _start) {

		DifferentialDriveRobotPC robot = new DifferentialDriveRobotPC(_config);
		robot.setPose(_start);
		synchronized (m_robots) {
			if (_config != null && !m_robots.contains(_config)) {
				m_robots.add(robot);
			}
		}

		// if the first robot was added, the start sim running
		if (m_robots.size() == 1) {
			m_simThread = new Thread(this);
			m_simThread.start();
		}

		return robot;
	}

	/**
	 * Creates a visualisation for the given simulation.
	 * 
	 * @param _sim
	 * @return
	 */
	public static MapVisualisationComponent createVisulation(
			MapBasedSimulation _sim) {
		MapVisualisationComponent visualisation = MapVisualisationComponent
				.createVisualisation(_sim.m_map);
		for (DifferentialDriveRobotPC robot : _sim.m_robots) {
			visualisation.addRobot(robot);
		}
		return visualisation;
	}

	public LineMap getMap() {
		return m_map;
	}

	@Override
	public Iterator<DifferentialDriveRobotPC> iterator() {
		return m_robots.iterator();
	}

}
