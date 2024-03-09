package com.github.novisoftware.patternDraw.geometryLanguage.primitives;

import java.awt.Graphics2D;
import java.util.ArrayList;

import com.github.novisoftware.patternDraw.Main;
import com.github.novisoftware.patternDraw.geometry.Line;
import com.github.novisoftware.patternDraw.geometry.Pos;
import com.github.novisoftware.patternDraw.renderer.AbstractRenderer;
import com.github.novisoftware.patternDraw.renderer.Renderer;
import com.github.novisoftware.patternDraw.svg.SvgInstruction;

public class Path implements Renderer {
	ArrayList<Pos> positions;
	private String strokeColor;
	private String fillColor;
	private String strokeWidth;
	boolean isFill;

	public Path(Pos a, Pos b, String strokeColor) {
		this.positions = new ArrayList<Pos>();
	}

	public Path(Line line, String strokeColor, String strokeWidth, boolean isFill, String fillColor) {
		this.positions = new ArrayList<Pos>();
		this.positions.add(new Pos(line.x0, line.y0));
		this.positions.add(new Pos(line.x1, line.y1));

		this.strokeColor = strokeColor;
		this.strokeWidth = strokeWidth;
		this.isFill = isFill;
		this.fillColor = fillColor;
	}


	public void setStrokeColor(String s) {
		this.strokeColor = s;
	}

	private final double ZOOM = 300;

	private int x2int(double x) {
		return (int) Math.round(x * ZOOM + Main.IMAGE_WIDTH / 2);
	}

	private int y2int(double y) {
		return (int) Math.round(y * ZOOM + Main.IMAGE_HEIGHT / 2);
	}

	static final double SCALING = 200.0;
	static double SCALE2 = 1;

	public double int2x(int x) {
		return (x - Main.IMAGE_WIDTH / 2) / SCALING / SCALE2;
	}

	public double int2y(int y) {
		return (y - Main.IMAGE_HEIGHT / 2) / SCALING / SCALE2;
	}


	public void localDrawLine(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s, double dx0, double dy0,
			double dx1, double dy1) {

		if (g != null) {
			int wx1 = x2int(dx0);
			int wy1 = y2int(dy0);
			int wx2 = x2int(dx1);
			int wy2 = y2int(dy1);

			g.drawLine(wx1, wy1, wx2, wy2);
		}

		if (svgBuff != null) {
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


	public void localPolyLine(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s, ArrayList<Pos> posList, boolean isFilled) {
		if (g != null) {
			int n = posList.size();
			for (int i = 0 ; i < n ; i++ ) {
				localDrawLine(g, null, null, posList.get(i), posList.get((i+1)%n));
			}
		}
		if (svgBuff != null) {
			String svgStr = s.polyLine(posList, false);
			svgBuff.add(svgStr);
		}
	}

	@Override
	public void render(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s) {
		this.localPolyLine(g, svgBuff, s, positions, true);
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
