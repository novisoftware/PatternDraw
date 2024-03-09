package com.github.novisoftware.patternDraw.geometricLanguage;

import java.io.IOException;
import java.util.ArrayList;

public class TokenList {
	private final ArrayList<Token> tokenList;

	public TokenList(String path) throws IOException {
		tokenList = FileReadUtil.readTokenList(path);
	}

	/**
	 * @return tokenList
	 */
	public ArrayList<Token> getList() {
		return tokenList;
	}
}