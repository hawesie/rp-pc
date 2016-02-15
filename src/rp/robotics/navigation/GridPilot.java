package rp.robotics.navigation;

import rp.robotics.localisation.GridPoseProvider;
import rp.robotics.mapping.GridMap;
import rp.robotics.simulation.Drive;
import rp.robotics.simulation.Movable;
import rp.robotics.simulation.MovablePilot;
import rp.robotics.simulation.Rotate;

/**
 * Uses the {@link Movable} approach to drive a robot around a grid.
 * 
 * @author Nick Hawes
 *
 */
public class GridPilot implements GridPoseProvider, IGridPilot {

	private final GridMap m_map;
	private final MovablePilot m_pilot;
	private GridPose m_pose;
	private float m_turnSpeed = 30f;
	private float m_travelSpeed = 0.2f;

	public GridPilot(MovablePilot _robot, GridMap _map, GridPose _start) {
		m_map = _map;
		m_pilot = _robot;
		m_pose = _start;
	}

	@Override
	public synchronized void moveForward() {
		m_pilot.executeMove(new Drive(m_travelSpeed, m_map.getCellSize()));
		m_pose.moveUpdate();
		m_pilot.setPose(m_map.toPose(m_pose));
	}

	public synchronized float getTurnSpeed() {
		return m_turnSpeed;
	}

	public synchronized void setTurnSpeed(float _turnSpeed) {
		m_turnSpeed = _turnSpeed;
	}

	public synchronized float getTravelSpeed() {
		return m_travelSpeed;
	}

	public synchronized void setTravelSpeed(float _travelSpeed) {
		m_travelSpeed = _travelSpeed;
	}

	@Override
	public synchronized void rotatePositive() {
		rotate(90);
	}

	@Override
	public synchronized void rotateNegative() {
		rotate(-90);

	}

	private void rotate(int _amount) {
		m_pilot.executeMove(new Rotate(m_turnSpeed, _amount));
		m_pose.rotateUpdate(_amount);
	}

	@Override
	public synchronized GridPose getGridPose() {
		return m_pose;
	}

	@Override
	public synchronized void setGridPose(GridPose _pose) {
		m_pose = _pose;
	}

}
