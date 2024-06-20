package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefine;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.AbstractInputChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.BooleanChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.FloatChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.IntegerChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.NonCheckChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.NumericChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.VariableNameChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.inputConstant.Util;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditParamDefListWindow;
import com.github.novisoftware.patternDraw.gui.misc.JCheckBox2;
import com.github.novisoftware.patternDraw.gui.misc.JFrame2;
import com.github.novisoftware.patternDraw.gui.misc.JLabel2;
import com.github.novisoftware.patternDraw.gui.misc.JLabel3_messageDisp;
import com.github.novisoftware.patternDraw.gui.misc.JTextField2;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.GuiPreference;

/**
 *
 * パラメーター定義の編集を行う
 *
 */
public class EditParamDefWindow extends JFrame2 {
	public static final int WINDOW_POS_X = 200;
	public static final int WINDOW_POS_Y = 100;
	public static final int WINDOW_WIDTH = 640;
	public static final int WINDOW_HEIGHT = 800;

	private final ParameterDefine param;
	private final boolean isNew;
	final ParameterDefine backupParam;
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
	final private ArrayList<ParameterDefine> params;

	public EditParamDefWindow(// final RpnGraphNodeElement element,
			final EditParamDefListWindow parent, final ParameterDefine param,
			ArrayList<ParameterDefine> params, final HashSet<String> variableNameSet, boolean isNew) {
		super();
		this.setTitle("パラメーターを設定します。");
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setLocation(WINDOW_POS_X, WINDOW_POS_Y);

		this.params = params;
		this.isNew = isNew;
		
		// 変数名一覧の設定
		this.variableNameSet = variableNameSet;
		this.param = param;
		this.backupParam = param.clone();

		// 入力項目でNGになっているものがあるかどうかで、 OK ボタンを制御する
		this.ngInputPanels = new HashSet<ValueInputPanel>();

		// 変数名のチェック結果
		this.messageDisp_varName = new JLabel3_messageDisp(" ");

		// デフォルト値のチェック結果
		this.messageDisp = new JLabel3_messageDisp(" ");

		//
		field_defalutValue = new ValueInputPanel(this, new Let() {
			public void let(String s) {
				param.defaultValue = s;
			}
		}, "デフォルト値", param.defaultValue, new IntegerChecker(), messageDisp);

		final ArrayList<JComponent> enumBuilderPartsSet = new ArrayList<JComponent>();
		final ArrayList<JComponent> sliderBuilderPartsSet = new ArrayList<JComponent>();

		final EditParamDefWindow thisObj = this;
		
		this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
            	thisObj.revert();
            	parent.subWindowDisposeNotify();
            }
        });

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

		VariableNameChecker variableNameChecker = new VariableNameChecker(this.variableNameSet);
		
		field_name = new ValueInputPanel(this, new Let() {
			public void let(String s) {
				param.name = s;
			}
		}, "変数名:", param.name, variableNameChecker, this.messageDisp_varName);
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
		ValueTypeSetter initialSetter =
			putTypeSelectionParts(pane,
				this.enableValueTypeList,
				param.valueType,
				new ValueTypeSetter() {
					@Override
					public void set(ValueType newValueType) {
						param.valueType = newValueType;
					}
				},
				field_defalutValue,
				enumBuilderPartsSet,
				sliderBuilderPartsSet
				);


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
		check_enumEnable = new JCheckBox2("ラジオボタンによる設定を有効にする");
		check_enumEnable.setSelected(param.enableEnum);
		check_enumEnable.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				param.enableEnum = check_enumEnable.isSelected();
			}
		});

		enumBuilderPartsSet.add(check_enumEnable);
		enumBuilderPartsSet.add(horizontalRule(1));

		field_enumValue = new ValueInputPanel(this, new Let() {
			public void let(String s) {
				param.enumValueList = s;
			}
		}, "列挙値:", param.enumValueList, new NonCheckChecker(), null);
		enumBuilderPartsSet.add(field_enumValue);

		for (JComponent c : enumBuilderPartsSet) {
			pane.add(c);
		}
		pane.add(horizontalRule(5));

		///////////////////
		// スライダー
		check_sliderEnable = new JCheckBox2("スライダーによる設定を有効にする");
		check_sliderEnable.setSelected(param.enableSlider);
		check_sliderEnable.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				param.enableSlider = check_sliderEnable.isSelected();
			}
		});

		sliderBuilderPartsSet.add(check_sliderEnable);
		sliderBuilderPartsSet.add(horizontalRule(1));

		///////////////////
		// スライダー最小値
		// デフォルト値のチェック結果
		this.messageDisp_sliderMin = new JLabel3_messageDisp(" ");
		sliderBuilderPartsSet.add(messageDisp_sliderMin);
		sliderBuilderPartsSet.add(horizontalRule(1));

		JLabel2 label_minValue = new JLabel2("最小値(左端の値):");
		sliderBuilderPartsSet.add(label_minValue);

		field_sliderMin = new ValueInputPanel(this, new Let() {
			public void let(String s) {
				param.sliderMin = s;
			}
		}, "", param.sliderMin, new FloatChecker(), this.messageDisp_sliderMin);
		sliderBuilderPartsSet.add(this.field_sliderMin);
		sliderBuilderPartsSet.add(horizontalRule(1));

		///////////////////
		// スライダー最大値
		this.messageDisp_sliderMax = new JLabel3_messageDisp(" ");
		sliderBuilderPartsSet.add(messageDisp_sliderMax);
		sliderBuilderPartsSet.add(horizontalRule(1));

		JLabel2 label_maxValue = new JLabel2("最大値(右端の値):");
		sliderBuilderPartsSet.add(label_maxValue);

		field_sliderMax = new ValueInputPanel(this, new Let() {
			public void let(String s) {
				param.sliderMax = s;
			}
		}, "", param.sliderMax, new FloatChecker(), this.messageDisp_sliderMax);
		sliderBuilderPartsSet.add(this.field_sliderMax);
		sliderBuilderPartsSet.add(horizontalRule(5));

		for (JComponent c : sliderBuilderPartsSet) {
			pane.add(c);
		}

		///////////////////
		// OKボタン
		this.buttonOk = Util.generateSubmitButton2(parent, this);
		pane.add(this.buttonOk);
		///////////////////
		// キャンセルボタン
		pane.add(Util.generateCancelButton2(parent, this));

		///////////////////
		// アイテムを削除
		if (! isNew) {
			pane.add(spacer(10));
			pane.add(Util.generateDeleteButton2(parent, this));
		}

		
		if ("".equals(param.name)) {
			// 変数名が空文字列の場合、決定できないようにエラー状態にしておく
			field_name.updateMessage();
		}

		initialSetter.set(param.valueType);
	}

	public void revert() {
		if (isNew) {
			this.params.remove(this.param);
		}
		else {
			getParam().updateTo(backupParam);
		}
	}

	static interface ValueTypeSetter {
		void set(ValueType newValueType);
	}



	/**
	 * 型の選択を変更したときの動作
	 * （ラジオボタン配置）
	 *
	 * @param pane 配置先のJFrame等
	 * @param valueTypeList 選択可能な一覧
	 * @param initialValueType 初期値
	 * @param inputPanel
	 * @param sliderBuilderPartsSet
	 * @param enumBuilderPartsSet
	 */
	static ValueTypeSetter putTypeSelectionParts(Container pane,
			ValueType[] valueTypeList,
			ValueType initialValueType,
			ValueTypeSetter setter,
			final ValueInputPanel inputPanel,
			final ArrayList<JComponent> enumBuilderPartsSet,
			final ArrayList<JComponent> sliderBuilderPartsSet) {

		String[] valueParamList = new String[valueTypeList.length];
		for (int i = 0; i < valueTypeList.length; i++) {
			valueParamList[i] = Value.valueTypeToDescString(valueTypeList[i]);
		}

		final ValueType[] valueTypeContainer = new ValueType[1];
		valueTypeContainer[0] = null;
		JRadioButton valueTypeChangeRadioButtons[] = null;
		ButtonGroup group = new ButtonGroup();
		valueTypeChangeRadioButtons = new JRadioButton[valueParamList.length];
		for (int i = 0; i < valueParamList.length; i++) {
			String value = valueParamList[i];

			final JRadioButton radioButton = new JRadioButton(value);
			radioButton.setFont(GuiPreference.LABEL_FONT);
			radioButton.setBackground(GuiPreference.BG_COLOR);
			radioButton.setForeground(GuiPreference.TEXT_COLOR);

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
						ValueType valueType_prev = valueTypeContainer[0];
						valueTypeContainer[0] = onRadioSelected(
											valueType_prev,
											valueType,
											setter,
											inputPanel,
											enumBuilderPartsSet,
											sliderBuilderPartsSet,
											false);
					}
				}
			});

		}

		// 注：
		// このタイミングで onRadioSelected を呼んでも
		// enumBuilderPartsSet   sliderBuilderPartsSetは nullであり、
		// 効果がないので、
		// 初回実行用のValueTypeSetterを作成して、呼び出し元から渡す。
		return new ValueTypeSetter() {
			@Override
			public void set(ValueType newValueType) {
				valueTypeContainer[0] = onRadioSelected(
						null,
						newValueType,
						setter,
						inputPanel,
						enumBuilderPartsSet,
						sliderBuilderPartsSet,
						false);
			}
		};
	}

	/**
	 * ラジオボタン変更時、または、初期化のときの設定処理
	 *
	 * @param valueType_prev 初期化のときは敢えてnullを渡す。
	 * @param valueType
	 * @param setter
	 * @param inputPanel
	 * @param enumBuilderPartsSet
	 * @param sliderBuilderPartsSet
	 * @param isInit
	 * @return
	 */
	static public ValueType onRadioSelected(
			ValueType valueType_prev,
			ValueType valueType,
			ValueTypeSetter setter,
			final ValueInputPanel inputPanel,
			final ArrayList<JComponent> enumBuilderPartsSet,
			final ArrayList<JComponent> sliderBuilderPartsSet,
			boolean isInit
			) {
		if (valueType == null) {
			return null;
		}
		if (valueType.equals(valueType_prev)) {
			return valueType;
		}

		setter.set(valueType);
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

		if (enumBuilderPartsSet != null) {
			// 注: 今後、対応する型の種類を増やすかもしれないけれど、現時点では以下の2分別。
			if (ValueType.INTEGER.equals(valueType)) {
				setEnablePartsList(true, enumBuilderPartsSet);
			} else {
				setEnablePartsList(false, enumBuilderPartsSet);
			}
		}

		if (sliderBuilderPartsSet != null) {
			// 注: 今後、対応する型の種類を増やすかもしれないけれど、現時点では以下の2分別。
			if (ValueType.FLOAT.equals(valueType)) {
				setEnablePartsList(true, sliderBuilderPartsSet);
			} else {
				setEnablePartsList(false, sliderBuilderPartsSet);
			}
		}

		return valueType;
	}


	static void setEnablePartsList(boolean enabled, ArrayList<JComponent> partsList) {
		for (JComponent c : partsList) {
			if (c instanceof JLabel) {
				c.setForeground(enabled ? Color.BLACK : Color.GRAY);
			} else if (c instanceof JCheckBox) {
				c.setEnabled(enabled);
			} else if (c instanceof JTextField) {
				c.setEnabled(enabled);
			} else if (c instanceof ValueInputPanel) {
				((ValueInputPanel)c).setEnabled(enabled);
			}
		}
	}


	/**
	 * @return param
	 */
	public ParameterDefine getParam() {
		return param;
	}

	static interface Let {
		public void let(String s);
	}

	static class ValueInputPanel extends JPanel {
		final EditParamDefWindow frame;
		final JLabel description;
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
		public ValueInputPanel(EditParamDefWindow frame, Let let, String description, String ini,
				AbstractInputChecker checker,
				final JLabel messageDisp) {
			Debug.println("Comment: " + description);
			Debug.println("Ini: --" + ini + "--");

			this.frame = frame;
			this.checker = checker;
			this.messageDisp = messageDisp;

			this.textField = new JTextField2("" + ini);
			// TODO テキストフィールドのサイズ指定が、あまりうまく行っていない。
			// this.textField.setPreferredSize(new Dimension(200,20));
			// this.textField.setFont(Preference.CONSOLE_FONT);
			this.setLayout(new GridLayout(1, 2));
			this.description = new JLabel2(description);
			this.add(this.description);
			this.add(this.textField);
			if (this.messageDisp != null) {
				this.messageDisp.setText(checker.message);
			}
			this.setBackground(GuiPreference.BG_COLOR);

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
		}

		void updateMessage() {
			if (messageDisp == null) {
				return;
			}

			// TODO: preferenceに。
			final Color COLOR_ERROR = GuiPreference.MESSAGE_ERROR_COLOR;
			final Color COLOR_NORMAL = GuiPreference.TEXT_COLOR;

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

		/*
		final JTextField textField;
		final JLabel messageDisp;
		*/

		public void setEnabled(boolean enabled) {
			textField.setEnabled(enabled);
			description.setEnabled(enabled);
		}
	}
}
