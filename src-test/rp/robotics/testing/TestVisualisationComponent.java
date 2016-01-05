package rp.robotics.testing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import lejos.robotics.mapping.LineMap;
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
	private final ZoneSequenceTest m_zst;

	public TestVisualisationComponent(LineMap _map, ZoneSequenceTest _test) {
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
		_g.draw(ell);

	}
}
