package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashSet;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefineToEdit;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.AbstractInputChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.BooleanChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.FloatChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.IntegerChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.NonCheckChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.NumericChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.VariableNameChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditParameterDefinitionListWindow;
import com.github.novisoftware.patternDraw.gui.editor.util.Debug;
import com.github.novisoftware.patternDraw.gui.misc.JCheckBox2;
import com.github.novisoftware.patternDraw.gui.misc.JFrame2;
import com.github.novisoftware.patternDraw.gui.misc.JLabel2;
import com.github.novisoftware.patternDraw.gui.misc.JTextField2;
import com.github.novisoftware.patternDraw.utils.Preference;

/**
 *
 * パラメーター定義の編集を行う
 *
 */
public class InputParamDefWindow extends JFrame2 {
	final ParameterDefineToEdit param;
	final ParameterDefineToEdit backupParam;
	// final Parameter param;
	private final JButton buttonOk;
	final HashSet<String> variableNameSet;

	// チェック結果
	final JLabel messageDisp;
	final JLabel messageDisp_varName;
	final JLabel messageDisp_sliderMin;
	final JLabel messageDisp_sliderMax;

	/**
	 * 選択可能な型の種類
	 */
	final ValueType[] enableValueTypeList = { ValueType.INTEGER, ValueType.FLOAT, ValueType.NUMERIC, ValueType.BOOLEAN,
			ValueType.STRING };

	// 注: Parameter は型をもっていなかった。
	// (持たせたほうが良い)

	final ValueInputPanel field_name;
	final ValueInputPanel field_desc;
	final ValueInputPanel field_defalutValue;
	final JCheckBox check_enumEnable;
	final ValueInputPanel field_enumValue;
	final JCheckBox check_sliderEnable;
	final ValueInputPanel field_sliderMin;
	final ValueInputPanel field_sliderMax;

	final HashSet<ValueInputPanel> ngInputPanels;

