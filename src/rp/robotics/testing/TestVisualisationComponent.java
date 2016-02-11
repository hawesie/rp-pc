package rp.robotics.testing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import rp.robotics.DifferentialDriveRobotPC;
import rp.robotics.mapping.LineMap;
import rp.robotics.simulation.MapBasedSimulation;
import rp.robotics.testing.TargetZone.Status;
import rp.robotics.visualisation.MapVisualisationComponent;

/**
 * 
 * @author nah
 */
public class TestVisualisationComponent extends MapVisualisationComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ZoneSequenceTest<?> m_zst;

	public TestVisualisationComponent(LineMap _map, ZoneSequenceTest<?> _test) {
		super(_map);
		m_zst = _test;
	}

	@Override
	public void paint(Graphics _g) {
		super.paint(_g);
		paintTest((Graphics2D) _g);

	}

	private void paintTest(Graphics2D _g) {
		for (TargetZone zone : m_zst) {
			paintTargetZone(_g, zone);
		}
	}

	private void paintTargetZone(Graphics2D _g, TargetZone _zone) {

		Ellipse2D ell =
		// first 2 coords are upper left corner of framing rectangle
		new Ellipse2D.Double(
				scale(_zone.getTarget().getX() - _zone.getRadius()) + X_MARGIN,
				scale(flipY(_zone.getTarget().getY() + _zone.getRadius()))
						+ Y_MARGIN, scale(_zone.getRadius() * 2),
				scale(_zone.getRadius() * 2));

		if (_zone.getStatus() == Status.HIT) {
			_g.setPaint(Color.GREEN);
			_g.fill(ell);
		} else if (_zone.getStatus() == Status.LIVE) {
			_g.setPaint(Color.ORANGE);
			_g.fill(ell);
		} else {
			_g.draw(ell);
		}

	}

	public static MapVisualisationComponent createVisulationForTest(
			MapBasedSimulation _sim, ZoneSequenceTest<?> _test) {
		MapVisualisationComponent visualisation = new TestVisualisationComponent(
				_sim.getMap(), _test);
		for (DifferentialDriveRobotPC robot : _sim) {
			visualisation.addRobot(robot);
		}

		visualisation.addRobot(_test.getPoseProvider());

		return visualisation;
	}
}
