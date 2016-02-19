package rp.robotics.simulation;

import static org.junit.Assert.assertTrue;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Pose;

import org.junit.Test;

public class DifferentialPilotTest {

	@Test
	public void testForward() {
		SimulationCore sim = SimulationCore.createSimulationCore();
		DifferentialPilot dp = new DifferentialPilot(56, 163,
				new SimulatedMotor(sim), new SimulatedMotor(sim));

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
		SimulationCore sim = SimulationCore.createSimulationCore();

		DifferentialPilot dp = new DifferentialPilot(56, 163,
				new SimulatedMotor(sim), new SimulatedMotor(sim));

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
		SimulationCore sim = SimulationCore.createSimulationCore();

		DifferentialPilot dp = new DifferentialPilot(56, 163,
				new SimulatedMotor(sim), new SimulatedMotor(sim));

		OdometryPoseProvider pp = new OdometryPoseProvider(dp);

		double distanceMm = 500;

		dp.travel(distanceMm);

		Pose pose = pp.getPose();

		double lowerBound = 0.98;
		double upperBound = 1.02;

		assertTrue(pose.getX() > distanceMm * lowerBound);
		assertTrue(pose.getX() < distanceMm * upperBound);
		assertTrue(pose.getY() > distanceMm * -0.01);
		assertTrue(pose.getY() < distanceMm * 0.01);
		assertTrue(pose.getHeading() > -1);
		assertTrue(pose.getHeading() < 1);

		float turn = 90;
		dp.rotate(turn);
		pose = pp.getPose();

		assertTrue(pose.getX() > distanceMm * lowerBound);
		assertTrue(pose.getX() < distanceMm * upperBound);
		assertTrue(pose.getY() > distanceMm * -0.01);
		assertTrue(pose.getY() < distanceMm * 0.01);
		assertTrue(pose.getHeading() > turn * lowerBound);
		assertTrue(pose.getHeading() < turn * upperBound);

		dp.travel(distanceMm);
		pose = pp.getPose();

		assertTrue(pose.getX() > distanceMm * lowerBound);
		assertTrue(pose.getX() < distanceMm * upperBound);
		assertTrue(pose.getY() > distanceMm * lowerBound);
		assertTrue(pose.getY() < distanceMm * upperBound);
		assertTrue(pose.getHeading() > turn * lowerBound);
		assertTrue(pose.getHeading() < turn * upperBound);

	}

	@Test
	public void testOdomReverse() {
		SimulationCore sim = SimulationCore.createSimulationCore();

		DifferentialPilot dp = new DifferentialPilot(56, 163,
				new SimulatedMotor(sim), new SimulatedMotor(sim));

		OdometryPoseProvider pp = new OdometryPoseProvider(dp);

		double distanceMm = 500;

		dp.travel(distanceMm);

		Pose pose = pp.getPose();

		double lowerBound = 0.98;
		double upperBound = 1.02;

		assertTrue(pose.getX() > distanceMm * lowerBound);

		assertTrue(pose.getX() < distanceMm * upperBound);
		assertTrue(pose.getY() > -0.02);
		assertTrue(pose.getY() < 0.02);
		assertTrue(pose.getHeading() > -1);
		assertTrue(pose.getHeading() < 1);

		float turn = -90;
		dp.rotate(turn);
		pose = pp.getPose();

		assertTrue(pose.getX() > distanceMm * lowerBound);
		assertTrue(pose.getX() < distanceMm * upperBound);
		assertTrue(pose.getY() > -0.02);
		assertTrue(pose.getY() < 0.02);
		assertTrue(pose.getHeading() > turn * upperBound);
		assertTrue(pose.getHeading() < turn * lowerBound);

		dp.travel(distanceMm);
		pose = pp.getPose();

		assertTrue(pose.getX() > distanceMm * lowerBound);
		assertTrue(pose.getX() < distanceMm * upperBound);
		assertTrue(pose.getY() > -distanceMm * upperBound);
		assertTrue(pose.getY() < -distanceMm * lowerBound);
		assertTrue(pose.getHeading() > turn * upperBound);
		assertTrue(pose.getHeading() < turn * lowerBound);

	}

}
