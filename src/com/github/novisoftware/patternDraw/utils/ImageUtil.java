package com.github.novisoftware.patternDraw.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.novisoftware.patternDraw.renderer.Renderer;

public class ImageUtil {
	static BufferedImage createBitmap(int width, int height, Renderer renderer) {
		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D)bimg.getGraphics();

		Color fg = new Color(0.7f, 0.7f, 0.7f);
		Color bg = new Color(1f, 1f, 1f);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setBackground(bg);
		g.clearRect(0, 0, width, height);
		g.setColor(fg);

		renderer.render(g, null, null);

		return bimg;
	}

	static void outPngFile(String f, Renderer renderer) {
		final int W = 1920, H = 1080;

		BufferedImage bimg = ImageUtil.createBitmap(W, H, renderer);
		try {
			ImageIO.write(bimg, "png", new File(f));
		} catch (IOException e) {
			System.err.println(e.toString());
			System.exit(1);
		}
	}
}
