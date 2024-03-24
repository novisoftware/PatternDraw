package com.github.novisoftware.patternDraw.gui.misc;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;

/**
 * JLabelの見た目を変更したもの。
 */
public class JLabel2 extends JLabel {
	public JLabel2(String text) {
		super(text);
		this.setFont(Preference.LABEL_FONT);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY);
		super.paintComponent(g2);
	}
}
