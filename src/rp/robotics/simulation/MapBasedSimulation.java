package rp.robotics.simulation;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.robotics.RangeFinder;
import lejos.robotics.RangeReadings;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import rp.config.RangeFinderDescription;
import rp.config.RangeScannerDescription;
import rp.config.WheeledRobotConfiguration;
import rp.geom.GeometryUtils;
import rp.robotics.DifferentialDriveRobotPC;
import rp.robotics.LocalisedRangeScanner;
import rp.robotics.TouchSensorEvent;
import rp.robotics.TouchSensorListener;
import rp.robotics.mapping.LineMap;
import rp.robotics.visualisation.MapVisualisationComponent;

/**
 * This combines a simulated robot with a map to create a simple simulation of
 * the robot in a 2D world. This class provides collision detection between a
 * single robot and the map and also supports limited sensing of the world.
 * 
 * 
 * @author Nick Hawes
 *
 */
public class MapBasedSimulation implements Iterable<DifferentialDriveRobotPC> {

	protected final LineMap m_map;
	protected final ArrayList<DifferentialDriveRobotPC> m_robots = new ArrayList<DifferentialDriveRobotPC>();

	private boolean m_running = false;

	private ArrayList<FootprintTouchPair> m_touchSensors;
	private ArrayList<SimulatorListener> m_simulatorListeners;
	private ArrayList<DynamicObstacle> m_obstacles;
	private ArrayList<RelativeRangeScanner> m_rangers;

	private class FootprintTouchPair {
		final PoseProvider poser;
		final Line[] footprint;
		final TouchSensorListener listener;
		boolean triggered = false;
		final DifferentialDriveRobotPC robot;

		public FootprintTouchPair(DifferentialDriveRobotPC _robot,
				Line[] _footprint, PoseProvider _poser,
				TouchSensorListener _listener) {
			robot = _robot;
			footprint = _footprint;
			listener = _listener;
			poser = _poser;
		}
	}

	private class RelativeRangeScanner implements LocalisedRangeScanner {
		final PoseProvider poser;
		final RangeScannerDescription scannerDesc;

		public RelativeRangeScanner(PoseProvider _poser,
				RangeScannerDescription _desc) {
			poser = _poser;
			scannerDesc = _desc;
		}

		@Override
		public RangeScannerDescription getDescription() {
			return scannerDesc;
		}

		/**
		 * Gets the absolute pose of the sensor.
		 */
		@Override
		public Pose getPose() {
			return GeometryUtils.transform(poser.getPose(),
					scannerDesc.getScannerPose());
		}

		@Override
		public void setPose(Pose _aPose) {
			throw new RuntimeException(
					"setPose is unimplemented for this ranger");
		}

		@Override
		public RangeReadings getRangeValues() {

			RangeReadings obstacleReadings = takeReadingsToObstacle(
					poser.getPose(), scannerDesc);
			RangeReadings mapReadings = m_map.takeReadings(poser.getPose(),
					scannerDesc);

			RangeReadings jointReadings = new RangeReadings(
					obstacleReadings.getNumReadings());

			for (int i = 0; i < obstacleReadings.getNumReadings(); i++) {
				float map = mapReadings.getRange(i);
				float obs = obstacleReadings.getRange(i);

				// System.out.println("map: " + mapReadings.getRange(i));
				// System.out.println("obs: " + obstacleReadings.getRange(i));

				if (RangeScannerDescription.isValidReading(map)
						&& RangeScannerDescription.isValidReading(obs)) {

					// System.out.println("AAA");
					jointReadings.setRange(i, mapReadings.getAngle(i),
							Math.min(map, obs));
				} else if (!RangeScannerDescription.isValidReading(map)) {
					// System.out.println("BBB");
					jointReadings.setRange(i, mapReadings.getAngle(i), obs);
				} else {
					// System.out.println("CCC");
					jointReadings.setRange(i, mapReadings.getAngle(i), map);
				}

				// System.out.println("chosen: " + jointReadings.getRange(i));
			}
			return jointReadings;
		}

		@Override
		public void setAngles(float[] _angles) {
			throw new RuntimeException(
					"setAngles is unimplemented for this ranger");
		}

		@Override
		public RangeFinder getRangeFinder() {
			return null;
		}

		@Override
		public float getRange() {
			return getRangeValues().get(0).getRange();
		}

		@Override
		public float[] getRanges() {
			return new float[] { getRange() };
		}

	}

	public MapBasedSimulation(LineMap _map) {
		m_map = _map;
	}

