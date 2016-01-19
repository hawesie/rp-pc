package rp.robotics.testing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static rp.robotics.testing.PoseMatcher.is;

import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import rp.robotics.testing.TargetZone.Status;
import rp.systems.StoppableRunnable;
import rp.util.Rate;

/**
 * 
 * Test that checks whether the robot moves though a sequence of zones. The test
 * can either fail on timeout or if another zone is reached out of sequence.
 * 
 * @author Nick Hawes
 *
 */
public class ZoneSequenceTest<T extends PoseProvider, C extends StoppableRunnable>
		implements Iterable<TargetZone> {

	private C m_controller;
	private T m_poser;
	private long m_timeout;
	private boolean m_failIfOutOfSequence;
	private final ZoneSequence m_sequence;

	public ZoneSequenceTest(ZoneSequence _sequence, C _controller, T _poser,
			long _timeout, boolean _failIfOutOfSequence) {
		m_sequence = _sequence;
		m_controller = _controller;
		m_poser = _poser;
		m_timeout = _timeout;
		m_failIfOutOfSequence = _failIfOutOfSequence;
	}

	public void run() {
		run(false);
	}

	public C getController() {
		return m_controller;
	}

	/**
	 * Run this test on the given controller/pose provider pair.
	 * 
	 * @param _controller
	 * @param _poser
	 * @throws InterruptedException
	 */
	public void run(boolean _failOnStopTimeout) {

		Stack<TargetZone> zones = new Stack<TargetZone>();
		zones.addAll(m_sequence.getZones());
		Collections.reverse(zones);

		m_poser.setPose(m_sequence.getStart());

		assertThat(m_poser.getPose(), is(m_sequence.getStart()));

		Thread t = new Thread(m_controller);
		long timeoutAt = System.currentTimeMillis() + m_timeout;

		Rate r = new Rate(5);

		zones.peek().setStatus(Status.LIVE);

		t.start();
		while (System.currentTimeMillis() < timeoutAt && zones.size() > 0) {

			Pose p = m_poser.getPose();

			if (zones.peek().inZone(p)) {
				zones.pop().setStatus(Status.HIT);

				if (!zones.isEmpty()) {
					zones.peek().setStatus(Status.LIVE);
				}
				// System.out.println("Zone done");
			} else if (m_failIfOutOfSequence) {
				for (TargetZone zone : m_sequence) {
					assertFalse(
							"Test must not visit other zones before next target",
							zone.inZone(p));
				}
			}

			r.sleep();
		}

		if (zones.size() > 0) {
			fail(String.format(
					"Test timed out after %d milliseconds with %d zones left.",
					m_timeout, zones.size()));
		}

		// System.out.println("Tests all passed, stopping controller");
		m_controller.stop();
		try {
			// If we should test the stopping time of this controller
			if (_failOnStopTimeout) {
				t.join(100);
				assertFalse(
						"Controller must not be alive 100 milliseconds after stop is called",
						t.isAlive());

			} else {
				t.join(5000);
			}

		} catch (InterruptedException e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
		// System.out.println("Test done");

	}

	@Override
	public Iterator<TargetZone> iterator() {
		return m_sequence.iterator();
	}

	public T getPoseProvider() {
		return m_poser;
	}
}
