package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.github.novisoftware.patternDraw.utils.GuiUtil;
import com.github.novisoftware.patternDraw.utils.Preference;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.ControlElement;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.EnumParameter;
import com.github.novisoftware.patternDraw.gui.editor.core.RpnUtil;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiMenu.ElementFactory;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.GraphConnector;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.IconGuiInterface;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement.KindId;
import com.github.novisoftware.patternDraw.gui.editor.util.Debug;
import com.github.novisoftware.patternDraw.gui.misc.JFrame2;

/**
 *
 * RPN式の編集を行う
 *
 */
public class InputOtherTypeWindow extends JFrame2 {
	/**
	 * 呼び出し元画面
	 */
	final int LINES = 14;

	ArrayList<String> rpnArray;
	final AbstractElement targetElement;

	abstract class InputChecker {
		boolean isOk = false;
		// String message = " ";
		String message = "整数を入力してください";

		abstract void check(String s);
	}

	class IntegerChecker extends InputChecker {
		@Override
		void check(String s) {
			if (s.matches("[\\+-]?[0-9]+")) {
				isOk = true;
				message = " ";
			} else if (s.length() == 0) {
				isOk = false;
				message = "整数を入力してください";
			} else {
				isOk = false;
				message = "整数を正しい形式で入力してください";
			}
		}
	}

	class NumericChecker extends InputChecker {
		NumericChecker() {
			this.message = "数(小数可)を入力してください";
		}

		@Override
		void check(String s) {
			if (s.matches("[\\+-]?[0-9]+([.][0-9]+)?")) {
				isOk = true;
				message = " ";
			} else if (s.length() == 0) {
				isOk = false;
				message = "数(小数可)を入力してください";
			} else {
				isOk = false;
				message = "数(小数可)を正しい形式で入力してください";
			}
		}
	}

	class NonCheckChecker extends InputChecker {
		NonCheckChecker() {
			this.message = "文字列を入力してください。";
			isOk = true;
		}

		@Override
		void check(String s) {
		}
	}

	class VariableNameChecker extends InputChecker {
		private String oldName;
		private List<String> variables;

		VariableNameChecker(String oldName, List<String> variables) {
			this.oldName = oldName;
			this.variables = variables;
			this.message = "変更する変数名を入力してください (元の変数名: " + this.oldName + ")";
		}

		@Override
		void check(String s) {
			if (s.equals(oldName)) {
				isOk = true;
				message = " ";
			} else if (variables.contains(s)) {
				isOk = false;
				message = "すでに使われている変数名です";
			} else if (s.matches("[A-Za-z][A-Za-z0-9_]*")) {
				isOk = true;
				message = " ";
			} else if (s.length() == 0) {
				isOk = false;
				message = "変数名を入力してください";
			} else {
				isOk = false;
				message = "変数名に使用できない文字が含まれています";
			}
		}
	}

	JLabel messageDisp;

