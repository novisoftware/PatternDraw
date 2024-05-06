package com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem;

import java.math.BigInteger;

public class ValueFloat extends Value implements InterfaceScalar {
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
	public InterfaceScalar add(InterfaceScalar a) {
		return new ValueFloat(this.internal + ((ValueFloat)a).internal);
	}

	@Override
	public InterfaceScalar sub(InterfaceScalar a) {
		return new ValueFloat(this.internal - ((ValueFloat)a).internal);
	}

	@Override
	public InterfaceScalar mul(InterfaceScalar a) {
		return new ValueFloat(this.internal * ((ValueFloat)a).internal);
	}

	@Override
	public InterfaceScalar div(InterfaceScalar a) {
		return new ValueFloat(this.internal / ((ValueFloat)a).internal);
	}

	@Override
	public InterfaceScalar mod(InterfaceScalar a) {
		return new ValueFloat(this.internal % ((ValueFloat)a).internal);
	}
}
