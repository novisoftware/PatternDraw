package com.github.novisoftware.patternDraw.geometricLanguage.primitives;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import com.github.novisoftware.patternDraw.renderer.Renderer;
import com.github.novisoftware.patternDraw.svg.SvgInstruction;

public class ColorSetterPNG implements Renderer {
	private Color color;

	public ColorSetterPNG(Color color) {
		this.color = color;
	}

	@Override
	public void render(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s) {
		if (g != null) {
			g.setColor(this.color);
		}
	}
}
