package com.github.novisoftware.patternDraw.gui.editor.guiMain;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.github.novisoftware.patternDraw.gui.editor.guiParts.ControlElement;
import com.github.novisoftware.patternDraw.gui.editor.core.RpnUtil;
import com.github.novisoftware.patternDraw.gui.editor.guiMenu.ElementFactory;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.ConnectTerminal;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.IconGuiInterface;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement.KindId;
import com.github.novisoftware.patternDraw.gui.misc.JFrame2;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.GuiUtil;
import com.github.novisoftware.patternDraw.utils.Preference;

public class OutputTextWindow extends JFrame2 {
	static private OutputTextWindow singleton;

	static public OutputTextWindow getInstance() {
		if (singleton == null) {
			singleton = new OutputTextWindow();
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

	private OutputTextWindow() {
		textArea = new JTextArea();
		textArea.setFont(Preference.CONSOLE_FONT);
		JScrollPane scrollPane = new JScrollPane(textArea);
//		p.add(a);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

	    this.setSize(600, 600);
		this.setVisible(true);
		this.setTitle(" テキストの出力");
	}
}
