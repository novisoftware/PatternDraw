package com.github.novisoftware.patternDraw.svg;

import java.util.ArrayList;

import com.github.novisoftware.patternDraw.Main;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;

public class SvgInstruction {
	private final double ZOOM_SVG = 300;
	private String strokeColor;
	private double strokeWidth;

	public SvgInstruction(String svg_stroke_color, double svg_stroke_width) {
		this.setStrokeColor(svg_stroke_color);
		this.setStrokeWidth(svg_stroke_width);
	}

	private double x2double(double x) {
		return (x * ZOOM_SVG + Main.IMAGE_WIDTH / 2);
	}

	private double y2double(double y) {
		return (y * ZOOM_SVG + Main.IMAGE_HEIGHT / 2);
	}

	public String line(double dx0, double dy0, double dx1, double dy1) {
		return String.format("<line x1=\"%g\" y1=\"%g\" x2=\"%g\" y2=\"%g\" stroke=\"%s\"  stroke-width=\"%g\"  />",
				x2double(dx0), y2double(dy0), x2double(dx1), y2double(dy1), getStrokeColor(), getStrokeWidth());
	}

	public String polyLine(ArrayList<Pos> posList, boolean isFilled) {
		StringBuilder sbPointList = new StringBuilder();

		for (Pos p: posList) {
			sbPointList.append(String.format("%g,%g ", x2double(p.getX()), y2double(p.getY())));
		}
		//  style=\"stroke-width:%g\"
		return String.format("<polyline points=\"%s\"  fill=\"none\" stroke=\"%s\"  stroke-width=\"%g\"   />",
				sbPointList,
				getStrokeColor(),
				getStrokeWidth()
				);
	}

	/**
	 * @return strokeColor
	 */
	public String getStrokeColor() {
		return strokeColor;
	}

	/**
	 * @param strokeColor セットする strokeColor
	 */
	public void setStrokeColor(String strokeColor) {
		this.strokeColor = strokeColor;
	}

	/**
	 * @return strokeWidth
	 */
	public double getStrokeWidth() {
		return strokeWidth;
	}

	/**
	 * @param strokeWidth セットする strokeWidth
	 */
	public void setStrokeWidth(double strokeWidth) {
		this.strokeWidth = strokeWidth;
	}
}