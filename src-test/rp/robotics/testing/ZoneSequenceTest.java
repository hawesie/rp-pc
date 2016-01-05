package rp.robotics.testing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static rp.robotics.testing.PoseMatcher.is;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;
import rp.robotics.simulation.DifferentialDriveRobot;
import rp.robotics.simulation.Rate;
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
	private boolean m_failIfOutOfSequence = false;
	private final Pose m_start;
	private final LineMap m_map;

	public ZoneSequenceTest(LineMap _map, Pose _start, TargetZone... _zones) {

		m_map = _map;
		m_start = _start;
		m_zones = new ArrayList<TargetZone>(_zones.length);
		for (TargetZone tz : _zones) {
			m_zones.add(tz);
		}
	}

	/**
	 * If set to true then the test fails if a target other than the next
	 * expected one is reached.
	 * 
	 * @param _failIfOutOfSequence
	 */
	public void setFailIfOutOfSequence(boolean _failIfOutOfSequence) {
		m_failIfOutOfSequence = _failIfOutOfSequence;
	}

	@Override
	public Iterator<TargetZone> iterator() {
		return m_zones.iterator();
	}

	/**
	 * Run this test on the given controller/pose provider pair.
	 * 
	 * @param _controller
	 * @param _poser
	 * @throws InterruptedException
	 */
	public void runTest(StoppableRunnable _controller, long _timeout)
			throws InterruptedException {

		DifferentialDriveRobot robot = new DifferentialDriveRobot();

		Stack<TargetZone> zones = new Stack<TargetZone>();
		zones.addAll(m_zones);

		_poser.setPose(m_start);

		assertThat(_poser.getPose(), is(m_start));

		Thread t = new Thread(_controller);
		long timeoutAt = System.currentTimeMillis() + _timeout;

		Rate r = new Rate(5);

		t.start();
		while (System.currentTimeMillis() < timeoutAt && zones.size() > 0) {

			Pose p = _poser.getPose();

			if (zones.peek().inZone(p)) {
				zones.pop();
				System.out.println("Zone done");
			} else if (m_failIfOutOfSequence) {
				for (int i = 1; i < m_zones.size(); i++) {
					assertFalse(
							"Test must not visit other zones before next target",
							zones.elementAt(i).inZone(p));
				}
			}

			r.sleep();
		}

		if (zones.size() > 0) {
			fail(String.format(
					"Test timed out after %d milliseconds with %d zones left.",
					_timeout, zones.size()));
		}

		_controller.stop();
		t.join(5000);

	}
}
