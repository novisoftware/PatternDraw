package com.github.novisoftware.patternDraw.geometricLanguage.lang;

import java.awt.Graphics2D;
import java.util.ArrayList;

import com.github.novisoftware.patternDraw.renderer.AbstractRenderer;
import com.github.novisoftware.patternDraw.renderer.Renderer;
import com.github.novisoftware.patternDraw.svg.SvgInstruction;
import com.github.novisoftware.patternDraw.utils.Debug;

public class InstructionRenderer extends AbstractRenderer {
	int counter;
	public Graphics2D g;
	public ArrayList<String> svgBuff;
	public SvgInstruction s;
	public ArrayList<Renderer> primitiveList;
	public String currentStrokeColor = "black";
	public String currentStrokeWidth = "1";
	public double translateX;
	public double translateY;

	public void setTranslate(double x, double y) {
		this.translateX = x;
		this.translateY = y;
	}

	public void addTranslate(double x, double y) {
		this.translateX += x;
		this.translateY += y;
	}

	public void init(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s) {
		this.g = g;
		this.svgBuff = svgBuff;
		this.s = s;
		this.counter = 0;
		this.primitiveList = new ArrayList<Renderer>();
		this.translateX = 0;
		this.translateY = 0;
	}

	void Debug(String s) {
		System.out.println(s);
	}

	public void render(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s) {
		Debug.println("renderer", "primitiveList size is " + primitiveList.size());

		for (Renderer p : primitiveList) {
			Debug.println("renderer", p.getClass().toString());
			p.render(g, svgBuff, s);
		}
	}
}
