package com.github.novisoftware.patternDraw.geometricLanguage;

import java.io.IOException;
import java.util.ArrayList;

public class TokenList {
	private final ArrayList<Token> tokenList;

	public TokenList(String path) throws IOException {
		tokenList = FileReadUtil.readTokenList(path);
	}

	/**
	 * 指定されたワードの前後で分割する
	 * @param key
	 * @return
	 */
	public ArrayList<ArrayList<Token>> separateWithKey(String key) {
		ArrayList<ArrayList<Token>> ret = new ArrayList<ArrayList<Token>>();
		ArrayList<Token> before = new ArrayList<Token>();
		ArrayList<Token> after = new ArrayList<Token>();
		boolean isFound = false;
		for (Token token : this.tokenList) {
			if (token.getToken().equals(key)) {
				isFound = true;
				continue;
			}
			if (isFound) {
				after.add(token);
			}
			else {
				before.add(token);
			}
		}

		ret.add(before);
		if (after.size() > 0) {
			ret.add(after);
		}

		return ret;
	}

	/**
	 * @return tokenList
	 */
	public ArrayList<Token> getList() {
		return tokenList;
	}
}
