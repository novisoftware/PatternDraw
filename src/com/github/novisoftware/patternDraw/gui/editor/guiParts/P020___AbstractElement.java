package com.github.novisoftware.patternDraw.gui.editor.guiParts;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.FileReadUtil;

public abstract class P020___AbstractElement extends P002__AbstractIcon {
	public abstract String str();
	public abstract ArrayList<String> optStr();
	public abstract P020___AbstractElement getCopy();
	public abstract void paintWithPhase(Graphics2D g, int phase);

	protected final EditDiagramPanel editPanel;

	P020___AbstractElement(EditDiagramPanel editPanel) {
		this.editPanel = editPanel;

		if (string2kind == null) {
			string2kind = new HashMap<>();
			string2kind.put("入力", KindId.INPUT);
			string2kind.put("表示", KindId.DISPLAY);
			string2kind.put("定数", KindId.CONSTANT);
			string2kind.put("変数を設定", KindId.VARIABLE_SET);
			string2kind.put("変数を参照", KindId.VARIABLE_REFER);
			string2kind.put("演算子", KindId.OPERATOR);
			string2kind.put("制御", KindId.CONTROL);

			kind2string = new HashMap<>();
			for (String s: string2kind.keySet()) {
				kind2string.put(string2kind.get(s), s);
			}
		}

		this.debugId = P020___AbstractElement.getDebugId();
	}

	static HashMap<String, KindId> string2kind;
	static HashMap<KindId, String> kind2string;

	public static enum KindId {
		INPUT,
		DISPLAY,
		CONSTANT,
		VARIABLE_SET,
		VARIABLE_REFER,
		OPERATOR,
		CONTROL
	}

	/**
	 * デバッグを便利なようにするため、表示用のID。
	 */
	public final int debugId;
	static int debugIdSequence = 1;
	static int getDebugId() {
		return debugIdSequence++;
	}

	/**
	 * @return kindString
	 */
	public String getDebugIdString() {
		// return kindString + "(" + debugId + ")";
		return id;
	}

	/** 画面上のX位置 */
	public int x;
	/** 画面上のY位置 */
	public int y;
	/** 画面上の横幅 */
	public int w;
	/** 画面上の高さ */
	public int h;
	/** 表示・識別に使用する要素の種類 */
	private String kindString;
	/** 表示・識別に使用する要素の種類 */
	private KindId kindId;

	/** 表示・識別に使用する名前 */
	public String id;

	/**
	 * 要素が値を持つ場合の、値の型(文字列表現)。
	 */
	public String getOutputType() {
		return Value.valueType2str.get(this.getValueType());
	}

	/**
	 * 要素が値を持つ場合の、値の型。
	 */
	abstract public Value.ValueType getValueType();

	/**
	 * @return kindString
	 */
	public String getKindString() {
		return kindString;
	}

	/**
	 * @param kindString セットするkindString
	 */
	public void setKindString(String kindString) {
		if (! P020___AbstractElement.string2kind.containsKey(kindString)) {
			System.err.println("未定義の種別が指定: " + kindString);
			System.exit(1);
		}
		Debug.println("setKindString", kindString);
		this.kindString = kindString;
		this.kindId = P020___AbstractElement.string2kind.get(kindString);
	}

	public KindId getKindId() {
		return kindId;
	}

	/**
	 * 中心のX座標を取得します。
	 *
	 * @return 中心のX座標
	 */
	public int getCenterX() {
		return x + w/2;
	}

	/**
	 * 出力のX座標を取得します。
	 *
	 * @return 出力のX座標
	 */
	public int getConnectOutputX() {
		return x + w;
	}

	/**
	 * 中心のY座標を取得します。
	 *
	 * @return 中心のY座標
	 */
	public int getCenterY() {
		return y + h/2;
	}

	/**
	 * ドラッグされた時の動作
	 *
	 * @param moveX
	 * @param moveY
	 */
	public void dragged(int moveX, int moveY) {
		x += moveX;
		y += moveY;
	}

	public P001_IconGuiInterface getTouchedObject(int x, int y) {
		if (
				1.0f * (this.x + this.w/2 - x)*(this.x + this.w/2 - x) * this.h*this.h
				+ 1.0f * (this.y + this.h/2  -y)*(this.y + this.h/2 - y) * this.w*this.w
				<
				1.0f * this.h*this.h*this.w*this.w/4
				) {
			return this;
		}
		return null;
	}

	public static String escape(String s) {
		return FileReadUtil.escape(s);
	}

	public static String unescape(String s) {
		return s;
	}
}
