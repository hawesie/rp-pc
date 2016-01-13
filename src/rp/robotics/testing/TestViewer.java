package rp.robotics.testing;

import javax.swing.JFrame;

import rp.robotics.DifferentialDriveRobotPC;
import rp.robotics.simulation.MapBasedSimulation;
import rp.robotics.visualisation.KillMeNow;
import rp.robotics.visualisation.MapVisualisationComponent;

/**
 * Demonstrates the use, and visualisation, of a simulated simple robot on a
 * line map.
 * 
 * @author Nick Hawes
 *
 */
public class TestViewer {

	private final ZoneSequenceTest<DifferentialDriveRobotPC> m_test;

	public TestViewer(ZoneSequenceTest<DifferentialDriveRobotPC> _test) {
		m_test = _test;
	}

	public void run() {

		// Create the simulation using the given map. This simulation can run
		// with a GUI.
		MapBasedSimulation sim = new MapBasedSimulation(TestMaps.EMPTY_8_x_6);

		// Create visualisation JComponent that renders map, robots etc
		MapVisualisationComponent viz = TestVisualisationComponent
				.createVisulationForTest(sim, m_test);

		// Add the visualisation to a JFrame to display it
		displayVisualisation(viz);
		Thread simThread = new Thread(sim);
		simThread.start();
		m_test.run();
		sim.stop();
		try {
			simThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

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

}
