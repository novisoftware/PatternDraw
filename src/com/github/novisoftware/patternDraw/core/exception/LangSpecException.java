package com.github.novisoftware.patternDraw.core.exception;

public class LangSpecException extends Exception {
	public static String WRONG_EXPORT_FILE = "記述ファイルに異常があります。";
	
	public LangSpecException(String message) {
		super(message);
	}
}
