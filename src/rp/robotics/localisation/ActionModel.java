package rp.robotics.localisation;

import rp.robotics.mapping.Heading;

/**
 * An example of an interface an action model might provide.
 * 
 * Note: you do not have to use this if you don't want to.
 * 
 * @author Nick Hawes
 * 
 */
public interface ActionModel {

	/***
	 * Update the given distribution assuming a one position translation in the
	 * direction given by the heading. This heading is in the global coordinate
	 * frame.
	 * 
	 * @param _dist
	 * @param _heading
	 * @return
	 */
	public GridPositionDistribution updateAfterMove(
			GridPositionDistribution _dist, Heading _heading);

}
