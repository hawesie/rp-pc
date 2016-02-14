package rp.robotics;

import java.util.UUID;

/**
 * A wrapper which associates a robot with a UUID.
 * 
 * @author nah
 *
 */
public class MobileRobotWrapper<R extends MobileRobot> implements
		Comparable<MobileRobotWrapper<R>> {

	private final UUID m_uuid = UUID.randomUUID();

	private final R m_robot;

	public MobileRobotWrapper(R _robot) {
		m_robot = _robot;
	}

	@Override
	public int compareTo(MobileRobotWrapper<R> _that) {
		return this.m_uuid.compareTo(_that.m_uuid);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object _that) {
		if (_that instanceof MobileRobotWrapper) {
			return this.m_uuid.equals(((MobileRobotWrapper) _that).m_uuid);
		} else {
			return false;
		}
	}

	public R getRobot() {
		return m_robot;
	}
}
