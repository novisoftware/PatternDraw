package com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem;

import java.math.BigInteger;

/**
 * 整数。多倍長整数( java.math.BigInteger )を内部で使用しています。
 *
 */
public class ValueInteger extends Value {
	public ValueInteger(String s) {
		super(ValueType.INTEGER);
		internal = new BigInteger(s);
	}

	public ValueInteger(BigInteger i) {
		super(ValueType.INTEGER);
		internal = i;
	}

	public ValueInteger(long i) {
		super(ValueType.INTEGER);
		internal = new BigInteger("" + i);
	}

	@Override
	public String toString() {
		return internal.toString();
	}

	private BigInteger internal;

	public BigInteger getInternal() {
		return internal;
	}

	public boolean sameTo(Object o) {
		return internal.equals(o);
	}
}
