package rp.robotics.example;

import javax.swing.JFrame;

import lejos.robotics.RangeFinder;
import rp.robotics.MobileRobotWrapper;
import rp.robotics.control.RandomGridWalk;
import rp.robotics.mapping.GridMap;
import rp.robotics.navigation.GridPose;
import rp.robotics.navigation.Heading;
import rp.robotics.simulation.MapBasedSimulation;
import rp.robotics.simulation.MovableRobot;
import rp.robotics.simulation.SimulatedRobots;
import rp.robotics.testing.TestMaps;
import rp.robotics.visualisation.GridMapVisualisation;
import rp.robotics.visualisation.KillMeNow;
import rp.robotics.visualisation.MapVisualisationComponent;

/**
 * A demo of robots moving about on a grid. Currently the range readings don't
 * return off other robots and they can't collide into each other.
 * 
 * @author Nick Hawes
 *
 */
public class ExampleGridMover {

	public void run() {

		// Create the simulation using the given map. This simulation can run
		// with a GUI.
		MapBasedSimulation sim = new MapBasedSimulation(TestMaps.EMPTY_8_x_6);

		// Grid junction numbers
		int xJunctions = 50;
		int yJunctions = 21;

		float junctionSeparation = 0.40f;

		float xInset = 0.4f;
		float yInset = 0.41f;

		GridMap gridMap = new GridMap(xJunctions, yJunctions, xInset, yInset,
				junctionSeparation, sim.getMap());

		// Add a robot of a given configuration to the simulation. The return
		// value is the object you can use to control the robot. //

		for (int i = 0; i < 5; i++) {
			// Starting point on the grid
			GridPose gridStart = new GridPose(8, 8, Heading.PLUS_X);

			MobileRobotWrapper<MovableRobot> wrapper = sim.addRobot(
					SimulatedRobots.makeConfiguration(false, true),
					gridMap.toPose(gridStart));

			RangeFinder ranger = sim.getRanger(wrapper);

			RandomGridWalk controller = new RandomGridWalk(wrapper.getRobot(),
					gridMap, gridStart, ranger);

			new Thread(controller).start();
		}

		GridMapVisualisation viz = new GridMapVisualisation(gridMap,
				sim.getMap());

		MapVisualisationComponent.populateVisualisation(viz, sim);

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
