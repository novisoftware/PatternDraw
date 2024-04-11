package com.github.novisoftware.patternDraw.gui;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.github.novisoftware.patternDraw.geometricLanguage.lang.typeSystem.ObjectHolder;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.EnumParameter;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.Int2Double;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.Parameter;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.SliderParameter;
import com.github.novisoftware.patternDraw.gui.misc.JFrame2;
import com.github.novisoftware.patternDraw.gui.misc.JLabel2;
import com.github.novisoftware.patternDraw.gui.misc.Preference;

public class SettingWindow extends JFrame2 {
	static class OnFixActionListener implements ActionListener {
		SettingWindow settingWindow;
		boolean isDisposeWindow;
		OnFixActionListener(SettingWindow settingWindow, boolean isDisposeWindow) {
			this.settingWindow = settingWindow;
			this.isDisposeWindow = isDisposeWindow;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			for (Parameter param : settingWindow.params) {
				JTextField field = settingWindow.textFields.get(param.getName());
				ObjectHolder value = new ObjectHolder(field.getText());
				settingWindow.variables.put(param.getName(), value);
			}
			settingWindow.callback.run();
			settingWindow.dispose();
		}
	}

	static class OnChangeActionListener implements ActionListener {
		SettingWindow settingWindow;
		final Runnable callback;
		OnChangeActionListener(SettingWindow settingWindow,
				final Runnable callback
				) {
			this.settingWindow = settingWindow;
			this.callback = callback;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean isFilled = true;
			for (Parameter param : settingWindow.params) {
				JTextField field = settingWindow.textFields.get(param.getName());
				String text = field.getText();
				if (text.length() > 0) {
					ObjectHolder value = new ObjectHolder(text);
					settingWindow.variables.put(param.getName(), value);
				} else {
					isFilled = false;
				}
			}
			if (isFilled) {
				settingWindow.callback.run();
			}
		}
	}

	static class SliederChangeListener implements ChangeListener {
		final JSlider slider;
		final JTextField tf;
		final Int2Double cv;
		final OnChangeActionListener onChangeActionListener;

		SliederChangeListener(JSlider slider, JTextField tf, Int2Double cv,
				OnChangeActionListener onChangeActionListener) {
			this.slider = slider;
			this.tf = tf;
			this.cv = cv;
			this.onChangeActionListener = onChangeActionListener;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			String text = String.format("%f", cv.int2double(slider.getValue(), slider.getMaximum()));
			tf.setText(text);
			onChangeActionListener.actionPerformed(null);
		}
	}

	public static final int WINDOW_POS_X = 20;
	public static final int WINDOW_POS_Y = 20;
	public static final int WINDOW_WIDTH = 600;
	public static final int WINDOW_HEIGHT = 250;

	final ArrayList<Parameter> params;
	final HashMap<String, ObjectHolder> variables;
	final Runnable callback;
	final HashMap<String,JTextField> textFields;

	public SettingWindow(
			final ArrayList<Parameter> params,
			final HashMap<String, ObjectHolder> variables,
			final Runnable callback,
			final boolean isDisposeWindow) {
		super();

		this.params = params;
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

		OnChangeActionListener listner1 = null;
		if (! isDisposeWindow) {
			listner1 = new OnChangeActionListener(this, callback);
		}


		this.textFields = new HashMap<String,JTextField>();
		pane.add(new JLabel2("条件を指定します。"));
		// for (String varName : items.keySet()) {
		for (Parameter param : params) {
			// 不可視の水平線を作成する (レイアウトの調整)
			addHorizontalRule(pane, 5);

			pane.add(new JLabel2(param.getDescription()));
			final JTextField field = new JTextField(10);
			field.setText(param.getDefaultValue());
			pane.add(field);
			textFields.put(param.getName(), field);

			if (! isDisposeWindow) {
				field.addActionListener(listner1);
			}

			if (param instanceof SliderParameter) {
				JSlider slider = new JSlider(0, 500);
				slider.addChangeListener(new SliederChangeListener(slider,
						field,
						(Int2Double)param,
						listner1));
				pane.add(slider);
			}

			if (param instanceof EnumParameter) {
				EnumParameter e = (EnumParameter)param;
				ButtonGroup group = new ButtonGroup();
				for (String value : e.enums) {
					final JRadioButton radioButton = new JRadioButton(value);
					group.add(radioButton);
					pane.add(radioButton);

					if (! isDisposeWindow) {
						final OnChangeActionListener l2 = listner1;

						radioButton.addChangeListener(new ChangeListener() {
							public void stateChanged(ChangeEvent e) {
								if (radioButton.isSelected()) {
									field.setText(value);
									l2.actionPerformed(null);
								}
							}
						});
					}
					else {
						radioButton.addChangeListener(new ChangeListener() {
							public void stateChanged(ChangeEvent e) {
								if (radioButton.isSelected()) {
									field.setText(value);
								}
							}
						});
					}
				}

			}

		}


		// 不可視の水平線を作成する (レイアウトの調整)
		addHorizontalRule(pane, 10);

		if (isDisposeWindow) {
			JButton runButton = new JButton("決定");
			pane.add(runButton);


			runButton.addActionListener(new OnFixActionListener(this, isDisposeWindow));
		}

		this.setVisible(true);
	}

}