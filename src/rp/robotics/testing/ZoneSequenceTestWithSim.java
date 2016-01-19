package rp.robotics.testing;

import lejos.robotics.localization.PoseProvider;
import rp.robotics.simulation.MapBasedSimulation;
import rp.systems.StoppableRunnable;

/**
 * Adds the associated simulation to the test.
 * 
 * This needs a cleaner design.
 * 
 * @author Nick Hawes
 *
 */
public class ZoneSequenceTestWithSim<T extends PoseProvider, C extends StoppableRunnable>
		extends ZoneSequenceTest<T, C> {

	private final MapBasedSimulation m_sim;

	public ZoneSequenceTestWithSim(ZoneSequence _sequence, C _controller,
			T _poser, long _timeout, boolean _failIfOutOfSequence,
			MapBasedSimulation _sim) {
		super(_sequence, _controller, _poser, _timeout, _failIfOutOfSequence);
		m_sim = _sim;
	}

	public MapBasedSimulation getSimulation() {
		return m_sim;
	}

}
