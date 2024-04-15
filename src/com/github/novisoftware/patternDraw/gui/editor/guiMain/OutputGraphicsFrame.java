package com.github.novisoftware.patternDraw.gui.editor.guiMain;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InvaliScriptException;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.typeSystem.ObjectHolder;
import com.github.novisoftware.patternDraw.geometricLanguage.token.TokenList;
import com.github.novisoftware.patternDraw.geometricLanguage.token.Token;
import com.github.novisoftware.patternDraw.gui.MyJPanel;
import com.github.novisoftware.patternDraw.gui.editor.util.Common;
import com.github.novisoftware.patternDraw.gui.misc.Preference;

public class OutputGraphicsFrame extends JFrame {
	static public int IMAGE_WIDTH = 800;
	static public int IMAGE_HEIGHT = 800;

	static private OutputGraphicsFrame singleton;

	static public OutputGraphicsFrame getInstance() {
		if (singleton == null) {
			singleton = new OutputGraphicsFrame();
		}

		return singleton;
	}

	class MyJPanel extends JPanel {
		private InstructionRenderer renderer;
		private BufferedImage buffer;

		public MyJPanel(InstructionRenderer renderer, BufferedImage buffer) {
			this.renderer = renderer;
			this.buffer = buffer;
			this.reset();
		}

		public void paint(Graphics graphics) {
			Graphics2D g = (Graphics2D) graphics;
			g.drawImage(buffer, 0, 0, null);
		}

		public void reset() {
			Graphics2D g = (Graphics2D)buffer.getGraphics();

			Color fg = new Color(0.7f, 0.7f, 0.7f);
			// Color bg = new Color(1f, 1f, 1f);
			Color bg = new Color(0f, 0f, 0f);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			this.setBackground(bg);
			g.setColor(bg);
			g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
			g.setColor(fg);
			renderer.init(g, null, null);
		}


		public void refresh() {
			Graphics2D g = (Graphics2D)buffer.getGraphics();

			renderer.render(g, null, null);
		}
	}

	MyJPanel panel;

	static public void reset() {
		getInstance().panel.reset();
	}

	static public void refresh() {
		getInstance().panel.refresh();
	}

	static public InstructionRenderer getRenderer() {
		return OutputGraphicsFrame.getInstance().panel.renderer;
	}

	/*
	static public void update() {
		getInstance().panel.update();
	}
	*/

	private OutputGraphicsFrame() {
		final InstructionRenderer renderer = new InstructionRenderer(new TokenList(new ArrayList<Token>()), new HashMap<String, ObjectHolder>());
		BufferedImage buffer = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);

		panel = new MyJPanel(renderer, buffer);
		this.getContentPane().add(panel, BorderLayout.CENTER);

		Common.setIconImage(this);
		this.setSize(IMAGE_WIDTH, IMAGE_HEIGHT);
		this.setTitle("PatternDraw (Graphics Preview)");
		this.setVisible(true);
	}
}
