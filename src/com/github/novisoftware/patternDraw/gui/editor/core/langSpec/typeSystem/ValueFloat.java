package com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem;

import java.math.BigInteger;

public class ValueFloat extends Value {
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
}