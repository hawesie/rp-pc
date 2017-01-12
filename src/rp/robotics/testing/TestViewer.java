package rp.robotics.testing;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JFrame;

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
public class TestViewer implements Runnable {

	private final RobotTest<?> m_test;
	private final MapBasedSimulation m_sim;

	public TestViewer(RobotTest<?> _test, MapBasedSimulation _sim) {
		m_test = _test;
		m_sim = _sim;
	}

	public void run() {

		// Create visualisation JComponent that renders map, robots etc
		MapVisualisationComponent viz = TestVisualisationComponent
				.createVisualisationForTest(m_sim, m_test);

		// Add the visualisation to a JFrame to display it
		displayVisualisation(viz);
		m_test.run();
		m_sim.stop();

	}

	private void displayVisualisation(MapVisualisationComponent viz) {
		// Create a frame to contain the viewer
		JFrame frame = new JFrame("Map Viewer");

		// Add visualisation to frame
		frame.add(viz);
		frame.addWindowListener(new KillMeNow());

		frame.pack();
		Insets insets = frame.getInsets();
		Dimension min = viz.getMinimumSize();
		frame.setSize((int)(min.getWidth() + insets.left), (int)(min.getHeight() + insets.top));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