	public InputParamDefWindow(// final RpnGraphNodeElement element,
			final EditParameterDefinitionListWindow parent, final ParameterDefineToEdit param,
			final HashSet<String> variableNameSet) {
		this.setTitle("パラメーターを設定します。");
		this.setSize(600, 800);

		// 変数名一覧の設定
		this.variableNameSet = variableNameSet;
		this.param = param;
		this.backupParam = param.clone();


		// 入力項目でNGになっているものがあるかどうかで、 OK ボタンを制御する
		this.ngInputPanels = new HashSet<ValueInputPanel>();


		// 変数名のチェック結果
		this.messageDisp_varName = new JLabel2(" ");

		// デフォルト値のチェック結果
		this.messageDisp = new JLabel2(" ");

		//
		field_defalutValue = new ValueInputPanel(this, new Let() {
			public void let(String s) {
				param.defaultValue = s;
			}
		}, "デフォルト値", param.defaultValue, new IntegerChecker(), messageDisp);

		////////////////////////////////////////////////////////////////////
		// レイアウト

		// レイアウト
		Container pane = this.getContentPane();
		this.setLayout(new FlowLayout(FlowLayout.LEADING));

		JLabel2 title = new JLabel2("パラメーターを編集します");
		pane.add(title);

		// 不可視の水平線を作成する (レイアウトの調整)
		addHorizontalRule__test(pane, 5);

		/////////////////////////
		// 変数名

		// 変数名のチェック結果
		pane.add(this.messageDisp_varName);

		field_name = new ValueInputPanel(this, new Let() {
			public void let(String s) {
				param.name = s;
			}
		}, "変数名:", param.name, new VariableNameChecker(this.variableNameSet), this.messageDisp_varName);
		pane.add(field_name);

		pane.add(horizontalRule(5));

		/////////////////////////
		// 説明
		field_desc = new ValueInputPanel(this, new Let() {
			public void let(String s) {
				param.description = s;
			}
		}, "説明:", param.description, new NonCheckChecker(), null);
		pane.add(field_name);

		pane.add(field_desc);
		pane.add(horizontalRule(5));

		/////////////////////////
		// 型
		pane.add(new JLabel2("型:"));
		pane.add(horizontalRule(1));

		// 型を切り替えるためのラジオボタン
		// TODO: 値チェックの更新
		// TODO: 押したときの値の保存
		putTypeSelectionParts(pane, this.enableValueTypeList, param.valueType, field_defalutValue);

		// 不可視の水平線を作成する (レイアウトの調整)
		addHorizontalRule__test(pane, 5);

		/////////////////////////
		// デフォルト値

		// デフォルト値のチェック結果
		pane.add(messageDisp);
		// 不可視の水平線を作成する (レイアウトの調整)
		addHorizontalRule__test(pane, 1);

		pane.add(field_defalutValue);
		pane.add(horizontalRule(5));

		/////////////////////////
		// 列挙値
		check_enumEnable = new JCheckBox2();
		pane.add(check_enumEnable);
		JLabel2 label_enumEnable = new JLabel2("ラジオボタンによる設定を有効にする");
		pane.add(label_enumEnable);

		pane.add(horizontalRule(1));

		field_enumValue = new ValueInputPanel(this, new Let() {
			public void let(String s) {
				param.enumValueList = s;
			}
		}, "列挙値:", param.enumValueList, new NonCheckChecker(), null);
		pane.add(field_enumValue);

		pane.add(horizontalRule(5));

		///////////////////
		// スライダー
		check_sliderEnable = new JCheckBox2();
		pane.add(check_sliderEnable);
		JLabel2 label_sliderEnable = new JLabel2("スライダーによる設定を有効にする");
		pane.add(label_sliderEnable);

		pane.add(horizontalRule(1));

		///////////////////
		// スライダー最小値
		// デフォルト値のチェック結果
		this.messageDisp_sliderMin = new JLabel2(" ");
		pane.add(messageDisp_sliderMin);
		pane.add(horizontalRule(1));

		JLabel2 label_minValue = new JLabel2("最小値(左端の値):");
		pane.add(label_minValue);

		field_sliderMin = new ValueInputPanel(this, new Let() {
			public void let(String s) {
				param.sliderMin = s;
			}
		}, "", param.sliderMin, new FloatChecker(), this.messageDisp_sliderMin);
		pane.add(this.field_sliderMin);
		pane.add(horizontalRule(1));

		///////////////////
		// スライダー最大値
		this.messageDisp_sliderMax = new JLabel2(" ");
		pane.add(messageDisp_sliderMax);
		pane.add(horizontalRule(1));

		JLabel2 label_maxValue = new JLabel2("最大値(右端の値):");
		pane.add(label_maxValue);

		field_sliderMax = new ValueInputPanel(this, new Let() {
			public void let(String s) {
				param.sliderMax = s;
			}
		}, "", param.sliderMax, new FloatChecker(), this.messageDisp_sliderMax);
		pane.add(this.field_sliderMax);
		pane.add(horizontalRule(5));

		///////////////////
		// 更新ボタン
		this.buttonOk = Util.generateSubmitButton2(parent, this);
		pane.add(this.buttonOk);
		///////////////////
		// キャンセルボタン
		pane.add(Util.generateCancelButton2(parent, this));
		///////////////////
		// アイテムを削除
		pane.add(spacer(10));
		pane.add(Util.generateDeleteButton2(parent, this));
	}

	void revert() {
		param.updateTo(backupParam);
	}


