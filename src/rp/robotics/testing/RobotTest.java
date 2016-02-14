package rp.robotics.testing;

import java.time.Duration;
import java.util.ArrayList;

import rp.robotics.MobileRobot;
import rp.robotics.MobileRobotWrapper;
import rp.robotics.simulation.MapBasedSimulation;
import rp.robotics.simulation.SimulatorListener;
import rp.systems.StoppableRunnable;

public abstract class RobotTest<C extends StoppableRunnable> implements
		Runnable {

	protected C m_controller;
	protected MobileRobot m_poser;
	protected Duration m_timeout;
	private ArrayList<SimulatorListener> m_simulatorListeners;
	protected final MapBasedSimulation m_sim;

	public RobotTest(MapBasedSimulation _sim, C _controller,
			MobileRobot _poser, long _timeout) {
		this(_sim, _controller, _poser, Duration.ofMillis(_timeout));
	}

	public RobotTest(MapBasedSimulation _sim, C _controller,
			MobileRobot _poser, Duration _timeout) {
		m_sim = _sim;
		m_controller = _controller;
		m_poser = _poser;
		m_timeout = _timeout;
	}

	public C getController() {
		return m_controller;
	}

	public void addSimulatorListener(SimulatorListener _listener) {
		if (m_simulatorListeners == null) {
			m_simulatorListeners = new ArrayList<SimulatorListener>();
		}
		m_simulatorListeners.add(_listener);
	}

	protected void callListenersControllerStopped(MobileRobot _robot,
			long _responseTime) {
		if (m_simulatorListeners != null) {
			synchronized (m_simulatorListeners) {
				for (SimulatorListener listener : m_simulatorListeners) {
					listener.controllerStopped(_robot, _responseTime);
				}
			}
		}
	}

	public MobileRobot getPoseProvider() {
		return m_poser;
	}

	public MapBasedSimulation getSimulation() {
		return m_sim;
	}

}