package com.github.novisoftware.patternDraw.core;

import java.util.ArrayList;

public class RpnMacroUtil {
	/**
	 * リピートマクロ。
	 * :MACRO_REPEAT=2,5 とあったら、直前の2個を繰り返し、5個になるようにする。
	 */
	static final String WORD_MACRO_REPEAT = ":MACRO_REPEAT=";

	/**
	 * :MACRO_REPEAT= があるかないかを調べる。
	 * 
	 * @param org
	 * @return
	 */
	static public boolean hasMacro(ArrayList<String> org) {
		int n0 = org.size();
		for (int i = 0; i< n0; i++) {
			if (org.get(i).startsWith(WORD_MACRO_REPEAT)) {
				return true;
			}
			
		}
	
		return false;
	}

	/**
	 * 文字列の中に数値が一部だけ入っている形式
	 * 
	 */
	final static String REG_DIGITS_IN_STRING = "^([^0-9]*)([0-9]+)([^0-9]*)$";

	/**
	 * リピートマクロを展開
	 * 
	 * @param org
	 * @return
	 */
	
	static public ArrayList<String> expandMacro(ArrayList<String> org) {
		if (!hasMacro(org)) {
			return org;
		}
	
		// Debug.println("RPN", " MACRO REPEAT FOUND");
		
	
		ArrayList<String> expanded = new ArrayList<String>();
		int n0 = org.size();
		for (int i = 0; i< n0; i++) {
			String word = org.get(i);
			if (word.startsWith(WORD_MACRO_REPEAT)) {
				String wk = word.substring(WORD_MACRO_REPEAT.length());
				String[] subArg = wk.split(",");
				int n = Integer.parseInt(subArg[0]);
				// マジックナンバー2は
				// a b + c + d + e +
				// と増やすので、元々ある a b を引いた数のためのもの。
				int m = Integer.parseInt(subArg[1]) - 2;
	
				for (int k = 0; k < m; k++) {
					ArrayList<String> adder = new ArrayList<String>();
					int size = org.size();
					for (int j = 0; j<n ; j++) {
						String wk2 = org.get(size - n + j - 1);
						if (wk2.matches(REG_DIGITS_IN_STRING)) {
							String header = wk2.replaceAll(REG_DIGITS_IN_STRING, "$1");
							String footer = wk2.replaceAll(REG_DIGITS_IN_STRING, "$3");
							int before = Integer.parseInt(wk2.replaceAll(REG_DIGITS_IN_STRING, "$2"));
							String body = "" + (before + 1 + k);
							wk2 = header + body + footer;
						}
						adder.add(wk2);
					}
					// Debug.println("   added adder " + adder);
					expanded.addAll(adder);
				}
			} else {
				// Debug.println("   added word " + word);
				expanded.add(word);
			}
		}
	
		/*
		Debug.println("RPN", "Expanded is:");
		for (String e : expanded) {
			Debug.println("RPN", "    " + e);
		}
		*/
		
		return expanded;
	}

	/**
	 * リピートマクロのリピート回数を書き換え
	 * 
	 * @param org
	 * @param newN リピート回数
	 * @return
	 */
	static public ArrayList<String> setRepeatN(ArrayList<String> org, int newN) {
		ArrayList<String> expanded = new ArrayList<String>();
		int n0 = org.size();
		for (int i = 0; i< n0; i++) {
			String word = org.get(i);
			if (word.startsWith(WORD_MACRO_REPEAT)) {
				String wk = word.substring(WORD_MACRO_REPEAT.length());
				String[] subArg = wk.split(",");
				expanded.add(WORD_MACRO_REPEAT + subArg[0] + "," + newN);
			} else {
				// Debug.println("   added word " + word);
				expanded.add(word);
			}
		}
	
		/*
		Debug.println("RPN", "Expanded is:");
		for (String e : expanded) {
			Debug.println("RPN", "    " + e);
		}
		*/
		
		return expanded;
	
	
	}

	/**
	 * リピートマクロのリピート回数を取得
	 * 
	 * @param org
	 * @return リピート回数
	 */
	static public Integer getRepeatN(ArrayList<String> org) {
		int n0 = org.size();
		for (int i = 0; i< n0; i++) {
			String word = org.get(i);
			if (word.startsWith(WORD_MACRO_REPEAT)) {
				String wk = word.substring(WORD_MACRO_REPEAT.length());
				String[] subArg = wk.split(",");
				return Integer.parseInt(subArg[1]);
			}
		}

		return null;
	}
	
}
