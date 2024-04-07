package com.github.novisoftware.patternDraw.gui.editor;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.github.novisoftware.patternDraw.gui.editor.util.RpnUtil;
import com.github.novisoftware.patternDraw.gui.editor.parts.ElementIcon;
import com.github.novisoftware.patternDraw.gui.editor.parts.ElementIcon.KindId;
import com.github.novisoftware.patternDraw.gui.editor.EditFrame.MListener;
import com.github.novisoftware.patternDraw.gui.editor.parts.ControlBlock;
import com.github.novisoftware.patternDraw.gui.editor.parts.ElementGenerator;
import com.github.novisoftware.patternDraw.gui.editor.parts.ElementIcon;
import com.github.novisoftware.patternDraw.gui.editor.parts.GraphConnector;
import com.github.novisoftware.patternDraw.gui.editor.parts.GraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.parts.IconGuiInterface;
import com.github.novisoftware.patternDraw.gui.editor.util.Common;
import com.github.novisoftware.patternDraw.gui.editor.util.Debug;
import com.github.novisoftware.patternDraw.gui.editor.parts.ControlBlock;
import com.github.novisoftware.patternDraw.gui.editor.parts.ElementGenerator;
import com.github.novisoftware.patternDraw.gui.editor.parts.ElementIcon;
import com.github.novisoftware.patternDraw.gui.editor.parts.GraphConnector;
import com.github.novisoftware.patternDraw.gui.editor.parts.GraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.parts.IconGuiInterface;

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
