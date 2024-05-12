package com.github.novisoftware.patternDraw.gui.misc;

import javax.swing.JRadioButton;

import com.github.novisoftware.patternDraw.utils.Preference;

public class JRadioButton2 extends JRadioButton {
	public JRadioButton2(String s) {
		super(s);
		this.setFont(Preference.LABEL_FONT);
		this.setBackground(Preference.BG_COLOR);
		this.setForeground(Preference.TEXT_COLOR);
	}
}
