package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker;

public abstract class AbstractInputChecker {
	protected boolean isValid = false;
	public boolean isOk() {
		return isValid;
	}

	// String message = " ";
	public String message = "整数を入力してください";

	public abstract void check(String s);
}