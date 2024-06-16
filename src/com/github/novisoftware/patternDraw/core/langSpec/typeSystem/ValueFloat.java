package com.github.novisoftware.patternDraw.core.langSpec.typeSystem;

public class ValueFloat extends ValueAbstractScalar {
	public ValueFloat(String s) {
		super(ValueType.FLOAT);
		internal = Double.parseDouble(s);
	}

	public ValueFloat(Double d) {
		super(ValueType.FLOAT);
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
	public ValueAbstractScalar add(ValueAbstractScalar a) {
		return new ValueFloat(this.internal + ((ValueFloat)a).internal);
	}

	@Override
	public ValueAbstractScalar sub(ValueAbstractScalar a) {
		return new ValueFloat(this.internal - ((ValueFloat)a).internal);
	}

	@Override
	public ValueAbstractScalar mul(ValueAbstractScalar a) {
		return new ValueFloat(this.internal * ((ValueFloat)a).internal);
	}

	@Override
	public ValueAbstractScalar div(ValueAbstractScalar a) {
		return new ValueFloat(this.internal / ((ValueFloat)a).internal);
	}

	@Override
	public ValueAbstractScalar mod(ValueAbstractScalar a) {
		return new ValueFloat(this.internal % ((ValueFloat)a).internal);
	}

	@Override
	public int compareInternal(ValueAbstractScalar a) {
		return this.internal.compareTo(((ValueFloat)a).internal);
	}

	@Override
	public boolean isZero() {
		return this.internal == 0;
	}
}
