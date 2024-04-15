package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.github.novisoftware.patternDraw.gui.editor.core.RpnUtil;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.ControlElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.misc.Preference;

public class Util {
	/**
	 * 入力ウィンドウのOKボタンを作る
	 */
	static JButton generateSubmitButton(final EditDiagramPanel editPanel, final InputConstantWindow tf) {
		JButton buttonOk = new JButton(Preference.OK_BUTTON_STRING);
		buttonOk.setFont(Preference.OK_BUTTON_FONT);
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

		return buttonOk;
	}

	/**
	 * 入力ウィンドウのCancelボタンを作る
	 */
	static JButton generateCancelButton(final EditDiagramPanel editPanel, final InputConstantWindow tf) {
		JButton buttonCancel = new JButton(Preference.CANCEL_BUTTON_STRING);
		buttonCancel.setFont(Preference.CANCEL_BUTTON_FONT);
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tf.dispose();
			}
		});

		return buttonCancel;
	}
}
