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
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P020___AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P020___AbstractElement.KindId;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P022_____RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.misc.JFrame2;
import com.github.novisoftware.patternDraw.gui.misc.JLabel2;
import com.github.novisoftware.patternDraw.gui.misc.JTextField2;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.GuiUtil;
import com.github.novisoftware.patternDraw.utils.Preference;

/**
 * ダイヤグラム中の定数値の編集を行う
 *
 */
public class InputConstantStringWindow extends AbstractInputConstantWindow {
	public InputConstantStringWindow(final P022_____RpnGraphNodeElement element, final EditDiagramPanel editPanel) {
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
				String comment = "";
				comment = RpnUtil.getComment(s0);
				String value = RpnUtil.getRepresent(s0);

				if (comment.length() > 0) {
					ValueInputPanel p = new ValueInputPanel(this, index, comment, value, new NonCheckChecker());

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
