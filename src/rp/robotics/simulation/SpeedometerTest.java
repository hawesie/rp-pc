package rp.robotics.simulation;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SpeedometerTest {


	@Test
	public void test() {
		Speedometer speedo = new Speedometer(0, 0, 5);
		assertTrue(speedo.update(100, 1000) == 100.0);
		assertTrue(speedo.update(200, 2000) == 100.0);
		assertTrue(speedo.update(300, 3000) == 100.0);
		assertTrue(speedo.update(400, 4000) == 100.0);
		assertTrue(speedo.update(500, 5000) == 100.0);
		assertTrue(speedo.update(600, 6000) == 100.0);
		assertTrue(speedo.update(700, 7000) == 100.0);
	}

}
