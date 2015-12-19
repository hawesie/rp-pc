package rp.sim;

import static org.junit.Assert.assertTrue;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;
import lejos.util.Delay;

import org.junit.Test;

public class SimulatedMotorTest {

	@Test
	public void testForward() {

		RegulatedMotor motor = new SimulatedMotor();

		int targetSpeed = 360;
		int acceleration = 6000;

		long startCount = motor.getTachoCount();

		motor.setSpeed(targetSpeed);
		motor.setAcceleration(acceleration);

		// start moving forward
		motor.forward();

		int delaySecs = 5;
		Delay.msDelay(delaySecs * 1000);

		// the motor should be going as fast as acceleration allows, up to
		// targetSpeed
		double estimatedSpeed = Math.min(acceleration * delaySecs, targetSpeed);
		int currentSpeed = motor.getRotationSpeed();

		assertTrue("Speed has exceeded target speed",
				currentSpeed <= targetSpeed);
		assertTrue("Speed is not fast enough",
				currentSpeed > estimatedSpeed * 0.95);
		assertTrue("Speed is too fast", currentSpeed < estimatedSpeed * 1.05);

		long estimatedTachoCountIncrease = Math.round(Math.floor(estimatedSpeed
				* delaySecs));
		long tachoCountIncrease = motor.getTachoCount() - startCount;

		assertTrue("Count is not high enough " + tachoCountIncrease
				+ "vs estimate " + estimatedTachoCountIncrease,
				tachoCountIncrease > estimatedTachoCountIncrease * 0.9);
		assertTrue("Count is too high " + tachoCountIncrease + "vs estimate "
				+ estimatedTachoCountIncrease,
				tachoCountIncrease < estimatedTachoCountIncrease * 1.1);
	}

	@Test
	public void testBackwards() {

		RegulatedMotor motor = new SimulatedMotor();

		int targetSpeed = 360;
		int acceleration = 6000;

		long startCount = motor.getTachoCount();

		motor.setSpeed(targetSpeed);
		motor.setAcceleration(acceleration);

		// start moving backward
		motor.backward();

		int delaySecs = 5;
		Delay.msDelay(delaySecs * 1000);

		// the motor should be going as fast as acceleration allows, up to
		// targetSpeed
		double estimatedSpeed = Math.min(acceleration * delaySecs, targetSpeed);
		int currentSpeed = motor.getRotationSpeed();

		assertTrue("Speed has exceeded target speed",
				currentSpeed <= targetSpeed * 1.05);
		assertTrue("Speed is not fast enough",
				currentSpeed > estimatedSpeed * 0.95);
		assertTrue("Speed is too fast", currentSpeed < estimatedSpeed * 1.05);

		long estimatedTachoCountIncrease = -Math.round(Math
				.floor(estimatedSpeed * delaySecs));
		long tachoCountIncrease = motor.getTachoCount() - startCount;

		assertTrue("Count is not high enough " + tachoCountIncrease
				+ "vs estimate " + estimatedTachoCountIncrease,
				tachoCountIncrease > estimatedTachoCountIncrease * 1.1);
		assertTrue("Count is too high " + tachoCountIncrease + "vs estimate "
				+ estimatedTachoCountIncrease,
				tachoCountIncrease < estimatedTachoCountIncrease * 0.9);
	}

	@Test
	public void testForwardBackwards() {

		RegulatedMotor motor = new SimulatedMotor();

		motor.forward();
		Delay.msDelay(1000);
		motor.stop(false);

		int targetSpeed = 360;
		int acceleration = 6000;

		long startCount = motor.getTachoCount();

		motor.setSpeed(targetSpeed);
		motor.setAcceleration(acceleration);

		// start moving backward
		motor.backward();

		int delaySecs = 5;
		Delay.msDelay(delaySecs * 1000);

		// the motor should be going as fast as acceleration allows, up to
		// targetSpeed
		double estimatedSpeed = Math.min(acceleration * delaySecs, targetSpeed);
		int currentSpeed = motor.getRotationSpeed();

		assertTrue("Speed has exceeded target speed",
				currentSpeed < targetSpeed * 1.05);
		assertTrue("Speed is not fast enough",
				currentSpeed > estimatedSpeed * 0.95);
		assertTrue("Speed is too fast", currentSpeed < estimatedSpeed * 1.05);

		long estimatedTachoCountIncrease = -Math.round(Math
				.floor(estimatedSpeed * delaySecs));
		long tachoCountIncrease = motor.getTachoCount() - startCount;

		assertTrue("Count is not high enough " + tachoCountIncrease
				+ "vs estimate " + estimatedTachoCountIncrease,
				tachoCountIncrease > estimatedTachoCountIncrease * 0.9);
		assertTrue("Count is too high " + tachoCountIncrease + "vs estimate "
				+ estimatedTachoCountIncrease,
				tachoCountIncrease < estimatedTachoCountIncrease * 1.1);

	}

	@Test
	public void testRotateTo() {
		RegulatedMotor motor = new SimulatedMotor();
		int[] targets = { 0, 361, -33, 400, 404, -27, -666, 1024 };
		for (int target : targets) {
			motor.rotateTo(target, false);
			assertTrue("Rotation for " + target + " did not go far enough: "
					+ motor.getTachoCount(),
					motor.getTachoCount() >= target - 2);
			assertTrue(
					"Rotation for " + target + "went too far: "
							+ motor.getTachoCount(),
					motor.getTachoCount() <= target + 2);
		}
	}

	@Test
	public void testRotate() {
		RegulatedMotor motor = new SimulatedMotor();
		int[] increments = { -13, 0, 300, 201, -20, -489, 1 };
		for (int increment : increments) {
			int target = motor.getTachoCount() + increment;
			motor.rotate(increment, false);
			assertTrue("Rotation for " + target + " did not go far enough: "
					+ motor.getTachoCount(),
					motor.getTachoCount() >= target - 2);
			assertTrue(
					"Rotation for " + target + "went too far: "
							+ motor.getTachoCount(),
					motor.getTachoCount() <= target + 2);
		}
	}

	private class TestListener implements RegulatedMotorListener {

		public int started = 0;
		public int stopped = 0;

		@Override
		public void rotationStarted(RegulatedMotor _motor, int _tachoCount,
				boolean _stalled, long _timeStamp) {
			started++;
		}

		@Override
		public void rotationStopped(RegulatedMotor _motor, int _tachoCount,
				boolean _stalled, long _timeStamp) {
			stopped++;
		}
	}

	@Test
	public void testListener() {
		TestListener listener = new TestListener();
		assertTrue(listener.started == 0);
		assertTrue(listener.stopped == 0);
		RegulatedMotor motor = new SimulatedMotor();
		motor.addListener(listener);
		int count = 5;
		for (int i = 0; i < count; i++) {
			motor.rotate(180, false);
		}
		assertTrue(listener.started == 5);
		assertTrue(listener.stopped == 5);
	}

}
