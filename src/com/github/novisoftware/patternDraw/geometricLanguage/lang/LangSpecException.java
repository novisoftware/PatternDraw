package com.github.novisoftware.patternDraw.geometricLanguage.lang;

public class LangSpecException extends Exception {
	public static String WRONG_EXPORT_FILE = "記述ファイルに異常があります。";
	
	public LangSpecException(String message) {
		super(message);
	}
}
