package com.github.novisoftware.patternDraw.geometricLanguage.primitives;

import java.awt.Graphics2D;
import java.util.ArrayList;

import com.github.novisoftware.patternDraw.renderer.Renderer;
import com.github.novisoftware.patternDraw.svg.SvgInstruction;
import com.github.novisoftware.patternDraw.utils.Debug;

public class LineWidthSetterSVG implements Renderer {
	private double strokeWidth;

	public LineWidthSetterSVG(double strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	@Override
	public void render(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s) {
		if (s != null) {
			Debug.println("SET SVG STROKE WIDTH " + this.strokeWidth);
			s.setStrokeWidth(strokeWidth);
		}
	}
}
