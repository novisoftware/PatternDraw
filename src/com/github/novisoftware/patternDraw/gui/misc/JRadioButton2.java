package com.github.novisoftware.patternDraw.gui.misc;

import javax.swing.JRadioButton;

import com.github.novisoftware.patternDraw.utils.GuiPreference;

public class JRadioButton2 extends JRadioButton {
	public JRadioButton2(String s) {
		super(s);
		this.setFont(GuiPreference.LABEL_FONT);
		this.setBackground(GuiPreference.BG_COLOR);
		this.setForeground(GuiPreference.TEXT_COLOR);
	}
}
