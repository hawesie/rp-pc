package rp.robotics.visualisation;

import javax.swing.JFrame;

import rp.robotics.mapping.MapUtils;
import rp.robotics.mapping.RPLineMap;

public class LineMapViewer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		JFrame frame = new JFrame("Map Viewer");

		RPLineMap lineMap = MapUtils.create2014Map2();

		// view the map with 2 pixels as 1 cm
		LineMapVisualisation mapVis = new LineMapVisualisation(lineMap, 2);

		frame.add(mapVis);
		frame.pack();
		frame.setSize(1050, 600);
		frame.setVisible(true);

	}
}
