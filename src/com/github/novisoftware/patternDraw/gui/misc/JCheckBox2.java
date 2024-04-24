package com.github.novisoftware.patternDraw.gui.misc;

import javax.swing.JCheckBox;

import com.github.novisoftware.patternDraw.utils.Preference;

public class JCheckBox2 extends JCheckBox {
	public JCheckBox2() {
		super();

		this.setBackground(Preference.BG_COLOR);
	    this.setForeground(Preference.TEXT_COLOR);
	}
}
