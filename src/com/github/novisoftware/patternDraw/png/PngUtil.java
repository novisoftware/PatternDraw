package com.github.novisoftware.patternDraw.png;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.novisoftware.patternDraw.geometricLanguage.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.InvaliScriptException;
import com.github.novisoftware.patternDraw.renderer.Renderer;

public class PngUtil {
	/*
	static int X = 1;
	static int Y = 1;
	*/
	static int X = 5;
	static int Y = 5;

	public static void outPng(String filename, InstructionRenderer renderer, int width, int height) throws IOException, InvaliScriptException {
		int repeatedW = width * (X);
		int repeatedH = height * (Y);

		BufferedImage buffer = new BufferedImage(repeatedW, repeatedH,
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = (Graphics2D)buffer.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, repeatedW, repeatedH);
		for (int x = 0; x < X ; x++) {
			for (int y = 0; y < Y; y++) {
				g.setColor(Color.WHITE);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				renderer.init(g, null, null);
				renderer.setTranslate(x * 2.6, y * 2.6);
				renderer.run();
				renderer.render(g, null, null);
			}
		}
		g.dispose();

		ImageIO.write(buffer, "png", new File(filename));
	}
}
