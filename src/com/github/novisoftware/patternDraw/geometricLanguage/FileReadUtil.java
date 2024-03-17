package com.github.novisoftware.patternDraw.geometricLanguage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FileReadUtil {
	private  static final String encoding = "UTF-8";

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
				for (String token : line.split("\\s")) {
					System.out.println("line: " + lineNumber + " token: \"" + token + "\"" );

					list.add(new Token(token, lineNumber));
				}
			}
		}
		br.close();

		return list;
	}
}
