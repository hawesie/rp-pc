package rp.assignments;

import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lejos.robotics.navigation.Pose;
import rp.robotics.DifferentialDriveRobotPC;
import rp.robotics.simulation.MapBasedSimulation;
import rp.robotics.simulation.SimulatedRobots;
import rp.robotics.testing.TestMaps;
import rp.robotics.testing.ZoneSequence;
import rp.robotics.testing.ZoneSequenceTest;
import rp.systems.StoppableRunnable;

public class AbstractTestHarness {

	private final Class<?> m_solutionCls;

	public AbstractTestHarness(String _solutionFactoryClassName) {
		Class<?> solutionCls = null;
		try {
			solutionCls = Class.forName(_solutionFactoryClassName);
		} catch (ClassNotFoundException e) {
			fail("Solution factory class \"" + _solutionFactoryClassName
					+ "\" not found for testing");
		}
		m_solutionCls = solutionCls;
	}

	/**
	 * Returns the StoppableRunnable controller from the class provided in the
	 * constructor.
	 * 
	 * @param _string
	 * @param _args
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public StoppableRunnable getController(String _string, Object... _args) {

		try {

			Method[] methods = m_solutionCls.getMethods();

			Method getContollerMethod = findMatchingMethod(methods, _string,
					_args);

			if (getContollerMethod == null) {
				fail("method " + _string + " not found in class "
						+ m_solutionCls.getName());
				return null;
			}

			Object controller = null;
			try {
				controller = getContollerMethod.invoke(null, _args);
			} catch (InvocationTargetException e) {
				throw e.getCause();
			}

			StoppableRunnable casted = StoppableRunnable.class.cast(controller);
			if (casted == null) {
				fail(m_solutionCls.getName() + "." + _string
						+ " returned a null controller, so failing");
			}

			return casted;

		} catch (Throwable e) {
			fail(e.getClass().getName() + ": " + e.getMessage());
			return null;
		}

	}

	private Method findMatchingMethod(Method[] _methods, String _name,
			Object... _args) {
		Method getContollerMethod = null;
		for (int i = 0; i < _methods.length; i++) {
			Method method = _methods[i];
			if (method.getName().equals(_name)) {
				if (method.getParameterTypes().length == _args.length) {
					int paramsMatched = 0;
					for (int j = 0; j < _args.length; j++) {
						if (method.getParameterTypes()[j]
								.isAssignableFrom(_args[j].getClass())) {
							paramsMatched++;
						} else {
							break;
						}
					}
					if (paramsMatched == _args.length) {
						getContollerMethod = method;
						break;
					}
				}
			}
		}
		return getContollerMethod;
	}

	public void runSequenceTest(ZoneSequenceTest<?> test) {
		test.run();
	}

	public ZoneSequenceTest<DifferentialDriveRobotPC> createSequenceTest(
			ZoneSequence _sequence, long _timeoutMillis, String _method,
			Object... _args) {
		MapBasedSimulation sim = new MapBasedSimulation(TestMaps.EMPTY_8_x_6);

		Pose start = _sequence.getStart();

		DifferentialDriveRobotPC robot = sim.addRobot(
				SimulatedRobots.CASTOR_BOT, start);

		Object[] args = new Object[_args.length + 1];
		args[0] = robot;
		for (int i = 1; i < args.length; i++) {
			args[i] = _args[i - 1];
		}

		StoppableRunnable controller = getController(_method, args);

		ZoneSequenceTest<DifferentialDriveRobotPC> test = new ZoneSequenceTest<DifferentialDriveRobotPC>(
				_sequence, controller, robot, _timeoutMillis, false);
		return test;
	}

}
