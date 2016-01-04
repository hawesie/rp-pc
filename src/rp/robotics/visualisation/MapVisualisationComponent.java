package rp.robotics.visualisation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.Timer;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;
import rp.robotics.LocalisedRangeScanner;
import rp.robotics.mapping.RPLineMap;

/**
 * 
 * @author nah
 */
public class MapVisualisationComponent extends JComponent {

	/*
	 * The code in here is ugly, badly written and error-prone. This is a great
	 * example of what happens when code organically develops and is regularly
	 * repurposed for tasks other than those it was designed for. Please don't
	 * use the code here for anything other than a negative example of
	 * development practice.
	 */

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int X_MARGIN = 30;

	private static final int Y_MARGIN = 30;

	private static final int ROBOT_RADIUS = 6;

	private static final int POINT_RADIUS = 2;

	private final Rectangle m_worldDimensions;

	private final Rectangle m_visualisationDimensions;

	private final LineMap m_lineMap;

	private final float m_scaleFactor;

	private Line[] m_translatedLines;

	private ArrayList<PoseProvider> m_poseProviders = new ArrayList<PoseProvider>(
			1);

	private ArrayList<Object> m_robots = new ArrayList<Object>(1);

	private final boolean m_flip;

	private boolean m_trackRobots = true;
	private ArrayList<Point> m_robotTracks = new ArrayList<Point>();

