package com.github.novisoftware.patternDraw.geometricLanguage;

/**
 * トークン。
 * スクリプトの実行のためには文字列だけを使う。
 * エラーを発生させる際に文字列と行番号を使用したい。
 *
 */
public class Token {
	private final int lineNumber;
	private final String token;

	public Token(String token, int lineNumber) {
		this.token = token;
		this.lineNumber = lineNumber;
	}

	/**
	 * @return lineNumber
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * @return token
	 */
	public String getToken() {
		return token;
	}
}