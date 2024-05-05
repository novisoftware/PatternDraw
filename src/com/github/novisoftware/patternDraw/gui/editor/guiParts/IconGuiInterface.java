package com.github.novisoftware.patternDraw.gui.editor.guiParts;

public interface IconGuiInterface {
	/**
	 * 中心のX座標を取得します。
	 *
	 * @return 中心のX座標
	 */
	public int getCenterX();


	/**
	 * 中心のY座標を取得します。
	 *
	 * @return 中心のY座標
	 */
	public int getCenterY();

	/**
	 * mouse hover の場合に true を設定します
	 *
	 * @param b
	 */
	public void setOnMouse(boolean b);
}
