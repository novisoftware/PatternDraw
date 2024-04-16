package com.github.novisoftware.patternDraw.gui.editor.core;

import java.util.ArrayList;

import com.github.novisoftware.patternDraw.utils.FileReadUtil;

public class RpnUtil {
	static public ArrayList<String> s2a(String s) {
		return FileReadUtil.tokenize(s);
	}

	static public String a2s(ArrayList<String> list) {
		ArrayList<String> escaped = new ArrayList<String>();
		for (String s : list) {
			escaped.add(FileReadUtil.escape(s));
		}
		return String.join(" ", escaped);
	}

	static public boolean isAlpha( char c ) {
		return ('a'<= c && c<='z') || ('A'<=c && c<='Z');
	}

	static public  boolean isNumber( char c ) {
		return ('0'<= c && c<='9') || (c=='.');
	}

	static public boolean hasComment(String value) {
		return value.matches("[^;]+;(.*)");
	}

	static public String getComment(String value) {
		return value.replaceAll("[^;]+;(.*)", "$1");
	}

	static public String getRepresent(String value) {
		return value.replaceAll(";.*", "");
	}

	// 以下の記号以外は、すべて変数or定数。
	static public  boolean isNameElement( char c ) {
		return "*/-+[](),".indexOf(c) == -1;
	}
}
