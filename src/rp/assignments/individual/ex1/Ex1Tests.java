package rp.assignments.individual.ex1;

import java.util.ArrayList;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;

import org.junit.Test;

import rp.assignments.AbstractTestHarness;
import rp.robotics.testing.TargetZone;
import rp.robotics.testing.ZoneSequence;
import rp.robotics.testing.ZoneSequenceTest;
import rp.systems.StoppableRunnable;

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
	 * 1.0
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
	 * Example test for Individual Exercise 1b: a square with side length of 1.0
	 * 
	 * @return
	 */
	public static ZoneSequence getSqureTestSequence() {

		Pose start = new Pose(3.0f, 3.0f, 0.0f);
		ArrayList<TargetZone> zones = new ArrayList<TargetZone>(4);
		zones.add(new TargetZone(new Point(4.0f, 3.0f), 0.1f));
		zones.add(new TargetZone(new Point(4.0f, 4.0f), 0.1f));
		zones.add(new TargetZone(new Point(3.0f, 4.0f), 0.1f));
		zones.add(new TargetZone(new Point(3.0f, 3.0f), 0.1f));
		return new ZoneSequence(start, zones);
	}

	public ZoneSequenceTest createTriangeTest() {
		return createSequenceTest(getTriangleTestSequence(), 30000,
				"createEquilateralTriangleController", 1.0f);
	}

	public ZoneSequenceTest createSquareTest() {
		return createSequenceTest(getSqureTestSequence(), 40000,
				"createSquareController", 1.0f);
	}

	@Test
	public void triangleTest() {
		runSequenceTest(createTriangeTest());
	}

	@Test
	public void squareTest() {
		runSequenceTest(createSquareTest());
	}

}
