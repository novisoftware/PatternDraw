package com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class ValueNumeric extends ValueAbstractScalar {
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
	public ValueAbstractScalar add(ValueAbstractScalar a) {
		BigDecimal a2 = ((ValueNumeric)a).internal;
		return new ValueNumeric(this.internal.add(a2));
	}

	@Override
	public ValueAbstractScalar sub(ValueAbstractScalar a) {
		BigDecimal a2 = ((ValueNumeric)a).internal;
		return new ValueNumeric(this.internal.subtract(a2));
	}

	@Override
	public ValueAbstractScalar mul(ValueAbstractScalar a) {
		BigDecimal a2 = ((ValueNumeric)a).internal;
		return new ValueNumeric(this.internal.multiply(a2));
	}

	static final int NUMERIC_DIV_DEFAULT_SCALE = 40;

	@Override
	public ValueAbstractScalar div(ValueAbstractScalar a) {
		BigDecimal a2 = ((ValueNumeric)a).internal;
		return new ValueNumeric(this.internal.divide(a2,NUMERIC_DIV_DEFAULT_SCALE,RoundingMode.HALF_EVEN));
	}

	@Override
	public ValueAbstractScalar mod(ValueAbstractScalar a) {
		BigDecimal a2 = ((ValueNumeric)a).internal;
		return new ValueNumeric(this.internal.remainder(a2));
	}

	@Override
	public int compareInternal(ValueAbstractScalar a) {
		return this.internal.compareTo(((ValueNumeric)a).internal);
	}

	@Override
	public boolean isZero() {
		return this.internal.equals(BigDecimal.ZERO);
	}
}
