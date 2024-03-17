package com.github.novisoftware.patternDraw.gui;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.github.novisoftware.patternDraw.geometricLanguage.ObjectHolder;
import com.github.novisoftware.patternDraw.gui.misc.JFrame2;
import com.github.novisoftware.patternDraw.gui.misc.JLabel2;
import com.github.novisoftware.patternDraw.gui.misc.Preference;

public class SettingWindow extends JFrame2 {
	static int WINDOW_POS_X = 20;
	static int WINDOW_POS_Y = 20;
	static int WINDOW_WIDTH = 600;
	static int WINDOW_HEIGHT = 250;

	public SettingWindow(final String itemName,
			final String varName,
			final HashMap<String, ObjectHolder> variables,
			final Runnable callback) {
		super();

		// this.display = display;
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setLocation(WINDOW_POS_X, WINDOW_POS_Y);

		this.setTitle("条件を指定します");

		// レイアウト
		Container pane = this.getContentPane();
		this.setLayout(new FlowLayout(FlowLayout.LEADING));


		final JCheckBox check_simplyConnected = new JCheckBox();
		check_simplyConnected.setBackground(Preference.BG_COLOR);
		check_simplyConnected.setSelected(true);

		pane.add(new JLabel2("条件を指定します。"));
		// 不可視の水平線を作成する (レイアウトの調整)
		addHorizontalRule(pane, 5);

		pane.add(new JLabel2(itemName));
		JTextField field = new JTextField(10);
		pane.add(field);


		// 不可視の水平線を作成する (レイアウトの調整)
		addHorizontalRule(pane, 10);

		JButton runButton = new JButton("決定");
		pane.add(runButton);

		final SettingWindow thisFrame = this;
		runButton.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						ObjectHolder value = new ObjectHolder(field.getText());
						variables.put(varName, value);
						callback.run();
						thisFrame.dispose();
					}
				}
				);

		this.setVisible(true);
	}

}