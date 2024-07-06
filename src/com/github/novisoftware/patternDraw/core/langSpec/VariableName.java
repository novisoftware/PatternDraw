package com.github.novisoftware.patternDraw.core.langSpec;

public class VariableName {
	/**
	 * このクラスはインスタンス化しない
	 */
	private VariableName() {}

	/**
	 * 変数名の妥当性チェック。
	 * 
	 * 許可する変数名は以下のルールにする。
	 * <ul>
	 * <li> 数字で始まるものは許可しない
	 * <li> アンダーバー以外の記号は許可しない。
	 * <li> 非ASCII文字は全て許可
	 * </ul>
	 * 
	 * 通常のプログラミング言語では記号を演算子の目的で使うので、変数名に許可していない。
	 * 実装上「'」や「;」や「 」は使えないが他は使えるので許可しても良いが、許可しない。
	 * Unicodeの非ASCII文字はすべて許可する。
	 * (ギリシャ文字のθとかや、絵文字を使いたい状況を想定。 絵文字はフォントの関係で使えないが)
	 * 
	 * @param s 1文字以上の文字列(空文字列は呼び出し元で検査する)
	 * @return 妥当な場合はnullを返す。 妥当でない場合はエラーメッセージに使用可能な文字列を返す。
	 */
	public static
	String validateVariableName(String s) {
		if (containsInvalidSymbolChar(s)) {
			return "変数名に使用できない記号です";
		} else if (Character.isDigit(s.charAt(0))) {
			return "変数名を数字で始めることはできません";
		}
		return null;
	}

	/**
	 * 文字がASCII記号文字であるかを検査する。
	 * 
	 * @param c 検査対象の文字
	 * @return
	 */
	private static boolean isSymbolChar(char c) {
		if (c < 0x2F || (0x3a <= c && c <= 0x40) || (0x5b <=c && c <= 0x60) || (0x7b <=c && c <= 0x7F)) {
			return true;
		}
		
		return false;
	}

	private static boolean isInvalidSybomChar(char c) {
		if (c == '_') {
			return false;
		}
		if (isSymbolChar(c)) {
			return true;
		}
		
		return false;
	}
	
	private static boolean containsInvalidSymbolChar(String s) {
		for (int i = 0; i<s.length(); i++) {
			if (isInvalidSybomChar(s.charAt(i))) {
				return true;
			}
		}
		
		return false;
	}
}