	static void putTypeSelectionParts(Container pane, ValueType[] valueTypeList, ValueType initialValueType,
			final ValueInputPanel inputPanel) {

		String[] valueParamList = new String[valueTypeList.length];
		for (int i = 0; i < valueTypeList.length; i++) {
			valueParamList[i] = Value.valueTypeToDescString(valueTypeList[i]);
		}
		JRadioButton valueTypeChangeRadioButtons[] = null;
		ButtonGroup group = new ButtonGroup();
		valueTypeChangeRadioButtons = new JRadioButton[valueParamList.length];
		for (int i = 0; i < valueParamList.length; i++) {
			String value = valueParamList[i];

			final JRadioButton radioButton = new JRadioButton(value);
			radioButton.setFont(Preference.LABEL_FONT);
			radioButton.setBackground(Preference.BG_COLOR);
			radioButton.setForeground(Preference.TEXT_COLOR);

			valueTypeChangeRadioButtons[i] = radioButton;
			if (valueTypeList[i].equals(initialValueType)) {
				radioButton.setSelected(true);
			}
			group.add(radioButton);
			pane.add(radioButton);

			// ラジオボタンの入れ替えでチェック処理の変更
			final ValueType valueType = valueTypeList[i];
			radioButton.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					if (radioButton.isSelected()) {
						if (ValueType.INTEGER.equals(valueType)) {
							inputPanel.setInputChecker(new IntegerChecker());
						} else if (ValueType.FLOAT.equals(valueType)) {
							inputPanel.setInputChecker(new FloatChecker());
						} else if (ValueType.NUMERIC.equals(valueType)) {
							inputPanel.setInputChecker(new NumericChecker());
						} else if (ValueType.BOOLEAN.equals(valueType)) {
							inputPanel.setInputChecker(new BooleanChecker());
						} else if (ValueType.STRING.equals(valueType)) {
							inputPanel.setInputChecker(new NonCheckChecker());
						} else {
							try {
								throw new Exception("Check");
							} catch (Exception e1) {
								System.err.println("値の異常のため点検必要");
								e1.printStackTrace();
							}
						}
						inputPanel.updateMessage();
					}
				}
			});
		}

	}

	static interface Let {
		public void let(String s);
	}

	static class ValueInputPanel extends JPanel {
		final InputParamDefWindow frame;
		final JTextField textField;
		final JLabel messageDisp;
		AbstractInputChecker checker;

		/**
		 *
		 * @param frame
		 * @param param_index
		 * @param comment
		 * @param ini
		 * @param checker
		 */
		public ValueInputPanel(InputParamDefWindow frame, Let let, String comment, String ini,
				AbstractInputChecker checker,
				final JLabel messageDisp) {
			Debug.println("Comment: " + comment);
			Debug.println("Ini: --" + ini + "--");

			this.frame = frame;
			this.checker = checker;
			this.messageDisp = messageDisp;

			this.textField = new JTextField2("" + ini);
			// TODO テキストフィールドのサイズ指定が、あまりうまく行っていない。
			// this.textField.setPreferredSize(new Dimension(200,20));
			// this.textField.setFont(Preference.CONSOLE_FONT);
			this.setLayout(new GridLayout(1, 2));
			this.add(new JLabel2(comment));
			this.add(this.textField);
			if (this.messageDisp != null) {
				this.messageDisp.setText(checker.message);
			}
			this.setBackground(Preference.BG_COLOR);

			// リスナーを設定
			final ValueInputPanel valueInputPanel = this;
			this.textField.getDocument().addDocumentListener(new DocumentListener() {
				void update() {
					String text = textField.getText();
					let.let(text);
					valueInputPanel.updateMessage();
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					// 別関数に移譲
					update();
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					// 別関数に移譲
					update();
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					// 別関数に移譲
					update();
				}
			});
		}

		void updateMessage() {
			if (messageDisp == null) {
				return;
			}

			final Color COLOR_ERROR = Color.RED;
			final Color COLOR_NORMAL = Color.BLACK;

			String text = textField.getText();
			checker.check(text);
			if (checker.isOk() || ! textField.isEnabled()) {
				if (frame.ngInputPanels.contains(this)) {
					frame.ngInputPanels.remove(this);
				}
				this.messageDisp.setForeground(COLOR_NORMAL);
				this.messageDisp.setText(checker.message);
			} else {
				frame.ngInputPanels.add(this);
				this.messageDisp.setForeground(COLOR_ERROR);
				this.messageDisp.setText(checker.message);
			}

			if (frame.ngInputPanels.isEmpty()) {
				frame.buttonOk.setEnabled(true);
			} else {
				frame.buttonOk.setEnabled(false);
			}
		}

		public void setInputChecker(AbstractInputChecker checker) {
			this.checker = checker;
			this.updateMessage();
		}
	}
}
