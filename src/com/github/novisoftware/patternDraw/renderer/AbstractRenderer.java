package com.github.novisoftware.patternDraw.renderer;

import java.awt.Graphics2D;
import java.util.ArrayList;

import com.github.novisoftware.patternDraw.geometricLanguage.entity.Line;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.svg.SvgInstruction;

public abstract class AbstractRenderer implements Renderer {
	public static int IMAGE_WIDTH = 800;
	public static int IMAGE_HEIGHT = 800;
	private final double ZOOM = 300;

	protected int x2int(double x) {
		return (int) Math.round(x * ZOOM + AbstractRenderer.IMAGE_WIDTH / 2);
	}

	protected int y2int(double y) {
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

	ArrayList<String> context = new ArrayList<String>();

	public void resetContext() {
		context = new ArrayList<String>();
	}

	String svg_stroke_color = "black";
	String svg_fill_color = "black";
	double svg_stroke_width = 0.3;

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
			/*
			String svgStr = s.polyLine(posList, false);
			*/
			String svgStr = s.path(posList, false, false);
			svgBuff.add(svgStr);
		}
	}

}
