package rp.assignments.individual.ex1;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import lejos.util.Delay;

import org.junit.Test;

import rp.assignments.AbstractTestHarness;
import rp.config.RangeFinderDescription;
import rp.robotics.EventBasedTouchSensor;
import rp.robotics.LocalisedRangeScanner;
import rp.robotics.MobileRobotWrapper;
import rp.robotics.TouchSensorListener;
import rp.robotics.testing.MockRangeFinder;
import rp.robotics.testing.TargetZone;
import rp.robotics.testing.TestMaps;
import rp.robotics.testing.TouchListenerTest;
import rp.robotics.testing.ZoneSequence;
import rp.robotics.testing.ZoneSequenceTestWithSim;

/**
 * 
 * Test instances for Individual Exercise 1
 * 
 * @author Nick Hawes
 *
 */

@SuppressWarnings("deprecation")
public class Ex1Tests extends AbstractTestHarness {

	private static final String TEST_CLASS = "rp.assignments.individual.ex1.SolutionFactory";

	public Ex1Tests() throws ClassNotFoundException {
		super(TEST_CLASS);
	}

	/**
	 * Example test for Individual Exercise 1a: a pentagon with side length of
	 * 0.5.
	 * 
	 * @return
	 */
	public static ZoneSequence getPentagonTestSequence() {
		Pose start = new Pose(3.0f, 3.0f, 0.0f);
		ArrayList<TargetZone> zones = new ArrayList<TargetZone>(5);
		zones.add(new TargetZone(new Point(3.5f, 3.0f), 0.35f));
		zones.add(new TargetZone(new Point(3.6545086f, 3.4755282f), 0.35f));
		zones.add(new TargetZone(new Point(3.25f, 3.7694209f), 0.35f));
		zones.add(new TargetZone(new Point(2.8454914f, 3.4755282f), 0.35f));
		zones.add(new TargetZone(new Point(3.0f, 3.0f), 0.35f));
		return new ZoneSequence(start, zones);

	}

	/**
	 * Example test for Individual Exercise 1b: an octagon with side length of
	 * 0.8m
	 * 
	 * @return
	 */
	public static ZoneSequence getOctagonTestSequence() {
		Pose start = new Pose(3.0f, 3.0f, 0.0f);
		ArrayList<TargetZone> zones = new ArrayList<TargetZone>(8);
		zones.add(new TargetZone(new Point(3.8f, 3.0f), 0.35f));
		zones.add(new TargetZone(new Point(4.3656855f, 3.5656855f), 0.35f));
		zones.add(new TargetZone(new Point(4.3656855f, 4.3656855f), 0.35f));
		zones.add(new TargetZone(new Point(3.8f, 4.9313707f), 0.35f));
		zones.add(new TargetZone(new Point(3.0f, 4.9313707f), 0.35f));
		zones.add(new TargetZone(new Point(2.4343145f, 4.3656855f), 0.35f));
		zones.add(new TargetZone(new Point(2.4343145f, 3.5656855f), 0.35f));
		zones.add(new TargetZone(new Point(3.0f, 3.0f), 0.35f));

		return new ZoneSequence(start, zones);
	}

	/**
	 * Example test for Individual Exercise 1c: a nonagon with side length of
	 * 0.9m
	 * 
	 * @return
	 */
	public static ZoneSequence getNonagonSequence() {
		Pose start = new Pose(3.0f, 3.0f, 0.0f);
		ArrayList<TargetZone> zones = new ArrayList<TargetZone>(9);
		zones.add(new TargetZone(new Point(3.9f, 3.0f), 0.35f));
		zones.add(new TargetZone(new Point(4.58944f, 3.5785089f), 0.35f));
		zones.add(new TargetZone(new Point(4.7457232f, 4.4648356f), 0.35f));
		zones.add(new TargetZone(new Point(4.2957234f, 5.2442584f), 0.35f));
		zones.add(new TargetZone(new Point(3.45f, 5.5520763f), 0.35f));
		zones.add(new TargetZone(new Point(2.6042767f, 5.2442584f), 0.35f));
		zones.add(new TargetZone(new Point(2.1542766f, 4.4648356f), 0.35f));
		zones.add(new TargetZone(new Point(2.31056f, 3.5785089f), 0.35f));
		zones.add(new TargetZone(new Point(3.0f, 3.0f), 0.35f));
		return new ZoneSequence(start, zones);

	}

	public static ZoneSequence getBumperSequence() {
		Pose start = new Pose(0.2f, 0.5f, 0.0f);
		ArrayList<TargetZone> zones = new ArrayList<TargetZone>(4);
		zones.add(new TargetZone(new Point(1.8000001f, 0.5f), 0.35f));
		zones.add(new TargetZone(new Point(0.20000005f, 0.5f), 0.35f));
		zones.add(new TargetZone(new Point(1.8000001f, 0.5f), 0.35f));
		zones.add(new TargetZone(new Point(0.20000005f, 0.5f), 0.35f));
		return new ZoneSequence(start, zones);
	}

	public ZoneSequenceTestWithSim<?> createPentagonTest() {
		return createSequenceTest(TestMaps.EMPTY_8_x_6,
				getPentagonTestSequence(), false, 30000,
				"createPentagonController", 0.5f);
	}

	public ZoneSequenceTestWithSim<?> createOctagonTest() {
		return createSequenceTest(TestMaps.EMPTY_8_x_6,
				getOctagonTestSequence(), false, 40000,
				"createOctagonController", 0.8f);
	}

