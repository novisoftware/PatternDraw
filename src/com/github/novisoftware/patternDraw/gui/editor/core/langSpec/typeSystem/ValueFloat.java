package com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem;

import java.math.BigInteger;

public class ValueFloat extends Value implements IsScalar {
	public ValueFloat(String s) {
		super(ValueType.FLOAT);
		internal = Double.parseDouble(s);
	}

	public ValueFloat(Double d) {
		super(ValueType.INTEGER);
		internal = d;
	}

	@Override
	public String toString() {
		return internal.toString();
	}

	private Double internal;

	public Double getInternal() {
		return internal;
	}

	public boolean sameTo(Object o) {
		return internal.equals(o);
	}

	@Override
	public IsScalar add(IsScalar a) {
		return new ValueFloat(this.internal + ((ValueFloat)a).internal);
	}

	@Override
	public IsScalar sub(IsScalar a) {
		return new ValueFloat(this.internal - ((ValueFloat)a).internal);
	}

	@Override
	public IsScalar mul(IsScalar a) {
		return new ValueFloat(this.internal * ((ValueFloat)a).internal);
	}

	@Override
	public IsScalar div(IsScalar a) {
		return new ValueFloat(this.internal / ((ValueFloat)a).internal);
	}

	@Override
	public IsScalar mod(IsScalar a) {
		return new ValueFloat(this.internal % ((ValueFloat)a).internal);
	}
}
