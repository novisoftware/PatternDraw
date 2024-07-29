package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.inputConstant;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P017___Comment;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.misc.JFrame2;
import com.github.novisoftware.patternDraw.gui.misc.JLabel2;
import com.github.novisoftware.patternDraw.gui.misc.JTextField2;
import com.github.novisoftware.patternDraw.utils.GuiPreference;

/**
 * コメントの編集を行う
 *
 */
public class InputCommentWindow extends JFrame2 {
	ArrayList<String> rpnArray;
	final P017___Comment targetElement;
	JLabel messageDisp;
	JButton buttonOk;
	protected final EditDiagramPanel editPanel;

	String newFontSize;
	
	public 	InputCommentWindow(final P017___Comment element, final EditDiagramPanel editPanel) {
		super();
		final InputCommentWindow thisObj = this;
		
		this.editPanel = editPanel;
		this.newFontSize = element.getFontSize();

		// ターゲットにする要素の設定
		this.targetElement = element;

		String message = "コメントを設定します。";
		this.setTitle(message);
		this.setSize(550, 250);
		
		
		////////////////////////////////////////////////////////////////////
		// レイアウト
		Container pane = this.getContentPane();
		this.setLayout(new FlowLayout(FlowLayout.LEADING));

		messageDisp = new JLabel2(message);
		pane.add(messageDisp);

		// 不可視の水平線を作成する (レイアウトの調整)
		addHorizontalRule__test(pane, 5);

		JTextField2 textField = new JTextField2(element.getCommentString());
		textField.setPreferredSize(new Dimension(500, 30));
		pane.add(textField);


		// 不可視の水平線を作成する (レイアウトの調整)
		addHorizontalRule__test(pane, 5);

		// 字の大きさを変更する部品
		JRadioButton[] buttons = new JRadioButton[P017___Comment.TEXT_SIZE_LIST.length];
		ButtonGroup group = new ButtonGroup();
		for (int i = 0; i < P017___Comment.TEXT_SIZE_LIST.length; i++) {
			String value = P017___Comment.TEXT_SIZE_LIST[i];

			final JRadioButton radioButton = new JRadioButton(value);
			radioButton.setFont(GuiPreference.LABEL_FONT);
			radioButton.setBackground(GuiPreference.BG_COLOR);
			radioButton.setForeground(GuiPreference.TEXT_COLOR);

			buttons[i] = radioButton;
			if (P017___Comment.TEXT_SIZE_VALUES[i].equals(element.getFontSize())) {
				radioButton.setSelected(true);
			}
			group.add(radioButton);
			pane.add(radioButton);
		}

		for (int i = 0; i < P017___Comment.TEXT_SIZE_LIST.length; i++) {
			final JRadioButton radioButton = buttons[i];
			final int idx = i;
			radioButton.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					if (radioButton.isSelected()) {
						thisObj.newFontSize = P017___Comment.TEXT_SIZE_VALUES[idx];
					}
				}
			});
		}

		// 不可視の水平線を作成する (レイアウトの調整)
		addHorizontalRule__test(pane, 5);

		this.buttonOk = new JButton(GuiPreference.OK_BUTTON_STRING);
		this.buttonOk.setFont(GuiPreference.OK_BUTTON_FONT);
		this.buttonOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				element.setCommentString(textField.getText());
				element.setFontSize(thisObj.newFontSize);
				thisObj.dispose();
				thisObj.editPanel.repaint();
			}
		});
		pane.add(this.buttonOk);

		JButton buttonCancel = new JButton(GuiPreference.CANCEL_BUTTON_STRING);
		buttonCancel.setFont(GuiPreference.CANCEL_BUTTON_FONT);
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				thisObj.dispose();
			}
		});
		pane.add(buttonCancel);
	}
}
