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
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;
import rp.config.RangeScannerDescription;
import rp.geom.GeometryUtils;
import rp.robotics.DifferentialDriveRobotPC;
import rp.robotics.LocalisedRangeScanner;
import rp.robotics.mapping.MapUtils;
import rp.robotics.mapping.RPLineMap;
import rp.robotics.simulation.DynamicObstacle;

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

	// in rendering units
	protected static final int X_MARGIN = 30;

	// in rendering units
	protected static final int Y_MARGIN = 30;

	// in world units
	private static final float ROBOT_RADIUS = 0.06f;

	// in world units
	private static final float POINT_RADIUS = 0.02f;

	private final Rectangle m_worldDimensions;

	private final Rectangle m_visualisationDimensions;

	private final RPLineMap m_lineMap;

	private final float m_scaleFactor;

	private Line[] m_transformedLines;

	private ArrayList<PoseProvider> m_poseProviders = new ArrayList<PoseProvider>(
			1);

	private ArrayList<DifferentialDriveRobotPC> m_robots = new ArrayList<>(1);
	private ArrayList<DynamicObstacle> m_obstacles = new ArrayList<>(1);
	private ArrayList<LocalisedRangeScanner> m_rangers = new ArrayList<>(1);

	private boolean m_trackRobots = true;

	private ArrayList<Point> m_robotTracks = new ArrayList<Point>();

	public MapVisualisationComponent(RPLineMap _lineMap, float _scaleFactor) {

		int _width = (int) _lineMap.getBoundingRect().getWidth();
		int _height = (int) _lineMap.getBoundingRect().getHeight();

		m_scaleFactor = _scaleFactor;
		m_worldDimensions = new Rectangle(scale(_width), scale(_height));
		m_visualisationDimensions = new Rectangle(scale(_width)
				+ (2 * X_MARGIN), scale(_height) + (2 * Y_MARGIN));

		// set minimum size of frame to scaled size of world
		setMinimumSize(m_visualisationDimensions.getSize());

		m_lineMap = _lineMap;

		// transform lines so that we don't need to do this every
		// render time during visualisation
		m_transformedLines = staticTransformMapLines(m_lineMap, X_MARGIN,
				Y_MARGIN);

		// repaint frequecy
		new Timer(16, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		}).start();
	}

	/**
	 * Create a visualisation of the given LineMap with scale factor (pixels to
	 * map units) of 100. This is suitable for maps defined in metres.
	 * 
	 * @param _lineMap
	 */
	public MapVisualisationComponent(RPLineMap _lineMap) {
		this(_lineMap, 100f);
	}

	/**
	 * Create an empty visualisation of a default size.
	 * 
	 * @return
	 */
	public static MapVisualisationComponent createVisualisation() {

		float _height = 6.0f;
		float _width = 8.0f;
		return createVisualisation(_width, _height);
	}

	public static MapVisualisationComponent createVisualisation(RPLineMap _map) {
		return new MapVisualisationComponent(_map);
	}

	public static MapVisualisationComponent createVisualisation(float _width,
			float _height) {

		return new MapVisualisationComponent(MapUtils.createRectangularMap(
				_width, _height));
	}

	public LineMap getLineMap() {
		return m_lineMap;
	}

	/**
	 * 
	 * @param _dimension
	 * @return
	 */
	protected final int scale(int _dimension) {
		return (int) (_dimension * m_scaleFactor);
	}

	protected final double scale(double _dimension) {
		return (_dimension * m_scaleFactor);
	}

	protected final float scale(float _dimension) {
		return (_dimension * m_scaleFactor);
	}

	/**
	 * The y axis visually is top down. We want it to be bottom up. Therefore
	 * this method takes a y value in world coordinates and returns a new world
	 * coordinate which is the world's max y minus the original value. In other
	 * words this treats max y as the y origin and the value as translated from
	 * there.
	 * 
	 * @param _y
	 */
	protected double flipY(double _y) {
		return m_lineMap.getBoundingRect().height - _y;
	}

	/**
	 * The y axis visually is top down. We want it to be bottom up. Therefore
	 * this method takes a y value in world coordinates and returns a new world
	 * coordinate which is the world's max y minus the original value. In other
	 * words this treats max y as the y origin and the value as translated from
	 * there.
	 * 
	 * @param _y
	 */
	private float flipY(float _y) {
		return m_lineMap.getBoundingRect().height - _y;
	}

	private Line[] staticTransformMapLines(LineMap _lm, int _dx, int _dy) {

		Line[] originalLines = _lm.getLines();
		Line[] translatedLines = new Line[originalLines.length];

		for (int i = 0; i < originalLines.length; i++) {
			translatedLines[i] = new Line(scale(originalLines[i].x1) + _dx,
					scale(flipY(originalLines[i].y1)) + _dy,
					scale(originalLines[i].x2) + _dx,
					scale(flipY(originalLines[i].y2)) + _dy);
		}

		return translatedLines;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		Rectangle rectFill = new Rectangle(m_worldDimensions);

		// move the rectangle away from the edge of the frame
		rectFill.translate(X_MARGIN, Y_MARGIN);
		g2.setPaint(Color.WHITE);
		g2.fill(rectFill);

		renderMap(g2);

	}

	protected void renderMap(Graphics2D g2) {

		if (m_lineMap != null) {
			g2.setStroke(new BasicStroke(2));
			g2.setPaint(Color.BLACK);

			for (Line line : m_transformedLines) {
				g2.draw(line);
			}

			float mapHeight = m_lineMap.getBoundingRect().height;

			// Also add useful guide text
			g2.drawString("(0,0)    0 heading ->", scale(0), scale(flipY(0))
					+ Y_MARGIN + 15);

			g2.drawString("(0," + mapHeight + ")", scale(0),
					scale(flipY(mapHeight)) + Y_MARGIN - 5);

			float mapWidth = m_lineMap.getBoundingRect().width;
			g2.drawString("(" + mapWidth + "," + mapHeight + ")",
					scale(mapWidth) + X_MARGIN, scale(flipY(mapHeight))
							+ Y_MARGIN - 5);

		}

		for (DynamicObstacle obstacle : m_obstacles) {
			renderRelative(obstacle.getFootprint(), obstacle.getPose(), g2);
		}

		// if the robot can give us a pose
		for (DifferentialDriveRobotPC r : m_robots) {
			renderRobot(g2, r);
		}

		for (LocalisedRangeScanner ranger : m_rangers) {
			renderRanger(g2, ranger);
		}

		for (PoseProvider pp : m_poseProviders) {
			renderPose(pp.getPose(), g2);
		}

		if (m_trackRobots) {
			g2.setStroke(new BasicStroke(1));
			g2.setPaint(Color.GRAY);
			for (Point p : m_robotTracks) {
				renderPoint(p, g2, 0.005);
			}
		}

	}

	private void renderRanger(Graphics2D _g2, LocalisedRangeScanner _ranger) {

		// System.out.println("renderRAnger");

		Pose sensorPose = _ranger.getPose();

		RangeReadings readings = _ranger.getRangeValues();

		for (RangeReading reading : readings) {

			float range = reading.getRange();

			if (!RangeScannerDescription.isValidReading(range)) {

				range = 2.55f;

				_g2.setStroke(new BasicStroke(1));
				_g2.setPaint(Color.RED);
				// range = 2.55f;

			} else {
				_g2.setStroke(new BasicStroke(1));
				_g2.setPaint(Color.BLUE);

			}

			drawLineToHeading(_g2, sensorPose.getX(), sensorPose.getY(),
					sensorPose.getHeading() + reading.getAngle(), range);
		}

	}

	private void renderRobot(Graphics2D _g2, DifferentialDriveRobotPC _robot) {
		_g2.setStroke(new BasicStroke(2));
		_g2.setPaint(Color.BLACK);

		Pose p = _robot.getPose();

		// renderPose(p, _g2);

		// drawLineToHeading(_g2, p.getX(), p.getY(), p.getHeading(),
		// _robot.getRobotLength() / 2);

		renderRelative(_robot.getFootprint(), _robot.getPose(), _g2);

		if (_robot.getTouchSensors() != null) {
			for (Line[] footprint : _robot.getTouchSensors()) {
				renderRelative(footprint, _robot.getPose(), _g2);
			}
		}

		if (m_trackRobots) {
			m_robotTracks.add(p.getLocation());
		}

	}

	private void renderRelative(Line[] _lines, Pose _pose, Graphics2D _g2) {

		for (Line l : _lines) {

			l = GeometryUtils.transform(_pose, l);
			_g2.drawLine((int) scale(l.x1) + X_MARGIN, (int) scale(flipY(l.y1))
					+ X_MARGIN, (int) scale(l.x2) + X_MARGIN,
					(int) scale(flipY(l.y2)) + X_MARGIN);
		}

	}

	/**
	 * Input should all be in world units/coords
	 * 
	 * @param g2
	 * @param _x
	 * @param _y
	 * @param heading
	 * @param _lineLength
	 */
	private void drawLineToHeading(Graphics2D g2, double _x, double _y,
			double heading, double _lineLength) {

		if (heading > 180) {
			heading -= 360;
		}

		// System.out.println("drawLineToHeading: " + heading);

		// double headingX = _x;
		// double headingY = _y;
		// if (heading >= 0 && heading < 90) {
		//
		// headingX = _lineLength * Math.cos(Math.toRadians(heading));
		// headingY = (_lineLength * Math.sin(Math.toRadians(heading)));
		//
		// } else if (heading >= 90 && heading <= 180) {
		//
		// headingX = -(_lineLength * Math.cos(Math.toRadians(180 - heading)));
		// headingY = (_lineLength * Math.sin(Math.toRadians(180 - heading)));
		//
		// } else if (heading < 0 && heading >= -90) {
		//
		// headingX = _lineLength
		// * Math.cos(Math.toRadians(Math.abs(heading)));
		// headingY = -(_lineLength * Math.sin(Math.toRadians(Math
		// .abs(heading))));
		//
		// } else if (heading < -90 && heading >= -180) {
		//
		// headingX = -(_lineLength * Math.cos(Math.toRadians(180 - Math
		// .abs(heading))));
		// headingY = -(_lineLength * Math.sin(Math.toRadians(180 - Math
		// .abs(heading))));
		//
		// }

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

		Line2D l = new Line2D.Double(scale(_x) + X_MARGIN, scale(flipY(_y))
				+ Y_MARGIN, scale(_x + headingX) + X_MARGIN, scale(flipY(_y
				+ headingY))
				+ Y_MARGIN);
		g2.draw(l);
	}

	protected void renderLine(Point _p1, Point _p2, Graphics2D _g2) {
		Line2D line = new Line2D.Double(scale(_p1.x) + X_MARGIN,
				scale(flipY(_p1.y)) + Y_MARGIN, scale(_p2.x) + X_MARGIN,
				scale(flipY(_p2.y)) + Y_MARGIN);
		_g2.draw(line);
	}

	protected void renderPose(Pose _pose, Graphics2D _g2) {
		Ellipse2D ell =
		// first 2 coords are upper left corner of framing rectangle
		new Ellipse2D.Float(scale(_pose.getX() - ROBOT_RADIUS) + X_MARGIN,
				scale(flipY(_pose.getY() + ROBOT_RADIUS)) + Y_MARGIN,
				scale(ROBOT_RADIUS * 2), scale(ROBOT_RADIUS * 2));
		_g2.draw(ell);

		drawLineToHeading(_g2, _pose.getX(), _pose.getY(), _pose.getHeading(),
				ROBOT_RADIUS * 2);
	}

	protected void renderPoint(Point _point, Graphics2D _g2) {
		renderPoint(_point, _g2, POINT_RADIUS);
	}

	protected void renderPoint(Point _point, Graphics2D _g2, double _radius) {
		Ellipse2D ell =
		// first 2 coords are upper left corner of framing rectangle
		new Ellipse2D.Double(scale(_point.getX() - _radius) + X_MARGIN,
				scale(flipY(_point.getY() + _radius)) + Y_MARGIN,
				scale(_radius * 2), scale(_radius * 2));
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

	public void addRobot(DifferentialDriveRobotPC _robot) {
		m_robots.add(_robot);
	}

	public void addObstacle(DynamicObstacle _obstacle) {
		m_obstacles.add(_obstacle);
	}

	public void addRangeScanner(LocalisedRangeScanner _scanner) {
		m_rangers.add(_scanner);
	}

	// public void addRobot(PoseProvider _robot) {
	// m_poseProviders.add(_robot);
	// }

}
