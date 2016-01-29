package rp.robotics.simulation;

import java.time.Duration;
import java.time.Instant;

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
	 * @param _now
	 *            When the step is called in the simulation
	 * 
	 * @param _stepInterval
	 *            The amount of time between steps for this steppable.
	 */
	void step(Instant _now, Duration _stepInterval);
}
