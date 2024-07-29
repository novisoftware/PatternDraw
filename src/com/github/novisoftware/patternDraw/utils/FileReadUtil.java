package com.github.novisoftware.patternDraw.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.github.novisoftware.patternDraw.utils.token.Token;

/**
 * ファイルからテキストを読み込みトークン化する。 記載ルールは以下です。
 * <ul>
 * <li>テキストファイルの1行がトークンに分割されます。
 * <li># で開始する行はコメントです。
 * <li>トークンが空白または「"」(ダブルクォート)を含む場合、「"」で囲まれます。
 * <li>トークン中のダブルクォートは「""」(ダブルクォート2個)に置換されます。
 * </ul>
 *
 * CSVに似ていますが、コメント行があること、および、以下が違います。
 * <ul>
 * <li>空白で区切ります。
 * <li>空白の連続は単一の空白と同じ意味になります。
 * <li>改行を含む文字列は許容していません。
 * </ul>
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
	 * 1行からトークン切り出し( ArrayList<String> での返却 )
	 *
	 * @param src
	 *            1行分の文字列
	 * @return トークンとして切り出した文字列のリスト
	 */
	public static ArrayList<String> tokenize(String src) {
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
				} else {
					sb.append(c);
				}
			} else {
				if (c == '"') {
					if (i < n - 1 && src.charAt(i+1) == '"') {
						// 入力文字列中に "" が出てきた場合は出力文字列に " を追加する。
						sb.append(c);
						i += 1;
					}
					else {
						ret.add(sb.toString());
						sb = new StringBuffer();
						isQuoted = false;
					}
				} else {
					sb.append(c);
				}
			}
		}

		// lineの文字列末尾に入っている値
		// (クォートされていない場合)
		if (sb.length() > 0) {
			ret.add(sb.toString());
		}

		return ret;
	}

	/**
	 * 1行からトークン切り出し(String[] での返却)
	 *
	 * @param src
	 *            1行分の文字列
	 * @return トークンとして切り出した文字列のリスト
	 */
	public static String[] tokenizeToArray(String src) {
		ArrayList<String> tokenized = tokenize(src);

		String[] ret = new String[tokenized.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = tokenized.get(i);
		}
		return ret;
	}

	/**
	 * 文字列をエスケープする。
	 *
	 * @param s 生の文字列
	 * @return エスケープした文字列
	 */
	public static String escape(String s) {
		if (s == null || s.equals("")) {
			return "\"\"";
		} else if (s.indexOf('"') != -1 || s.indexOf(' ') != -1) {
			return '"' + s.replaceAll("\"", "\"\"") + '"';
		} else {
			return s;
		}
	}

	/**
	 * 文字列をアンエスケープする。
	 * (仮実装。想定する正しい使い方は 1行を tokenizeToArray() で分解する)
	 *
	 * @param s 生の文字列
	 * @return エスケープした文字列
	 */
	public static String unescape(String s) {
		if (s.equals("\"\"")) {
			return "";
		}
		if (s.charAt(0) != '"') {
			return s;
		}
		return s.replaceAll("\"\"", "\"");
	}

	public static String escape(Boolean b) {
		if (b != null) {
			return b ? "true" : "false";
		}
		return "null";
	}

	public static Boolean booleanValue(String str) {
		String s = str.toLowerCase();
		if (s.equals("true")) {
			return true;
		}
		else if (s.equals("false")) {
			return false;
		}
		return null;
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