	private MapVisualisationComponent(int _width, int _height,
			LineMap _lineMap, float _scaleFactor, boolean _flip) {

		m_scaleFactor = _scaleFactor;
		m_worldDimensions = new Rectangle(scale(_width), scale(_height));
		m_visualisationDimensions = new Rectangle(_width + (2 * X_MARGIN),
				_height + (2 * Y_MARGIN));

		// set minimum size of frame to scaled size of world
		setMinimumSize(m_visualisationDimensions.getSize());

		m_lineMap = _lineMap;

		m_translatedLines = translateLines(m_lineMap, X_MARGIN, Y_MARGIN);

		m_flip = _flip;

		// repaint at 20Hz
		new Timer(50, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		}).start();
	}

	/**
	 * Create a visualisation of the given LineMap with scale factor (pixels to
	 * map units) of 1.
	 * 
	 * @param _lineMap
	 */
	public MapVisualisationComponent(LineMap _lineMap) {
		this((int) _lineMap.getBoundingRect().getWidth(), (int) _lineMap
				.getBoundingRect().getHeight(), _lineMap, 0.1f, false);
	}

	/**
	 * Create an empty visualisation of a default size.
	 * 
	 * @return
	 */
	public static MapVisualisationComponent createVisualisation() {

		float _height = 6000f;
		float _width = 8000f;
		return createVisualisation(_width, _height);
	}

	public static MapVisualisationComponent createVisualisation(LineMap _map) {
		return new MapVisualisationComponent(_map);
	}

	public static MapVisualisationComponent createVisualisation(float _width,
			float _height) {

		// these are the walls for the world outline
		Line[] lines = new Line[] { new Line(0f, 0f, _width, 0f),
				new Line(_width, 0f, _width, _height),
				new Line(_width, _height, 0f, _height),
				new Line(0f, _height, 0f, 0f) };

		LineMap map = new LineMap(lines, new lejos.geom.Rectangle(0, 0, _width,
				_height));

		return new MapVisualisationComponent(map);
	}

	/**
	 * Create a visualisation of the given LineMap with the given scale factor
	 * (pixels to map units).
	 * 
	 * @param _lineMap
	 * @param _scaleFactor
	 */
	public MapVisualisationComponent(LineMap _lineMap, float _scaleFactor) {
		this((int) _lineMap.getBoundingRect().getWidth(), (int) _lineMap
				.getBoundingRect().getHeight(), _lineMap, _scaleFactor, false);
	}

	/**
	 * Create a visualisation of the given LineMap with the given scale factor
	 * (pixels to map units).
	 * 
	 * @param _lineMap
	 * @param _scaleFactor
	 * @param _flip
	 *            invert y axis
	 */

	public MapVisualisationComponent(LineMap _lineMap, float _scaleFactor,
			boolean _flip) {
		this((int) _lineMap.getBoundingRect().getWidth(), (int) _lineMap
				.getBoundingRect().getHeight(), _lineMap, _scaleFactor, _flip);
	}

	public LineMap getLineMap() {
		return m_lineMap;
	}

	/**
	 * 
	 * @param _dimension
	 * @return
	 */
	private final int scale(int _dimension) {
		return (int) (_dimension * m_scaleFactor);
	}

	private final double scale(double _dimension) {
		return (_dimension * m_scaleFactor);
	}

	private final float scale(float _dimension) {
		return (_dimension * m_scaleFactor);
	}

	private Line[] translateLines(LineMap _lm, int _dx, int _dy) {

		Line[] originalLines = _lm.getLines();
		Line[] translatedLines = new Line[originalLines.length];

		for (int i = 0; i < originalLines.length; i++) {
			translatedLines[i] = new Line(scale(originalLines[i].x1) + _dx,
					scale(originalLines[i].y1) + _dy,
					scale(originalLines[i].x2) + _dx,
					scale(originalLines[i].y2) + _dy);
		}

		return translatedLines;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		Rectangle rectFill = new Rectangle(m_worldDimensions);

		rectFill.translate(X_MARGIN, Y_MARGIN);
		g2.setPaint(Color.WHITE);
		g2.fill(rectFill);

		if (m_flip) {
			int height = getHeight() / 2;
			g2.translate(0, height);
			g2.scale(1, -1);
			g2.translate(0, -height);
		}

		renderMap(g2);

	}

	protected void renderMap(Graphics2D g2) {
		if (m_lineMap != null) {
			g2.setStroke(new BasicStroke(2));
			g2.setPaint(Color.BLACK);

			for (Line line : m_translatedLines) {
				g2.draw(line);
			}

			// Also add useful guide text
			g2.drawString("(0,0)    0 heading ->", 0, Y_MARGIN - 10);

			float mapHeight = m_lineMap.getBoundingRect().height;

			g2.drawString("(0," + mapHeight + ")", 0, scale(mapHeight)
					+ Y_MARGIN + 5);

			float mapWidth = m_lineMap.getBoundingRect().width;
			g2.drawString("(" + mapWidth + ",0)", scale(mapWidth) + X_MARGIN,
					-Y_MARGIN * 2 - 10);

		}

		g2.setStroke(new BasicStroke(1));

		for (PoseProvider pp : m_poseProviders) {
			renderPose(pp.getPose(), g2);
		}

		if (m_trackRobots) {
			g2.setPaint(Color.GRAY);
			for (Point p : m_robotTracks) {
				renderPoint(p, g2, 0.25);
			}
		}

		// if the robot can give us a pose
		for (Object r : m_robots) {
			if (r instanceof PoseProvider) {
				Pose p = ((PoseProvider) r).getPose();
				renderPose(p, g2);

				if (m_trackRobots) {
					m_robotTracks.add(new Point(p.getX(), p.getY()));
				}

				// if the robot can give us some range readings too
				if (r instanceof RangeScanner) {
					RangeReadings readings = ((RangeScanner) r)
							.getRangeValues();
					for (RangeReading reading : readings) {
						float range = reading.getRange();

						if (range == 255) {
							g2.setStroke(new BasicStroke(1));
							g2.setPaint(Color.RED);
							range = 20;

						} else {
							g2.setStroke(new BasicStroke(1));
							g2.setPaint(Color.BLUE);

						}
						drawLineToHeading(g2, p.getX(), p.getY(),
								p.getHeading() + reading.getAngle(), range);
					}
				}

			}
		}
	}

	private void drawLineToHeading(Graphics2D g2, double _x, double _y,
			double heading, double _lineLength) {

		if (heading > 180) {
			heading -= 360;
		}

		// System.out.println("drawLineToHeading: " + heading);

		double headingX = _x;
		double headingY = _y;
		if (heading >= 0 && heading < 90) {

			headingX = _lineLength * Math.cos(Math.toRadians(heading));
			headingY = (_lineLength * Math.sin(Math.toRadians(heading)));

		} else if (heading >= 90 && heading <= 180) {

			headingX = -(_lineLength * Math.cos(Math.toRadians(180 - heading)));
			headingY = (_lineLength * Math.sin(Math.toRadians(180 - heading)));

		} else if (heading < 0 && heading >= -90) {

			headingX = _lineLength
					* Math.cos(Math.toRadians(Math.abs(heading)));
			headingY = -(_lineLength * Math.sin(Math.toRadians(Math
					.abs(heading))));

		} else if (heading < -90 && heading >= -180) {

			headingX = -(_lineLength * Math.cos(Math.toRadians(180 - Math
					.abs(heading))));
			headingY = -(_lineLength * Math.sin(Math.toRadians(180 - Math
					.abs(heading))));

		}

		Line2D l = new Line2D.Double(scale(_x) + X_MARGIN,
				scale(_y) + Y_MARGIN, scale(_x + headingX) + X_MARGIN, scale(_y
						+ headingY)
						+ Y_MARGIN);
		g2.draw(l);
	}

	protected void renderLine(Point _p1, Point _p2, Graphics2D _g2) {
		Line2D line = new Line2D.Double(scale(_p1.x) + X_MARGIN, scale(_p1.y)
				+ Y_MARGIN, scale(_p2.x) + X_MARGIN, scale(_p2.y) + Y_MARGIN);
		_g2.draw(line);
	}

	protected void renderPose(Pose _pose, Graphics2D _g2) {
		Ellipse2D ell =
		// first 2 coords are upper left corner of framing rectangle
		new Ellipse2D.Float(scale(_pose.getX()) - ROBOT_RADIUS + X_MARGIN,
				scale(_pose.getY()) - ROBOT_RADIUS + Y_MARGIN,
				ROBOT_RADIUS * 2, ROBOT_RADIUS * 2);
		_g2.draw(ell);

		drawLineToHeading(_g2, _pose.getX(), _pose.getY(), _pose.getHeading(),
				ROBOT_RADIUS * 10);
	}

	protected void renderPoint(Point _point, Graphics2D _g2) {
		renderPoint(_point, _g2, POINT_RADIUS);
	}

	protected void renderPoint(Point _point, Graphics2D _g2, double _radius) {
		Ellipse2D ell =
		// first 2 coords are upper left corner of framing rectangle
		new Ellipse2D.Double(scale(_point.getX()) - _radius + X_MARGIN,
				scale(_point.getY()) - _radius + Y_MARGIN, _radius * 2,
				_radius * 2);
		_g2.draw(ell);
	}

	/**
	 * Add a pose provider to be visualised.
	 * 
	 * @param _poser
	 */
	public void addPoseProvide(PoseProvider _poser) {
		m_poseProviders.add(_poser);
	}

	// public void addRobot(LocalisedRangeScanner _robot) {
	// m_robots.add(_robot);
	// }

	public void addRobot(PoseProvider _robot) {
		m_robots.add(_robot);
	}

}
