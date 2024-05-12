package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.inputConstant;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;

import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P020___AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P022_____RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.misc.JFrame2;
import com.github.novisoftware.patternDraw.utils.Debug;

/**
 * ダイヤグラム中の定数値の編集を行う
 *
 */
public class AbstractInputConstantWindow extends JFrame2 {
	ArrayList<String> rpnArray;
	final P020___AbstractElement targetElement;
	JLabel messageDisp;
	JButton buttonOk;


	public 	AbstractInputConstantWindow(final P022_____RpnGraphNodeElement element, final EditDiagramPanel editPanel) {
		super();
		Debug.println("ElementEdit", "RPN to Edit is " + element.getRpnString());
		rpnArray = element.getRpn().getArrayCopy();

		// ターゲットにする要素の設定
		this.targetElement = element;

		String message = element.getKindString() + " を設定します。";
		this.setTitle(message);
		this.setSize(500, 250);
	}
}
