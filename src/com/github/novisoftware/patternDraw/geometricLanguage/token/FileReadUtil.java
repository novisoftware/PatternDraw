package com.github.novisoftware.patternDraw.geometricLanguage.token;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * ファイルからテキストを読み込みトークン化する。
 *
 */
public class FileReadUtil {
	private static final String ENCODING = "UTF-8";

	/**
	 * テキストファイル読み取り
	 *
	 * @param path
	 * @return 読み取ったテキスト
	 * @throws IOException
	 */
	public static ArrayList<String> readText(String path) throws IOException {
		ArrayList<String> ret = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), ENCODING));
		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			ret.add(line);
		}
		br.close();

		return ret;
	}

	/**
	 * 1行からトークン切り出し
	 *
	 * @param src
	 *            1行分の文字列
	 * @return トークンとして切り出した文字列のリスト
	 */
	private static ArrayList<String> tokenize(String src) {
		ArrayList<String> ret = new ArrayList<String>();
		int n = src.length();
		StringBuffer sb = new StringBuffer();
		boolean isQuoted = false;
		for (int i = 0; i < n; i++) {
			char c = src.charAt(i);

			if (!isQuoted) {
				if (c == ' ') {
					if (sb.length() > 0) {
						ret.add(sb.toString());
						sb = new StringBuffer();
					}
				} else if (c == '"') {
					isQuoted = true;
					if (sb.length() > 0) {
						sb.append('"');
					}
				} else {
					sb.append(c);
				}
			} else {
				if (c == '"') {
					isQuoted = false;
				} else {
					sb.append(c);
				}
			}
		}

		if (sb.length() > 0) {
			ret.add(sb.toString());
		}

		return ret;
	}

	/**
	 * 行ごとの文字列をトークン化する。
	 *
	 * @param lines
	 *            行
	 * @return トークン列
	 */
	private static ArrayList<Token> LinesToTokenList(ArrayList<String> lines) {
		ArrayList<Token> list = new ArrayList<Token>();
		int lineNumber = 0;
		for (String line : lines) {
			lineNumber++;
			if (line.startsWith("#")) {
				continue;
			}

			if (line.length() > 0) {
				for (String token : FileReadUtil.tokenize(line)) {
					// System.out.println("line: " + lineNumber + " token: \"" +
					// token + "\"" );
					list.add(new Token(token, lineNumber));
				}
			}
		}

		return list;
	}

	/**
	 * ファイルからテキストを読み込みトークン化する。
	 *
	 * @param path
	 *            入力ファイルのパス
	 * @return トークン列
	 * @throws IOException
	 */
	public static ArrayList<Token> readTokenList(String path) throws IOException {
		ArrayList<String> lines = FileReadUtil.readText(path);
		ArrayList<Token> list = LinesToTokenList(lines);

		return list;
	}
}
