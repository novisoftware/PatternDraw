package com.github.novisoftware.patternDraw.geometricLanguage.primitives;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.novisoftware.patternDraw.geometricLanguage.entity.Line;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.renderer.AbstractRenderer;
import com.github.novisoftware.patternDraw.renderer.Renderer;
import com.github.novisoftware.patternDraw.svg.SvgInstruction;

public class Path implements Renderer {
	ArrayList<Pos> positions;
	private String strokeColor;
	private double strokeWidth_PNG;
	private double strokeWidth_SVG;
	private String fillColor;
	private final boolean isClosed;
	boolean isFill;
	private final double ZOOM = 300;


	public Path(Line line, String strokeColor, String strokeWidth, boolean isClosed, boolean isFill, String fillColor) {
		this.positions = new ArrayList<Pos>();
		this.positions.add(new Pos(line.x0, line.y0));
		this.positions.add(new Pos(line.x1, line.y1));

		this.strokeColor = strokeColor;
		this.strokeWidth_PNG = Double.parseDouble(strokeWidth);
		this.strokeWidth_SVG = Double.parseDouble(strokeWidth);
		this.isClosed = isClosed;
		this.isFill = isFill;
		this.fillColor = fillColor;
	}

	public Path(ArrayList<Pos> positions, String strokeColor, double strokeWidth, boolean isClosed, boolean isFill, String fillColor) {
		this.positions = positions;

		this.strokeColor = strokeColor;
		this.strokeWidth_PNG = strokeWidth;
		this.strokeWidth_SVG = strokeWidth;
		this.isClosed = isClosed;
		this.isFill = isFill;
		this.fillColor = fillColor;
	}


	public void setStrokeColor(String s) {
		this.strokeColor = s;
	}

	public void setStrokeWidth_SVG(double width) {
		this.strokeWidth_SVG = width;
	}

	private int x2int(double x) {
		return (int) Math.round(x * ZOOM + AbstractRenderer.IMAGE_WIDTH / 2);
	}

	private int y2int(double y) {
		return (int) Math.round(y * ZOOM + AbstractRenderer.IMAGE_HEIGHT / 2);
	}

	static final double SCALING = 200.0;
	static double SCALE2 = 1;

	public double int2x(int x) {
		return (x - AbstractRenderer.IMAGE_WIDTH / 2) / SCALING / SCALE2;
	}

	public double int2y(int y) {
		return (y - AbstractRenderer.IMAGE_HEIGHT / 2) / SCALING / SCALE2;
	}

	HashMap<String, Color> colorCache = new HashMap<String, Color>();

	Color getColor(String s) {
		Color cachedColor = colorCache.get(s);
		if (cachedColor != null) {
			return cachedColor;
		}

		if (s.startsWith("#")) {
			Color c = Color.decode(s.substring(1));
			colorCache.put(s, c);
			return c;
		}

		return null;
	}

	public void localDrawLine(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s, double dx0, double dy0,
			double dx1, double dy1) {

		if (g != null) {
			Color color = this.getColor(this.strokeColor);
			if (color != null) {
				g.setColor(color);
			}

			BasicStroke stroke = new BasicStroke((float)this.strokeWidth_PNG);
			g.setStroke(stroke);

			int wx1 = x2int(dx0);
			int wy1 = y2int(dy0);
			int wx2 = x2int(dx1);
			int wy2 = y2int(dy1);

			g.drawLine(wx1, wy1, wx2, wy2);
		}

		if (svgBuff != null) {
			s.setStrokeWidth(this.strokeWidth_SVG);
			String svgStr = s.line(dx0, dy0, dx1, dy1);
			svgBuff.add(svgStr);
		}
	}

	public void localDrawLine(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s, Line line) {
		this.localDrawLine(g, svgBuff, s,
				line.x0,
				line.y0,
				line.x1,
				line.y1
				);
	}

	public void localDrawLine(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s, Pos pos1, Pos pos2) {
		localDrawLine(g, svgBuff, s, pos1.getX(), pos1.getY(), pos2.getX(), pos2.getY());
	}


	public void localPolyLine(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s, ArrayList<Pos> posList, boolean isClosed, boolean isFilled) {
		if (posList.size() == 0) {
			return;
		}
		
		if (g != null) {
			Color color = this.getColor(this.strokeColor);
			if (color != null) {
				g.setColor(color);
			}

			float w = (float)this.strokeWidth_PNG;
			BasicStroke stroke = new BasicStroke(w, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
			g.setStroke(stroke);

			int nPoints = posList.size() + (isClosed ? 1: 0);
			int[] xPoints = new int[nPoints];
			int[] yPoints = new int[nPoints];
			for (int i = 0 ; i < nPoints ; i++ ) {
				Pos pos = posList.get(i % posList.size());
				xPoints[i] = x2int(pos.getX());
				yPoints[i] = y2int(pos.getY());
			}

			if (this.isFill) {
				g.fillPolygon(xPoints, yPoints, nPoints);
			} else {
				g.drawPolyline(xPoints, yPoints, nPoints);
			}
		}

		if (svgBuff != null) {
			s.setStrokeWidth(this.strokeWidth_SVG);
			/*
			String svgStr = s.polyLine(posList, this.isFill);
			*/
			String svgStr = s.path(posList, isClosed, this.isFill);
			svgBuff.add(svgStr);
		}
	}

	@Override
	public void render(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s) {
		this.localPolyLine(g, svgBuff, s, positions, this.isClosed, true);
	}

	/**
	 * @return strokeColor
	 */
	public String getStrokeColor() {
		return strokeColor;
	}

	/**
	 * @return fillColor
	 */
	public String getFillColor() {
		return fillColor;
	}

	/**
	 * @param fillColor セットする fillColor
	 */
	public void setFillColor(String fillColor) {
		this.fillColor = fillColor;
	}
}
