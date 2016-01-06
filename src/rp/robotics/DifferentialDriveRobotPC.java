package rp.robotics;

import java.util.UUID;

import rp.config.WheeledRobotConfiguration;

/**
 * And extention of the shared DifferentialDriveRobot class which adds extra
 * functionality useful for managing multiple robots from a PC with more Java
 * classes.
 * 
 * @author nah
 *
 */
public class DifferentialDriveRobotPC extends
		rp.robotics.DifferentialDriveRobot implements
		Comparable<DifferentialDriveRobotPC> {

	private final UUID m_uuid = UUID.randomUUID();

	public DifferentialDriveRobotPC(WheeledRobotConfiguration _config) {
		super(_config);
	}

	@Override
	public int compareTo(DifferentialDriveRobotPC _that) {
		return this.m_uuid.compareTo(_that.m_uuid);
	}

	@Override
	public boolean equals(Object _that) {
		if (_that instanceof DifferentialDriveRobotPC) {
			return this.m_uuid
					.equals(((DifferentialDriveRobotPC) _that).m_uuid);
		} else {
			return false;
		}
	}

}
