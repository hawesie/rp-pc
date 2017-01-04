package rp.robotics.visualisation;

import javax.swing.JFrame;

import rp.robotics.mapping.GridMap;
import rp.robotics.mapping.MapUtils;

public class ExampleGridMapVisualisation {

	public static void main(String[] args) {
	
		// Grid map configuration

		GridMap gridMap = MapUtils.createMarkingWarehouseMap();

		GridMapVisualisation mapVis = new GridMapVisualisation(gridMap, gridMap, 300f);


		displayVisualisation(mapVis);
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

}
