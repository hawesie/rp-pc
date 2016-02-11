package rp.robotics.visualisation;

import javax.swing.JFrame;

import rp.robotics.mapping.GridMap;
import rp.robotics.mapping.LineMap;
import rp.robotics.mapping.MapUtils;

public class ExampleGridMapVisualisation {

	public static void main(String[] args) {
		// Work on this map
		LineMap lineMap = MapUtils.create2014Map2();

		// Grid map configuration

		// Grid junction numbers
		int xJunctions = 10;
		int yJunctions = 7;

		float junctionSeparation = 30;

		int xInset = 14;
		int yInset = 31;

		GridMap gridMap = new GridMap(xJunctions, yJunctions, xInset, yInset,
				junctionSeparation, lineMap);

		GridMapVisualisation mapVis = new GridMapVisualisation(gridMap,
				lineMap, 2);

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
