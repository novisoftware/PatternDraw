package com.github.novisoftware.patternDraw.gui;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTextField;

import com.github.novisoftware.patternDraw.geometricLanguage.ObjectHolder;
import com.github.novisoftware.patternDraw.gui.misc.JFrame2;
import com.github.novisoftware.patternDraw.gui.misc.JLabel2;
import com.github.novisoftware.patternDraw.gui.misc.Preference;

public class SettingWindow extends JFrame2 {
	static class OnFixActionListener implements ActionListener {
		SettingWindow settingWindow;
		OnFixActionListener(SettingWindow settingWindow) {
			this.settingWindow = settingWindow;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			for (String varName : settingWindow.items.keySet()) {
				JTextField field = settingWindow.textFields.get(varName);
				ObjectHolder value = new ObjectHolder(field.getText());
				settingWindow.variables.put(varName, value);
			}
			settingWindow.callback.run();
			settingWindow.dispose();
		}
	}


	static int WINDOW_POS_X = 20;
	static int WINDOW_POS_Y = 20;
	static int WINDOW_WIDTH = 600;
	static int WINDOW_HEIGHT = 250;

	final LinkedHashMap<String, String> items;
	final HashMap<String, ObjectHolder> variables;
	final Runnable callback;
	final HashMap<String,JTextField> textFields;

	public SettingWindow(
			final LinkedHashMap<String, String> items,
			final HashMap<String, ObjectHolder> variables,
			final Runnable callback) {
		super();

		this.items = items;
		this.variables = variables;
		this.callback = callback;

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

		this.textFields = new HashMap<String,JTextField>();

		pane.add(new JLabel2("条件を指定します。"));
		for (String varName : items.keySet()) {
			// 不可視の水平線を作成する (レイアウトの調整)
			addHorizontalRule(pane, 5);

			pane.add(new JLabel2(items.get(varName)));
			JTextField field = new JTextField(10);
			pane.add(field);

			textFields.put(varName, field);
		}


		// 不可視の水平線を作成する (レイアウトの調整)
		addHorizontalRule(pane, 10);

		JButton runButton = new JButton("決定");
		pane.add(runButton);


		runButton.addActionListener(new OnFixActionListener(this));

		/*
		final SettingWindow thisFrame = this;

		runButton.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						for (String varName : items.keySet()) {
							JTextField field = textFields.get(varName);
							ObjectHolder value = new ObjectHolder(field.getText());
							variables.put(varName, value);
						}
						callback.run();
						thisFrame.dispose();
					}
				}
				);
		*/

		this.setVisible(true);
	}

}