package rp.robotics.simulation;

import java.time.Duration;
import java.time.Instant;

import lejos.robotics.navigation.Pose;

public class NoOpMovable implements Movable {

	private Pose m_pose;

	@Override
	public Pose getPose() {
		return m_pose;
	}

	public NoOpMovable() {
		this(new Pose());
	}
	
	public NoOpMovable(Pose _pose) {
		m_pose = _pose;
	}

	@Override
	public void setPose(Pose _pose) {
		m_pose = _pose;
	}

	@Override
	public boolean remove(Instant _now, Duration _stepInterval) {
		return true;
	}

	@Override
	public void step(Instant _now, Duration _stepInterval) {

	}

}
