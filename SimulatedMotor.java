package rp.sim;

import java.util.function.Predicate;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;
import lejos.util.Delay;

/**
 * This class simulates the desired behaviour of a regulated motor. It does not
 * simulate noise or errors.
 * 
 * @author Nick Hawes
 *
 */
public class SimulatedMotor implements RegulatedMotor {

	// target speed of motor in degrees/second
	protected float m_targetSpeed = 360;
	// acceleration of motor in degrees/second/second
	protected int m_acceleration = 6000;
	// current speed of motor in degrees/second
	protected float m_currentSpeed = 0;
	// tacho count of motor in degrees
	protected int m_tachoCount = 0;

	// whether or not the motor should be moving
	private boolean m_isMoving = false;

	// listener for motor actions, only support a single listener to match NXT
	// implementation
	private RegulatedMotorListener m_listener = null;

	private final float FORWARD = 1f;
	private final float STOPPED = 0f;
	private final float BACKWARD = -1f;

	private float m_direction = STOPPED;
	private Thread m_moveThread;
	private int m_limitAngle;

	private void notifyListener(boolean _started) {
		if (m_listener != null) {
			if (_started) {
				m_listener.rotationStarted(this, getTachoCount(), isStalled(),
						System.currentTimeMillis());
			} else {
				m_listener.rotationStopped(this, getTachoCount(), isStalled(),
						System.currentTimeMillis());
			}
		}

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

		float cycleTimeSecs = 2f / m_targetSpeed;
		long cycleTimeMs = (long) (cycleTimeSecs * 1000);

		notifyListener(true);

		while (m_isMoving && !_tachoPredicate.test(m_tachoCount)) {

			// System.out.println("loop:" + Math.floor(cycleTimeSecs *
			// _currentSpeed
			// * _direction));

			m_tachoCount += Math.floor(cycleTimeSecs * m_currentSpeed
					* m_direction);
			// System.out.println(m_tachoCount);

			if (m_currentSpeed < m_targetSpeed) {
				m_currentSpeed = Math.min(m_targetSpeed, m_currentSpeed
						+ (cycleTimeSecs * m_acceleration));
			}

			Delay.msDelay(cycleTimeMs);
		}

		// need to set this if the tacho predicate stopped the move
		m_isMoving = false;

		notifyListener(false);
	}

	@Override
	protected void finalize() throws Throwable {
		// make sure any movement thread can exit on shutdown
		m_isMoving = false;
	}

	private void startMove(float _direction) {
		startMove(_direction, i -> false);
	}

	private void startMove(float _direction, Predicate<Integer> _tachoPredicate) {

		// if moving in a different direction, make sure we've stopped before
		// moving again
		if (m_isMoving) {
			if (m_direction != _direction) {
				stop(false);
			} else {
				return;
			}
		} else if (m_moveThread != null) {
			// if we're not moving, make sure the last move thread has cleaned
			// up
			waitComplete();
		}

		m_direction = _direction;
		m_isMoving = true;

		m_moveThread = new Thread(new Runnable() {
			@Override
			public void run() {
				move(_tachoPredicate);

			}
		});
		m_moveThread.setDaemon(true);
		m_moveThread.start();
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
		return m_isMoving;
	}

	@Override
	public int getRotationSpeed() {
		return getSpeed();
	}

	@Override
	public int getTachoCount() {
		return m_tachoCount;
	}

	@Override
	public void resetTachoCount() {
		m_tachoCount = 0;
	}

	@Override
	public void addListener(RegulatedMotorListener _listener) {
		m_listener = _listener;
	}

	@Override
	public RegulatedMotorListener removeListener() {
		RegulatedMotorListener listener = m_listener;
		m_listener = null;
		return m_listener;
	}

	@Override
	public void stop(boolean _immediateReturn) {
		m_isMoving = false;
		if (!_immediateReturn) {
			waitComplete();
		}
		m_direction = STOPPED;
	}

	@Override
	public void flt(boolean _immediateReturn) {
		stop(_immediateReturn);
	}

	@Override
	public void waitComplete() {
		if (m_moveThread != null) {
			try {
				m_moveThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
		m_limitAngle = _limitAngle;
		float direction = FORWARD;
		Predicate<Integer> target = i -> i >= _limitAngle - 2;
		if (_limitAngle < getTachoCount()) {
			direction = BACKWARD;
			target = i -> i <= _limitAngle + 2;
		}
		startMove(direction, target);
		if (!_immediateReturn) {
			waitComplete();
		}
	}

	@Override
	public int getLimitAngle() {
		return m_limitAngle;
	}

	@Override
	public void setSpeed(int _speed) {
		if (_speed > 0 && _speed <= getMaxSpeed()) {
			m_targetSpeed = _speed;
		} else {
			throw new IllegalArgumentException(
					"Speed must be greater than 0 and less than or equal to "
							+ getMaxSpeed());
		}

	}

	@Override
	public int getSpeed() {
		return Math.round((float) Math.floor(m_currentSpeed));
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
		if (_acceleration > 0 && _acceleration <= 6000) {
			m_acceleration = _acceleration;
		} else {
			throw new IllegalArgumentException(
					"Acceleration must be greater than 0 and less than or equal to 6000");
		}

	}

	public static void main(String[] args) {
		SimulatedMotor motor = new SimulatedMotor();
		motor.rotateTo(0, false);
		System.out.println("Count: " + motor.getTachoCount());
		motor.rotateTo(361, false);
		System.out.println("Count: " + motor.getTachoCount());
	}

}
