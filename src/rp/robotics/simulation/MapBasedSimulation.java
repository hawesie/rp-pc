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
import rp.config.MobileRobotConfiguration;
import rp.config.RangeFinderDescription;
import rp.config.RangeScannerDescription;
import rp.config.WheeledRobotConfiguration;
import rp.geom.GeometryUtils;
import rp.robotics.DifferentialDriveRobot;
import rp.robotics.LocalisedRangeScanner;
import rp.robotics.MobileRobot;
import rp.robotics.MobileRobotWrapper;
import rp.robotics.TouchSensorEvent;
import rp.robotics.TouchSensorListener;
import rp.robotics.mapping.LineMap;
import rp.util.SensorUtils;

/**
 * This combines a simulated robot with a map to create a simple simulation of
 * the robot in a 2D world. This class provides collision detection between a
 * single robot and the map and also supports limited sensing of the world.
 * 
 * 
 * @author Nick Hawes
 *
 */
@SuppressWarnings("rawtypes")
public class MapBasedSimulation implements
		Iterable<MobileRobotWrapper<? extends MobileRobot>> {

	protected final LineMap m_map;
	protected final ArrayList<MobileRobotWrapper<? extends MobileRobot>> m_robots = new ArrayList<>();

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
		final MobileRobot robot;

		public FootprintTouchPair(MobileRobot _robot, Line[] _footprint,
				PoseProvider _poser, TouchSensorListener _listener) {
			robot = _robot;
			footprint = _footprint;
			listener = _listener;
			poser = _poser;
		}
	}

	private class RelativeRangeScanner implements LocalisedRangeScanner {
		final PoseProvider poser;
		final RangeScannerDescription scannerDesc;
		final MobileRobotWrapper<?> robotWrapper;

		public RelativeRangeScanner(PoseProvider _poser,
				RangeScannerDescription _desc, MobileRobotWrapper<?> _wrapper) {
			poser = _poser;
			scannerDesc = _desc;
			robotWrapper = _wrapper;
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

			RangeReadings obstacleReadings = takeReadingsToNonMapThings(
					poser.getPose(), scannerDesc, robotWrapper);

			RangeReadings mapReadings = m_map.takeReadings(poser.getPose(),
					scannerDesc);

			return SensorUtils.getMinimumValues(obstacleReadings, mapReadings);
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
							for (MobileRobotWrapper robot : m_robots) {

								if (isInCollision(robot.getRobot())) {
									// System.out.println("In collision");
									robot.getRobot().startCollision();
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
	 * @param _robotWrapper
	 * @return the range or -1 if not in range
	 */
	private float rangeToObstacle(Pose pose, MobileRobotWrapper<?> _robotWrapper) {

		float largestDimension = Math.max(m_map.getBoundingRect().width,
				m_map.getBoundingRect().width);
		Line l = new Line(pose.getX(), pose.getY(), pose.getX()
				+ largestDimension
				* (float) Math.cos(Math.toRadians(pose.getHeading())),
				pose.getY() + largestDimension
						* (float) Math.sin(Math.toRadians(pose.getHeading())));
		Line rl = null;

		if (m_obstacles != null) {

			for (DynamicObstacle obstacle : m_obstacles) {

				Line[] footprint = new Line[obstacle.getFootprint().length];

				// transform footprint to it's pose location
				GeometryUtils.transform(obstacle.getPose(),
						obstacle.getFootprint(), footprint);

				for (int i = 0; i < footprint.length; i++) {

					Line target = footprint[i];

					Point p = LineMap.intersectsAt(target, l);

					if (p == null) {
						continue;
					}

					Line tl = new Line(pose.getX(), pose.getY(), p.x, p.y);

					// If the range line intersects more than one map line
					// then take the shortest distance.
					if (rl == null || tl.length() < rl.length()) {
						rl = tl;
					}
				}

			}
		}

//		System.out.println("to obstacle: " + rl);

		for (MobileRobotWrapper<? extends MobileRobot> wrapper : m_robots) {

			if (!wrapper.equals(_robotWrapper)) {

				Line[] footprint = new Line[wrapper.getRobot().getFootprint().length];

				// transform footprint to it's pose location
				GeometryUtils.transform(wrapper.getRobot().getPose(), wrapper
						.getRobot().getFootprint(), footprint);

				for (int i = 0; i < footprint.length; i++) {

					Line target = footprint[i];

					Point p = LineMap.intersectsAt(target, l);

					if (p == null) {
						continue;
					}

					Line tl = new Line(pose.getX(), pose.getY(), p.x, p.y);

					// If the range line intersects more than one map line
					// then take the shortest distance.
					if (rl == null || tl.length() < rl.length()) {
						rl = tl;
					}
				}

			}

		}

		return (rl == null ? RangeFinderDescription.OUT_OF_RANGE_VALUE : rl
				.length());

	}

	/**
	 * Obtain range readings from obstacles at the predefined angles relative to
	 * the robot
	 * 
	 * @param _robotWrapper
	 */
	private RangeReadings takeReadingsToNonMapThings(Pose _robotPose,
			RangeScannerDescription _ranger, MobileRobotWrapper<?> _robotWrapper) {

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
			float obsRange = rangeToObstacle(readingPose, _robotWrapper);

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

	private void callListenersSensorPressed(MobileRobot _robot,
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

	private boolean isInCollision(MobileRobot _robot) {
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
	public MobileRobotWrapper<DifferentialDriveRobot> addRobot(
			WheeledRobotConfiguration _config, Pose _start) {
		DifferentialDriveRobot robot = new DifferentialDriveRobot(_config);
		return addRobot(robot, _start);
	}

	/**
	 * Add a robot to the simulation with the given configuration at the given
	 * pose.
	 * 
	 * @param _config
	 */
	public MobileRobotWrapper<MovableRobot> addRobot(
			MobileRobotConfiguration _config, Pose _start) {
		MovableRobot robot = new MovableRobot(_config, new MovablePilot(_start));
		return addRobot(robot, _start);
	}

	// public MobileRobotWrapper<DifferentialDriveRobot> addRobot(
	// MobileRobotConfiguration _config, Pose _start) {
	// MobileRobot robot = new MobileRobot(_config);
	// return addRobot(robot, _start);
	// }

	private <R extends MobileRobot> MobileRobotWrapper<R> addRobot(R _robot,
			Pose _start) {

		MobileRobotWrapper<R> wrapper = new MobileRobotWrapper<>(_robot);
		_robot.setPose(_start);
		synchronized (m_robots) {
			m_robots.add(wrapper);
		}

		// if the first robot was added, the start sim running
		if (m_robots.size() == 1) {
			start();
		}

		return wrapper;
	}

	public void addTouchSensorListener(MobileRobotWrapper<?> _robot,
			TouchSensorListener _listener, int _sensorIndex) {
		for (MobileRobotWrapper<?> robot : m_robots) {
			if (robot.equals(_robot)) {

				if (robot.getRobot().getTouchSensors() == null) {
					throw new NullPointerException(
							"Robot has no touch sensors in description");
				}

				if (_sensorIndex >= robot.getRobot().getTouchSensors().size()) {
					throw new IndexOutOfBoundsException(
							"Sensor index is out of bounds");
				} else {
					if (m_touchSensors == null) {
						m_touchSensors = new ArrayList<MapBasedSimulation.FootprintTouchPair>(
								1);
					}
					synchronized (m_touchSensors) {
						m_touchSensors
								.add(new FootprintTouchPair(robot.getRobot(),
										robot.getRobot().getTouchSensors()
												.get(_sensorIndex), robot
												.getRobot(), _listener));
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
	public void addTouchSensorListener(MobileRobotWrapper<?> _robot,
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

	public LocalisedRangeScanner getRanger(MobileRobotWrapper<?> _robot) {
		return getRanger(_robot, 0);
	}

	public LocalisedRangeScanner getRanger(MobileRobotWrapper<?> _robot,
			int _sensorIndex) {
		for (MobileRobotWrapper<?> wrapper : m_robots) {

			if (wrapper.equals(_robot)) {

				MobileRobot robot = wrapper.getRobot();

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
							robot, robot.getRangeScanners().get(_sensorIndex),
							wrapper);
					m_rangers.add(ranger);
					return ranger;

				}

			}
		}
		throw new IllegalArgumentException("Robot is not part of simulation");
	}

	public LineMap getMap() {
		return m_map;
	}

	@Override
	public Iterator<MobileRobotWrapper<? extends MobileRobot>> iterator() {
		return m_robots.iterator();
	}

	public ArrayList<MobileRobotWrapper<? extends MobileRobot>> getRobots() {
		return m_robots;
	}

	public ArrayList<DynamicObstacle> getObstacles() {
		return m_obstacles;
	}

	public ArrayList<RelativeRangeScanner> getRangers() {
		return m_rangers;
	}

}
