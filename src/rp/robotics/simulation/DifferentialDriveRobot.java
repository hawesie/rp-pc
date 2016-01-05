package rp.robotics.simulation;

import java.util.UUID;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Pose;
import rp.config.WheeledRobotConfiguration;
import rp.config.WheeledRobotDescription;
import rp.systems.WheeledRobotSystem;

public class DifferentialDriveRobot implements PoseProvider,
		WheeledRobotDescription, Comparable<DifferentialDriveRobot> {

	/**
	 * Configuration for the simulation of Nick's Expressbot build.
	 */
	public static final WheeledRobotConfiguration EXPRESS_BOT = new WheeledRobotConfiguration(
			0.056, 0.163, 0.210, new SimulatedMotor(), new SimulatedMotor());

	/**
	 * Configuration for the simulation of Nick's Castorbot build.
	 */
	public static final WheeledRobotConfiguration CASTOR_BOT = new WheeledRobotConfiguration(
			0.056, 0.12, 0.23, new SimulatedMotor(), new SimulatedMotor());

	private final WheeledRobotConfiguration m_config;

	private final UUID m_uuid = UUID.randomUUID();

	public double getWheelDiameter() {
		return m_config.getWheelDiameter();
	}

	public double getTrackWidth() {
		return m_config.getTrackWidth();
	}

	public RegulatedMotor getLeftWheel() {
		return m_config.getLeftWheel();
	}

	public RegulatedMotor getRightWheel() {
		return m_config.getRightWheel();
	}

	public double getRobotLength() {
		return m_config.getRobotLength();
	}

	private final DifferentialPilot m_pilot;

	private final OdometryPoseProvider m_odomPose;

	public DifferentialDriveRobot(WheeledRobotConfiguration _config) {
		m_config = _config;
		m_pilot = new WheeledRobotSystem(m_config).getPilot();
		m_odomPose = new OdometryPoseProvider(m_pilot);
	}

	public DifferentialDriveRobot() {
		this(EXPRESS_BOT);
	}

	/**
	 * Returns the ground truth pose from the simulation.
	 */
	@Override
	public Pose getPose() {
		return m_odomPose.getPose();
	}

	/***
	 * Changes the pose of the robot in the simulation.
	 */
	@Override
	public void setPose(Pose _pose) {
		m_odomPose.setPose(_pose);
	}

	public DifferentialPilot getDifferentialPilot() {
		return m_pilot;
	}

	@Override
	public int compareTo(DifferentialDriveRobot _that) {
		return this.m_uuid.compareTo(_that.m_uuid);
	}

	@Override
	public boolean equals(Object _that) {
		if (_that instanceof DifferentialDriveRobot) {
			return this.m_uuid.equals(((DifferentialDriveRobot) _that).m_uuid);
		} else {
			return false;
		}
	}

}
