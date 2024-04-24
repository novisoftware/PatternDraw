package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker;

public class BooleanChecker extends AbstractInputChecker {
	public BooleanChecker() {
		this.message = "ブール値(true または false)を入力してください";
	}

	@Override
	public
	void check(String s) {
		String s2 = s.toLowerCase();
		if (s2.equals("true") || s2.equals("false")) {
			isValid = true;
			message = " ";
		} else {
			isValid = false;
			message = "ブール値(true または false)を入力してください";
		}
	}
}