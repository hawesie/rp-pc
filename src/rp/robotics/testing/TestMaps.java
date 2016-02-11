package rp.robotics.testing;

import rp.robotics.mapping.MapUtils;
import rp.robotics.mapping.LineMap;

/**
 * Standard maps to be used for automated simulation testing.
 * 
 * @author Nick Hawes
 *
 */
public class TestMaps {

	public static final LineMap EMPTY_8_x_6 = MapUtils.createRectangularMap(
			8, 6);

	public static final LineMap EMPTY_1_x_1 = MapUtils.createRectangularMap(
			1, 1);

	public static final LineMap EMPTY_2_x_1 = MapUtils.createRectangularMap(
			2, 1);

	public static final LineMap EMPTY_4_x_1 = MapUtils.createRectangularMap(
			4, 1);

	public static final LineMap EMPTY_8_x_1 = MapUtils.createRectangularMap(
			8, 1);

}
