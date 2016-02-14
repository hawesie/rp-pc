package rp.assignments.individual.ex2;

import java.time.Duration;

import lejos.geom.Line;
import lejos.robotics.navigation.Pose;

import org.junit.Test;

import rp.assignments.AbstractTestHarness;
import rp.config.RangeScannerDescription;
import rp.robotics.DifferentialDriveRobot;
import rp.robotics.LocalisedRangeScanner;
import rp.robotics.MobileRobotWrapper;
import rp.robotics.mapping.MapUtils;
import rp.robotics.simulation.Drive;
import rp.robotics.simulation.MapBasedSimulation;
import rp.robotics.simulation.SimulatedRobots;
import rp.robotics.simulation.SimulatorListener;
import rp.robotics.simulation.TranslationObstacle;
import rp.robotics.testing.RangeLimitTest;
import rp.systems.StoppableRunnable;

/**
 * 
 * Test instances for Individual Exercise 2
 * 
 * @author Nick Hawes
 *
 */

public class Ex2Tests extends AbstractTestHarness {

	private static final String TEST_CLASS = "rp.assignments.individual.ex2.SolutionFactory";

	public Ex2Tests() throws ClassNotFoundException {
		super(TEST_CLASS);
	}

	/**
	 * Create a test in which the robot must stay within a given range of a
	 * moving obstacle. Everything moves in the postive x direction.
	 * 
	 * @param _robotStartX
	 *            The x position the robot will start at
	 * @param _obstacleStartX
	 *            The x position the robot will start at *
	 * @param _obstacleSpeed
	 *            The speed at which the robot will move
	 * @param _limit
	 *            The distance (in metres) within which the robot must stay of
	 *            the obstacle
	 * @param _allowableOutsideLimit
	 *            How long can the robot stay outside of the range without the
	 *            test failing.
	 * @param _startupTime
	 *            How long until the test starts checking ranges *
	 * @return The test.
	 */
	public <C extends StoppableRunnable> RangeLimitTest<?> createRangeLimitTest(
			float _robotStartX, float _obstacleStartX, float _obstacleSpeed,
			float _limit, Duration _allowableOutsideLimit, Duration _startupTime) {
		return createRangeLimitTest(_robotStartX, _obstacleStartX,
				_obstacleSpeed, _limit, _allowableOutsideLimit, _startupTime,
				null);
	}

	/**
	 * Create a test in which the robot must stay within a given range of a
	 * moving obstacle. Everything moves in the postive x direction.
	 * 
	 * @param _robotStartX
	 *            The x position the robot will start at
	 * @param _obstacleStartX
	 *            The x position the robot will start at *
	 * @param _obstacleSpeed
	 *            The speed at which the robot will move
	 * @param _limit
	 *            The distance (in metres) within which the robot must stay of
	 *            the obstacle
	 * @param _allowableOutsideLimit
	 *            How long can the robot stay outside of the range without the
	 *            test failing.
	 * @param _startupTime
	 *            How long until the test starts checking ranges *
	 * @param _listener
	 *            A listener to pass on to the underlying simulation.
	 * @return The test.
	 */
	public <C extends StoppableRunnable> RangeLimitTest<?> createRangeLimitTest(
			float _robotStartX, float _obstacleStartX, float _obstacleSpeed,
			float _limit, Duration _allowableOutsideLimit,
			Duration _startupTime, SimulatorListener _listener) {
		try {

			float mapWidth = 16f;

			MapBasedSimulation sim = new MapBasedSimulation(
					MapUtils.createRectangularMap(mapWidth, 1));

			float moveDistance = mapWidth - _obstacleStartX;
			double testMaxDurationSecs = (moveDistance / _obstacleSpeed) / 2;
			Duration timeout = Duration
					.ofMillis((long) (1000d * testMaxDurationSecs));

			sim.addObstacle(new TranslationObstacle(new Line[] { new Line(0,
					0.5f, 0, -0.5f) }, new Drive(new Pose(
					_obstacleStartX, 0.5f, 0f), _obstacleSpeed, 13f)));

			Pose start = new Pose(_robotStartX, 0.5f, 0f);

			MobileRobotWrapper<DifferentialDriveRobot> wrapper = sim.addRobot(
					SimulatedRobots.makeWheeledConfiguration(false, true), start);
			LocalisedRangeScanner ranger = sim.getRanger(wrapper);
			RangeScannerDescription desc = wrapper.getRobot()
					.getRangeScanners().get(0);

			Object[] args = new Object[] { wrapper.getRobot(), desc, ranger,
					new Float(_limit) };

			C controller = getTestObject("createRangeController",
					StoppableRunnable.class, args);

			RangeLimitTest<C> test = new RangeLimitTest<C>(sim, ranger, _limit,
					controller, wrapper.getRobot(), timeout,
					_allowableOutsideLimit, _startupTime);

			if (_listener != null) {
				sim.addSimulatorListener(_listener);
				test.addSimulatorListener(_listener);
			}

			return test;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	public RangeLimitTest<?> createFastTest() {
		return createRangeLimitTest(2f, 3f, 0.25f, 0.5f, Duration.ofSeconds(2),
				Duration.ofSeconds(5));
	}

	public RangeLimitTest<?> createMediumTest() {
		return createRangeLimitTest(2f, 3f, 0.15f, 0.5f, Duration.ofSeconds(2),
				Duration.ofSeconds(5));
	}

	public RangeLimitTest<?> createSlowTest() {
		return createRangeLimitTest(1.5f, 3f, 0.08f, 0.5f,
				Duration.ofSeconds(2), Duration.ofSeconds(5));
	}

	@Test
	public void slowTest() {
		System.out.println("Running slow test");
		runTest(createSlowTest());
	}

	@Test
	public void mediumTest() {
		System.out.println("Running medium test");
		runTest(createMediumTest());
	}

	@Test
	public void fastTest() {
		System.out.println("Running fast test");
		runTest(createFastTest());
	}

}
