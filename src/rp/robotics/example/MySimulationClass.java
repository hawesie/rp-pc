package rp.robotics.example;

import lejos.robotics.navigation.Pose;
import rp.robotics.DifferentialDriveRobot;
import rp.robotics.MobileRobotWrapper;
import rp.robotics.simulation.MapBasedSimulation;
import rp.robotics.simulation.SimulatedRobots;
import rp.robotics.testing.TestMaps;
import rp.robotics.visualisation.DifferentialDriveSim;
import rp.robotics.visualisation.MapVisualisationComponent;

/**
 * An example of a movable-based robot simulation.
 * 
 * @author Nick Hawes
 *
 */
public class MySimulationClass {

	public void run() {

		// Create the simulation using the given map. This simulation can run
		// with a GUI.
		MapBasedSimulation sim = new MapBasedSimulation(TestMaps.EMPTY_8_x_6);

		// Add a robot of a given configuration to the simulation. The return
		// value is the object you can use to control the robot.
		//
		// The dimensions of the simulated robot are defined in metres, thus all
		// other parts of your code should use metres too.
		MobileRobotWrapper<DifferentialDriveRobot> wrapper = sim.addRobot(
				SimulatedRobots.EXPRESS_BOT_WITH_SENSORS, new Pose(3f, 3f, 0));

		sim.getRanger(wrapper);

		// This is the controller that actually makes the robot move
		MyRobotController controller = new MyRobotController(wrapper.getRobot());

		// Create visualisation JComponent that renders map, robots etc
		MapVisualisationComponent viz = MapVisualisationComponent
				.createFromSimulation(sim);

		// Add the visualisation to a JFrame to display it
		DifferentialDriveSim.displayVisualisation(viz);

		// Start the controller running -- this should move your robot
		controller.run();

	}

	public static void main(String[] args) {
		MySimulationClass demo = new MySimulationClass();
		demo.run();
	}

}
