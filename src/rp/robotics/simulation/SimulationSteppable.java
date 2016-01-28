package rp.robotics.simulation;

import java.time.Duration;

public interface SimulationSteppable {

	/**
	 * When this returns true it is removed from the simulation.
	 * 
	 * @return
	 */
	boolean remove();

	/**
	 * Run a single step of the simulated entity.
	 * 
	 * @param _stepInterval
	 *            The amount of time between steps for this steppable.
	 */
	void step(Duration _stepInterval);
}
