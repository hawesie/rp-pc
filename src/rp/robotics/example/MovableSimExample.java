package rp.robotics.example;

import javax.swing.JFrame;

import lejos.robotics.navigation.Pose;
import rp.robotics.MobileRobotWrapper;
import rp.robotics.simulation.Drive;
import rp.robotics.simulation.MapBasedSimulation;
import rp.robotics.simulation.MovableRobot;
import rp.robotics.simulation.Rotate;
import rp.robotics.simulation.SimulatedRobots;
import rp.robotics.testing.TestMaps;
import rp.robotics.visualisation.KillMeNow;
import rp.robotics.visualisation.MapVisualisationComponent;

/**
 * An example to get you started. Copy this to your own project, rename it and
 * go.
 * 
 * @author Nick Hawes
 *
 */
public class MovableSimExample {

	public void run() {

		// Create the simulation using the given map. This simulation can run
		// with a GUI.
		MapBasedSimulation sim = new MapBasedSimulation(TestMaps.EMPTY_8_x_6);

		// Add a robot of a given configuration to the simulation. The return
		// value is the object you can use to control the robot. //
		MobileRobotWrapper<MovableRobot> wrapper = sim.addRobot(SimulatedRobots
				.makeConfiguration(false, false), new Pose(3f, 3f, 0));

		MovableRobot robot = wrapper.getRobot();
		robot.getPilot().addMove(new Drive(0.4f, 1f));
		robot.getPilot().addMove(new Rotate(45f, 90f));
		robot.getPilot().addMove(new Drive(0.4f, 1f));
		robot.getPilot().addMove(new Rotate(-45f, 90f));
		robot.getPilot().addMove(new Drive(0.4f, 1f));
		robot.getPilot().addMove(new Rotate(45f, 90f));
		robot.getPilot().addMove(new Drive(0.4f, 1f));
		robot.getPilot().addMove(new Rotate(-45f, 90f));
		robot.getPilot().addMove(new Drive(0.4f, 10f));

		// Create visualisation JComponent that renders map, robots etc
		MapVisualisationComponent viz = MapVisualisationComponent
				.createFromSimulation(sim);

		// Add the visualisation to a JFrame to display it
		displayVisualisation(viz);

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
		MovableSimExample demo = new MovableSimExample();
		demo.run();
	}

}
