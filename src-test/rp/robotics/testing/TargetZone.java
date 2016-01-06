package rp.robotics.testing;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;

/**
 * This represents a target we expect a robot to drive into.
 * 
 * @author Nick Hawes
 *
 */
public class TargetZone {

	public enum Status {
		NEUTRAL,
		// the next target
		LIVE,
		// when robot has arrived
		HIT
	}

	private final Point m_target;
	private final float m_radius;
	private Status m_status = Status.NEUTRAL;

	public TargetZone(Point _target, float _radius) {
		m_target = _target;
		m_radius = _radius;
	}

	public float getRadius() {
		return m_radius;
	}

	public Point getTarget() {
		return m_target;
	}

	/**
	 * Returns true if the given pose is in the zone.
	 * 
	 * @param _p
	 * @return
	 */
	public boolean inZone(Pose _p) {
		return _p.distanceTo(getTarget()) < getRadius();
	}

	public Status getStatus() {
		return m_status;
	}

	public void setStatus(Status _status) {
		m_status = _status;
	}
}
