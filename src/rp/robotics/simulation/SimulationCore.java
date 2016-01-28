package rp.robotics.simulation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import rp.util.Rate;

/**
 * Class to manage the core components of the simulation to ensure they run in
 * sync. All steppables are run in the same thread, minimising the damage thread
 * contention can do to the simulation.
 * 
 * @author Nick Hawes
 *
 */
public class SimulationCore extends Thread {

	private class SteppableWrapper {

		private final int m_stepRatio;
		private final ArrayList<SimulationSteppable> m_steppables;
		private int countDown;

		public SteppableWrapper(int _stepRatio, SimulationSteppable _steppable) {
			super();
			m_stepRatio = _stepRatio;
			m_steppables = new ArrayList<SimulationSteppable>();
			m_steppables.add(_steppable);
			countDown = m_stepRatio;
		}

		public void step(Duration _stepInterval) {
			// System.out.println(countDown);

			if (--countDown == 0) {

				Duration interval = _stepInterval.multipliedBy(m_stepRatio);

				for (Iterator<SimulationSteppable> iterator = m_steppables
						.iterator(); iterator.hasNext();) {
					SimulationSteppable steppable = iterator.next();
					if (steppable.remove()) {

						iterator.remove();
						synchronized (steppable) {
							steppable.notifyAll();
						}

					} else {
						// System.out.println("wrapper step");
						steppable.step(interval);
					}
				}

				countDown = m_stepRatio;
			}
		}

	}

	private static SimulationCore m_core;
	private final LinkedList<SteppableWrapper> m_wrappers = new LinkedList<SteppableWrapper>();

	// void add

	public static SimulationCore getSimulationCore() {
		if (m_core == null) {
			m_core = new SimulationCore();
		}
		return m_core;
	}

	private final double m_targetRate;

	public SimulationCore() {
		setDaemon(true);
		setPriority(MAX_PRIORITY);
		m_targetRate = 60;
		start();
	}

	public double getSimulationRate() {
		return m_targetRate;
	}

	/**
	 * 
	 * Add a steppable entity to the simulation that runs at a configurable rate
	 * compared with the base rate.
	 * 
	 * @param _steppable
	 * @param _stepRate
	 *            The multiple of the target rate to run at. 1 runs every
	 *            simulation step, 2 runs every other step etc.
	 */
	public void addSteppable(SimulationSteppable _steppable, int _stepRate) {
		if (_stepRate < 1) {
			System.out.println("Invalid step rate");
			_stepRate = 1;
		}

		synchronized (m_wrappers) {
			int count = 0;
			for (SteppableWrapper wrapper : m_wrappers) {
				if (_stepRate < wrapper.m_stepRatio) {
					m_wrappers.add(count, new SteppableWrapper(_stepRate,
							_steppable));
					return;
				} else if (_stepRate == wrapper.m_stepRatio) {
					wrapper.m_steppables.add(_steppable);
					return;
				} else {
					count++;
				}
			}

			// if we got to the end of the list, add it there
			m_wrappers.add(count, new SteppableWrapper(_stepRate, _steppable));
		}

	}

	public void addSteppable(SimulationSteppable _steppable) {
		addSteppable(_steppable, 1);
	}

	// public double getCycleTime() {
	// return TemporalA
	// }

	@Override
	public void run() {
		Rate r = new Rate(m_targetRate);
		Duration stepTime = Duration.ofMillis((long) (1000 / m_targetRate));

		long lastTime = System.currentTimeMillis();
		long currentTime = System.currentTimeMillis();
		long doubleStepTimeMillis = stepTime.toMillis() * 2;

		while (true) {

			try {
				synchronized (m_wrappers) {
					for (SteppableWrapper wrapper : m_wrappers) {
						wrapper.step(stepTime);
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}

			currentTime = System.currentTimeMillis();
			if (currentTime - lastTime > doubleStepTimeMillis) {
				System.out.println("Simulation update duration was "
						+ (currentTime - lastTime)
						+ "ms which more than double the target of "
						+ stepTime.toMillis() + "ms");
			}
			lastTime = currentTime;

			r.sleep();
		}

	}

	public void waitSteppable(SimulationSteppable _steppable) {
		waitSteppable(_steppable, 1);
	}

	/**
	 * Adds a steppable to the simulation then waits until it is removed again.
	 * 
	 * @param _steppable
	 * @param _stepRate
	 */
	public void waitSteppable(SimulationSteppable _steppable, int _stepRate) {

		addSteppable(_steppable, _stepRate);

		while (!_steppable.remove()) {
			synchronized (_steppable) {
				try {
					// wait on a loop as it helps deal with sync errors
					_steppable.wait(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
