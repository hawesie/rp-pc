package rp.robotics.simulation;

import java.time.Duration;
import java.time.Instant;

import lejos.robotics.navigation.Pose;

/**
 * Movable that translates in the x direction back and forth around the starting
 * pose.
 * 
 * @author Nick Hawes
 *
 */
public class Rotate extends AbstractPoseMove {

	private float m_speed;
	private float m_degreesRemaining;

	public Rotate(Pose _pose, float _speed, float _rotateAmount) {
		super(_pose);

		if (_rotateAmount > 0) {
			m_speed = _speed;
			m_degreesRemaining = _rotateAmount;

		} else {
			m_speed = -_speed;
			m_degreesRemaining = -_rotateAmount;
		}
	}

	public Rotate(float _speed, float _rotateAmount) {
		this(null, _speed, _rotateAmount);
	}

	protected void moveStep(Instant _now, Duration _stepInterval) {

		float durationSecs = _stepInterval.toMillis() / 1000f;
		float moveAmount = m_speed * durationSecs;
		m_pose.rotateUpdate(moveAmount);
		m_degreesRemaining -= Math.abs(moveAmount);

		if (m_degreesRemaining <= 0) {
			m_remove = true;
		}
	}

}
