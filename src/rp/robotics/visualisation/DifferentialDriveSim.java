package rp.robotics.visualisation;

import javax.swing.JFrame;

import lejos.robotics.RangeFinder;
import lejos.robotics.navigation.Pose;
import rp.config.WheeledRobotConfiguration;
import rp.robotics.DifferentialDriveRobot;
import rp.robotics.MobileRobotWrapper;
import rp.robotics.control.RandomWalkController;
import rp.robotics.simulation.MapBasedSimulation;
import rp.robotics.simulation.SimulatedRobots;
import rp.robotics.testing.TestMaps;

/**
 * Demonstrates the use, and visualisation, of a simulated simple robot on a
 * line map.
 * 
 * @author Nick Hawes
 *
 */
public class DifferentialDriveSim {

	public void run() {

		// Create the simulation using the given map. This simulation object can
		// run with or without a GUI. In this case we use an empty map which is
		// 8m by 6m.
		MapBasedSimulation sim = new MapBasedSimulation(TestMaps.EMPTY_8_x_6);

		// Create a configuration object to describe to the kind of robot you
		// want to add to the simulation.
		// The dimensions of the simulated robot are defined in metres, thus all
		// other parts of your code should use metres too.
		// Note that it is also possible to create a configuration to describe a
		// real robot and use that with the same control code on real hardware.
		boolean withTouchSensor = true;
		boolean withRangeSensor = true;
		WheeledRobotConfiguration robotConfig = SimulatedRobots
				.makeWheeledConfiguration(sim.getSimulationCore(),
						withTouchSensor, withRangeSensor);

		// Add a robot with this configuration to the simulation at the given
		// starting pose. The return value from addRobot is the object you use
		// to actually control the
		// simulated robot.
		Pose startingPose = new Pose(0.5f, 0.5f, 0);
		MobileRobotWrapper<DifferentialDriveRobot> wrapper = sim.addRobot(
				robotConfig, startingPose);

		// This object implements a simple random move controller for a wheeled
		// robot. We pass it the robot object from the simulator.
		// It is important to note that this controller could also be used with
		// a real robot provided you have a configuration object to describe it.
		RandomWalkController controller = new RandomWalkController(
				wrapper.getRobot());

		// This call attaches the event listener implemented by the controller
		// to the touch sensor on the simulated robot
		sim.addTouchSensorListener(wrapper, controller);

		// This gets the sensor used for range measurement on the simulated
		// robot. This object implements the RangeFinder method which is also
		// implemted by range sensors on the real robot
		RangeFinder ranger = sim.getRanger(wrapper);

		// By passing this object to the controller it can now measure the
		// distance to walls
		controller.setRangeScanner(ranger);

		// We can now create a JComponent that renders the map, robots etc. for
		// visualisation.
		// This is not needed to run the simulation, only if you want to see
		// what is going on.
		MapVisualisationComponent viz = MapVisualisationComponent
				.createFromSimulation(sim);

		// Add the visualisation to a JFrame to display it
		displayVisualisation(viz);

		// Start the controller running in a new thread
		Thread controllerThread = new Thread(controller);
		controllerThread.start();

	}

	public static JFrame displayVisualisation(MapVisualisationComponent viz) {
		// Create a frame to contain the viewer
		JFrame frame = new JFrame("Simulation Viewer");

		// Add visualisation to frame
		frame.add(viz);
		frame.pack();
		frame.setSize(viz.getMinimumSize());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		return frame;
	}

	public static void main(String[] args) {
		DifferentialDriveSim demo = new DifferentialDriveSim();
		demo.run();
	}

}
