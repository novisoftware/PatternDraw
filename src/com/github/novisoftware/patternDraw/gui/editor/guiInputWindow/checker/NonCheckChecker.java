package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker;

public class NonCheckChecker extends AbstractInputChecker {
	public NonCheckChecker() {
		this.message = "文字列を入力してください。";
		isValid = true;
	}

	@Override
	public
	void check(String s) {
	}
}