package rp.robotics.example;

import rp.robotics.simulation.Rotate;

import javax.swing.JFrame;

import lejos.robotics.navigation.Pose;
import rp.robotics.MobileRobotWrapper;
import rp.robotics.simulation.Drive;
import rp.robotics.simulation.MapBasedSimulation;
import rp.robotics.simulation.MovableRobot;
import rp.robotics.simulation.SimulatedRobots;
import rp.robotics.testing.TestMaps;
import rp.robotics.visualisation.KillMeNow;
import rp.robotics.visualisation.MapVisualisationComponent;

/**
 * An example of the underlying move simulation which does not simulate the
 * motors.
 * 
 * @author Nick Hawes
 *
 */
public class MovableSimExample {

	public void run() {

		// Create the simulation using the given map. This simulation can run
		// with a GUI.
		MapBasedSimulation sim = new MapBasedSimulation(TestMaps.EMPTY_8_x_6);

		MobileRobotWrapper<MovableRobot> wrapper = sim.addRobot(SimulatedRobots
				.makeConfiguration(false, false), new Pose(3, 3, 0));

		MapVisualisationComponent viz = MapVisualisationComponent
				.createFromSimulation(sim);

		// Add the visualisation to a JFrame to display it
		displayVisualisation(viz);

		wrapper.getRobot().getPilot().executeMove(new Drive(0.3f, 1f));
		wrapper.getRobot().getPilot().executeMove(new Rotate(45f, 90));
		wrapper.getRobot().getPilot().executeMove(new Drive(0.3f, 1f));
		wrapper.getRobot().getPilot().executeMove(new Rotate(45f, -90));
		wrapper.getRobot().getPilot().executeMove(new Drive(0.3f, 1f));
		wrapper.getRobot().getPilot().executeMove(new Rotate(45f, 90));
		wrapper.getRobot().getPilot().executeMove(new Drive(0.3f, 1f));

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
