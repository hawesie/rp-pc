package rp.robotics.testing;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Duration;
import java.time.Instant;

import lejos.robotics.RangeFinder;
import rp.robotics.MobileRobot;
import rp.robotics.simulation.MapBasedSimulation;
import rp.robotics.simulation.SimulationCore;
import rp.robotics.simulation.SimulationSteppable;
import rp.systems.StoppableRunnable;

/**
 * 
 * Test that checks whether the robot maintains range readings under a given
 * limit for a set time.
 * 
 * @author Nick Hawes
 *
 */
public class RangeLimitTest<C extends StoppableRunnable> extends RobotTest<C> {

	private final float m_rangeLimit;
	// private final Pose m_startPose;
	private final Duration m_allowableOutsideLimit;
	private final Duration m_startupTime;
	private final RangeFinder m_ranger;

	public RangeLimitTest(MapBasedSimulation _sim, RangeFinder _ranger,
			float _limit, C _controller, MobileRobot _poser,
			Duration _timeout, Duration _allowableOutsideLimit,
			Duration _startupTime) {
		super(_sim, _controller, _poser, _timeout);
		m_rangeLimit = _limit;
		m_ranger = _ranger;
		m_allowableOutsideLimit = _allowableOutsideLimit;
		m_startupTime = _startupTime;

	}

	private boolean isWithinRange(float _reading) {
		return _reading <= m_rangeLimit;
	}

	@Override
	public void run() {

		assertTrue("Controller could not be created for this test.",
				m_controller != null);

		Thread t = new Thread(m_controller);
		Instant now = Instant.now();

		Instant endAt = now.plus(m_timeout);
		Instant startAfter = now.plus(m_startupTime);

		assertTrue(startAfter.isBefore(endAt));

		try {
			t.start();
			SimulationCore.getSimulationCore().addAndWaitSteppable(
					new SimulationSteppable() {

						boolean failed = false;
						boolean ended = false;
						Instant lastGood;

						@Override
						public void step(Instant _now, Duration _stepInterval) {
														
							
							if (_now.isAfter(endAt)) {
								ended = true;
								// System.out
								// .println("Successfully reached end of test");
							} else if (lastGood != null
									&& _now.isAfter(startAfter)) {
								float reading = m_ranger.getRange();
								// System.out.println(reading);

								if (reading <= 0.03) {
									failed = true;
									fail("Robot is too close to obstacle. Range reading: "
											+ reading);
								} else if (isWithinRange(reading)) {
									// System.out.println("Good reading!");
									lastGood = _now;
								} else if (Duration.between(lastGood, _now)
										.compareTo(m_allowableOutsideLimit) > 0) {
									failed = true;

									fail("Range exceeded limit of "
											+ m_rangeLimit
											+ " for longer than allowable duration of "
											+ m_allowableOutsideLimit);
								}

							} else {
								lastGood = _now;
							}
						}

						@Override
						public boolean remove() {
							return failed || ended;
						}
					});

		} finally {

			long stopCalledAt = System.currentTimeMillis();
			// System.out.println("stopping controller");
			m_controller.stop();
			try {
				// System.out.println("joining");
				t.join(10000);
				callListenersControllerStopped(m_poser,
						System.currentTimeMillis() - stopCalledAt);
				// System.out.println("done");

			} catch (InterruptedException e) {
				fail(e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
