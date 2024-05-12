package com.github.novisoftware.patternDraw.gui.editor.guiMain;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.github.novisoftware.patternDraw.geometricLanguage.lang.typeSystem.ObjectHolder;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.EnumParameter;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.Int2Double;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.Parameter;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefine;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.SliderParameter;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.ValueBoolean;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.AbstractInputChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.BooleanChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.FloatChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.IntegerChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.NonCheckChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.NumericChecker;
import com.github.novisoftware.patternDraw.gui.misc.JFrame2;
import com.github.novisoftware.patternDraw.gui.misc.JLabel2;
import com.github.novisoftware.patternDraw.gui.misc.JRadioButton2;
import com.github.novisoftware.patternDraw.gui.misc.JTextField2;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.Preference;


/**
 * プログラムを開始するときのパラメーター設定ウィンドウ
 * (ダイアグラムの言語)
 *
 */
public class EditParamWindow extends JFrame2 {
	public static final int WINDOW_POS_X = 50;
	public static final int WINDOW_POS_Y = 50;
	public static final int WINDOW_WIDTH = 640;
	public static final int WINDOW_HEIGHT = 600;

	private final ArrayList<ParameterDefine> paramDefList;
	private final HashMap<String, Value> variables;
	private Runnable callback;
	private final HashMap<String,JTextField> textFields;

	private final HashSet<AbstractInputChecker> ngInputs;

	static class CheckMessageLabel extends JLabel2 {
		/** エラー時の文字色 */
		private final Color COLOR_ERROR = Preference.MESSAGE_ERROR_COLOR;
		/** 正常時の文字色 */
		private final Color COLOR_NORMAL = Preference.TEXT_COLOR;
		private final Font MESSAGE_DISP_FONT = Preference.MESSAGE_DISP_FONT;

		/**
		 * チェック処理
		 */
		private final AbstractInputChecker checker;
		/**
		 * 不正な(invalidな)入力を含む場合に COMMIT ボタンを押させないなどの制御に使う
		 */
		private final Set<AbstractInputChecker> ngInputs;
		private final Let let;

		/**
		 * パラメーターの変更をトリガーにしてダイヤグラム実行する等の用途
		 */
		private final Runnable callback;

		public CheckMessageLabel(
				AbstractInputChecker checker,
				Set<AbstractInputChecker> ngInputs,
				Let let,
				Runnable callback) {
			super(checker.message);
			this.setFont(MESSAGE_DISP_FONT);
			this.checker = checker;
			this.ngInputs = ngInputs;
			this.let = let;
			this.callback = callback;
		}

		void updateMessage(String text) {
			checker.check(text);
			if (checker.isOk()) {
				ngInputs.remove(checker);
				this.setForeground(COLOR_NORMAL);
				this.setText(checker.message);
			} else {
				ngInputs.add(checker);
				this.setForeground(COLOR_ERROR);
				this.setText(checker.message);
			}

			if (checker.isOk()) {
				let.let(text);
			}

			if (ngInputs.isEmpty()) {
				callback.run();
			}
		}
	}

	static interface Let {
		public void let(String s);
	}

	/**
	 *
	 * @param textField
	 * @param checker
	 * @param ngInputs
	 * @param let チェック処理でOKだった場合(validだった場合)の代入動作
	 * @param callback (パラメーターの変更をトリガーにしてダイヤグラム実行する等の用途)
	 * @return
	 */
	static CheckMessageLabel setupCheckerToTextField(JTextField textField,
			AbstractInputChecker checker,
			Set<AbstractInputChecker> ngInputs,
			final Let let,
			Runnable callback
			) {
		final CheckMessageLabel check = new CheckMessageLabel(checker, ngInputs, let, callback);
		textField.getDocument().addDocumentListener(new DocumentListener() {
			void update() {
				String text = textField.getText();
				check.updateMessage(text);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				// update()に移譲
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// update()に移譲
				update();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// update()に移譲
				update();
			}
		});

		return check;
	}

	static interface BooleanLet {
		void let(ValueBoolean b);
	}

