package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker;

public class NumericChecker extends AbstractInputChecker {
	public NumericChecker() {
		this.message = "数(小数可)を入力してください";
	}

	@Override
	public
	void check(String s) {
		if (s.matches("[\\+-]?[0-9]+([.][0-9]+)?")) {
			isValid = true;
			message = " ";
		} else if (s.length() == 0) {
			isValid = false;
			message = "数(小数可)を入力してください";
		} else {
			isValid = false;
			message = "数(小数可)を正しい形式で入力してください";
		}
	}
}