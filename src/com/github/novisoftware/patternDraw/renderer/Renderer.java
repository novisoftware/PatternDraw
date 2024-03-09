package com.github.novisoftware.patternDraw.renderer;

import java.awt.Graphics2D;
import java.util.ArrayList;

import com.github.novisoftware.patternDraw.svg.SvgInstruction;

public interface Renderer {
	public void render(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s);
}
