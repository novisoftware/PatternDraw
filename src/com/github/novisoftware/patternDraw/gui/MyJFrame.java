package com.github.novisoftware.patternDraw.gui;

import java.awt.BorderLayout;
import javax.swing.JFrame;

import com.github.novisoftware.patternDraw.renderer.Renderer;

public class MyJFrame extends JFrame {
	MyJPanel panel;

	public MyJFrame(MyJPanel panel) {
		this.panel = panel;
		this.getContentPane().add(panel, BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
