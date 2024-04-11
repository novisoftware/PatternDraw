package com.github.novisoftware.patternDraw.gui.editor.guiMain;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.github.novisoftware.patternDraw.gui.editor.util.RpnUtil;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.ControlElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.ElementGenerator;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.GraphConnector;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.IconGuiInterface;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement.KindId;
import com.github.novisoftware.patternDraw.gui.editor.util.Common;
import com.github.novisoftware.patternDraw.gui.editor.util.Debug;

public class OutputFrame extends JFrame {
	static private OutputFrame singleton;

	static public OutputFrame getInstance() {
		if (singleton == null) {
			singleton = new OutputFrame();
		}

		return singleton;
	}

	static public void print(String s) {
		singleton.textArea.setText(
		singleton.textArea.getText() + s
		);
	}

	static public void println(String s) {
		print(s + "\n");
	}


	static public void clear() {
		singleton.textArea.setText("");
	}

	public final JTextArea textArea;

	private OutputFrame() {
		textArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textArea);
//		p.add(a);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

	    this.setSize(600, 600);
		this.setVisible(true);
		this.setTitle(Common.FRAME_TITLE_BASE + " アプトプット");

		Common.setIconImage(this);
	}
}
