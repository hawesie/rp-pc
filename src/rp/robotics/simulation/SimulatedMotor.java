package rp.robotics.simulation;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Predicate;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;
import rp.util.Rate;

/**
 * This class simulates the desired behaviour of a regulated motor. It does not
 * simulate noise or errors.
 * 
 * @author Nick Hawes
 *
 */
public class SimulatedMotor implements RegulatedMotor,
		Comparable<SimulatedMotor> {

	// target speed of motor in degrees/second
	protected double m_targetSpeed = 360;
	// acceleration of motor in degrees/second/second
	protected int m_acceleration = 6000;
	// current speed of motor in degrees/second
	protected double m_commandedSpeed = 0;
	protected double m_measuredSpeed = 0;

	// tacho count of motor in degrees
	protected double m_tachoCount = 0;

	// whether or not the motor should be moving
	private boolean m_isMoving = false;

	// whether a stop has been requested
	private boolean m_stopRequested = false;

	// listener for motor actions, only support a single listener to match NXT
	// implementation
	private RegulatedMotorListener m_listener = null;

	private enum MotorState {
		ACCELERATING, DECELERATING, REGULATING, STOPPED
	};

	private MotorState m_state = MotorState.STOPPED;

	private final double FORWARD = 1f;
	private final double STOPPED = 0f;
	private final double BACKWARD = -1f;

	private double m_direction = STOPPED;

	private int m_limitAngle;
	private final SimulationCore m_sim;
	private final Object m_stepLock = new Object();
	private final Object m_moveLock = new Object();

	private final UUID m_uuid = UUID.randomUUID();

	@SuppressWarnings("unused")
	private final String m_label;

	@Override
	public boolean equals(Object _obj) {
		if (_obj instanceof SimulatedMotor) {
			SimulatedMotor that = (SimulatedMotor) _obj;
			return this.m_uuid.equals(that.m_uuid);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return m_uuid.hashCode();
	}

	public SimulatedMotor(SimulationCore _sim, String _label) {
		m_sim = _sim;
		m_label = _label;
	}

	private void notifyListener(boolean _started, Instant _now) {
		if (m_listener != null) {
			if (_started) {
				m_listener.rotationStarted(this, getTachoCount(), isStalled(),
						_now.toEpochMilli());
			} else {
				m_listener.rotationStopped(this, getTachoCount(), isStalled(),
						_now.toEpochMilli());
				// println("Before notify");
				synchronized (m_moveLock) {
					m_moveLock.notifyAll();
				}
				// println("After notify");
			}
		}

	}

	public SimulationCore getSim() {
		return m_sim;
	}

	/**
	 * Move until stopped or until the predicate tests true.
	 * 
	 * @param _tachoPredicate
	 */
	private void move(Predicate<Integer> _tachoPredicate) {

		// the resolution of the movement simulation

		// tacho should have a resolution of 4 degrees (+/- 2) so this loop
		// needs to increment in smaller steps, e.g. 2 degree steps

		m_state = MotorState.ACCELERATING;

		SimulationSteppable moveSteppable = new SimulationSteppable() {

			boolean first = true;

			Speedometer speedo = new Speedometer(getTachoCount(), Instant.now()
					.toEpochMilli(), 20);

			@SuppressWarnings("unused")
			public void regulateStep(Instant _now, Duration _stepInterval) {

				m_measuredSpeed = speedo.update(getTachoCount(),
						_now.toEpochMilli());

				double difference = m_targetSpeed - m_measuredSpeed;
				// println("regulate: " + _stepInterval);

				if (m_state == MotorState.REGULATING) {
					// println("spped diff: " + difference);
					// println("measured: " + m_measuredSpeed);

					// m_commandedSpeed = m_commandedSpeed + (0.0001 *
					// difference);
				}

			}

			@Override
			public void step(Instant _now, Duration _stepInterval) {

				// println("start step");

				if (first) {
					notifyListener(true, _now);
					first = false;
				}

				synchronized (m_stepLock) {
					// println("In moveStep stepLock");

					moveStep(_now, _stepInterval);
					// regulateStep(_now, _stepInterval);
					// println("Out moveStep stepLock");
				}

				// println("end step");
			}

			public void moveStep(Instant _now, Duration _stepInterval) {

				// println("move: " + _stepInterval);

				// println("inner step");
				//
				// println(_stepInterval.toMillis());
				double cycleTimeSecs = _stepInterval.toMillis() / 1000.0;

				if (m_state == MotorState.ACCELERATING) {

					if (m_commandedSpeed < m_targetSpeed) {
						// don't accelerate past target speed
						m_commandedSpeed = Math.min(m_commandedSpeed
								+ (m_acceleration * cycleTimeSecs),
								m_targetSpeed);
						// println(cycleTimeSecs);
						// println("accelerating to speed: "
						// + m_commandedSpeed);

					} else {
						m_state = MotorState.REGULATING;
					}
				} else if (m_state == MotorState.DECELERATING) {

					if (m_commandedSpeed > m_targetSpeed) {
						// don't accelerate past target speed
						m_commandedSpeed = Math.max(m_commandedSpeed
								- (m_acceleration * cycleTimeSecs),
								m_targetSpeed);
						// println(cycleTimeSecs);
						// println("accelerating to speed: "
						// + m_commandedSpeed);
					} else {
						m_state = MotorState.REGULATING;
					}
				}

				m_tachoCount += (cycleTimeSecs * m_commandedSpeed * m_direction);
				// println("Count: " + m_tachoCount);
				m_measuredSpeed = speedo.update(getTachoCount(),
						_now.toEpochMilli());

			}

			@Override
			public boolean remove(Instant _now, Duration _stepInterval) {
				boolean remove = !(!m_stopRequested && !_tachoPredicate
						.test(getTachoCount()));
				// println("Removing? " + remove);
				if (remove) {
					println("Removing");
					m_isMoving = false;
					m_commandedSpeed = 0;
					m_measuredSpeed = 0;
					m_state = MotorState.STOPPED;
					m_stopRequested = false;
					// System.out.print("NOtifing..");
					notifyListener(false, _now);
					// println("... done");
				}
				return remove;
			}
		};

		m_sim.addSteppable(moveSteppable);

		// m_sim.addAndWaitSteppable(moveSteppable);
		//
		// // need to set this if the tacho predicate stopped the move
		// m_isMoving = false;
		// m_commandedSpeed = 0;
		// m_measuredSpeed = 0;
		// m_state = MotorState.STOPPED;

	}

	@Override
	protected void finalize() throws Throwable {
		// make sure any movement thread can exit on shutdown
		m_stopRequested = true;
	}

	private void startMove(double _direction) {
		startMove(_direction, i -> false);
	}

	private void startMove(double _direction, Predicate<Integer> _tachoPredicate) {

		// if moving in a different direction, make sure we've stopped
		// before
		// moving again
		if (m_isMoving) {
			if (m_direction != _direction) {
				println("Stopping for changing direction... ");
				stop(false);
				println("... done");
			} else {
				return;
			}
		}

		synchronized (m_stepLock) {
			println("In startMove stepLock");

			m_direction = _direction;
			m_isMoving = true;

			move(_tachoPredicate);
			println("Out startMove stepLock");
		}
	}

	@Override
	public void forward() {
		startMove(FORWARD);
	}

	@Override
	public void backward() {
		startMove(BACKWARD);
	}

	@Override
	public void stop() {
		stop(false);
	}

	@Override
	public void flt() {
		// the simulation can't float, so this is as good as we get.
		stop();
	}

	@Override
	public boolean isMoving() {
		// println("In isMoving");
		synchronized (m_stepLock) {
			// println("Out isMoving");

			return m_isMoving;
		}
	}

	@Override
	public int getRotationSpeed() {
		return getSpeed();
	}

	@Override
	public int getTachoCount() {
		synchronized (m_stepLock) {
			return (int) Math.round(Math.floor(m_tachoCount));
		}
	}

	@Override
	public void resetTachoCount() {
		synchronized (m_stepLock) {
			m_tachoCount = 0;
		}
	}

	@Override
	public void addListener(RegulatedMotorListener _listener) {
		m_listener = _listener;
	}

	@Override
	public RegulatedMotorListener removeListener() {
		RegulatedMotorListener listener = m_listener;
		m_listener = null;
		return listener;
	}

	private void println(Object _o) {
		// System.out.println(m_label + ": " + _o.toString());
	}

	private int stopCalled = 0;

	@Override
	public void stop(boolean _immediateReturn) {

		println("stop() before step lock");
		if (isMoving()) {
			synchronized (m_stepLock) {
				println((++stopCalled) + " stop() in step lock");
				m_stopRequested = true;
			}
			if (!_immediateReturn) {
				println(stopCalled + " stop() before wait");
				waitComplete();
				println(stopCalled + " stop() after wait");
			}
		}

	}

	@Override
	public void flt(boolean _immediateReturn) {
		stop(_immediateReturn);
	}

	@Override
	public void waitComplete() {

		while (isMoving()) {
			println("waitComplete() Before moveLock: " + m_stopRequested);
			synchronized (m_moveLock) {
				try {
					println("waitComplete() waiting");
					m_moveLock.wait(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Thread.yield();
			// Delay.msDelay(10);
		}

	}

	@Override
	public void rotate(int _angle, boolean _immediateReturn) {
		rotateTo(getTachoCount() + _angle, _immediateReturn);
	}

	@Override
	public void rotate(int _angle) {
		rotateTo(getTachoCount() + _angle, false);
	}

	@Override
	public void rotateTo(int _limitAngle) {
		rotateTo(_limitAngle, false);

	}

	@Override
	public void rotateTo(int _limitAngle, boolean _immediateReturn) {
		double direction = FORWARD;
		Predicate<Integer> target = null;

		synchronized (m_stepLock) {
			m_limitAngle = _limitAngle;
			target = i -> i >= _limitAngle - 2;
			if (_limitAngle < getTachoCount()) {
				direction = BACKWARD;
				target = i -> i <= _limitAngle + 2;
			}
		}

		startMove(direction, target);

		if (!_immediateReturn) {
			waitComplete();
		}

	}

	@Override
	public int getLimitAngle() {
		synchronized (m_stepLock) {
			return m_limitAngle;
		}
	}

	@Override
	public void setSpeed(int _speed) {

		synchronized (m_stepLock) {

			if (_speed >= 0 && _speed <= getMaxSpeed()) {
				if (m_state == MotorState.REGULATING) {
					if (_speed > m_targetSpeed) {
						m_state = MotorState.ACCELERATING;
					} else if (_speed < m_targetSpeed) {
						m_state = MotorState.DECELERATING;
					}
				} else if (m_state == MotorState.ACCELERATING
						&& _speed < getSpeed()) {
					m_state = MotorState.DECELERATING;

				} else if (m_state == MotorState.DECELERATING
						&& _speed > getSpeed()) {
					m_state = MotorState.ACCELERATING;
				}
				m_targetSpeed = _speed;
			} else {
				throw new IllegalArgumentException("Speed must be >= 0 and <= "
						+ getMaxSpeed());
			}
		}

	}

	@Override
	public int getSpeed() {
		synchronized (m_stepLock) {
			return Math.round((float) Math.floor(m_measuredSpeed));
		}
	}

	@Override
	public float getMaxSpeed() {
		// It is generally assumed, that the maximum accurate speed of Motor is
		// 100 degree/second * Voltage
		// 9.4V seems like a high value for the voltage
		return 900;
	}

	@Override
	public boolean isStalled() {
		return false;
	}

	@Override
	public void setStallThreshold(int _error, int _time) {
	}

	@Override
	public void setAcceleration(int _acceleration) {
		synchronized (m_stepLock) {
			if (_acceleration > 0 && _acceleration <= 6000) {
				m_acceleration = _acceleration;
			} else {
				throw new IllegalArgumentException(
						"Acceleration must be greater than 0 and less than or equal to 6000");
			}
		}
	}

	public static void main(String[] args) {
		SimulationCore sim = SimulationCore.createSimulationCore();
		SimulatedMotor motor1 = new SimulatedMotor(sim, "");
		motor1.forward();
		int prev = 0;
		Rate rate = new Rate(10);

		for (int i = 0; i < 20; i++) {
			int curr = motor1.getTachoCount();
			System.out.println(motor1.getSpeed() + " " + (curr - prev));
			prev = curr;
			rate.sleep();
		}

		//
		// // RegulatedMotor motor = new SimulatedMotor();
		// // int[] targets = { 0, 361, -33, 400, 404, -27, -666, 1024 };
		// // for (int target : targets) {
		// // motor.rotateTo(target, false);
		// // println("Rotation for " + target + " att " +
		// // motor.getTachoCount());
		// // }
		//
		// // DifferentialPilot dp = new DifferentialPilot(56, 163,
		// // new SimulatedMotor(), new SimulatedMotor());
		// //
		// // OdometryPoseProvider pp = new OdometryPoseProvider(dp);
		// //
		// // println(pp.getPose());
		// //
		// // double distanceMm = 500;
		// //
		// // dp.travel(distanceMm);
		// // dp.rotate(-90);
		// // dp.travel(distanceMm);
		// //
		// // println(pp.getPose());
	}

	@Override
	public int compareTo(SimulatedMotor _that) {
		return this.m_uuid.compareTo(_that.m_uuid);
	}

}
