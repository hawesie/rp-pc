package rp.robotics.simulation;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;

import rp.util.Pair;
import rp.util.SynchronisedMethodCall;

public class SynchronisedMotor extends SimulatedMotor {

	public static Pair<SynchronisedMotor, SynchronisedMotor> createMotorPair(
			SimulationCore _sim) {
		return Pair.makePair(new SynchronisedMotor(_sim),
				new SynchronisedMotor(_sim));
	}

	private static int m_motorCount = 0;

	private static Object m_syncLock = new Object();

	private static final HashMap<String, SynchronisedMethodCall> m_calls = new HashMap<>();

	private SynchronisedMotor(SimulationCore _sim) {
		super(_sim);
		synchronized (m_syncLock) {
			m_motorCount++;
		}
	}

	private static MethodHandle lookupMethod(String _name, MethodType _type) {
		MethodHandle mh = null;
		try {
			// This is the only way to call a super method by reflection!
			mh = MethodHandles.lookup().findSpecial(SimulatedMotor.class,
					_name, _type, SynchronisedMotor.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mh;
	}

	@Override
	public void setSpeed(int _speed) {
		callSynchronisedMethod("setSpeed",
				MethodType.methodType(void.class, int.class), this, _speed);

	}

	@Override
	public void forward() {
		callSynchronisedMethod("forward", MethodType.methodType(void.class),
				this);
	}

	@Override
	public void backward() {
		callSynchronisedMethod("backward", MethodType.methodType(void.class),
				this);
	}

	@Override
	public void stop() {
		callSynchronisedMethod("backward", MethodType.methodType(void.class),
				this);
	}

	@Override
	public void rotate(int _angle, boolean _immediateReturn) {
		callSynchronisedMethod("rotate",
				MethodType.methodType(void.class, int.class, boolean.class),
				this, _angle, _immediateReturn);
	}

	private static void callSynchronisedMethod(String _methodName,
			MethodType _type, Object... _parameters) {

		synchronized (m_syncLock) {

			MethodHandle mh = lookupMethod(_methodName, _type);
			assert mh != null;

			// System.out.println(m_motorCount);
			// System.out.println(_parameters);

			SynchronisedMethodCall method;
			if (!m_calls.containsKey(_methodName)) {

				SynchronisedMotor m = (SynchronisedMotor) _parameters[0];
				m.getSim().waitForEndOfStep();
				// m.getSim().pause();

				method = new SynchronisedMethodCall(mh, m_motorCount,
						_parameters);

				m_calls.put(_methodName, method);
			} else {
				// System.out.println("adding");
				method = m_calls.get(_methodName);
				method.add(mh, _parameters);
			}

			// System.out.println("Syn Motor Calls: " + m_calls.size());

			if (method.call()) {
				m_calls.remove(mh);
				SynchronisedMotor m = (SynchronisedMotor) _parameters[0];
				// m.getSim().unpause();
				// System.out.println("Removing: " + mh);
			}

		}
	}

	public static void main(String[] args) {
		Pair<SynchronisedMotor, SynchronisedMotor> motors = createMotorPair(new SimulationCore());
		motors.getItem1().setSpeed(100);
		motors.getItem1().forward();
		motors.getItem2().setSpeed(100);
		motors.getItem2().forward();
		System.out.println("Done");

	}

}
