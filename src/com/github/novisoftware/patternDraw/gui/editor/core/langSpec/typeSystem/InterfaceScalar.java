package com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem;

/**
 * マーカーとして使用。
 */
public interface InterfaceScalar {
	/**
	 * 加算。
	 *
	 * 注: Float同士、Integer同士、Numeric同士を演算するよう事前に揃える必要がある。
	 * @param a
	 * @return
	 */
	public InterfaceScalar add(InterfaceScalar a);
	/**
	 * 減算
	 * @param a
	 * @return
	 */
	public InterfaceScalar sub(InterfaceScalar a);
	/**
	 * 乗算
	 * @param a
	 * @return
	 */
	public InterfaceScalar mul(InterfaceScalar a);
	/**
	 * 除算
	 * @param a
	 * @return
	 */
	public InterfaceScalar div(InterfaceScalar a);
	/**
	 * 剰余
	 * @param a
	 * @return
	 */
	public InterfaceScalar mod(InterfaceScalar a);
}
