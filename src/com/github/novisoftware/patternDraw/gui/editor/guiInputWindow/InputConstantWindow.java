package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement.KindId;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.util.Common;
import com.github.novisoftware.patternDraw.gui.editor.util.Debug;
import com.github.novisoftware.patternDraw.gui.misc.JFrame2;
import com.github.novisoftware.patternDraw.gui.misc.JLabel2;
import com.github.novisoftware.patternDraw.gui.misc.Preference;

/**
 *
 * 定数値の編集を行う
 *
 */
public class InputConstantWindow extends JFrame2 {
	ArrayList<String> rpnArray;
	final AbstractElement targetElement;
	JLabel messageDisp;
	private final JButton buttonOk;

	public InputConstantWindow(final RpnGraphNodeElement element, final EditDiagramPanel editPanel) {
		if (element.getKindId() != KindId.CONSTANT) {
			System.err.println("呼び出し条件がおかしいので要確認。");
			try {
				throw new Exception("check");
			} catch (Exception e) {
				// 場所を強調するため
				e.printStackTrace();
			}
		}
		Common.setIconImage(this);
		String message = element.getKindString() + " を設定します。";
		this.setTitle(Common.FRAME_TITLE_BASE + message);
		this.setSize(500, 250);

		// ターゲットにする要素の設定
		this.targetElement = element;

		////////////////////////////////////////////////////////////////////
		// レイアウト

		// レイアウト
		Container pane = this.getContentPane();
		this.setLayout(new FlowLayout(FlowLayout.LEADING));

		messageDisp = new JLabel2(" ");
		pane.add(messageDisp);

		// 不可視の水平線を作成する (レイアウトの調整)
		addHorizontalRule__test(pane, 5);

		Debug.println("ElementEdit", "RPN to Edit is " + element.getRpnString());
		rpnArray = element.getRpn().getArrayCopy();

		/////////////////////////
		// 型を切り替えるためのラジオボタン

		// TODO: 「やめる」としたときに、型の切り換えも元に戻す。
		final Value.ValueType[] valueTypes = {
				Value.ValueType.INTEGER,
				Value.ValueType.FLOAT,
				Value.ValueType.NUMERIC
				};
		String[] valueParamList = new String[valueTypes.length];
		for (int i = 0; i < valueTypes.length; i++) {
			valueParamList[i] = Value.valueTypeToDescString(valueTypes[i]);
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
			if (valueTypes[i].equals(element.getValueType())) {
				radioButton.setSelected(true);
			}
			group.add(radioButton);
			pane.add(radioButton);
		}

		// 不可視の水平線を作成する (レイアウトの調整)
		addHorizontalRule__test(pane, 5);

		// {
		for (int index = 0; index < rpnArray.size(); index++) {
			String s0 = rpnArray.get(index);
			// 「;」の右側がコメント
			if (RpnUtil.hasComment(s0)) {
				String comment = "";
				comment = RpnUtil.getComment(s0);
				String value = RpnUtil.getRepresent(s0);

				Debug.println("element.getKindId() = " + element.getKindId());

				if (comment.length() > 0) {
					AbstractInputChecker inputChecker = null;
					if (element.getKindId() == KindId.CONSTANT) {
						if (Value.ValueType.INTEGER.equals(element.getValueType())) {
							inputChecker = new IntegerChecker();
						} else if (Value.ValueType.FLOAT.equals(element.getValueType())) {
							inputChecker = new FloatChecker();
						} else if (Value.ValueType.NUMERIC.equals(element.getValueType())) {
							inputChecker = new NumericChecker();
						} else {
							inputChecker = new NonCheckChecker();
						}
					} else if (element.getKindId() == KindId.CONTROL) {
						inputChecker = new IntegerChecker();
					} else if (element.getKindId() == KindId.VARIABLE_SET) {
						Debug.println("2   element.getKindId() = " + element.getKindId());
						value = value.replaceAll("'", "");
						inputChecker = new VariableNameChecker(value, editPanel.networkDataModel.nameOfvaliables);
					}
					messageDisp.setText(inputChecker.message);

					ValueInputPanel p = new ValueInputPanel(this, index, comment, value, inputChecker);

					// チェック処理を切り替える
					if (valueTypeChangeRadioButtons != null) {
						// (注: element.getKindId() == KindId.CONSTANT の場合 )
						for (int i = 0; i < valueTypeChangeRadioButtons.length; i++) {
							final JRadioButton radioButton = valueTypeChangeRadioButtons[i];

							// 整数とか浮動小数点とかの切り換え
							final int idx = i;
							radioButton.addChangeListener(new ChangeListener() {
								public void stateChanged(ChangeEvent e) {
									if (radioButton.isSelected()) {
										((RpnGraphNodeElement) element).setValueType(valueTypes[idx]);
										if (ValueType.INTEGER.equals(valueTypes[idx])) {
											p.setInputChecker(new IntegerChecker());
										} else {
											p.setInputChecker(new NumericChecker());
										}
									}
								}
							});
						}
					}

					pane.add(p);
				}
			}
		}


		// 不可視の水平線を作成する (レイアウトの調整)
		addHorizontalRule__test(pane, 5);

		this.buttonOk = Util.generateSubmitButton(editPanel, this);
		pane.add(this.buttonOk);
		pane.add(Util.generateCancelButton(editPanel, this));
	}

	static class ValueInputPanel extends JPanel {
		final InputConstantWindow frame;
		final JTextField textField;
		AbstractInputChecker checker;

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

		public ValueInputPanel(InputConstantWindow frame, int param_index, String comment, String ini,
				AbstractInputChecker checker) {
			Debug.println("Comment: " + comment);
			Debug.println("Ini: --" + ini + "--");

			this.frame = frame;
			this.checker = checker;

			this.textField = new JTextField("" + ini);
			// TODO テキストフィールドのサイズ指定が、あまりうまく行っていない。
			this.textField.setPreferredSize(new Dimension(200,20));
			this.textField.setFont(Preference.CONSOLE_FONT);
			this.setLayout(new GridLayout(1, 2));
			this.add(new JLabel2(comment));
			this.add(this.textField);
			this.frame.messageDisp.setText(checker.message);
			this.setBackground(Preference.BG_COLOR);

			// リスナーを設定
			final ValueInputPanel thisObj = this;
			this.textField.getDocument().addDocumentListener(new DocumentListener() {
				void update() {
					// 再度 RPN オブジェクトを作成する
					String text = textField.getText();
					frame.rpnArray.set(param_index, text + (comment.length() > 0 ? ";" + comment : ""));
					System.out.println("Set element from textField:" + text);

					thisObj.updateMessage();
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					update();
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					update();
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					update();
				}
			});
		}
	}
}
