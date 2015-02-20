package rp.robotics.simulation;

import lejos.robotics.RangeFinder;
import lejos.robotics.RangeReadings;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;
import rp.robotics.LocalisedRangeScanner;

/**
 * A simple simulated robot for testing.
 * 
 * @author nah
 *
 */
public class SimulatedRobot implements LocalisedRangeScanner {

	private Pose m_pose;
	private LineMap m_map;
	private RangeReadings m_readings;
	private float[] m_readingAngles;
	private boolean m_needReadings = true;

	/**
	 * 
	 * @param _pose
	 *            The initial pose of the robot
	 * @param _map
	 *            The map on which the robot exists
	 * @param _readingAngles
	 *            The angles to take readings from, relative to 0 for this robot
	 */
	public SimulatedRobot(Pose _pose, LineMap _map, float[] _readingAngles) {
		m_pose = _pose;
		m_map = _map;
		m_readings = new RangeReadings(_readingAngles.length);
		m_readingAngles = _readingAngles;
		takeReadings();
	}

	public SimulatedRobot(Pose _pose, LineMap _map) {
		this(_pose, _map, new float[] {});
	}

	@Override
	public Pose getPose() {
		return m_pose;
	}

	@Override
	public void setPose(Pose _pose) {
		m_pose = _pose;
	}

	/**
	 * Obtain range readings from the map at the predefined angles relatve to
	 * the robot
	 */
	private void takeReadings() {

		// the pose to use for taking range readings
		Pose readingPose = new Pose(m_pose.getX(), m_pose.getY(),
				m_pose.getHeading());

		for (int i = 0; i < m_readingAngles.length; i++) {

			// rotate the reading pose to the angle of the sensor
			readingPose.setHeading(m_pose.getHeading() + m_readingAngles[i]);

			// and take a reading from there
			m_readings
					.setRange(i, m_readingAngles[i], m_map.range(readingPose));
			System.out.println(m_map.range(readingPose));
		}

		m_needReadings = false;
	}

	@Override
	public RangeReadings getRangeValues() {
		if (m_needReadings) {
			takeReadings();
		}
		return m_readings;
	}

	@Override
	public void setAngles(float[] _angles) {
		// does nothing as these are fixed in constructor
	}

	@Override
	public RangeFinder getRangeFinder() {
		// returns null as there is no actual reading
		return null;
	}

	/***
	 * Rotate the robot by this angle.
	 * 
	 * @param _angle
	 */
	public void rotate(int _angle) {
		m_pose.rotateUpdate(_angle);
		m_needReadings = true;
	}
}
