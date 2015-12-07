package rp.sim;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import lejos.robotics.RegulatedMotor;
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

		int delaySecs = 3;
		Delay.msDelay(delaySecs * 1000);

		// the motor should be going as fast as acceleration allows, up to
		// targetSpeed
		double estimatedSpeed = Math.min(acceleration * delaySecs, targetSpeed);
		int currentSpeed = motor.getRotationSpeed();

		int wiggleRoom = 30;

		assertTrue("Speed has exceeded target speed",
				currentSpeed <= targetSpeed);
		assertTrue("Speed is not fast enough", currentSpeed > estimatedSpeed
				- wiggleRoom);
		assertTrue("Speed is too fast", currentSpeed < estimatedSpeed
				+ wiggleRoom);

		long estimatedTachoCountIncrease = Math.round(Math.floor(estimatedSpeed
				* delaySecs));
		long tachoCountIncrease = motor.getTachoCount() - startCount;

		wiggleRoom = 200;
		assertTrue("Count is not high enough",
				tachoCountIncrease > estimatedTachoCountIncrease - wiggleRoom);
		assertTrue("Count is too high",
				tachoCountIncrease < estimatedTachoCountIncrease + wiggleRoom);

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

		int delaySecs = 3;
		Delay.msDelay(delaySecs * 1000);

		// the motor should be going as fast as acceleration allows, up to
		// targetSpeed
		double estimatedSpeed = Math.min(acceleration * delaySecs, targetSpeed);
		int currentSpeed = motor.getRotationSpeed();

		int wiggleRoom = 30;

		assertTrue("Speed has exceeded target speed",
				currentSpeed <= targetSpeed);
		assertTrue("Speed is not fast enough", currentSpeed > estimatedSpeed
				- wiggleRoom);
		assertTrue("Speed is too fast", currentSpeed < estimatedSpeed
				+ wiggleRoom);

		long estimatedTachoCountIncrease = -Math.round(Math
				.floor(estimatedSpeed * delaySecs));
		long tachoCountIncrease = motor.getTachoCount() - startCount;

		wiggleRoom = 200;
		assertTrue("Count is not high enough",
				tachoCountIncrease > estimatedTachoCountIncrease - wiggleRoom);
		assertTrue("Count is too high",
				tachoCountIncrease < estimatedTachoCountIncrease + wiggleRoom);

	}

	@Test
	public void testForwardBackwards() {

		RegulatedMotor motor = new SimulatedMotor();

		motor.forward();
		Delay.msDelay(1000);
		motor.stop();

		int targetSpeed = 360;
		int acceleration = 6000;

		long startCount = motor.getTachoCount();

		motor.setSpeed(targetSpeed);
		motor.setAcceleration(acceleration);

		// start moving backward
		motor.backward();

		int delaySecs = 3;
		Delay.msDelay(delaySecs * 1000);

		// the motor should be going as fast as acceleration allows, up to
		// targetSpeed
		double estimatedSpeed = Math.min(acceleration * delaySecs, targetSpeed);
		int currentSpeed = motor.getRotationSpeed();

		int wiggleRoom = 30;

		assertTrue("Speed has exceeded target speed",
				currentSpeed <= targetSpeed);
		assertTrue("Speed is not fast enough", currentSpeed > estimatedSpeed
				- wiggleRoom);
		assertTrue("Speed is too fast", currentSpeed < estimatedSpeed
				+ wiggleRoom);

		long estimatedTachoCountIncrease = -Math.round(Math
				.floor(estimatedSpeed * delaySecs));
		long tachoCountIncrease = motor.getTachoCount() - startCount;

		wiggleRoom = 100;
		assertTrue("Count is not high enough",
				tachoCountIncrease > estimatedTachoCountIncrease - wiggleRoom);
		assertTrue("Count is too high",
				tachoCountIncrease < estimatedTachoCountIncrease + wiggleRoom);

	}

	@Test
	public void testRotateTo() {
		RegulatedMotor motor = new SimulatedMotor();
		int[] targets = { 0, 361, -33, 400, 404, -27, -666, 1024 };
		for (int target : targets) {
			motor.rotateTo(target, false);
			assertTrue("Rotation for " + target + " did not go far enough",
					motor.getTachoCount() >= target - 2);
			assertTrue("Rotation for " + target + "went too far",
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
			assertTrue("Rotation for " + target + " did not go far enough",
					motor.getTachoCount() >= target - 2);
			assertTrue("Rotation for " + target + "went too far",
					motor.getTachoCount() <= target + 2);
		}
	}

}
