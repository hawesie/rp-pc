package rp.robotics.simulation;

import rp.config.MobileRobotConfiguration;
import rp.config.WheeledRobotConfiguration;
import rp.util.Pair;

public class SimulatedRobots {

	// /**
	// * Configuration for the simulation of Nick's Expressbot build.
	// */
	// public static final WheeledRobotConfiguration EXPRESS_BOT = new
	// WheeledRobotConfiguration(
	// 0.056f, 0.163f, 0.210f, new SimulatedMotor(), new SimulatedMotor());
	// /**
	// * Configuration for the simulation of Nick's Castorbot build.
	// */
	// public static final WheeledRobotConfiguration CASTOR_BOT = new
	// WheeledRobotConfiguration(
	// 0.056f, 0.12f, 0.23f, new SimulatedMotor(), new SimulatedMotor());
	//
	// /**
	// * Configuration for the simulation of Nick's Expressbot build with range
	// * and touch sensors.
	// */
	// public static final WheeledRobotConfiguration EXPRESS_BOT_WITH_SENSORS =
	// new WheeledRobotConfiguration(
	// 0.056f, 0.163f, 0.210f, new SimulatedMotor(), new SimulatedMotor());
	// /**
	// * Configuration for the simulation of Nick's Castorbot build with range
	// and
	// * touch sensors.
	// */
	// public static final WheeledRobotConfiguration CASTOR_BOT_WITH_SENSORS =
	// new WheeledRobotConfiguration(
	// 0.056f, 0.12f, 0.23f, new SimulatedMotor(), new SimulatedMotor());
	//
	// public static final WheeledRobotConfiguration CASTOR_BOT_WITH_BUMPER =
	// new WheeledRobotConfiguration(
	// 0.056f, 0.12f, 0.23f, new SimulatedMotor(), new SimulatedMotor());
	//
	// static {
	// EXPRESS_BOT_WITH_SENSORS.addTouchSensor();
	// EXPRESS_BOT_WITH_SENSORS.addRangeScanner();
	// CASTOR_BOT_WITH_SENSORS.addTouchSensor();
	// CASTOR_BOT_WITH_SENSORS.addRangeScanner();
	// CASTOR_BOT_WITH_BUMPER.addTouchSensor();
	// }

	public static WheeledRobotConfiguration makeWheeledConfiguration(
			SimulationCore _sim, boolean _touchSensor, boolean _rangeSensor) {

		Pair<SynchronisedMotor, SynchronisedMotor> motors = SynchronisedMotor
				.createMotorPair(_sim);
		WheeledRobotConfiguration config = new WheeledRobotConfiguration(
				0.056f, 0.12f, 0.23f, motors.getItem1(), motors.getItem2());

		if (_touchSensor) {
			config.addTouchSensor();
		}
		if (_rangeSensor) {
			config.addRangeScanner();
		}
		return config;
	}

	public static MobileRobotConfiguration makeConfiguration(
			boolean _touchSensor, boolean _rangeSensor) {
		MobileRobotConfiguration config = new MobileRobotConfiguration(0.12f,
				0.23f);

		if (_touchSensor) {
			config.addTouchSensor();
		}
		if (_rangeSensor) {
			config.addRangeScanner();
		}
		return config;
	}

}
