package rp.robotics.testing;

import rp.robotics.simulation.MapBasedSimulation;
import rp.robotics.visualisation.MapVisualisationComponent;

/**
 * 
 * @author nah
 */
public class TestVisualisationComponent {

	public static MapVisualisationComponent createVisualisationForTest(
			MapBasedSimulation _sim, ZoneSequenceTest<?> _test) {

		ZoneSequenceTest<?> test = (ZoneSequenceTest<?>) _test;
		MapVisualisationComponent visualisation = new ZoneSequenceTestVisualisation(
				_sim.getMap(), test);

		MapVisualisationComponent.populateVisualisation(visualisation, _sim);

		return visualisation;
	}

	public static MapVisualisationComponent createVisualisationForTest(
			MapBasedSimulation _sim, RobotTest<?> _test) {

		MapVisualisationComponent visualisation = new MapVisualisationComponent(
				_sim.getMap(), 75f);

		MapVisualisationComponent.populateVisualisation(visualisation, _sim);

		return visualisation;
	}
}
