package rp.robotics.testing;

import java.time.Duration;

import lejos.robotics.localization.PoseProvider;
import rp.robotics.MobileRobot;
import rp.robotics.simulation.MapBasedSimulation;
import rp.systems.StoppableRunnable;

/**
 * 
 * Test that checks whether the robot maintains range readings under a given
 * limit for a set time.
 * 
 * @author Nick Hawes
 *
 */
public class PoseDistanceLimitTest<C extends StoppableRunnable> extends
		DistanceLimitTest<C> {

	private PoseProvider m_p1;
	private PoseProvider m_p2;

	public PoseDistanceLimitTest(MapBasedSimulation _sim, PoseProvider _p1,
			PoseProvider _p2, float _limit, C _controller, MobileRobot _poser,
			Duration _timeout, Duration _allowableOutsideLimit,
			Duration _startupTime) {

		super(_sim, _limit, _controller, _poser, _timeout,
				_allowableOutsideLimit, _startupTime);

		m_p1 = _p1;
		m_p2 = _p2;

	}

	@Override
	protected float getDistance() {
		return m_p1.getPose().distanceTo(m_p2.getPose().getLocation());
	}
}
