package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.inputConstant;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker.AbstractInputChecker;
import com.github.novisoftware.patternDraw.gui.misc.JLabel2;
import com.github.novisoftware.patternDraw.gui.misc.JTextField2;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.Preference;

class ValueInputPanel extends JPanel {
	final AbstractInputConstantWindow frame;
	final JTextField textField;
	AbstractInputChecker checker;

	public ValueInputPanel(AbstractInputConstantWindow frame,
			int param_index,
			String comment,
			String ini,
			AbstractInputChecker checker) {
		Debug.println("Comment: " + comment);
		Debug.println("Ini: --" + ini + "--");

		this.frame = frame;
		this.checker = checker;

		this.textField = new JTextField2("" + ini);
		/*
		this.textField = new JTextField("" + ini);
		// TODO テキストフィールドのサイズ指定が、あまりうまく行っていない。
		this.textField.setPreferredSize(new Dimension(200,20));
		this.textField.setFont(Preference.CONSOLE_FONT);
		*/
		this.setLayout(new GridLayout(1, 2));
		this.add(new JLabel2(comment));
		this.add(this.textField);
		this.frame.messageDisp.setText(checker.message);
		this.setBackground(Preference.BG_COLOR);

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

	void updateMessage() {
		final Color COLOR_ERROR = Color.RED;
		final Color COLOR_NORMAL = Color.BLACK;

		String text = textField.getText();
		checker.check(text);
		if (checker.isOk()) {
			frame.buttonOk.setEnabled(true);
			frame.messageDisp.setForeground(COLOR_NORMAL);
			frame.messageDisp.setText(checker.message);
		} else {
			frame.buttonOk.setEnabled(false);
			frame.messageDisp.setForeground(COLOR_ERROR);
			frame.messageDisp.setText(checker.message);
		}
	}

	public void setInputChecker(AbstractInputChecker checker) {
		this.checker = checker;
		this.updateMessage();
	}
}