package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker;

import java.util.HashSet;
import java.util.List;

public class VariableNameChecker extends AbstractInputChecker {
	private String oldName;
	private HashSet<String> variables;

	public VariableNameChecker(String oldName, List<String> variables) {
		this.oldName = oldName;
		this.variables = new HashSet<String>();
		this.variables.addAll(variables);
		this.message = "変更する変数名を入力してください (元の変数名: " + this.oldName + ")";
	}

	public VariableNameChecker(HashSet<String> variables) {
		this.oldName = null;
		this.variables = variables;
		this.message = "変更する変数名を入力してください";
	}

	@Override
	public
	void check(String s) {
		if (s.equals(oldName)) {
			isValid = true;
			message = " ";
		} else if (variables.contains(s)) {
			isValid = false;
			message = "すでに使われている変数名です";
		} else if (s.matches("[A-Za-z][A-Za-z0-9_]*")) {
			isValid = true;
			message = " ";
		} else if (s.length() == 0) {
			isValid = false;
			message = "変数名を入力してください";
		} else {
			isValid = false;
			message = "変数名に使用できない文字が含まれています";
		}
	}
}