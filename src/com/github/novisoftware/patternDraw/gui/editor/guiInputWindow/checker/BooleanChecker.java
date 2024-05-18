package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker;

import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueBoolean;

/**
 * 文字列で入力されたブール値のバリデーションチェックをする。
 *
 */
public class BooleanChecker extends AbstractInputChecker {
	public BooleanChecker() {
		this.message = "ブール値(true または false)を入力してください";
	}

	@Override
	public
	void check(String s) {
		String s2 = s.toLowerCase();
		if (s2.equals(ValueBoolean.LABEL_TRUE) || s2.equals(ValueBoolean.LABEL_FALSE)) {
			isValid = true;
			message = " ";
		} else {
			isValid = false;
			message = "ブール値(true または false)を入力してください";
		}
	}
}