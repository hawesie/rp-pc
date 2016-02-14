package rp.robotics.simulation;

import java.time.Duration;
import java.time.Instant;

import lejos.robotics.navigation.Pose;

public abstract class AbstractPoseMove implements Movable {

	protected Pose m_pose;
	protected boolean m_remove = false;

	public AbstractPoseMove(Pose _pose) {
		m_pose = _pose;
		setPose(_pose);
	}

	@Override
	public Pose getPose() {

		if (m_pose == null) {
			return null;
		} else {
			synchronized (m_pose) {
				return new Pose(m_pose.getX(), m_pose.getY(),
						m_pose.getHeading());
			}
		}
	}

	/**
	 * Move the movable to this pose. Restarts movement from the given pose.
	 */
	@Override
	public void setPose(Pose _pose) {
		if (m_pose == null) {
			m_pose = _pose;
		} else {
			synchronized (m_pose) {
				m_pose = _pose;
			}
		}
	}

	@Override
	public boolean remove() {
		return m_remove;
	}

	@Override
	public void step(Instant _now, Duration _stepInterval) {
		if (m_pose != null) {
			synchronized (m_pose) {
				moveStep(_stepInterval);
			}

		} else {
			System.out.println("Cannot execute a move with a null pose");
			m_remove = true;
		}
	}

	protected abstract void moveStep(Duration _stepInterval);
}