package rp.robotics.testing;

import java.util.ArrayList;

import lejos.geom.Line;
import lejos.geom.Rectangle;
import rp.robotics.mapping.GridMap;
import rp.robotics.mapping.MapUtils;
import rp.robotics.mapping.LineMap;
import static rp.robotics.mapping.MapUtils.createBox;

/**
 * Standard maps to be used for automated simulation testing.
 * 
 * @author Nick Hawes
 *
 */
public class TestMaps {

	public static final LineMap EMPTY_8_x_6 = MapUtils.createRectangularMap(8,
			6);

	public static final LineMap EMPTY_1_x_1 = MapUtils.createRectangularMap(1,
			1);

	public static final LineMap EMPTY_2_x_1 = MapUtils.createRectangularMap(2,
			1);

	public static final LineMap EMPTY_4_x_1 = MapUtils.createRectangularMap(4,
			1);

	public static final LineMap EMPTY_8_x_1 = MapUtils.createRectangularMap(8,
			1);

	public static final LineMap EMPTY_16_x_1 = MapUtils.createRectangularMap(
			16, 1);

	public static GridMap warehouseMap() {

		float height = 2.44f;
		float width = 3.67f;

		float xInset = 0.17f, yInstet = 0.155f;
		int gridWitdth = 12, gridHeight = 8;
		float cellSize = 0.30f;

		// First ins 36 39 56 188

		final boolean createBoxLinesAsBoxes = false;

		ArrayList<Line> lines = new ArrayList<Line>();

		// Rectangular outer wall edges
		lines.addAll(createBox(0, 0, width, height, false));

		float[] wallStarts = { 0.36f, 1.26f, 2.16f, 3.06f };
		float wallWidth = 0.2f;
		for (float wallStart : wallStarts) {

			lines.addAll(createBox(wallStart, 0.39f, wallStart + wallWidth,
					1.78f, createBoxLinesAsBoxes));

		}

		Line[] lineArray = new Line[lines.size()];

		lines.toArray(lineArray);

		return new GridMap(gridWitdth, gridHeight, xInset, yInstet, cellSize,
				new LineMap(lineArray, new Rectangle(0, 0, width, height)));
	}
}
