package rp.robotics.simulation;

import rp.config.WheeledRobotConfiguration;

public class SimulatedRobots {

	/**
	 * Configuration for the simulation of Nick's Expressbot build.
	 */
	public static final WheeledRobotConfiguration EXPRESS_BOT = new WheeledRobotConfiguration(
			0.056f, 0.163f, 0.210f, new SimulatedMotor(), new SimulatedMotor());
	/**
	 * Configuration for the simulation of Nick's Castorbot build.
	 */
	public static final WheeledRobotConfiguration CASTOR_BOT = new WheeledRobotConfiguration(
			0.056f, 0.12f, 0.23f, new SimulatedMotor(), new SimulatedMotor());

	/**
	 * Configuration for the simulation of Nick's Expressbot build with range
	 * and touch sensors.
	 */
	public static final WheeledRobotConfiguration EXPRESS_BOT_WITH_SENSORS = new WheeledRobotConfiguration(
			0.056f, 0.163f, 0.210f, new SimulatedMotor(), new SimulatedMotor());
	/**
	 * Configuration for the simulation of Nick's Castorbot build with range and
	 * touch sensors.
	 */
	public static final WheeledRobotConfiguration CASTOR_BOT_WITH_SENSORS = new WheeledRobotConfiguration(
			0.056f, 0.12f, 0.23f, new SimulatedMotor(), new SimulatedMotor());

	static {
		EXPRESS_BOT_WITH_SENSORS.addTouchSensor();
		EXPRESS_BOT_WITH_SENSORS.addRangeScanner();
		CASTOR_BOT_WITH_SENSORS.addTouchSensor();
		CASTOR_BOT_WITH_SENSORS.addRangeScanner();
	}

}
