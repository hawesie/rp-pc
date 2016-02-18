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

	/**
	 * Drive forward from the given pose at the given speed for the given
	 * distance.
	 * 
	 * @param _pose
	 * @param _speed
	 * @param _distance
	 */
	public Drive(Pose _pose, float _speed, float _distance) {
		super(_pose);
		m_speed = _speed;
		m_distanceRemaining = _distance;
	}

	/**
	 * Drive forward at the given speed for the given distance. This assumes
	 * setPose is called to provide a starting pose before movement.
	 * 
	 * @param _speed
	 * @param _distance
	 */

	public Drive(float _speed, float _distance) {
		this(null, _speed, _distance);
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
