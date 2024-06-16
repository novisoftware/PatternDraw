package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.inputConstant;

import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.github.novisoftware.patternDraw.core.RpnUtil;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueBoolean;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.NonCheckChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P020___AbstractElement.KindId;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P022_____RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.misc.JLabel2;
import com.github.novisoftware.patternDraw.gui.misc.JRadioButton2;
import com.github.novisoftware.patternDraw.utils.Debug;

/**
 * ダイヤグラム中の定数値の編集を行う
 *
 */
public class InputConstantBooleanWindow extends AbstractInputConstantWindow {
	public InputConstantBooleanWindow(final P022_____RpnGraphNodeElement element, final EditDiagramPanel editPanel) {
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
		Container pane = this.getContentPane();
		this.setLayout(new FlowLayout(FlowLayout.LEADING));

		messageDisp = new JLabel2(" ");
		pane.add(messageDisp);

		// 不可視の水平線を作成する (レイアウトの調整)
		addHorizontalRule__test(pane, 5);

		Debug.println("ElementEdit", "RPN to Edit is " + element.getRpnString());
		rpnArray = element.getRpn().getArrayCopy();

		// 元々は、RPN式で各変数で編集させたいものはコメントを設定しておくという意図
		for (int index = 0; index < rpnArray.size(); index++) {
			String s0 = rpnArray.get(index);
			// 「;」の右側がコメント
			if (RpnUtil.hasComment(s0)) {
				final String comment = RpnUtil.getComment(s0);
				String value = RpnUtil.getRepresent(s0);
				boolean initValue = new ValueBoolean(value).getInternal();

				if (comment.length() > 0) {
					JRadioButton radioButtons[] = new JRadioButton[2];
					ButtonGroup group = new ButtonGroup();
					String[] labels = {ValueBoolean.LABEL_TRUE, ValueBoolean.LABEL_FALSE};
					Boolean[] values = {true, false};
					for (int i = 0 ; i < labels.length ; i ++) {
						final JRadioButton radioButton = new JRadioButton2(labels[i]);
						final String label = labels[i];
						final int f_index = index;
						radioButton.addChangeListener(new ChangeListener() {
							@Override
							public void stateChanged(ChangeEvent e) {
								if (radioButton.isSelected()) {
									rpnArray.set(f_index, label + (comment.length() > 0 ? ";" + comment : ""));
								}
							}
						});

						if (initValue == values[i]) {
							radioButton.setSelected(true);
						}
						group.add(radioButton);
						radioButtons[i] = radioButton;
						pane.add(radioButton);
					}

					ValueInputPanel p = new ValueInputPanel(this, index, "", comment, value, new NonCheckChecker());

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
