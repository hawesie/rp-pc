package rp.robotics.control;

import rp.robotics.DifferentialDriveRobot;
import rp.systems.StoppableRunnable;

public interface ControllerFactory {

	public StoppableRunnable createController(int _test, DifferentialDriveRobot _config);

}
