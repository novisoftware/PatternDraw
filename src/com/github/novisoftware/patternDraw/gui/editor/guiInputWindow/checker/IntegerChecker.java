package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker;

import java.math.BigInteger;

public class IntegerChecker extends AbstractInputChecker {
	@Override
	public
	void check(String s) {
		if (s.length() == 0) {
			isValid = false;
			message = "整数を入力してください";
			return;
		}

		/*
		if (s.matches("[\\+-]?[0-9]+")) {
			isValid = true;
			message = " ";
		} else {
			isValid = false;
			message = "整数を正しい形式で入力してください";
		}
		*/
		try {
			new BigInteger(s);

			isValid = true;
			message = " ";
		} catch (Exception e) {
			isValid = false;
			message = "整数を正しい形式で入力してください";
		}
	}
}