package com.github.novisoftware.patternDraw.geometricLanguage;

public class InvalidProgramException extends Exception {
	private final Token causedToken;

	InvalidProgramException(String message, Token causedToken) {
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
