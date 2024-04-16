package com.github.novisoftware.patternDraw.gui;

import java.awt.BorderLayout;
import javax.swing.JFrame;

import com.github.novisoftware.patternDraw.gui.misc.JFrame2;
import com.github.novisoftware.patternDraw.renderer.Renderer;

public class MyJFrame2 extends JFrame2 {
	MyJPanel2 panel;

	public MyJFrame2(MyJPanel2 panel) {
		this.panel = panel;
		this.getContentPane().add(panel, BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}