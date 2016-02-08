package rp.robotics.simulation;

import lejos.geom.Line;
import lejos.robotics.localization.PoseProvider;

public interface DynamicObstacle extends PoseProvider, SimulationSteppable {

	Line[] getFootprint();

}
