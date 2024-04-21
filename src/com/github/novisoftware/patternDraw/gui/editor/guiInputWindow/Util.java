package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.github.novisoftware.patternDraw.gui.editor.core.RpnUtil;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditParameterDefinitionListWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.ControlElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.utils.Preference;

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


	/**
	 * パラメーター設定ウィンドウのOKボタンを作る
	 */
	static JButton generateSubmitButton2(final EditParameterDefinitionListWindow caller, final InputParamDefWindow tf) {
		JButton buttonOk = new JButton(Preference.OK_BUTTON_STRING);
		buttonOk.setFont(Preference.OK_BUTTON_FONT);
		buttonOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				// 更新済パラメーターに合わせて、パラメタ一覧ウィンドウを更新する
				caller.updateParamDef(tf.param);
				caller.repaint();
				// 呼び出し元の参照を削除
				caller.subWindowDisposeNotify();
				// 編集ウィンドウは閉じる
				tf.dispose();
			}
		});

		return buttonOk;
	}

	/**
	 * パラメーター設定ウィンドウのCancelボタンを作る
	 */
	static JButton generateCancelButton2(final EditParameterDefinitionListWindow caller, final InputParamDefWindow tf) {
		JButton buttonCancel = new JButton(Preference.CANCEL_BUTTON_STRING);
		buttonCancel.setFont(Preference.CANCEL_BUTTON_FONT);
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 変更を元に戻す
				tf.revert();
				// 呼び出し元の参照を削除
				caller.subWindowDisposeNotify();
				// 編集ウィンドウは閉じる
				tf.dispose();
			}
		});

		return buttonCancel;
	}

	/**
	 * パラメーター設定ウィンドウのDeleteボタンを作る
	 * (パラメーターを削除する)
	 */
	static JButton generateDeleteButton2(final EditParameterDefinitionListWindow caller, final InputParamDefWindow tf) {
		JButton buttonCancel = new JButton(Preference.DELETE_BUTTON_STRING);
		buttonCancel.setFont(Preference.CANCEL_BUTTON_FONT);
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 変更を元に戻す
				caller.removeParamDefFromPane(tf.param);
				caller.repaintHard();
				// 呼び出し元の参照を削除
				caller.subWindowDisposeNotify();
				// 編集ウィンドウは閉じる
				tf.dispose();
			}
		});

		return buttonCancel;
	}

}

