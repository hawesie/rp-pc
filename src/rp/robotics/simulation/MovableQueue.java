package rp.robotics.simulation;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;

import lejos.robotics.navigation.Pose;

/**
 * Execute a sequence of moves
 * 
 * @author Nick Hawes
 *
 */
public class MovableQueue extends AbstractPoseMove {

	private final Queue<Movable> m_moves = new LinkedList<>();
	private Movable m_current;

	/**
	 * Construct a queue of moves to exectue as a single move. Any poses set in
	 * the provided moves will be ignored. Instead each move will start from the
	 * pose the previous one finished at.
	 * 
	 * @param _pose
	 * @param _movables
	 */
	public MovableQueue(Pose _pose, Movable... _movables) {
		super(_pose);
		for (Movable m : _movables) {
			m_moves.add(m);
		}
	}

	@Override
	protected void moveStep(Instant _now, Duration _stepInterval) {
		if (m_current == null && m_moves.isEmpty()) {
			// System.out.println("Done: " + m_current);
			m_remove = true;
		} else if (m_current == null) {
			m_current = m_moves.poll();
			// System.out.println("Next move: " + m_current);
			m_current.setPose(getPose());
		}

		if (!m_remove) {
			if (m_current.remove(_now, _stepInterval)) {
				m_current = null;
			} else {
				m_current.step(_now, _stepInterval);
				setPose(m_current.getPose());
			}
		}
	}

}
