package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.inputConstant;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.github.novisoftware.patternDraw.core.RpnUtil;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P020___AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P022_____RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P030____ControlElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P020___AbstractElement.KindId;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.AbstractInputChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.IntegerChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.NumericChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.VariableNameChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.NonCheckChecker;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.misc.JLabel2;
import com.github.novisoftware.patternDraw.utils.Debug;

/**
 *
 * RPN式の編集を行う
 *
 */
public class InputOtherTypeWindow  extends AbstractInputConstantWindow {
	/**
	 * 呼び出し元画面
	 */
	final int LINES = 14;

	ArrayList<String> rpnArray;
	final P020___AbstractElement targetElement;

	public InputOtherTypeWindow(final P022_____RpnGraphNodeElement element, final EditDiagramPanel editPanel) {
		super(element, editPanel);
		this.targetElement = element;
		c2(element, editPanel);
	}

	public InputOtherTypeWindow(final P030____ControlElement element, final EditDiagramPanel editPanel) {
		super(element, editPanel);
		this.targetElement = element;
		c2(element, editPanel);
	}

	private boolean hasInputArea = false;
	public boolean hasInputArea() {
		return hasInputArea;
	}
	
	public void c2(final P020___AbstractElement element, final EditDiagramPanel editPanel) {
		this.setTitle(element.getKindString() + " を編集");

		////////////////////////////////////////////////////////////////////
		// レイアウト

		////////////////////////////////////////////////////////////////////
		// レイアウト
		Container pane = this.getContentPane();
		this.setLayout(new FlowLayout(FlowLayout.LEADING));

		messageDisp = new JLabel2(" ");
		pane.add(messageDisp);

		if (element instanceof P030____ControlElement) {
			P030____ControlElement e = (P030____ControlElement) element;
			Debug.println("ElementEdit", "RPN to Edit is " + e.getRpnString());
			rpnArray = e.getRpn().getArray();
		} else if (element instanceof P022_____RpnGraphNodeElement) {
			P022_____RpnGraphNodeElement e = (P022_____RpnGraphNodeElement) element;
			Debug.println("ElementEdit", "RPN to Edit is " + e.getRpnString());
			rpnArray = e.getRpn().getArray();
		}

		if (   // CONSTANT, VARIABLE_SET の場合は来ない。
				// element.getKindId() == KindId.CONSTANT || 
				// || element.getKindId() == KindId.VARIABLE_SET
				element.getKindId() == KindId.CONTROL
				
				
				) { // element.getKindString().equals("定数"))
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
						// 入力に対して行う検査の種類
						String checkType = comment.substring(0, 1);
						// 表示部
						String comment2 = comment.replaceAll(".*,", "");

						System.out.println("s0 = " + s0);
						System.out.println("checkType = " + checkType + "   .... " + comment);
						
						AbstractInputChecker inputChecker;
						if (checkType.equals("n")) {
							inputChecker = new NumericChecker();
						}
						else if (checkType.equals("v")) {
							inputChecker = new VariableNameChecker(value, editPanel.networkDataModel.refVariableNameList);
						}
						else if (checkType.equals("i")) {
							inputChecker = new IntegerChecker();
						}
						else {
							inputChecker = new IntegerChecker();
						}
						messageDisp.setText(inputChecker.message);

						ValueInputPanel p = new ValueInputPanel(this, index, "", comment, comment2, value, inputChecker);
						pane.add(p);

						this.hasInputArea = true;
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
								editPanel.networkDataModel.refVariableNameList);

						// (this, index, comment, value, inputChecker);

						pane.add(p);
						this.hasInputArea = true;
					}
				}
			}
		}

		// 不可視の水平線を作成する (レイアウトの調整)
		addHorizontalRule__test(pane, 5);

		this.buttonOk = Util.generateSubmitButton(editPanel, this);
		pane.add(this.buttonOk);
		pane.add(Util.generateCancelButton(editPanel, this));

		// this.setSize(500, 200);
	}

	static class ValueSelectPanel extends JPanel {
		final InputOtherTypeWindow frame;
		final JComboBox<String> selecter;
		final AbstractInputChecker checker;

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
