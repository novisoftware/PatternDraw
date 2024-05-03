package com.github.novisoftware.patternDraw.gui.editor.guiMain;

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
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefine;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.SliderParameter;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.misc.JFrame2;
import com.github.novisoftware.patternDraw.gui.misc.JLabel2;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.Preference;


/**
 * GUI完結のパラメーター設定ウィンドウ
 *
 *
 * @author user
 *
 */

public class EditParamWindow extends JFrame2 {
	public static final int WINDOW_POS_X = 20;
	public static final int WINDOW_POS_Y = 20;
	public static final int WINDOW_WIDTH = 600;
	public static final int WINDOW_HEIGHT = 250;

	private final ArrayList<ParameterDefine> paramDefList;
	private final HashMap<String, Value> variables;
	private Runnable callback;
	private final HashMap<String,JTextField> textFields;

	/**
	 *
	 * @param paramDefList パラメーターを設定するために必要な、型や名称の定義情報
	 * @param variables 設定したパラメーターの値
	 */
	public EditParamWindow(
			final ArrayList<ParameterDefine> paramDefList
			) {
		super();

		this.paramDefList = paramDefList;
		this.variables = new HashMap<String, Value>();

		// this.display = display;
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setLocation(WINDOW_POS_X, WINDOW_POS_Y);

		this.setTitle("条件を指定します");

		// レイアウト
		Container pane = this.getContentPane();
		this.setLayout(new FlowLayout(FlowLayout.LEADING));

		this.textFields = new HashMap<String,JTextField>();
		pane.add(new JLabel2("条件を指定します。"));
		// for (String varName : items.keySet()) {
		for (ParameterDefine param : paramDefList) {
			OnChangeActionListener listner1 = null;
			listner1 = new OnChangeActionListener(this, param, callback);

			// 不可視の水平線を作成する (レイアウトの調整)
			addHorizontalRule(pane, 5);

			pane.add(new JLabel2(param.description));
			final JTextField field = new JTextField(10);
			field.setText(param.defaultValue);
			pane.add(field);
			textFields.put(param.name, field);

			field.addActionListener(listner1);


			if (param.enableSlider) {
				// TODO:
				// ここでSliderParameterを作るツギハギ感。
				// クラス階層 Parameter は本当に必要?
				SliderParameter p = new SliderParameter(param.name, param.description, param.defaultValue,
						Double.parseDouble(param.sliderMin), Double.parseDouble(param.sliderMax));

				JSlider slider = new JSlider(0, 500);
				slider.addChangeListener(new SliederChangeListener(slider,
						field,
						p,
						listner1));
				pane.add(slider);
			}

			// 列挙パラメーターは後で
			if (DUMMY_FALSE) {
				if (param.enableEnum) {
					// EnumParameter e = (EnumParameter)param;
					EnumParameter e = null;//(EnumParameter)param;
					ButtonGroup group = new ButtonGroup();
					for (String value : e.enums) {
						final JRadioButton radioButton = new JRadioButton(value);
						group.add(radioButton);
						pane.add(radioButton);

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

		this.setVisible(true);
	}

	/**
  	 * @param callback パラメーター変更時に呼び出すコールバック
	 */
	public void setCallback(Runnable callback) {
		this.callback = callback;
	}

	/**
	 * @return variables
	 */
	public HashMap<String, Value> getVariables() {
		return variables;
	}

	public boolean DUMMY_FALSE = false;

	static class OnChangeActionListener implements ActionListener {
		final EditParamWindow settingWindow;
		final Runnable callback;
		OnChangeActionListener(EditParamWindow settingWindow,
				ParameterDefine param, final Runnable callback
				) {
			this.settingWindow = settingWindow;
			this.callback = callback;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			changeAction();
		}

		public void changeAction() {
			boolean isFilled = true;
			for (ParameterDefine param : settingWindow.paramDefList) {
				JTextField field = settingWindow.textFields.get(param.name);
				String text = field.getText();
				if (text.length() > 0) {
					Value value = Value.createValue(param.valueType, text);
					settingWindow.getVariables().put(param.name, value);


					Debug.println("" + param.name + " に " + text + " を設定。");
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
			onChangeActionListener.changeAction();
		}
	}
}