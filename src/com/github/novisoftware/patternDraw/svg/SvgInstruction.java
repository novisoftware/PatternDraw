package com.github.novisoftware.patternDraw.svg;

import java.util.ArrayList;

import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.renderer.AbstractRenderer;

public class SvgInstruction {
	private final double ZOOM_SVG = 300;
	private String strokeColor;
	private double strokeWidth;
	/** デフォルト値は不透明 */
	private double strokeAlpha = 1;

	public SvgInstruction(String svg_stroke_color, double svg_stroke_width) {
		this.setStrokeColor(svg_stroke_color);
		this.setStrokeWidth(svg_stroke_width);
	}

	private double x2double(double x) {
		return (x * ZOOM_SVG + AbstractRenderer.IMAGE_WIDTH / 2);
	}

	private double y2double(double y) {
		return (y * ZOOM_SVG + AbstractRenderer.IMAGE_HEIGHT / 2);
	}

	public String line(double dx0, double dy0, double dx1, double dy1) {
		return String.format("<line x1=\"%g\" y1=\"%g\" x2=\"%g\" y2=\"%g\" stroke=\"%s\"  stroke-width=\"%g\"  />",
				x2double(dx0), y2double(dy0), x2double(dx1), y2double(dy1), getStrokeColor(), getStrokeWidth());
	}

	/**
	 * SVG <polyline/> による多角形描画
	 * 
	 * @param posList
	 * @param isFilled
	 * @return
	 */
	public String polyLine(ArrayList<Pos> posList, boolean isFilled) {
		/*
		 * SVGの属性の指定例:
		 * fill="purple"
		 * fill-opacity="0.5"
	 	 * stroke-opacity="0.8"
		 */
		
		
		StringBuilder sbPointList = new StringBuilder();

		for (Pos p: posList) {
			sbPointList.append(String.format("%g,%g ", x2double(p.getX()), y2double(p.getY())));
		}
		
		String fillColor;
		if (isFilled) {
			fillColor = getStrokeColor();
		} else {
			fillColor = "none";
		}

		String opacity;
		if (this.hasAlpha()) {
			if (isFilled) {
				opacity = String.format("fill-opacity=\"%s\"", this.getAlphaString());
			} else {
				opacity = String.format("stroke-opacity=\"%s\"", this.getAlphaString());
			}
		} else {
			opacity = "";
		}
		
		if (isFilled) {
			return String.format("<polyline points=\"%s\" fill=\"%s\" %s />",
					sbPointList,
					fillColor,
					opacity
					);
			
		} else {
			return String.format("<polyline points=\"%s\" fill=\"none\" stroke=\"%s\" stroke-width=\"%g\" %s />",
					sbPointList,
					getStrokeColor(),
					getStrokeWidth(),
					opacity
					);
		}
		
	}

	/**
	 * SVG <path/> による多角形描画
	 * 
	 * @param posList 点の系列
	 * @param isClosed true の場合は閉じる
	 * @param isFilled true の場合は塗りつぶす
	 * @return
	 */
	public String path(ArrayList<Pos> posList, boolean isClosed, boolean isFilled) {
		/*
		 * SVGの属性の指定例:
		 * fill="purple"
		 * fill-opacity="0.5"
	 	 * stroke-opacity="0.8"
		 */
		
		
		StringBuilder sbPointList = new StringBuilder();

		boolean isFirst = true;
		for (Pos p: posList) {
			String command = isFirst ? "M" : "L";
			if (isFirst) {
					isFirst = false;
			} else {
				sbPointList.append(" ");
		}
			
			sbPointList.append(String.format("%s %g %g", command, x2double(p.getX()), y2double(p.getY())));
		}
		if (isClosed) {
			sbPointList.append(" Z");
		}

		String fillColor;
		if (isFilled) {
			fillColor = getStrokeColor();
		} else {
			fillColor = "none";
		}

		String opacity;
		if (this.hasAlpha()) {
			if (isFilled) {
				opacity = String.format("fill-opacity=\"%s\"", this.getAlphaString());
			} else {
				opacity = String.format("stroke-opacity=\"%s\"", this.getAlphaString());
			}
		} else {
			opacity = "";
		}
		
		if (isFilled) {
			return String.format("<path d=\"%s\" fill=\"%s\" %s />",
					sbPointList,
					fillColor,
					opacity
					);
			
		} else {
			return String.format("<path d=\"%s\" fill=\"none\" stroke=\"%s\" stroke-width=\"%g\" %s />",
					sbPointList,
					getStrokeColor(),
					getStrokeWidth(),
					opacity
					);
		}
		
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
	
	public boolean hasAlpha() {
		return this.strokeAlpha != 1.0;
	}
	
	public String getAlphaString() {
		return String.format("%.5f", this.strokeAlpha);
	}

	public void setStrokeAlpha(double alphaValue) {
		this.strokeAlpha = alphaValue;
	}
}