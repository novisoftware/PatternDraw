package com.github.novisoftware.patternDraw.geometricLanguage.lang;

import com.github.novisoftware.patternDraw.geometricLanguage.token.Token;

public class InvaliScriptException extends Exception {
	private final Token causedToken;

	public InvaliScriptException(String message, Token causedToken) {
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
