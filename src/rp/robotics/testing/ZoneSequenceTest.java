package rp.robotics.testing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static rp.robotics.testing.PoseMatcher.is;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;

import lejos.robotics.navigation.Pose;
import rp.robotics.MobileRobot;
import rp.robotics.simulation.MapBasedSimulation;
import rp.robotics.simulation.SimulationSteppable;
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
public class ZoneSequenceTest<C extends StoppableRunnable> extends RobotTest<C>
		implements Iterable<TargetZone> {

	private boolean m_failIfOutOfSequence;
	private final ZoneSequence m_sequence;

	public ZoneSequenceTest(MapBasedSimulation _sim, ZoneSequence _sequence,
			C _controller, MobileRobot _poser, long _timeout,
			boolean _failIfOutOfSequence) {
		super(_sim, _controller, _poser, _timeout);
		m_sequence = _sequence;
		m_failIfOutOfSequence = _failIfOutOfSequence;
	}

	@Override
	public void run() {

		assertTrue("Controller could not be created for this test.",
				m_controller != null);

		Stack<TargetZone> zones = new Stack<TargetZone>();
		zones.addAll(m_sequence.getZones());
		Collections.reverse(zones);

		m_poser.setPose(m_sequence.getStart());

		assertThat(m_poser.getPose(), is(m_sequence.getStart()));

		Thread t = new Thread(m_controller);
		Instant timeoutAt = Instant.now().plus(m_timeout);

		zones.peek().setStatus(Status.LIVE);
		try {
			t.start();
			m_sim.getSimulationCore().addAndWaitSteppable(
					new SimulationSteppable() {

						boolean failed = false;

						@Override
						public void step(Instant _now, Duration _stepInterval) {

							if (_now.isAfter(timeoutAt)) {
								failed = false;
							} else {

								Pose p = m_poser.getPose();

								if (zones.peek().inZone(p)) {
									zones.pop().setStatus(Status.HIT);

									if (!zones.isEmpty()) {
										zones.peek().setStatus(Status.LIVE);
									}
									// System.out.println("Zone done");
								} else if (m_failIfOutOfSequence) {
									for (TargetZone zone : m_sequence) {
										failed = true;
										assertFalse(
												"Test must not visit other zones before next target",
												zone.inZone(p));
									}
								}
							}

						}

						@Override
						public boolean remove(Instant _now, Duration _stepInterval) {
							return failed || zones.size() == 0;
						}
					});

			if (zones.size() > 0) {
				fail(String
						.format("Test timed out after %d milliseconds with %d zones left.",
								m_timeout, zones.size()));
			}

		} finally {

			// System.out.println("Tests all passed, stopping controller");
			long stopCalledAt = System.currentTimeMillis();
			m_controller.stop();
			try {
				t.join(10000);
				callListenersControllerStopped(m_poser,
						System.currentTimeMillis() - stopCalledAt);

			} catch (InterruptedException e) {
				fail(e.getMessage());
				e.printStackTrace();
			}
			// System.out.println("Test done");
		}
	}

	@Override
	public Iterator<TargetZone> iterator() {
		return m_sequence.iterator();
	}
}
