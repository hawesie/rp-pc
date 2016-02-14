package rp.robotics.simulation;

import java.time.Duration;
import java.time.Instant;

import lejos.robotics.navigation.Pose;

/**
 * Movable that moves a pose forward, i.e. along x in the direction of the
 * heading.
 * 
 * @author Nick Hawes
 *
 */
public class Drive extends AbstractPoseMove {

	private float m_speed;
	private float m_distanceRemaining;

	public Drive(Pose _pose, float _speed, float _distance) {
		super(_pose);
		m_speed = _speed;
		m_distanceRemaining = _distance;
	}

	public Drive(float _speed, float _distance) {
		this(null, _speed, _distance);
	}

	protected void moveStep(Duration _stepInterval) {
		float durationSecs = _stepInterval.toMillis() / 1000f;
		float moveAmount = m_speed * durationSecs;
		m_pose.moveUpdate(moveAmount);
		m_distanceRemaining -= moveAmount;

		if (m_distanceRemaining <= 0) {
			m_remove = true;
		}
	}

}
