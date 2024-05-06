package com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ValueNumeric extends Value implements IsScalar {
	public ValueNumeric(String s) {
		super(ValueType.NUMERIC);
		internal = new BigDecimal(s);
	}

	public ValueNumeric(BigDecimal i) {
		super(ValueType.NUMERIC);
		internal = i;
	}

	public ValueFloat toValueFloat() {
		return new ValueFloat(this.internal.doubleValue());
	}

	@Override
	public String toString() {
		return internal.toString();
	}

	private BigDecimal internal;

	public BigDecimal getInternal() {
		return internal;
	}

	public boolean sameTo(Object o) {
		return internal.equals(o);
	}

	@Override
	public IsScalar add(IsScalar a) {
		BigDecimal a2 = ((ValueNumeric)a).internal;
		return new ValueNumeric(this.internal.add(a2));
	}

	@Override
	public IsScalar sub(IsScalar a) {
		BigDecimal a2 = ((ValueNumeric)a).internal;
		return new ValueNumeric(this.internal.subtract(a2));
	}

	@Override
	public IsScalar mul(IsScalar a) {
		BigDecimal a2 = ((ValueNumeric)a).internal;
		return new ValueNumeric(this.internal.multiply(a2));
	}

	@Override
	public IsScalar div(IsScalar a) {
		BigDecimal a2 = ((ValueNumeric)a).internal;
		return new ValueNumeric(this.internal.divide(a2));
	}

	@Override
	public IsScalar mod(IsScalar a) {
		BigDecimal a2 = ((ValueNumeric)a).internal;
		return new ValueNumeric(this.internal.remainder(a2));
	}
}
