package com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.novisoftware.patternDraw.core.exception.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.FileReadUtil;
import com.github.novisoftware.patternDraw.utils.GuiPreference;

public abstract class P020___AbstractElement extends P002__AbstractIcon {
	/** 親オブジェクトへの参照 */
	protected final EditDiagramPanel editPanel;

	/** 表示・識別に使用する名前 */
	public String id;

	/**
	 * 外部パラメタ関連 (コネクタと他の箱を結ぶ線で表現される)。
	 * 
	 * キー: コネクタ名。
	 * 値: 参照先オブジェクトの ID 文字列
	 */
	public HashMap<String, String> paramMapInfo;

	/**
	 * 外部パラメタ関連 (コネクタと他の箱を結ぶ線で表現される)
	 * 
	 * キー: コネクタ名。
	 * 値: 参照先オブジェクト。
	 */
	public HashMap<String, P021____AbstractGraphNodeElement> paramMapObj;

	/**
	 * コネクタ(端子)のオブジェクト
	 */
	public ArrayList<P010___ConnectTerminal> connectors;

	/**
	 * 入力によって結果が変わる場合がある。
	 * この変数は入力に応じた型を記録する。
	 *
	 */
	public ValueType actualValueTypeResult;

	/** 画面上のX位置 */
	public int x;
	/** 画面上のY位置 */
	public int y;
	/** 画面上の横幅 */
	public int w;
	/** 画面上の高さ */
	public int h;
	/** 表示・識別に使用する要素の種類(種類を識別するID) */
	private KindId kindId;
	/** 表示・識別に使用する要素の種類(種類を識別するIDの表示用文言) */
	private String kindString;

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
			string2kind.put("処理", KindId.PROCESSING);
			string2kind.put("制御", KindId.CONTROL);
			string2kind.put("コメント", KindId.COMMENT);

