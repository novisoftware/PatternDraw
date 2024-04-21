package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

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

import com.github.novisoftware.patternDraw.geometricLanguage.parameter.Parameter;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefineToEdit;
import com.github.novisoftware.patternDraw.gui.editor.core.RpnUtil;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.AbstractInputChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.FloatChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.IntegerChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.NonCheckChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.NumericChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.VariableNameChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditParameterDefinitionListWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement.KindId;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.util.Debug;
import com.github.novisoftware.patternDraw.gui.misc.JFrame2;
import com.github.novisoftware.patternDraw.gui.misc.JLabel2;
import com.github.novisoftware.patternDraw.gui.misc.JTextField2;
import com.github.novisoftware.patternDraw.utils.GuiUtil;
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
	JLabel messageDisp;
	private final JButton buttonOk;
	final ArrayList<String> variableNameList;
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

	public InputParamDefWindow(// final RpnGraphNodeElement element,
			final EditParameterDefinitionListWindow parent, final ParameterDefineToEdit param,
			final ArrayList<String> variableNameList) {
		this.setTitle("パラメーターを設定します。");
		this.setSize(600, 600);

		// 変数名一覧の設定
		this.variableNameList = variableNameList;
		this.param = param;
		this.backupParam = param.clone();

		////////////////////////////////////////////////////////////////////
		// レイアウト

		// レイアウト
		Container pane = this.getContentPane();
		this.setLayout(new FlowLayout(FlowLayout.LEADING));

		JLabel2 title = new JLabel2("パラメーターを編集します");
		pane.add(title);

		// 不可視の水平線を作成する (レイアウトの調整)
		addHorizontalRule__test(pane, 5);

		// 値チェック
		messageDisp = new JLabel2(" ");
		pane.add(messageDisp);

		// 不可視の水平線を作成する (レイアウトの調整)
		addHorizontalRule__test(pane, 5);

		/////////////////////////
		// 変数名
		field_name = new ValueInputPanel(this, new Let() {
			public void let(String s) {
				param.name = s;
			}
		}, "変数名:", param.name, new NonCheckChecker());
		pane.add(field_name);

		pane.add(horizontalRule(5));

		/////////////////////////
		// 説明
		field_desc = new ValueInputPanel(this, new Let() {
			public void let(String s) {
				param.description = s;
			}
		}, "説明:", param.description, new NonCheckChecker());
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
		putTypeSelectionParts(pane, this.enableValueTypeList, param.valueType);

		// 不可視の水平線を作成する (レイアウトの調整)
		addHorizontalRule__test(pane, 5);

		/////////////////////////
		// デフォルト値
		field_defalutValue = new ValueInputPanel(this, new Let() {
			public void let(String s) {
				param.defaultValue = s;
			}
		}, "デフォルト値", param.defaultValue, new IntegerChecker());
		pane.add(field_defalutValue);
		pane.add(horizontalRule(5));

		/////////////////////////
		// 列挙値
		check_enumEnable = new JCheckBox();
		pane.add(check_enumEnable);
		JLabel2 label_enumEnable = new JLabel2("ラジオボタンによる設定を有効にする");
		pane.add(label_enumEnable);

		pane.add(horizontalRule(1));

		field_enumValue = new ValueInputPanel(this, new Let() {
			public void let(String s) {
				param.enumValueList = s;
			}
		}, "列挙値:", param.enumValueList, new NonCheckChecker());
		pane.add(field_enumValue);

		pane.add(horizontalRule(5));

		///////////////////
		// スライダー
		check_sliderEnable = new JCheckBox();
		pane.add(check_sliderEnable);
		JLabel2 label_sliderEnable = new JLabel2("スライダーによる設定を有効にする");
		pane.add(label_sliderEnable);

		pane.add(horizontalRule(1));

		///////////////////
		// スライダー最小値
		JLabel2 label_minValue = new JLabel2("最小値:");
		pane.add(label_minValue);

		field_sliderMin = new ValueInputPanel(this, new Let() {
			public void let(String s) {
				param.sliderMin = s;
			}
		}, "", param.sliderMin, new FloatChecker());
		pane.add(this.field_sliderMin);
		pane.add(horizontalRule(1));

		///////////////////
		// スライダー最大値
		JLabel2 label_maxValue = new JLabel2("最大値:");
		pane.add(label_maxValue);

		field_sliderMax = new ValueInputPanel(this, new Let() {
			public void let(String s) {
				param.sliderMax = s;
			}
		}, "", param.sliderMax, new FloatChecker());
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


	static void putTypeSelectionParts(Container pane, ValueType[] valueTypeList, ValueType initialValueType) {

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
		}

	}

	static interface Let {
		public void let(String s);
	}

	static class ValueInputPanel extends JPanel {
		final InputParamDefWindow frame;
		final JTextField textField;
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
				AbstractInputChecker checker) {
			Debug.println("Comment: " + comment);
			Debug.println("Ini: --" + ini + "--");

			this.frame = frame;
			this.checker = checker;

			this.textField = new JTextField2("" + ini);
			// TODO テキストフィールドのサイズ指定が、あまりうまく行っていない。
			// this.textField.setPreferredSize(new Dimension(200,20));
			// this.textField.setFont(Preference.CONSOLE_FONT);
			this.setLayout(new GridLayout(1, 2));
			this.add(new JLabel2(comment));
			this.add(this.textField);
			this.frame.messageDisp.setText(checker.message);
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
			final Color COLOR_ERROR = Color.RED;
			final Color COLOR_NORMAL = Color.BLACK;

			String text = textField.getText();
			checker.check(text);
			if (checker.isOk()) {
				frame.buttonOk.setEnabled(true);
				frame.messageDisp.setForeground(COLOR_NORMAL);
				frame.messageDisp.setText(checker.message);
			} else {
				frame.buttonOk.setEnabled(false);
				frame.messageDisp.setForeground(COLOR_ERROR);
				frame.messageDisp.setText(checker.message);
			}
		}

		public void setInputChecker(AbstractInputChecker checker) {
			this.checker = checker;
			this.updateMessage();
		}

	}
}
