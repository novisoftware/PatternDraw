package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.inputConstant;

import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.github.novisoftware.patternDraw.core.RpnUtil;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P022_____RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P020___AbstractElement.KindId;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.AbstractInputChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.FloatChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.IntegerChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.NonCheckChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.NumericChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.VariableNameChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.misc.JLabel2;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.GuiPreference;

/**
 * ダイヤグラム中の定数値の編集を行う
 *
 */
public class InputConstantScalarWindow extends AbstractInputConstantWindow {
	public InputConstantScalarWindow(final P022_____RpnGraphNodeElement element, final EditDiagramPanel editPanel) {
		super(element, editPanel);
		if (element.getKindId() != KindId.CONSTANT) {
			System.err.println("呼び出し条件がおかしいので要確認。");
			try {
				throw new Exception("check");
			} catch (Exception e) {
				// 場所を強調するため
				e.printStackTrace();
			}
		}

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
			radioButton.setFont(GuiPreference.LABEL_FONT);
			radioButton.setBackground(GuiPreference.BG_COLOR);
			radioButton.setForeground(GuiPreference.TEXT_COLOR);

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
						inputChecker = new VariableNameChecker(value, editPanel.networkDataModel.variableNameList);
					}
					messageDisp.setText(inputChecker.message);

					ValueInputPanel p = new ValueInputPanel(this, index, "", comment, value, inputChecker);

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
										((P022_____RpnGraphNodeElement) element).setValueType(valueTypes[idx]);
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
}
