package rp.robotics.testing;

import lejos.robotics.RangeFinder;

/**
 * Mock range finder that simply returns the set value.
 * 
 * @author nah
 *
 */
public class MockRangeFinder implements RangeFinder {

	private float m_range = 0;
	private Object m_OnReading = new Object();

	public void setRange(float _range) {
		m_range = _range;
	}

	@Override
	public float getRange() {
		synchronized (m_OnReading) {
			m_OnReading.notifyAll();
			return m_range;
		}
	}

	@Override
	public float[] getRanges() {
		return new float[] { getRange() };
	}

	public void waitForReading() throws InterruptedException {
		synchronized (m_OnReading) {
			m_OnReading.wait();
		}
	}

}
