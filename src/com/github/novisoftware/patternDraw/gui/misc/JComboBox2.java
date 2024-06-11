package com.github.novisoftware.patternDraw.gui.misc;

// import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JComboBox;

import com.github.novisoftware.patternDraw.utils.GuiPreference;

public class JComboBox2 extends JComboBox<String> {
	public JComboBox2(Vector<String> vec) {
		super(vec);

		// this.setPreferredSize(new Dimension(200,20));
		this.setFont(GuiPreference.INPUT_FONT);
	}

}
