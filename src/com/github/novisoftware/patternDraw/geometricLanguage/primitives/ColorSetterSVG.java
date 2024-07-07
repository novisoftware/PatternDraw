package com.github.novisoftware.patternDraw.geometricLanguage.primitives;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import com.github.novisoftware.patternDraw.renderer.Renderer;
import com.github.novisoftware.patternDraw.svg.SvgInstruction;
import com.github.novisoftware.patternDraw.utils.Debug;

public class ColorSetterSVG implements Renderer {
	private Color color;
	private String colorString;
	private double alphaValue;

	public ColorSetterSVG(Color color) {
		this.color = color;
		this.colorString = String.format("#%06x", (this.color.getRGB() & 0xFFFFFF));
		this.alphaValue = this.color.getAlpha() / (double)0xFF;
		// Debug.println("SET SVG STROKE COLOR " + colorString);
	}

	@Override
	public void render(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s) {
		if (s != null) {
			s.setStrokeColor(colorString);
			s.setStrokeAlpha(this.alphaValue);
		}
	}
}
