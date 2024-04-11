package com.github.novisoftware.patternDraw.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InvaliScriptException;
import com.github.novisoftware.patternDraw.renderer.Renderer;

public class MyJPanel2 extends JPanel implements Runnable {
	private InstructionRenderer renderer;
	private BufferedImage buffer;

	public MyJPanel2(InstructionRenderer renderer, BufferedImage buffer) {
		this.renderer = renderer;
		this.buffer = buffer;
	}

	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.drawImage(buffer, 0, 0, null);
	}

	public void update() {
		Graphics2D g = (Graphics2D)buffer.getGraphics();

		Color fg = new Color(0.7f, 0.7f, 0.7f);
		Color bg = new Color(1f, 1f, 1f);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.setBackground(bg);
		g.setColor(bg);
		g.fillRect(0, 0, 1920, 1080);
		g.setColor(fg);
		renderer.init(g, null, null);
		try {
			renderer.run();
		} catch (InvaliScriptException e) {
			e.printStackTrace();
			return;
		}
		renderer.render(g, null, null);
	}

	@Override
	public void run() {
		this.update();
		this.repaint();
	}
}
