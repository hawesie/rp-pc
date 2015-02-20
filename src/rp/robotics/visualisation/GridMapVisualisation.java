package rp.robotics.visualisation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import lejos.geom.Point;
import lejos.robotics.mapping.LineMap;
import rp.robotics.mapping.IGridMap;

public class GridMapVisualisation extends LineMapVisualisation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected IGridMap m_gridMap;

	public GridMapVisualisation(IGridMap _gridMap, LineMap _lineMap,
			float _scaleFactor) {
		super(_lineMap, _scaleFactor);
		m_gridMap = _gridMap;
	}

	@Override
	protected void renderMap(Graphics2D _g2) {
		// render lines first
		super.renderMap(_g2);

		_g2.setStroke(new BasicStroke(1));
		_g2.setPaint(Color.BLUE);

		// then add grid
		for (int x = 0; x < m_gridMap.getXSize(); x++) {
			for (int y = 0; y < m_gridMap.getYSize(); y++) {
				if (!m_gridMap.isObstructed(x, y)) {
					Point gridPoint = m_gridMap.getCoordinatesOfGridPosition(x,
							y);
					renderPoint(gridPoint, _g2);
				}
			}
		}

	}

}
