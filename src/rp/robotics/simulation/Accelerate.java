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
public class Accelerate extends AbstractPoseMove {

	private float m_speed;
	private float m_distanceRemaining;

	public Accelerate(Pose _pose, float _startingSpeed, float _minSpeed,
			float _maxSpeed, float _acceleration, float _distance) {
		super(_pose);
		m_speed = _startingSpeed;
		m_distanceRemaining = _distance;
	}

	public Accelerate(Pose _pose, float _startingSpeed, float _acceleration,
			float _distance) {
		this(_pose, _startingSpeed, 0.01f, 0.4f, _acceleration, _distance);
	}

	protected void moveStep(Instant _now, Duration _stepInterval) {
		float durationSecs = _stepInterval.toMillis() / 1000f;
		float moveAmount = m_speed * durationSecs;
		m_pose.moveUpdate(moveAmount);
		m_distanceRemaining -= moveAmount;

		if (m_distanceRemaining <= 0) {
			m_remove = true;
		}
	}

}
