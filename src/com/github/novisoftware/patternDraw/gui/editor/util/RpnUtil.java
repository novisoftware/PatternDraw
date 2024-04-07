package com.github.novisoftware.patternDraw.gui.editor.util;

import java.util.ArrayList;

public class RpnUtil {
	static public ArrayList<String> s2a(String s) {
		String a1[] = s.split(" +");
		ArrayList<String> a2 = new ArrayList<String>();
		for(String s1 : a1) {
			a2.add(s1);
		}
		return a2;
	}

	static public String a2s(ArrayList<String> s) {
		return String.join(" ", s);
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

	/*
	static public boolean isNameElement( char c ) {
		return isAlpha(c) || (c=='_') || isNumber(c);
	}
*/
	// 以下の記号以外は、すべて変数or定数。
	static public  boolean isNameElement( char c ) {
		return "*/-+[](),".indexOf(c) == -1;
	}
}
