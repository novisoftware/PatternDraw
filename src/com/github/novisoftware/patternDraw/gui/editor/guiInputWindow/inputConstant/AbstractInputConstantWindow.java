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
