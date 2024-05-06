package com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem;

/**
 * マーカーとして使用。
 */
public interface IsScalar {
	/**
	 * 加算。
	 *
	 * 注: Float同士、Integer同士、Numeric同士を演算するよう事前に揃える必要がある。
	 * @param a
	 * @return
	 */
	public IsScalar add(IsScalar a);
	/**
	 * 減算
	 * @param a
	 * @return
	 */
	public IsScalar sub(IsScalar a);
	/**
	 * 乗算
	 * @param a
	 * @return
	 */
	public IsScalar mul(IsScalar a);
	/**
	 * 除算
	 * @param a
	 * @return
	 */
	public IsScalar div(IsScalar a);
	/**
	 * 剰余
	 * @param a
	 * @return
	 */
	public IsScalar mod(IsScalar a);
}
