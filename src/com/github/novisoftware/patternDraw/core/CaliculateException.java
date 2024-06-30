package com.github.novisoftware.patternDraw.core;

/**
 * Rpnの計算エラー情報を保持する。
 */
public class CaliculateException extends Exception {
	static public final String MESSAGE_INVALID_CLASS = "入力の種類に誤りがあります。";
	static public final String MESSAGE_INVALID_VALUE = "入力の値に誤りがあります。";
	static public final String MESSAGE_NOT_ENOUGH_INPUT = "入力が設定されていません。";
	static public final String MESSAGE_ZERO_DIV = "ゼロ除算です。";
	static public final String MESSAGE_OTHER_ERROR = "その他エラー。";
	static public final String MESSAGE_OUT_OF_MEMORY = "メモリ容量の不足が発生しました。";
	static public final String MESSAGE_INTERRUPTED = "中断されました。";

	public CaliculateException(String message) {
		super(message);

		// デバッグ時
		this.printStackTrace();
	}
}