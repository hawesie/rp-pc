package rp.sim;

import static org.junit.Assert.*;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;

import org.junit.Test;

public class DifferentialPilotTest {

	@Test
	public void testForward() {

		DifferentialPilot dp = new DifferentialPilot(56, 163,
				new SimulatedMotor(), new SimulatedMotor());

		double speedMmPerSec = 50;
		dp.setTravelSpeed(speedMmPerSec);
		double distanceMm = 500;
		long startTimeMillis = System.currentTimeMillis();
		dp.travel(distanceMm);
		double durationSecs = (System.currentTimeMillis() - startTimeMillis) / 1000.0;
		double expectedDurationSecs = distanceMm / speedMmPerSec;

		assertTrue("Travel was too slow: " + durationSecs + " vs expected "
				+ expectedDurationSecs,
				durationSecs < expectedDurationSecs * 1.05);
		assertTrue("Travel was too quick: " + durationSecs + " vs expected "
				+ expectedDurationSecs,
				durationSecs > expectedDurationSecs * 0.95);
	}

	@Test
	public void testBackward() {

		DifferentialPilot dp = new DifferentialPilot(56, 163,
				new SimulatedMotor(), new SimulatedMotor());

		double speedMmPerSec = 50;
		dp.setTravelSpeed(speedMmPerSec);
		double distanceMm = 500;
		long startTimeMillis = System.currentTimeMillis();
		dp.travel(-distanceMm);
		double durationSecs = (System.currentTimeMillis() - startTimeMillis) / 1000.0;
		double expectedDurationSecs = distanceMm / speedMmPerSec;

		assertTrue("Travel was too slow: " + durationSecs + " vs expected "
				+ expectedDurationSecs,
				durationSecs < expectedDurationSecs * 1.05);
		assertTrue("Travel was too quick: " + durationSecs + " vs expected "
				+ expectedDurationSecs,
				durationSecs > expectedDurationSecs * 0.95);
	}

	@Test
	public void testOdom() {

		DifferentialPilot dp = new DifferentialPilot(56, 163,
				new SimulatedMotor(), new SimulatedMotor());

		OdometryPoseProvider pp = new OdometryPoseProvider(dp);

		double distanceMm = 500;

		dp.travel(distanceMm);

		Pose pose = pp.getPose();

		assertTrue(pose.getX() > distanceMm * 0.99);
		assertTrue(pose.getX() < distanceMm * 1.01);
		assertTrue(pose.getY() > distanceMm * -0.01);
		assertTrue(pose.getY() < distanceMm * 0.01);
		assertTrue(pose.getHeading() > -1);
		assertTrue(pose.getHeading() < 1);

		float turn = 90;
		dp.rotate(turn);
		pose = pp.getPose();
		
		assertTrue(pose.getX() > distanceMm * 0.99);
		assertTrue(pose.getX() < distanceMm * 1.01);
		assertTrue(pose.getY() > distanceMm * -0.01);
		assertTrue(pose.getY() < distanceMm * 0.01);
		assertTrue(pose.getHeading() > turn * 0.98);
		assertTrue(pose.getHeading() < turn * 1.02);

		dp.travel(distanceMm);
		pose = pp.getPose();
		
		assertTrue(pose.getX() > distanceMm * 0.99);
		assertTrue(pose.getX() < distanceMm * 1.01);
		assertTrue(pose.getY() > distanceMm * 0.99);
		assertTrue(pose.getY() < distanceMm * 1.01);
		assertTrue(pose.getHeading() > turn * 0.98);
		assertTrue(pose.getHeading() < turn * 1.02);

		
	}

	
	@Test
	public void testOdomReverse() {

		DifferentialPilot dp = new DifferentialPilot(56, 163,
				new SimulatedMotor(), new SimulatedMotor());

		OdometryPoseProvider pp = new OdometryPoseProvider(dp);

		double distanceMm = 500;

		dp.travel(distanceMm);

		Pose pose = pp.getPose();

		assertTrue(pose.getX() > distanceMm * 0.99);
		assertTrue(pose.getX() < distanceMm * 1.01);
		assertTrue(pose.getY() > -distanceMm * -0.01);
		assertTrue(pose.getY() < -distanceMm * 0.01);
		assertTrue(pose.getHeading() > -1);
		assertTrue(pose.getHeading() < 1);

		float turn = -90;
		dp.rotate(turn);
		pose = pp.getPose();
		
		assertTrue(pose.getX() > distanceMm * 0.99);
		assertTrue(pose.getX() < distanceMm * 1.01);
		assertTrue(pose.getY() > -distanceMm * -0.01);
		assertTrue(pose.getY() < -distanceMm * 0.01);
		assertTrue(pose.getHeading() > turn * 0.98);
		assertTrue(pose.getHeading() < turn * 1.02);

		dp.travel(distanceMm);
		pose = pp.getPose();
		
		assertTrue(pose.getX() > distanceMm * 0.99);
		assertTrue(pose.getX() < distanceMm * 1.01);
		assertTrue(pose.getY() > -distanceMm * 0.99);
		assertTrue(pose.getY() < -distanceMm * 1.01);
		assertTrue(pose.getHeading() > turn * 0.98);
		assertTrue(pose.getHeading() < turn * 1.02);

		
	}

}
