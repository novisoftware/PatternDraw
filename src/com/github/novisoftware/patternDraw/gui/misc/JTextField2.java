package com.github.novisoftware.patternDraw.gui.misc;

import java.awt.Dimension;

import javax.swing.JTextField;

import com.github.novisoftware.patternDraw.utils.GuiPreference;

public class JTextField2 extends JTextField {
	public JTextField2(String s) {
		super(s);

		this.setPreferredSize(new Dimension(200,20));
		this.setFont(GuiPreference.INPUT_FONT);
	}

}