	public ZoneSequenceTestWithSim<?> createNonagonTest() {
		return createSequenceTest(TestMaps.EMPTY_8_x_6, getNonagonSequence(),
				false, 50000, "createNonagonController", 0.9f);
	}

	public ZoneSequenceTestWithSim<?> createBumperTest() {

		try {
			// test with bumper controller, this doesn't include the touch
			// sensor.
			ZoneSequenceTestWithSim<?> test = createSequenceTest(
					TestMaps.EMPTY_2_x_1, getBumperSequence(), true, 70000,
					"createBumperController");

			// this adds the touch sensor for the simulator if the controller
			// accepts it
			MobileRobotWrapper robot = test.getSimulation().iterator().next();
			Object controller = test.getController();

			if (controller != null) {
				if (controller instanceof TouchSensorListener) {
					test.getSimulation().addTouchSensorListener(robot,
							(TouchSensorListener) controller);
				} else {
					fail("Controller does not implement TouchSensorListener");
				}
			}
			return test;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}

	}

	public ZoneSequenceTestWithSim<?> createVirtualBumperTest() {
		try {
			// test with bumper controller
			ZoneSequenceTestWithSim<?> test = createSequenceTest(
					TestMaps.EMPTY_2_x_1, getBumperSequence(), false, 50000,
					"createBumperController");

			MobileRobotWrapper robot = test.getSimulation().iterator().next();
			LocalisedRangeScanner ranger = test.getSimulation()
					.getRanger(robot);

			EventBasedTouchSensor sensor = getTouchSensor(
					"createVirtualBumper", ranger.getDescription(), ranger,
					0.2f);

			Object controller = test.getController();

			if (controller != null) {
				if (controller instanceof TouchSensorListener) {
					sensor.addTouchSensorListener((TouchSensorListener) controller);
				} else {
					fail("Controller does not implement TouchSensorListener");
				}
			}

			return test;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	@Test
	public void pentagonTest() {
		System.out.println("Running pentagon test");
		runSequenceTest(createPentagonTest());
	}

	@Test
	public void octagonTest() {
		System.out.println("Running octagon test");
		runSequenceTest(createOctagonTest());
	}

	@Test
	public void nonagonTest() {
		System.out.println("Running nonagon test");
		runSequenceTest(createNonagonTest());
	}

	@Test
	public void bumperTest() {
		System.out.println("Running bumper test");
		runSequenceTest(createBumperTest());
	}

	@Test
	public void virtualBumperTest() {
		System.out.println("Running virtual bumper robot test");
		runSequenceTest(createVirtualBumperTest());
	}

	@Test
	public void virtualBumperUnitTests() throws InterruptedException {
		System.out.println("Running virtual bumper unit tests");

		RangeFinderDescription description = new RangeFinderDescription(
				new Pose(0, 0, 0), 2.4f, 0.03f, 0.03f, 10f);
		float touchRange = 0.2f;

		testSensorWithDescription(description, touchRange);
		// System.out.println("Test done");

	}

	public void testSensorWithDescription(RangeFinderDescription description,
			float touchRange) throws InterruptedException {

		// This defines how long to wait after changing the value reported by
		// the ranger before querying the sensor under test.
		//

		long delayMs = (long) (1000 / description.getRate()) * 2;

		// This is a fake range finder device which allows us to control which
		// range values are received by the sensor under test
		MockRangeFinder ranger = new MockRangeFinder();

		// By called setRange we change the value received by the sensor
		ranger.setRange(description.getMaxRange());

		// This is a mock listener which we use to test the results of event
		// generation
		TouchListenerTest listener = new TouchListenerTest();

		// This is where we instantiate the sensor under test
		EventBasedTouchSensor sensor = getTouchSensor("createVirtualBumper",
				description, ranger, touchRange);

		assertTrue("Virtual bumper could not be created from SolutionFactory",
				sensor != null);

		// and register our mock listener with it
		sensor.addTouchSensorListener(listener);

		// This is how we test whether an event has been received
		assertTrue("No events should occur when readings at max range",
				listener.eventStatus(false, false, false));

		ranger.setRange(touchRange + description.getNoise() + 0.01f);
		ranger.waitForReading(delayMs);
		Delay.msDelay(delayMs);

		assertTrue("The readings should still be out of touch range",
				!sensor.isPressed());
		assertTrue(
				"The readings should still be out of touch range so no events should be received",
				listener.eventStatus(false, false, false));

		ranger.setRange(touchRange + description.getNoise()
				- (description.getNoise() / 2));
		listener.waitForEvent(delayMs);

		// wait for the range value to be updated

		assertTrue(
				"The reading is within the noise range of the touch range, so sensor is pressed",
				sensor.isPressed());
		assertTrue(
				"The reading is within the noise range of the touch range, so a pressed event should occcur",
				listener.eventStatus(true, false, false));

		listener.reset();

		ranger.setRange(touchRange - description.getNoise());
		ranger.waitForReading(delayMs);
		Delay.msDelay(delayMs);

		assertTrue("Within touch range so sensor should be pressed",
				sensor.isPressed());
		assertTrue("Further within touch range, no need for extra event",
				listener.eventStatus(false, false, false));

		listener.reset();

		ranger.setRange(touchRange + description.getNoise()
				+ (description.getNoise() * 2));

		listener.waitForEvent(delayMs, 2);

		assertTrue("Reading is out of touch range", !sensor.isPressed());
		assertTrue(
				"Moved out of touch range, so should get release and bumper events",
				listener.eventStatus(false, true, true));
	}

}