	public void addSimulatorListener(SimulatorListener _listener) {
		if (m_simulatorListeners == null) {
			m_simulatorListeners = new ArrayList<SimulatorListener>();
		}
		m_simulatorListeners.add(_listener);
	}

	private void start() {

		m_running = true;
		SimulationCore.getSimulationCore().addSteppable(
				new SimulationSteppable() {

					@Override
					public void step(Instant _now, Duration _stepInterval) {

						if (m_touchSensors != null) {
							synchronized (m_touchSensors) {
								for (FootprintTouchPair sensor : m_touchSensors) {
									if (isInCollision(sensor.poser.getPose(),
											sensor.footprint)) {
										if (!sensor.triggered) {
											sensor.triggered = true;

											long start = System
													.currentTimeMillis();
											sensor.listener
													.sensorPressed(new TouchSensorEvent(
															100, 3));

											long responseTime = System
													.currentTimeMillis()
													- start;
											callListenersSensorPressed(
													sensor.robot, responseTime);
										}
									} else if (sensor.triggered) {
										sensor.triggered = false;
									}
								}
							}
						}

						synchronized (m_robots) {
							for (DifferentialDriveRobotPC robot : m_robots) {

								if (isInCollision(robot)) {
									// System.out.println("In collision");
									robot.startCollision();
								}
							}
						}
					}

					@Override
					public boolean remove() {
						return !m_running;
					}
				});

	}

	/**
	 * Calculate the range from the robot to the nearest dynamic obstacle.
	 * Copied from RPLineMap.
	 * 
	 * @param pose
	 *            the pose of the robot
	 * @return the range or -1 if not in range
	 */
	private float rangeToObstacle(Pose pose) {

		float largestDimension = Math.max(m_map.getBoundingRect().width,
				m_map.getBoundingRect().width);
		Line l = new Line(pose.getX(), pose.getY(), pose.getX()
				+ largestDimension
				* (float) Math.cos(Math.toRadians(pose.getHeading())),
				pose.getY() + largestDimension
						* (float) Math.sin(Math.toRadians(pose.getHeading())));
		Line rl = null;
		// System.out.println("ray: " + l.x1 + " " + l.y1 + ", " + l.x2 + " "
		// + l.y2);

		if (m_obstacles != null) {

			for (DynamicObstacle obstacle : m_obstacles) {

				Line[] footprint = new Line[obstacle.getFootprint().length];

				// transform footprint to it's pose location
				GeometryUtils.transform(obstacle.getPose(),
						obstacle.getFootprint(), footprint);

				for (int i = 0; i < footprint.length; i++) {

					Line target = footprint[i];

					// System.out.println("target: " + target.x1 + " " +
					// target.y1
					// + ", " + target.x2 + " " + target.y2);

					Point p = LineMap.intersectsAt(target, l);

					// System.out.println("p: " + p);

					if (p == null) {
						// Does not intersect
						// System.out.println(i + " checking against: " +
						// lines[i].x1
						// + " " + lines[i].y1 + ", " + lines[i].x2 + " "
						// + lines[i].y2);
						//
						// System.out.println("does not intersect");
						continue;
					}

					Line tl = new Line(pose.getX(), pose.getY(), p.x, p.y);

					// System.out.println(i + " checking against: " +
					// lines[i].x1 +
					// " "
					// + lines[i].y1 + ", " + lines[i].x2 + " " + lines[i].y2);
					//
					// System.out.println("does intersect: " + tl.length());

					// If the range line intersects more than one map line
					// then take the shortest distance.
					if (rl == null || tl.length() < rl.length()) {
						rl = tl;
					}
				}

			}
			return (rl == null ? -1 : rl.length());
		} else {
			return RangeScannerDescription.OUT_OF_RANGE_VALUE;
		}
	}

	/**
	 * Obtain range readings from obstacles at the predefined angles relative to
	 * the robot
	 */
	private RangeReadings takeReadingsToObstacle(Pose _robotPose,
			RangeScannerDescription _ranger) {

		float[] readingAngles = _ranger.getReadingAngles();

		RangeReadings readings = new RangeReadings(readingAngles.length);

		// the pose to use for taking range readings
		Pose readingPose = GeometryUtils.transform(_robotPose,
				_ranger.getScannerPose());

		float readingPoseHeading = readingPose.getHeading();

		for (int i = 0; i < readingAngles.length; i++) {

			// rotate the reading pose to the angle of the sensor
			readingPose.setHeading(readingPoseHeading + readingAngles[i]);

			// and take a reading from there
			float obsRange = rangeToObstacle(readingPose);

			// System.out.println("obsRange: " + obsRange);
			// System.out.println(_ranger.getMaxRange());
			// System.out.println(_ranger.getMinRange());

			// bound reading to configured parameters
			if (obsRange > _ranger.getMaxRange()) {
				obsRange = RangeFinderDescription.OUT_OF_RANGE_VALUE;
			} else if (obsRange < _ranger.getMinRange()) {
				obsRange = 0;
			}

			readings.setRange(i, readingAngles[i], obsRange);

			// System.out.println(mapRange);

		}

		return readings;

	}

