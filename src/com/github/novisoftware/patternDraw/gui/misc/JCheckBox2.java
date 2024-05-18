package com.github.novisoftware.patternDraw.gui.misc;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JCheckBox;

import com.github.novisoftware.patternDraw.utils.GuiPreference;

public class JCheckBox2 extends JCheckBox {
	public JCheckBox2() {
		super();

		this.setBackground(GuiPreference.BG_COLOR);
	    this.setForeground(GuiPreference.TEXT_COLOR);
	}

	public JCheckBox2(String s) {
		super(s);

		this.setBackground(GuiPreference.BG_COLOR);
	    this.setForeground(GuiPreference.TEXT_COLOR);
		this.setFont(GuiPreference.LABEL_FONT);
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
