package com.github.novisoftware.patternDraw.gui.misc;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JCheckBox;

import com.github.novisoftware.patternDraw.utils.Preference;

public class JCheckBox2 extends JCheckBox {
	public JCheckBox2() {
		super();

		this.setBackground(Preference.BG_COLOR);
	    this.setForeground(Preference.TEXT_COLOR);
	}

	public JCheckBox2(String s) {
		super(s);

		this.setBackground(Preference.BG_COLOR);
	    this.setForeground(Preference.TEXT_COLOR);
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