	private void callListenersSensorPressed(DifferentialDriveRobotPC _robot,
			long _responseTime) {
		if (m_simulatorListeners != null) {
			synchronized (m_simulatorListeners) {
				for (SimulatorListener listener : m_simulatorListeners) {
					listener.touchSensorPressed(_robot, _responseTime);
				}
			}
		}
	}

	private boolean isInCollision(Pose _pose, Line[] _footprint) {
		Line[] m_footprint = new Line[_footprint.length];
		// transform robot footprint to it's pose location
		GeometryUtils.transform(_pose, _footprint, m_footprint);
		// check for footprint intersection with map
		return m_map.intersectsWith(m_footprint);

	}

	private boolean isInCollision(DifferentialDriveRobotPC _robot) {
		Line[] m_footprint = new Line[_robot.getFootprint().length];
		// transform robot footprint to it's pose location
		GeometryUtils.transform(_robot.getPose(), _robot.getFootprint(),
				m_footprint);
		// check for footprint intersection with map
		return m_map.intersectsWith(m_footprint);

	}

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
			start();
		}

		return robot;
	}

	public void addTouchSensorListener(DifferentialDriveRobotPC _robot,
			TouchSensorListener _listener, int _sensorIndex) {
		for (DifferentialDriveRobotPC robot : m_robots) {
			if (robot.equals(_robot)) {

				if (robot.getTouchSensors() == null) {
					throw new NullPointerException(
							"Robot has no touch sensors in description");
				}

				if (_sensorIndex >= robot.getTouchSensors().size()) {
					throw new IndexOutOfBoundsException(
							"Sensor index is out of bounds");
				} else {
					if (m_touchSensors == null) {
						m_touchSensors = new ArrayList<MapBasedSimulation.FootprintTouchPair>(
								1);
					}
					synchronized (m_touchSensors) {
						m_touchSensors.add(new FootprintTouchPair(robot, robot
								.getTouchSensors().get(_sensorIndex), robot,
								_listener));
					}
					return;
				}

			}
		}
		throw new IllegalArgumentException("Robot is not part of simulation");
	}

	/*
	 * Add a touch sensor listener to the first touch sensor on the robot.
	 */
	public void addTouchSensorListener(DifferentialDriveRobotPC _robot,
			TouchSensorListener _listener) {
		addTouchSensorListener(_robot, _listener, 0);
	}

	public void addObstacle(DynamicObstacle _obstacle) {
		if (m_obstacles == null) {
			m_obstacles = new ArrayList<>();

		}

		m_obstacles.add(_obstacle);
		SimulationCore.getSimulationCore().addSteppable(_obstacle);
	}

	public LocalisedRangeScanner getRanger(DifferentialDriveRobotPC _robot) {
		return getRanger(_robot, 0);
	}

	public LocalisedRangeScanner getRanger(DifferentialDriveRobotPC _robot,
			int _sensorIndex) {
		for (DifferentialDriveRobotPC robot : m_robots) {
			if (robot.equals(_robot)) {

				if (robot.getRangeScanners() == null) {
					throw new NullPointerException(
							"Robot has no range scanners in description");
				}
				if (_sensorIndex >= robot.getRangeScanners().size()) {
					throw new IndexOutOfBoundsException(
							"Sensor index is out of bounds");
				} else {
					if (m_rangers == null) {
						m_rangers = new ArrayList<RelativeRangeScanner>(1);
					}

					RelativeRangeScanner ranger = new RelativeRangeScanner(
							robot, robot.getRangeScanners().get(_sensorIndex));
					m_rangers.add(ranger);
					return ranger;

				}

			}
		}
		throw new IllegalArgumentException("Robot is not part of simulation");
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

		if (_sim.m_obstacles != null) {
			for (DynamicObstacle obstacle : _sim.m_obstacles) {
				visualisation.addObstacle(obstacle);
			}
		}

		if (_sim.m_rangers != null) {
			for (RelativeRangeScanner ranger : _sim.m_rangers) {
				visualisation.addRangeScanner(ranger);
			}
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
