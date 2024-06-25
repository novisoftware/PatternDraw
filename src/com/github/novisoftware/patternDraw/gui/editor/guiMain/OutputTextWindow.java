package com.github.novisoftware.patternDraw.gui.editor.guiMain;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.github.novisoftware.patternDraw.gui.misc.JFrame2;
import com.github.novisoftware.patternDraw.utils.GuiPreference;

public class OutputTextWindow extends JFrame2 implements OutputTextInterface {
	public static final int WINDOW_POS_X = EditParamWindow.WINDOW_POS_X;
	public static final int WINDOW_POS_Y = 20 + EditParamWindow.WINDOW_POS_Y + EditParamWindow.WINDOW_HEIGHT ;
	public static final int WINDOW_WIDTH = 640;
	public static final int WINDOW_HEIGHT = 240;


	static private OutputTextWindow singleton;

	static public OutputTextWindow getInstance() {
		if (singleton == null) {
			singleton = new OutputTextWindow();
		}

		return singleton;
	}

	public void print(String s) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					textArea.setText(
							textArea.getText() + s
							);
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	public void println(String s) {
		print(s + "\n");
	}

	public void clear() {
		textArea.setText("");
	}

	public final JTextArea textArea;

	private OutputTextWindow() {
		textArea = new JTextArea();
		textArea.setFont(GuiPreference.CONSOLE_FONT);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

	    this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setLocation(WINDOW_POS_X, WINDOW_POS_Y);
		this.setTitle(" テキストの出力");
	}
}
