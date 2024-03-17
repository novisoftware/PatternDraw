package com.github.novisoftware.patternDraw.geometricLanguage;

public class InvaliScriptException extends Exception {
	private final Token causedToken;

	InvaliScriptException(String message, Token causedToken) {
		super(message);
		this.causedToken = causedToken;
	}

	/**
	 * @return causedToken
	 */
	public Token getCausedToken() {
		return causedToken;
	}
}
