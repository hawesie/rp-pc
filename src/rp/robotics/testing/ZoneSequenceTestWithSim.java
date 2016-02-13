package rp.robotics.testing;

import rp.robotics.DifferentialDriveRobotPC;
import rp.robotics.simulation.MapBasedSimulation;
import rp.systems.StoppableRunnable;

/**
 * Legacy class. Replace in your code with {@link ZoneSequenceTest}.
 * 
 * @author Nick Hawes
 *
 */
@Deprecated
public class ZoneSequenceTestWithSim<C extends StoppableRunnable> extends
		ZoneSequenceTest<C> {

	public ZoneSequenceTestWithSim(ZoneSequence _sequence, C _controller,
			DifferentialDriveRobotPC _poser, long _timeout,
			boolean _failIfOutOfSequence, MapBasedSimulation _sim) {
		super(_sim, _sequence, _controller, _poser, _timeout,
				_failIfOutOfSequence);
	}

}
