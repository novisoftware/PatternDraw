package com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts;

import java.awt.Cursor;
import java.awt.Graphics2D;

import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.utils.FileReadUtil;

public abstract class P015__AbstractIcon2 extends P002__AbstractIcon {
	/**
	 * ドラッグモードの列挙定義
	 */
	enum DragMode {
		MOVE,
		RESIZE_XY,
		RESIZE_X,
		RESIZE_Y
	};

	/**
	 * ドラッグモードの値
	 */
	protected DragMode dragMode = DragMode.MOVE;

	/**
	 * 枠の部分の当たり判定用の幅
	 */
	static private final int EDGE_HIT_WIDTH = 5;

	/** 親オブジェクトへの参照 */
	protected final EditDiagramPanel editPanel;

	/** 表示・識別に使用する名前 */
	public String id;

	/** 画面上のX位置 */
	public int x;
	/** 画面上のY位置 */
	public int y;
	/** 画面上の横幅 */
	public int w;
	/** 画面上の高さ */
	public int h;

	P015__AbstractIcon2(EditDiagramPanel editPanel) {
		this.editPanel = editPanel;
	}
	
	public abstract String str();

	public static String escape(String s) {
		return FileReadUtil.escape(s);
	}

	public static String unescape(String s) {
		return s;
	}

	public abstract P015__AbstractIcon2 getCopy();

	public abstract void paintWithPhase(Graphics2D g, int phase);

	public abstract boolean isComment();

	// 接触判定のデフォルトメソッド
	
	public P001_IconGuiInterface getTouchedObject(EditDiagramPanel editDiagramPanel, int x, int y) {
		if (1.0f * (this.x + this.w / 2 - x) * (this.x + this.w / 2 - x) * this.h * this.h
				+ 1.0f * (this.y + this.h / 2 - y) * (this.y + this.h / 2 - y) * this.w * this.w < 1.0f * this.h
						* this.h * this.w * this.w / 4) {
			editDiagramPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			return this;
		}
		return null;
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
	 * 中心のY座標を取得します。
	 *
	 * @return 中心のY座標
	 */
	public int getCenterY() {
		return y + h / 2;
	}


	// 以降、ドラッグ動作関連のメソッド

	/**
	 * 管理上のx, y と 描画上での視覚的な x, y が一致すると限らないため、
	 * オフセットを設定することができるようにする。
	 */
	protected int boxOffsetX = 0;
	protected int boxOffsetY = 0;
	
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
