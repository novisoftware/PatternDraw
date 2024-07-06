package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker;

import java.util.HashSet;
import java.util.List;

import com.github.novisoftware.patternDraw.core.langSpec.VariableName;

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
		if (s.length() == 0) {
			// 長さ 0 の文字列は許可されない
			isValid = false;
			message = "変数名を入力してください";
		} else if (s.equals(oldName)) {
			// 前と名前が変わっていなければ チェック上問題なし
			isValid = true;
			message = " ";
		} else if (variables.contains(s)) {
			// 他の変数と名前が衝突していれば チェック上NG
			isValid = false;
			message = "すでに使われている変数名です";
		} else {
			String msg = VariableName.validateVariableName(s);
			if (msg == null) {
				isValid = true;
				message = " ";
			}
			else {
			}
		}
	}
}