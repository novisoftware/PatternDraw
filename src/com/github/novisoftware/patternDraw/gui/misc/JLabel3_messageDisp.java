package com.github.novisoftware.patternDraw.gui.misc;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;

import com.github.novisoftware.patternDraw.utils.GuiPreference;

/**
 * JLabelの見た目を変更したもの。
 */
public class JLabel3_messageDisp extends JLabel {
	public JLabel3_messageDisp(String text) {
		super(text);
		this.setFont(GuiPreference.MESSAGE_DISP_FONT);
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
