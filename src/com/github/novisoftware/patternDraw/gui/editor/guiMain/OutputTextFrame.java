package com.github.novisoftware.patternDraw.gui.editor.guiMain;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.github.novisoftware.patternDraw.gui.editor.guiParts.ControlElement;
import com.github.novisoftware.patternDraw.gui.editor.core.RpnUtil;
import com.github.novisoftware.patternDraw.gui.editor.guiMenu.ElementFactory;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.GraphConnector;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.IconGuiInterface;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement.KindId;
import com.github.novisoftware.patternDraw.gui.editor.util.Common;
import com.github.novisoftware.patternDraw.gui.editor.util.Debug;
import com.github.novisoftware.patternDraw.gui.misc.Preference;

public class OutputTextFrame extends JFrame {
	static private OutputTextFrame singleton;

	static public OutputTextFrame getInstance() {
		if (singleton == null) {
			singleton = new OutputTextFrame();
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

	private OutputTextFrame() {
		textArea = new JTextArea();
		textArea.setFont(Preference.CONSOLE_FONT);
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