	public InputOtherTypeWindow(final AbstractElement element, final EditDiagramPanel editPanel) {
		this.targetElement = element;
		this.setTitle(element.getKindString() + " を編集");

		////////////////////////////////////////////////////////////////////
		// レイアウト

		// レイアウト定義用オブジェクトを作成する
		GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		// gbc.gridheight = 100;
		// gbc.gridwidth = 500;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		gbc.weightx = 1;
		// Inset( Botton, Left, Right , Top )
		gbc.insets = new Insets(15, 15, 15, 15);

		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 0;
		gbc2.gridy = 0;
		gbc2.weighty = 1;
		gbc2.weightx = 1;
		gbc2.insets = new Insets(15, 15, 15, 15);

		messageDisp = new JLabel(" ");
		layout.setConstraints(messageDisp, gbc);
		this.add(messageDisp);

		if (element instanceof ControlElement) {
			ControlElement e = (ControlElement) element;
			Debug.println("ElementEdit", "RPN to Edit is " + e.getRpnString());
			rpnArray = e.getRpn().getArray();
		} else if (element instanceof RpnGraphNodeElement) {
			RpnGraphNodeElement e = (RpnGraphNodeElement) element;
			Debug.println("ElementEdit", "RPN to Edit is " + e.getRpnString());
			rpnArray = e.getRpn().getArray();
		}

		int n = 1;

		final Value.ValueType[] valueTypes = { Value.ValueType.INTEGER, Value.ValueType.FLOAT,
				Value.ValueType.NUMERIC };
		// String[] valueParamList = {"整数", "浮動小数点", "任意精度"};
		String[] valueParamList = new String[valueTypes.length];
		for (int i = 0; i < valueTypes.length; i++) {
			valueParamList[i] = Value.valueTypeToDescString(valueTypes[i]);
		}
		JRadioButton valueTypeChangeRadioButtons[] = null;
		if (element.getKindId() == KindId.CONSTANT) {
			ButtonGroup group = new ButtonGroup();
			valueTypeChangeRadioButtons = new JRadioButton[valueParamList.length];
			for (int i = 0; i < valueParamList.length; i++) {
				String value = valueParamList[i];

				final JRadioButton radioButton = new JRadioButton(value);
				valueTypeChangeRadioButtons[i] = radioButton;
				if (valueTypes[i].equals(element.getValueType())) {
					radioButton.setSelected(true);
				}
				group.add(radioButton);
				this.add(radioButton);

				gbc.gridx = 0;
				gbc.gridy = n; // 桁位置
				gbc.weighty = 0;
				layout.setConstraints(radioButton, gbc);
				n++;
			}
		}

		if (element.getKindId() == KindId.CONSTANT || element.getKindId() == KindId.CONTROL
				|| element.getKindId() == KindId.VARIABLE_SET) { // element.getKindString().equals("定数"))
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
						InputChecker inputChecker = null;
						if (element.getKindId() == KindId.CONSTANT) {
							if (Value.ValueType.INTEGER.equals(element.getValueType())) {
								inputChecker = new IntegerChecker();
							} else if (Value.ValueType.FLOAT.equals(element.getValueType())) {
								inputChecker = new NumericChecker();
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
							inputChecker = new VariableNameChecker(value, editPanel.networkDataModel.variableNameList);
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
											((RpnGraphNodeElement)element).setValueType(valueTypes[idx]);
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

						gbc.gridx = 0;
						gbc.gridy = n; // 桁位置
						gbc.weighty = 1;
						layout.setConstraints(p, gbc);
						this.add(p);
						n++;
					}
				}
			}
		}
		if (element.getKindId() == KindId.VARIABLE_REFER) { // 変数を参照
			for (int index = 0; index < rpnArray.size(); index++) {
				String s0 = rpnArray.get(index);
				// 「;」の右側がコメント
				if (RpnUtil.hasComment(s0)) {
					String comment = "";
					comment = RpnUtil.getComment(s0);
					String value = RpnUtil.getRepresent(s0);

					Debug.println("element.getKindId() = " + element.getKindId());

					if (comment.length() > 0) {
						messageDisp.setText("");

						// public ValueSelectPanel(ElementEditFrame frame,
						// String comment, String ini, List<String> selectList)
						// {

						ValueSelectPanel p = new ValueSelectPanel(this, comment, value,
								editPanel.networkDataModel.variableNameList);

						// (this, index, comment, value, inputChecker);

						gbc.gridx = 0;
						gbc.gridy = n; // 桁位置
						gbc.weighty = 1;
						layout.setConstraints(p, gbc);
						this.add(p);
						n++;
					}
				}
			}
		}

		// ValueSelectPanel

		/*
		 * if (element.getKindId() == KindId.VARIABLE_SET) { //
		 * element.getKindString().equals("変数を設定")) {
		 * Debug.println("ElementEditFrame", "set valiable name"); for(int index
		 * = 0 ; index < rpnArray.size() ; index++) { String s0 =
		 * rpnArray.get(index); // 「;」の右側がコメント String comment = ""; if
		 * (RpnUtil.hasComment(s0)) { comment = RpnUtil.getComment(s0); } String
		 * value = s0.replaceAll(";.*", "").replaceAll("^'", "");
		 *
		 * if (comment.length() > 0) { InputPanel_Integer p = new
		 * InputPanel_Integer(this, index, comment, value, new
		 * InputChecker_Integer());
		 *
		 * gbc2.gridx = 0; gbc2.gridy = n; // 桁位置 gbc2.weighty = 1;
		 * layout.setConstraints(p, gbc2); this.add(p);
		 *
		 * n++; } } }
		 */

