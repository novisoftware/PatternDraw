package com.github.novisoftware.patternDraw.geometricLanguage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FileReadUtil {
	private  static final String encoding = "UTF-8";

	static ArrayList<String> tokenize(String src) {
		ArrayList<String> ret = new ArrayList<String>();
		int n = src.length();
		StringBuffer sb = new StringBuffer();
		boolean isQuoted = false;
		for (int i = 0; i<n; i++) {
			char c = src.charAt(i);

			if (!isQuoted) {
				if (c == ' ') {
					if (sb.length() > 0) {
						ret.add(sb.toString());
						sb = new StringBuffer();
					}
				}
				else if (c == '"') {
					isQuoted = true;
					if (sb.length() > 0) {
						sb.append('"');
					}
				}
				else {
					sb.append(c);
				}
			}
			else {
				if (c == '"') {
					isQuoted = false;
				}
				else {
					sb.append(c);
				}
			}
		}

		if (sb.length() > 0) {
			ret.add(sb.toString());
		}

		return ret;
	}

	public static ArrayList<Token> readTokenList(String path) throws IOException {
		ArrayList<Token> list = new ArrayList<Token>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), encoding));
		int lineNumber = 0;
		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			lineNumber ++;
			if (line.startsWith("#")) {
				continue;
			}

			if (line.length() > 0) {
				for (String token : line.split("\\s+")) {
					if (token.length() > 0) {
						System.out.println("line: " + lineNumber + " token: \"" + token + "\"" );

						list.add(new Token(token, lineNumber));
					}
				}
			}
		}
		br.close();

		return list;
	}
}
