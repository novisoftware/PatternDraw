package com.github.novisoftware.patternDraw.geometricLanguage.token;

/**
 * トークン。
 *
 * スクリプトの実行のためには文字列だけを使う。
 * エラーを発生させる際に文字列と行番号を使用したい。
 */
public class Token {
	/**
	 * トークン文字列
	 */
	private final String token;
	/**
	 * 行番号
	 */
	private final int lineNumber;

	public Token(String token, int lineNumber) {
		this.token = token;
		this.lineNumber = lineNumber;
	}

	/**
	 * トークン文字列を取得します。
	 *
	 * @return token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * 行番号を取得します。
	 *
	 * @return lineNumber
	 */
	public int getLineNumber() {
		return lineNumber;
	}
}