package com.github.novisoftware.patternDraw.geometricLanguage.primitives;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.ArrayList;

import com.github.novisoftware.patternDraw.renderer.Renderer;
import com.github.novisoftware.patternDraw.svg.SvgInstruction;

public class LineWidthSetterPNG implements Renderer {
	private double strokeWidth;

	public LineWidthSetterPNG(double strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	@Override
	public void render(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s) {
		if (g != null) {
			BasicStroke stroke = new BasicStroke((float)this.strokeWidth);
			g.setStroke(stroke);
		}
	}
}
