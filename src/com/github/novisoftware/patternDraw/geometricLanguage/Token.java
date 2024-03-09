package com.github.novisoftware.patternDraw.geometricLanguage;

class Token {
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