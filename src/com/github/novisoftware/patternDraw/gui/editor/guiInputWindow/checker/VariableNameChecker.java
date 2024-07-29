package com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.checker;

import java.util.HashSet;
import java.util.List;

import com.github.novisoftware.patternDraw.core.langSpec.VariableName;
import com.github.novisoftware.patternDraw.utils.Debug;

public class VariableNameChecker extends AbstractInputChecker {
	private String oldName;
	private String newName;
	private HashSet<String> variables;

	public VariableNameChecker(String oldName, List<String> variables) {
		this.oldName = oldName;
		this.newName = oldName;
		this.variables = new HashSet<String>();
		this.variables.addAll(variables);
		this.message = "変更する変数名を入力してください (元の変数名: " + this.oldName + ")";
	}

	public boolean isChanged() {
		if (oldName == null) {
			// 元の変数名が設定されていない場合、参照している箇所もなく、変更通知を発生させる必要がないため。
			// ( なお、oldName == null となる状況が実際にはない可能性もあり )
			return false;
		}
		return ! oldName.equals(newName);
	}
	
	public String getOldName() {
		return oldName;
	}

	public String getNewName() {
		return newName;
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
			return;
		}
		
		else if (s.equals(oldName)) {
			// 前と名前が変わっていなければ チェック上問題なし
			isValid = true;
			message = " ";
			return;
		}

		if (variables.contains(s)) {
			// 他の変数と名前が衝突していれば チェック上NG
			isValid = false;
			message = "すでに使われている変数名です";
			return;
		}
		String msg = VariableName.validateVariableName(s);
		Debug.println("check: \"" + s + "\"");
		Debug.println("check result = " + msg);
		if (msg != null) {
			isValid = false;
			message = msg;
			return;
		}

		isValid = true;
		message = " ";
		this.newName = s;
	}
}