	static void addPaneToBooleanSelector(
			final Container pane,
			final ValueBoolean initValue,
			final BooleanLet let,
			final Runnable callback) {
		JRadioButton radioButtons[] = new JRadioButton[2];
		ButtonGroup group = new ButtonGroup();
		ValueBoolean[] values = {ValueBoolean.TRUE, ValueBoolean.FALSE};
		for (int i = 0 ; i < values.length ; i ++) {
			final JRadioButton radioButton = new JRadioButton2(values[i].toString());
			group.add(radioButton);
			radioButtons[i] = radioButton;
			pane.add(radioButton);
		}
		Debug.println("radioButtons  initValue = " + initValue);
		Debug.println("radioButtons  initValue.getInternal() = " + initValue.getInternal());
		for (int i = 0 ; i < values.length ; i ++) {
			Debug.println("radioButtons  values[i].getInternal() = " + values[i].getInternal());
			if (initValue != null && initValue.getInternal().equals(values[i].getInternal())) {
				Debug.println("radioButtons["  + i + "].setSelected(true);");
				radioButtons[i].setSelected(true);
			}
		}
		for (int i = 0 ; i < values.length ; i ++) {
			final JRadioButton radioButton = radioButtons[i];
			final ValueBoolean newValue = values[i];
			radioButton.addChangeListener(new ChangeListener() {
				Boolean isSelectedOld = initValue == null ? null:
											(initValue.getInternal() == newValue.getInternal());

				@Override
				public void stateChanged(ChangeEvent e) {
					if (radioButton.isSelected()) {
						let.let(newValue);

						if (isSelectedOld != null || isSelectedOld != radioButton.isSelected()) {
							callback.run();
						}
						isSelectedOld = radioButton.isSelected();
					}
				}
			});
		}
	}

	static class EditParamPanel extends JPanel {
		EditParamPanel() {
		    this.setBackground(Preference.BG_COLOR);
		    this.setForeground(Preference.TEXT_COLOR);
	    }
	}


	static class SubPanel extends JPanel {
		SubPanel() {
		    this.setBackground(Preference.BG_COLOR);
		    this.setForeground(Preference.TEXT_COLOR);
		    this.setLayout(new FlowLayout(FlowLayout.LEADING));
	    }
	}


