package com.github.novisoftware.patternDraw.geometricLanguage.token;

import java.io.IOException;
import java.util.ArrayList;

import com.github.novisoftware.patternDraw.utils.FileReadUtil;

/**
 * トークン列
 *
 */
public class TokenList {
	private final ArrayList<Token> tokenList;

	public TokenList(String path) throws IOException {
		this.tokenList = FileReadUtil.readTokenList(path);
	}

	public TokenList(ArrayList<Token> tokenList) {
		this.tokenList = tokenList;
	}

	/**
	 * 指定されたワードの前後でトークン列を分割する
	 * @param key
	 * @return
	 */
	public ArrayList<TokenList> separateWithKey(String key) {
		ArrayList<TokenList> ret = new ArrayList<TokenList>();
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

		ret.add(new TokenList(before));
		if (after.size() > 0) {
			ret.add(new TokenList(after));
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
