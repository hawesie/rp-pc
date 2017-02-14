package rp.robotics.testing;

import rp.robotics.TouchSensorEvent;
import rp.robotics.TouchSensorListener;

/**
 * Mock listener object for use in tests.
 * 
 * @author Nick Hawes
 *
 */
public class TouchListenerTest implements TouchSensorListener {

	private boolean m_pressed;
	private boolean m_released;
	private boolean m_bumped;
	private TouchSensorEvent m_lastEvent;
	private Object m_OnEvent = new Object();

	public TouchListenerTest() {
		reset();
	}

	public void reset() {
		m_pressed = false;
		m_released = false;
		m_bumped = false;
	}

	@Override
	public void sensorPressed(TouchSensorEvent _e) {
		System.out.println("Pressed");
		synchronized (m_OnEvent) {
			onEvent(_e);
			m_pressed = true;
		}
	}

	@Override
	public void sensorReleased(TouchSensorEvent _e) {
		System.out.println("Released");
		synchronized (m_OnEvent) {
			onEvent(_e);
			m_released = true;
		}
	}

	@Override
	public void sensorBumped(TouchSensorEvent _e) {
		System.out.println("Bumped");
		synchronized (m_OnEvent) {
			onEvent(_e);
			m_bumped = true;
		}
	}

	public boolean wasPressed() {
		return m_pressed;
	}

	public boolean wasReleased() {
		return m_released;
	}

	public boolean wasBumped() {
		return m_bumped;
	}

	public TouchSensorEvent getLastEvent() {
		return m_lastEvent;
	}

	/**
	 * Tests if the event status matches the input.
	 * 
	 * @param _pressed
	 * @param _released
	 * @param _bumped
	 * @return
	 */
	public boolean eventStatus(boolean _pressed, boolean _released,
			boolean _bumped) {

		return wasPressed() == _pressed && wasReleased() == _released
				&& wasBumped() == _bumped;
	}

	public void waitForEvent(long _timeout) throws InterruptedException {
		synchronized (m_OnEvent) {
			m_OnEvent.wait(_timeout);
		}
	}

	public void waitForEvent(long _timeout, int _count)
			throws InterruptedException {
		int remainingCount = _count;
		long stopTime = System.currentTimeMillis() + _timeout;
		long remainingTime = _timeout;

		synchronized (m_OnEvent) {
			while (remainingCount > 0 && System.currentTimeMillis() < stopTime) {

				m_OnEvent.wait(remainingTime);
				remainingCount--;
				remainingTime = stopTime - System.currentTimeMillis();
			}
		}
	}

	private void onEvent(TouchSensorEvent _e) {
		m_lastEvent = _e;
		m_OnEvent.notifyAll();
	}

}
