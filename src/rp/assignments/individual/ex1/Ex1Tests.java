package rp.assignments.individual.ex1;

import java.util.ArrayList;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;

import org.junit.Test;

import rp.assignments.AbstractTestHarness;
import rp.robotics.DifferentialDriveRobotPC;
import rp.robotics.testing.TargetZone;
import rp.robotics.testing.ZoneSequence;
import rp.robotics.testing.ZoneSequenceTest;

/**
 * 
 * Test instances for Individual Exercise 1
 * 
 * @author Nick Hawes
 *
 */

public class Ex1Tests extends AbstractTestHarness {

	private static final String TEST_CLASS = "rp.assignments.individual.ex1.SolutionFactory";

	public Ex1Tests() throws ClassNotFoundException {
		super(TEST_CLASS);
	}

	/**
	 * Example test for Individual Exercise 1a: a triangle with side length of
	 * 1.0m
	 * 
	 * @return
	 */
	public static ZoneSequence getTriangleTestSequence() {

		Pose start = new Pose(3f, 3f, 0f);

		ArrayList<TargetZone> zones = new ArrayList<TargetZone>();
		zones.add(new TargetZone(new Point(4.0f, 3.0f), 0.1f));
		zones.add(new TargetZone(new Point(3.5f, 3.87f), 0.1f));
		zones.add(new TargetZone(new Point(3.0f, 3.0f), 0.1f));

		return new ZoneSequence(start, zones);
	}

	/**
	 * Example test for Individual Exercise 1b: a square with side length of
	 * 1.0m
	 * 
	 * @return
	 */
	public static ZoneSequence getSquareTestSequence() {

		Pose start = new Pose(3.0f, 3.0f, 0.0f);
		ArrayList<TargetZone> zones = new ArrayList<TargetZone>(4);
		zones.add(new TargetZone(new Point(4.0f, 3.0f), 0.1f));
		zones.add(new TargetZone(new Point(4.0f, 4.0f), 0.1f));
		zones.add(new TargetZone(new Point(3.0f, 4.0f), 0.1f));
		zones.add(new TargetZone(new Point(3.0f, 3.0f), 0.1f));
		return new ZoneSequence(start, zones);
	}

	/**
	 * Example test for Individual Exercise 1c: a decahendron with side length
	 * of 0.2m
	 * 
	 * @return
	 */
	public static ZoneSequence getDecahendronSequence() {
		Pose start = new Pose(3.0f, 3.0f, 0.0f);
		ArrayList<TargetZone> zones = new ArrayList<TargetZone>(10);
		zones.add(new TargetZone(new Point(3.2f, 3.0f), 0.1f));
		zones.add(new TargetZone(new Point(3.3618035f, 3.117557f), 0.1f));
		zones.add(new TargetZone(new Point(3.4236069f, 3.3077683f), 0.1f));
		zones.add(new TargetZone(new Point(3.3618035f, 3.4979796f), 0.1f));
		zones.add(new TargetZone(new Point(3.2f, 3.6155367f), 0.1f));
		zones.add(new TargetZone(new Point(3.0f, 3.6155367f), 0.1f));
		zones.add(new TargetZone(new Point(2.8381965f, 3.4979796f), 0.1f));
		zones.add(new TargetZone(new Point(2.7763932f, 3.3077683f), 0.1f));
		zones.add(new TargetZone(new Point(2.8381965f, 3.117557f), 0.1f));
		zones.add(new TargetZone(new Point(3.0f, 3.0f), 0.1f));
		return new ZoneSequence(start, zones);
	}

	public ZoneSequenceTest<DifferentialDriveRobotPC> createTriangeTest() {
		return createSequenceTest(getTriangleTestSequence(), 30000,
				"createEquilateralTriangleController", 1.0f);
	}

	public ZoneSequenceTest<DifferentialDriveRobotPC> createSquareTest() {
		return createSequenceTest(getSquareTestSequence(), 40000,
				"createSquareController", 1.0f);
	}

	public ZoneSequenceTest<DifferentialDriveRobotPC> createDecahendroTest() {
		return createSequenceTest(getDecahendronSequence(), 50000,
				"createDecahedronController", 0.2f);
	}

	@Test
	public void triangleTest() {
		runSequenceTest(createTriangeTest());
	}

	@Test
	public void squareTest() {
		runSequenceTest(createSquareTest());
	}

	@Test
	public void decahedronTest() {
		runSequenceTest(createDecahendroTest());
	}

}