			kind2string = new HashMap<>();
			for (String s : string2kind.keySet()) {
				kind2string.put(string2kind.get(s), s);
			}
		}

		this.debugId = P020___AbstractElement.getDebugId();
	}

	public abstract String str();
	public abstract P020___AbstractElement getCopy();
	public abstract boolean isComment();
	public abstract void paintWithPhase(Graphics2D g, int phase);

	// コネクタ表示用
	abstract public int getXc();
	abstract public int getYc();
	abstract public int getWc();
	abstract public int getHc();

	public void typeCheck() {
		P020___AbstractElement e = this;

		/*
		 * この演算子への入力の妥当性の検査
		 */
		for (P010___ConnectTerminal connector : this.connectors) {
			P020___AbstractElement src = e.paramMapObj.get(connector.getParaName());
			if (src == null || ValueType.UNDEF.equals(src.actualValueTypeResult)) {
				// 入力がない場合や、入力もとで既にエラーになっている場合は、
				// エラー表示しても仕方がないので(自明のため)、エラー検出扱いしない。
				// （端子に対するエラーとはしない）
				connector.isTypeChekResultValid = true;
				connector.typeChekErrorMessage = null;
			}
			else if (Value.isAcceptable(src.actualValueTypeResult, connector.valueType) ) {
				// 入力が妥当だと判定した場合
				connector.isTypeChekResultValid = true;
				connector.typeChekErrorMessage = null;
			}
			else {
				// 入力が妥当でないと判定した場合
				connector.isTypeChekResultValid = false;
				String[] msg = {
				   CaliculateException.MESSAGE_INVALID_CLASS,
				   "入力: " +  Value.valueTypeToDescString(src.actualValueTypeResult),
				   "受付可能: " + Value.valueTypeToDescString(connector.valueType)
				};
				connector.typeChekErrorMessage = msg;
			}
		}

		/*
		 * この演算子の型
		 */
		if (this instanceof P022_____RpnGraphNodeElement) {
			actualValueTypeResult = ((P022_____RpnGraphNodeElement)this).evaluateValueType();
		} else if (this instanceof P023_____FncGraphNodeElement) {
			// TODO
			// 現時点では FNC node element には、入力に応じて返り値が変化する機能なし
			actualValueTypeResult = this.getValueType();
		}
	}

	public ArrayList<String> optStr() {
		ArrayList<String> ret = new ArrayList<>();

		for (String s: paramMapInfo.keySet()) {
			ret.add(String.format("REF: %s %s %s", escape(id), escape(s), escape(paramMapInfo.get(s))));
		}
		return ret;
	}

	/**
	 * 構造化されているか（外部パラメタを持つか）。
	 *
	 * @return
	 */
	public boolean hasParameter() {
		return connectors.size() != 0;
	}

	public void paintConnectedLine(Graphics2D g2) {
		P020___AbstractElement e = this;

		// 「端子」と結線されるよう表現する
		g2.setStroke(GuiPreference.STROKE_BOLD);
		for (P010___ConnectTerminal connector : this.connectors) {
			P020___AbstractElement src = e.paramMapObj.get(connector.getParaName());
			if (src != null) {
				if (connector.isTypeChekResultValid) {
					g2.setColor(Color.GRAY);
				}
				else {
					// エラー時
					g2.setColor(Color.RED);
				}

				// 線分描画
				double x0 = src.getConnectOutputX();
				double y0 = src.getCenterY();
				double x2 = connector.getCenterX();
				double y2 = connector.getCenterY();
				RenderingUtil.drawConnectorStroke(g2, x0, y0, x2, y2);

				// エラーがある場合
				if (! connector.isTypeChekResultValid) {
					double x1 = (x0 + x2) / 2;
					double y1 = (y0 + y2) / 2;
					g2.setFont(GuiPreference.ICON_BOX_FONT);
					float sz = GuiPreference.ICON_BOX_FONT.getSize2D();
					int h = (int)Math.round(sz * 1.5);
					
					int n = connector.typeChekErrorMessage.length;
					for (int i = 0 ; i < n ; i++) {
						String msg = connector.typeChekErrorMessage[i];
						g2.drawString(
								msg,
								(int)x1,
								(int)y1 + h * (i - n) + GuiPreference.CONNECTOR_ERROR_MESSAGE_Y_OFFSET);
					}
				}

			}
		}
	}

	static HashMap<String, KindId> string2kind;
	static HashMap<KindId, String> kind2string;

	public static enum KindId {
		INPUT, DISPLAY, CONSTANT, VARIABLE_SET, VARIABLE_REFER, OPERATOR, PROCESSING, CONTROL, COMMENT
	}

	/**
	 * デバッグ時に使用する「表示用のID」。
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
	 * @return 値の型の UI 用文字列
	 */
	public String getValueTypeDescString() {
		return Value.valueTypeToDescString(this.getValueType());
	}

	/**
	 * @return kindString
	 */
	public String getKindString() {
		return kindString;
	}

	/**
	 * @param kindString
	 *            セットするkindString
	 */
	public void setKindString(String kindString) {
		if (!P020___AbstractElement.string2kind.containsKey(kindString)) {
			System.err.println("未定義の種別が指定: " + kindString);
			System.exit(1);
		}
		Debug.println("setKindString", kindString);
		this.kindString = kindString;
		this.kindId = P020___AbstractElement.string2kind.get(kindString);
	}

	/**
	 * @param kindId
	 *            セットするkindId
	 */
	public void setKindId(KindId kindId) {
		this.kindString =  P020___AbstractElement.kind2string.get(kindId);
		this.kindId = kindId;
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
		return x + w / 2;
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
		return y + h / 2;
	}

	/**
	 * 変数名やパラメタ名が変更になったことを通知するインタフェース
	 * 
	 * @param before 変更前
	 * @param after 変更後
	 */
	public void notifyVarNameChange(String before, String after) {
		// 何もしないのをデフォルト動作にする
	}
	
	public P001_IconGuiInterface getTouchedObject(EditDiagramPanel editDiagramPanel, int x, int y) {
		if (1.0f * (this.x + this.w / 2 - x) * (this.x + this.w / 2 - x) * this.h * this.h
				+ 1.0f * (this.y + this.h / 2 - y) * (this.y + this.h / 2 - y) * this.w * this.w < 1.0f * this.h
						* this.h * this.w * this.w / 4) {
			editDiagramPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
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

	// ドラッグ動作関連のユーティリティメソッド

	protected DragMode dragMode = DragMode.MOVE;
	static private final int EDGE_HIT_WIDTH = 5;
	
	/**
	 * 管理上のx, y と 描画上での視覚的な x, y が一致すると限らないため、
	 * オフセットを設定することができるようにする。
	 */
	protected int boxOffsetX = 0;
	protected int boxOffsetY = 0;
	
	enum DragMode {
		MOVE,
		RESIZE_XY,
		RESIZE_X,
		RESIZE_Y
	};
	
	/**
	 * リサイズ開始判定用 右端判定
	 * 
	 * @param x 判定対象X座標
	 * @return true: 含まれる false: 含まれない
	 */
	protected boolean isOnRightEdge(int x) {
		int myX = this.x + boxOffsetX;
		return (myX + this.w - EDGE_HIT_WIDTH <= x)
				&& (x <= myX + this.w + EDGE_HIT_WIDTH);
	}

	/**
	 * リサイズ開始判定用 下辺判定
	 * 
	 * @param y 判定対象Y座標
	 * @return true: 含まれる false: 含まれない
	 */
	protected boolean isOnBottomEdge(int y) {
		int myY = this.y + boxOffsetY;
		return (myY + this.h - EDGE_HIT_WIDTH  <= y)
				&& (y <= myY + this.h + EDGE_HIT_WIDTH);
	}

	/**
	 * リサイズ開始判定用 左右範囲判定
	 * 
	 * @param x 判定対象X座標
	 * @return true: 含まれる false: 含まれない
	 */
	protected boolean isOnWidth(int x) {
		int myX = this.x + boxOffsetX;
		return myX <= x && x <= myX + this.w;
	}

	/**
	 * リサイズ開始判定用 上下範囲判定
	 * 
	 * @param y 判定対象Y座標
	 * @return true: 含まれる false: 含まれない
	 */
	protected boolean isOnHeight(int y) {
		int myY = this.y + boxOffsetY;
		return myY <= y && y <= myY + this.h;
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

	public boolean isHandled() {
		return editPanel.getHandledObject() == this;
	}
}
