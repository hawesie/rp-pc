package rp.robotics.visualisation;

import javax.swing.JFrame;

import lejos.robotics.RangeFinder;
import lejos.robotics.navigation.Pose;
import rp.robotics.DifferentialDriveRobotPC;
import rp.robotics.control.RandomWalk;
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

		// Create the simulation using the given map. This simulation can run
		// with a GUI.
		MapBasedSimulation sim = new MapBasedSimulation(TestMaps.EMPTY_1_x_1);

		// Add a robot of a given configuration to the simulation. The return
		// value is the object you can use to control the robot.
		//
		// The dimensions of the simulated robot are defined in metres, thus all
		// other parts of your code should use metres too.
		DifferentialDriveRobotPC robot = sim.addRobot(
				SimulatedRobots.EXPRESS_BOT_WITH_SENSORS, new Pose(0.5f, 0.5f,
						0));

		RandomWalk controller = new RandomWalk(robot);
		sim.addTouchSensorListener(robot, controller);
		RangeFinder ranger = sim.getRanger(robot);
		controller.setRangeScanner(ranger);

		// ForwardBackwards controller = new ForwardBackwards(robot);
		// Create visualisation JComponent that renders map, robots etc
		MapVisualisationComponent viz = MapBasedSimulation
				.createVisulation(sim);

		// Add the visualisation to a JFrame to display it
		displayVisualisation(viz);

		controller.run();

	}

	private void displayVisualisation(MapVisualisationComponent viz) {
		// Create a frame to contain the viewer
		JFrame frame = new JFrame("Map Viewer");

		// Add visualisation to frame
		frame.add(viz);
		frame.addWindowListener(new KillMeNow());

		frame.pack();
		frame.setSize(viz.getMinimumSize());
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		DifferentialDriveSim demo = new DifferentialDriveSim();
		demo.run();
	}

}
