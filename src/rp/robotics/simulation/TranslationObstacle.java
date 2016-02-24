package rp.robotics.simulation;

import java.time.Duration;
import java.time.Instant;

import lejos.geom.Line;
import lejos.robotics.navigation.Pose;

/**
 * Single straight line that translates according to some simple rules.
 * 
 * @author Nick Hawes
 *
 */
public class TranslationObstacle implements DynamicObstacle {

	private final Line[] m_footprint;
	private Movable m_mover;

	/**
	 * Starting pose and footprint of obstacle relative to pose.
	 * 
	 * @param _startingPose
	 * @param _footprint
	 */
	public TranslationObstacle(Line[] _footprint, Movable _mover) {
		m_footprint = _footprint;
		m_mover = _mover;
	}

	@Override
	public Pose getPose() {
		return m_mover.getPose();
	}

	@Override
	public void setPose(Pose _pose) {
		m_mover.setPose(_pose);
	}

	@Override
	public Line[] getFootprint() {
		return m_footprint;
	}

	@Override
	public boolean remove(Instant _now, Duration _stepInterval) {
		return m_mover.remove(_now, _stepInterval);
	}

	@Override
	public void step(Instant _now, Duration _stepInterval) {
		m_mover.step(_now, _stepInterval);
	}

}
