package rp.robotics.simulation;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;

/**
 * This object moves a pose around given a {@link Movable};
 * 
 * @author nah
 *
 */
public class MovablePilot implements PoseProvider {

	private boolean m_isMoving = false;
	private Movable m_movable;
	private final BlockingQueue<Movable> m_moves = new LinkedBlockingQueue<>();
	private Thread m_moveThread;

	public MovablePilot(Pose _startingPose) {
		m_movable = new NoOpMovable(_startingPose);
		m_moveThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						executeMove(m_moves.take());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		});
		m_moveThread.setDaemon(true);
		m_moveThread.start();
	}

	public MovablePilot() {
		m_movable = new NoOpMovable();
	}

	private void executeMove(Movable _move) {
		// Update the pose of the movable
		_move.setPose(m_movable.getPose());
		m_movable = _move;
		m_isMoving = true;
		SimulationCore.getSimulationCore().addAndWaitSteppable(m_movable);
		m_isMoving = false;
	}

	/**
	 * Queues up a move for the pilot.
	 * 
	 * @param _move
	 */
	public void addMove(Movable _move) {
		try {
			m_moves.put(_move);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Pose getPose() {
		return m_movable.getPose();
	}

	@Override
	public void setPose(Pose _pose) {
		m_movable.setPose(_pose);
	}

	public boolean isMoving() {
		return m_isMoving;
	}
}