	/**
	 *
	 * @param paramDefList パラメーターを設定するために必要な、型や名称の定義情報
	 * @param variables 設定したパラメーターの値
	 */
	public EditParamWindow(
			final ArrayList<ParameterDefine> paramDefList
			) {
		super();

		EditParamPanel editParamPanel = new EditParamPanel();
		// editParamPanel.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		JScrollPane sp = new JScrollPane(editParamPanel);

		/**
		 * このコンストラクタが呼ばれたときは callback はまだ設定されていないので。
		 */
		final EditParamWindow thisWindow = this;
		Runnable callbackWraped = new Runnable() {
			@Override
			public void run() {
				if (thisWindow.callback != null) {
					thisWindow.callback.run();
				}
			}
		};

		this.paramDefList = paramDefList;
		this.variables = new HashMap<String, Value>();
		this.ngInputs = new HashSet<AbstractInputChecker>();

		// this.display = display;
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setLocation(WINDOW_POS_X, WINDOW_POS_Y);

		this.setTitle("条件を指定します");

		// レイアウト
		/*
		Container pane = this.getContentPane();
		*/
		JPanel pane = editParamPanel;

		// pane.setLayout(new FlowLayout(FlowLayout.LEADING));
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

		this.textFields = new HashMap<String,JTextField>();
		SubPanel subPanel0 = new SubPanel();
		pane.add(subPanel0);
		subPanel0.add(new JLabel2("実行パラメーターを指定します。"));
		// for (String varName : items.keySet()) {
		for (ParameterDefine param__ : paramDefList) {
			final ParameterDefine param = param__;
			OnChangeActionListener listner1 = null;
			listner1 = new OnChangeActionListener(this, param, callbackWraped);

			SubPanel subPanel1 = new SubPanel();
			pane.add(subPanel1);
			subPanel1.add(new JLabel2(param.description));

			boolean isDefaultSetted = false;

			if (param.defaultValue != null && (
					ValueType.STRING.equals(param.valueType) ||
					(param.defaultValue).length() > 0 ) ) {
				isDefaultSetted = true;
				Value value = Value.createValue(param.valueType, param.defaultValue);
				this.variables.put(param.name, value);

				Debug.println("initinal set " + param.name + " = (String) " + param.defaultValue);
			}

			if (ValueType.BOOLEAN.equals(param.valueType)) {
				SubPanel subPanel2 = new SubPanel();
				pane.add(subPanel2);

				BooleanLet let = new BooleanLet() {
					@Override
					public void let(ValueBoolean b) {
						thisWindow.getVariables().put(param.name, b);
					}
				};

				subPanel2.add(spacer(30));
				subPanel2.add(new JLabel2(param.name + " ="));
				addPaneToBooleanSelector(subPanel2, new ValueBoolean(param.defaultValue),
						let, callbackWraped);
				Debug.println("(defaultValue)  " + param.name + " ... " + param.defaultValue);
			}
			else {
				final JTextField2 field = new JTextField2(param.defaultValue);
				textFields.put(param.name, field);

				Let let = new Let() {
					@Override
					public void let(String text) {
						Value value = Value.createValue(param.valueType, text);
						thisWindow.getVariables().put(param.name, value);
					}
				};

				AbstractInputChecker c = null;
				if (ValueType.INTEGER.equals(param.valueType)) {
					c = new IntegerChecker();
				} else if (ValueType.FLOAT.equals(param.valueType)) {
					c = new FloatChecker();
				} else if (ValueType.NUMERIC.equals(param.valueType)) {
					c = new NumericChecker();
				} else if (ValueType.STRING.equals(param.valueType)) {
					c = new NonCheckChecker();
				}
				if (c != null) {
					////////////////
					c.check(param.defaultValue);
					CheckMessageLabel check = null;
					check = setupCheckerToTextField(field, c,
							this.ngInputs, let, callbackWraped);
					subPanel1.add(spacer(20));
					subPanel1.add(check);

					if (! isDefaultSetted) {
						this.ngInputs.add(c);
					}
				}
				else {
					field.addActionListener(listner1);
				}

				SubPanel subPanel2 = new SubPanel();
				pane.add(subPanel2);

				subPanel2.add(spacer(30));
				subPanel2.add(new JLabel2(param.name + " ="));
				subPanel2.add(field);

				// スライダー有効パラメーター
				if (param.enableSlider) {
					// TODO:
					// この時点でもともと param は変数として持っているのに。
					// ここでSliderParameterを作るツギハギ感。
					// クラス階層 Parameter は本当に必要?
					SliderParameter p = new SliderParameter(param.name, param.description, param.defaultValue,
							Double.parseDouble(param.sliderMin), Double.parseDouble(param.sliderMax));

					JSlider slider = new JSlider(0, 500);
					slider.addChangeListener(new SliederChangeListener(slider,
							field,
							p,
							listner1));

					SubPanel subPanel3 = new SubPanel();
					pane.add(subPanel3);
					subPanel3.add(spacer(30 + 20));
					subPanel3.add(new JLabel2("" + param.sliderMin + " "));
					subPanel3.add(slider);
					subPanel3.add(new JLabel2(" " + param.sliderMax));
				}

				// 列挙パラメーター
				if (param.enableEnum) {
					String[] opts = param.enumValueList.split(",");
					ArrayList<String> opts_ = new ArrayList<String>();
					for (String s : opts) {
						opts_.add(s);
					}

					EnumParameter e = new EnumParameter(
							param.name,
							param.description,
							param.defaultValue,
							opts_
							);
					ButtonGroup group = new ButtonGroup();

					SubPanel subPanel4 = new SubPanel();
					pane.add(subPanel4);

					subPanel4.add(spacer(30 + 20));
					for (String value : e.opts) {
						final JRadioButton radioButton = new JRadioButton2(value);
						group.add(radioButton);
						subPanel4.add(radioButton);

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

		// 「実行」ボタン
		// TODO
		// ・現状、たんにガワだけ。
		// ・「実行ボタンがある場合」は、ボタンを押してはじめて処理を行う。
		// ・必要かどうかは場面によりけりだと思う。
		//   テキストエディタの「インクリメンタル検索」が苦手な人もいるので。

		SubPanel subPanel8 = new SubPanel();
		pane.add(subPanel8);
		JButton buttonOk = new JButton(Preference.RUN_BUTTON_STRING);
		buttonOk.setFont(Preference.OK_BUTTON_FONT);
		subPanel8.add(buttonOk);
		SubPanel subPanel9 = new SubPanel();
		pane.add(subPanel9);
		// subPanel9.add(boxSpacer(5, 100));
		pane.add(Box.createGlue());
		pane.add(boxSpacer(5, 100));

		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setPreferredSize(editParamPanel.getPreferredSize());
		this.add(sp);
	}

	public boolean DUMMY_FALSE = true;

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
			// onChangeActionListener.changeAction();
		}
	}
}