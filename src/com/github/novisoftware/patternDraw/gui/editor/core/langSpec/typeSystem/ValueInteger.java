package com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 整数。多倍長整数( java.math.BigInteger )を内部で使用しています。
 *
 */
public class ValueInteger extends Value implements IsScalar {
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

	public ValueFloat toValueFloat() {
		return new ValueFloat(this.getInternal().doubleValue());
	}

	public ValueNumeric toValueNumeric() {
		return new ValueNumeric(new BigDecimal(this.getInternal()));
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

	@Override
	public IsScalar add(IsScalar a) {
		BigInteger a2 = ((ValueInteger)a).internal;
		return new ValueInteger(this.internal.add(a2));
	}

	@Override
	public IsScalar sub(IsScalar a) {
		BigInteger a2 = ((ValueInteger)a).internal;
		return new ValueInteger(this.internal.subtract(a2));
	}

	@Override
	public IsScalar mul(IsScalar a) {
		BigInteger a2 = ((ValueInteger)a).internal;
		return new ValueInteger(this.internal.multiply(a2));
	}

	@Override
	public IsScalar div(IsScalar a) {
		BigInteger a2 = ((ValueInteger)a).internal;
		return new ValueInteger(this.internal.divide(a2));
	}

	@Override
	public IsScalar mod(IsScalar a) {
		BigInteger a2 = ((ValueInteger)a).internal;
		return new ValueInteger(this.internal.mod(a2));
	}
}
