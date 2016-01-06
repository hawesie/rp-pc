package rp.robotics.testing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static rp.robotics.testing.PoseMatcher.is;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import rp.robotics.simulation.Rate;
import rp.robotics.testing.TargetZone.Status;
import rp.systems.StoppableRunnable;

/**
 * 
 * Test that checks whether the robot moves though a sequence of zones. The test
 * can either fail on timeout or if another zone is reached out of sequence.
 * 
 * @author Nick Hawes
 *
 */
public class ZoneSequenceTest implements Iterable<TargetZone> {

	private final ArrayList<TargetZone> m_zones;
	private StoppableRunnable m_controller;
	private PoseProvider m_poser;
	private Pose m_start;
	private long m_timeout;
	private boolean m_failIfOutOfSequence;

	public ZoneSequenceTest(StoppableRunnable _controller, PoseProvider _poser,
			Pose _start, long _timeout, boolean _failIfOutOfSequence,
			ArrayList<TargetZone> _zones) {
		m_controller = _controller;
		m_poser = _poser;
		m_start = _start;
		m_timeout = _timeout;
		m_failIfOutOfSequence = _failIfOutOfSequence;
		m_zones = _zones;
	}

	/**
	 * Run this test on the given controller/pose provider pair.
	 * 
	 * @param _controller
	 * @param _poser
	 * @throws InterruptedException
	 */
	public void run() {

		Stack<TargetZone> zones = new Stack<TargetZone>();
		Collections.reverse(m_zones);
		zones.addAll(m_zones);

		m_poser.setPose(m_start);

		assertThat(m_poser.getPose(), is(m_start));

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
				System.out.println("Zone done");
			} else if (m_failIfOutOfSequence) {
				for (TargetZone zone : m_zones) {
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

		m_controller.stop();
		try {
			t.join(5000);
		} catch (InterruptedException e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Test done");

	}

	@Override
	public Iterator<TargetZone> iterator() {
		return m_zones.iterator();
	}
}
