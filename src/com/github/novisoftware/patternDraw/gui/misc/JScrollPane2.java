package com.github.novisoftware.patternDraw.gui.misc;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import com.github.novisoftware.patternDraw.utils.GuiPreference;

public class JScrollPane2 extends JScrollPane {

	public JScrollPane2(JComponent c) {
		super(c);
		this.getVerticalScrollBar().setUnitIncrement(GuiPreference.MOUSE_WHEEL_SCROLL_AMOUNT);
	}
}