		SubmitButtonPanel submit = new SubmitButtonPanel(editPanel, this);

		gbc2.fill = GridBagConstraints.BOTH;
		gbc2.gridy = ++n;
		layout.setConstraints(submit, gbc2);
		this.add(submit);

		this.setSize(500, 200);
	}

	JButton buttonOk;
	JButton buttonCancel;

	/**
	 * OK や Cancel のボタンを配置したパネル
	 *
	 */
	static class SubmitButtonPanel extends JPanel {
		SubmitButtonPanel(final EditDiagramPanel editPanel, final InputOtherTypeWindow tf) {
			JButton buttonOk = new JButton("これに決める");
			buttonOk.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ev) {
					System.out.println("Set RPN:" + RpnUtil.a2s(tf.rpnArray));
					AbstractElement te = tf.targetElement;
					if (te instanceof ControlElement) {
						ControlElement e = (ControlElement) te;
						e.setRpnString(RpnUtil.a2s(tf.rpnArray));
					} else if (te instanceof RpnGraphNodeElement) {
						RpnGraphNodeElement e = (RpnGraphNodeElement) te;
						e.setRpnString(RpnUtil.a2s(tf.rpnArray));
					}
					tf.dispose();
					editPanel.repaint();
				}
			});

			JButton buttonCancel = new JButton("やめる");
			buttonCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					tf.dispose();
				}
			});

			buttonOk.setFont(Preference.OK_BUTTON_FONT);
			buttonCancel.setFont(Preference.CANCEL_BUTTON_FONT);

			this.setLayout(new GridLayout(1, 3));
			this.add(buttonOk);
			this.add(buttonCancel);

			tf.buttonOk = buttonOk;
			tf.buttonCancel = buttonCancel;
		}
	}

	static class ValueInputPanel extends JPanel {
		final InputOtherTypeWindow frame;
		final JTextField textField;
		InputChecker checker;

		void updateMessage() {
			final Color COLOR_ERROR = Color.RED;
			final Color COLOR_NORMAL = Color.BLACK;

			String text = textField.getText();
			checker.check(text);
			if (checker.isOk) {
				frame.buttonOk.setEnabled(true);
				frame.messageDisp.setForeground(COLOR_NORMAL);
				frame.messageDisp.setText(checker.message);
			} else {
				frame.buttonOk.setEnabled(false);
				frame.messageDisp.setForeground(COLOR_ERROR);
				frame.messageDisp.setText(checker.message);
			}
		}

		public void setInputChecker(InputChecker checker) {
			this.checker = checker;
			this.updateMessage();
		}

		public ValueInputPanel(InputOtherTypeWindow frame, int param_index, String comment, String ini,
				InputChecker checker) {
			Debug.println("Comment: " + comment);
			Debug.println("Ini: --" + ini + "--");

			this.frame = frame;
			// TODO テキストフィールドのサイズ指定が、うまく行っていない。
			// this.textField.setPreferredSize(new Dimension(200,20));
			this.checker = checker;

			this.textField = new JTextField("" + ini);
			this.setLayout(new GridLayout(1, 2));
			this.add(new JLabel(comment));
			this.add(this.textField);
			this.frame.messageDisp.setText(checker.message);

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

	static class ValueSelectPanel extends JPanel {
		final InputOtherTypeWindow frame;
		final JComboBox<String> selecter;
		final InputChecker checker;

		public ValueSelectPanel(InputOtherTypeWindow frame, String comment, String ini, List<String> selectList) {
			this.frame = frame;
			// TODO テキストフィールドのサイズ指定が、うまく行っていない。
			// this.textField.setPreferredSize(new Dimension(200,20));
			this.checker = null;

			String[] str = new String[1];
			this.selecter = new JComboBox<String>(selectList.toArray(str));

			this.setLayout(new GridLayout(1, 2));
			this.add(new JLabel(comment));
			this.add(this.selecter);
		}
	}

	static interface SetValue {
		void set(double a);
	}

}
