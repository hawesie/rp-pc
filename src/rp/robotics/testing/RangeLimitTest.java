package rp.robotics.testing;

import java.time.Duration;

import lejos.robotics.RangeFinder;
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
public class RangeLimitTest<C extends StoppableRunnable> extends
		DistanceLimitTest<C> {

	private final RangeFinder m_ranger;

	public RangeLimitTest(MapBasedSimulation _sim, RangeFinder _ranger,
			float _limit, C _controller, MobileRobot _poser, Duration _timeout,
			Duration _allowableOutsideLimit, Duration _startupTime) {
		super(_sim, _limit, _controller, _poser, _timeout,
				_allowableOutsideLimit, _startupTime);
		m_ranger = _ranger;

	}

	@Override
	protected float getDistance() {
		return m_ranger.getRange();
	}

}
