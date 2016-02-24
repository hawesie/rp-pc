package rp.robotics.simulation;

import java.time.Duration;
import java.time.Instant;

public interface SimulationSteppable {

	/**
	 * When this returns true it is removed from the simulation.
	 * 
	 * @param _now
	 *            When the step is called in the simulation
	 * 
	 * @param _stepInterval
	 *            The amount of time between this step and the previous step in
	 *            the simulation.
	 */
	boolean remove(Instant _now, Duration _stepInterval);

	/**
	 * Run a single step of the simulated entity.
	 * 
	 * @param _now
	 *            When the step is called in the simulation
	 * 
	 * @param _stepInterval
	 *            The amount of time between this step and the previous step in
	 *            the simulation.
	 */
	void step(Instant _now, Duration _stepInterval);
}
