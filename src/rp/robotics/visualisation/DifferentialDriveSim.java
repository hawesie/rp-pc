package rp.robotics.visualisation;

import javax.swing.JFrame;

import lejos.robotics.RangeFinder;
import lejos.robotics.navigation.Pose;
import lejos.util.Delay;
import rp.robotics.DifferentialDriveRobot;
import rp.robotics.MobileRobotWrapper;
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
		MapBasedSimulation sim = new MapBasedSimulation(TestMaps.EMPTY_8_x_6);

		// Add a robot of a given configuration to the simulation. The return
		// value is the object you can use to control the robot.
		//
		// The dimensions of the simulated robot are defined in metres, thus all
		// other parts of your code should use metres too.
		MobileRobotWrapper<DifferentialDriveRobot> wrapper = sim.addRobot(
				SimulatedRobots.EXPRESS_BOT_WITH_SENSORS, new Pose(0.5f, 0.5f,
						0));

		// This is the controller that actually makes the robot move
		RandomWalk controller = new RandomWalk(wrapper.getRobot());

		// This call attaches an event listener to the robot's touch sensor in
		// the simulator
		sim.addTouchSensorListener(wrapper, controller);

		// This gets the object used for range measurement for the robot in the
		// simulator
		RangeFinder ranger = sim.getRanger(wrapper);

		// This gives the controller the range scanner to use.
		controller.setRangeScanner(ranger);

		// Create visualisation JComponent that renders map, robots etc
		MapVisualisationComponent viz = MapVisualisationComponent
				.createFromSimulation(sim);

		// Add the visualisation to a JFrame to display it
		displayVisualisation(viz);

		// Start the controller running

		new Thread(controller).start();
		Delay.msDelay(10000);
		controller.stop();

	}

	public static JFrame displayVisualisation(MapVisualisationComponent viz) {
		// Create a frame to contain the viewer
		JFrame frame = new JFrame("Simulation Viewer");

		// Add visualisation to frame
		frame.add(viz);
		frame.addWindowListener(new KillMeNow());

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
