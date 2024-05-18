package com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem;

import com.github.novisoftware.patternDraw.core.CaliculateException;

public abstract class ValueAbstractScalar extends Value {
	protected ValueAbstractScalar(ValueType valueKind) {
		super(valueKind);
	}

	/**
	 * 加算。
	 *
	 * 注: Float同士、Integer同士、Numeric同士を演算するよう事前に揃える必要がある。
	 * @param a
	 * @return
	 */
	public abstract ValueAbstractScalar add(ValueAbstractScalar a);
	/**
	 * 減算
	 * @param a
	 * @return
	 */
	public abstract ValueAbstractScalar sub(ValueAbstractScalar a);
	/**
	 * 乗算
	 * @param a
	 * @return
	 */
	public abstract ValueAbstractScalar mul(ValueAbstractScalar a);
	/**
	 * 除算
	 * @param a
	 * @return
	 */
	public abstract ValueAbstractScalar div(ValueAbstractScalar a);
	/**
	 * 剰余
	 * @param a
	 * @return
	 */
	public abstract ValueAbstractScalar mod(ValueAbstractScalar a);

	/**
	 * ゼロかどうかを判定する
	 * @return ゼロの場合True
	 */
	public abstract boolean isZero();

	/**
	 * 比較。
	 * 注: CompareTo はオーバーライドしない。
	 * 型が同じ(整数 / 浮動小数点 / 任意精度実数)を想定しているため。
	 */
	public abstract int compareInternal(ValueAbstractScalar a);
}
