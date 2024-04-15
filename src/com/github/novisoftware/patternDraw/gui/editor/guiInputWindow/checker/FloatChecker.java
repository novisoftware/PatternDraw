package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker;

public class FloatChecker extends AbstractInputChecker {
	public FloatChecker() {
		super();
		this.message = "数(浮動小数点)を入力してください";
	}

	@Override
	public
	void check(String s) {
		if (s.length() == 0) {
			isValid = false;
			message = "数を入力してください";
			return;
		}

		try {
			Double.parseDouble(s);

			isValid = true;
			message = " ";
		} catch (Exception e) {
			isValid = false;
			message = "数(浮動小数点)を正しい形式で入力してください";
		}
	}
}
